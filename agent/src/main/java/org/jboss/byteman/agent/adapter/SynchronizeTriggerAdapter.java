/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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
package org.jboss.byteman.agent.adapter;

import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.*;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class SynchronizeTriggerAdapter extends RuleTriggerAdapter
{
    public SynchronizeTriggerAdapter(ClassVisitor cv, TransformContext transformContext, int count, boolean whenComplete)
    {
        super(cv, transformContext);
        this.count = count;
        this.whenComplete = whenComplete;
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        // we can use the same adapter for methods and constructors
        if (injectIntoMethod(name, desc)) {
            return new SynchronizeTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class SynchronizeTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        private int visitedCount;

        SynchronizeTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            visitedCount = 0;
        }

        // somewhere we need to add a catch exception block
        // super.catchException(startLabel, endLabel, new Type("org.jboss.byteman.rule.exception.ExecuteException")));

        @Override
        public void visitInsn(int opcode) {
            if (whenComplete) {
                super.visitInsn(opcode);
            }
            if (opcode == Opcodes.MONITORENTER && (count == 0 || visitedCount < count)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count == 0 || visitedCount == count) {
                    injectTriggerPoint();
                }
            }
            if (!whenComplete) {
                super.visitInsn(opcode);
            }
        }
    }

    private int count;
    private boolean whenComplete;
}