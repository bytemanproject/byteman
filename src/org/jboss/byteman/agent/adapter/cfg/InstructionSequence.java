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

import org.jboss.byteman.agent.adapter.OpcodesHelper;

/**
 * Class used to hold a sequence of instructions within a basic block
 */
public class InstructionSequence
{
    /**
     * since instructions are encoded with their operands we need an offsets array to identify
     * where each instruction strats, allowing instructions and their operand to be searched
     * forwards and backwards
     */
    private int[] instructionOffsets;

    /**
     * the number of valid offsets to instructions in array instructionOffsets
     */

    private int numInstructions;

    /**
     * data array storing instructions and their operands encoded as ints.
     *
     * integer operands are embedded as is
     * operands which are strings (type, const, field etc)  must be translated via the names array in
     * the associated cfg.
     * operands which are jump labels must be translated by calling getNthOut() on the associated
     * basic block.
     */
    private int[] encodedInstructions;

    /**
     * the number of valid entries in array encodedInstructions 
     */

    private int numEncoded;

    /**
     * expand the offsets array if necessary to allow room for 1 more instructions with count more arguments
     */
    private void ensureSpace(int count)
    {
        int length = instructionOffsets.length;
        if (numInstructions == length) {
            int[] newOffsets = new int[length * 2];
            for (int i = 0; i < numInstructions; i++) {
                newOffsets[i] = instructionOffsets[i];
            }
            instructionOffsets = newOffsets;
        }
        // we need room for the instruction and count args
        length = encodedInstructions.length;
        if (numEncoded + count + 1 >= length) {
            int[] newEncoded = new int[length * 2];
            for (int i = 0; i < numEncoded; i++) {
                newEncoded[i] = encodedInstructions[i];
            }
            encodedInstructions = newEncoded;
        }
    }

    public InstructionSequence()
    {
        instructionOffsets = new int[3];
        encodedInstructions = new int[6];
    }

    /**
     * return the number of instructions in the sequence
     *
     * @return the the number of instructions in the sequence
     */
    public int size()
    {
        return numInstructions;
    }

    /**
     * return the instruction at the supplied offset
     *
     * @return the ith instruction in the sequuence
     */
    public int get(int i)
    {
        int offset = instructionOffsets[i];
        return encodedInstructions[offset];
    }

    /**
     * return the type of a given instruction
     *
     * @return the ith instruction in the sequuence
     */
    public int getType(int i)
    {
        int offset = instructionOffsets[i];
        int insn = encodedInstructions[offset];
        return OpcodesHelper.insnType(insn);
    }

    /**
     * return the number of encoded arguments of a given instruction
     * @param i the offset of the instruction
     *
     * @return the number of encoded arguments of the ith instruction in the sequuence
     */
    public int getArgCount(int i)
    {
        int offset = instructionOffsets[i];
        int nextOffset;
        if (offset == numInstructions - 1) {
            nextOffset = numEncoded;
        } else {
            nextOffset = instructionOffsets[i + 1];
        }

        return (nextOffset - (offset + 1));
    }

    /**
     * return a specific encoded argument of a given instruction
     * @param i the offset of the instruction
     * @param j the index of the arguument attached to the instruction
     *
     * @return the jth encoded argument of the ith instruction in the sequuence
     */
    public int getArg(int i, int j)
    {
        int offset = instructionOffsets[i];
        return encodedInstructions[offset + j + 1];
    }

    /**
     * add an instruction to the sequence
     * @param insn
     * @return the index of the newly added instruction
     */
    public int add(int insn)
    {
        int result = numInstructions;
        ensureSpace(0);
        instructionOffsets[numInstructions++] = numEncoded;
        encodedInstructions[numEncoded++] = insn;
        return result;
    }

    /**
     * add an instruction with one encoded argument to the sequence
     * @param insn
     * @return the index of the newly added instruction
     */
    public int add(int insn, int arg1)
    {
        int result = numInstructions;
        ensureSpace(1);
        instructionOffsets[numInstructions++] = numEncoded;
        encodedInstructions[numEncoded++] = insn;
        encodedInstructions[numEncoded++] = arg1;
        return result;
    }

    /**
     * add an instruction with two encoded arguments to the sequence
     * @param insn
     * @return the index of the newly added instruction
     */
    public int add(int insn, int arg1, int arg2)
    {
        int result = numInstructions;
        ensureSpace(2);
        instructionOffsets[numInstructions++] = numEncoded;
        encodedInstructions[numEncoded++] = insn;
        encodedInstructions[numEncoded++] = arg1;
        encodedInstructions[numEncoded++] = arg2;
        return result;
    }

    /**
     * add an instruction with three encoded arguments to the sequence
     * @param insn
     * @return the index of the newly added instruction
     */
    public int add(int insn, int arg1, int arg2, int arg3)
    {
        int result = numInstructions;
        ensureSpace(3);
        instructionOffsets[numInstructions++] = numEncoded;
        encodedInstructions[numEncoded++] = insn;
        encodedInstructions[numEncoded++] = arg1;
        encodedInstructions[numEncoded++] = arg2;
        encodedInstructions[numEncoded++] = arg3;
        return result;
    }

    /**
     * add an instruction with an arbitrary number of encoded arguments to the sequence
     * @param insn
     * @return the index of the newly added instruction
     */
    public int add(int insn, int[] args)
    {
        int result = numInstructions;
        ensureSpace(args.length);
        instructionOffsets[numInstructions++] = numEncoded;
        encodedInstructions[numEncoded++] = insn;
        for (int i =  0; i < args.length; i++) {
            encodedInstructions[numEncoded++] = args[i];
        }
        return result;
    }
}
