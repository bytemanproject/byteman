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

import org.jboss.byteman.agent.TransformContext;
import org.objectweb.asm.*;

/**
 * asm Adapter class used to add a rule event trigger call to a method of some given class
 */
public class RuleTriggerAdapter extends RuleAdapter
{
    protected String className;
    protected String superName;
    protected RuleTriggerAdapter(ClassVisitor cv, TransformContext transformContext)
    {
        super(cv, transformContext);
        className = null;
        superName =  null;
    }

    protected boolean injectIntoMethod(String name, String desc) {
        // if the check adapter matched successfully then there will be a rule in place
        // for this method and descriptor
        return (transformContext.injectIntoMethod(name, desc));

    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        // keep track of the name and super name in case we need to check that a
        // constructor ahs actually been constructed
        this.className = name;
        this.superName = superName;
    }

    protected boolean isSuperOrSiblingConstructorCall(int opcode, String owner, String name)
    {
        return (opcode == Opcodes.INVOKESPECIAL &&
                name.equals("<init>") &&
                (superName.equals(owner) || className.equals(owner)));
    }
}
