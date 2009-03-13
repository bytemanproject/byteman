package org.jboss.jbossts.orchestration.rule.compiler;

import org.jboss.jbossts.orchestration.rule.exception.CompileException;

/**
 * container to track either the current or the maximum local variable count and stack height
 * while generating compiled code for a rule
 */
public class StackHeights
{
    /**
     * number of stack slots
     */
    public int stackCount;
    /**
     * number of local variable slots
     */
    int localCount;

    /**
     * create withinitial counts 0
     */
    public StackHeights()
    {
        stackCount = localCount = 0;
    }

    /**
     * create a copy with the same coutns as the original
     * @param toCopy
     */
    public StackHeights(StackHeights toCopy)
    {
        stackCount = toCopy.stackCount;
        localCount = toCopy.localCount;
    }

    /**
     * increment the stack count and return this to allow chaining
     * @param increment the amount ot add to stackCount (can be negative)
     * @return this
     * @throws CompileException if the stack count goes negative
     */
    public StackHeights addStackCount(int increment) throws CompileException
    {
        stackCount += increment;
        if (stackCount < 0) {
            throw new CompileException("StackHeights.addStackCount : negative count for stack slots!");
        }
        return this;
    }

    /**
     * increment the local count and return this to allow chaining
     * @param increment the amount ot add to localCount (can be negative)
     * @return this
     * @throws CompileException if the local count goes negative
     */
    public StackHeights addLocalCount(int increment) throws CompileException
    {
        localCount += increment;
        if (localCount < 0) {
            throw new CompileException("StackHeights.addLocalCount : negative count for local variable slots!");
        }
        return this;
    }
};
