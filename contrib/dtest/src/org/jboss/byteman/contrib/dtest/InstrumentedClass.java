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

import java.rmi.RemoteException;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * InstrumentedClass instances serve two purposes:
 *
 * Internally to the framework they provide a communication endpoint for
 * receiving information from the remote, Byteman instrumented code execution.
 *
 * To the framework user, they provide utility methods for verifying expectations relating
 * to that remote execution e.g. the number of method calls made.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-05
 */
public class InstrumentedClass implements RemoteInterface
{
    private static final Integer STATIC_INSTANCE_ID = new Integer(-1);

    private final String className;
    private final Map<Integer, InstrumentedInstance> instrumentedInstances = new HashMap<Integer, InstrumentedInstance>();

    InstrumentedClass(String className)
    {
        this.className = className;
    }

    /**
     * Receiving side of the remote communication between the test code and the BytemanTestHelper.
     *
     * @param methodName the method name that was traced.
     * @param args the arguments to the method invocation, in String form.
     * @throws RemoteException in case of communication failure.
     * @see BytemanTestHelper#remoteTrace(String, String, Object[])
     */
    @Override
    public void trace(String methodName, Object[] args) throws RemoteException
    {
        Integer objectId = (Integer)args[0];
        if(objectId == null)
        {
            objectId = STATIC_INSTANCE_ID;
        }
        InstrumentedInstance instrumentedInstance = instrumentedInstances.get(objectId);
        if(instrumentedInstance == null)
        {
            instrumentedInstance = new InstrumentedInstance(className, objectId);
            instrumentedInstances.put(objectId, instrumentedInstance);
        }

        Object[] innerArgs = new Object[args.length-1];
        System.arraycopy(args, 1, innerArgs, 0, innerArgs.length);

        instrumentedInstance.addMethodTrace(methodName, innerArgs);
    }

    /**
     * Returns the set of known instances of the class.
     *
     * @return a Set of Objects representing instances of the class.
     */
    public Set<InstrumentedInstance> getInstances()
    {
        return new HashSet<InstrumentedInstance>(instrumentedInstances.values());
    }

    /**
     * Checks that the number of known, distinct object instances of this class is as stated.
     *
     * @param count the expected number of instances of the class.
     */
    public void assertKnownInstances(int count)
    {
        assertEquals(null, count, instrumentedInstances.size());
    }

    /**
     * Checks that the number of known invocations of the given method falls within the specified
     *  range for each known instances of the class.
     *
     * @param message the message to print in case of assertion failure.
     * @param methodName the method name to look for.
     * @param callCount the expected range for the invocation count.
     */
    public void assertMethodCallCount(String message, String methodName, CallCount callCount)
    {
        for(InstrumentedInstance instance : getInstances())
        {
            instance.assertMethodCallCount(message, methodName, callCount);
        }
    }

    /**
     * Checks that the given method has been called at least once on each known instance of the class.
     * Uses junit internally, hence expect the normal exception throwing in case of failure.
     *
     * @param methodName the method name to look for.
     */
    public void assertMethodCalled(String methodName)
    {
        assertMethodCallCount(null, methodName, new CallCount(1, Integer.MAX_VALUE));
    }

    /**
     * Checks that the given method has not been seen to be called on any known instance of the class. 
     * Uses junit internally, hence expect the normal exception throwing in case of failure.
     *
     * @param methodName the method name to look for.
     */
    public void assertMethodNotCalled(String methodName)
    {
        assertMethodCallCount(null, methodName, new CallCount(0, 0));
    }
}
