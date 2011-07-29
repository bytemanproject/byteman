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
 *
 * (C) 2009-10,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.tests.helpers;

import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.Rule;

/**
 */
public class WaitAfterSignalWakeMustMeetHelper extends Helper
{
    protected WaitAfterSignalWakeMustMeetHelper(Rule rule) {
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
