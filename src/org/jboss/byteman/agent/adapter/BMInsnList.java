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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class BMInsnList extends InsnList
{
    // the local variables list managed by the method node which owns this instruction list
    List localvariables;
    
    public BMInsnList(List localVariables)
    {
        this.localvariables = localVariables;
    }
    public void accept(final MethodVisitor mv)
    {
        // this method visitor must implement LocalScopeMethodVisitor or else we woudl not be here
        
        LocalScopeMethodVisitor lsmv = (LocalScopeMethodVisitor) mv;
        
        // first index all local vars by start and end label
        HashMap<Label, LinkedList<LocalVariableNode>> localStarts = new HashMap<Label, LinkedList<LocalVariableNode>>();
        HashMap<Label, LinkedList<LocalVariableNode>> localEnds = new HashMap<Label, LinkedList<LocalVariableNode>>();

        Iterator iterator = localvariables.iterator();
        while (iterator.hasNext()) {
            LocalVariableNode local = (LocalVariableNode)iterator.next();
            Label label = local.start.getLabel();
            LinkedList<LocalVariableNode> locals = localStarts.get(label);
            if (locals == null) {
                locals = new LinkedList<LocalVariableNode>();
                localStarts.put(label, locals);
            }
            locals.addLast(local);
            label = local.end.getLabel();
            locals = localEnds.get(label);
            if (locals == null) {
                locals = new LinkedList<LocalVariableNode>();
                localEnds.put(label, locals);
            }
            locals.addLast(local);
        }

        // now visit the instructions intercepting labels
        AbstractInsnNode insn = getFirst();
        while (insn != null) {
            insn.accept(mv);
            if (insn.getType() == AbstractInsnNode.LABEL) {
                LabelNode labelNode = (LabelNode) insn;
                Label label = labelNode.getLabel();
                List<LocalVariableNode> localStart = localStarts.get(label);
                List<LocalVariableNode> localEnd = localEnds.get(label);
                if (localStart != null) {
                    for (LocalVariableNode local : localStart) {
                        lsmv.visitLocalScopeStart(local.name, local.desc, local.signature, local.index, label.getOffset());
                    }
                }
                if (localEnd != null) {
                    for (LocalVariableNode local : localEnd) {
                        lsmv.visitLocalScopeEnd(local.name, local.desc, local.signature, local.index, label.getOffset());
                    }
                }
            }
            insn = insn.getNext();
        }
    }
}
