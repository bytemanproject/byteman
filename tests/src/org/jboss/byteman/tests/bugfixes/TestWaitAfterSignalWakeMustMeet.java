package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.rule.exception.ExecuteException;

/**
 * This test accompaniesBYTEMAN-38. The bug happens when the same key is used for two successive pairs of
 * calls to builtin helper methods waitFor(key) and signalWake(key, true). When the first call to waitFor
 * happens before the first call to signalWake then the cleanup under signalWake fails to remove the
 * Waiter object associated with key. The next call to waitFor finds a waiter which has been signalled
 * and returns immediately. The call to signalWake shoudl remove the waiter before returning.
 */
public class TestWaitAfterSignalWakeMustMeet extends Test
{
    public TestWaitAfterSignalWakeMustMeet() {
        super(TestWaitAfterSignalWakeMustMeet.class.getCanonicalName());
    }

    public void test() throws Exception
    {
        Thread thread1 = new Thread() {
            public void run()
            {
                runThread1();
            }
        };
        Thread thread2 = new Thread() {
            public void run()
            {
                runThread2();
            }
        };
        thread1.start();
        thread2.start();
        try {
            triggerTimeoutCheck();
        } catch (ExecuteException e) {
            log("caught execute exception");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        checkOutput();
    }

    public void ensureSignalWake()
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void ensureWaitFor()
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerWaitFor()
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerRendezvous()
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerSignalWake()
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerTimeoutCheck() throws Exception
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerTimeoutCancel()
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void runThread1()
    {
        ensureSignalWake();
        //System.out.println("thread1 : ensured signalWake sent");
        triggerWaitFor();
        //System.out.println("thread1 : triggered waitFor");
        triggerRendezvous();
        //System.out.println("thread1 : triggered rendezvous 1");
        triggerWaitFor();
        //System.out.println("thread1 : triggered waitFor");
        triggerRendezvous();
        //System.out.println("thread1 : triggered rendezvous 2");
    }

    public void runThread2()
    {
        triggerSignalWake();
        //System.out.println("thread2 : triggered signalWake");
        ensureWaitFor();
        //System.out.println("thread2 : ensured waitFor");
        triggerRendezvous();
        //System.out.println("thread2 : triggered rendezvous 1");
        ensureWaitFor();
        //System.out.println("thread2 : ensured waitFor");
        triggerSignalWake();
        //System.out.println("thread2 : triggered signalWake");
        triggerRendezvous();
        //System.out.println("thread2 : triggered rendezvous 2");
        triggerTimeoutCancel();
        //System.out.println("thread2 : cancelled timeout");
    }

    @Override
    public String getExpected() {
        logExpected("signalWake");
        logExpected("waitFor");
        logExpected("waitFor");
        logExpected("signalWake");
        logExpected("caught execute exception");
        return super.getExpected();
    }
}
