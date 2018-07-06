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
 * @authors Gary Brown
 */
package org.jboss.byteman.agent.adapter;

import org.jboss.byteman.agent.TransformContext;
import org.jboss.byteman.rule.type.TypeHelper;
import org.objectweb.asm.*;

/**
 * asm Adapter class used to add a rule event trigger call to a method of some given class
 */
public class ExceptionExitTriggerAdapter extends RuleTriggerAdapter
{
    public ExceptionExitTriggerAdapter(ClassVisitor cv, TransformContext transformContext)
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
                return new ExceptionExitTriggerConstructorAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            } else {
                return new ExceptionExitTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class ExceptionExitTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        private Label newStart;

        ExceptionExitTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
        }

        @Override
        public void visitCode()
        {
            // call the super method first so we have a valid CFG and start label
            super.visitCode();
            
            newStart = newLabel();
            visitLabel(newStart);
        }
        
        @Override
        public void visitMaxs(int maxStack, int maxLocals)
        {
            /** NEED TO CREATE CATCH HANDLER FOR THROWABLE THAT COVERS COMPLETE METHOD
             * 
             */
            Label newEnd=newLabel();
            
            Label throwableCatch = newLabel();
			
            visitTryCatchBlock(newStart, newEnd, throwableCatch,
            		Type.getType(TypeHelper.externalizeType("java.lang.Throwable")).getInternalName());

            visitLabel(newEnd);

            visitLabel(throwableCatch);

            injectTriggerPoint();
	        
            throwException();

            super.visitMaxs(maxStack, maxLocals);
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class ExceptionExitTriggerConstructorAdapter extends ExceptionExitTriggerMethodAdapter
    {
        ExceptionExitTriggerConstructorAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, getTransformContext(), access, name, descriptor, signature, exceptions);
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc,
            final boolean itf)
        {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
}