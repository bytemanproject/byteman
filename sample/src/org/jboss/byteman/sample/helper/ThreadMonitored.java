/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016 Red Hat and individual contributors
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

import java.io.Serializable;

/**
 * This is a DTO object which contains an identity of a real {@link Thread} object.
 * This one is used for monitoring of thread creation and termination.
 * Additional stack trace data are gathered by {@link ThreadMonitorEvent} objects
 * and processed in {@link ThreadHistoryMonitorHelper}. 
 */
public class ThreadMonitored implements Serializable {
    private static final long serialVersionUID = 1;

    private String threadName;
    private long threadId;
    private int threadHashCode;
    private String runnableClass;
    private ThreadMonitored createdBy;

    /**
     * Creating new instance of {@link ThreadMonitored}. Data is drained
     * from the supplied {@link Thread} instance.
     */
    public static ThreadMonitored newMonitoredThread(final Thread thread) {
        return new ThreadMonitored(thread.getName(), thread.getId(), thread.hashCode());
    }

    private ThreadMonitored(String threadName, long threadId, int threadHashCode) {
        this.threadId = threadId;
        this.threadName = threadName;
        this.threadHashCode = threadHashCode;
    }

    public String getThreadName() {
        return threadName;
    }
    
    public long getThreadId() {
        return threadId;
    }

    public String getRunnableClass() {
        return runnableClass;
    }

    public void setRunnableClass(Class<?> runnableClass) {
        this.runnableClass = runnableClass.toString();
    }

    public void setCreatedBy(ThreadMonitored createdBy) {
        this.createdBy = createdBy;
    }

    public ThreadMonitored getCreatedBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        StringBuffer eventId = new StringBuffer()
            .append(getThreadName())
            .append(":")
            .append(getThreadId());
        if(getRunnableClass() != null) {
            eventId
                .append("(")
                .append(getRunnableClass())
                .append(")");
        }
        return eventId.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + threadHashCode;
        result = prime * result + (int) (threadId ^ (threadId >>> 32));
        result = prime * result + ((threadName == null) ? 0 : threadName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ThreadMonitored))
            return false;
        ThreadMonitored other = (ThreadMonitored) obj;
        if (threadHashCode != other.threadHashCode)
            return false;
        if (threadId != other.threadId)
            return false;
        if (threadName == null) {
            if (other.threadName != null)
                return false;
        } else if (!threadName.equals(other.threadName))
            return false;
        return true;
    }
}
