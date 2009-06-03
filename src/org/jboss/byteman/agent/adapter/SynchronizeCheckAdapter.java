/*
* JBoss, Home of Professional Open Source
* Copyright 2008-9, Red Hat Middleware LLC, and individual contributors
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

import org.objectweb.asm.*;
import org.jboss.byteman.rule.Rule;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class SynchronizeCheckAdapter extends RuleCheckAdapter
{
     public SynchronizeCheckAdapter(ClassVisitor cv, Rule rule,  String targetClass, String targetMethod, int count)
    {
        super(cv, rule, targetClass, targetMethod);
        this.count = count;
        this.visitedCount = 0;
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (matchTargetMethod(name, desc)) {
            return new SynchronizeCheckMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class SynchronizeCheckMethodAdapter extends RuleCheckMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private boolean visited;

        SynchronizeCheckMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            visitedCount = 0;
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.MONITORENTER && visitedCount < count) {
                // a synchronized block occurs in the called method
                visitedCount++;
                if (visitedCount == count) {
                    // and we have enough occurences to match the count
                    setTriggerPoint();
                }
            }
            super.visitInsn(opcode);
        }

        public void visitEnd()
        {
            if (checkBindings()) {
                setVisitOk();
            }
            super.visitEnd();
        }
   }

    private int count;
    private int visitedCount;
}