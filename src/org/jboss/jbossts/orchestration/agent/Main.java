package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.annotation.EventHandlerClass;

import java.lang.instrument.Instrumentation;
import java.lang.annotation.Annotation;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * agent class supplied at JVM startup to install orchestration package bytecode transformer
 */
public class Main {
    public static void premain(String args, Instrumentation inst)
            throws Exception
    {
        if (args != null) {
            // args are supplied eparated by ',' characters
            String[] argsArray = args.split(",");
            // the only args we accept are extra jar files to be added to the boot path or scanned for rules
            for (String arg : argsArray) {
                if (arg.startsWith(BOOT_PREFIX)) {
                    bootJarPaths.add(arg.substring(BOOT_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(SCRIPT_PREFIX)) {
                    scriptPaths.add(arg.substring(SCRIPT_PREFIX.length(), arg.length()));
                } else {
                    System.err.println("org.jboss.jbossts.orchestration.agent.Main:\n" +
                            "  illegal agent argument : " + arg + "\n" +
                            "  valid arguments are boot:<path-to-jar> or script:<path-to-scriptr>");
                }
            }
        }

        // add any boot jars to the boot class path
        // TODO can only do this when we get to 1.6

        for (String bootJarPath : bootJarPaths) {
            try {
                JarFile jarfile = new JarFile(new File(bootJarPath));
                // inst.appendToBootstrapClassLoaderSearch(jarfile);
            } catch (IOException ioe) {
                System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to open boot jar file : " + bootJarPath);
                throw ioe;
            }
        }

        // look up rules in any script files

        for (String scriptPath : scriptPaths) {
            try {
                System.out.println("processing script file " + scriptPath);

                FileInputStream fis = new FileInputStream(scriptPath);
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);
                String ruleScript = new String(bytes);
                scripts.add(ruleScript);
            } catch (IOException ioe) {
                System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to read rule script file : " + scriptPath);
                throw ioe;
            }
        }

        // install an instance of Transformer to instrument the bytecode

        inst.addTransformer(new Transformer(inst, scriptPaths, scripts));
    }

    /**
     * prefix used to specify boot jar argument for agent
     */
    private static final String BOOT_PREFIX = "boot:";

    /**
     * prefix used to specify script argument for agent
     */

    private static final String SCRIPT_PREFIX = "script:";

    /**
     * list of paths to extra bootstrap jars supplied on command line
     */
    private static List<String> bootJarPaths = new ArrayList<String>();

    /**
     * list of paths to script files supplied on command line
     */
    private static List<String> scriptPaths = new ArrayList<String>();

    /**
     * list of scripts read from script files
     */
    private static List<String> scripts = new ArrayList<String>();
}
