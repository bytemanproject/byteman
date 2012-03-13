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

import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.*;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class EntryTriggerAdapter extends RuleTriggerAdapter
{
    public EntryTriggerAdapter(ClassVisitor cv, TransformContext transformContext)
    {
        super(cv, transformContext);
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
                return new EntryTriggerConstructorAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            } else {
                return new EntryTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class EntryTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        protected boolean unlatched;
        /**
         * flag which says whether a trigger has been injected into this method
         */
        private boolean visited;

        EntryTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            this.unlatched = true;  // subclass can manipulate this to postponne visit
            visited = false;
        }

        // if possible inject a trigger as soon as we visit the code. This will precede any visit to labels defined
        // by the original program -- in particular the loop label for a while loop occuring at the start of the
        // method body. if we don't do this then we can end up injecting the trigger into the body of the while
        // loop

        @Override
        public void visitCode()
        {
            // call the super method first so we have a valid CFG and start label

            super.visitCode();

            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
        }

        // we will not be able to inject into a constructor at visitCode because we need to delay until
        // we have run the super constructor. so we also need to override each visitXXXINsn operation
        // and inject a trigger point as soon as possible after the constructor unlatches the adapter.
        
        @Override
        public void visitInsn(int opcode) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitIincInsn(var, increment);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitIntInsn(opcode, operand);
        }

        @Override
        public void visitLdcInsn(Object cst) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitLdcInsn(cst);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitTypeInsn(int opcode, String desc) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitTypeInsn(opcode, desc);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitTableSwitchInsn(min, max, dflt, labels);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            if (unlatched && !visited) {
                visited = true;
                injectTriggerPoint();
            }
            super.visitLookupSwitchInsn(dflt, keys, labels);
        }

        @Override
        public void visitMultiANewArrayInsn(String desc, int dims) {
            if (unlatched && !visited) {
                visited = true;
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
        EntryTriggerConstructorAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, getTransformContext(), access, name, descriptor, signature, exceptions);
            this.unlatched = false;
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            super.visitMethodInsn(opcode, owner, name, desc);
            unlatched |= isSuperOrSiblingConstructorCall(opcode, owner, name);
        }
    }
}