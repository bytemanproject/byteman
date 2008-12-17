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
package org.jboss.jbossts.orchestration.agent.adapter;

import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.agent.Location;
import org.jboss.jbossts.orchestration.agent.Transformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class AccessTriggerAdapter extends RuleTriggerAdapter
{
    public AccessTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod, String ownerClass,
                       String fieldName, int flags, int count, boolean whenComplete)
    {
        super(cv, rule, targetClass, targetMethod);
        this.ownerClass = ownerClass;
        this.fieldName = fieldName;
        this.flags = flags;
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
                return new AccessTriggerConstructorAdapter(mv, access, name, desc, signature, exceptions);
            } else {
                return new AccessTriggerMethodAdapter(mv, access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class AccessTriggerMethodAdapter extends GeneratorAdapter
    {
        /**
         * flag used by subclass to avoid inserting trigger until after super constructor has been called
         */
        protected boolean latched;
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private Label startLabel;
        private Label endLabel;

        AccessTriggerMethodAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            startLabel = null;
            endLabel = null;
            visitedCount = 0;
            latched = false;
        }

        // somewhere we need to add a catch exception block
        // super.catchException(startLabel, endLabel, new Type("org.jboss.jbossts.orchestration.rule.exception.ExecuteException")));

        public void visitFieldInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            if (whenComplete) {
                // access the field before generating the trigger call
                super.visitFieldInsn(opcode, owner, name, desc);
            }
            if (visitedCount < count && matchCall(opcode, owner, name, desc)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (!latched && visitedCount == count) {
                    rule.setTypeInfo(targetClass, access, name, descriptor, exceptions);
                    String key = rule.getKey();
                    Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.Rule"));
                    Method method = Method.getMethod("void execute(String, Object, Object[])");
                    // we are at the relevant line in the method -- so add a trigger call here
                    if (Transformer.isVerbose()) {
                        System.out.println("AccessTriggerMethodAdapter.visitFieldInsn : inserting trigger for " + rule.getName());
                    }
                    startLabel = super.newLabel();
                    endLabel = super.newLabel();
                    super.visitLabel(startLabel);
                    super.push(key);
                    if ((access & Opcodes.ACC_STATIC) == 0) {
                        super.loadThis();
                    } else {
                        super.push((Type)null);
                    }
                    super.loadArgArray();
                    super.invokeStatic(ruleType, method);
                    super.visitLabel(endLabel);
                }
            }
            if (!whenComplete) {
                // access the field after generating the trigger call
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }

        public void visitEnd()
        {
            /*
             * unfortunately, if we generate the handler code here it comes too late for the stack size
             * computation to take account of it. the handler code uses 4 stack slots so this causes and
             * error when the method body uses less than 4. we can patch this by generating the handler
             * code when visitMaxs is called
            Type exceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.ExecuteException"));
            Type earlyReturnExceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.EarlyReturnException"));
            Type returnType =  Type.getReturnType(descriptor);
            // add exception handling code subclass first
            super.catchException(startLabel, endLabel, earlyReturnExceptionType);
            if (returnType == Type.VOID_TYPE) {
                // drop exception and just return
                super.pop();
                super.visitInsn(Opcodes.RETURN);
            } else {
                // fetch value from exception, unbox if needed and return value
                Method getReturnValueMethod = Method.getMethod("Object getReturnValue()");
                super.invokeVirtual(earlyReturnExceptionType, getReturnValueMethod);
                super.unbox(returnType);
                super.returnValue();
            }
            super.catchException(startLabel, endLabel, exceptionType);
            super.throwException(exceptionType, rule.getName() + " execution exception ");
            */
            super.visitEnd();
        }

        public void visitMaxs(int maxStack, int maxLocals) {
            /*
             * this really ought to be in visitEnd but see above for why we do it here
             */
            Type exceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.ExecuteException"));
            Type earlyReturnExceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.EarlyReturnException"));
            Type throwExceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.ThrowException"));
            Type throwableType = Type.getType(TypeHelper.externalizeType("java.lang.Throwable"));
            Type returnType =  Type.getReturnType(descriptor);
            // add exception handling code subclass first
            super.catchException(startLabel, endLabel, earlyReturnExceptionType);
            if (returnType == Type.VOID_TYPE) {
                // drop exception and just return
                super.pop();
                super.visitInsn(Opcodes.RETURN);
            } else {
                // fetch value from exception, unbox if needed and return value
                Method getReturnValueMethod = Method.getMethod("Object getReturnValue()");
                super.invokeVirtual(earlyReturnExceptionType, getReturnValueMethod);
                super.unbox(returnType);
                super.returnValue();
            }
            super.catchException(startLabel, endLabel, throwExceptionType);
            // fetch value from exception, unbox if needed and return value
            Method getThrowableMethod = Method.getMethod("Throwable getThrowable()");
            super.invokeVirtual(throwExceptionType, getThrowableMethod);
            super.throwException();

            super.catchException(startLabel, endLabel, exceptionType);
            super.throwException(exceptionType, rule.getName() + " execution exception ");
            // ok now recompute the stack size
            super.visitMaxs(maxStack, maxLocals);
        }

        private boolean matchCall(int opcode, String owner, String name, String desc)
        {
            if (!fieldName.equals(name)) {
                return false;
            }

            switch (opcode) {
                case Opcodes.GETSTATIC:
                case Opcodes.GETFIELD:
                {
                    if ((flags & Location.ACCESS_READ) == 0) {
                        return false;
                    }
                }
                break;
                case Opcodes.PUTSTATIC:
                case Opcodes.PUTFIELD:
                {
                    if ((flags & Location.ACCESS_WRITE) == 0) {
                        return false;
                    }
                }
                break;
            }
            if (ownerClass != null) {
                if (!ownerClass.equals(owner)) {
                    // TODO check for unqualified names
                    return false;
                }
            }
            // TODO work out how to use desc???
            return true;
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class AccessTriggerConstructorAdapter extends AccessTriggerMethodAdapter
    {
        AccessTriggerConstructorAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, access, name, descriptor, signature, exceptions);
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

    private String ownerClass;
    private String fieldName;
    private int flags;
    private int count;
    private boolean whenComplete;
    private int visitedCount;
}