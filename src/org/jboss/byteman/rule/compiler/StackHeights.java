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
 */package org.jboss.byteman.rule.compiler;

import org.jboss.byteman.rule.exception.CompileException;

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
    public int localCount;

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
