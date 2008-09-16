package org.jboss.jbossts.orchestration.synchronization;

import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;

/**
 * class used to manage rule wait operations
 */
public class Waiter
{
    public Waiter(Object object)
    {
        this.waiterFor = object;
        this.signalled = false;
        this.killed = false;
    }

    public void waitFor(long millisecs)
    {
        synchronized(this) {
            if (!signalled) {
                try {
                    this.wait(millisecs);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        
        // if a signalKill was used then we have to throw an exception otherwise we just return
        
        if (killed) {
            throw new ExecuteException("Waiter.waitFor waiting thread killed for " + waiterFor);
        }
    }

    public boolean signal()
    {
        boolean result;

        synchronized (this) {
            result = signalled;
            if (!signalled) {
                signalled = true;
            }
        }
        if (!result) {
            this.notifyAll();
        }

        return result;
    }

    public boolean signalKill()
    {
        boolean result;

        synchronized (this) {
            result = signalled;
            if (!signalled) {
                signalled = true;
                killed = true;
            }
        }

        if (!result) {
            this.notifyAll();
        }

        return result;
    }

    /**
     * the object with which this waiter is associated
     */

    private Object waiterFor;

    /**
     * true if this waiter has been signalled by a call to signalKill
     */

    private boolean signalled;

    /**
     * true if this waiter has been signalled by a call to signal or signalKill
     */

    private boolean killed;
}
