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
import org.jboss.jbossts.orchestration.agent.Transformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * asm Adapter class used to add a rule event trigger call to a method of some given class
 */
public class ThrowTriggerAdapter extends RuleTriggerAdapter
{
    public ThrowTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod, String exceptionClass, int count)
    {
        super(cv, rule, targetClass, targetMethod);
        this.exceptionClass = exceptionClass;
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
            if (name.equals("<init>")) {
                return new ThrowTriggerConstructorAdapter(mv, access, name, desc, signature, exceptions);
            } else {
                return new ThrowTriggerMethodAdapter(mv, access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class ThrowTriggerMethodAdapter extends GeneratorAdapter
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

        ThrowTriggerMethodAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
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

        public void visitInsn(final int opcode) {
            if (opcode == Opcodes.ATHROW) {
                // ok, we have hit a throw -- for now we just count any throw
                // later we will try to match the exception class
                if (visitedCount < count) {
                    // a relevant invocation occurs in the called method
                    visitedCount++;
                    if (!latched && visitedCount == count) {
                        rule.setTypeInfo(targetClass, access, name, descriptor, exceptions);
                        String key = rule.getKey();
                        Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.Rule"));
                        Method method = Method.getMethod("void execute(String, Object, Object[])");
                        // we are at the relevant line in the method -- so add a trigger call here
                        if (Transformer.isVerbose()) {
                            System.out.println("ThrowTriggerMethodAdapter.visitInsn : inserting trigger for " + rule.getName());
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
            }
            
            super.visitInsn(opcode);
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

    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class ThrowTriggerConstructorAdapter extends ThrowTriggerMethodAdapter
    {
        ThrowTriggerConstructorAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
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

    private String exceptionClass;
    private int count;
    private int visitedCount;
}