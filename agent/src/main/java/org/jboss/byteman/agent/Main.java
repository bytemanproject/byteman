/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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
package org.jboss.byteman.agent;


import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.Socket;

/**
 * agent class supplied at JVM startup to install byteman package bytecode transformer
 */
public class Main {
    public static boolean firstTime = true;
    public final static String BYTEMAN_PREFIX = "org.jboss.byteman.";

    public static void premain(String args, Instrumentation inst)
            throws Exception
    {
        // guard against the agent being loaded twice
        synchronized (Main.class) {
            if (firstTime) {
                firstTime = false;
            } else {
                throw new Exception("Main : attempting to load Byteman agent more than once");
            }
        }
        boolean allowRedefine = false;

        if (args != null) {
            // args are supplied eparated by ',' characters
            String[] argsArray = args.split(",");
            // we accept extra jar files to be added to the boot/sys classpaths
            // script files to be scanned for rules
            // listener flag which implies use of a retransformer
            for (String arg : argsArray) {
                if (arg.startsWith(BOOT_PREFIX)) {
                    bootJarPaths.add(arg.substring(BOOT_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(SYS_PREFIX)) {
                    sysJarPaths.add(arg.substring(SYS_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(ADDRESS_PREFIX)) {
                    hostname = arg.substring(ADDRESS_PREFIX.length(), arg.length());
                    // setting host name forces listener on
                    allowRedefine = true;
                } else if (arg.startsWith(PORT_PREFIX)) {
                    try {
                        port = Integer.valueOf(arg.substring(PORT_PREFIX.length(), arg.length()));
                        if (port <= 0) {
                            System.err.println("Invalid port specified [" + port + "]");
                            port = null;
                        } else {
                            // setting port forces listener on
                            allowRedefine = true;
                        }
                    } catch (Exception e) {
                        System.err.println("Invalid port specified [" + arg + "]. Cause: " + e);
                    }
                } else if (arg.startsWith(SCRIPT_PREFIX)) {
                    scriptPaths.add(arg.substring(SCRIPT_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(LISTENER_PREFIX)) {
                    String value = arg.substring(LISTENER_PREFIX.length(), arg.length());
                    allowRedefine = Boolean.parseBoolean(value);
                    // clearing listener when port or host is set should be flagged
                    if (!allowRedefine && (hostname != null || port != null)) {
                        System.err.println("listener disabled with host/port set");
                    }
                } else if (arg.startsWith(REDEFINE_PREFIX)) {
                    // this is only for backwards compatibility -- it is the same as listener
                    String value = arg.substring(REDEFINE_PREFIX.length(), arg.length());
                    allowRedefine = Boolean.parseBoolean(value);
                    // clearing listener when port or host is set should be flagged
                    if (!allowRedefine && (hostname != null || port != null)) {
                        System.err.println("listener disabled with host/port set");
                    }
                } else if (arg.startsWith(PROP_PREFIX)) {
                    // this can be used to set byteman properties
                    String prop = arg.substring(PROP_PREFIX.length(), arg.length());
                    String value="";
                    if (prop.startsWith(BYTEMAN_PREFIX)) {
                        int index = prop.indexOf('=');
                        if (index > 0) {
                            // need to split off the value
                            if (index == prop.length() - 1)
                            {
                                // value is empty so just drop the =
                                prop = prop.substring(0, index);
                            } else {
                                value = prop.substring(index + 1);
                                prop = prop.substring(0, index);
                            }
                        }
                        System.out.println("Setting " + prop + "=" + value);
                        System.setProperty(prop, value);
                    } else {
                        System.err.println("Invalid property : " +  prop);
                    }
                } else {
                    System.err.println("org.jboss.byteman.agent.Main:\n" +
                            "  illegal agent argument : " + arg + "\n" +
                            "  valid arguments are boot:<path-to-jar>, sys:<path-to-jar>, script:<path-to-script> or listener:<true-or-false>");
                }
            }
        }

        // add any boot jars to the boot class path

        for (String bootJarPath : bootJarPaths) {
            try {
                JarFile jarfile = new JarFile(new File(bootJarPath));
                inst.appendToBootstrapClassLoaderSearch(jarfile);
            } catch (IOException ioe) {
                System.err.println("org.jboss.byteman.agent.Main: unable to open boot jar file : " + bootJarPath);
                throw ioe;
            }
        }

        // add any sys jars to the system class path

        for (String sysJarPath : sysJarPaths) {
            try {
                JarFile jarfile = new JarFile(new File(sysJarPath));
                inst.appendToSystemClassLoaderSearch(jarfile);
            } catch (IOException ioe) {
                System.err.println("org.jboss.byteman.agent.Main: unable to open system jar file : " + sysJarPath);
                throw ioe;
            }
        }
        // create a socket so we can be sure it is loaded before the transformer gets cerated. otherwise
        // we seem to hit a deadlock when trying to instrument socket

        Socket dummy = new Socket();

        // look up rules in any script files

        for (String scriptPath : scriptPaths) {
            try {
                FileInputStream fis = new FileInputStream(scriptPath);
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);
                String ruleScript = new String(bytes);
                scripts.add(ruleScript);
            } catch (IOException ioe) {
                System.err.println("org.jboss.byteman.agent.Main: unable to read rule script file : " + scriptPath);
                throw ioe;
            }
        }

        // install an instance of Transformer to instrument the bytecode
        // n.b. this is done with boxing gloves on using explicit class loading and method invocation
        // via reflection for a GOOD reason. This class (Main) gets laoded by the System class loader.
        // If we refer to Transformer by name then it also gets loaded va the System class loader.
        // But if we want to transform a bootstrap class we need Transformer (et al) to be visible
        // from the bootstrap class loader. That will not happen until after this method has called
        // inst.appendToBootstrapClassLoaderSearch (see above) to add the byteman jar to the path.
        // Directly referring to Transformer will giveus two versions of Transformer et al. Not only
        // does that cause us class mismathc problem it also means that a new done here will not install
        // the new instance in the static field fo the oneloaded in the bootstrap loader. If instead we
        // use boxing gloves then the byteman code wil get loaded in the bootstrap loader and its constructor
        // will be called.
        //
        // Of course, if the user does not supply boot:byteman.jar as a -javaagent option then class references
        // resolve against the system loader and injection into bootstrap classes fails. But that's still ok
        // because the byteman classes are still only foudn in one place.

        boolean isRedefine = inst.isRedefineClassesSupported();

        ClassFileTransformer transformer;
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class transformerClazz;

        if (allowRedefine && isRedefine) {
            transformerClazz = loader.loadClass("org.jboss.byteman.agent.Retransformer");
            //transformer = new Retransformer(inst, scriptPaths, scripts, true);
            Constructor constructor = transformerClazz.getConstructor(Instrumentation.class, List.class, List.class, boolean.class);
            transformer = (ClassFileTransformer)constructor.newInstance(new Object[] { inst, scriptPaths, scripts, isRedefine});
        } else {
            transformerClazz = loader.loadClass("org.jboss.byteman.agent.Transformer");
            //transformer = new Transformer(inst, scriptPaths, scripts, isRedefine);
            Constructor constructor = transformerClazz.getConstructor(Instrumentation.class, List.class, List.class, boolean.class);
            transformer = (ClassFileTransformer)constructor.newInstance(new Object[] { inst, scriptPaths, scripts, isRedefine});
        }

        inst.addTransformer(transformer, true);
        
        if (allowRedefine && isRedefine) {
            Method method = transformerClazz.getMethod("addTransformListener", String.class, Integer.class);
            method.invoke(transformer, hostname, port);
        }

        if (isRedefine) {
            Method method;

            method = transformerClazz.getMethod("installBootScripts");
            method.invoke(transformer);
            //transformer.installBootScripts();
        }
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception
    {
        premain(args, inst);
    }
    /**
     * prefix used to specify port argument for agent
     */
    private static final String PORT_PREFIX = "port:";

    /**
     * prefix used to specify bind address argument for agent
     */
    private static final String ADDRESS_PREFIX = "address:";
    
    /**
     * prefix used to specify boot jar argument for agent
     */
    private static final String BOOT_PREFIX = "boot:";

    /**
     * prefix used to specify system jar argument for agent
     */
    private static final String SYS_PREFIX = "sys:";

    /**
     * prefix used to specify script argument for agent
     */

    private static final String SCRIPT_PREFIX = "script:";

    /**
     * prefix used to specify transformer type argument for agent
     */

    private static final String LISTENER_PREFIX = "listener:";

    /**
     * for backwards compatibiltiy
     */

    private static final String REDEFINE_PREFIX = "redefine:";

    /**
     * prefix used to specify system properties to be set before starting the agent
     */

    private static final String PROP_PREFIX = "prop:";

    /**
     * list of paths to extra bootstrap jars supplied on command line
     */
    private static List<String> bootJarPaths = new ArrayList<String>();

    /**
     * list of paths to extra system jars supplied on command line
     */
    private static List<String> sysJarPaths = new ArrayList<String>();

    /**
     * list of paths to script files supplied on command line
     */
    private static List<String> scriptPaths = new ArrayList<String>();

    /**
     * list of scripts read from script files
     */
    private static List<String> scripts = new ArrayList<String>();
    
    /**
     * The hostname to bind the listener to, supplied on the command line (optional argument)
     */
    private static String hostname = null;
    
    /**
     * The port that the listener will listen to, supplied on the command line (optional argument)
     */
    private static Integer port = null;
}