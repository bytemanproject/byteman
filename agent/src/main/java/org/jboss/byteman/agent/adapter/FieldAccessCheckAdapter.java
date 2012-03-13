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

import org.objectweb.asm.*;
import org.jboss.byteman.agent.Location;
import org.jboss.byteman.agent.TransformContext;
import org.jboss.byteman.rule.type.TypeHelper;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class FieldAccessCheckAdapter extends RuleCheckAdapter
{
     public FieldAccessCheckAdapter(ClassVisitor cv, TransformContext transformContext, String ownerClass,
                               String fieldName, int flags, int count)
    {
        super(cv, transformContext);
        this.ownerClass = ownerClass;
        this.fieldName = fieldName;
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
            return new FieldAccessCheckMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class FieldAccessCheckMethodAdapter extends RuleCheckMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private int visitedCount;

        FieldAccessCheckMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            visitedCount = 0;
        }

        public void visitFieldInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            if ((count == 0 || visitedCount < count) && matchCall(opcode, owner, name, desc)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count == 0 || visitedCount == count) {
                    setTriggerPoint();
                }
            }
            super.visitFieldInsn(opcode, owner, name, desc);
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
                if (!ownerClass.equals(TypeHelper.internalizeClass(owner))) {
                    // TODO check for unqualified names
                    // if the called class has no package qualification and the owner class does then we
                    // can still match if the unqualified owner name equals the called class

                    if (ownerClass.indexOf('.') >= 0) {
                        return false;
                    }

                    int ownerPackageIdx = owner.lastIndexOf('/');
                    if (ownerPackageIdx < 0) {
                        return false;
                    } else if (!owner.substring(ownerPackageIdx+1).equals(ownerClass)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private String ownerClass;
    private String fieldName;
    private int flags;
    private int count;
}