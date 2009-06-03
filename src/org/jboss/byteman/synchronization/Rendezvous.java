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
package org.jboss.byteman.synchronization;

/**
 * class used to manage rule rendezvous operations
 */
public class Rendezvous
{
    public Rendezvous(int expected)
    {
        this(expected, false);
    }

    public Rendezvous(int expected, boolean rejoinable)
    {
        this.expected = expected;
        this.arrived = 0;
        this.rejoinable = rejoinable;
    }

    public int rendezvous()
    {
        synchronized(this) {
            int index = arrived++;
            if (arrived < expected) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // do nothing
                }
            } else {
                if (rejoinable) {
                    // the last one in needs to set the count back to zero while it has the lock
                    // this makes sure that any of the existing threads or any new thread trying
                    // to re-enter the rendezvous will not fail to suspend
                    arrived = 0;
                }
                this.notifyAll();
            }
            return index;
        }
    }

    public int getExpected() {
        return expected;
    }
    /**
     * the number of threads which are expected to arrive at this rendezvous
     */
    private int expected;

    /**
     * the number of threads which have arrive at this rendezvous so far
     */
    private int arrived;
    /**
     * true if this rendezvous can be repeatedly joined, false it it is a one-off meeting
     */
    private boolean rejoinable;

    public boolean isRejoinable() {
        return rejoinable;
    }

    public int getArrived() {
        return arrived;
    }
}
