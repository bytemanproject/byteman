package org.jboss.byteman.rule.compiler;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: Jul 22, 2010
 * Time: 4:39:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompileContext
{
    private int sourceLine;
    private int javaLine;
    private int stackCount;
    private int stackMax;
    private int localCount;
    private int localMax;

    CompileContext(int initialSourceLine)
    {
        sourceLine = initialSourceLine;
        javaLine = 1;
        stackCount = stackMax = localCount = localMax = 0;
    }

    public int getSourceLine()
    {
        return sourceLine;
    }

    public int getJavaLine()
    {
        return javaLine;
    }

    public int getStackCount()
    {
        return stackCount;
    }

    public int getLocalCount()
    {
        return localCount;
    }

    public int getStackMax()
    {
        return stackMax;
    }

    public int getLocalMax()
    {
        return localMax;
    }

    public void addSourceLine(int delta)
    {
        this.sourceLine += delta;
    }

    public void addJavaLine(int delta)
    {
        this.javaLine += delta;
    }

    public void addStackCount(int count)
    {
        stackCount += count;
        if (stackCount > stackMax) {
            stackMax = stackCount;
        }
    }

    public void addLocalCount(int count)
    {
        localCount += count;
        if (localCount > localMax) {
            localMax = localCount;
        }
    }
}
