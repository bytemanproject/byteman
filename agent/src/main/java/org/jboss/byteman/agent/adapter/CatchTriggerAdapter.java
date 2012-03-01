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
* @authors James Livingston
*/
package org.jboss.byteman.agent.adapter;

import java.util.HashSet;
import java.util.Set;

import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Adapter class used to add a rule event trigger call to a method of some given class
 */
public class CatchTriggerAdapter extends RuleTriggerAdapter
{
    public CatchTriggerAdapter(ClassVisitor cv, TransformContext transformContext, String typeName, int count)
    {
        super(cv, transformContext);
        this.typeName = typeName.replace('.', '/');
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
        // we can use the same adapter for methods and constructors
        if (matchTargetMethod(access, name, desc)) {
            return new CatchTriggerMethodAdapter(mv, getTransformContext(), access, name, desc, signature, exceptions);
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class CatchTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        private int visitedCount;

        CatchTriggerMethodAdapter(MethodVisitor mv, TransformContext transformContext, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, transformContext, access, name, descriptor, signature, exceptions);
            visitedCount = 0;
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            super.visitTryCatchBlock(start, end, handler, type);

            //FIXME
            if (type.equals(typeName) && (count == 0 || visitedCount < count)) {
                // a relevant invocation occurs in the called method
                visitedCount++;
                if (count == 0 || visitedCount == count) {
                    // and we have enough occurrences to match the count
                    triggerPoints.add(handler);
                }
            }
        }

		@Override
		public void visitLabel(Label label) {
			super.visitLabel(label);

            System.out.println("label: "  + label);
			if (triggerPoints.contains(label)) {
				// this is the start of the catch block
				injectTriggerPoint();
			}
		}
    }

    private String typeName;
    private int count;
    private Set<Label> triggerPoints = new HashSet<Label>();
}
