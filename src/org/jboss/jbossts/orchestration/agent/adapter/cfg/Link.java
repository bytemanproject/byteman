package org.jboss.jbossts.orchestration.agent.adapter.cfg;

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
public class Link
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
    public Link(Label from)
    {
        this.from = from;
        this.to = new LinkedList<Label>();
    }

    /**
     * construct a new link with one element in the target set
     * @param from
     * @param to
     */
    public Link(Label from, Label to)
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
    public Link(Label from, Label to1, Label to2)
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
