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

import org.objectweb.asm.*;
import org.jboss.byteman.agent.Location;
import org.jboss.byteman.rule.Rule;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class AccessCheckAdapter extends RuleCheckAdapter
{
     public AccessCheckAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod, String ownerClass,
                       String fieldName,  int flags, int count)
    {
        super(cv, rule, targetClass, targetMethod);
        this.ownerClass = ownerClass;
        this.fieldName = fieldName;
        this.flags = flags;
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
            return new AccessCheckMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class AccessCheckMethodAdapter extends RuleCheckMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private boolean visited;

        AccessCheckMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor);
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
            if (visitedCount < count && matchCall(opcode, owner, name, desc)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (visitedCount == count) {
                    setTriggerPoint();
                }
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        public void visitEnd()
        {
            if (checkBindings()) {
                setVisitOk();
            }
            super.visitEnd();
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

    private String ownerClass;
    private String fieldName;
    private int flags;
    private int count;
    private int visitedCount;
}