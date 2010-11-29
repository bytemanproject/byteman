/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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
package org.jboss.byteman.synchronization;

import org.jboss.byteman.rule.exception.ExecuteException;

/**
 * class used to manage rule wait operations
 */
public class Waiter
{
    public Waiter(Object object)
    {
        this(object, false, false);
    }

    public Waiter(Object object, boolean signalled, boolean killed)
    {
        this.waiterFor = object;
        this.signalled = signalled;
        this.killed = killed;
        this.waiting = false;
    }

    public void waitFor(long millisecs)
    {
        long start = System.currentTimeMillis();
        long waitForMillis = millisecs;
        synchronized(this) {
            waiting = true;
            while (!signalled && waitForMillis >= 0){
                try {
                	if (waitForMillis == 0 && millisecs > 0) {
                		break;
                	}
                	this.wait(waitForMillis);
                } catch (InterruptedException e) {
                    // ignore
                }
                
                if (!signalled)
                {
                   waitForMillis = (millisecs == 0) ? 0 : millisecs + start - System.currentTimeMillis();
                }
            }
            if (signalled) {
               // notify in case a signalling thread was waiting
               this.notifyAll();
            }
        }
        
        // if a signalKill was used then we have to throw an exception otherwise we just return
        if (killed) {
            throw new ExecuteException("Waiter.waitFor : killed thread waiting for " + waiterFor);
        }
    }

    public boolean signalWake()
    {
        boolean result;

        synchronized (this) {
            result = signalled;
            if (!signalled) {
                signalled = true;
                this.notifyAll();
            }
        }

        return result;
    }

    public boolean signalThrow()
    {
        boolean result;

        synchronized (this) {
            result = signalled;
            if (!signalled) {
                signalled = true;
                killed = true;
                this.notifyAll();
            }
        }

        return result;
    }

    public boolean waiting()
    {
        return waiting;
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

    /**
     * true if waitFor has been called
     */

    private boolean waiting;

    /**
     * getter for signalled flag
     * @return signalled
     */
    public boolean isSignalled() {
        return signalled;
    }
}
