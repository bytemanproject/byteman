package org.jboss.jbossts.orchestration.agent.adapter;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.agent.Transformer;

import java.util.Vector;

/**
 * asm Adapter class used to add a rule event trigger call to a method of some given class
 */
public class ExitTriggerAdapter extends RuleTriggerAdapter
{
    /**
     * table used to track which returns have been added because of exception handling code
     */

    private Vector<Label> earlyReturnHandlers;

    public ExitTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod) {
        super(cv, rule, targetClass, targetMethod);
        this.earlyReturnHandlers = new Vector<Label>();
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
            return new ExitTriggerMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class ExitTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private Vector<Label> startLabels;
        private Vector<Label> endLabels;

        ExitTriggerMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            startLabels = new Vector<Label>();
            endLabels = new Vector<Label>();
        }

        /**
         * Visits a try catch block and records the label of the handler start if the
         * exception type EarlyReturnException so we can later avoid inserting a rule
         * trigger.
         *
         * @param start beginning of the exception handler's scope (inclusive).
         * @param end end of the exception handler's scope (exclusive).
         * @param handler beginning of the exception handler's code.
         * @param type internal name of the type of exceptions handled by the
         *        handler, or <tt>null</tt> to catch any exceptions (for "finally"
         *        blocks).
         * @throws IllegalArgumentException if one of the labels has already been
         *         visited by this visitor (by the {@link #visitLabel visitLabel}
         *         method).
         */
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type)
        {
            // check whether type is one of ours and if so add the labels to the
            // return table

            if (type.equals("org/jboss/jbossts/orchestration/rule/exception/EarlyReturnException")) {
                earlyReturnHandlers.add(handler);
            }
            super.visitTryCatchBlock(start, end, handler, type);
        }

        /**
         * each time we visit a label we set or clear flag inhibit depending upon whether the label
         * identifies an EarlyReturnException block or not in order to avoid inserting triggers
         * for returns added by our own exception handling code
         *
         * @param label
         */
        public void visitLabel(Label label)
        {
            if (earlyReturnHandlers.contains(label)) {
                inhibit = true;
            } else {
                inhibit = false;
            }

            super.visitLabel(label);
        }

        /**
         * we need to identify return instructions which are inserted because of other rules
         *
         * @param opcode
         */
        public void visitInsn(final int opcode) {
            switch (opcode) {
                case Opcodes.RETURN: // empty stack
                case Opcodes.IRETURN: // 1 before n/a after
                case Opcodes.FRETURN: // 1 before n/a after
                case Opcodes.ARETURN: // 1 before n/a after
                case Opcodes.LRETURN: // 2 before n/a after
                case Opcodes.DRETURN: // 2 before n/a after
                {
                    if (!inhibit) {
                        // ok to insert a rule trigger as this is not one of our inserted return instructions
                        rule.setTypeInfo(targetClass, access, name, descriptor, exceptions);
                        String key = rule.getKey();
                        Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.Rule"));
                        Method method = Method.getMethod("void execute(String, Object, Object[])");
                        // we are at the relevant line in the method -- so add a trigger call here
                        if (Transformer.isVerbose()) {
                            System.out.println("ExitTriggerMethodAdapter.visitInsn : inserting trigger for " + rule.getName());
                        }
                        Label startLabel = super.newLabel();
                        Label endLabel = super.newLabel();
                        startLabels.add(startLabel);
                        endLabels.add(endLabel);
                        super.visitLabel(startLabel);
                        super.push(key);
                        if ((access & Opcodes.ACC_STATIC) == 0) {
                            super.loadThis();
                        } else {
                            super.push((Type)null);
                        }
                        doArgLoad();
                        super.invokeStatic(ruleType, method);
                        super.visitLabel(endLabel);
                    }
                }
                break;
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
             *
            Type exceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.ExecuteException"));
            Type earlyReturnExceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.EarlyReturnException"));
            Type returnType =  Type.getReturnType(descriptor);
            int count = startLabels.size();
            int idx;
            for (idx = 0; idx < count; idx++) {
                Label startLabel = startLabels.get(idx);
                Label endLabel = endLabels.get(idx);
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
            }
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
            int count = startLabels.size();
            int idx;
            for (idx = 0; idx < count; idx++) {
                Label startLabel = startLabels.get(idx);
                Label endLabel = endLabels.get(idx);
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
            }
            // ok now recompute the stack size
            super.visitMaxs(maxStack, maxLocals);
        }

        private boolean inhibit;
    }
}
