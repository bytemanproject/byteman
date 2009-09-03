package org.jboss.byteman.synchronization;

import java.util.List;
import java.util.LinkedList;

/**
 * class used by default helper to implement join dependencies between threads
 */
public class Joiner
{

    /**
     * status values returned from child add method
     */
    public enum Status {
        /**
         * a DUPLICATE status is returned when a child fails to add itself to the join list because it is already present
         */
        DUPLICATE,
        /**
         * an EXCESS status is returned when a child fails to add itself to a join list because it already contains the
         * expected number of children
         */
        EXCESS,
        /**
         * an ADDED status is returned when a child successfully adds itself to the join list but without reaching
         * the expected number of children
         */
        ADDED,
        /**
         * a FILLED status is returned when a child successfully adds itself to the join list reaching the expected
         * number of children but there is no parent thread waiting for the children
         */
        FILLED,
        /**
         * a DONE  status is returned when a child successfully adds itself to the join list reaching the expected
         * number of children and there is a parent thread waiting for the children
         */
        DONE
    }

    private List<Thread> children;
    private int max;
    private Thread parent;

    public Joiner(int max)
    {
        this.max = max;
        this.children = new LinkedList<Thread>();
        this.parent =  null;
    }

    public int getMax()
    {
        return max;
    }

    public synchronized Status addChild(Thread thread)
    {
        if (children.contains(thread)) {
            return Status.DUPLICATE;
        }

        int size = children.size();

        if (size == max) {
            return Status.EXCESS;
        }

        children.add(thread);
        size++;

        if (size == max) {
            if (parent ==  null) {
                return Status.FILLED;
            } else {
                notifyAll();
                return Status.DONE;
            }
        }
        return Status.ADDED;
    }

    public boolean joinChildren(Thread thread)
    {
        synchronized (this) {
            if (parent != null) {
                return false;
            }
            parent = thread;
            while (children.size() < max) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
        // since we are the parent and the waiting is over we don't need to stay synchronized
        for (int i = 0; i < max;) {
            Thread child = children.get(i);
            try {
                child.join();
            } catch (InterruptedException e) {
                // try again
                break;
            }
            i++;
        }
        return true;
    }
}

