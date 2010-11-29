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
package org.jboss.byteman.rule.binding;

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
