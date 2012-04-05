/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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

import org.jboss.byteman.rule.type.TypeHelper;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.jboss.byteman.agent.Transformer;
import org.objectweb.asm.Type;

import java.util.*;

/**
 * A control flow graph (cfg) for use by trigger method adapters.<p/>
 * A trigger method adapter is required to notify the CFG each time an instruction or label is visited and
 * each time a try catch block is notified. It is also required to notify the CFG when trigger coe generartion
 * begins and ends. The cfg allows the trigger method adapter to identify whether or not trigger code is
 * within the scope of one or more synchronized blocks, allowing it to protect the trigger call with try catch
 * handlers which ensure that any open monitor enters are rounded off with a corresponding monitor exit.
 * <p/>
 * A cfg is constructed dynamically as the code is visited in order to enable trigger insertion to be performed
 * during a single pass of the bytecode. See {@link org.jboss.byteman.agent.adapter.RuleTriggerMethodAdapter}
 * for an example of how the methods provided by this class are invoked during visiting of the method byte code.
 * Methods provided for driving CFG construction include:
 * <ul>
 * <li> non-control instruction visit:
 *  {@link CFG#add(int)}, {@link CFG#add(int, int)},
 * {@link CFG#add(int, int, int)} {@link CFG#add(int, int[])}  {@link CFG#add(int, String)},
 * {@link CFG#add(int, String, String, String)}, {@link CFG#add(int, String, int)},
 * <li> control instruction visit:
 * {@link CFG#split(org.objectweb.asm.Label)}, {@link CFG#split(org.objectweb.asm.Label, org.objectweb.asm.Label)},
 * {@link CFG#split(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label)},
 * {@link CFG#split(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label[])},
 * <li> label visit:
 * {@link CFG#visitLabel(org.objectweb.asm.Label)},
 * <li> try/catch block visit:
 * {@link CFG#visitTryCatchBlock(org.objectweb.asm.Label, org.objectweb.asm.Label, org.objectweb.asm.Label, String)},
 * <li> trigger region demarcation:
 * {@link CFG#visitTriggerStart(org.objectweb.asm.Label)}, {@link CFG#visitTriggerEnd(org.objectweb.asm.Label)},
 * <li> code visit end demarcation:
 * {@link org.jboss.byteman.agent.adapter.cfg.CFG#visitMaxs()}, {@link org.jboss.byteman.agent.adapter.cfg.CFG#visitEnd()},
 * </ul>
 * <p/>
 * The cfg maintains the current instruction sequence for the method in encoded form as it is being generated.
 * The cfg models both the linear instruction sequence and the directed graph of control flow through that sequence.
 * It splits the instruction stream at control flow branch points, grouping instructions into basic blocks. A
 * successor link relation between blocks retains the linear instruction sequence. Control flow links between
 * basic blocks define the graph structure. The cfg correlates labels with i) blocks and ii) instruction offsets
 * within those blocks as the labels are visited during bytecode visiting. It also tracks the locations within
 * blocks of try catch regions and their handlers and of monitor enter and exit instructions.
 * <p/>
 * The lock propagation algorithm employed to track the extent of monitor enter/exit pairs and try/catch blocks is
 * the most complex aspect of this implementation, mainly because it has to be done in a single pass. This means
 * that the end location of a try catch block or the location of the (one or more) monitor exit(s) associated with
 * a monitor enter may not be known when a trigger point is reached. This algorithm is described below in detail.
 * First an explanation of the CFG organization is provided.
 * </p>
 * <h3>Control flow graph model</h3>
 * The bytecode sequence is segmented into basic blocks at control flow branches ensuring there is no <em>explicit</em>
 * control flow internal to a block. The only way <em>normal</em> control can flow from one block to another is via a
 * switch/goto/branch instruction occuring at the end of the block. So, basic blocks are the nodes of the CFG
 * and the links in the graph identify these control flow transitions.
 * <p>
 * Normal control flow linkage is explicitly
 * represented in the blocks as a list containing the labels of the target blocks. Labels are used rather than
 * handles on the block themselves so that forward links to blocks which have not yet been generated can be
 * modelled. Labels are resolved to the relevant block and instruction index as they are visited during walking
 * of the bytecode.
 * </p>
 * The outgoing control flow link count can be obtained by calling method
 * {@link BBlock#nOuts()}. The label of the block to which control is transferred can be identified by calling
 * method {@link BBlock#nthOut(int)}. Note that valid link indices run from 1 to nOuts() (see below). Once
 * a label has been visited it can be resolved to a {@link CodeLocation} by calling method
 * {@link CFG#getLocation(org.objectweb.asm.Label)}. The returned value identifies both a block and an instruction
 * offset in the block.
 * <p/>
 * Several caveats apply to this simple picture. Firstly, blocks ending in return or throw have no control flow -- they
 * pass control back to the caller rather than to another basic block. So, the count returned by {@link BBlock#nOuts()}
 * will be 0 for such blocks.
 * <p/>
 * Secondly, all blocks except the last have a distinguished link which identifies the block successor link
 * relationship. The successor block can be obtained by supplying value 0 as argument to method
 * {@link BBlock#nthOut(int)}. This link is <em>additional</em> to any control flow links and it is <em>not</em>
 * included in the count returned by {@link BBlock#nOuts()}. Note that where there is a control flow link to the
 * next block in line (e.g. where the block ends in an ifXX instruction) the label employed for the distinguished 0
 * link will also appear in the set of control flow links (as link 1 in the case of an ifXX instruction).
 * <p/>
 * The final caveat is that this graph model does not identify control flow which occurs as a consequence of
 * generated exceptions.
 * </p>
 * <h3>Exceptional Control Flow</h3>
 * Exception control flow is modelled independently from normal flow because it relates to a segment of the
 * instruction sequence rather than individual instructions. A specific exception flow is associated with a each
 * try catch block and the target of the flow is the start of the handler block. The cfg maintains a list of
 * {@link TryCatchDetails} which identify the location of the try/catch start, its end and the associated handler
 * start location. Once again labels are used so as to allow modelling of forward references to code locations
 * which have not yet been generated.
 * <p/>
 * Note that handler start labels always refer to a code location which is at the start of a basic block. Start
 * and end labels for a given try/catch block may refer to code locations offset into their containing basic block
 * and possibly in distinct blocks.
 * <p/>
 * Methods {@link #tryCatchStart(org.objectweb.asm.Label)}, {@link #tryCatchEnd(org.objectweb.asm.Label)}
 * and {@link #tryCatchHandlerStart(org.objectweb.asm.Label)} can be called to determine whether a given label
 * identifies, respectively, the start of a try catch block, the end of a try catch block or the start of a handler
 * block. Methods {@link #tryCatchStartDetails(org.objectweb.asm.Label)} {@link #tryCatchEndDetails(org.objectweb.asm.Label)},
 * and {@link #tryCatchHandlerStartDetails(org.objectweb.asm.Label)} can be used to retrieve the associated
 * {@link TryCatchDetails} information.
 * </p>
 * <h3>Label Resolution</h3>
 * The cfg relies upon its adapter client to notify it whenever a label is visited during a walk of the bytecode.
 * This allows it to associate labels with the basic blocks and instruction offsets within those blocks. The cfg
 * provides method {@link CFG#getBlock(org.objectweb.asm.Label)} to resolve the primary label for a block (i.e. the
 * one supplied as argument to a split call) to the associated block. It also provides method
 * {@link CFG#getBlockInstructionIdx(org.objectweb.asm.Label)} to resolve a label to a {@link CodeLocation} i.e.
 * block and instruction index within a block. Both methods return null if the label has not yet been visited.
 * <p/>
 * Method {@link CFG#getContains(BBlock)} is also provided to obtain a list of all labels contained within a
 * specific block. There may be more than one label which resolves to a location within a specific block. For
 * example, the handler start label associated with a try/catch handler is contained in the handler block at
 * offset 0 but is never the primary label for the block. Iteration over the contained set is used internally
 * in the cfg to resolve equivalent labels.
 * <h3>lock propagation algorithm</h3>
 * The cfg tracks the occurence of monitor enter and monitor exit instructions as they are encountered during
 * the bytecode walk. Note that the relationship between enter and exit instructions is 1 to many. For any given
 * monitor enter there are one or more exits associated with the normal control flow path and zero
 * or more alternative exits associated with exception control flow paths. The association between monitor
 * entry and monitor exit instructions is made available via methods {@link CFG#getPairedEnter(CodeLocation)},
 * and {@link CFG#getPairedExit(CodeLocation, BBlock)} 9note that a given enter will never have more than one
 * exit in any given block).
 * <p/>
 * The cfg associates monitor enters and exits with their enclosing block, allowing it to identify the start and/or
 * end of synchronized regions within a specific block. This information can be propagated along control flow links
 * to identify outstanding monitor enters at any point in a given control flow path. Whenever a block is created
 * it is associated with a set of open enter instructions i.e. enter instructions occurring along all control flow
 * paths to the block for which no corresponding exit has been executed.
 * <p/>
 * <ul>
 * <li>For the initial block the open enters list is empty.
 * <p/>
 * <li>For a block reached by normal control flow the open enters list can be derived from any of the feed blocks
 * which transfer control to it. It is computed by adding and removing entries to/from the feed block's open
 * enters list according to the order the enters or exits appear in the block. Any feed block is valid because
 * every enter must have a single corresponding exit on each valid path through the bytecode. Two paths to the
 * same block cannot introduce different enters and exits without breaking this invariant. Also, enters and exits
 * must be strictly nested so the set of open monitors can be tracked using a simple stack model.
 * <p/>
 * The algorithm propagates open enters along normal control flow paths whenever a split instruction is invoked
 * (splitting the instruction stream into a new block). The work is done in method {@link CFG#carryForward()}. This
 * method identifies the current block's open enters list (how will emerge below), updates it with any enters and
 * exits performed in the block and then, for each each outgoing control link, associates the new list with the
 * linked block by inserting the list into a hash table keyed by the block label. Clearly, if the current block was
 * itself arrived at via normal control flow then its open enters list will already be available in the hash table.
 * Handler blocks require a different lookup.
 *<li>
 * Computing the open enters list for a handler block which is the target of exception control flow is also done in
 * method {@link CFG#carryForward()}. This requires identifying all try/catch regions which enclose the block
 * and tagging the corresponding {@link TryCatchDetails} object with the location of any monitor enter instructions
 * which are open at some  point in the try catch region. If this is done for every block encountered during
 * the bytecode walk then at the point where the handler block is split all enter instructions which are still open
 * somewhere within the try/catch region will be listed in the {@link TryCatchDetails}. So, at the split point
 * the old block can be tested to see if it is labelled as a try/catch handler target and, if so, its open enters
 * list can be looked up by locating the {@link TryCatchDetails} associated with the handler start label.
 * </ul>
 * Note that we still need to worry about nested try/catch regions shadowing outer try/catch handlers with a more
 * specific exception type e.g. when a try region with a catch-all handler type is embedded within a try region with
 * a specific exception type or, less commonly, when the inner block employs a super type of the outer block. In these
 * cases exception flow from the inner region cannot reach the handler for the outer region. This means we must
 * dispense with propagation of open monitor enters to the outer region since they will be closed by the inner
 * handler.
 * TODO -- implement this last check
 */
public class CFG
{
    /**
     * Type identifying execute exceptions thrown by runtime
     */
    public final static Type EXECUTE_EXCEPTION_TYPE = Type.getType(TypeHelper.externalizeType("org.jboss.byteman.rule.exception.ExecuteException"));
    /**
     * Type identifying return exceptions thrown by runtime
     */
    public final static Type EARLY_RETURN_EXCEPTION_TYPE = Type.getType(TypeHelper.externalizeType("org.jboss.byteman.rule.exception.EarlyReturnException"));
    /**
     * Type identifying throw exceptions thrown by runtime
     */
    public final static Type THROW_EXCEPTION_TYPE = Type.getType(TypeHelper.externalizeType("org.jboss.byteman.rule.exception.ThrowException"));
    /**
     * name of type identifying execute exceptions thrown by runtime
     */
    public final static String EXECUTE_EXCEPTION_TYPE_NAME = EXECUTE_EXCEPTION_TYPE.getInternalName();
    /**
     * name of type identifying return exceptions thrown by runtime
     */
    public final static String EARLY_RETURN_EXCEPTION_TYPE_NAME = EARLY_RETURN_EXCEPTION_TYPE.getInternalName();
    /**
     * name of type identifying throw exceptions thrown by runtime
     */
    public final static String THROW_EXCEPTION_TYPE_NAME = THROW_EXCEPTION_TYPE.getInternalName();
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
    private Map<BBlock, FanOut> contains;
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
     * a list of all try catch blocks which are started but not ended. this is updated as tryStart and tryEnd
     * labels are visited.
     */
    private List<TryCatchDetails> currentTryCatchStarts;
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
        contains = new HashMap<BBlock, FanOut>();
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
        contains.put(current, new FanOut(start));
        openMonitorEnters.put(start, new LinkedList<CodeLocation>());
        currentTryCatchStarts = new LinkedList<TryCatchDetails>();
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

    public FanOut getContains(BBlock block)
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
        FanOut containsFanOut = contains.get(block);
        if (containsFanOut == null) {
            containsFanOut = new FanOut(block.getLabel());
            contains.put(block, containsFanOut);
        }
        containsFanOut.append(label);
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
     * @return the list of open monitor enter locations in reverse order of appearance in the bytecode
     */
    public List<CodeLocation> getOpenMonitorEnters(BBlock block)
    {
        List<CodeLocation> blockMonitorEnters = null;

        // if this is a handler target block then it will have an attached list of handler starts
        // the open enters list can be constructed by combining the lists attached to the try catch
        // details.

        Iterator<TryCatchDetails> iterator = block.getHandlerStarts();

        if (iterator.hasNext()) {
            // at least one try/catch targets this block as a handler
            blockMonitorEnters = new LinkedList<CodeLocation>();
            while (iterator.hasNext()) {
                TryCatchDetails details = iterator.next();
                details.addOpenLocations(blockMonitorEnters);
            }

            return blockMonitorEnters;
        }

        // ok that failed so look for a control flow label with the propagated information
        // any of them will do since they all *must* have the same open monitor list

        FanOut fanOut = getContains(block);
        int count = fanOut.getToCount();
        for (int i = 0; i < count; i++) {
            Label l = fanOut.getTo(i);
            CodeLocation loc = getLocation(l);
            if (loc.getInstructionIdx() > 0) {
                // nothing open on entry to this block
                break;
            }
            // see if we have an inherited list
            blockMonitorEnters = getOpenMonitorEnters(l);
            if (blockMonitorEnters != null) {
                return new LinkedList<CodeLocation>(blockMonitorEnters);
            }
        }

        // just return an empty list

        return new LinkedList<CodeLocation>();
    }

    /**
     * retrieve the list of monitor enter locations associated with a trigger block. this is called
     * when we are inserting try catch handlers for trigger locations to determine whether they need
     * to perform any monitor exit operations before executing the normal trigger exception handling code.
     * @param triggerDetails the trigger being checked
     * @return the list of locations for monitor enters open at the trigger start
     */
    public Iterator<CodeLocation> getOpenMonitors(TriggerDetails triggerDetails)
    {
        // there should be at least one try catch handlers associated with the trigger start label
        List<TryCatchDetails> tryCatchDetails = tryCatchStartDetails(triggerDetails.getStart());
        // all 3 handler should have the same open monitor enters list
        return tryCatchDetails.get(0).getOpenEnters();
    }

    /**
     * pair a monitor enter instruction with an associated monitor exit instructions
     * @param enter
     * @param exit
     */
    void addMonitorPair(CodeLocation enter, CodeLocation exit)
    {
        List<CodeLocation> paired = monitorPairs.get(enter);
        if (paired == null) {
            paired = new LinkedList<CodeLocation>();
            monitorPairs.put(enter, paired);
        }
        if (!paired.contains(exit)) {
            paired.add(exit);
            // we also need to be able to query this relationship in reverse order
            CodeLocation inverse = inverseMonitorPairs.put(exit, enter);
        }
    }

    /**
     * locate a monitor exit instruction in block associated with a given monitor enter
     * @param enter
     */
    private CodeLocation getPairedExit(CodeLocation enter, BBlock block)
    {
        List<CodeLocation> paired = monitorPairs.get(enter);
        if (paired != null) {
            Iterator<CodeLocation> iter = paired.iterator();
            while (iter.hasNext()) {
                CodeLocation location = iter.next();
                if (location.getBlock() == block) {
                    return location;
                }
            }
        }

        return null;
    }

    /**
     * locate the monitor enter instruction associated with a given monitor exit
     * @param exit
     */
    public CodeLocation getPairedEnter(CodeLocation exit)
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
            System.out.println("getSavedMonitorIdx : unexpected! close pair has invalid index " + instructionIdx + " in method " + methodName);
        }
        int instruction = block.getInstruction(instructionIdx);
        if (instruction != Opcodes.MONITORENTER) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair instruction " + instruction + " is not MONITOREXIT in method " + methodName);
        }
        instructionIdx--;
        instruction = block.getInstruction(instructionIdx );
        // normally the monitorenter is preceded by a DUP ASTORE pair to save the monitor object
        // however, if an AT SYNCHRONIZE trigger has been injected before the MONITORENTER then
        // there may be a call to Rule.execute between the ASTORE and the MONITORENTER
        if (instruction == Opcodes.INVOKESTATIC) {
            // we can safely skip backwards to the last ASTORE because the trigger sequence will not
            // use an ASTORE
            while (instruction != Opcodes.ASTORE && instructionIdx > 0) {
                // skip backwards until we find the required ASTORE
                instructionIdx--;
                instruction = block.getInstruction(instructionIdx);
            }
        }
        if (instruction != Opcodes.ASTORE) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair preceding instruction " + instruction + " is not ASTORE in method " + methodName);
            return -1;
        }
        int varIdx = block.getInstructionArg(instructionIdx, 0);
        if (varIdx < 0) {
            System.out.println("getSavedMonitorIdx : unexpected! close pair preceding ASTORE instruction has invalid index " + varIdx + " in method " + methodName);
        }
        return varIdx;
    }

    /**
     * test whether there are any open monitorenters with no corresponding monitorexit on a path to
     * the current instruction
     *
     * @return true if there are open monitorenters otherwise false
     */
    public boolean inOpenMonitor()
    {
        List<CodeLocation> currentOpenEnters = currentOpenEnters(false);
        return currentOpenEnters.size() > 0;
    }


    /**
     * return a list of all open monitorenters with no corresponding monitorexit on a path to
     * the current instruction
     *
     * @param dumpOk true if it is appropriate todump the cfg at this point
     * @return true if there are open monitorenters otherwise false
     */

    private List<CodeLocation> currentOpenEnters(boolean dumpOk)
    {
        // any opens which were not closed in this block will have been retained in
        // the monitorEnters list in reverse order of appearance.
        //
        // any opens which were closed in this block will have been installed as a pair with their
        // corresponding close.

        Iterator<CodeLocation> entersIter = current.getMonitorEnters();

        // any closes which occurred in this block will have been retained in
        // the monitorExits list

        Iterator<CodeLocation> exitsIter = current.getMonitorExits();

        // opens which have been propagated to this block can be identified by calling getOpenMonitorEnters(current)

        List<CodeLocation> openEnters = getOpenMonitorEnters(current);

        Iterator<CodeLocation> openEntersIter = openEnters.iterator();

        // if we are dumping debug info then do it now
        if (Transformer.isDumpCFGPartial() && dumpOk) {
            List<TryCatchDetails> active = current.getActiveTryStarts();
            int openEntersCount = openEnters.size();

            System.out.print("Carry forward open monitors for " + current.getBlockIdx() +" ==> {" );
            String sepr = "";
            for (int i = 0; i < openEntersCount; i++) {
                System.out.print(sepr);
                System.out.print(openEnters.get(i));
                sepr=", ";
            }
            System.out.println("}");

            int activeTryStartsCount = active.size();
            System.out.print("active try starts for " + current.getBlockIdx() +" ==> {" );
            sepr = "";
            for (int i = 0; i < activeTryStartsCount; i++) {
                TryCatchDetails details = active.get(i);
                CodeLocation start = getLocation(details.getStart());
                CodeLocation end = getLocation(details.getEnd());
                System.out.print(sepr);
                System.out.print(start);
                if (end != null) {
                    System.out.print(":");
                    System.out.print(end);
                }
                sepr=", ";
            }
            System.out.println("}");

            int currentTryStartsCount = currentTryCatchStarts.size();
            System.out.print("current try starts for " + current.getBlockIdx() +" ==> {" );
            sepr = "";
            for (int i = 0; i < currentTryStartsCount; i++) {
                TryCatchDetails details = currentTryCatchStarts.get(i);
                CodeLocation start = getLocation(details.getStart());
                CodeLocation end = getLocation(details.getEnd());
                System.out.print(sepr);
                System.out.print(start);
                if (end != null) {
                    System.out.print(":");
                    System.out.print(end);
                }
                sepr=", ";
            }
            System.out.println("}");
        }

        // find any exits which have not been closed

        List<CodeLocation> nonLocalExits = new LinkedList<CodeLocation>();

        while (exitsIter.hasNext()) {
            CodeLocation exit = exitsIter.next();
            // see if this one is paired off with a local enter
            CodeLocation enter = getPairedEnter(exit);
            if (enter ==  null || enter.getBlock() != current) {
                // this is a non-local exit matching a a propagated enter
                nonLocalExits.add(exit);
            }
        }

        // now pair off propagated enters with any non local exits

        exitsIter = nonLocalExits.iterator();

        while (exitsIter.hasNext() && openEntersIter.hasNext()) {
            CodeLocation exit = exitsIter.next();
            CodeLocation enter = openEntersIter.next();
            // we may have already done this but it is idempotent
            addMonitorPair(enter, exit);
        }

        // sanity check
        if (exitsIter.hasNext()) {
            System.out.println("exits unaccounted for in block B" + current.getBlockIdx());
        }

        // any left over values are still open at the end of this block so accumulate them
        //  keeping them in reverse order of appearance

        LinkedList<CodeLocation> newOpenEnters = new LinkedList<CodeLocation>();

        while (entersIter.hasNext()) {
            newOpenEnters.add(entersIter.next());
        }

        if (openEntersIter != null) {
            while (openEntersIter.hasNext()) {
                newOpenEnters.add(openEntersIter.next());
            }
        }

        return newOpenEnters;
    }

    /**
     * forward details of open monitor and try catch block locations from the current
     * block to its reachable labels. This is always called just before splitting the current block.
     */
    private void carryForward()
    {
        if (Transformer.isDumpCFGPartial()) {
            int blockIdx = current.getBlockIdx();
            if (blockIdx ==  0) {
                System.out.println("Intermediate Control Flow Graph for " + methodName);
            }
            System.out.println("Carry forward for block " + blockIdx);
        }
        
        Label label = current.getLabel();
        int nOuts = current.nOuts();

        // identify which try start regions overlap this block
        //
        // the current try start list will include try catch regions which subsume the whole of this block
        // however, some regions may have partially overlapped so we need to add them too. to do this we need
        // to add any starts which have block offset greater than 0

        List<TryCatchDetails> active = new ArrayList<TryCatchDetails>(currentTryCatchStarts);

        Iterator<TryCatchDetails> ends = current.getTryEnds();

        while (ends.hasNext()) {
            TryCatchDetails details = ends.next();
            CodeLocation location = getLocation(details.getEnd());
            if (location.getInstructionIdx() > 0) {
                active.add(details);
            }
        }

        // any remaining starts are active somewhere in the block and hence indicate
        // possible exception control flow

        current.setActiveTryStarts(active);

        if (Transformer.isDumpCFGPartial()) {
            System.out.println(current);
        }

        // compute the list of monitorenters which are still open at the current instruction
        List<CodeLocation> newOpenEnters = currentOpenEnters(true);

        int newOpenCount = newOpenEnters.size();
        
        // ok, now attach the list to all blocks reachable via normal control flow

        // first the blocks reachable via jump links

        // n.b. link 0 is the next block in line, if it is reachable then it will also appear as a later link
        // so we start the iteration from 1

        for (int i = 1; i <= nOuts; i++) {
            label = current.nthOut(i);
            List<CodeLocation> blockOpenEnters = openMonitorEnters.get(label);
            if (blockOpenEnters == null) {
                openMonitorEnters.put(label, newOpenEnters);
                if (Transformer.isDumpCFGPartial()) {
                    System.out.print("open monitors " + label + " ==> {");
                    String sepr="";
                    for (int j = 0; j < newOpenCount; j++) {
                        CodeLocation l = newOpenEnters.get(j);
                        System.out.print(sepr);
                        System.out.print("BB");
                        System.out.print(l.getBlock().getBlockIdx());
                        System.out.print(".");
                        System.out.print(l.getInstructionIdx());
                        sepr=", ";
                    }
                    System.out.println("}");
                }
            } else {
                // sanity check
                // this should contain the same locations as our current list!
                int openCount = blockOpenEnters.size();
                if (openCount != newOpenCount) {
                    System.out.println("CFG.carryForward: unexpected! invalid open enters count for block " + label + " in method " + methodName);
                }
                for (int j = 0; j < newOpenCount && j < openCount; j++) {
                    CodeLocation l1 = blockOpenEnters.get(j);
                    CodeLocation l2 = newOpenEnters.get(j);
                    if (l1.getBlock() != l2.getBlock()) {
                        System.out.println("CFG.carryForward: unexpected! invalid open enters block for block " + label + " at index " + j + " in method " + methodName);
                    }
                    if (l1.getInstructionIdx() != l2.getInstructionIdx()) {
                        System.out.println("CFG.carryForward: unexpected! invalid open enters instruction index for block " + label + " at index " + j + " in method " + methodName);
                    }
                }
            }
        }
        if (Transformer.isDumpCFGPartial()) {
            System.out.println();
        }

        List<TryCatchDetails> activeTryStarts = current.getActiveTryStarts();

        if (activeTryStarts != null) {
            // now propagate open monitors to handler blocks reachable via exceptions thrown from this block
            // we need to be conservative here. a handler block may not actually be reachable because another
            // try catch block with a generic exception type shadows it.

            // check each monitor exited in the current block to see if it's extent overlaps an active try catch
            // anywhere in the current block

            Iterator<CodeLocation> exitIter  = current.getMonitorExits();

            while (exitIter.hasNext()) {
                CodeLocation exit = exitIter.next();
                CodeLocation enter = getPairedEnter(exit);
                Iterator<TryCatchDetails> activeStartsIter = activeTryStarts.iterator();
                while (activeStartsIter.hasNext()) {
                    TryCatchDetails activeRegion = activeStartsIter.next();
                    // skip if we know about this one already
                    if (activeRegion.containsOpenEnter(enter)) {
                        // ignore
                        continue;
                    }
                    // if the handler has already been visited then this try catch returns to this same
                    // block or one of its predecessors. that's not a real overlap since these try catches
                    // are only added to ensure that exceptions in the handler code reenter the handler
                    CodeLocation handlerStart = getLocation(activeRegion.getHandler());
                    if (handlerStart != null) {
                        continue;
                    }
                    // ok let's look for an overlap -- we will definitely have a start location
                    // but there may not be an end location yet
                    CodeLocation tryStart = getLocation(activeRegion.getStart());
                    CodeLocation tryEnd = getLocation(activeRegion.getEnd());
                    int containment = computeContainment(tryStart, tryEnd, enter, exit, OVERLAPS);
                    if (containment == OVERLAPS) {
                        // the enter/exit overlaps the try region but we still need to check whether
                        // it lies in a shadowing region
                        List<TryCatchDetails> shadowRegions = activeRegion.getShadowRegions();
                        boolean isShadow = false;
                        if (shadowRegions != null) {
                            Iterator<TryCatchDetails> shadowRegionsIter = shadowRegions.iterator();
                            while (shadowRegionsIter.hasNext() && !isShadow) {
                                TryCatchDetails shadowRegion = shadowRegionsIter.next();
                                CodeLocation shadowStart = getLocation(shadowRegion.getStart());
                                CodeLocation shadowEnd = getLocation(shadowRegion.getEnd());
                                containment = computeContainment(shadowStart, shadowEnd, enter, exit, CONTAINS);
                                // we will not see UNKNOWN in this case as exit is known
                                if (containment == CONTAINS) {
                                    // this region encloses the enter/exit so there is no need to propagate it
                                    if (Transformer.isDumpCFGPartial()) {
                                        System.out.println("ignoring open enter " +  enter +
                                                " for region " + tryStart +  ":" + (tryEnd != null ? tryEnd.toString() : "??") +
                                                " shadowed by region " + shadowStart +  ":" + (shadowEnd != null ? shadowEnd.toString() : "??"));
                                    }
                                    isShadow = true;
                                }
                            }
                        }
                        if (!isShadow) {
                            // ok, we need to add the enter to this region's open enter list
                            if (Transformer.isDumpCFGPartial()) {
                                System.out.println("propagating enter " +  enter + " to try handler for " +
                                        tryStart +  ":" + (tryEnd != null ? tryEnd.toString() : "??"));
                            }
                            activeRegion.addOpenEnter(enter);
                        }
                    }
                }
            }

            // for each monitor enter open at the end of the block to see if it's extent overlaps an active try catch
            // anywhere in the current block

            Iterator<CodeLocation> newOpenEntersIter = newOpenEnters.iterator();

            while (newOpenEntersIter.hasNext()) {
                CodeLocation enter = newOpenEntersIter.next();
                Iterator<TryCatchDetails> activeStartsIter = activeTryStarts.iterator();
                while (activeStartsIter.hasNext()) {
                    TryCatchDetails activeRegion = activeStartsIter.next();
                    // skip if we know about this one already
                    if (activeRegion.containsOpenEnter(enter)) {
                        // ignore
                        continue;
                    }
                    // ok let's look for an overlap there may not be an end location yet
                    // but we know the try start must precede the monitor exit because we have not
                    // reached the exit and we know the try is active somewhere in this block
                    CodeLocation tryStart = getLocation(activeRegion.getStart());
                    CodeLocation tryEnd = getLocation(activeRegion.getEnd());
                    int containment = computeContainment(tryStart, tryEnd, enter, null, OVERLAPS);

                    if (containment == OVERLAPS) {
                        // the enter/exit overlaps the try region but we still need to check whether
                        // it lies in a shadowing region
                        List<TryCatchDetails> shadowRegions = activeRegion.getShadowRegions();
                        boolean isShadow = false;
                        if (shadowRegions != null) {
                            Iterator<TryCatchDetails> shadowRegionsIter = shadowRegions.iterator();
                            while (shadowRegionsIter.hasNext() && !isShadow) {
                                TryCatchDetails shadowRegion = shadowRegionsIter.next();
                                CodeLocation shadowStart = getLocation(shadowRegion.getStart());
                                CodeLocation shadowEnd = getLocation(shadowRegion.getEnd());
                                containment = computeContainment(shadowStart, shadowEnd, enter, null, CONTAINS);
                                if (containment == CONTAINS) {
                                    // ok, the inner shadowing region overlaps the enter/exit
                                    if (Transformer.isDumpCFGPartial()) {
                                        System.out.println("ignoring open enter " +  enter +
                                                " for region " + tryStart +  ":" + (tryEnd != null ? tryEnd.toString() : "??") +
                                                " shadowed by region " + shadowStart +  ":" + (shadowEnd != null ? shadowEnd.toString() : "??"));
                                    }
                                    isShadow = true;
                                } else if (containment == UNKNOWN) {
                                    // this region may shadow the outer region but we don't know because we
                                    // have not yet seen an exit or a tryEnd for the inner region -- we will
                                    // find out when a monitor exit or tryEnd is reached so just delay for now
                                    if (Transformer.isDumpCFGPartial()) {
                                        System.out.println("ignoring open enter " +  enter +
                                                " for region " + tryStart +  ":" + (tryEnd != null ? tryEnd.toString() : "??") +
                                                " potentially shadowed by region " + shadowStart +  ":" + (shadowEnd != null ? shadowEnd.toString() : "??"));
                                    }
                                    isShadow = true;
                                }
                            }
                        }
                        if (!isShadow) {
                            // ok, we need to add the enter to this regions open enter list
                            if (Transformer.isDumpCFGPartial()) {
                                System.out.println("propagating enter " +  enter + " to try handler for " +
                                        tryStart +  ":" + (tryEnd != null ? tryEnd.toString() : "??"));
                            }
                            activeRegion.addOpenEnter(enter);
                        }
                    }
                }
            }
        }
    }

    /**
     * flag value passed to request a check for an overlap and returned to notify an overlap
     */
    private final static int OVERLAPS = 1;
    /**
     * flag value passed to request a check for a containment and returned to notify a containment
     */
    private final static int CONTAINS = 2;
    /**
     * flag value returned to notify that a containment cannot yet be computed
     */
    private final static int UNKNOWN = 4;

    /**
     * compute whether the the region defined by a given enter and exit location pair overlaps or is contained within
     * the region defined by a try start and end location pair when both regions ar erestricted to the current block
     * @param tryStart the location of the start of the try region which will already have been visited
     * @param tryEnd the location of the end of the try region which may be null because the end point has not been
     * yet visited
     * @param enter the location of the start of the monitor region which will already have been visited
     * @param exit the location of an exit corresponding to the enter which may be null because the exit has not
     * yet been visited
     * @param flags OVERLAPS if an overlap is being checked for or CONTAINS if a containment is being checked for
     * @return OVERLAPS if the monitor region overlaps the try region and an overlap is being checked
     * for or CONTAINS if the monitor region is definitely contained within the try region and containment is being
     * checked for or UNKNOWN if the monitor region cannot yet be determined to be contained within the try region
     * and containment is being checked or 0 if there is no overlap and and an overlap is being checked
     * for or 0 if there is definitely no containment and containment is being checked for.
     */
    private int computeContainment(CodeLocation tryStart, CodeLocation tryEnd,
                                   CodeLocation enter, CodeLocation exit, int flags)
    {
        int result = 0;

        // we are only interested in overlaps or containment in the current block so
        // restrict the region start to be inside this block

        if (tryStart.getBlockIdx() < current.getBlockIdx()) {
            tryStart = new CodeLocation(current, 0);
        }
        if (enter.getBlockIdx() < current.getBlockIdx()) {
            enter = new CodeLocation(current, 0);
        }

        // tryStart and enter will both be non-null but either or both of exit and tryEnd can null indicating that
        // they are both greater than or equal to the current position. by checking specifically for these cases
        // we can avoid having to create code locations

        if (exit == null) {
            if (tryEnd == null) {
                // there must at least be an OVERLAP since we have started both regions and have not exited either of
                // them. however we cannot determine containment yet
                if ((flags & CONTAINS) != 0) {
                    return UNKNOWN;
                }
                return OVERLAPS;
            } else {
                // the tryEnd precedes the exit. if it also precedes the enter then there is no overlap
                if (tryEnd.compareTo(enter) <= 0) {
                    return 0;
                }
                // the enter is contained in the try region so we have an overlap. we cannot have containment
                // because the exit lies beyond the try end
                if ((flags & CONTAINS) == 0) {
                    return OVERLAPS;
                } else {
                    return 0;
                }
            }
        } else if (tryEnd == null) {
            // well, we have an exit which precedes the try end. if it also precedes the try start then there
            // is no overlap
            if (exit.compareTo(tryStart) < 0) {
                return 0;
            }
            // the exit is contained in the try region so we have an overlap. if we are not interested in
            // containment then we are done
            if ((flags & CONTAINS) == 0) {
                return OVERLAPS;
            }
            // for containment we need the enter to be at or after the try start -- actually we also allow it
            // to be in the same block as the try start but precede it by one instruction because the enter
            // is only exposed starting from the instruction which follows it

            if (tryStartMayContainEnter(tryStart, enter)) {
                return CONTAINS;
            }
            // no containment
            return 0;
        } else {
            // we have a start and end for both regions. we can rule out any overlap/containment if the exit
            // precedes the try start or the enter follows the try end
            if (exit.compareTo(tryStart) < 0 || tryEnd.compareTo(enter) <= 0) {
                return 0;
            }
            // we must at least have an overlap. if we are not interested in
            // containment then we are done
            if ((flags & CONTAINS) == 0) {
                return OVERLAPS;
            }
            // for containment we need the exit to preceded the tryEnd and the enter to be at or after the try
            // start -- actually we also allow it to be in the same block as the try start but precede it by
            // one instruction because the enter is only exposed starting from the instruction which follows it

            // first check the exit against the try end
            if (exit.compareTo(tryEnd) >= 0) {
                // no containment
                return 0;
            }
            if (tryStartMayContainEnter(tryStart, enter)) {
                return CONTAINS;
            }
            // no containment
            return 0;
        }
    }

    /**
     * check whether the instructions exposed by a monitor enter may be contained within the scope of a
     * tryStart. this is possible if the try start begins before the first instruction which follows the
     * enter i.e. if the try start has a lower block idx than the enter or has the same block idx and an
     * instruction offset less than or equal to the enter instruction idx + 1.
     * @param enter
     * @param tryStart
     * @return
     */
    private boolean tryStartMayContainEnter(CodeLocation tryStart, CodeLocation enter)
    {
        int enterBlockIdx = enter.getBlockIdx();
        int tryStartBlockIdx = tryStart.getBlockIdx();
        if (tryStartBlockIdx < enterBlockIdx) {
            return true;
        }
        if (tryStartBlockIdx == enterBlockIdx) {
            int enterInsnIdx = enter.getInstructionIdx();
            int tryStartInsnIdx = tryStart.getInstructionIdx();
            if (tryStartInsnIdx <= enterInsnIdx + 1) {
                return true;
            }
        }
        return false;
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
        // maintained in each block. in the former two cases we also need to update the set of currently
        // open try starts

        List<TryCatchDetails> newStarts = tryCatchStartDetails(label);

        if (newStarts != null) {
            current.addTryStarts(newStarts);
            currentTryCatchStarts.addAll(newStarts);
        }

        List<TryCatchDetails> newEnds = tryCatchEndDetails(label);

        if (newEnds != null) {
            current.addTryEnds(newEnds);
            currentTryCatchStarts.removeAll(newEnds);
        }

        List<TryCatchDetails> newhandlers = tryCatchHandlerStartDetails(label);

        if (newhandlers != null) {
            current.addHandlerStarts(newhandlers);
        }

        if (newStarts != null) {
            // we need to identify whether any of the new try catch regions shadows any outer try catch regions
            // and add them to the shadow list for those outer regions. shadowing occurs if the inner region
            // has a catch type which is the same as or a superclass of the outer region catch type or
            // if the inner region is a catch all. we cannot guarantee to detect superclass relations as
            // that requires code loading. but we can detect shadowing for same type and catch alls
            // TODO extend this to cope with superclass shadowing

            // currentTryStarts contains all tryStarts which have not yet been closed
            Iterator<TryCatchDetails> currentStartsIter = currentTryCatchStarts.iterator();

            while (currentStartsIter.hasNext()) {
                TryCatchDetails currentStart = currentStartsIter.next();
                if (newStarts.contains(currentStart)) {
                    // this was just added so no shadowing occurs
                    continue;
                }
                Iterator<TryCatchDetails> newStartsIter = newStarts.iterator();
                while (newStartsIter.hasNext()) {
                    TryCatchDetails newStart = newStartsIter.next();
                    // TODO extend this to cope with superclass shadowing
                    if (newStart.getType() == null || newStart.getType().equals(currentStart.getType())) {
                        currentStart.addShadowRegion(newStart);
                    }
                }
            }
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
        latestTrigger = null;
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
     * check if the current block is a byteman-generated handler i.e. one which was created to
     * catch an exception thrown by the byteman runtime. n.b. a byteman handler only ever spans
     * one block.
     * @return true if the current block is a byteman-generated handler
     */
    public boolean inBytemanHandler()
    {
        // we just need to check whether the current block has a handler with a byteman exception type

        Iterator<TryCatchDetails> handlerStarts = current.getHandlerStarts();
        while (handlerStarts.hasNext()) {
            TryCatchDetails details = handlerStarts.next();
            // any trigger we have planted will be tagged as such
            if (details.isTriggerHandler()) {
                return true;
            }
            // handlers planted by previous transforms will not be tagged but will be for a byteman exception type
            String typeName = details.getType();
	    // n.b. handlers for finally blocks will have a null  type name
	    if (typeName != null) {
		if (typeName.equals(CFG.EARLY_RETURN_EXCEPTION_TYPE_NAME) ||
                    typeName.equals(CFG.EXECUTE_EXCEPTION_TYPE_NAME) ||
                    typeName.equals(CFG.THROW_EXCEPTION_TYPE_NAME)) {
		    return true;
		}
            }
        }
        
        return false;
    }
    /**
     * return true if the current block is a rethrow handler i.e. one which was created to close a monitor
     * exit instruction and then rethrow an exception. n.b. this must only be called when the next instruction
     * to be added to the byetcode sequence is an ATHROW
     * @return true if the current block is a rethrow handler
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
     * check if the current block is a byteman-generated trigger section. this can be checked by testing whether
     * there is an open try catch for one of the Byteman exception types
     * @return true if the current block is a byteman-generated trigger section
     */
    public boolean inBytemanTrigger()
    {
        // if we are in the middle of injecting a trigger then latestTrigger will be non null
        if (latestTrigger != null) {
            return true;
        }
        // if we are in a previously injected trigger then we will be in the scope of a try catch
        // which hanldes one of the Byteman generated exceptions

        Iterator<TryCatchDetails> currentTryStarts = currentTryCatchStarts.iterator();
        while (currentTryStarts.hasNext()) {
            TryCatchDetails details = currentTryStarts.next();
            // any trigger we have planted will be tagged as such
            if (details.isTriggerHandler()) {
                return true;
            }
            // handlers planted by previous transforms will not be tagged but will be for a byteman exception type
            String typeName = details.getType();
            if (typeName.equals(CFG.EARLY_RETURN_EXCEPTION_TYPE_NAME) ||
                    typeName.equals(CFG.EXECUTE_EXCEPTION_TYPE_NAME) ||
                    typeName.equals(CFG.THROW_EXCEPTION_TYPE_NAME)) {
                return true;
            }
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

