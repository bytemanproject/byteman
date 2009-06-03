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

    public int getInstructionIdx()
    {
        return instructionIdx;
    }

    public String toString()
    {
        return "B" + block.getBlockIdx() + "." + getInstructionIdx();
    }

    public int compareTo(CodeLocation loc)
    {
        int blockIdx = block.getBlockIdx();
        int otherBlockIdx = loc.block.getBlockIdx();
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
}
