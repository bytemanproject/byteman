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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.jboss.byteman.agent.adapter.OpcodesHelper;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * A Basic Block represents a segment of bytecode in a control flow graph. Basic blocks divide up the
 * code at control flow branch points and hence there is no normal control flow internal to a block. Normal
 * control flow will only transfer control from the end of one basic block to the start of another
 * basic block or to the caller (via a return or throw).
 *
 *
 * If the block overlaps a try/catch region then exception control flow may transfer control
 * from any instruction lying within the try/catch region to the the start of another basic
 * block which handles the instruction. So, exception control flow may exit a block at a location
 * preceding the block end but may only enter at block start.
 */

public class BBlock
{
    private CFG cfg;

    /**
     * the sequence of instructions contained in this basic block
     */
    private InstructionSequence instructions;
    /*
     * A collection of the outgoing links from this basic block to other basic blocks.
     * Every block except the last one has at least one outgoing link because link 0
     * identifies the linkage of blocks in the original bytecode ordering. Any subsequent
     * links identify outgoing non-excepting control flow from the basic block. The number
     * and significance of these subsequent links is determined by the type of the last
     * instruction. As a consequence iterations over this collection should be 1-based, not 0-based.
     *
     * Blocks ending in RETURN or ATHROW are terminal so they have no extra links.
     *
     * Blocks ending in GOTO have on extra link, identifying the target of the GOTO instruction.
     *
     * Blocks ending in an IF_XXX instruction have two extra links, the target branch if the
     * instruction computes to false followed by the target branch if the instruction computes to
     * true.
     */
    private FanOut outGoing;

    /**
     * an index for the block allocated by the CFG starting from 0 in block order
     */
    private int blockIdx;

    /**
     * details of all try catch blocks which are active inside this block. n.b. this must omit try
     * catch blocks which are open when the block is created then subsequently closed at offset 0.
     */
    private List<TryCatchDetails> activeTryStarts;
    /**
     * details of all try catch blocks which start in this block
     */
    private List<TryCatchDetails> tryStarts;
    /**
     * a list of all try catch blocks which end in this block
     */
    private List<TryCatchDetails> tryEnds;
    /**
     * a list of all try catch blocks whose handlers start in this block
     */
    private List<TryCatchDetails> handlerStarts;

    /**
     * a stack (reverse order list) containing the locations of all monitor enter instructions contained in this
     * block excluding those which have been closed by a corresponding exit in this block
     */
    private LinkedList<CodeLocation> monitorEnters;
    /**
     * a list of the location of all monitor exit instructions contained in this block
     */
    private LinkedList<CodeLocation> monitorExits;

    /**
     * construct a new basic block
     * @param cfg the control flow graph it belongs to
     * @param start the label for the start of the block
     * @param blockIdx the index of the block which respects the order of the bytecode segments
     * contained in each block.
     */
    public BBlock(CFG cfg, Label start, int blockIdx)
    {
        this.cfg = cfg;
        this.instructions = new InstructionSequence();
        this.outGoing = new FanOut(start);
        this.blockIdx = blockIdx;
        this.activeTryStarts = null;
        this.tryStarts = new LinkedList<TryCatchDetails>();
        this.tryEnds = new LinkedList<TryCatchDetails>();
        this.handlerStarts = new LinkedList<TryCatchDetails>();
        this.monitorEnters = new LinkedList<CodeLocation>();
        this.monitorExits = new LinkedList<CodeLocation>();
    }

    /**
     * obtain the control flow graph to which this block belongs
     * @return the control flow graph
     */
    public CFG getCFG()
    {
        return cfg;
    }

    /**
     * get the primary label which idenitfies this block. It will be located in the block at offset 0.
     * @return the primary label
     */
    public Label getLabel()
    {
        return outGoing.getFrom();
    }

    /**
     * retrieve the index of this block in the block sequence.
     * @return the block index
     */
    public int getBlockIdx()
    {
        return blockIdx;
    }

    /**
     * add an instruction to the sequence in the block
     * @param instruction an Opcode
     * @return the index of the newly added instruction
     */
    public int append(int instruction)
    {
        int index = instructions.add(instruction);
        if (instruction == Opcodes.MONITORENTER) {
            // push onto front of list
            monitorEnters.push(new CodeLocation(this, index));
        } else if (instruction == Opcodes.MONITOREXIT) {
            CodeLocation exit = new CodeLocation(this, index);
            // we need to keep track of exits for when we can collate monitor section ends
            // with try catch blocks which overlapthis block so we don't drop them here
            // even if there is a matching enter in this block
            monitorExits.add(exit);
            // however if there is an enter in this block then it belongs to this exit
            // so we pop it to ensurewe only retain active local enters
            // we also record the pairing so we can work back from the matched exit to
            //its enter. pairing with non-local enters is done at block end carry forward
            if (!monitorEnters.isEmpty()) {
                CodeLocation enter = monitorEnters.pop();
                cfg.addMonitorPair(enter, exit);
            }
        }
        return index;
    }

    /**
     * add an instruction with one int operand to thhe sequence in the block
     * @param instruction an Opcode
     * @param operand an int operand or the code for a String operand lcoated in the cfg name table
     * @return the index of the newly added instruction
     */
    public int append(int instruction, int operand)
    {
        return instructions.add(instruction, operand);
    }

    /**
     * add an instruction with two int operands to the sequence in the block
     * @param instruction an Opcode
     * @param operand1 an int operand or the code for a String operand lcoated in the cfg name table
     * @param operand2 an int operand or the code for a String operand lcoated in the cfg name table
     * @return the index of the newly added instruction
     */
    public int append(int instruction, int operand1, int operand2)
    {
        return instructions.add(instruction, operand1, operand2);
    }

    /**
     * add an instruction with three int operands to thhe sequence in the block
     * @param instruction an Opcode
     * @param operand1 an int operand or the code for a String operand lcoated in the cfg name table
     * @param operand2 an int operand or the code for a String operand lcoated in the cfg name table
     * @param operand3 an int operand or the code for a String operand lcoated in the cfg name table
     * @return the index of the newly added instruction
     */
    public int append(int instruction, int operand1, int operand2, int operand3)
    {
        return instructions.add(instruction, operand1, operand2, operand3);
    }

     /**
     * add an instruction with four int operands to the sequence in the block
     * @param instruction an Opcode
     * @param operand1 an int operand or the code for a String operand lcoated in the cfg name table
     * @param operand2 an int operand or the code for a String operand lcoated in the cfg name table
     * @param operand3 an int operand or the code for a String operand lcoated in the cfg name table
     * @param operand4 an int operand or the code for a String operand lcoated in the cfg name table
     * @return the index of the newly added instruction
     */
    public int append(int instruction, int operand1, int operand2, int operand3, int operand4)
    {
        return instructions.add(instruction, operand1, operand2, operand3, operand4);
    }

   /**
     * add an instruction with an arbitrary number of int operands to thhe sequence in the block

     * @param instruction an Opcode
     * @param operands an array containing int operands or codes for String operands lcoated in the cfg name table
     * @return the index of the newly added instruction
     */
    public int append(int instruction, int[] operands)
    {
        return instructions.add(instruction, operands);
    }

    /**
     * record details of a try catch block which starts in this block
     * @param details list of try catch block details
     */
    public void addTryStarts(List<TryCatchDetails> details)
    {
        tryStarts.addAll(details);
    }

    /**
     * record details of a try catch block which ends in this block
     * @param details list of try catch block details
     */
    public void addTryEnds(List<TryCatchDetails> details)
    {
        tryEnds.addAll(details);
    }

    /**
     * record details of a try catch block handler which starts in this block
     * @param details list of try catch block details
     */
    public void addHandlerStarts(List<TryCatchDetails> details)
    {
        handlerStarts.addAll(details);
    }

    /**
     * set the list of try starts which are active somewhere in this block.
     * @param active list of active try catch block details
     */
    public void setActiveTryStarts(List<TryCatchDetails> active)
    {
        activeTryStarts = active;
    }

    /**
     * retrieve details of all try catch blocks which end in this block
     * @return list of try catch block details
     */
    public Iterator<TryCatchDetails> getTryEnds()
    {
        return tryEnds.iterator();
    }

    /**
     * retrieve details of all try catch block handlers whcih start in this block
     * @return list of try catch block details
     */
    public Iterator<TryCatchDetails> getHandlerStarts()
    {
        return handlerStarts.iterator();
    }

    /**
     * retrieve details of all try catch blocks which are capable of generating an exception in this block
     * @return list of active try catch block details
     */
    public List<TryCatchDetails> getActiveTryStarts()
    {
        return activeTryStarts;
    }

    /**
     * retrieve a list of all monitor enter instruction locations occurring in this block
     * @return list of monitor enter locations
     */
    public Iterator<CodeLocation> getMonitorEnters()
    {
        return monitorEnters.iterator();
    }

    /**
     * retrieve a list of all monitor exit instruction locations occurring in this block
     * @return list of monitor exit locations
     */
    public Iterator<CodeLocation> getMonitorExits()
    {
        return monitorExits.iterator();
    }

    /**
     * retrieve a count of all monitor enter instruction locations occurring in this block
     * @return count of monitor enter locations
     */
    public int getMonitorEnterCount()
    {
        return monitorEnters.size();
    }

    /**
     * retrieve a count of all monitor exit instruction locations occuring in this block
     * @return count of monitor exit locations
     */
    public int getMonitorExitCount()
    {
        return monitorExits.size();
    }

    /**
     * return the number of instructions in the blocks instructuion sequence equivalent to the
     * index of the next instruction added to the block.
     * @return the next instruction count
     */
    public int getInstructionCount()
    {
        return instructions.size();
    }

    /**
     * return the instruction at a given index.
     * @param index the index for the instruction
     * @return the instruction at index
     */
    public int getInstruction(int index)
    {
        return instructions.get(index);
    }

    /**
     * retrieve the integer operand or encoded name associated with a particular instruction
     * @param index the index  of the instruction in the block
     * @param argIndex the index of the argument in the sequence of arguments presented when the instruction
     * was inserted into the block.
     * @return the arg
     */
    public int getInstructionArg(int index, int argIndex)
    {
        return instructions.getArg(index, argIndex);
    }

    /**
     * install an outgoing normal control flow link
     * @param label the control flow destination
     */
    public void append(Label label)
    {
        outGoing.append(label);
    }

    /**
     * return the label of the next block in line in the block sequence in bytecode order.
     * @return the label of the next block
     */
    public Label next()
    {
        return outGoing.getTo(0);
    }

    /**
     * return the label of the first normal control flow link
     * @return the label
     */
    public Label firstOut()
    {
        return outGoing.getTo(1);
    }

    /**
     * return the label of the second normal control flow link
     * @return the label
     */
    public Label secondOut()
    {
        return outGoing.getTo(2);
    }

    // n.b. index for out link is 1-based rather than 0-based as entry 0 is the next link
    /**
     * return the label of the nth normal control flow link
     * @param n the index of the link
     * @return the label
     */
    public Label nthOut(int n)
    {
        return outGoing.getTo(n);
    }

    /**
     * return a count of the normal control flow links from this block.
     * @return  the number of outgoingnormalcontrol flow links. n.b. iterations over
     * the links should count from 1 to nOuts() inclusive. this is the size of a 1-based
     * collection
     */
    public int nOuts()
    {
        return outGoing.getToCount() - 1;
    }

    /**
     * return a string representation of this block
     * @return a string representation
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        printTo(buf);
        return buf.toString();
    }

    public void printLabelOffset(StringBuffer buf, Label l, boolean appendBlockPos, Object altText)
    {
        CodeLocation loc = cfg.getLocation(l);
        if (loc != null) {
            buf.append(l.getOffset());
            if (appendBlockPos) {
                buf.append(" BB");
                buf.append(loc.getBlockIdx());
                buf.append('.');
                buf.append(loc.getInstructionIdx());
            }
        } else if (altText != null) {
            buf.append(altText);
        }
    }
    /**
     * write a string representation of this block to the buffer
     * @param buf the buffer to be written to
     */
    void printTo(StringBuffer buf)
    {
        int blockIdx = this.getBlockIdx();
        int bcpos = 0;
        buf.append(this.getLabel().getOffset());
        buf.append(": BB ");
        buf.append(blockIdx);
        buf.append("\n");
        FanOut containsFanOut = cfg.getContains(this);
        Iterator<Label> containsIter;
        Label containedLabel;
        int containedPosition;
        if (containsFanOut != null) {
            containsIter = containsFanOut.iterator();
            if (containsIter.hasNext()) {
                containedLabel = containsIter.next();
                containedPosition = cfg.getBlockInstructionIdx(containedLabel);
                bcpos =  containedLabel.getOffset();
            } else {
                containedLabel = null;
                containedPosition = -1;
            }
        } else {
            containsIter = null;
            containedLabel = null;
            containedPosition = -1;
        }

        int instructionCount = this.getInstructionCount();
        for (int i = 0; i < instructionCount; i++) {
            // we will never enter this if containedPosition is -1 which safeguards us when containsIter
            // is null or containedLabel is null
            while (containedPosition == i) {
                bcpos = containedLabel.getOffset();
                buf.append(bcpos);
                buf.append(": ");
                buf.append(containedLabel);
                if (cfg.tryCatchStart(containedLabel)) {
                    List<TryCatchDetails> detailsList = cfg.tryCatchStartDetails(containedLabel);
                    int detailsCount = detailsList.size();
                    for (int j = 0; j < detailsCount; j++) {
                        TryCatchDetails details = detailsList.get(j);
                        Label handlerLabel = details.getHandler();
                        buf.append("\n  ");
                        buf.append(" try ");
                        buf.append(details.getType());
                        buf.append(" -> ");
                        printLabelOffset(buf, handlerLabel, false, "??");
                        buf.append(" ");
                        buf.append(handlerLabel);
                    }
                }
                if (cfg.tryCatchEnd(containedLabel)) {
                    List<TryCatchDetails> detailsList = cfg.tryCatchEndDetails(containedLabel);
                    int detailsCount = detailsList.size();
                    for (int j = 0; j < detailsCount; j++) {
                        TryCatchDetails details = detailsList.get(j);
                        Label handlerLabel = details.getHandler();
                        buf.append("\n  ");
                        buf.append(" catch ");
                        buf.append(details.getType());
                        buf.append(" -> ");
                        printLabelOffset(buf, handlerLabel, false, "??");
                        buf.append(" ");
                        buf.append(handlerLabel);
                    }
                }
                if (cfg.tryCatchHandlerStart(containedLabel)) {
                    List<TryCatchDetails> detailsList = cfg.tryCatchHandlerStartDetails(containedLabel);
                    int detailsCount = detailsList.size();
                    for (int j = 0; j < detailsCount; j++) {
                        TryCatchDetails details = detailsList.get(j);
                        buf.append("\n  ");
                        buf.append(" handle ");
                        buf.append(details.getType());
                        buf.append(" <- ");
                        Label start = details.getStart();
                        Label end = details.getEnd();
                        printLabelOffset(buf, start, false, start);
                        buf.append(":");
                        printLabelOffset(buf, end, false, end);
                    }
                }
                if (cfg.triggerStart(containedLabel)) {
                    buf.append("\n  ");
                    buf.append(" trigger start");
                }
                if (cfg.triggerEnd(containedLabel)) {
                    buf.append("\n  ");
                    buf.append(" trigger end");
                }
                List<CodeLocation> openEnters = cfg.getOpenMonitorEnters(containedLabel);
                if (openEnters != null) {
                    int openCount = openEnters.size();
                    if (openCount > 0) {
                        buf.append("\n  ");
                        buf.append("open monitors: ");
                        for (int j = 0; j < openCount; j++) {
                            CodeLocation l = openEnters.get(j);
                            buf.append(" BB");
                            buf.append(l.getBlockIdx());
                            buf.append(".");
                            buf.append(l.getInstructionIdx());
                        }
                    }
                }
                buf.append("\n");
                containedLabel = (containsIter.hasNext() ? containsIter.next() : null);
                containedPosition = (containedLabel != null ? cfg.getBlockInstructionIdx(containedLabel) : -1);
            }
            // buf.append("   ");
                buf.append(bcpos);
            buf.append(":\t");
            buf.append(blockIdx);
            buf.append(".");
            buf.append(i);
            buf.append(": ");
            int opcode = this.getInstruction(i);
            int insnType = OpcodesHelper.insnType(opcode);
            switch (insnType) {
                case OpcodesHelper.INSN_NONE:
                {
                    // print the instruction name
                    buf.append(OpcodesHelper.insnName(opcode));
                    if (opcode == Opcodes.MONITOREXIT) {
                        CodeLocation exit = new CodeLocation(this, i);
                        CodeLocation enter = cfg.getPairedEnter(exit);
                        // print the corresponding open instruction
                        buf.append(" (enter: ");
                        buf.append(enter);
                        buf.append(")");
                    }
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_INT:
                {
                    // just print the instruction name and one integer argument
                    int intValue = this.getInstructionArg(i, 0);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(intValue);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_LDC:
                {
                    // print the instruction and one constant argument
                    int nameIdx = this.getInstructionArg(i, 0);
                    String name = cfg.getName(nameIdx);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(name);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_VAR:
                {
                    // print the instruction and the var idx
                    int varIdx = this.getInstructionArg(i, 0);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(varIdx);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_IINC:
                {
                    // print the instruction and the var idx
                    int increment = this.getInstructionArg(i, 0);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(increment);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_JUMP:
                {
                    // note that we may not have generated the code for the jump target yet
                    Label targetLabel = this.firstOut();
                    switch (opcode) {
                        case Opcodes.IFEQ:
                            buf.append("IFEQ ");
                            break;
                        case Opcodes.IFNE:
                            buf.append("IFNE ");
                            break;
                        case Opcodes.IFLT:
                            buf.append("IFLT ");
                            break;
                        case Opcodes.IFGE:
                            buf.append("IFGE ");
                            break;
                        case Opcodes.IFGT:
                            buf.append("IFGT ");
                            break;
                        case Opcodes.IFLE:
                            buf.append("IFLE ");
                            break;
                        case Opcodes.IF_ICMPEQ:
                            buf.append("IF_ICMPEQ ");
                            break;
                        case Opcodes.IF_ICMPNE:
                            buf.append("IF_ICMPNE ");
                            break;
                        case Opcodes.IF_ICMPLT:
                            buf.append("IF_ICMPLT ");
                            break;
                        case Opcodes.IF_ICMPGE:
                            buf.append("IF_ICMPGE ");
                            break;
                        case Opcodes.IF_ICMPGT:
                            buf.append("IF_ICMPGT ");
                            break;
                        case Opcodes.IF_ICMPLE:
                            buf.append("IF_ICMPLE ");
                            break;
                        case Opcodes.IF_ACMPEQ:
                            break;
                        case Opcodes.IF_ACMPNE:
                            buf.append("IF_ACMPNE ");
                            break;
                        case Opcodes.GOTO:
                            buf.append("GOTO ");
                            break;
                        case Opcodes.JSR:
                            buf.append("JSR ");
                            break;
                        case Opcodes.IFNULL:
                            buf.append("IFNULL ");
                            break;
                        case Opcodes.IFNONNULL:
                            buf.append("IFNONNULL ");
                            break;
                    }
                    printLabelOffset(buf, targetLabel, true, "??");
                    buf.append(" ");
                    buf.append(targetLabel);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_TSWITCH:
                {
                    Label targetLabel;
                    // print the instruction followed by the jump table discriminant min and max and then
                    // the jump labels
                    int min = this.getInstructionArg(i, 0);
                    int max = this.getInstructionArg(i, 1);
                    int count = (max + 1 - min);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(min);
                    buf.append(" ");
                    buf.append(max);
                    buf.append("\n");
                    for (int j = 1; j <= count; j++) {
                        // note that we may not have generated the code for the jump target yet
                        targetLabel = this.nthOut(j);
                        buf.append("    ");
                        buf.append(min + j);
                        buf.append(" : ");
                        printLabelOffset(buf, targetLabel, true, "??");
                        buf.append(" ");
                    }
                    targetLabel = this.firstOut();
                    buf.append("    dflt : ");
                    printLabelOffset(buf, targetLabel, true, "??");
                    buf.append(" ");
                }
                break;
                case OpcodesHelper.INSN_LOOKUP:
                {
                    Label targetLabel;
                    CodeLocation targetLocation;
                    int targetPos;
                    // print the instruction followed by each jump table discriminant and label
                    int count = this.getInstructionArg(i, 0);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append("\n");
                    for (int j = 1; j <= count; j++) {
                        // note that we may not have generated the code for the jump target yet
                        targetLabel = this.nthOut(j);
                        buf.append("    ");
                        buf.append(this.getInstructionArg(i, j));
                        buf.append(" : ");
                        printLabelOffset(buf, targetLabel, true, "??");
                        buf.append(" ");
                    }
                    targetLabel = this.firstOut();
                    buf.append("    dflt : ");
                    printLabelOffset(buf, targetLabel, true, "??");
                    buf.append(" ");
                }
                break;
                case OpcodesHelper.INSN_FIELD:
                case OpcodesHelper.INSN_METHOD:
                {
                    // print the instruction with the owner, name and descriptor
                    int idx1 = this.getInstructionArg(i, 0);
                    int idx2 = this.getInstructionArg(i, 1);
                    int idx3 = this.getInstructionArg(i, 2);
                    String owner = cfg.getName(idx1);
                    String name = cfg.getName(idx2);
                    String desc = cfg.getName(idx3);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(owner);
                    buf.append(" ");
                    buf.append(name);
                    buf.append(" ");
                    buf.append(desc);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_INDYMETH:
                {
                    // print the instruction with the owner, name and descriptor
                    int idx1 = this.getInstructionArg(i, 0);
                    int idx2 = this.getInstructionArg(i, 1);
                    String owner = cfg.getName(idx1);
                    String name = cfg.getName(idx2);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(owner);
                    buf.append(" ");
                    buf.append(name);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_TYPE:
                {
                    // print the instruction with the type name
                    int idx = this.getInstructionArg(i, 0);
                    String name = cfg.getName(idx);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(name);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_MULTIANEWARRAY:
                {
                    // print the instruction with the typename and the dimension count
                    int idx = this.getInstructionArg(i, 0);
                    int dims = this.getInstructionArg(i, 1);
                    String name = cfg.getName(idx);
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append(" ");
                    buf.append(name);
                    buf.append(" ");
                    buf.append(dims);
                    buf.append("\n");
                }
                break;
                case OpcodesHelper.INSN_UNUSED:
                {
                    // print the instruction
                    buf.append(OpcodesHelper.insnName(opcode));
                    buf.append("!!!\n");
                }
                break;
            }
            bcpos = nextBCPos(opcode, i, bcpos);
        }
        // print the active starts for this block
        if (activeTryStarts != null) {
            Iterator<TryCatchDetails> activeStartsIter = activeTryStarts.iterator();
            buf.append("active try starts:\n");
            while (activeStartsIter.hasNext()) {
                TryCatchDetails details = activeStartsIter.next();
                buf.append("  try: ");
                buf.append(details.getType());
                buf.append(" ");
                Label label = details.getStart();
                printLabelOffset(buf, label, true, label);
                buf.append(" catch: ");
                label = details.getEnd();
                printLabelOffset(buf, label, true, label);
                buf.append(" handle: ");
                label = details.getHandler();
                printLabelOffset(buf, label, true, label);
                buf.append("\n");
            }
        }
    }
    private int nextBCPos(int opcode, int insnIdx, int currentPos)
    {
        int size = OpcodesHelper.insnsSize(opcode);
        if (size < 0) {
            // normal (non-wide) size is the negative of the returned value
            size = -size;
            // may be a wide opcode or a table/lookupswitch
            switch (opcode) {
                case Opcodes.LOOKUPSWITCH:
                {
                    // LOOKUPSWITCH has 0..3 padding bytes, off_def, N, key_1, off_1, ... key_N, off_N
                    // add one byte for bytecode
                    currentPos += 1;
                    // round up to multiple of 4
                    currentPos = ((currentPos + 3) / 4) * 4;
                    // count fixed operand bytes
                    currentPos += 8;
                    // count variable operand bytes
                    int count = this.getInstructionArg(insnIdx, 0);
                    currentPos += count * 8;
                }
                break;
                case Opcodes.TABLESWITCH:
                {
                    // TABLESWITCH has 0..3 padding bytes, off_def, lo, hi, off_1, ... off_N where N = ((hi + 1) - lo)
                    // add one byte for bytecode
                    currentPos += 1;
                    // round up to multiple of 4
                    currentPos = ((currentPos + 3) / 4) * 4;
                    // count fixed operand bytes
                    currentPos += 12;
                    // count variable operand bytes
                    int hi = this.getInstructionArg(insnIdx, 0);
                    int lo = this.getInstructionArg(insnIdx, 1);
                    int count = (hi + 1 - lo);
                    currentPos += count * 4;
                }
                break;
                default:
                {
                    // most wide operands just need an extra wide byte and an extra operand byte
                    // but we have a few special cases
                    if (opcode == Opcodes.IINC) {
                        // wide iinc needs wide byte plus two extra operand bytes
                        int slot = this.getInstructionArg(insnIdx, 0);
                        int value = this.getInstructionArg(insnIdx, 1);
                        if(slot > 255 || value > 127 || value < -128) {
                            // wide format add one byte for preceding wide, one for operand and four operand bytes
                            currentPos += 6;
                        } else {
                            // add one byte for operand and two operand bytes
                            currentPos += 3;
                        }
                    } else if (opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD ||
                            opcode >= Opcodes.ISTORE && opcode <= Opcodes.ASTORE) {
                        // we can see xload when the real opcode is xload_<n>
                        int slot = this.getInstructionArg(insnIdx, 0);
                        if(slot < 4) {
                            // add one byte for operand
                            currentPos += 1;
                        } else if(slot > 255) {
                            // wide format add one byte for preceding wide, one for operand and two operand bytes
                            currentPos += 4;
                        } else {
                            // add one byte for operand and one operand bytes
                            currentPos += 2;
                        }
                    } else if (opcode == Opcodes.LDC) {
                        int offset = this.getInstructionArg(insnIdx, 0);
                        if (offset > 255) {
                            // wide format add one byte for operand and two operand bytes
                            currentPos += 3;
                        } else {
                            // add one byte for operand and one operand bytes
                            currentPos += 2;
                        }
                    } else {
                        // add one byte for preceding wide and one extra operand byte
                        currentPos += (size + 2);
                    }
                }
                break;
            }
        } else {
            currentPos += size;
        }
        return currentPos;
    }
}
