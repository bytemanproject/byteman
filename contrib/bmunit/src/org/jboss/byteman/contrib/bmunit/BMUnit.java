package org.jboss.byteman.contrib.bmunit;

import com.sun.tools.attach.AgentInitializationException;
import org.jboss.byteman.agent.install.Install;
import org.jboss.byteman.agent.install.VMInfo;
import org.jboss.byteman.agent.submit.Submit;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Byteman Unit test manager class which provides support for loading and  unloading scripts.
 * This version assumes that the agent is loaded in the current JVM and is listening on the default
 * host and port.
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
    private final static boolean verbose = (System.getProperty(VERBOSE) != null);

    /**
     * the directory in which to look for rule scripts. this can be configured by setting system property
     * org.jboss.byteman.contrib.bmunit.load.directory
     */
    private static String defaultLoadDirectory = initDefaultLoadDirectory();

    /**
     * hash table used to maintain association between test cases and rule files
     */
    private static HashMap<String, String> fileTable = new HashMap<String, String>();

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
     */
    private static synchronized void loadAgent() throws Exception
    {
        String[] properties = new String[0];
        String host = System.getProperty(AGENT_HOST);
        String portString = System.getProperty(AGENT_PORT);
        int port = (portString == null ? 0 : Integer.valueOf(portString));
        String id = null;

        // if we can get a proper pid on Linux  we use it
        int pid = getPid();
        // uncomment to force lookup by name even on Linux
        // pid = 0;

        if (pid > 0) {
            id = Integer.toString(pid);
        } else {
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
            Install.install(id, true, host, port, properties);
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

    static {
        if (System.getProperty(AGENT_INHIBIT) == null) {
            try {
                loadAgent();
            } catch(Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * loads a script by calling loadTestScript(clazz, testName, null)
     * @param clazz
     * @param testName
     * @throws Exception
     */
    public static void loadTestScript(Class<?> clazz, String testName) throws Exception
    {
        loadTestScript(clazz, testName, null);
    }
    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".btm" or, failing that, ".txt" for the file extension
     * @param name the name of the unit test
     * @throws Exception
     */
    public static void loadTestScript(Class<?> clazz, String testName, String dir) throws Exception
    {
        String loadDirectory = dir;
        if (dir == null) { 
            dir = defaultLoadDirectory;
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
            System.out.println("BMUNit : loading script = " + filename);
        }
        submit.addRulesFromFiles(files);
        fileTable.put(key, filename);
    }

    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".btm" or, failing that, ".txt" for the file extension
     * @param name the name of the unit test
     * @throws Exception
     */
    public static void unloadTestScript(Class<?> clazz, String testName) throws Exception
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
            System.out.println("BMUNit : unloading script = " + filename);
        }
        submit.deleteRulesFromFiles(files);
    }
}
