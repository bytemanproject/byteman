/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
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
 */
package org.jboss.byteman.contrib.bmunit;

import com.sun.tools.attach.AgentInitializationException;
import org.jboss.byteman.agent.install.Install;
import org.jboss.byteman.agent.install.VMInfo;
import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Byteman Unit test manager class which provides support for loading and  unloading scripts.
 * This version assumes loads the agent as needed (unless inhibited -- see below) using System
 * properties to control what hostname and port it uses for the socket. Other system properties
 * can be used to configure operation of the load/unload operations.
 * @author Andrew Dinn (adinn@redhat.com) (C) 2010 Red Hat Inc.
 */
public class BMUnit
{
    /**
     * System property which identifies the directory from which to start searching
     * for rule script. If unset the current working directory of the test is used.
     */
    public final static String LOAD_DIRECTORY = "org.jboss.byteman.contrib.bmunit.script.directory";

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
     * System property which inhibits automatic loading of the agent. If you set this then you have to load
     * the agent yourself using the Install API or ensure JUnit loads by forking a JVM and passing
     * the necessary -javaagent options on the command line. You may also want to set this if you you have
     * loaded the agent into a remote service in another JVM driven by your unit test.
     */
    public final static String AGENT_INHIBIT = "org.jboss.byteman.contrib.bmunit.agent.inhibit";

    /**
     * System property which enables tracing of bmunit activity
     */
    public final static String VERBOSE = "org.jboss.byteman.contrib.bmunit.verbose";

    /**
     * flag which controls whether or not verbose trace output is enabled
     */
    private final static boolean verbose = (SecurityActions.getSystemProperty(VERBOSE, "false") != null);

    /**
     * the directory in which to look for rule scripts. this can be configured by setting system property
     * org.jboss.byteman.contrib.bmunit.load.directory
     */
    private static String defaultLoadDirectory = initDefaultLoadDirectory();

    /**
     * hash table used to maintain association between test cases and rule files
     */
    private static HashMap<String, String> fileTable = new HashMap<String, String>();
    private static Install agent;

    /**
     * computes the default load directory from system property org.jboss.byteman.contrib.bmunit.load.directory
     * or defaults  it to "."
     * @return the load directory
     */
    private static String initDefaultLoadDirectory()
    {
        String dir = System.getProperty(LOAD_DIRECTORY);
        if (dir == null || dir.length() == 0) {
            dir = ".";
        }

        return dir;
    }

    /**
     * load the agent into this JVM if not already loaded. unfortunately this can only be done if we have
     * the pid of the current process and we cannot get that in a portable way
     * @param config - the unit test configuration
     */
    public static synchronized void loadAgent(BMUnitConfig config) throws Exception
    {
        if (config.isAgentLoadEnabled() == false) {
           return;
        }

        // Get properties from the config
       String[] properties = Helper.getProperties(config);
        String host = Helper.getAgentHost(config);
        int port = Helper.getAgentPort(config);
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
            // make sure we found a process
            if (id == null) {
                throw new Exception("BMUnit : Unable to identify test JVM process during agent load");
            }
        }

        try {
            if (verbose) {
                System.out.println("BMUNit : loading agent id = " + id);
            }
            agent = Install.install(id, true, host, port, properties);
        } catch (AgentInitializationException e) {
            // this probably indicates that the agent is already installed
        }
    }

   /**
    * 
    * @return
    */
    public static Install getInstalledAgent() {
       return agent;
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

    /* Need more control over when the agent is loaded
    static {
        if (System.getProperty(AGENT_INHIBIT) == null) {
            try {
                loadAgent();
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }
    */

    /**
     * loads a script by calling loadScriptFile(clazz, null, dir)
     * @param clazz the test class
     * @param dir the directory to load the script from
     * @throws Exception
     */
    public static void loadScriptFile(Class<?> clazz, String dir) throws Exception
    {
        loadScriptFile(clazz, null, dir);
    }
    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".btm" or, failing that, ".txt" for the file extension
     * @param testName the name of the unit test
     * @throws Exception
     */
    public static void loadScriptFile(Class<?> clazz, String testName, String dir) throws Exception
    {
        String loadDirectory = dir;
        if (loadDirectory == null) {
            loadDirectory = defaultLoadDirectory;
        }
        // turn '.' characters into file separator characters
        String className = clazz.getName();
        if (testName ==  null) {
            testName = "";
        }
        String key = className + "#"  + testName;
        className = className.replace('.', File.separatorChar);
        int index = className.lastIndexOf(File.separatorChar);
        // we can also use the class name without package qualifier
        String bareClassName = (index < 0 ? null : className.substring(index  + 1));

        String filename = null;
        File file = null;
        // first try for rule file based on test name or class name  plus test name
        if (testName.length() > 0) {
            filename = loadDirectory + File.separator + testName + ".btm";
            file = new File(filename);
            if (!file.exists()) {
                // try .txt extension for backwards compatibility
                filename = loadDirectory + File.separator + testName + ".txt";
                file = new File(filename);
            }

            if (!file.exists()) {
                // ok, now try for rule file based on class and test name
                filename = loadDirectory + File.separator + className + "-" + testName + ".btm";
                file = new File(filename);
            }

            if (!file.exists()) {
                // try .txt extension for backwards compatibility
                filename = loadDirectory + File.separator + className + ".txt";
                file = new File(filename);
            }
        }
        // we may not have a file yet if the testname was null
        if (file == null || !file.exists()) {
            // ok, try using the package qualified classname to locate a directory hierarchy
            filename = loadDirectory + File.separator + className + ".btm";
            file = new File(filename);
        }
        if (!file.exists()) {
            // try .txt extension for backwards compatibility
            filename = loadDirectory + File.separator + className + ".txt";
            file = new File(filename);
        }
        if (!file.exists() && bareClassName != null) {
            // ok, final try using just base class name with no package qualifier
            filename = loadDirectory + File.separator + bareClassName + ".btm";
            file = new File(filename);
            if (!file.exists()) {
                // try .txt extension for backwards compatibility
                filename = loadDirectory + File.separator + bareClassName + ".txt";
                file = new File(filename);
            }
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Rule file not found for Byteman test case " + key);
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read Byteman rule file " + filename);
        }
        Submit submit = new Submit();
        List<String> files =  new ArrayList<String>();
        files.add(filename);
        if (verbose) {
            System.out.println("BMUNit : loading file script = " + filename);
        }
        submit.addRulesFromFiles(files);
        fileTable.put(key, filename);
    }

    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".btm" or, failing that, ".txt" for the file extension
     * @param clazz the test class
     * @param testName the test name
     * @throws Exception
     */
    public static void unloadScriptFile(Class<?> clazz, String testName) throws Exception
    {
        String className = clazz.getName();
        if (testName ==  null) {
            testName = "";
        }
        String key = className + "#"  + testName;
        String filename = fileTable.remove(key);
        if (filename == null) {
            throw new FileNotFoundException("Rule file not found for Byteman test case " + key);
        }
        Submit submit = new Submit();
        List<String> files =  new ArrayList<String>();
        files.add(filename);
        if (verbose) {
            System.out.println("BMUNit : unloading file script = " + filename);
        }
        submit.deleteRulesFromFiles(files);
    }

    /**
     * loads a script supplied as a text String rather than via a file on disk
     * @param clazz the test class
     * @param testName the test name
     * @param scriptText the text of the rule or rules contained in the script
     */
    public static void loadScriptText(Class<?> clazz, String testName, String scriptText) throws Exception
    {
        String className = clazz.getName();
        if (testName ==  null) {
            testName = "";
        }
        String key = className + "+"  + testName;
        fileTable.put(key, scriptText);
        Submit submit = new Submit();
        if (verbose) {
            System.out.println("BMUNit : loading text script = " + key);
            // System.out.println(scriptText);
        }
        List<ScriptText> scripts = new ArrayList<ScriptText>();
        ScriptText script = new ScriptText(key, scriptText);
        scripts.add(script);
        submit.addScripts(scripts);
    }

    /**
     * unloads a script previously supplied as a text String
     * @param clazz the test class
     * @param testName the test name
     */
    public static void unloadScriptText(Class<?> clazz, String testName) throws Exception
    {
        String className = clazz.getName();
        if (testName ==  null) {
            testName = "";
        }
        String key = className + "+"  + testName;
        String scriptText = fileTable.remove(key);
        if (scriptText == null) {
            throw new Exception("Rule script not found " + key);
        }
        Submit submit = new Submit();
        if (verbose) {
            System.out.println("BMUNit : unloading text script = " + key);
        }
        List<ScriptText> scripts = new ArrayList<ScriptText>();
        ScriptText script = new ScriptText(key, scriptText);
        scripts.add(script);
        submit.deleteScripts(scripts);
    }
}
