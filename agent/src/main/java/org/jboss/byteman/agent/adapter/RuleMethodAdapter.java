/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.agent.TransformContext;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

/**
 * generic rule method adapter which extends GeneratorAdpater and adds the ability to track in-scope
 * local variables
 */

// public class RuleMethodAdapter extends GeneratorAdapter {
public class RuleMethodAdapter extends MethodAdapter {
    public RuleMethodAdapter(final MethodVisitor mv, final TransformContext transformContext, final int access, final String name, final String desc, Rule rule) {
        super(mv);
        this.access = access;
        this.name = name;
        this.descriptor = desc;
        this.transformContext = transformContext;
        this.rule = rule;
    }

    public String getTriggerClassName()
    {
        return transformContext.getTriggerClassName();
    }

    public void visitLocalVariable(
        final String name,
        final String desc,
        final String signature,
        final Label start,
        final Label end,
        final int index)
    {
        // first, let the parent class do its stuff
        super.visitLocalVariable(name, desc, signature, start, end, index);

        // keep track of all local variables and their labels
        LocalVar localVar = new LocalVar(name, desc, signature, start, end, index);
        LinkedList<LocalVar> locals = localVarsByName.get(name);
        if (locals == null) {
            locals = new LinkedList<LocalVar>();
            localVarsByName.put(name, locals);
        }
        locals.add(localVar);
    }

    protected List<LocalVar> lookup(String name)
    {
        return localVarsByName.get(name);
    }

    /**
     * a hashmap mapping local variable names to all local variables with that name.  note  that although
     * there may be more than one scope for a given local var name they will all have disjoint extents
     * because Java does not permit shadowing.
     */

    HashMap<String, LinkedList<LocalVar>> localVarsByName = new HashMap<String, LinkedList<LocalVar>>();

    protected static class LocalVar
    {
        public String name;
        public String desc;
        public String signature;
        public Label start;
        public Label end;
        public int index;

        public LocalVar(String name, String desc, String signature, Label start, Label end, int index)
        {
            this.name = name;
            this.desc = desc;
            this.signature = signature;
            this.start = start;
            this.end = end;
            this.index = index;
        }
    }

    protected TransformContext transformContext;
    protected Rule rule;
    protected int access;
    protected String name;
    protected String descriptor;
}
