package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
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
                return new RuleMethodAdapter(mv, access, name, desc, signature, exceptions);
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
            if (!visitedLine && (targetLine < 0 || targetLine == line)) {
                rule.setTypeInfo(targetClass, access, name, descriptor);
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
            Type exceptionType = Type.getType(TypeHelper.externalizeType("org.jboss.jbossts.orchestration.rule.exception.ExecuteException"));
            super.catchException(startLabel, endLabel, exceptionType);
            super.throwException(exceptionType, rule.getName() + " execution exception ");
            super.visitEnd();
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
