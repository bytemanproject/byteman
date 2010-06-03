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

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class containing functions used by Byteman rules created by the dtest framework.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-05
 */
public class BytemanTestHelper extends Helper
{
    public static final String RMIREGISTRY_PORT_PROPERTY_NAME = "org.jboss.byteman.contrib.dtest.rmiregistry.port";

    private static Map<String, Map<Object, Integer>> targetInstances = new HashMap<String, Map<Object, Integer>>();
    private static Registry registry;

    public BytemanTestHelper(Rule rule) throws Exception
    {
        super(rule);

        if(registry == null)
        {
            String propertyValue = System.getProperty(RMIREGISTRY_PORT_PROPERTY_NAME);
            int rmiPort = Integer.parseInt(propertyValue);
            registry = LocateRegistry.getRegistry(rmiPort);
        }
    }

    /**
     * Print a message during rule execution. n.b. this always returns true which means
     * it can be invoked during condition execution
     *
     * @param text the message to be printed as trace output
     * @param object0 an object, typically the rule target, whose String representation will be added to the output.
     * @return true
     */
    public boolean debug(String text, Object object0)
    {
        super.debug(text+" "+object0.toString()); // arg, recursion on toString rule!
        return true;
    }

    /**
     * Send trace information to a remote listener.
     * A Rule will normally be installed to invoke this on entry to each method of interest.
     *
     * @param className the name of the instrumented, i.e. traced, class
     * @param methodName the name of the traced method.
     * @param dollarStar
     * @throws Exception
     * @see Instrumentor#instrumentClass, InstrumentedClass#trace
     */
    public void remoteTrace(String className, String methodName, Object[] dollarStar) throws Exception
    {
        Map<Object, Integer> knownInstancesOfType = targetInstances.get(className);
        if(knownInstancesOfType == null)
        {
            knownInstancesOfType = new HashMap<Object, Integer>();
            targetInstances.put(className, knownInstancesOfType);
        }

        Object targetObject = dollarStar[0];

        Integer objectId = knownInstancesOfType.get(targetObject);
        if(objectId == null)
        {
            objectId = knownInstancesOfType.size();
            knownInstancesOfType.put(targetObject, objectId);
        }

        Object[] args = convertForRemoting(dollarStar);
        args[0] = objectId;

        RemoteInterface server = (RemoteInterface)registry.lookup(className);
        server.trace(methodName, args);
    }

    /**
     * Convert Objects to their String representation for transmission over RMI.
     *
     * @param input An Array of Objects, possibly including nulls.
     * @return An Array of length equivalent to the input, containing
     *   corresponding nulls or the String representation of the input.
     */
    private Object[] convertForRemoting(Object[] input)
    {
        Object[] output = new Object[input.length];
        for(int i  = 0; i < input.length; i++)
        {
            output[i] = (input[i] == null ? null : input[i].toString());
        }
        return output;
    }
}
