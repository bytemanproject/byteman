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

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class NewArrayCheckAdapter extends RuleCheckAdapter
{
     public NewArrayCheckAdapter(ClassVisitor cv, TransformContext transformContext,
                                 String typeName, int count, int dims, boolean whenComplete)
    {
        super(cv, transformContext);
        this.typeName = typeName;
        this.count = count;
        this.dims = dims;
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
        if (matchTargetMethod(access, name, desc)) {
            setVisited();
            return new NewArrayCheckMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
        }

        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class NewArrayCheckMethodAdapter extends RuleCheckMethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private boolean visited;
        private int visitedCount;
        String matchedBaseName;

        NewArrayCheckMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            visitedCount = 0;
            matchedBaseName = null;
        }

        @Override
        public void visitTypeInsn(int opcode, String type)
        {
            boolean triggerReady = false;
            if (opcode == Opcodes.ANEWARRAY &&
                    (count == 0 || visitedCount < count) &&
                    matchDims(type, 1) && matchType(type)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count ==  0 || visitedCount == count) {
                    if (whenComplete) {
                        // set ready for triggering after the array is created
                        triggerReady = true;
                    } else {
                        // inject a trigger here just before the NEW
                        setTriggerPoint();
                    }
                }
            }
            super.visitTypeInsn(opcode, type);
            if (triggerReady) {
                setTriggerPoint();
            }
        }

        @Override
        public void visitIntInsn(int opcode, int operand)
        {
            boolean triggerReady = false;
            if (opcode == Opcodes.NEWARRAY && dims == 1 &&
                    (count == 0 || visitedCount < count) && matchType(operand)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count ==  0 || visitedCount == count) {
                    if (whenComplete) {
                        // set ready for triggering after the array is created
                        triggerReady = true;
                    } else {
                        // inject a trigger here just before the NEW
                        setTriggerPoint();
                    }
                }
            }
            super.visitIntInsn(opcode, operand);
            if (triggerReady) {
                setTriggerPoint();
            }
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions)
        {
            boolean triggerReady = false;
            if (dims == numDimensions && (count == 0 || visitedCount < count) && matchType(descriptor)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count ==  0 || visitedCount == count) {
                    if (whenComplete) {
                        // set ready for triggering after super call
                        triggerReady = true;
                    } else {
                        // inject a trigger here just before the NEW
                        setTriggerPoint();
                    }
                }
            }
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
            if (triggerReady) {
                setTriggerPoint();
            }
        }

        private boolean matchDims(String type, int included) {
            int typeDims = TypeHelper.dimCount(type) + included;
            return typeDims == dims;
        }

        private boolean matchType(int operand)
        {
            String baseName = null;
            // operand identifies a primitive type
            switch (operand) {
                case Opcodes.T_BOOLEAN:
                    baseName = "boolean";
                    break;
                case Opcodes.T_BYTE:
                    baseName = "byte";
                    break;
                case Opcodes.T_CHAR:
                    baseName = "char";
                    break;
                case Opcodes.T_SHORT:
                    baseName = "short";
                    break;
                case Opcodes.T_INT:
                    baseName = "int";
                    break;
                case Opcodes.T_LONG:
                    baseName = "long";
                    break;
                case Opcodes.T_FLOAT:
                    baseName = "float";
                    break;
                case Opcodes.T_DOUBLE:
                    baseName = "double";
                    break;
                default:
                    // should never happen
                    break;
            }

            if(typeName.length() == 0 || typeName.equals(baseName)) {
                // save any matched base name so we can use it to type $!
                matchedBaseName = baseName;
                return true;
            }
            return false;
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
        public String getNewTypeParamDescriptor()
        {
            StringBuilder builder = new StringBuilder(matchedBaseName);
            for (int i = 0; i < dims ; i++) {
                builder.append("[]");
            }
            return TypeHelper.externalizeType(builder.toString());
        }
    }

    private String typeName;
    private int count;
    private int dims;
    private boolean whenComplete;
}