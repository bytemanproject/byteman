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
        this.rejoinable = rejoinable;
        this.needsRemove = false;
        this.isDeleted = false;
        this.counter = new Counter();
    }

    /**
     * enter this rendezvous. n.b. this must be called synchronized on the rendezvous object
     * in question
     * @return the index in arrival order from 0 to expected of the calling thread or -1 if
     * either the rendezvous has completed and is not restartable or the rendezvous has been deleted
     */
    public int rendezvous()
    {
        Counter currentCounter = counter;

        // too late the rendezvous has expired
        
        if (isDeleted || (currentCounter.arrived == expected)) {
            return -1;
        }

        // n.b. getting here implies !currentCounter.isPoisoned

        int index = currentCounter.arrived++;

        if (currentCounter.arrived < expected) {
            // make sure we don't return before the rendezvous has actually happened
            while (currentCounter.arrived < expected) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // do nothing
                }

                // isPoisoned may have changed because a delete happened while we were waiting

                if (currentCounter.isPoisoned) {
                    return -1;
                }
            }
        } else {
            if (rejoinable) {
                // create a new counter for the next rendezvous -- this allows the current threads
                // to complete without counting them back out
                counter = new Counter();
            } else {
                // tag the rendezvous to indicate that it has been deleted and needs ot be removed. the
                // first thread emerging from a call to rendezvous must make sure it gets removed from
                // the rendezvous map.
                isDeleted = true;
                needsRemove = true;
            }
            this.notifyAll();
        }

        return index;
    }

    /**
     * delete this rendezvous causing any waiting threads to return -1 form the rendezvous call. n.b. this
     * must be called synchronized on the rendezvous object in question
     * @return
     */
    public boolean delete()
    {
        if (isDeleted) {
            return false;
        }
        isDeleted = true;
        needsRemove = true;

        // if any threads arrived then make sure they are *all* poisoned
        if (counter.arrived > 0 && counter.arrived < expected) {
            counter.isPoisoned = true;
            this.notifyAll();
        }

        return true;
    }

    public int getExpected() {
        return expected;
    }
    /**
     * the number of threads which are expected to arrive at this rendezvous
     */
    private int expected;

    /**
     * the current counter for this rendezvous
     */
    private Counter counter;
    /**
     * true if this rendezvous can be repeatedly joined, false it it is a one-off meeting
     */
    private boolean rejoinable;

    /**
     * true if a rendezvous was deleted while a rendezbvous was in progress but had not completed
     */

    private boolean isDeleted;

    /**
     * true if a non-restartable rendezvous has completed and has not been removed from the rendezvous map
     */
    private boolean needsRemove;

    /**
     * retrieve the number of threads waiting at the rendezvous or -1 if the rendezvous has
     * been deleted
     * @return
     */
    public int getArrived() {
        if (isDeleted) {
            return -1;
        }
        return counter.arrived;
    }

    /**
     * check if the rendezvous has completed but has not yet been rtemoved
     * @return
     */
    public boolean needsRemove() {
        return needsRemove;
    }

    /**
     * mark a completed rendezvous to indicate that it has been removed
     * @return
     */
    public void setRemoved() {
        needsRemove = false;
    }

    /**
     * class encapsulating state for a specific rendezvous
     */

    public class Counter
    {
        /**
         *  count of the number of threads actually arrived at this rendezvous
         */
        public int arrived;
        /**
         *  true if this
         */
        public boolean isPoisoned;

        public Counter()
        {
            arrived = 0;
            isPoisoned = false;
        }
    }
}
