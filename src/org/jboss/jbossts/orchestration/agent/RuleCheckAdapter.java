/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.objectweb.asm.*;

/**
 * asm Adapter class used to check that the target method for a rule exists in a class
 */
public class RuleCheckAdapter extends ClassAdapter
{
    RuleCheckAdapter(ClassVisitor cv, String targetClass, String handlerMethod, int handlerLine)
    {
        super(cv);
        this.targetClass = targetClass;
        this.targetMethod = TypeHelper.parseMethodName(handlerMethod);
        this.targetDescriptor = TypeHelper.parseMethodDescriptor(handlerMethod);
        this.targetLine = handlerLine;
        this.visitOk = false;
    }

    public MethodVisitor visitMethod(
        final int access,
        final String name,
        final String desc,
        final String signature,
        final String[] exceptions)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (targetMethod.equals(name)) {
            if (targetDescriptor.equals("") || TypeHelper.equalDescriptors(targetDescriptor, desc))
            {
                return new RuleCheckMethodAdapter(mv, access, name, desc, signature, exceptions);
            }
        }

        return mv;
    }

    public boolean isVisitOk()
    {
        return visitOk;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class RuleCheckMethodAdapter extends MethodAdapter
    {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;
        private boolean visited;

        RuleCheckMethodAdapter(MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv);
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            this.visited = false;
        }

        public void visitLineNumber(final int line, final Label start) {
            if (!visited && (targetLine < 0 || targetLine == line)) {
                // the relevant line occurs in the called method
                visited = true;
                visitOk = true;
                String name = targetClass + "." + targetMethod + targetDescriptor;
                if (targetLine >= 0) {
                    name += targetLine;
                }
            }
            mv.visitLineNumber(line, start);
        }

    }

    private Rule rule;
    private String targetClass;
    private String targetMethod;
    private String targetDescriptor;
    private int targetLine;
    private boolean visitOk;
}