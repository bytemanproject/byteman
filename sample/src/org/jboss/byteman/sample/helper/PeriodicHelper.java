/*
* JBoss, Home of Professional Open Source
* Copyright 2010 Red Hat and individual contributors
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
package org.jboss.byteman.sample.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

/**
 * A helper class which adds a background thread when the helper class is activated and removes the background
 * thread when it is deactivated. The thread loops, calling a specific trigger method, {@link #periodicTrigger}
 * and then waiting for a fixed time. Rule sets which employ the helper class can attach rules to the periodic
 * trigger method in order to perform operations which should happen at regular intervalsx. This is useful,
 * for example, for rule sets which collect statistical information. A periodically triggered rule can read and
 * then rezero a set of stats counters, allowing it to provide regular interval statistics.
 *
 * The basic way to use this class is to employ attach a rule to method {@link #periodicTrigger()}. The rule will
 * be triggered at 10 second intervals. If the period needs to be altered then a rule attached to method
 * {@link #getPeriod()} can be used to return an alternative value. {@link #getPeriod()} is called once when the
 * periodic trigger thread is created. If the wait interval needs to be recomputed before each wait then a rule
 * attached to method {@link #resetPeriod(long)} can be used to return the desired value. The input argument is
 * the value returned by the call to {@link #getPeriod()}.
 */

public class PeriodicHelper extends Helper
{
    /**
     * the default period which the helper will wait for between calls to periodicTrigger in milliseconds. this
     * can be redefined either by overriding defaultPeriod
     */
    public final static long DEFAULT_PERIOD = 10000L;

    public PeriodicHelper(Rule rule) {
        super(rule);
        shutDown = false;
    }

    /**
     * a method which is called at regular intervals by the periodic helper thread to trigger rule processing.
     * This can be redefined by attaching one or more rules to the method. It is also possible to override
     * this method in a subclass
     */

    protected void periodicTrigger()
    {
        // do nothing -- rules will override this behaviour
    }

    /**
     * a method which is called when the periodic helper thread is started to compute the interval in milliseconds
     * for which the thread should wait between calls to the trigger method which by default returns the default
     * interval of 10 seconds. This can either be overridden or redefined by attaching a rule to the method.
     */

    protected long getPeriod()
    {
        return DEFAULT_PERIOD;
    }

    /**
     * a method which is called when the periodic helper thread is about to wait which by default returns the
     * input value. this can be overridden or redefined by attaching a rule to the method.
     * @param initialPeriod the initial wait time returned by getPeriod when the periodic thread was created
     */

    protected long resetPeriod(long initialPeriod)
    {
        return initialPeriod;
    }

    /**
     * helper activation method which creates a periodic helper thread to perform periodic calls to the trigger
     * method. should only be called when synchronized on PeriodicHelper.class.
     */
    public static void activated()
    {
        if (theHelper == null) {
            theHelper = new PeriodicHelper(null);
            theHelper.start();
        }
    }

    /**
     * helper deactivation method which shuts down the periodic helper thread. will only be called when
     * synchronized on PeriodicHelper.class
     */
    public static void deactivated()
    {
        if (theHelper != null) {
            theHelper.shutdown();
            theHelper = null;
        }
    }

    /**
     * method called in activate to create and run the shutdown thread.  will only be called when synchronized
     * on PeriodicHelper.class
     */
    private void start()
    {
        theHelperThread = new PeriodicHelperThread();
        theHelperThread.start();
    }

    /**
     * method called in deactivate the helper thread. will only be called when synchronized on
     * PeriodicHelper.class
     */
    private void shutdown()
    {
        synchronized (this) {
            shutDown = true;
            this.notify();
        }
        try {
            theHelperThread.join();
        } catch (InterruptedException e) {
            // ignore -- should never happen
        }
        theHelperThread = null;
    }

    /**
     * method called by the periodic helper thread to wait between calls to the trigger method
     */

    private boolean doWait(long periodMilliSecs)
    {
        synchronized(this) {
            if (!shutDown) {
                try {
                    setTriggering(true);
                    periodMilliSecs = resetPeriod(periodMilliSecs);
                    setTriggering(false);
                    wait(periodMilliSecs);
                } catch (InterruptedException e) {
                    // ignore -- should never happen
                }
            }
            return !shutDown;
        }
    }

    /**
     * singleton instance holding the current periodic helper
     */

    private static PeriodicHelper theHelper = null;

    /**
     * handle on the current helper thread
     */

    private static PeriodicHelperThread theHelperThread = null;

    /**
     * flag which enforces shutdown
     */
    private boolean shutDown;

    private class PeriodicHelperThread extends Thread
    {
        private long periodMilliSecs;

        public PeriodicHelperThread()
        {
            super("Periodic Helper Thread");
        }

        public void run()
        {
            periodMilliSecs = getPeriod();
            setTriggering(false);
            while (doWait(periodMilliSecs)) {
                setTriggering(true);
                periodicTrigger();
                setTriggering(false);
            }
        }
    }
}
