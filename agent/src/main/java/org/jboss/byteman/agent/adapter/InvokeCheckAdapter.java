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

import org.jboss.byteman.rule.type.Type;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.agent.TransformContext;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class InvokeCheckAdapter extends RuleCheckAdapter
{
     public InvokeCheckAdapter(ClassVisitor cv, TransformContext transformContext,
                               String calledClass, String calledMethodName,String calledMethodDescriptor, int count)
    {
        super(cv, transformContext);
        this.calledClass = calledClass;
        this.calledMethodName = calledMethodName;
        this.calledMethodDescriptor = calledMethodDescriptor;
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
        if (matchTargetMethod(access, name, desc)) {
            setVisited();
            return new InvokeCheckMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class InvokeCheckMethodAdapter extends RuleCheckMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private boolean visited;

        InvokeCheckMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            visitedCount = 0;
            matchedReturnType = null;
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            if ((count == 0 || visitedCount < count) && matchCall(owner, name, desc)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count ==  0 || visitedCount == count) {
                    setTriggerPoint();
                }
            }
            super.visitMethodInsn(opcode, owner, name, desc);
        }

        private boolean matchCall(String owner, String name, String desc)
        {
            if (!calledMethodName.equals(name)) {
                return false;
            }
            if (calledClass != null) {
                if (!calledClass.equals(TypeHelper.internalizeClass(owner))) {
                    // TODO check for unqualified names
                    // if the called class has no package qualification and the owner class does
                    // then we can still match if the unqualified owner name equals the called class
                    if (calledClass.indexOf('.') >= 0) {
                        return false;
                    }
                    int ownerPackageIdx = owner.lastIndexOf('/');
                    if (ownerPackageIdx < 0) {
                        return false;
                    } else if (!owner.substring(ownerPackageIdx+1).equals(calledClass)) {
                        return false;
                    }
                }
            }
            if (calledMethodDescriptor.length() > 0) {
                if (!TypeHelper.equalDescriptors(calledMethodDescriptor, desc)) {
                    return false;
                }
            }

            // save the descriptor for the call or void if the descriptors don't match

            if (matchedReturnType == null) {
                matchedReturnType = Type.parseMethodReturnType(desc);
            } else if (!matchedReturnType.equals("void")) {
                String newReturnType = Type.parseMethodReturnType(desc);
                // TODO - allow for one type to be a subtype of the other?
                if (newReturnType != matchedReturnType) {
                    matchedReturnType = "void";
                }
            }

            return true;
        }

        @Override
        protected String getReturnBindingType() {
            if (matchedReturnType != null) {
                return matchedReturnType;
            }

            return "void";
        }
    }

    private String calledClass;
    private String calledMethodName;
    private String calledMethodDescriptor;
    private String matchedReturnType;
    private int count;
    private int visitedCount;
}