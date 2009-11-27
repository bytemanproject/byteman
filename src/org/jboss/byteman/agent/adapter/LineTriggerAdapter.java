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

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.agent.Transformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;

/**
 * asm Adapter class used to add a rule event trigger call to a method of som egiven class
 */
public class LineTriggerAdapter extends RuleTriggerAdapter
{
    public LineTriggerAdapter(ClassVisitor cv, Rule rule, String targetClass, String targetMethod, int targetLine)
    {
        super(cv, rule, targetClass, targetMethod);
        this.targetLine = targetLine;
        this.visitedLine = false;
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
            if (name.equals("<init>")) {
                return new LineTriggerConstructorAdapter(mv, rule, access, name, desc, signature, exceptions);
            } else {
                return new LineTriggerMethodAdapter(mv, rule, access, name, desc, signature, exceptions);
            }
        }
        return mv;
    }

    /**
     * a method visitor used to add a rule event trigger call to a method
     */

    private class LineTriggerMethodAdapter extends RuleTriggerMethodAdapter
    {
        protected boolean unlatched;

        LineTriggerMethodAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, targetClass, access, name, descriptor, signature, exceptions);
            this.unlatched = true;  // subclass can manipulate this to postponne visit
        }

        // somewhere we need to add a catch exception block
        // super.catchException(startLabel, endLabel, new Type("org.jboss.byteman.rule.exception.ExecuteException")));

        public void visitLineNumber(final int line, final Label start) {
            if (unlatched && !visitedLine && (targetLine <= line)) {
                injectTriggerPoint();
            }
            super.visitLineNumber(line, start);
        }
    }

    /**
     * a method visitor used to add a rule event trigger call to a constructor -- this has to make sure
     * the super constructor has been called before allowing a trigger call to be compiled
     */

    private class LineTriggerConstructorAdapter extends LineTriggerMethodAdapter
    {
        LineTriggerConstructorAdapter(MethodVisitor mv, Rule rule, int access, String name, String descriptor, String signature, String[] exceptions)
        {
            super(mv, rule, access, name, descriptor, signature, exceptions);
            this.unlatched = false;
        }

        public void visitMethodInsn(
            final int opcode,
            final String owner,
            final String name,
            final String desc)
        {
            super.visitMethodInsn(opcode, owner, name, desc);
            // hmm, this probably means the super constructor has been invoked :-)
            unlatched |= (opcode == Opcodes.INVOKESPECIAL);
        }
    }

    private int targetLine;
    private boolean visitedLine;
}