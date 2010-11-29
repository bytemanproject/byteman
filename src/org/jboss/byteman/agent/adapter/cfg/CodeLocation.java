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

/**
 * a reference to a specific instruction location in a given BBlock
 */
public class CodeLocation implements Comparable<CodeLocation>
{
    /**
     * the basic block containing the instruction
     */
    private BBlock block;
    /**
     * the index of the instruction in the basic block's instruction sequence
     */
    private int instructionIdx;

    public CodeLocation(BBlock block, int instructionIdx)
    {
        this.block = block;
        this.instructionIdx = instructionIdx;
    }

    public BBlock getBlock()
    {
        return block;
    }

    public int getBlockIdx()
    {
        return block.getBlockIdx();
    }

    public int getInstructionIdx()
    {
        return instructionIdx;
    }

    public String toString()
    {
        return "BB" + getBlockIdx() + "." + getInstructionIdx();
    }

    public int compareTo(CodeLocation loc)
    {
        int blockIdx = getBlockIdx();
        int otherBlockIdx = loc.getBlockIdx();
        if (blockIdx < otherBlockIdx) {
            return -1;
        } else if (blockIdx > otherBlockIdx) {
            return 1;
        } else {
            if (instructionIdx < loc.instructionIdx) {
                return -1;
            } else if (instructionIdx > loc.instructionIdx) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean equals(Object o)
    {
        if (o instanceof CodeLocation) {
            return (compareTo((CodeLocation) o) == 0);
        } else {
            return false;
        }
    }

    public int hashCode()
    {
        return (block.getBlockIdx() << 16) ^ instructionIdx; 
    }
}
