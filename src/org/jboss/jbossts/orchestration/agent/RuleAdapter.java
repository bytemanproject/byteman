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
package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class RuleAdapter extends ClassAdapter
{
    RuleAdapter(ClassVisitor cv, Rule rule, String targetClass, String handlerMethod, int handlerLine)
    {
        super(cv);
        this.rule = rule;
        this.targetClass = targetClass;
        this.targetMethod = TypeHelper.parseMethodName(handlerMethod);
        this.targetDescriptor = TypeHelper.parseMethodDescriptor(handlerMethod);
        this.targetLine = handlerLine;
        this.visitedLine = false;
    }

    void visitClass(
            final int version,
            final int access,
            final String name,
            final String signature,
            final String superName,
            final String[] interfaces)
    {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public FieldVisitor visitField(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final Object value)
    {
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        return fv;
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (targetMethod.equals(name)) {
            if (targetDescriptor.equals("") || TypeHelper.equalDescriptors(targetDescriptor, desc))
            {
                if (name == "<init>") {
                    return new RuleConstructorAdapter(mv, access, name, desc, signature, exceptions);
                } else {
                    return new RuleMethodAdapter(mv, access, name, desc, signature, exceptions);
                }
            }
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class RuleMethodAdapter extends GeneratorAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private Label startLabel;
        private Label endLabel;

        RuleMethodAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            startLabel = null;
            endLabel = null;
        }

        // somewhere we need to add a catch exception block
        // super.catchException(startLabel, endLabel, new Type("org.jboss.jbossts.orchestration.rule.exception.ExecuteException")));

        public void visitLineNumber(final int line, final Label start) {
            if (!visitedLine && (targetLine <= line)) {
                rule.setTypeInfo(targetClass, access, name, descriptor, exceptions);
                String key = rule.getKey();
                Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.Rule"));
                Method method = Method.getMethod("void execute(String, Object, Object[])");
                // we are at the relevant line in the method -- so add a trigger call here
                System.out.println("RuleMethodAdapter.visitLineNumber : inserting trigger for " + rule.getName());
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
                visitedLine = true;
            }
            super.visitLineNumber(line, start);
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

    private class RuleConstructorAdapter extends RuleMethodAdapter
    {
        private boolean superCalled;

        RuleConstructorAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, access, name, descriptor, signature, exceptions);
            this.superCalled = false;
        }

        // don't pass on line visits until we have seen an INVOKESPECIAL
        public void visitLineNumber(final int line, final Label start) {
            if (superCalled) {
                super.visitLineNumber(line, start);
            }
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            super.visitMethodInsn(opcode, owner, name, desc);
            // hmm, this probably means the super constructor has been invoked :-)
            superCalled &= (opcode == Opcodes.INVOKESPECIAL);
        }
    }

    private Rule rule;
    private String targetClass;
    private String targetMethod;
    private String targetDescriptor;
    private int targetLine;
    private String fieldName;
    private boolean visitedLine;
}
