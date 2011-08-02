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
package org.jboss.byteman.agent.adapter.cfg;

import org.objectweb.asm.Label;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * A representation of a 1:M relation between labels/code locations. This is used to represent
 * branchouts in a CFG where the 1 is the label of the BBlock and the M lists outgoing normal
 * (non-exception) control flow. It is also used to represent the relationship between basic
 * blocks (identified by their primary label) and labels identifying instructions contained in
 * the block. Note that in the former case the use of labels allows control flow linkes to basic
 * blocks which have not yet been generated to be recorded.
 */
public class FanOut
{
    /**
     * the 1 in the 1:m
     */
    private Label from;
    /**
     * the m in the 1:m
     */
    private List<Label> to;

    /**
     * construct a new empty link
     * @param from
     */
    public FanOut(Label from)
    {
        this.from = from;
        this.to = new LinkedList<Label>();
    }

    /**
     * construct a new link with one element in the target set
     * @param from
     * @param to
     */
    public FanOut(Label from, Label to)
    {
        this(from);
        append(to);
    }

    /**
     * construct a new link with two elements in the target set
     * @param from
     * @param to1
     * @param to2
     */
    public FanOut(Label from, Label to1, Label to2)
    {
        this(from);
        append(to1);
        append(to2);
    }

    public Label getFrom()
    {
        return from;
    }

    /**
     * add a new link to the target set
     * @param to
     */
    public void append(Label to)
    {
        this.to.add(to);
    }

    /**
     * retrieve alink from the target set by index
     * @param i
     * @return
     */
    public Label getTo(int i)
    {
        if (to.size() > i) {
            return to.get(i);
        } else {
            return null;
        }
    }

    /**
     * retrieve the size of the target set
     * @return
     */
    public int getToCount()
    {
        return to.size();
    }

    /**
     * obtain an iterator over the target set
     * @return
     */
    public Iterator<Label> iterator()
    {
        return to.iterator();
    }
}
