/*
* JBoss, Home of Professional Open Source
* Copyright 2011 Red Hat and individual contributors
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
*/
package org.jboss.byteman.sample.helper;

import java.io.IOException;

/**
 * An MXBean interface for the thread event history monitoring.
 * @author Scott stark (sstark@redhat.com) (C) 2011 Red Hat Inc.
 * @version $Revision:$
 */
public interface ThreadHistoryMonitorHelperMXBean {
    /**
     * Get the array of thread creation events.
     * @return the array of thread creation events in the order of occurrence.
     */
    public ThreadMonitorEvent[] getCreateEvents();
    /**
     * Get the array of thread start events.
     * @return the array of thread start events in the order of occurrence.
     */
    public ThreadMonitorEvent[] getStartEvents();
    /**
     * Get the array of thread exit events.
     * @return the array of thread exit events in the order of occurrence.
     */
    public ThreadMonitorEvent[] getExitEvents();
    /**
     * Get the array of Runnable.run events.
     * @return the array of Runnable.run events in the order of occurrence.
     */
    public ThreadMonitorEvent[] getRunEvents();
    /**
     * Get a string description of all thread events. This is the same event
     * information written by {@link #writeAllEventsToFile(String)}.
     * @return a formatted text description of all thread events.
     */
    public String getEventReport() throws IOException;
    /**
     * Write a report of all events of the indicated type to the given path.
     * @param type - one of create, start, exit, run; case insensitive
     * @param path - the pathname of the file to write the event report to.
     */
    public void writeEventsToFile(String type, String path) throws IOException;
    /**
     * Write a report of all events to the given path.
     * @param path - the pathname of the file to write the event report to.
     */
    public void writeAllEventsToFile(String path) throws IOException;
}
