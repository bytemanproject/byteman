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

import org.jboss.byteman.agent.Location;
import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class VariableAccessTriggerAdapter extends RuleTriggerAdapter
{
    public VariableAccessTriggerAdapter(ClassVisitor cv, TransformContext transformContext,
                                String varName, int flags, int count, boolean whenComplete)
    {
        super(cv, transformContext);
        this.varName = varName;
        this.flags = flags;
        this.count = count;
        this.whenComplete = whenComplete;
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
                return new VariableAccessTriggerConstructorAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            } else {
                return new VariableAccessTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class VariableAccessTriggerMethodAdapter extends RuleTriggerMethodAdapter implements LocalScopeMethodVisitor
    {
        /**
         * flag used by subclass to avoid inserting trigger until after super constructor has been called
         */
        protected boolean latched;
        private int index;
        private int visitedCount;
     
        VariableAccessTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            // index -1 indicates that the var is not in scope
            this.index =  -1;
            visitedCount = 0;
            latched = false;
        }

        /**
         * checks if the local var coming into scope is the one mentioned in the rule location and if so
         * records which slot is now being used to store the variable. this is called by the BMJSRInliner
         * which feeds this adapter because this adapter implements LocalScopeMethodVisitor
         * @param name
         * @param desc
         * @param signature
         * @param stackSlot
         */
        public void visitLocalScopeStart(String name, String desc, String signature, int stackSlot, int startOffset) {
            if (name.equals(varName)) {
                // update the current var index so we track accesses correctly
                index =  stackSlot;
                // if this is truly a local variable rather than a param and the event is a local WRITE
                // then the var comes into scope just after it is initialised. so we count this point as
                // the point where an AT WRITE 1 or AFTER WRITE 1 rule should be injected. That's not
                // strictly correct for an AT WRITE 1 rule but then again we cannot expose the slot
                // before it is initialised since it is nto a valid slot until that point
                if  (startOffset > 0 && (count == 0 || (visitedCount < count)) && ((flags & Location.ACCESS_WRITE) != 0)) {
                    // a relevant invocation occurs in the called method
                    visitedCount++;
                    if (!latched && (count == 0 || visitedCount == count)) {
                        injectTriggerPoint();
                    }
                }
            }
        }

        /**
         * checks if the local var going out of scope is the one mentioned in the rule location and if so
         * records that the slot is no longer active. this is called by the BMJSRInliner
         * which feeds this adapter because this adapter implements LocalScopeMethodVisitor
         * @param name
         * @param desc
         * @param signature
         * @param stackSlot
         */
        public void visitLocalScopeEnd(String name, String desc, String signature, int stackSlot, int endOffset) {
            if (name.equals(varName)) {
                // restore the previous var index so we know which index the var is now located at
                index  =-1;
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            // this instruction can be called during trigger injection
            // if we in a trigger region then we don't want to re-enter
            // trigger injection or count this visit
            if (inBytemanTrigger()) {
                super.visitVarInsn(opcode, var);
            } else {
                if (whenComplete) {
                    super.visitVarInsn(opcode, var);
                }
                if (var == index) {
                    if ((count == 0 || (visitedCount < count)) && matchCall(opcode)) {
                        // a relevant invocation occurs in the called method
                        visitedCount++;
                        if (!latched && (count == 0 || visitedCount == count)) {
                            injectTriggerPoint();
                        }
                    }
                }
                if (!whenComplete) {
                    super.visitVarInsn(opcode, var);
                }
            }
        }

        public void visitIincInsn(int var, int increment)
        {
            // IINC counts as a read and a write but we need to regard it as a read, increment and then write back
            // so we trigger AT READ, AFTER READ and AT WRITE before the IINC and AFTER WRITE after the IINC

            boolean ruleIsAfterWrite = ((flags & Location.ACCESS_WRITE) != 0 && whenComplete);
            if (ruleIsAfterWrite) {
                // trigger comes after the IINC
                super.visitIincInsn(var, increment);
            }
            if (var == index) {
                // n.b. no need to check if we are in a Byteman trigegr as we never inject IINC into trigger regions
                if ((count == 0 || (visitedCount < count))) {
                    // a relevant invocation occurs in the called method
                    visitedCount++;
                    if (!latched && (count == 0 || visitedCount == count)) {
                        injectTriggerPoint();
                    }
                }
            }
            if (!ruleIsAfterWrite) {
                // trigger comes before the IINC
                super.visitIincInsn(var, increment);
            }
        }

        private boolean matchCall(int opcode)
        {
            if (opcode < Opcodes.ISTORE) {
                //  it's a load
                return ((flags & Location.ACCESS_READ) != 0);
            } else {
                //  it's a store
                return ((flags & Location.ACCESS_WRITE) != 0);
            }
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class VariableAccessTriggerConstructorAdapter extends VariableAccessTriggerMethodAdapter
    {
        VariableAccessTriggerConstructorAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
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
            // hmm, this probably means the super constructor has been invoked :-)
            if (latched && isSuperOrSiblingConstructorCall(opcode, owner, name)) {
                latched = false;
            }

        }
    }

    private String varName;
    private int flags;
    private int count;
    private boolean whenComplete;
}