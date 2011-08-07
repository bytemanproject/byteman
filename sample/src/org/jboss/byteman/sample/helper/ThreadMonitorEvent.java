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

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Scott stark (sstark@redhat.com) (C) 2011 Red Hat Inc.
 * @version $Revision:$
 */
public class ThreadMonitorEvent implements Serializable {
    private static final long serialVersionUID = 1;

    private String event;
    private String threadName;
    private String runnableClass;
    private String[] stack;
    private String fullStack;

    @ConstructorProperties({"event", "threadName", "stack"})
    public ThreadMonitorEvent(String event, String threadName, Collection<String> stack, String fullStack) {
        this.event = event;
        this.threadName = threadName;
        this.stack = new String[stack.size()];
        stack.toArray(this.stack);
        this.fullStack = fullStack;
    }

    public String getEvent() {
        return event;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getRunnableClass() {
        return runnableClass;
    }
    public void setRunnableClass(String runnableClass) {
        this.runnableClass = runnableClass;
    }

    public String[] getStack() {
        return stack;
    }

    public String getFullStack() {
        return fullStack;
    }
}
