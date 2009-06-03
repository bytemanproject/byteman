package org.jboss.byteman.agent.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;
import org.jboss.byteman.rule.Rule;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

/**
 * generic rule method adapter which extends GeneratorAdpater and adds the ability to track in-scope
 * local variables
 */

// public class RuleMethodAdapter extends GeneratorAdapter {
public class RuleMethodAdapter extends RuleGeneratorAdapter {
    public RuleMethodAdapter(final MethodVisitor mv, final Rule rule, final int access, final String name, final String desc) {
        super(mv, access, name, desc);
        this.name = name;
        this.rule = rule;
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
        locals.addFirst(localVar);
    }

    protected List<LocalVar> lookup(String name)
    {
        return localVarsByName.get(name);
    }

    /**
     * a hashmap mapping local variable names to all in-scope variables with that name. the list of local
     * variables operates like a stack with the current in-scope binding at the top and each successive
     * entry representing an outer binding shadowed by its predecessors
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

    protected Rule rule;
    protected String name;
}
