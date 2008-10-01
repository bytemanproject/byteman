/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

    public boolean signalKill()
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
