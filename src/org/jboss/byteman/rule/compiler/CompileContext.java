/*
* JBoss, Home of Professional Open Source
* Copyright 2010 Red Hat and individual contributors
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
package org.jboss.byteman.rule.compiler;

/**
 * class which retains compiler state during recursive compilation of rule expressions to bytecode
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
