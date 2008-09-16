package org.jboss.jbossts.orchestration.synchronization;

/**
 * class provided to support rule builtins getCounter, decrementCounter and addCounter
 */
public class CountDown
{
    public CountDown(int count)
    {
        this.count = (count < 1 ? 1 : count);
    }

    public synchronized boolean decrement()
    {
        if (count > 0) {
            count--;
            return false;
        }

        return true;
    }

    private int count;
}
