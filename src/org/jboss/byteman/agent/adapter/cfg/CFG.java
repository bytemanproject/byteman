/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.byteman.agent.adapter.cfg;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.jboss.byteman.agent.Transformer;

import java.util.*;

/**
 * A control flow graph for use by the trigger adapter. the cfg maintains the current instruction sequence
 * for the bytecode in encoded form as it is being generated. It segments the instructions into basic blocks,
 * splitting them at control flow branch points. It also keeps track of the location of try catch blocks
 * and their handlers and of monitor enter and exit instructions. In particular it allows the rule trigger
 * insertion adapter to identify whether or not an inserted rule trigger call is within the scope of one
 * or more synchronized blocks and hence protect the trigger call with try catch handlers which ensure that
 * any pending monitor enters are roudned off with a corresponidng monitor exit. See RuleTriggerMethodAdapter
 * for details of hos the methods provided by this class are used.
 */
public class CFG
{
    /**
     * the name of the method for which this is a CFG
     */
    private String methodName;
    /**
     * the label of the first basic block in the code
     */
    private BBlock entry;
    /**
     * the current basic block
     */
    private BBlock current;
    /**
     * a counter used to number bblocks in code order
     */
    private int nextIdx;
    /**
     * a mapping from the start label of a basic block to the associated block
     */
    private Map<Label, BBlock> blocks;
    /**
     * a mapping from each label to its enclosing basic block and instruction offset
     */
    private Map<Label, CodeLocation> labelLocations;
    /**
     * a map identifying the containment relationship between a basic block and labels which identify
     * instructions located within the block - the first entry is the block label itself
     */
    private Map<BBlock, Link> contains;
    /**
     * a list of names employed in the bytecode
     */
    private List<String> names;
    /**
     * a map from labels which identify the start of a code injection sequence to details of the labels
     * which locate the sequence and its exception handlers
     */
    private Map<Label, TriggerDetails> triggerStarts;
    /**
     * a map from labels which identify the end of a code injection sequence to details of the labels
     * which locate the sequence and its exception handlers
     */
    private Map<Label, TriggerDetails> triggerEnds;

    /**
     * details of the last trigger section encountered set when a trigger start label is notified
     */
    private TriggerDetails latestTrigger;
    /**
     * a map from try catch block start labels to the corresponding try catch block details -- the value
     * is a list because the code reader will reuse teh same label when two try catch blocks start at the
     * same bytecode
     */
    private Map<Label, List<TryCatchDetails>> tryCatchStarts;
    /**
     * a map from try catch block end labels to the corresponding try catch block details -- the value
     * is a list because the code reader will reuse the same label when two try catch blocks end at the
     * same bytecode
     */
    private Map<Label, List<TryCatchDetails>> tryCatchEnds;
    /**
     * a map from try catch block handler labels to the corresponding try catch block details -- the value
     * is a list because the code reader will reuse the same label when two handler blocks start at the
     * same bytecode
     */
    private Map<Label, List<TryCatchDetails>> tryCatchHandlers;
    /**
     * a list of all try catch blocks which were open at the start of the block identified by current. this
     * is kept up to date by method carryForward as blocks are split at control branch points.
     */
    private List<TryCatchDetails> openTryCatchStarts;
    /**
     * a map from block labels to any unclosed monitor enter instructions outstanding when the block is entered.
     * this is only valid for blocks which are arrived at via conventional control flow i.e. not direct targets
     * of try catch handler exceptions.
     */
    private Map<Label, List<CodeLocation>> openMonitorEnters;
    /**
     * a map from monitor enter instructions to the monitor exit insructions which close them. this is a list
     * because an enter may have corresponding exits in exception handler blocks as well as the exit which
     * is executed via normal control flow. Note that the latter is always the first entry in the list.
     */
    private Map<CodeLocation, List<CodeLocation>> monitorPairs;
    /**
     * an inverse map from each monitor exit instruction to the monitor enter insructions it closes.
     */
    private Map<CodeLocation, CodeLocation> inverseMonitorPairs;

    /**
     * construct a CFG labelling the initial block with a given label
     * @param methodName the name of the method fro which this is a CFG
     * @param start a label for the entry block of the CFG
     */
    public CFG(String methodName, Label start)
    {
        this.methodName = methodName;
        this.nextIdx = 0;
        this.entry = this.current = new BBlock(this, start, nextIdx++);
        blocks = new HashMap<Label, BBlock>();
        contains = new HashMap<BBlock, Link>();
        labelLocations = new HashMap<Label, CodeLocation>();
        names = new ArrayList<String>();
        triggerStarts = new HashMap<Label, TriggerDetails>();
        triggerEnds = new HashMap<Label, TriggerDetails>();
        latestTrigger = null;
        tryCatchStarts = new HashMap<Label, List<TryCatchDetails>>();
        tryCatchEnds = new HashMap<Label, List<TryCatchDetails>>();
        tryCatchHandlers = new HashMap<Label, List<TryCatchDetails>>();
        openMonitorEnters = new HashMap<Label, List<CodeLocation>>();
        monitorPairs = new HashMap<CodeLocation, List<CodeLocation>>();
        inverseMonitorPairs = new HashMap<CodeLocation, CodeLocation>();
        blocks.put(start, current);
        setLocation(start);
        contains.put(current, new Link(start));
        openMonitorEnters.put(start, new LinkedList<CodeLocation>());
        openTryCatchStarts = new LinkedList<TryCatchDetails>();
    }

    /*
     * routines for managing and querying the CFG structure
     */

    /**
     * aopend an instruction to the current block
     * @param instruction
     */
    public void add(int instruction)
    {
        current.append(instruction);
    }

    /**
     * append an instruction with one operand to the current block
     * @param instruction
     * @param operand
     */
    public void add(int instruction, int operand)
    {
        current.append(instruction, operand);
    }

    /**
     * append an instruction with two operands to the current block
     * @param instruction
     * @param operand1
     * @param operand2
     */
    public void add(int instruction, int operand1, int operand2)
    {
        current.append(instruction, operand1, operand2);
    }

    /**
     * append an operand with more than two operands ot the current block
     * @param instruction
     * @param operands
     */
    public void add(int instruction, int[] operands)
    {
        current.append(instruction, operands);
    }

    /**
     * append an instruction with a String operand to the current block
     * @param instruction
     * @param name
     */
    public void add(int instruction, String name)
    {
        int idx = names.indexOf(name);
        if (idx < 0) {
            idx = names.size();
            names.add(name);
        }
        current.append(instruction, idx);
    }

    /**
     * append a multiarray create instruction to the current block
     * @param instruction
     * @param name the name of the array base type
     * @param dims the number of array dimensions
     */
    public void add(int instruction, String name, int dims)
    {
        int idx = names.indexOf(name);
        if (idx < 0) {
            idx = names.size();
            names.add(name);
        }
        current.append(instruction, idx, dims);
    }

    /**
     * append a field or method instruction with 3 String operands to the current block
     * @param instruction
     * @param name
     */
    public void add(int instruction, String owner, String name, String desc)
    {
        int idx1 = names.indexOf(owner);
        if (idx1 < 0) {
            idx1 = names.size();
            names.add(owner);
        }
        int idx2 = names.indexOf(name);
        if (idx2 < 0) {
            idx2 = names.size();
            names.add(name);
        }
        int idx3 = names.indexOf(desc);
        if (idx3 < 0) {
            idx3 = names.size();
            names.add(desc);
        }
        current.append(instruction, idx1, idx2, idx3);
    }

    /**
     * set the location of a label to the next instruction offset in the current block
     * @param label the label whose location is to be set
     */
    public CodeLocation setLocation(Label label)
    {
        CodeLocation location =  nextLocation();
        labelLocations.put(label, location);
        return location;
    }

    /**
     * return the location of the label if known or null if it has not yet been reached. note that if this
     * returns non-null then the label's offset in the generated bytecode can be safely retrieved but if it
     * returns null then attempting to retrieve the offset will generate an exception.
     * @param label the label whose location is desired
     * @return the label's location if it has been reached otherwise null
     */

    public CodeLocation getLocation(Label label)
    {
        return labelLocations.get(label);
    }

    /**
     * test whether the location of a label is known yet
     * @param label the label whose location is desired
     * @return true if the label's location has been reached otherwise false
     */

    public boolean hasLocation(Label label)
    {
        return (labelLocations.get(label) != null);
    }

    /**
     * return a location which will identify the next instruction added to the current block
     * @return the location of the next instruction added to the current block
     */

    public CodeLocation nextLocation()
    {
        return  new CodeLocation(current, current.getInstructionCount());
    }

    /**
     * return the block containing a label if known
     *
     * @param label the label whose containing block is desired
     * @return the label's location if it has been reached otherwise null
     */

    public BBlock getBlock(Label label)
    {
        CodeLocation location = labelLocations.get(label);
        if (location == null) {
            // may not have generated code for this label yet
            return null;
        }

        return location.getBlock();
    }


    /**
     * return a link object listing all the labels contained in a given block
     * @param block the block whose labels are being sought
     * @return the associated set of labels
     */

    public Link getContains(BBlock block)
    {
        return contains.get(block);
    }

    /**
     * add a label to the list of labels contained in a given block
     * @param block the block whose containslist is to be updated
     * @param label the label to be added to the list
     */
    private void addContains(BBlock block, Label label)
    {
        Link containsLink = contains.get(block);
        if (containsLink == null) {
            containsLink = new Link(block.getLabel());
            contains.put(block,  containsLink);
        }
        containsLink.append(label);
    }

    /**
     * retrieve the list of monitor enter locations open at the start of a given block
     * @param label the label of the block
     * @return the list of open monitor enter locations
     */
    public List<CodeLocation> getOpenMonitorEnters(Label label)
    {
        return openMonitorEnters.get(label);
    }

    /**
     * retrieve the list of monitor enter locations open at the start of a given block
     * @param block the block
     * @return the list of open monitor enter locations
     */
    public List<CodeLocation> getOpenMonitorEnters(BBlock block)
    {
        List<CodeLocation> blockMonitorEnters = null;

        // if this is a handler target block then the open list can be constructed by combining the
        // lists attached to the try catch details. these can be looked up using the handler
        // labels

        // if this is a normal block then the open list will have been attached to a GOTO label
        // for the block which will have offset 0

        // so first check the block for handler labels -- they will have offset 0
        Link link = getContains(block);
        int count = link.getToCount();
        for (int i = 0; i < count; i++) {
            Label l = link.getTo(i);
            CodeLocation loc = getLocation(l);
            if (loc.getInstructionIdx() == 0) {
                // see if this is a try catch label
                List<TryCatchDetails> detailsList = tryCatchHandlerStartDetails(l);
                if (detailsList != null) {
                    Iterator<TryCatchDetails> iterator = detailsList.iterator();
                    while (iterator.hasNext()) {
                        TryCatchDetails details = iterator.next();
                        if (blockMonitorEnters == null) {
                            blockMonitorEnters = new LinkedList<CodeLocation>();
                        }
                        details.addOpenLocations(blockMonitorEnters);
                    }
                }
            }
        }

        // if that failed then look fro a control flow label with the propagated information
        // any of them will do since they all *must* have the same open monitor list

        if (blockMonitorEnters ==  null) {
            for (int i = 0; i < count; i++) {
                Label l = link.getTo(i);
                CodeLocation loc = getLocation(l);
                if (loc.getInstructionIdx() > 0) {
                    // nothing open on entry to this block
                    break;
                }
                // see if we have an inherited list
                blockMonitorEnters = getOpenMonitorEnters(l);
                if (blockMonitorEnters != null) {
                    break;
                }
            }
        }
        
        return blockMonitorEnters;
    }

    /**
     * retrieve the list of monitor enter locations open at a particular trigger start location. this is called
     * when we are inserting try catch handlers for trigger locations to determine whetehr they need
     * to perform any monitor exit operations before executing the normal trigger exception handling code.
     * @param triggerLocation the location of the trigger start
     * @return the list of locations for monitor enters open at the trigger start
     */
    public List<CodeLocation> getOpenMonitors(CodeLocation triggerLocation)
    {
        BBlock block = triggerLocation.getBlock();
        List<CodeLocation> initialMonitors = getOpenMonitorEnters(block);
        Iterator<CodeLocation> localMonitors = block.getMonitorEnters();
        if ((initialMonitors == null || initialMonitors.size() == 0) && (!localMonitors.hasNext())) {
            return null;
        }
        Iterator<CodeLocation> localExits = block.getMonitorExits();
        List<CodeLocation> outStanding = new LinkedList<CodeLocation>();
        int triggerIdx = triggerLocation.getInstructionIdx();
        if (initialMonitors != null) {
            outStanding.addAll(initialMonitors);
        }
        while (localMonitors.hasNext()) {
            CodeLocation nextLocation = localMonitors.next();
            int nextIdx = nextLocation.getInstructionIdx();
            if (nextIdx <= triggerIdx) {
                outStanding.add(nextLocation);
            }
        }
        while (localExits.hasNext()) {
            CodeLocation nextLocation = localExits.next();
            CodeLocation enterLocation = getPairedEnter(nextLocation);
            if (enterLocation != null) {
                int nextIdx = nextLocation.getInstructionIdx();
                if (nextIdx <= triggerIdx) {
                    outStanding.remove(nextLocation);
                }
            } else {
                System.out.println("floating monitor exit " + nextLocation);
            }
        }
        return outStanding;
    }

    /**
     * pair a monitor enter instruction with an associated monitor exit instructions
     * @param enter
     * @param exit
     */
    private void addMonitorPair(CodeLocation enter, CodeLocation exit)
    {
        List<CodeLocation> paired = monitorPairs.get(enter);
        if (paired == null) {
            paired = new LinkedList<CodeLocation>();
            monitorPairs.put(enter, paired);
        }
        paired.add(exit);
        // we also need to be abel to query this relationship in reverse order
        CodeLocation inverse = inverseMonitorPairs.put(exit, enter);
    }

    /**
     * locate the first monitor exit instruction associated with a given monitor enter
     * @param enter
     */
    private CodeLocation getPairedExit(CodeLocation enter)
    {
        List<CodeLocation> paired = monitorPairs.get(enter);
        if (paired != null) {
            return paired.get(0);
        }

        return null;
    }

    /**
     * locate the monitor enter instruction associated with a given monitor exit
     * @param exit
     */
    private CodeLocation getPairedEnter(CodeLocation exit)
    {
        return inverseMonitorPairs.get(exit);
    }

    /**
     * return the index of the local var at which this monitorenter saved its lock object
     */
    public int getSavedMonitorIdx(CodeLocation open)
    {
        // this should identify a monitorexit instruction preceded by an aload N instruction
        BBlock block = open.getBlock();
        int instructionIdx = open.getInstructionIdx();
        if (instructionIdx <= 0) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair has invalid index " + instructionIdx);
        }
        int instruction = block.getInstruction(instructionIdx);
        if (instruction != Opcodes.MONITORENTER) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair instruction " + instruction + " is not MONITOREXIT");
        }
        instruction = block.getInstruction(instructionIdx - 1);
        if (instruction != Opcodes.ASTORE) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair preceding instruction " + instruction + " is not ASTORE");
        }
        int varIdx = block.getInstructionArg(instructionIdx - 1, 0);
        if (varIdx < 0) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair preceding ASTORE instruction has invalid index " + varIdx);
        }
        return varIdx;
    }

    /**
     * forward details of open monitor and try catch block locations from the current
     * block to its reachable labels. This is always called just before splitting the current block.
     */
    private void carryForward()
    {
        if (Transformer.isDumpCFGPartial()) {
            System.out.println("Carry forward for block " + current.getBlockIdx());
        }
        
        Label label = current.getLabel();
        int nOuts = current.nOuts();

        // the active try start list for the block is the list of all currently open starts
        // minus those which are closed in the block at instruction index 0

        Iterator<TryCatchDetails> starts = current.getTryStarts();
        Iterator<TryCatchDetails> ends = current.getTryEnds();

        while (ends.hasNext()) {
            TryCatchDetails details = ends.next();
            CodeLocation location = getLocation(details.getEnd());
            if (location.getInstructionIdx() == 0) {
                openTryCatchStarts.remove(details);
            }
        }

        // any remaining starts are active somewhere in the block and hence indicate
        // possible exception control flow

        current.updateActiveTryStarts(openTryCatchStarts);

        if (Transformer.isDumpCFGPartial()) {
            System.out.println(current);
        }

        // the new list of open starts is the old list plus any starts opened in the block
        // minus any ends in the block

        current.carryTryStarts(openTryCatchStarts);
        current.removeTryEnds(openTryCatchStarts);

        // now update the list of outstanding monitor enters calls

        Iterator<CodeLocation> entersIter = current.getMonitorEnters();
        Iterator<CodeLocation> exitsIter = current.getMonitorExits();
        List<CodeLocation> openEnters = getOpenMonitorEnters(current);
        Iterator<CodeLocation> openEntersIter = (openEnters != null ? openEnters.iterator() : null);

        int openEntersCount = (openEnters == null ? 0 : openEnters.size());
        int entersCount = current.getMonitorEnterCount();
        int exitsCount = current.getMonitorExitCount();

        if (Transformer.isDumpCFGPartial()) {
            System.out.print("Carry forward open monitors for " + current.getBlockIdx() +" ==>" );
            for (int i = 0; i < openEntersCount; i++) {
                System.out.print(" ");
                System.out.print(openEnters.get(i));
            }
            System.out.println();
        }

        // pair off any new found exits with their respective enters

        LinkedList<CodeLocation> reversed = new LinkedList<CodeLocation>();
        while (entersIter.hasNext()) {
            reversed.addFirst(entersIter.next());
        }
        entersIter = reversed.iterator();

        while (exitsIter.hasNext() && entersIter.hasNext()) {
            CodeLocation exit = exitsIter.next();
            CodeLocation enter = entersIter.next();
            addMonitorPair(enter, exit);
        }

        // if all is right then this test should not be needed
        if (openEntersIter != null) {
            reversed = new LinkedList<CodeLocation>();
            while (openEntersIter.hasNext()) {
                reversed.addFirst(openEntersIter.next());
            }
            openEntersIter = reversed.iterator();

            while (exitsIter.hasNext() && openEntersIter.hasNext()) {
                CodeLocation exit = exitsIter.next();
                CodeLocation enter = openEntersIter.next();
                addMonitorPair(enter, exit);
            }
        } else {
            if (exitsIter.hasNext()) {
                System.out.println("exits unaccounted for in block B" + current.getBlockIdx());
            }
        }

        // any left over values are still open
        
        List<CodeLocation> newOpenEnters = new LinkedList<CodeLocation>();

        while (entersIter.hasNext()) {
            newOpenEnters.add(entersIter.next());
        }

        if (openEntersIter != null) {
            while (openEntersIter.hasNext()) {
                newOpenEnters.add(openEntersIter.next());
            }
        }

        int newOpenCount = newOpenEnters.size();
        
        // ok, now attach the list to all reachable blocks

        // first the blocks reachable via jump links

        // n.b. link 0 is the next block in line, if it is reachable then it will also appear as a later link
        // so we start the iteration from 1

        for (int i = 1; i <= nOuts; i++) {
            label = current.nthOut(i);
            openEnters = openMonitorEnters.get(label);
            if (openEnters == null) {
                openMonitorEnters.put(label, newOpenEnters);
                if (Transformer.isDumpCFGPartial()) {
                    System.out.print("open monitors " + label + " ==>");
                    for (int j = 0; j < newOpenCount; j++) {
                        CodeLocation l = newOpenEnters.get(j);
                        System.out.print(" BB");
                        System.out.print(l.getBlock().getBlockIdx());
                        System.out.print(".");
                        System.out.print(l.getInstructionIdx());
                    }
                    System.out.println();
                }
            } else {
                // sanity check
                // this should contain the same locations as our current list!
                if (openEnters.size() != newOpenCount) {
                    System.out.println("invalid open enters count for block " + label);
                }
                for (int j = 0; j < newOpenCount; j++) {
                    CodeLocation l1 = openEnters.get(j);
                    CodeLocation l2 = newOpenEnters.get(j);
                    if (l1.getBlock() != l2.getBlock()) {
                        System.out.println("invalid open enters block for block " + label + " at index " + j);
                    }
                    if (l1.getInstructionIdx() != l2.getInstructionIdx()) {
                        System.out.println("invalid open enters instruction index for block " + label + " at index " + j);
                    }
                }
            }
        }

        // now propagate open monitors to handler blocks reachable via thrown exceptions
        // we need to be conservative here. a handler block may not actually be reachable because another
        // try catch block with a generic exception type shadows it.

        Iterator<CodeLocation> newOpenIter = newOpenEnters.iterator();

        // for each open enter check if any of the currently open try catch blocks is enclosed by the
        // enter and its first exit (n.b. the latter is inclusive). if so then attach the open
        // to the try catch details. a pseudo handler should be found later which closes the monitor
        // and rethrows

        while (newOpenIter.hasNext()) {
            CodeLocation enter = newOpenIter.next();
            Iterator<TryCatchDetails> activeStarts = current.getActiveTryStarts();
            while (activeStarts.hasNext()) {
                TryCatchDetails details = activeStarts.next();
                if (details.containsOpenEnter(enter)) {
                    // ignore
                    continue;
                }
                CodeLocation tryStart = getLocation(details.getStart());
                if (enter.compareTo(tryStart) <= 0) {
                    // try start is after the enter -- now see where the corresponding exit is

                    CodeLocation exit = getPairedExit(enter);
                    if (exit != null && tryStart.compareTo(exit) <= 0) {
                        // open is in scope of try catch so attach it to the try start
                        details.addOpenEnter(enter);
                    }
                }
            }
        }

        // do the same for each enter opened in the current block

        newOpenIter = current.getMonitorEnters();
        
        while (newOpenIter.hasNext()) {
            CodeLocation enter = newOpenIter.next();
            Iterator<TryCatchDetails> activeStarts = current.getActiveTryStarts();
            while (activeStarts.hasNext()) {
                TryCatchDetails details = activeStarts.next();
                if (details.containsOpenEnter(enter)) {
                    // ignore
                    continue;
                }
                CodeLocation tryStart = getLocation(details.getStart());
                if (enter.compareTo(tryStart) <= 0) {
                    // try start is after the enter -- now see where the corresponding exit is

                    CodeLocation exit = getPairedExit(enter);
                    if (exit != null && tryStart.compareTo(exit) <= 0) {
                        // open is in scope of try catch so attach it to the try start
                        details.addOpenEnter(enter);
                    }
                }
            }
        }
    }

    /**
     * split the graph at a control-flow dead-end using the label provided to identify the new current
     * block. the caller is obliged to call visitLabel immediately after calling this method to ensure
     * that the current block label is indexed appropriately.
     *
     * @param newStart the label to be used to identify the new current block
     */
    public void split(Label newStart)
    {
        current.append(newStart);
        // carry forward any open monitor enter locations and update the open try start list
        carryForward();
        current = new BBlock(this, newStart, nextIdx++);
        blocks.put(newStart, this.current);
    }

    /**
     * split the graph at a control-flow goto point using the labels provided to identify the new current
     * block and the goto target. the caller is obliged to call visitLabel immediately after calling this
     * method to ensure that the current block label is indexed appropriately.
     *
     * @param newStart the label to be used to identify the new current block
     * @param out the target of the GOTO
     */
    public void split(Label newStart, Label out)
    {
        current.append(newStart);
        current.append(out);
        // carry forward any open monitor enter locations and update the open try start list
        carryForward();
        current = new BBlock(this, newStart, nextIdx++);
        blocks.put(newStart, this.current);
    }

    /**
     * split the graph at a control-flow if branch point using the labels provided to identify the new current
     * block the if branch target and the else branch target. the caller is obliged to call visitLabel
     * immediately after calling this method to ensure that the current block label is indexed appropriately.
     *
     * @param newStart the label to be used to identify the new current block
     * @param out the target of the if branch
     * @param out2 the target of the else branch which probably ought to be the same label as passed for the
     * current block (IF instructions assume drop-through)
     */
    public void split(Label newStart, Label out, Label out2)
    {
        current.append(newStart);
        current.append(out);
        current.append(out2);
        // carry forward any open monitor enter locations and update the open try start list
        carryForward();
        current = new BBlock(this, newStart, nextIdx++);
        blocks.put(newStart, current);
    }

    /**
     * split the graph at a control-flow switch branch point using the labels provided to identify the new
     * current block, the switch case default branch target and the rest of the switch case branch targets.
     * the caller is obliged to call visitLabel immediately after calling this method to ensure that the
     * current block label is indexed appropriately.
     *
     * @param newStart the label to be used to identify the new current block
     * @param dflt the switch case default branch target
     * @param labels the other switch case branch targets
     */

    public void split(Label newStart, Label dflt, Label[] labels)
    {
        current.append(newStart);
        current.append(dflt);
        for (int i = 0; i < labels.length ; i++) {
            current.append(labels[i]);
        }
        // carry forward any open monitor enter locations and update the open try start list
        carryForward();
        current = new BBlock(this, newStart, nextIdx++);
        blocks.put(newStart, this.current);
    }

    /**
     * test if a label marks the start of a try catch block
     * @param label the label to be tested
     * @return true if the label marks the start of a try catch block otherwise false
     */
    public boolean tryCatchStart(Label label)
    {
        return tryCatchStarts.containsKey(label);
    }

    /**
     * test if a label marks the end of a try catch block
     * @param label the label to be tested
     * @return true if the label marks the start of a try catch block otherwise false
     */
    public boolean tryCatchEnd(Label label)
    {
        return tryCatchEnds.containsKey(label);
    }

    /**
     * test if a label marks the start of the handler for a try catch block
     * @param label the label to be tested
     * @return true if the label marks the start of a try catch block otherwise false
     */
    public boolean tryCatchHandlerStart(Label label)
    {
        return tryCatchHandlers.containsKey(label);
    }

    /**
     * return the list of details of try catch blocks which start at this label
     * @param label
     * @return
     */
    public List<TryCatchDetails> tryCatchStartDetails(Label label)
    {
        return tryCatchStarts.get(label);
    }

    /**
     * return the list of details of try catch blocks which end at this label
     * @param label
     * @return
     */
    public List<TryCatchDetails> tryCatchEndDetails(Label label)
    {
        return tryCatchEnds.get(label);
    }

    /**
     * return the list of details of try catch blocks whose handler startsend at this label
     * @param label
     * @return
     */
    public List<TryCatchDetails> tryCatchHandlerStartDetails(Label label)
    {
        return tryCatchHandlers.get(label);
    }

    /**
     * test if a label marks the start of a trigger block
     * @param label the label to be tested
     * @return true if the label marks the start of a trigger block otherwise false
     */
    public boolean triggerStart(Label label)
    {
        return triggerStarts.containsKey(label);
    }

    /**
     * test if a label marks the end of a trigger block
     * @param label the label to be tested
     * @return true if the label marks the start of a trigger block otherwise false
     */
    public boolean triggerEnd(Label label)
    {
        return triggerEnds.containsKey(label);
    }

    /**
     * return details of any trigger block which starts at this label
     * @param label
     * @return
     */
    public TriggerDetails triggerStartDetails(Label label)
    {
        return triggerStarts.get(label);
    }

    /**
     * return the list of details of try catch blocks which end at this label
     * @param label
     * @return
     */
    public TriggerDetails triggerEndDetails(Label label)
    {
        return triggerEnds.get(label);
    }

    /**
     * return an iterator ovver all known trigger detailsd
     * @return
     */
    public Iterator<TriggerDetails> triggerDetails()
    {
        return triggerStarts.values().iterator();
    }

    /**
     * notify the CFG that a label has been visited by the method visitor and hence its position will now
     * be resolved
     * @param label
     */
    public void visitLabel(Label label)
    {
        // record the label's location in the current block

        addContains(current, label);

        // now we need to add a block location for this label

        CodeLocation location = setLocation(label);

        // if this is a try catch block start, end or handler label then we need to update the list
        // maintained in each block

        List<TryCatchDetails> details = tryCatchStartDetails(label);

        if (details != null) {
            current.addTryStarts(details);
        }

        details = tryCatchEndDetails(label);

        if (details != null) {
            current.addTryEnds(details);
        }

        details = tryCatchHandlerStartDetails(label);

        if (details != null) {
            current.addTryHandlerStarts(details);
        }
    }

    /**
     * notify the CFG that a label which represents the start of a trigger injection sequence has just been visited
     * by the method visitor.
     * @param label
     */
    public void visitTriggerStart(Label label)
    {
        // we normally only see one trigger start for a given trigger adapter run but in the
        // case of an AT EXIT trigger adapter we may see multiple trigger starts

        latestTrigger = new TriggerDetails(this, label);
        triggerStarts.put(label, latestTrigger);
    }

    /**
     * notify the CFG that a label which represents the end of a trigger injection sequence has just been visited
     * by the method visitor.
     * @param label
     */
    public void visitTriggerEnd(Label label)
    {
        // we normally only see one trigger end for a given trigger adaoter run but in the
        // case of an AT EXIT trigger adapter we may see multiple trigger ends

        latestTrigger.setEnd(label);
        triggerEnds.put(label, latestTrigger);
    }

    /**
     * notify the CFG of the location of a try catch block. note that this does not mean that the code
     * generator has been notified of this information. these are normally notified to the method visitor
     * before visiting the code. this is problematic if we want to insert our trigger point try catch
     * blocks because we need to order them before any enclosing try catch blocks with wider scope. so the
     * method visitor calls this routine up front but only notifies the try catch block to its super when
     * the end label for the try catch block is reached.
     *
     * @param start
     * @param end
     * @param handler
     * @param type
     */
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
    {
        // hmm, we need to store this info so we can track it later
        boolean isTriggerHandler = triggerStarts.containsKey(start);
        TryCatchDetails details = new TryCatchDetails(this, start, end, handler, type, isTriggerHandler);

        // each label should only ever correspond to a single code location but we may see the same label
        // associated with more than one tryCatchBlock notification so the index links are 1:m
        List<TryCatchDetails> detailsList = tryCatchStarts.get(start);
        if (detailsList == null) {
            detailsList = new LinkedList<TryCatchDetails>();
            tryCatchStarts.put(start, detailsList);
        }
        detailsList.add(details);

        detailsList = tryCatchEnds.get(end);
        if (detailsList == null) {
            detailsList = new LinkedList<TryCatchDetails>();
            tryCatchEnds.put(end, detailsList);
        }
        detailsList.add(details);

        detailsList = tryCatchHandlers.get(handler);
        if (detailsList == null) {
            detailsList = new LinkedList<TryCatchDetails>();
            tryCatchHandlers.put(handler, detailsList);
        }
        detailsList.add(details);
    }

    /**
     * return true if the current block is a rethrow handler i.e. one which was created to close a monitor
     * exit instruction and then rethrow an exception. n.b. this must only be called when the next instruction
     * to be added to the byetcode sequence is an ATHROW
     * @return
     */
    public boolean inRethrowHandler()
    {
        int nextIdx = current.getInstructionCount();
        // a compiler generated rethrow block always has the same format
        // astore  N1
        // aload   N2
        // monitorexit
        // aload   N1
        // athrow
        if ((nextIdx >= 4) &&
                current.getInstruction(nextIdx - 1) == Opcodes.ALOAD &&
                current.getInstruction(nextIdx - 2) == Opcodes.MONITOREXIT  &&
                current.getInstruction(nextIdx - 3) == Opcodes.ALOAD  &&
                current.getInstruction(nextIdx - 4) == Opcodes.ASTORE &&
                current.getInstructionArg(nextIdx - 1, 0) == current.getInstructionArg(nextIdx - 4, 0)) {
            return true;
        }

        // a rethrow block generated by byteman has the simpler format
        // aload   N1
        // monitorexit
        // athrow
        if ((nextIdx >= 2) &&
                current.getInstruction(nextIdx - 1) == Opcodes.MONITOREXIT  &&
                current.getInstruction(nextIdx - 2) == Opcodes.ALOAD) {
            return true;
        }

        return false;
    }

    /**
     * this can be called when the code generator call visiMaxs but it does nothing just now
     */
    public void visitMaxs()
    {
        // nothing to do just here
    }

    public void visitEnd()
    {
        // we don't need to do anything here to but for now just dump the CFG if we are verbose

        if (Transformer.isDumpCFG()) {
            System.out.println(this);
        }
    }

    /**
     * generate a string representation of the CFG
     * @return
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("Control Flow Graph for ");
        buf.append(methodName);
        buf.append("\n");
        BBlock next = entry;
        while (next != null) {
            next.printTo(buf);
            next = blocks.get(next.next());
        }

        return buf.toString();
    }

    /**
     * return the index of the label in its enclosing block's instruction sequence of -1 if the
     * label has not yet been visited. the index can be used to lookup the insruction following
     * the label.
     * @param label
     * @return
     */
    public int getBlockInstructionIdx(Label label)
    {
        CodeLocation location = labelLocations.get(label);
        if (location == null) {
            // may not have generated code for this label yet
            return -1;
        }

        return location.getInstructionIdx();
    }

    public String getName(int nameIdx)
    {
        return names.get(nameIdx);
    }
}

