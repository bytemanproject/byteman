/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2010,
 * @author JBoss, by Red Hat.
 */
package org.jboss.byteman.contrib.dtest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * InstrumentedInstance instances serve two purposes:
 *
 * Internally to the framework they provide storage of traced method invocation information
 *  received from the remote execution via BytemanTestHelper->InstrumentedClass->this.
 *
 * To the framework user, they provide utility methods for verifying expectations relating
 * to that remote execution e.g. the number of method calls made.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-05
 */
public class InstrumentedInstance
{
    private final String className;
    private final Integer instanceId;
    private final List<String> methodTraces = new ArrayList<String>();

    InstrumentedInstance(String className, Integer instanceId)
    {
        this.className = className;
        this.instanceId = instanceId;
    }

    /**
     * Record a method invocation.
     *
     * @param methodName the method that was traced.
     * @param args the parameters to the method call.
     */
    void addMethodTrace(String methodName, Object[] args)
    {
        methodTraces.add(methodName);
    }

    /**
     * Returns the number of known invocations of the given method upon the object instance.
     *
     * @param methodName the method name to look for.
     * @return the number of invocations seen.
     */
    public int getInvocationCount(String methodName)
    {
        int count = 0;
        for(String name : methodTraces)
        {
            if(methodName.equals(name))
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks that the number of known invocations of the given method falls
     *  within the specified range.
     *
     * @param message the message to print in case of assertion failure.
     * @param methodName the method name to look for.
     * @param callCount the expected range for the invocation count.
     */
    public void assertMethodCallCount(String message, String methodName, CallCount callCount)
    {
        int invocationCount = getInvocationCount(methodName);
        assertTrue((message == null ? "" : message)+" - required minimum call count "+callCount.getMin()+" but was "+invocationCount, callCount.getMin() <= invocationCount);
        assertTrue((message == null ? "" : message)+" - required maximum call count "+callCount.getMax()+" but was "+invocationCount, callCount.getMax() >= invocationCount);
    }
}
