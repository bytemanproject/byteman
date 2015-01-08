package org.jboss.byteman.contrib.bmunit;

import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Byteman Unit test manager class which provides support for loading and  unloading scripts.
 * This version assumes loads the agent as needed (unless inhibited -- see below) using System
 * properties to control what hostname and port it uses for the socket. Other system properties
 * can be used to configure operation of the load/unload operations.
 */
public class BMUnit
{
    /**
     * the file separator character used by the native file system
     */
    private static char fs = File.separatorChar;

    /**
     * hash table used to maintain association between test cases and rule files
     */
    private static HashMap<String, String> fileTable = new HashMap<String, String>();

    /**
     * getter for the allowAgentConfigUpdate setting
     * @return the allowAgentConfigUpdate setting
     */
    public static boolean isAllowConfigUpdate()
    {
        return BMUnitConfigState.getCurrentConfigState().isAllowConfigUpdate();
    }
    /**
     * getter for the Byteman verbose setting
     * @return the Byteman verbose setting
     */
    public static boolean isVerbose()
    {
        return BMUnitConfigState.getCurrentConfigState().isVerbose();
    }
    /**
     * getter for the Byteman debug setting
     * @return the Byteman debug setting
     */
    public static boolean isDebug()
    {
        return BMUnitConfigState.getCurrentConfigState().isDebug();
    }
    /**
     * getter for the BMUnit verbose setting
     * @return the BMUnit verbose setting
     */
    public static boolean isBMUnitVerbose()
    {
        return BMUnitConfigState.getCurrentConfigState().isBMUnitVerbose();
    }
    /**
     * getter for the load directory
     * @return the load directory
     */
    public static String getLoadDirectory()
    {
        return BMUnitConfigState.getCurrentConfigState().getLoadDirectory();
    }
    /**
     * getter for the resource load directory
     * @return the resource load directory
     */
    public static String getResourceLoadDirectory()
    {
        return BMUnitConfigState.getCurrentConfigState().getResourceLoadDirectory();
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
     * getter for the host name used to communicate with the agent
     * @return the host name
     */
    public static String getHost()
    {
        return BMUnitConfigState.getCurrentConfigState().getHost();
    }

    /**
     * getter for the port used to communicate with the agent
     * @return the port
     */
    public static int getPort()
    {
        return BMUnitConfigState.getCurrentConfigState().getPort();
    }

    /**
     * getter for the security policy setting
     * @return the security policy setting
     */
    public static boolean getPolicy()
    {
        return BMUnitConfigState.getCurrentConfigState().isPolicy();
    }

    /**
     * loads a script by calling loadScriptFile(clazz, null, dir)
     * @param clazz the test class
     * @param dir the directory to load the script from
     * @throws Exception if the script cannot be loaded
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
     * @throws Exception if the script cannot be loaded
     */
    public static void loadScriptFile(Class<?> clazz, String testName, String dir) throws Exception
    {
        if (BMUnitConfigState.getCurrentConfigState() == null) {
            throw new Exception("BMUnit : load script file requested with no current configuration " +
                    clazz.getName() +
                    (testName != null ? " " + testName : "") +
                    (dir != null ? " " + dir : ""));
        }
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
            if (isBMUnitVerbose()) {
                System.out.println("BMUnit : failed to find file = " + filename);
            }
            throw new FileNotFoundException("Rule file not found for Byteman test case " + key);
        }
        if (!file.canRead()) {
            if (isBMUnitVerbose()) {
                System.out.println("BMUnit : cannot read file = " + filename);
            }
            throw new IOException("Cannot read Byteman rule file " + filename);
        }
        Submit submit = new Submit(getHost(), getPort());
        List<String> files =  new ArrayList<String>();
        files.add(filename);
        if (isBMUnitVerbose()) {
            System.out.println("BMUnit : loading file script = " + filename);
        }
        submit.addRulesFromFiles(files);
        fileTable.put(key, filename);
    }

    /**
     * loads a script from the load directory using the name of a unit test as the root name for the script
     * file and ".btm" or, failing that, ".txt" for the file extension
     * @param clazz the test class
     * @param testName the test name
     * @throws Exception if the script cannot be unloaded
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
        if (isBMUnitVerbose()) {
            System.out.println("BMUnit : unloading file script = " + filename);
        }
        submit.deleteRulesFromFiles(files);
    }

    /**
     * loads a script supplied as a text String rather than via a file on disk
     * @param clazz the test class
     * @param testname the test name
     * @param scriptText the text of the rule or rules contained in the script
     * @throws Exception if the script text cannot be loaded
     */
    public static void loadScriptText(Class<?> clazz, String testname, String scriptText) throws Exception
    {
        if (BMUnitConfigState.getCurrentConfigState() == null) {
            throw new Exception("BMUnit : load script file requested with no current configuration " +
                    clazz.getName() +
                    (testname != null ? " " + testname : ""));
        }
        String className = clazz.getName();
        if (testname ==  null) {
            testname = "";
        }
        String key = className + "+"  + testname;
        fileTable.put(key, scriptText);
        Submit submit = new Submit(getHost(), getPort());
        if (isBMUnitVerbose()) {
            System.out.println("BMUnit : loading text script = " + key);
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
     * @throws Exception if the script text cannot be unloaded
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
        if (isBMUnitVerbose()) {
            System.out.println("BMUnit : unloading text script = " + key);
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
            filename = normalize(getLoadDirectory(), true) + filename;
            resourceName = normalize(getResourceLoadDirectory(), true) + filename;
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
