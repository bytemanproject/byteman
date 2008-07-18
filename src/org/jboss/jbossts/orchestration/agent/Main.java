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
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * agent class supplied at JVM startup to install orchestration package bytecode transformer
 */
public class Main {
    public static void premain(String args, Instrumentation inst) {
        if (args != null) {
            // args are supplied eparated by ',' characters
            String[] argsArray = args.split(",");
            // the only args we accept are extra jar files to be added to the boot path or scanned for rules
            for (String arg : argsArray) {
                if (arg.startsWith(BOOT_PREFIX)) {
                    bootJarPaths.add(arg.substring(BOOT_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(RULE_PREFIX)) {
                    ruleJarPaths.add(arg.substring(RULE_PREFIX.length(), arg.length()));
                } else {
                    System.err.println("org.jboss.jbossts.orchestration.agent.Main:\n" +
                            "  illegal agent argument : " + arg + "\n" +
                            "  valid arguments are boot:<path-to-jar> or rule:<path-to-jar>");
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
            }
        }

        // look up rules in any rule jars

        for (String ruleJarPath : ruleJarPaths) {
            try {
                JarFile jarFile = new JarFile(new File(ruleJarPath));
                processRules(jarFile);
            } catch (IOException ioe) {
                System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to open rule jar file : " + ruleJarPath);
            }
        }

        // install an instance of Transformer to instrument the bytecode

        inst.addTransformer(new Transformer(inst, ruleClasses));
    }

    private static void processRules(JarFile jarFile)
    {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[] { new URL("file:" + jarFile.getName()) });
            Enumeration<JarEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String entryName = jarEntry.getName();

                if (entryName.endsWith(CLASS_FILE_SUFFIX)) {
                    // try to load the rule class
                    int classNameLength = entryName.length() - CLASS_FILE_SUFFIX.length();
                    String className = entryName.substring(0, classNameLength).replaceAll("/", ".");
                    try {
                        Class clazz = classLoader.loadClass(className);
                        Annotation a = clazz.getAnnotation(EventHandlerClass.class);
                        if (a != null) {
                            ruleClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to load class " + className + " from : " + jarFile.getName());
                    }
                }
            }
        } catch (MalformedURLException mue) {
            System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to load classes from : " + jarFile.getName());
        }
    }

    /**
     * prefix used to specify boot jar argument for agent
     */
    private static final String BOOT_PREFIX = "boot:";

    /**
     * prefix used to specify rulejar argument for agent
     */

    private static final String RULE_PREFIX = "rule:";

    /**
     * suffix found on end of .class files (doh :-)
     */

    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * list of paths to extra bootstrap jars supplied on command line
     */
    private static List<String> bootJarPaths = new ArrayList<String>();

    /**
     * list of paths to rule jars supplied on command line
     */
    private static List<String> ruleJarPaths = new ArrayList<String>();

    /**
     * list of classes annotated with methods annotated with event handler annotations
     */
    private static List<Class> ruleClasses = new ArrayList<Class>();
}
