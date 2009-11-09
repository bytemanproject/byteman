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
public class EntryTriggerAdapter extends RuleTriggerAdapter
{
    public EntryTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod)
    {
        super(cv, rule, targetClass, targetMethod);
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
                return new EntryTriggerConstructorAdapter(mv, rule, access, name, desc, signature, exceptions);
            } else {
                return new EntryTriggerMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class EntryTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private Label startLabel;
        private Label endLabel;
        protected boolean unlatched;
        /**
         * flag which says whether a trigger has been injected into this method
         */
        private boolean visited;

        EntryTriggerMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            this.unlatched = true;  // subclass can manipulate this to postponne visit
            startLabel = null;
            endLabel = null;
            visited = false;
        }

        /**
         * inject the rule trigger code
         */
        private void injectTriggerPoint()
        {
            // we need to set this here to avoid recursive re-entry into inject routine

            visited = true;
            rule.setTypeInfo(targetClass, access, name, descriptor, exceptions);
            String key = rule.getKey();
            Type ruleType = Type.getType(TypeHelper.externalizeType("org.jboss.byteman.rule.Rule"));
            Method method = Method.getMethod("void execute(String, Object, Object[])");
            // we are at the relevant line in the method -- so add a trigger call here
            if (Transformer.isVerbose()) {
                System.out.println("EntryTriggerMethodAdapter.injectTriggerPoint : inserting trigger for " + rule.getName());
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

        // we need to override each visitXXXINsn operation so we see each instruction being generated. we
        // inject a trigger point as soon as possible.
        
        @Override
        public void visitInsn(int opcode) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitIincInsn(var, increment);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitLdcInsn(cst);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitTypeInsn(int opcode, String desc) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitTypeInsn(opcode, desc);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMultiANewArrayInsn(String desc, int dims) {
            if (unlatched && !visited) {
                injectTriggerPoint();
            }
            super.visitMultiANewArrayInsn(desc, dims);
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class EntryTriggerConstructorAdapter extends EntryTriggerMethodAdapter
    {
        EntryTriggerConstructorAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor, signature, exceptions);
            this.unlatched = false;
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            super.visitMethodInsn(opcode, owner, name, desc);
            // hmm, this probably means the super constructor has been invoked :-)
            unlatched |= (opcode == Opcodes.INVOKESPECIAL);
        }
    }
}