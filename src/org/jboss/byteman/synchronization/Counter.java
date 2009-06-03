package org.jboss.byteman.synchronization;

/**
 * class used to associate a counter value with a given object
 */
public class Counter
{
    private int count;

    public Counter()
    {
        this(0);
    }
    public Counter(int count)
    {
        this.count = count;
    }

    public synchronized int count()
    {
        return count;
    }

    public synchronized int increment()
    {
        count++;

        return count;
    }

    public synchronized int decrement()
    {
        count--;

        return count;
    }
}
