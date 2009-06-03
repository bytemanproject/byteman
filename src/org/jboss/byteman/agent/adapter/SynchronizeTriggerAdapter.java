/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.agent.Transformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class SynchronizeTriggerAdapter extends RuleTriggerAdapter
{
    public SynchronizeTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod, int count, boolean whenComplete)
    {
        super(cv, rule, targetClass, targetMethod);
        this.calledClass = calledClass;
        this.calledMethodName = calledMethodName;
        this.calledMethodDescriptor = calledMethodDescriptor;
        this.count = count;
        this.whenComplete = whenComplete;
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
        // we can use the same adapter for methods and constructors
        if (matchTargetMethod(name, desc)) {
            return new SynchronizeTriggerMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class SynchronizeTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private Label startLabel;
        private Label endLabel;

        SynchronizeTriggerMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            startLabel = null;
            endLabel = null;
            visitedCount = 0;
        }

        // somewhere we need to add a catch exception block
        // super.catchException(startLabel, endLabel, new Type("org.jboss.byteman.rule.exception.ExecuteException")));

        @Override
        public void visitInsn(int opcode) {
            if (whenComplete) {
                super.visitInsn(opcode);
            }
            if (opcode == Opcodes.MONITORENTER && visitedCount < count) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (visitedCount == count) {
                    rule.setTypeInfo(targetClass, access, name, descriptor, exceptions);
                    String key = rule.getKey();
                    Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.byteman.rule.Rule"));
                    Method method = Method.getMethod("void execute(String, Object, Object[])");
                    // we are at the relevant line in the method -- so add a trigger call here
                    if (Transformer.isVerbose()) {
                        System.out.println("SynchronizeTriggerMethodAdapter.visitMethodInsn : inserting trigger for " + rule.getName());
                    }
                    startLabel = newLabel();
                    endLabel = newLabel();
                    visitTriggerStart(startLabel);
                    push(key);
                    if ((access & Opcodes.ACC_STATIC) == 0) {
                        loadThis();
                    } else {
                        push((Type)null);
                    }
                    doArgLoad();
                    invokeStatic(ruleType, method);
                    visitTriggerEnd(endLabel);
                }
            }
            if (!whenComplete) {
                super.visitInsn(opcode);
            }
        }
    }

    private String calledClass;
    private String calledMethodName;
    private String calledMethodDescriptor;
    private int count;
    private boolean whenComplete;

    private int visitedCount;
}