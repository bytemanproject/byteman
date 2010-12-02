/*
* JBoss, Home of Professional Open Source
* Copyright 2010 Red Hat and individual contributors
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
package org.jboss.byteman.sample.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

/**
 * A variant of PeriodicHelper which publishes its stats via JMX
 */

public class JMXHelper extends Helper implements DynamicMBean
{
    /************************************************************************/
    /* public API                                                           */
    /************************************************************************/

    /**
     * This is a system property whose value will determine which MBean Server the
     * MBeans should be registered in. If the system property is not defined, the default
     * will be the platform MBeanServer itself. If this system property is set, it will
     * be assumed to be a default domain name of an existing MBeanServer. The existing
     * MBeanServer with that default domain name will be used to house the MBeans. If there
     * is no existing MBeanServer with the given default domain name, one will be created.
     * Note that if this sysprop has the special value "*platform*", then the platform
     * MBeanServer will be used (i.e. it will be as if this sysprop was not set). 
     */
    public final static String SYSPROP_MBEAN_SERVER = "org.jboss.byteman.jmx.mbeanserver";

    /**
     * the default period which the helper will wait for between calls to periodicUpdate in milliseconds. this
     * can be redefined either by overriding defaultPeriod
     */
    public final static long DEFAULT_PERIOD = 10000L;

    /**
     * default number of samples we are willing to store in order to maintain a running count of all previous
     */

    public final static int DEFAULT_SAMPLE_SET_SIZE = 5;

    /**
     * default value for the rmi server host address used by the JMX onnector server
     *
     * used only if an rmi server is required for the JMXConnector
     */

    public final static String DEFAULT_RMI_HOST = "localhost";

    /**
     * default value for the rmi server port used by the JMX connector server
     *
     * used only if an rmi server is required for the JMXConnector
     */

    public final static int DEFAULT_RMI_PORT = 1099;

    /**
     * JMX Url pattern for use  when creating the connector server
     */

    public final static String JMX_URL = "service:jmx:rmi:///jndi/rmi://%1$s:%2$d/jmxrmi" ;

    /**
     * constructor allowing this helper to be used as a helper
     * @param rule
     */
    public JMXHelper(Rule rule) {
        super(rule);
        shutDown = false;
    }

    /************************************************************************/
    /* Helper lifecycle implementation methods used to install a background */
    /* thread when rules employing the helper are installed and to remove   */
    /* the background thread when they are uninstalled                      */
    /************************************************************************/

    /**
     * helper activation method which creates a periodic helper thread to perform periodic calls to the trigger
     * method. should only be called when synchronized on PeriodicHelper.class.
     */
    public static void activated()
    {
        if (theHelper == null) {
            theHelper = new JMXHelper(null);
            theHelper.start();
        }
    }

    /**
     * helper deactivation method which shuts down the periodic helper thread. will only be called when
     * synchronized on PeriodicHelper.class
     */
    public static void deactivated()
    {
        if (theHelper != null) {
            theHelper.shutdown();
            theHelper = null;
        }
    }

    /**
     * Byteman does not guarantee to deactivate the helper at shutdown. However, we need to be sure that
     * a deactivate happens if we are using the connector server to avoid RMI problems. So, we register
     * a shutdown hook when this helper class gets loaded. We don't bother about deregistering it inside
     * deactivate and then reregistering it next time we activate and so on. We can safely do so because
     * deactivated is idempotent.
     */
    static {
        Thread thread = new Thread("JMXHelper Shutdown Hook Deactivation Thread") {
            public void run()
            {
                synchronized(JMXHelper.class)
                {
                    JMXHelper.deactivated();
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(thread);
    }

    /************************************************************************/
    /* methods exposed by the helper class for the benefit of a rule set,   */
    /* allowing it to specify:                                              */
    /* the sampling period                                                  */
    /* the keys and value types of the counters used for sampling           */
    /* the number of sample sets to combine when computing RATE and MEAN    */
    /* whether to create a JMX RMI Connector server to expose MBean info    */
    /* if so what host and port name to use for the connection              */
    /************************************************************************/

    /**
     * method called by the helper thread when it is activated to obtain the initial sample period.
     * this is provided so that a rule set can inject an initial value to be used a the sample
     * period. it should return a time interval in milliseconds.
     * @return
     */
    private long samplePeriod()
    {
        return DEFAULT_PERIOD;
    }

    /**
     * method called by the helper thread when it is activated to obtain the number of samples
     * over which counter rates or counter means should be averaged. this is provided so that
     * a rule set can inject an initial value to be used a the sample period. it should return
     * a positive integer ibetween 1 and 10.
     * @return
     */
    private int sampleSetSize()
    {
        return DEFAULT_SAMPLE_SET_SIZE;
    }

    /**
     * method called once by the helper thread when it is activated to obtain the list of keys identifying
     * counters which are to be displayed by the helper mbean. this is provided so that a rule set
     * inject a rule which creates and returns a value identifying the counters the rule set is
     * collecting.
     * @return
     */
    private KeyInfo keyInfo()
    {
        // if the rule set does not generate a keyinfo value then we return this one to indicate
        // that something is missing.
        String[] keyNames = new String[1];
        keyNames[0] = "No counters defined";
        return new KeyInfo("Byteman Periodic Statistics", keyNames);
    }

    /**
     * method called once by the helper thread when it is activated to decide whether to start up a
     * JMX RMI Connector Service. this is provided so that a rule set can inject a rule which returns
     * true to enable startup. this method returns false which means it is disabled by default.
     * @return
     */
    private boolean rmiServerRequired()
    {
        return false;
    }

    /**
     * method called once by the helper thread when it is activated if rmiServerrequired returns true.
     * this is provided so that a rule set can inject a rule which returns a host name to  use for
     * the connection. this method returns localhost as the default value.
     * @return
     */
    private String rmiHost()
    {
        return DEFAULT_RMI_HOST;
    }

    /**
     * method called once by the helper thread when it is activated if rmiServerrequired returns true.
     * this is provided so that a rule set can inject a rule which returns a port to use for
     * the connection. this method returns "1234" as the default value.
     * @return
     */
    private int rmiPort()
    {
        return DEFAULT_RMI_PORT;
    }

    /************************************************************************/
    /* DynamicMBean implementation                                          */
    /************************************************************************/

    public Object getAttribute(String attribute)
            throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        if (attribute.equals("period")) {
            return getPeriodSecs();
        }

        if (attribute.equals("samples")) {
            return getPeriodSecs();
        }

        int pos = attribute.lastIndexOf(" : ");
        String keyname = attribute.substring(0,pos);
        int keyType;
        if (attribute.endsWith(" : rate")) {
            keyType = KeyInfo.KEY_TYPE_RATE;
        } else if (attribute.endsWith(" : mean")) {
            keyType = KeyInfo.KEY_TYPE_MEAN;
        } else {
            keyType = KeyInfo.KEY_TYPE_CUMULATIVE;
        }
        String[] keyNames = keyInfo.getKeyNames();
        int[] keyTypes = keyInfo().getKeyTypes();
        int keyCount = keyInfo.getKeyCount();
        for (int i = 0; i < keyCount; i++) {
            if (keyNames[i].equals(keyname) && keyTypes[i] == keyType) {
                return getValue(i);
            }
        }
        throw new AttributeNotFoundException("JMXHelper : not expecting get for attribute " + attribute);
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (attribute.getName().equals("period")) {
            int value = ((Integer)attribute.getValue()).intValue();
            setPeriodSecs(value);
        } else {
            throw new InvalidAttributeValueException("JMXHelper : not expecting set call for attribute " + attribute.getName());
        }
    }

    public AttributeList getAttributes(String[] attributes) {
        AttributeList list = new AttributeList();
        for (String name : attributes) {
            try {
                Object value = getAttribute(name);
                if (value != null) {
                    list.add(new Attribute(name, value));
                }
            } catch (AttributeNotFoundException e) {
                // ignore
            } catch (MBeanException e) {
                // ignore
            } catch (ReflectionException e) {
                // ignore
            }
        }
        return list;
    }

    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList list = new AttributeList();
        for (Attribute attribute : attributes.asList()) {
            try {
                setAttribute(attribute);
                list.add(getAttribute(attribute.getName()));
            } catch (AttributeNotFoundException e) {
                // ignore
            } catch (InvalidAttributeValueException e) {
                // ignore
            } catch (MBeanException e) {
                // ignore
            } catch (ReflectionException e) {
                // ignore
            }
        }
        return list;
    }

    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        if (!"reset".equals(actionName)) {
            throw new MBeanException(null, "JMXHelper : not expecting invoke call for action " + actionName);
        }

        if (params.length != 0) {
            throw new MBeanException(null, "JMXHelper : not expecting arguments for action " + actionName);
        }

        synchronized (this) {
            String[] keyNames = keyInfo.getKeyNames();
            int keyCount = keyInfo.getKeyCount();
            // reset time stamps

            for (int i = 0; i < sampleSetSizePlusOne; i++) {
                timeStamps[i][START_TIME] = timeStamps[i][END_TIME] = 0;
            }

            timeStamps[0][START_TIME] = System.currentTimeMillis();

            ringIndex = 0;

            for (int i = 0; i < keyCount; i++) {
                String keyName = keyNames[i];
                readCounter(keyName, true);
                for (int j = 0; j < sampleSetSizePlusOne; j++) {
                    seriesValues[j][i] = 0;
                }
                counterValues[i] = 0;
            }
        }

        return null;
    }

    public MBeanInfo getMBeanInfo() {
        String label =  keyInfo().getLabel();
        int keyCount = keyInfo.getKeyCount();
        String[] keyNames = keyInfo.getKeyNames();
        int[] keyTypes = keyInfo.getKeyTypes();
        String[] keyLabels = keyInfo.getKeyLabels();
        String className = getClass().getName();
        MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[keyInfo.getKeyCount() + 2];
        MBeanConstructorInfo[] constructors = null;
        MBeanOperationInfo[] operations = new MBeanOperationInfo[1];
        MBeanNotificationInfo[] notifications = null;

        attributes[0] = new MBeanAttributeInfo("period",
                "java.lang.Integer",
                "Sample Period",
                true,
                true,
                false);

        attributes[1] = new MBeanAttributeInfo("samples",
                "java.lang.Integer",
                "Sample Set Size",
                true,
                false,
                false);

        for (int i = 0; i < keyCount; i++) {
            String type;
            String descriptor;
            if (keyTypes[i] == KeyInfo.KEY_TYPE_CUMULATIVE) {
                type = "java.lang.Integer";
                descriptor = " : total";
            } else if (keyTypes[i] == KeyInfo.KEY_TYPE_RATE) {
                type = "java.lang.Float";
                descriptor = " : rate";
            } else {
                type = "java.lang.Float";
                descriptor = " : mean";
            }
            attributes[i + 2] = new MBeanAttributeInfo(keyNames[i] + descriptor,
                type,
                keyLabels[i],
                true,
                false,
                false);
        }

        operations[0] = new MBeanOperationInfo("reset", "zero all counters", null, "void", MBeanOperationInfo.ACTION);

        return new MBeanInfo(className, label, attributes, constructors, operations, notifications);
    }


    /************************************************************************/
    /* private implementation                                               */
    /************************************************************************/

    /**
     * singleton instance holding the current periodic helper
     */

    private static JMXHelper theHelper = null;

    /**
     * handle on the current helper thread
     */

    private static PeriodicHelperThread theHelperThread = null;

    /**
     * an mbean server for registering the mbean
     */

    private static MBeanServer mbeanServer = null;

    /**
     * a connector server providing RMI access to the mbean server
     */
    
    private static JMXConnectorServer connectorServer = null;

    /**
     * flag used to control shutdown
     */
    private boolean shutDown;

    /**
     * the interval between wakeups for the helper thread
     */
    private long period;

    /**
     * the number of samples collected plus one. this allows for the fact that one of the sample
     * slots will be live and so will not contain a valid end time.
     */
    private int sampleSetSizePlusOne;

    /**
     * the key information identifying the counters being sampled and the type of information they provide
     */
    private KeyInfo keyInfo;

    /**
     * array storing sampled counter values dimension 1 is keyCount
     */
    private int[] counterValues;

    /**
     * array storing previously sampled counter values dimension 1 is RING_SIZE dimension 2 is keyCount
     */
    private int[][] seriesValues;

    /**
     * array storing timestamps for previously sampled values dimension 1 is RING_SIZE dimension 2 is 2
     */
    private long[][] timeStamps;

    /**
     * index into first dimension of timestamp array containing start time
     */
    private final static int START_TIME =  0;
    /**
     * index into second dimension of timestamp array containing end time
     */
    private final static int END_TIME =  1;

    /**
     * ring buffer index identifying next
     */

    private int ringIndex;

    /**
     * fetch the current sample period in milliseconds
     */
    private long getPeriodMillisecs()
    {
        return period;
    }

    /**
     * update the current sample period
     * @param period the new period in milliseconds which must be greater than one second
     */
    private void setPeriodMillisecs(long period)
    {
        if (period <  1000L) {
            throw new IllegalArgumentException("Period must be greater than 1 second!");
        }
        synchronized (this) {
            this.period = period;
            notify();
        }
    }

    /**
     * update the current sample set size
     * @param period the new period in milliseconds which must be greater than one second
     */
    private void setSampleSetSize(int sampleSetSize)
    {
        if (sampleSetSize < 1) {
            throw new IllegalArgumentException("Sample set size must be greater than 1!");
        }
        if (sampleSetSize > 10) {
            throw new IllegalArgumentException("Sample set size must be no greater than 10!");
        }
        this.sampleSetSizePlusOne = sampleSetSize;
    }

    /**
     * fetch the info describing the counter keys and types
     * @return
     */
    private KeyInfo getKeyInfo()
    {
        return keyInfo;
    }

    /**
     * assign the info describing the counter keys and types
     * @param keyInfo
     */
    private void setKeyInfo(KeyInfo keyInfo)
    {
        this.keyInfo = keyInfo;

        // initialise the counter arrays according to the size

        int keyCount = keyInfo.getKeyCount();
        ringIndex = 0;
        counterValues = new int[keyCount];
        seriesValues = new int[sampleSetSizePlusOne][];
        for (int i = 0; i < sampleSetSizePlusOne; i++) {
            seriesValues[i] = new int[keyCount];
        }
        timeStamps = new long[sampleSetSizePlusOne][];
        for (int i = 0; i < sampleSetSizePlusOne; i++) {
            timeStamps[i] = new long[2];
            timeStamps[i] = new long[2];
        }
        timeStamps[ringIndex][START_TIME] = System.currentTimeMillis();
    }

    /**
     * method called in activate to create and run the shutdown thread.  will only be called when synchronized
     * on PeriodicHelper.class
     */
    private void start()
    {
        theHelperThread = new PeriodicHelperThread();
        theHelperThread.start();
    }

    /**
     * method called in deactivate to shutdown the helper thread. will only be called when synchronized on
     * PeriodicHelper.class
     */
    private void shutdown()
    {
        synchronized (this) {
            shutDown = true;
            this.notify();
        }
        try {
            theHelperThread.join();
        } catch (InterruptedException e) {
            // ignore -- should never happen
        }
        theHelperThread = null;
    }

    /**
     * a getter called when the helper is activated which computes the mbean server to use
     */

    private static MBeanServer getMBeanServer()
    {
        // If the sysprop is not defined, default to the platform MBeanServer.
        // If it is defined, it is the name of the MBS' default domain name so
        // look for an MBeanServer that already has that default domain name and
        // if exists, use it, otherwise create a new one.
        // Note that if the sysprop is defined and has the special value "*platform*"
        // the platform MBeanServer will be used.

        MBeanServer mbsToReturn = null;
        String mbeanServerDomainToLookFor = System.getProperty(SYSPROP_MBEAN_SERVER);
        if (mbeanServerDomainToLookFor == null || "*platform*".equals(mbeanServerDomainToLookFor)) {
            mbsToReturn = ManagementFactory.getPlatformMBeanServer();
        } else {
            ArrayList<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
            if (mbeanServers != null) {
                for (MBeanServer mbs : mbeanServers ) {
                    if (mbeanServerDomainToLookFor.equals(mbs.getDefaultDomain())) {
                        mbsToReturn = mbs;
                        break;
                    }
                }
            }
            
            if (mbsToReturn == null) {
                mbsToReturn = MBeanServerFactory.createMBeanServer(mbeanServerDomainToLookFor);
            }
        }

        return mbsToReturn;
    }

    /**
     * called by the helper thread when at startup. this calls the trigger methods which the rule set
     * should short circuit to return information used to parameterise operation of the dynamic MBean
     */
    private void initialise()
    {
        // enable triggering and then install the MBean
        setTriggering(true);
        setPeriodMillisecs(samplePeriod());
        setSampleSetSize(sampleSetSize());
        setKeyInfo(keyInfo());
        mbeanServer = getMBeanServer();
        try {
            mbeanServer.registerMBean(this, ObjectName.getInstance("org.jboss.byteman.sample.jmx:type=PeriodicStats"));
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace(System.out);
        } catch (MBeanRegistrationException e) {
            e.printStackTrace(System.out);
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace(System.out);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace(System.out);
        }
        // start a JMX RMI server if  it is needed

        boolean isRmiRequired = rmiServerRequired();
        if (isRmiRequired) {
            String host = rmiHost();
            int port = rmiPort();
            if (port > 0) {
                try {
                    JMXServiceURL url = new JMXServiceURL(String.format( JMX_URL, host, port));
                    connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbeanServer);
                    connectorServer.start();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
        }

        setTriggering(false);
    }

    /**
     * this gets called by the helper thread when it starts and calls the trigger methods which
     * the rule set can use to provide the information which parameterises operation of the MBean
     */
    private void cleanup()
    {
        if (connectorServer != null) {
            try {
                connectorServer.stop();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
            connectorServer = null;
        }
        try {
            mbeanServer.unregisterMBean(ObjectName.getInstance("org.jboss.byteman.sample.jmx:type=PeriodicStats"));
        } catch (InstanceNotFoundException e) {
            e.printStackTrace(System.out);
        } catch (MBeanRegistrationException e) {
            e.printStackTrace(System.out);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace(System.out);
        }
        mbeanServer = null;
    }

    /**
     * method called by the periodic helper thread to wait between calls to the trigger method
     */

    private boolean doWait()
    {
        synchronized(this) {
            if (!shutDown) {
                try {
                    wait(period);
                } catch (InterruptedException e) {
                    // ignore -- should never happen
                }
            }
            return !shutDown;
        }
    }

    /**
     * getter for period used by the MBean code
     *
     * @return
     */
    private int getPeriodSecs()
    {
        return (int) (period / 1000L);
    }

    /**
     * setter for period used by the MBean code
     * @param period
     */
    private void setPeriodSecs(int period)
    {
        this.period = ((long)period) * 1000L;
    }

    /**
     * method called at regular intervals by the periodic helper thread to trigger sampling and
     * publishing of counters.
     */

    private synchronized void periodicUpdate()
    {
        int keyCount = keyInfo.getKeyCount();
        String[] keyNames = keyInfo.getKeyNames();
        int[] keyTypes = keyInfo.getKeyTypes();
        long timestamp = System.currentTimeMillis();

        // collect the sample data and then update the DynamicMBean
        for (int i = 0; i < keyCount; i++) {
            int counterValue = readCounter(keyNames[i], false);
            if (keyTypes[i] != KeyInfo.KEY_TYPE_CUMULATIVE) {
                // copy difference in value into next free slot in ring
                seriesValues[ringIndex][i] = counterValue - counterValues[i];
            }
            counterValues[i] = counterValue;
        }
        // update timestamp slots and increment index
        timeStamps[ringIndex][END_TIME] = timestamp;
        ringIndex = (ringIndex + 1) % sampleSetSizePlusOne;
        timeStamps[ringIndex][START_TIME] = timestamp;
        timeStamps[ringIndex][END_TIME] = 0;
    }

    /**
     * getter for counter values used by MBean code
     */

    public synchronized Object getValue(int idx)
    {
        int keyType = keyInfo.getKeyTypes()[idx];
        switch (keyType) {
            case KeyInfo.KEY_TYPE_CUMULATIVE:
            default:
                return counterValues[idx];
            case KeyInfo.KEY_TYPE_RATE:
            {
                long start = Long.MAX_VALUE;
                long end = 0;
                float sum = 0;
                // sum as many values as we have time intervals for
                for (int i = 0; i < sampleSetSizePlusOne; i++) {
                    long nextStart = timeStamps[i][START_TIME];
                    long nextEnd = timeStamps[i][END_TIME];
                    if (nextStart != 0 && nextEnd != 0) {
                       sum += seriesValues[i][idx];
                        if (start > nextStart) {
                            start = nextStart;
                        }
                        if (end < nextEnd) {
                            end = nextEnd;
                        }
                    }
                }
                if (end == 0) {
                    // no complete samples yet
                    return 0.0;
                } else {
                    // result is number per second
                    return ((sum * 1000) / (end - start));
                }
            }
            case KeyInfo.KEY_TYPE_MEAN:
            {
                long start = Long.MAX_VALUE;
                long end = 0;
                float sum = 0;
                long totalTime = 0;
                // sum as many values as we have time intervals for
                for (int i = 0; i < sampleSetSizePlusOne; i++) {
                    long nextStart = timeStamps[i][START_TIME];
                    long nextEnd = timeStamps[i][END_TIME];
                    if (nextStart != 0 && nextEnd != 0) {
                        // the intervals may not all be equal so we
                        // weight the amount added to the sum by the time interval
                        long interval = (nextEnd - nextStart);
                        sum += (seriesValues[i][idx] * interval);
                        totalTime += interval;
                    }
                }
                if (totalTime == 0) {
                    // no complete samples yet
                    return 0.0;
                } else {
                    // now divide the weighted sum by the total time and the
                    // result is a running mean value per sample
                    return (sum / totalTime);
                }
            }
        }
    }

    // auxiliary classes

    /**
     * background thread which regularly samples the counters updated by the rule set and
     * updates the counters accordingly
     */
    private class PeriodicHelperThread extends Thread
    {
        public PeriodicHelperThread()
        {
            super("Periodic Helper Thread");
        }

        public void run()
        {
            // we only want this thread to be able to trigger rules at very specific points
            setTriggering(false);
            // allow the rule set to initialise theJMX helper sample period and key info
            // and register am MX bean
            initialise();
            // if we got through that with no exceptions then we must ensure we clean up
            try {
                while (doWait()) {
                    periodicUpdate();
                }
            } finally {
                cleanup();
            }
        }
    }
}
