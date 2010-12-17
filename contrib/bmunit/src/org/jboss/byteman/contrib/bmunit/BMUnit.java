package org.jboss.byteman.contrib.bmunit;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import org.jboss.byteman.agent.install.Install;
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
     * the directory in which to look for rule scripts. this can be configured by setting system property
     * org.jboss.byteman.contrib.bmunit.load.directory
     */
    private static String loadDirectory = initLoadDirectory();

    /**
     * hash table used to maintain association between test cases and rule files
     */
    private static HashMap<BMTestCase, String> fileTable =new HashMap<BMTestCase, String>();

    /**
     * computes the load directory from system property org.jboss.byteman.contrib.bmunit.load.directory
     * or defaults  it to "."
     * @return the load directory
     */
    private static String initLoadDirectory()
    {
        String dir = System.getProperty("org.jboss.byteman.contrib.bmunit.script.directory");
        if (dir == null || dir.length() == 0) {
            dir = ".";
        }

        return dir;
    }

    /**
     * we try loading the agent once only
     */
    private static boolean triedLoad = false;

    /**
     * load the agent into this JVM if not already loaded. unfortunately this can only be done if we have
     * the pid of the current process and we cannot get that in a portable way
     */
    private static void loadAgent() throws Exception
    {
        String[] properties = new String[0];

        // if we can get a proper pid on Linux  we use it
        int pid = getPid();
        pid =  0;

        try {
            if (pid != 0) {
                Install.install(Integer.toString(pid), true, null, 0, properties);
            } else {
                Install.install("org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner", true, null, 0, properties);
            }
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
        try {
            loadAgent();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".bmr" or, failing that, ".txt" for the file extension
     * @param name the name of the unit test
     * @throws Exception
     */
    public static void loadTestScript(BMTestCase testCase) throws Exception
    {
        String name = testCase.getName();
        String className = testCase.getClass().getName().replace('.', File.separatorChar);
        int index = className.lastIndexOf(File.separatorChar);
        String bareClassName = (index < 0 ? null : className.substring(index  + 1));

        // first try for rule file based on test name
        String filename = loadDirectory + File.separator + name + ".bmr";
        boolean found = false;
        File file = new File(filename);
        if (!file.exists()) {
            // try .txt extension for backwards compatibility
            filename = loadDirectory + File.separator + name + ".txt";
            file = new File(filename);
        }
        if (!file.exists()) {
            // ok, now try for rule file based on class and test name
            filename = loadDirectory + File.separator + className + "-" + name + ".bmr";
            file = new File(filename);
        }
        if (!file.exists()) {
            // try .txt extension for backwards compatibility
            filename = loadDirectory + File.separator + className + ".txt";
            file = new File(filename);
        }
        if (!file.exists()) {
            // ok, try using just class name
            filename = loadDirectory + File.separator + className + ".bmr";
            file = new File(filename);
        }
        if (!file.exists()) {
            // try .txt extension for backwards compatibility
            filename = loadDirectory + File.separator + className + ".txt";
            file = new File(filename);
        }
        if (bareClassName != null) {
            if (!file.exists()) {
                // ok, final try using just base class name with no package qualifier
                filename = loadDirectory + File.separator + bareClassName + ".bmr";
                file = new File(filename);
            }
            if (!file.exists()) {
                // try .txt extension for backwards compatibility
                filename = loadDirectory + File.separator + bareClassName + ".txt";
                file = new File(filename);
            }
        }
        if (!file.exists()) {
            throw new FileNotFoundException("Rule file not found for Byteman test case " + name); 
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read Byteman rule file " + filename);
        }
        Submit submit = new Submit();
        List<String> files =  new ArrayList<String>();
        files.add(filename);
        submit.addRulesFromFiles(files);
        fileTable.put(testCase, filename);
    }

    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".bmr" or, failing that, ".txt" for the file extension
     * @param name the name of the unit test
     * @throws Exception
     */
    public static void unloadTestScript(BMTestCase testCase) throws Exception
    {
        String filename = fileTable.remove(testCase);
        if (filename == null) {
            throw new FileNotFoundException("Rule file not found for Byteman test case " + testCase.getName());
        }
        Submit submit = new Submit();
        List<String> files =  new ArrayList<String>();
        files.add(filename);
        submit.deleteRulesFromFiles(files);
    }
}
