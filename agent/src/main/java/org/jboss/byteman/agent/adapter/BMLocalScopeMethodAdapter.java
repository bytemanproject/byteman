/*
* JBoss, Home of Professional Open Source
* Copyright 2010 Red Hat and individual contributors
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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

/**
 * a subclass of JSRInlinerAdapter which pushes local variable info through to the next
 * adapter inline during code generation if it wants it
 */
public class BMLocalScopeMethodAdapter extends MethodNode
{
    private MethodVisitor mv;

    /**
     * creates a method node with an instruction list which notifies local var scope start and end
     * events. should only be called with a method visitor which is an instance of LocalScopeMethodVisitor
     * @param mv
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     */
    public BMLocalScopeMethodAdapter(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        super(access, name, desc, signature, exceptions);
        this.mv = mv;
        if (mv instanceof LocalScopeMethodVisitor) {
            // replace the instruction list so that it generates the required start and end local scope calls
            instructions = new BMInsnList(localVariables);
        }
    }

    /**
     * once we have seen all the opcodes we can push the stored  method tree through the next visitor in line
     */
    public void visitEnd()
    {
        accept(mv);
    }
}
