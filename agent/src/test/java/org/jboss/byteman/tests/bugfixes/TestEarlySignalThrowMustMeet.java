/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-10, Red Hat and individual contributors
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
 */

package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.tests.Test;

/**
 * A mustMeet signal delivered before anybody is waiting is held for the next
 * thread to arrive, so the two arrival orders have to reach the same outcome.
 * signalThrow and its signalKill alias must abort the waiting thread in either
 * order and signalWake must abort it in neither. The early signalThrow case is
 * the one that used to release the waiting thread normally.
 *
 * Each case uses its own identifier and the two threads are ordered by waiting
 * for a waiter to be registered, which is what both a waiting thread and a
 * mustMeet signaller with nobody to meet do first, so neither order depends on
 * a delay.
 *
 * Both participants run on their own thread in both orders. Neither side of a
 * mustMeet exchange is bounded, the wait in Waiter.waitFor because it is given
 * a zero timeout and the meet loop in Helper.signalThrow because it waits on
 * the waiter with none, so a rule call left on the junit thread could park the
 * test with nothing able to stop it. The junit thread therefore only starts
 * threads, joins them with a timeout and reports what they left behind.
 */
public class TestEarlySignalThrowMustMeet extends Test
{
    private static final String THROW = "throw";
    private static final String KILL = "kill";
    private static final String WAKE = "wake";

    /**
     * how long the test waits for a worker to finish before reporting it as
     * stuck rather than hanging the build
     */
    private static final long JOIN_TIMEOUT = 60 * 1000;

    public TestEarlySignalThrowMustMeet() {
        super(TestEarlySignalThrowMustMeet.class.getCanonicalName());
    }

    public void test() throws Exception
    {
        checkSignalFirst(THROW, "early-throw");
        checkWaiterFirst(THROW, "late-throw");
        checkSignalFirst(KILL, "early-kill");
        checkWaiterFirst(KILL, "late-kill");
        checkSignalFirst(WAKE, "early-wake");
        checkWaiterFirst(WAKE, "late-wake");

        checkOutput();
    }

    /**
     * runs one participant of a scenario on a thread of its own and keeps hold
     * of anything which escaped it, so that a failure on a worker is reported
     * by the junit thread instead of being swallowed by the default handler
     */
    private abstract static class Worker implements Runnable
    {
        private final String description;
        private final Thread thread;
        /**
         * volatile because the junit thread reads this after a join which may
         * have timed out, and only a join which completed would give it the
         * happens before edge that makes a plain field safe to read
         */
        private volatile Throwable failure;

        Worker(String description)
        {
            this.description = description;
            thread = new Thread(this, description);
            // a worker which is still parked must not be able to hold the jvm
            // open, otherwise a reported failure is followed by a hung fork
            thread.setDaemon(true);
        }

        public final void run()
        {
            try {
                work();
            } catch (Throwable t) {
                failure = t;
            }
        }

        protected abstract void work();

        void start()
        {
            thread.start();
        }

        void join(long timeout) throws InterruptedException
        {
            thread.join(timeout);
        }

        boolean isAlive()
        {
            return thread.isAlive();
        }

        String getDescription()
        {
            return description;
        }

        Throwable getFailure()
        {
            return failure;
        }
    }

    /**
     * the signalling thread arrives first, so it registers a waiter of its own
     * and holds the signal for whoever turns up next
     */
    private void checkSignalFirst(final String kind, final String identifier) throws Exception
    {
        Worker signaller = new Worker(identifier + " signaller") {
            protected void work()
            {
                signal(kind, identifier);
            }
        };
        Worker waiter = new Worker(identifier + " waiter") {
            protected void work()
            {
                ensureWaiterRegistered(identifier);
                waitAndLog(identifier);
            }
        };
        signaller.start();
        waiter.start();
        awaitBoth(signaller, waiter);
    }

    /**
     * the waiting thread arrives first, so the signal is delivered to a waiter
     * which is already registered
     */
    private void checkWaiterFirst(final String kind, final String identifier) throws Exception
    {
        Worker waiter = new Worker(identifier + " waiter") {
            protected void work()
            {
                waitAndLog(identifier);
            }
        };
        Worker signaller = new Worker(identifier + " signaller") {
            protected void work()
            {
                ensureWaiterRegistered(identifier);
                signal(kind, identifier);
            }
        };
        waiter.start();
        signaller.start();
        awaitBoth(waiter, signaller);
    }

    /**
     * join both workers before reporting on either, then report everything
     * found in one failure, so that a worker parked by its partner's failure
     * is not the only thing the log shows
     */
    private void awaitBoth(Worker first, Worker second) throws Exception
    {
        first.join(JOIN_TIMEOUT);
        second.join(JOIN_TIMEOUT);
        String firstProblem = problem(first);
        String secondProblem = problem(second);
        if (firstProblem != null && secondProblem != null) {
            fail(firstProblem + "; " + secondProblem);
        } else if (firstProblem != null) {
            fail(firstProblem);
        } else if (secondProblem != null) {
            fail(secondProblem);
        }
    }

    /**
     * the expected abort is caught and logged inside waitAndLog, so a captured
     * failure here is an ordering or helper failure and must not be mistaken
     * for the signal outcome
     */
    private String problem(Worker worker)
    {
        if (worker.isAlive()) {
            return worker.getDescription() + " did not finish within " + JOIN_TIMEOUT + "ms";
        }
        Throwable failure = worker.getFailure();
        if (failure != null) {
            return worker.getDescription() + " failed unexpectedly: " + failure;
        }
        return null;
    }

    /**
     * wait for the identifier and record whether the wait aborted the calling
     * rule or let it carry on. the outcome is taken from the trigger call
     * rather than from what the signalling builtin returned, so that these
     * cases do not depend on the signal return value. the catch is kept around
     * this call alone so that a failure to order the threads escapes it rather
     * than being logged as an abort.
     */
    private void waitAndLog(String identifier)
    {
        try {
            triggerWaitFor(identifier);
            log(identifier + " resumed");
        } catch (ExecuteException e) {
            log(identifier + " aborted");
        }
    }

    private void signal(String kind, String identifier)
    {
        if (THROW.equals(kind)) {
            triggerSignalThrow(identifier);
        } else if (KILL.equals(kind)) {
            triggerSignalKill(identifier);
        } else {
            triggerSignalWake(identifier);
        }
    }

    public void ensureWaiterRegistered(String identifier)
    {
        // unlike the other trigger methods this one must not be left as a no-op
        // if its rule fails to fire. the rule is what orders the two threads, so
        // without it the signal first cases would quietly run as if the waiter
        // had arrived first and would pass even with the bug reinstated. the
        // rule returns before this line, so reaching it means it did not fire
        throw new IllegalStateException("ensureWaiterRegistered rule did not fire for " + identifier);
    }

    public void triggerWaitFor(String identifier)
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerSignalThrow(String identifier)
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerSignalKill(String identifier)
    {
        // do nothing. this is just for the purpose of triggering
    }

    public void triggerSignalWake(String identifier)
    {
        // do nothing. this is just for the purpose of triggering
    }

    @Override
    public String getExpected() {
        logExpected("early-throw aborted");
        logExpected("late-throw aborted");
        logExpected("early-kill aborted");
        logExpected("late-kill aborted");
        logExpected("early-wake resumed");
        logExpected("late-wake resumed");
        return super.getExpected();
    }
}
