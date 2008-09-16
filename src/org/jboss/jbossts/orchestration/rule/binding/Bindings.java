package org.jboss.jbossts.orchestration.rule.binding;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * * an ordered list of ECA rule event bindings as they occur in the event specification
 */
public class Bindings {
    /**
     * lookup a binding in the list by name
     *
     * @param name
     * @return the binding or null if no bidngin exists with the supplied name
     */
    public Binding lookup(String name)
    {
        ListIterator<Binding> iterator = bindings.listIterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();
            if (binding.getName().equals(name)) {
                return binding;
            }
        }

        return null;
    }

    /**
     * add the method parameter bindings to the front of the list
     *
     * n.b. the caller must ensure that the bindings are only for the rule's
     * positional parameters and have names constructed from successive non-negative integers
     * @param bindings
     */
    public void addBindings(List<Binding> bindings)
    {
        this.bindings.addAll(0, bindings);
    }

    /**
     * append a binding to the end of the currrent bindings list
     * @param binding
     * @return
     */
    public void append(Binding binding)
    {
        bindings.add(binding);
    }

    public Iterator<Binding> iterator()
    {
        return bindings.iterator();
    }

    /**
     * the list of current bindings
     */
    private List<Binding> bindings = new ArrayList<Binding>();
}
