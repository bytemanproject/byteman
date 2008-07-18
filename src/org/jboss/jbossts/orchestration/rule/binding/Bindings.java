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
     * add a binding to the end of the list
     *
     * n.b. the caller msut ensure that no binding with the same name is already present in
     * the list
     * @param binding
     * @return
     */
    public Binding append(Binding binding)
    {
        Binding oldBinding = lookup(binding.getName());
        if (oldBinding!= null) {
            bindings.remove(binding);
        }

        bindings.add(binding);

        return oldBinding;
    }

    /**
     * the list of current bindings
     */
    private List<Binding> bindings = new ArrayList<Binding>();
}
