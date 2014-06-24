/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat and individual contributors
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

package org.jboss.byteman.contrib.bmunit;

import com.sun.tools.attach.AgentInitializationException;
import org.jboss.byteman.agent.install.Install;
import org.jboss.byteman.agent.install.VMInfo;
import org.jboss.byteman.agent.submit.Submit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Properties;

/**
 * class used to model a specific BMUnit configuration
 */
public class BMUnitConfigState
{
    private static BMUnitConfigState currentConfigState = null;

    /**
     * a global configuration state used to preserve the configuration
     * associated with a test class should the BMUnitRunner temporarily
     * reconfigure the state for a specific test method.
     */
    private static BMUnitConfigState shadowConfigState = null;

    /**
     * a default configuration state used when a test class does not specify
     * a configuration state. this state is initialised using the state
     * associated with the first test class processed during the test run.
     * it is also the state used to define parameters which configure autoload
     * of the Byteman agent.
     */
    public static BMUnitConfigState defaultConfigState = null;

    private String agentHost;
    private int agentPort;
    private String loadDirectory;
    private String resourceLoadDirectory;
    private boolean allowConfigUpdate;
    private boolean verbose;
    private boolean debug;
    private boolean bmunitVerbose;
    private boolean inhibitAgentLoad;
    private boolean policy;
    private BMUnitConfigState previous;

    private BMUnitConfigState(BMUnitConfig config, BMUnitConfigState previous) throws Exception
    {
        this.previous = previous;
        boolean enforce = config.enforce();
        // we don't need to use the same host and port as the default config
        // although using something different is a tad weird
        // if we have an empty host name and no previous setting then
        // check for an env setting
        agentHost = config.agentHost();
        if (previous == null && (agentHost == null || agentHost.length() == 0)) {
                agentHost = initHost();
        }
        // same story for agentPort as for agentHost
        String agentPortString = config.agentPort();
        if (previous == null && (agentPortString == null || agentPortString.length() == 0)) {
                agentPort = initPort();
                if (agentPort < 0) {
                    agentPort = 0;
                }
        } else {
            try {
                agentPort = Integer.valueOf(agentPortString);
            } catch (NumberFormatException e) {
                agentPort = 0;
            }
            if (agentPort < 0) {
                agentPort = 0;
            }
        }
        // the load dir and resource load dir can be reset but if they are
        // empty and we have no previous setting then check for an env setting
        loadDirectory = config.loadDirectory();
        if (previous == null && (loadDirectory== null || loadDirectory.length() == 0)) {
                loadDirectory = initDefaultLoadDirectory();
        }
        resourceLoadDirectory = config.resourceLoadDirectory();
        if (previous == null && (resourceLoadDirectory== null || resourceLoadDirectory.length() == 0)) {
            resourceLoadDirectory = initDefaultResourceLoadDirectory();
        }
        // whatever previous setting we have for config update cannot be changed
        // so we need to check for enforcing mode
        allowConfigUpdate = config.allowAgentConfigUpdate();
        if (previous != null && allowConfigUpdate != previous.allowConfigUpdate) {
            if (enforce) {
                String message = "BMUnit configuration specifies incompatible settings for allowAgentConfigUpdate";
                throw new Exception(message);
            }
        }
        // if the verbose setting differs from the previous and we cannot update the config
        // then we need to check for enforcing
        // if there is no previous setting check and verbose is not set then allow  for
        // an environment setting
        verbose = config.verbose();
        if (previous != null && verbose != previous.verbose && !allowConfigUpdate) {
            if (enforce) {
                String message = "BMUnit configuration specifies incompatible settings for verbose";
                throw new Exception(message);
            }
        }
        // if we have no previous config allow the env setting to override
        if (previous == null && !verbose) {
            verbose = initVerbose();
        }
        // if the debug setting differs from the previous and we cannot update the config
        // then we need to check for enforcing
        debug = config.debug();
        if (previous != null && debug != previous.debug && !allowConfigUpdate) {
            if (enforce) {
                String message = "BMUnit configuration specifies incompatible settings for debug";
                throw new Exception(message);
            }
        }
        // if we have no previous config allow the env setting to override
        if (previous == null && !debug) {
            debug = initDebug();
        }
        // we can always change the bmunit verbose setting
        // if there is no previus setting and the config does nto set it
        // then allow for an environment setting
        bmunitVerbose = config.bmunitVerbose();
         if (previous == null && !bmunitVerbose) {
             bmunitVerbose = initBMUnitVerbose();
         }
        // if the initial config set inhibitAgentLoad then we stick with it
        // if there is no previous setting check and inhibitAgentLoad is not set
        // then allow  for an environment setting
        inhibitAgentLoad = config.inhibitAgentLoad();
        if (previous != null && inhibitAgentLoad != previous.inhibitAgentLoad) {
            if (enforce) {
                String message = "BMUnit configuration specifies incompatible settings for inhibitAgentLoad";
                throw new Exception(message);
            }
        }
        if (previous == null && !inhibitAgentLoad) {
            inhibitAgentLoad = System.getProperty(AGENT_INHIBIT) != null;
        }
        // whatever previous setting we have for policy cannot be changed
        // so we need to check for enforcing mode
        policy = config.policy();
        if (previous != null && previous.policy != policy) {
            if (enforce) {
                String message = "BMUnit configuration specifies incompatible settings for inhibitAgentLoad";
                throw new Exception(message);
            }
        }
    }

    private BMUnitConfigState(BMUnitConfigState previous) throws Exception
    {
        this.previous = previous;
        if (previous != null ) {
            agentHost = previous.agentHost;
            agentPort = previous.agentPort;
            loadDirectory = previous.loadDirectory;
            resourceLoadDirectory = previous.resourceLoadDirectory;
            allowConfigUpdate = previous.allowConfigUpdate;
            verbose = previous.verbose;
            bmunitVerbose = previous.bmunitVerbose;
            inhibitAgentLoad = previous.inhibitAgentLoad;
            policy = previous.policy;
        } else {
            agentHost = initHost();
            agentPort = initPort();
            loadDirectory = initDefaultLoadDirectory();
            resourceLoadDirectory = initDefaultResourceLoadDirectory();
            // default is to allow sys property updates
            allowConfigUpdate = true;
            verbose = initVerbose();
            debug = initDebug();
            bmunitVerbose = initBMUnitVerbose();
            inhibitAgentLoad = System.getProperty(AGENT_INHIBIT) != null;
            policy = initPolicy();
     }
    }

    private static void uploadAgentProperties() throws Exception {
        // if any Byteman config changes have been requested and
        // are allowed upload all reconfigured system property
        // settings to the agent.
        //
        // for the moment that means just the Byteman verbose
        // and debug properties
        BMUnitConfigState previousConfigState = currentConfigState.previous;
        if (previousConfigState != null && previousConfigState.isAllowConfigUpdate()) {
            if (currentConfigState.verbose != previousConfigState.verbose ||
                    currentConfigState.debug != previousConfigState.debug) {
                Submit submit = new Submit(currentConfigState.getHost(), currentConfigState.getPort());
                Properties properties = new Properties();
                properties.setProperty("org.jboss.byteman.verbose", (currentConfigState.verbose ? "true" : ""));
                properties.setProperty("org.jboss.byteman.debug", (currentConfigState.debug? "true" : ""));
                submit.setSystemProperties(properties);
            }
        }
    }

    private static void resetAgentProperties() throws Exception {
        // if we upoaded any reconfigured system property settings
        // to the agent then revert them
        //
        // for the moment that means just the Byteman verbose
        // property
        BMUnitConfigState previousConfigState = currentConfigState.previous;
        if (previousConfigState != null && previousConfigState.isAllowConfigUpdate()) {
            if (currentConfigState.verbose != previousConfigState.verbose ||
                    currentConfigState.debug != previousConfigState.debug) {
                Submit submit = new Submit(currentConfigState.getHost(), currentConfigState.getPort());
                Properties properties = new Properties();
                properties.setProperty("org.jboss.byteman.verbose", (previousConfigState.verbose ? "true" : ""));
                properties.setProperty("org.jboss.byteman.debug", (previousConfigState.debug? "true" : ""));
                submit.setSystemProperties(properties);
            }
        }
    }

    /**
     * load the agent into this JVM if not already loaded. unfortunately this can only be done if we have
     * the pid of the current process and we cannot get that in a portable way
     */
    private void loadAgent() throws Exception
    {
        LinkedList<String> propList = new LinkedList<String>();
        String id = null;

        // if we can get a proper pid on Linux  we use it
        int pid = getPid();
        // uncomment to force lookup by name even on Linux
        // pid = 0;

        if (pid > 0) {
            id = Integer.toString(pid);
        } else {
            /*
            VMInfo[] vmInfo = Install.availableVMs();
            // search for a JVM which looks like it is running a JUnit test
            // and install the agent into that JVM
            // it could be run from ant or maven or some other process!!
            for (int i = 0; i < vmInfo.length; i++) {
                String displayName = vmInfo[i].getDisplayName();
                if (displayName.startsWith("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner ")) {
                    // a JUnit test forked by ant
                    id = vmInfo[i].getId();
                    break;
                } else if (displayName.startsWith("org.apache.tools.ant.launch.Launcher ")) {
                    // a JUnit test run directly by ant
                    id = vmInfo[i].getId();
                    break;
                } else {
                    // TODO -- identify a forked maven test and then a test run directly or any other mode of running
                }
                */
            // alternative strategy which will  work everywhere
            // set a unique system property and then check each available VM until we find it
            String prop = "org.jboss.byteman.contrib.bmunit.agent.unique";
            String unique = Long.toHexString(System.currentTimeMillis());
            System.setProperty(prop, unique);
            VMInfo[] vmInfo = Install.availableVMs();
            for (int i = 0; i < vmInfo.length; i++) {
                String nextId = vmInfo[i].getId();
                String value = Install.getSystemProperty(nextId, prop);
                if (unique.equals(value)) {
                    id = nextId;
                    break;
                }
            }
            // last ditch effort to obtain pid on Windows where the availableVMs list may be empty
            if (id == null) {
                String processName = ManagementFactory.getRuntimeMXBean().getName();
                if (processName != null && processName.contains("@")) {
                    id = processName.substring(0, processName.indexOf("@"));
                    // check we actually have an integer
                    try {
                        Integer.parseInt(id);
                        // well, it's a number so now check it identifies the current VM
                        String value = Install.getSystemProperty(id, prop);
                        if (!unique.equals(value)) {
                            // nope, not the right process
                            id = null;
                        }
                    } catch (NumberFormatException e) {
                        // nope, not a number
                        id = null;
                    }
                }
            }
            // make sure we found a process
            if (id == null) {
                throw new Exception("BMUnit : Unable to identify test JVM process during agent load");
            }
        }

        try {
            if (isBMUnitVerbose()) {
                System.out.println("BMUnit : loading agent id = " + id);
            }
            // if we have any system properties to configure then do so now
            if (isAllowConfigUpdate()) {
                propList.add("org.jboss.byteman.allow.config.update=true");
            }
            if (isVerbose()) {
                propList.add("org.jboss.byteman.verbose=true");
            }
            if (isDebug()) {
                propList.add("org.jboss.byteman.debug=true");
            }
            String[] properties = new String[propList.size()];
            Install.install(id, true, isPolicy(), getHost(), getPort(), propList.toArray(properties));
        } catch (AgentInitializationException e) {
            // this probably indicates that the agent is already installed
        }
    }

    /**
     * return the integer process id of the current  process. n.b. only works on Linux.
     * @return
     */
    private static int getPid()
    {
        File file = new File("/proc/self/stat");
        if (!file.exists()  || !file.canRead()) {
            return 0;
        }

        FileInputStream fis = null;
        int  pid = 0;

        try {
            fis = new FileInputStream(file);
            byte[] bytes = new byte[10];
            StringBuilder builder = new StringBuilder();
            fis.read(bytes);
            for (int i = 0; i < 10; i++) {
                char c = (char)bytes[i];
                if (Character.isDigit(c)) {
                    builder.append(c);
                } else {
                    break;
                }
            }
            pid = Integer.valueOf(builder.toString());
        } catch (Exception e) {
            // ignore
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    // ignore
                }
            }
        }
        return pid;
    }

    /**
     * System property which identifies the directory from which to
     * start searching for rule script. If unset the current working
     * directory of the test is used.
     */
    public final static String LOAD_DIRECTORY = "org.jboss.byteman.contrib.bmunit.load.directory";

    /**
     * System property which identifies the resource load directory
     * from which to start searching for rule script.
     */
    public final static String RESOURCE_LOAD_DIRECTORY = "org.jboss.byteman.contrib.bmunit.resource.load.directory";

    /**
     * System property specifying the port to be used when starting the agent and when submitting
     * rules to it. You can normally just use the default port.
     */
    public final static String AGENT_PORT = "org.jboss.byteman.contrib.bmunit.agent.port";

    /**
     * System property specifying the host to be used when starting the agent and when submitting
     * rules to it. You can normally just use the default host.
     */
    public final static String AGENT_HOST = "org.jboss.byteman.contrib.bmunit.agent.host";

    /**
     * System property specifying whether to set a security policy when loading the agent.
     */
    public final static String AGENT_POLICY = "org.jboss.byteman.contrib.bmunit.agent.policy";
    /**
     * System property which inhibits automatic loading of the agent. If you set this then you have to load
     * the agent yourself using the Install API or ensure JUnit loads by forking a JVM and passing
     * the necessary -javaagent options on the command line. You may also want to set this if you you have
     * loaded the agent into a remote service in another JVM driven by your unit test.
     */
    public final static String AGENT_INHIBIT = "org.jboss.byteman.contrib.bmunit.agent.inhibit";

    /**
     * System property which enables tracing of Byteman activity
     */
    public final static String BYTEMAN_VERBOSE = "org.jboss.byteman.verbose";

    /**
     * System property which enables printing of Byteman rule debug statements
     */
    public final static String BYTEMAN_DEBUG = "org.jboss.byteman.debug";

    /**
     * System property which enables tracing of bmunit activity
     */
    public final static String BMUNIT_VERBOSE = "org.jboss.byteman.contrib.bmunit.verbose";

    /**
     * this is only provided for backward compatibility in case some app was
     * using this constant string to confiure the required property.
     */
    public final static String VERBOSE = BMUNIT_VERBOSE;

    /**
     * install the configuration for a specific test class, possibly
     * also adopting it as the default configuration
     *
     * this method is not thread-safe. BMUnit assumes that only one
     * JUnit/TestNG test is run at a time.
     * @param config
     * @param testClass
     * @throws Exception
     */
    public static void pushConfigurationState(BMUnitConfig config, Class<?> testClass) throws Exception
    {
        // current config should be null when this is called
        if (currentConfigState != null) {
            throw new Exception("BMUnit test class configuration pushed without prior pop!");
        }
        if (config != null) {
            currentConfigState = new BMUnitConfigState(config, defaultConfigState);
        } else {
            currentConfigState = new BMUnitConfigState(defaultConfigState);
        }
        // if this is the first config we have seen then use it as the default
        // and auto load the agent if needed
        if (defaultConfigState == null) {
            defaultConfigState = currentConfigState;
            if (!defaultConfigState.inhibitAgentLoad) {
                defaultConfigState.loadAgent();
            }
        } else {
            // upload any properties changed by this config
            uploadAgentProperties();

        }
    }

    public static void popConfigurationState(Class<?> testClass) throws Exception
    {
        // current config should be non-null and shadow config null when this is called
        if (currentConfigState == null) {
            // should not happen
            throw new Exception("BMUnit test class configuration popped without prior push!");
        }
        if (shadowConfigState != null) {
            // should not happen
            throw new Exception("BMUnit test class configuration popped without popping method configuration!");
        }
        // reset any properties set by this config
        resetAgentProperties();

        currentConfigState = null;
    }

    public static void pushConfigurationState(BMUnitConfig config, Method method) throws Exception
    {
        // current config should be non-null and shadow config null when this is called
        if (currentConfigState == null) {
            // should not happen
            throw new Exception("BMUnit method configuration pushed without prior test configuration push!");
        }
        if (shadowConfigState != null) {
            // should not happen
            throw new Exception("BMUnit method configuration pushed without prior method configuration pop!");
        }
        shadowConfigState = currentConfigState;
        if (config != null) {
            currentConfigState = new BMUnitConfigState(config, currentConfigState);
            // agent properties may need updating
            uploadAgentProperties();
        } else {
            // agent properties will not have changed
            currentConfigState = new BMUnitConfigState(currentConfigState);
        }
    }

    public static void popConfigurationState(Method method) throws Exception
    {
        // shadow config should be non-null when this is called
        if (shadowConfigState == null) {
            // should not happen
            throw new Exception("BMUnit method configuration pushed without prior method configuration pop!");
        }
        // agent properties may need resetting
        resetAgentProperties();

        currentConfigState = shadowConfigState;
        shadowConfigState = null;
    }


    public static void resetConfigurationState(Method method) throws Exception
    {
        if (shadowConfigState != null) {
            popConfigurationState(method);
        }
    }

    public static void resetConfigurationState(Class<?> testClass) throws Exception
    {
        if (currentConfigState != null) {
            popConfigurationState(testClass);
        }
    }
  /**
     * the global configuration state which configures the operation
     * of BMUnit when running tests in a specific class or a method
     * in that class. this value is set by BMUnitRunner before it
     * starts running the tests in a test class. it may be reset
     * temporarily by BMUnitRunner before executing a specific test
     * method but should then be set back to the previous
     * configuration.
     */
    public static BMUnitConfigState getCurrentConfigState() {
        return currentConfigState;
    }

    public BMUnitConfigState currentConfigState()
    {
        return currentConfigState;
    }

    /**
     * getter for currently configured agent host setting
     * @return
     */
    public String getHost() {
        // agentHost can be overridden so don't look up previous
        return agentHost;
    }

    /**
     * getter for currently configured agent port setting
     * @return
     */
    public int getPort() {
        // agentPort can be overridden so don't look up previous
        // unlesss current port is 0
        if (agentPort == 0 && previous != null) {
            return previous.getPort();
        }
        return agentPort;
    }

    /**
     * getter for currently configured load directory setting
     * @return
     */
    public String getLoadDirectory() {
        // loadDirectory can be overridden so don't look up previous
        // unless it is empty or null
        if ((loadDirectory != null || loadDirectory.length() == 0) && previous != null) {
            return previous.getLoadDirectory();
        }
        return loadDirectory;
    }

    /**
     * getter for currently configured resource directory setting
     * @return
     */
    public String getResourceLoadDirectory() {
        // loadDirectory can be overridden so don't look up previous
        // unless it is empty or null
        if ((resourceLoadDirectory != null || resourceLoadDirectory.length() == 0) && previous != null) {
            return previous.getResourceLoadDirectory();
        }
        return resourceLoadDirectory;
    }

    /**
     * smart getter for currently configured allowAgentConfigUpdate setting
     * which redirects through to the previous config to ensure that
     * we employ the setting used for the initial agent load
     * @return
     */
    public boolean isAllowConfigUpdate() {
        // allowAgentConfigUpdate cannot be overridden so check for previous setting
        if (previous != null) {
            return previous.isAllowConfigUpdate();
        }
        return allowConfigUpdate;
    }

    /**
     * smart getter for currently configured Byteman verbose setting
     * which redirects through to the previous config if config
     * updates are not allowed but otherwise returns the currently
     * configured Byteman verbose setting
     * @return
     */
    public boolean isVerbose() {
        // verbose can be overridden if allowAgentConfigUpdate is true
        if (previous == null || previous.isAllowConfigUpdate()) {
            return verbose;
        } else {
            return previous.isVerbose();
        }
    }

    /**
     * smart getter for currently configured Byteman debug setting
     * which redirects through to the previous config if config
     * updates are not allowed but otherwise returns the currently
     * configured Byteman debug setting
     * @return
     */
    public boolean isDebug() {
        // debug can be overridden if allowAgentConfigUpdate is true
        if (previous == null || previous.isAllowConfigUpdate()) {
            return debug;
        } else {
            return previous.isDebug();
        }
    }

    /**
     * getter for currently configured BMUnit verbose setting
     * @return
     */
    public boolean isBMUnitVerbose() {
        // bmunitVerbose can be overridden so don't look up previous
        return bmunitVerbose;
    }

    /**
     * smart getter for currently configured inhibitAgentLoad setting
     * which redirects through to the previous config to ensure that
     * we employ the setting used for the initial agent load
     * @return
     */
    public boolean isInhibitAgentLoad() {
        // inhibitAgentLoad cannot be overridden so check for previous setting
        if (previous != null) {
            return previous.isInhibitAgentLoad();
        }
        return inhibitAgentLoad;
    }

    /**
     * smart getter for currently configured policy setting
     * which redirects through to the previous config to ensure that
     * we employ the setting used for the initial agent load
     * @return
     */
    public boolean isPolicy() {
        // isPolicy cannot be overridden so check for previous setting
        if (previous != null) {
            return previous.isPolicy();
        }
        return policy;
    }

    /**
     * return the String configured for the agent host or null if it
     * was not configured
     */
    private static String initHost()
    {
        String host = System.getProperty(AGENT_HOST);
        return (host != null ? host : "");
    }

    /**
     * return the integer port configured for the agent port or 0 if
     * it was not configured or was misconfigured
     */
    private static int initPort()
    {
        String portString = System.getProperty(AGENT_PORT);
        return (portString == null ? 0 : Integer.valueOf(portString));
    }

    /**
     * computes the default load directory from system property
     * org.jboss.byteman.contrib.bmunit.load.directory or defaults it
     * to ""
     * @return the load directory
     */
    private static String initDefaultLoadDirectory()
    {
        String dir = System.getProperty(LOAD_DIRECTORY);
        if (dir == null) {
            return "";
        }
        return dir;
    }

    /**
     * computes the default resource load directory from system
     * property
     * org.jboss.byteman.contrib.bmunit.resource.load.directory or
     * defaults it to the load directory
     * @return the resource load directory
     */
    private static String initDefaultResourceLoadDirectory()
    {
        String dir = System.getProperty(RESOURCE_LOAD_DIRECTORY);
        if (dir == null) {
            dir = System.getProperty(LOAD_DIRECTORY);
            if (dir == null) {
                dir = "";
            }
        }
        int l = dir.length();
        if (l > 0 && dir.charAt(l) != '/') {
            dir = dir + "/";
        }
        return dir;
    }

    private static boolean initVerbose()
    {
        return System.getProperty(BYTEMAN_VERBOSE) != null;
    }

    private static boolean initDebug()
    {
        return System.getProperty(BYTEMAN_DEBUG) != null;
    }

    private static boolean initBMUnitVerbose()
    {
        return System.getProperty(BMUNIT_VERBOSE) != null;
    }

    /**
     * test whether a security policy should be set for agent codewhen the agent is installed
     */
    private static boolean initPolicy()
    {
        String policyString= System.getProperty(AGENT_POLICY);
        return (policyString == null ? false : Boolean.valueOf(policyString));
    }
}
