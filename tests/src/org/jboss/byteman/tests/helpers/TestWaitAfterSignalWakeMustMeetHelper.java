package org.jboss.byteman.tests.helpers;

import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.Rule;

/**
 */
public class TestWaitAfterSignalWakeMustMeetHelper extends Helper
{
    protected TestWaitAfterSignalWakeMustMeetHelper(Rule rule) {
        super(rule);
    }

    static boolean waitForCalled = false;
    static boolean signalWakeCalled = false;
    static Object lock = new Object();

    public void waitFor(Object identifier, long timeout)
    {
        setWaitFor(); // there is a window here! we use a delay to close it
        super.waitFor(identifier, timeout);
    }

    public boolean signalWake(Object identifier, boolean mustMeet)
    {
        setSignalWake(); // there is a window here! we use a delay to close it
        return super.signalWake(identifier, mustMeet);
    }

    private void setWaitFor()
    {
        synchronized (lock) {
            waitForCalled = true;
            //System.out.println("waitForCalled <= true");
            lock.notify();
        }
    }
    
    private void setSignalWake()
    {
        synchronized (lock) {
            signalWakeCalled = true;
            //System.out.println("signalWakeCalled <= true");
            lock.notify();
        }
    }

    public void ensureWaitFor()
    {
        synchronized (lock) {
            //System.out.println("*waitForCalled = " + waitForCalled);
            while (!waitForCalled) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // do nothing
                }
                //System.out.println("**waitForCalled = " + waitForCalled);
            }
            //System.out.println("***waitForCalled = " + waitForCalled);
            waitForCalled = false;
            //System.out.println("****waitForCalled = " + waitForCalled);
        }
        // close the window -- maybe leaves a little air gap
        delay(1000);
    }

    public void ensureSignalWake()
    {
        synchronized (lock) {
            //System.out.println("*signalWakeCalled = " + signalWakeCalled);
            while (!signalWakeCalled) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    // do nothing
                }
                //System.out.println("**signalWakeCalled = " + signalWakeCalled);
            }
            //System.out.println("***signalWakeCalled = " + signalWakeCalled);
            signalWakeCalled = false;
            //System.out.println("****signalWakeCalled = " + signalWakeCalled);
        }
        // close the window -- maybe leaves a little air gap
        delay(1000);
    }
}
