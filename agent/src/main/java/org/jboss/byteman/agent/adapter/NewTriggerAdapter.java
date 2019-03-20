/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat and individual contributors
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
import org.jboss.byteman.rule.type.TypeHelper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class NewTriggerAdapter extends RuleTriggerAdapter
{
    public NewTriggerAdapter(ClassVisitor cv, TransformContext transformContext,
                             String typeName, int count, boolean whenComplete)
    {
        super(cv, transformContext);
        this.typeName = typeName;
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
                return new NewTriggerConstructorAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            } else {
                return new NewTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class NewTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        /**
         * flag used by subclass to avoid inserting trigger until after super constructor has been called
         */
        protected boolean latched;
        private int visitedCount;
        private boolean triggerReady;
        private String matchedBaseName;

        NewTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            visitedCount = 0;
            latched = false;
            triggerReady = false;
            matchedBaseName = null;
        }

        @Override
        public String getNewClassName()
        {
            return matchedBaseName;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface)
        {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            if (triggerReady && opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
                if (!latched) {
                    // inject a trigger here after the NEW and <init> call
                    injectTriggerPoint();
                } else {
                    transformContext.warn(name, descriptor, "cannot inject AFTER NEW rule into constructor before super constructor has been called");
                }
                triggerReady = false;
            }
        }

        @Override
        public void visitTypeInsn(int opcode, String type)
        {
            if (opcode == Opcodes.NEW && (visitedCount == 0 || visitedCount < count) && matchType(type)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if(visitedCount == 0 || visitedCount == count) {
                    if(whenComplete) {
                        // set ready for triggering at the next invokespecial
                        triggerReady = true;
                    } else {
                        // cannot inject unless the object has been initialized
                        if(!latched) {
                            // inject a trigger here just before the NEW
                            injectTriggerPoint();
                        } else {
                            transformContext.warn(name, descriptor, "cannot inject AT NEW rule into constructor before super constructor has been called");
                        }
                    }
                }
            }
            super.visitTypeInsn(opcode, type);
        }

        private boolean matchType(String type)
        {
            String baseName = TypeHelper.internalizeClass(type, true);
            boolean matched = false;
            // matches are for the explicitly named type
            // or any type if the chosen name is the empty string
            if (typeName.length() == 0) {
                matched = true;
            } else if (typeName.equals(baseName)) {
                matched = true;
            } else if (!typeName.contains(".") && baseName.contains(".")) {
                int tailIdx = baseName.lastIndexOf(".");
                matched = typeName.equals(baseName.substring(tailIdx+1));
            }
            if (matched) {
                // save any matched base name so we can use it to type $!
                matchedBaseName = baseName;
            }

            return matched;
        }

        @Override
        public Type getReturnBindingType()
        {
            // n.b. we use externalizeClass here as we don't need a bracketing L and colon
            return Type.getObjectType(TypeHelper.externalizeClass(getNewClassName()));
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class NewTriggerConstructorAdapter extends NewTriggerMethodAdapter
    {
        NewTriggerConstructorAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            // ensure we don't transform calls before the super constructor is called
            latched = true;
        }

        public void visitMethodInsn(
                final int opcode,
                final String owner,
                final String name,
                final String desc,
                boolean itf)
        {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if (latched && isSuperOrSiblingConstructorCall(opcode, owner, name)) {
                latched = false;
            }
        }
    }

    protected String typeName;
    protected int count;
    protected boolean whenComplete;
}