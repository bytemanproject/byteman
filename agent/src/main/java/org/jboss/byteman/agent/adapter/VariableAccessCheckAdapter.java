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
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class VariableAccessCheckAdapter extends RuleCheckAdapter
{
     public VariableAccessCheckAdapter(ClassVisitor cv, TransformContext transformContext,
                                       String varName, int flags, int count)
    {
        super(cv, transformContext);
        this.varName = varName;
        this.flags = flags;
        this.count = count;
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (matchTargetMethod(access, name, desc)) {
            setVisited();
            return new VariableAccessCheckMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    // TODO fix this to deal with names which are actually integers i.e. $1, $2 etc
    // TODO probably best to use a different adapter
    private class VariableAccessCheckMethodAdapter extends RuleCheckMethodAdapter implements LocalScopeMethodVisitor
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private int index;
        private int visitedCount;

        VariableAccessCheckMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            // index -1 indicates that the var is not in scope
            this.index =  -1;
            visitedCount = 0;
        }

        private boolean matchCall(int opcode)
        {
            if (opcode < Opcodes.ISTORE)  {
                // it's a load
                return ((flags & Location.ACCESS_READ) != 0);
            } else {
                //it's a store
                return ((flags & Location.ACCESS_WRITE) != 0);
            }
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
                index =  stackSlot;
                // if this is truly a local variable rather than a param and the event is a local WRITE
                // then the var comes into scope just after it is initialised. so we count this point as
                // the point where an AT WRITE 1 or AFTER WRITE 1 rule should be injected. That's not
                // strictly correct for an AT WRITE 1 rule but then again we cannot expose the slot
                // before it is initialised since it is nto a valid slot until that point
                if  (startOffset > 0 && (count == 0 || (visitedCount < count)) && ((flags & Location.ACCESS_WRITE) != 0)) {
                    // a relevant invocation occurs in the called method
                    visitedCount++;
                    if (count == 0 || visitedCount == count) {
                        setTriggerPoint();
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
                index = -1;
            }
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (var == index) {
                if ((count == 0 || (visitedCount < count)) && matchCall(opcode)) {
                    // a relevant invocation occurs in the called method
                    visitedCount++;
                    if (count == 0 || visitedCount == count) {
                        setTriggerPoint();
                    }
                }
            }
            super.visitVarInsn(opcode, var);
        }

        public void visitIincInsn(int var, int increment)
        {
            // IINC counts as a read and a write
            if (var == index) {
                if ((count == 0 || (visitedCount < count))) {
                    // a relevant invocation occurs in the called method
                    visitedCount++;
                    if (count == 0 || visitedCount == count) {
                        setTriggerPoint();
                    }
                }
            }
            super.visitIincInsn(var, increment);
        }
    }

    private String varName;
    private int flags;
    private int count;
}