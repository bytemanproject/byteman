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

import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.*;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class InvokeTriggerAdapter extends RuleTriggerAdapter
{
    public InvokeTriggerAdapter(ClassVisitor cv, TransformContext transformContext,
                                String calledClass, String calledMethodName, String calledMethodDescriptor, int count, boolean whenComplete)
    {
        super(cv, transformContext);
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
        if (injectIntoMethod(name, desc)) {
            if (name.equals("<init>")) {
                return new InvokeTriggerConstructorAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            } else {
                return new InvokeTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
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

        InvokeTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            visitedCount = 0;
            latched = false;
        }

        @Override
        public Type[] getInvokedTypes()
        {
            Type ownerType = (matchedClass != null ? Type.getType(TypeHelper.externalizeType(matchedClass)) : null);
            Type[] argTypes = Type.getArgumentTypes(matchedMethodDescriptor);
            Type returnType = Type.getReturnType(matchedMethodDescriptor);
            int numArgs = argTypes.length;
            Type[] result = new Type[numArgs + 1];
            result[0] = ownerType;
            for (int i = 0; i < numArgs; i++) {
                result[i + 1] = argTypes[i];
            }

            return result;
        }

        /**
         * method overridden by AT INVOKE method adapter allowing the type of the $! binding to be identified.
         * this version should only get invoked for an AFTER INVOKE rule where it returns the invoked method
         * return type
         * @return the appropriate return type
         */
        public Type getReturnBindingType()
        {
            return Type.getReturnType(matchedMethodDescriptor);
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
            if ((count == 0 || visitedCount < count) && matchCall(owner, name, desc)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (!latched && (count == 0 || visitedCount == count)) {
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

            matchedClass = TypeHelper.internalizeClass(owner);
            matchedMethodName = name;
            matchedMethodDescriptor = desc;
            return true;
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class InvokeTriggerConstructorAdapter extends InvokeTriggerMethodAdapter
    {
        InvokeTriggerConstructorAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
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
            if (latched && isSuperOrSiblingConstructorCall(opcode, owner, name)) {
                latched = false;
            }

        }
    }

    private String calledClass;
    private String calledMethodName;
    private String calledMethodDescriptor;
    private String matchedClass;
    private String matchedMethodName;
    private String matchedMethodDescriptor;
    private int count;
    private boolean whenComplete;

    private int visitedCount;
}