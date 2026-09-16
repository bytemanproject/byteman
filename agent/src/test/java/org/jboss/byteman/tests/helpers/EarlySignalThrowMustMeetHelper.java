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

package org.jboss.byteman.tests.helpers;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.exception.ExecuteException;
import org.jboss.byteman.rule.helper.Helper;

/**
 * Helper which lets a test pin down the order in which a waiter and a mustMeet
 * signaller reach the same identifier. Both of them register a waiter under
 * that identifier, the waiting thread because it is about to wait and the
 * signaller because it found nobody waiting yet, so waiting for the
 * registration to appear is enough to establish which of the two arrived
 * first without either thread having to guess a delay.
 */
public class EarlySignalThrowMustMeetHelper extends Helper
{
    /**
     * how long {@link #ensureWaiter} is prepared to wait for the other thread.
     * it only ever waits this long when the test has already gone wrong, so
     * the limit buys a reported failure rather than a build which hangs.
     */
    private static final long ARRIVAL_TIMEOUT = 60 * 1000;

    protected EarlySignalThrowMustMeetHelper(Rule rule) {
        super(rule);
    }

    /**
     * return once a waiter is registered for the supplied identifier
     * @param identifier the identifier the other thread is expected to reach
     */
    public void ensureWaiter(Object identifier)
    {
        long deadline = System.currentTimeMillis() + ARRIVAL_TIMEOUT;
        // waiting() takes the wait map lock on every call, so the registration
        // made by the other thread is published to this one
        while (!waiting(identifier)) {
            if (System.currentTimeMillis() >= deadline) {
                throw new ExecuteException("no waiter registered for " + identifier);
            }
            delay(1);
        }
    }
}
