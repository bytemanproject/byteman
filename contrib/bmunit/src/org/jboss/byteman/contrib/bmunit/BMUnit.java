package org.jboss.byteman.contrib.bmunit;

import com.sun.tools.attach.AgentInitializationException;
import org.jboss.byteman.agent.install.Install;
import org.jboss.byteman.agent.install.VMInfo;
import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Byteman Unit test manager class which provides support for loading and  unloading scripts.
 * This version assumes loads the agent as needed (unless inhibited -- see below) using System
 * properties to control what hostname and port it uses for the socket. Other system properties
 * can be used to configure operation of the load/unload operations.
 */
public class BMUnit
{
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
     * System property which enables tracing of bmunit activity
     */
    public final static String VERBOSE = "org.jboss.byteman.contrib.bmunit.verbose";

    /**
     * flag which controls whether or not verbose trace output is enabled
     */
    private final static boolean verbose = (System.getProperty(VERBOSE) != null);

    /**
     * the file separator character used by the native file system
     */
    private static char fs = File.separatorChar;

    /**
     * the directory in which to look for rule scripts. this can be
     * configured by setting system property
     * org.jboss.byteman.contrib.bmunit.load.directory
     */
    private static String defaultLoadDirectory = initDefaultLoadDirectory();

    /**
     * the resource path used to look for rule scripts. this can be
     * configured by setting system property
     * org.jboss.byteman.contrib.bmunit.resource.load.directory
     *
     * if that property is unset then it will be set from property
     * org.jboss.byteman.contrib.bmunit.load.directory
     */
    private static String defaultResourceLoadDirectory = initDefaultResourceLoadDirectory();

    /**
     * hash table used to maintain association between test cases and rule files
     */
    private static HashMap<String, String> fileTable = new HashMap<String, String>();

    /**
     * Host used to connect to the agent.
     */
    private static String host = initHost();

    /**
     * Port used to connect to the agent.
     */
    private static int port = initPort();

    /**
     * flag determining whether to set a security policy at agent load
     */
    private static boolean policy = initPolicy();

    /**
     * return the String configured for the agent host or null if it
     * was not configured
     */
    private static String initHost()
    {
	return System.getProperty(AGENT_HOST);
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
     * test whether a security policy should be set for agent codewhen the agent is installed
     */
    private static boolean initPolicy()
    {
	String policyString= System.getProperty(AGENT_POLICY);
	return (policyString == null ? false : Boolean.valueOf(policyString));
    }

   /**
     * getter for the host name used to communicate with the agent
     */
    public static String getHost()
    {
	return host;
    }

    /**
     * getter for the port used to communicate with the agent
     */
    public static int getPort()
    {
	return port;
    }

    /**
     * getter for the security policy setting
     */
    public static boolean getPolicy()
    {
	return policy;
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
        return normalize(dir, true);
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

    /**
     * transform the supplied directory string if necessary to employ
     * the file separator appropriate to the current file system,
     * including a separator at the end if requested and not present.
     *
     * BMUnit assumes that all supplied paths are specified in Unix
     * format i.e. with a '/' separator. So, transformation of '/' to
     * '\' is only performed on Windows systems.
     * 
     * @dir the directory string to be checked
     * @endWithSeparator true if the returned directory must terminate
     * wiht a file separator
     */
    private static String normalize(String dir, boolean endWithSeparator)
    {
        int l = dir.length();
        if (l == 0) {
            // don't worry about appending a separator to an empty
            // string as endWithSeparator is only used for the
            // relative load path and "" is used to specify a relative
            // path
            return dir;
        }

        if (fs == '\\' && dir.indexOf('/', 0) >= 0) {
            dir = dir.replace('/', '\\');
        }

        if (endWithSeparator && dir.charAt(l - 1) != fs) {
            StringBuilder sb = new StringBuilder(l+1);
            sb.append(dir);
            sb.append(fs);
            return sb.toString();
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
            if (verbose) {
                System.out.println("BMUNit : loading agent id = " + id);
            }
            Install.install(id, true, getPolicy(), getHost(), getPort(), properties);
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
     * @param clazz the classname of the unit test
     * @param testName the name of the unit test method
     * @param dir the directory in which the scripts are located
     * @throws Exception
     */
    public static void loadScriptFile(Class<?> clazz, String testName, String dir) throws Exception
    {
        // turn '.' characters into file separator characters
        String className = clazz.getName();
        if (testName ==  null) {
            testName = "";
        }
        String key = className + "#"  + testName;
        className = className.replace('.', '/');
        int index = className.lastIndexOf('/');
        // we can also use the class name without package qualifier
        String bareClassName = (index < 0 ? null : className.substring(index  + 1));

        String filename = null;
        File file = null;
        // first try for rule file based on test name or class name  plus test name
        filename=findScript(dir,
                            testName,
                            className + "-" + testName,
                            className, bareClassName,
                            bareClassName + "-" + testName);
        if(filename != null)
            file=new File(filename);

        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Rule file not found for Byteman test case " + key);
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read Byteman rule file " + filename);
        }
        Submit submit = new Submit(getHost(), getPort());
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
        Submit submit = new Submit(getHost(), getPort());
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
     * @param testname the test name
     * @param scriptText the text of the rule or rules contained in the script
     */
    public static void loadScriptText(Class<?> clazz, String testname, String scriptText) throws Exception
    {
        String className = clazz.getName();
        if (testname ==  null) {
            testname = "";
        }
        String key = className + "+"  + testname;
        fileTable.put(key, scriptText);
        Submit submit = new Submit(getHost(), getPort());
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
        Submit submit = new Submit(getHost(), getPort());
        if (verbose) {
            System.out.println("BMUNit : unloading text script = " + key);
        }
        List<ScriptText> scripts = new ArrayList<ScriptText>();
        ScriptText script = new ScriptText(key, scriptText);
        scripts.add(script);
        submit.deleteScripts(scripts);
    }

    /**
     * Tries to find dir/name in the working directory. If not found, tries to add the ".btm", then ".txt" suffixes.
     * If still not found, tries to find the above on the classpath
     * @param dir The name of the directory
     * @param name The file name
     * @return The fully qualified name of the file, or null if not found
     */
    protected static String findScript(String dir, String name) {
        if(name == null) return null;
        String filename=normalize(name, false);
	String resourceName = name;
        if(dir != null && dir.length() > 0) {
            filename=normalize(dir, true) + filename;
            resourceName=dir + "/" + resourceName;
	} else {
            // n.b. defaults either are "" or end with correct separator
            filename = defaultLoadDirectory + filename;
            resourceName = defaultResourceLoadDirectory + filename;
        }

        final String[] filenames={filename, filename + ".btm", filename + ".txt"};
        final String[] resourceNames={resourceName, resourceName + ".btm", resourceName + ".txt"};

        for(String fname: filenames) {
            File file=new File(fname);
            if(file.exists() && file.isFile())
                return fname;
        }

        for(String rname: resourceNames) {
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }
            URL resource=loader.getResource(rname);
            if(resource != null) {
                File file=new File(resource.getFile());
                if(file.exists() && file.isFile())
                    return resource.getFile();
            }
        }
        
        return null;
    }

    protected static String findScript(String dir, String ... names) {
        for(String name: names) {
            String filename=findScript(dir, name);
            if(filename != null)
                return filename;
        }
        return null;
    }
}
