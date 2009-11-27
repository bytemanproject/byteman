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

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.agent.Transformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class InvokeTriggerAdapter extends RuleTriggerAdapter
{
    public InvokeTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod, String calledClass,
                       String calledMethodName, String calledMethodDescriptor, int count, boolean whenComplete)
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
        if (matchTargetMethod(name, desc)) {
            if (name.equals("<init>")) {
                return new InvokeTriggerConstructorAdapter(mv, rule, access, name, desc, signature, exceptions);
            } else {
                return new InvokeTriggerMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class InvokeTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        /**
         * flag used by subclass to avoid inserting trigger until after super constructor has been called
         */
        protected boolean latched;

        InvokeTriggerMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, targetClass, access, name, descriptor, signature, exceptions);
            visitedCount = 0;
            latched = false;
        }

        // somewhere we need to add a catch exception block
        // super.catchException(startLabel, endLabel, new Type("org.jboss.byteman.rule.exception.ExecuteException")));

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            if (whenComplete) {
                // invoke the method before generating the trigger call
                super.visitMethodInsn(opcode, owner, name, desc);
            }
            if (visitedCount < count && matchCall(owner, name, desc)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (!latched && visitedCount == count) {
                    injectTriggerPoint();
                }
            }
            if (!whenComplete) {
                // invoke the method before generating the trigger call
                super.visitMethodInsn(opcode, owner, name, desc);
            }
        }

        private boolean matchCall(String owner, String name, String desc)
        {
            if (!calledMethodName.equals(name)) {
                return false;
            }
            if (calledClass != null) {
                if (!calledClass.equals(TypeHelper.internalizeClass(owner))) {
                    // TODO check for unqualified names
                    // if the called class has no package qualification and the owner class does
                    // then we can still match if the unqualified owner name equals the called class
                    if (calledClass.indexOf('.') >= 0) {
                        return false;
                    }
                    int ownerPackageIdx = owner.lastIndexOf('/');
                    if (ownerPackageIdx < 0) {
                        return false;
                    } else if (!owner.substring(ownerPackageIdx+1).equals(calledClass)) {
                        return false;
                    }
                }
            }
            if (calledMethodDescriptor.length() > 0) {
                if (!TypeHelper.equalDescriptors(calledMethodDescriptor, desc)) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class InvokeTriggerConstructorAdapter extends InvokeTriggerMethodAdapter
    {
        InvokeTriggerConstructorAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor, signature, exceptions);
            // ensure we don't transform calls before the super constructor is called
            latched = true;
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            super.visitMethodInsn(opcode, owner, name, desc);
            // hmm, this probably means the super constructor has been invoked :-)
            if (latched && opcode == Opcodes.INVOKESPECIAL) {
                latched = false;
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