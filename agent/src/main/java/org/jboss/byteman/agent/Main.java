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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * agent class supplied at JVM startup to install byteman package bytecode transformer
 */
public class Main {
    public static boolean firstTime = true;
    public final static String BYTEMAN_PREFIX = "org.jboss.byteman.";
    public final static String BYTEMAN_AGENT_LOADED = "org.jboss.byteman.agent.loaded";

    public static void premain(String args, Instrumentation inst)
            throws Exception
    {
        // guard against the agent being loaded twice
        synchronized (Main.class) {
            if (firstTime) {
                firstTime = false;
                System.setProperty(BYTEMAN_AGENT_LOADED, Boolean.TRUE.toString());
            } else {
                throw new Exception("Main : attempting to load Byteman agent more than once");
            }
        }
        boolean installPolicy = false;

        if (args != null) {
            // args are supplied separated by ',' characters
            String[] argsArray = args.split(",");
            // we accept extra jar files to be added to the boot/sys classpaths
            // script files to be scanned for rules
            // listener flag which implies use of a retransformer
            for (String arg : argsArray) {
                if (arg.startsWith(BOOT_PREFIX)) {
                    // boot argument can be a single jar or a ':' or ';' separated list of jars
                    String bootArg =  arg.substring(BOOT_PREFIX.length(), arg.length());
                    String[] jarNames = bootArg.split(File.pathSeparator);
                    for (String jarName : jarNames) {
                        bootJarPaths.add(jarName);
                    }
                } else if (arg.startsWith(SYS_PREFIX)) {
                    // sys argument can be a single jar or a ':' or ';' separated list of jars
                    String sysArg =  arg.substring(SYS_PREFIX.length(), arg.length());
                    String[] jarNames = sysArg.split(File.pathSeparator);
                    for (String jarName : jarNames) {
                        sysJarPaths.add(jarName);
                    }
                } else if (arg.startsWith(ADDRESS_PREFIX)) {
                    hostname = arg.substring(ADDRESS_PREFIX.length(), arg.length());
                    if (managerClassName == null) {
                        managerClassName=MANAGER_NAME;
                    }
                } else if (arg.startsWith(PORT_PREFIX)) {
                    try {
                        port = Integer.valueOf(arg.substring(PORT_PREFIX.length(), arg.length()));
                        if (port <= 0) {
                            System.err.println("Invalid port specified [" + port + "]");
                            port = null;
                        } else if (managerClassName == null) {
                            managerClassName=MANAGER_NAME;
                        }
                    } catch (Exception e) {
                        System.err.println("Invalid port specified [" + arg + "]. Cause: " + e);
                    }
                } else if (arg.startsWith(SCRIPT_PREFIX)) {
                    scriptPaths.add(arg.substring(SCRIPT_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(RESOURCE_SCRIPT_PREFIX)) {
                    resourcescriptPaths.add(arg.substring(RESOURCE_SCRIPT_PREFIX.length(), arg.length()));
                } else if (arg.startsWith(LISTENER_PREFIX)) {
                    // listener:true is an alias for manager:o.j.b.a.TransformListener
                    // listener:false means no manager (yes, not even TransformListener)
                    String value = arg.substring(LISTENER_PREFIX.length(), arg.length());
                    if (Boolean.parseBoolean(value)) {
                        managerClassName = MANAGER_NAME;
                    } else {
                        managerClassName = null;
                    }
                } else if (arg.startsWith(REDEFINE_PREFIX)) {
                    // this is only for backwards compatibility -- it is the same as listener
                    String value = arg.substring(REDEFINE_PREFIX.length(), arg.length());
                    if (Boolean.parseBoolean(value)) {
                        managerClassName = MANAGER_NAME;
                    } else {
                        managerClassName = null;
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
                } else if (arg.startsWith(POLICY_PREFIX)) {
                    String value = arg.substring(POLICY_PREFIX.length(), arg.length());
                    installPolicy = Boolean.parseBoolean(value);
                } else if (arg.startsWith(MANAGER_PREFIX)) {
                    managerClassName = arg.substring(MANAGER_PREFIX.length(), arg.length());
                    if (managerClassName.length() == 0) {
                        managerClassName = null;
                    }
                } else if (arg.startsWith(MODULE_PREFIX)) {
                    // this can be used to set byteman properties
                    String mod = arg.substring(MODULE_PREFIX.length(), arg.length());
                    String moduleArgs="";
                    int index = mod.indexOf('=');
                    if (index > 0) {
                        // need to split off the value
                        if (index == mod.length() - 1)
                        {
                            // value is empty so just drop the =
                            mod = mod.substring(0, index);
                        } else {
                            moduleArgs = mod.substring(index + 1);
                            mod = mod.substring(0, index);
                        }
                    }
                    moduleSystemName = mod;
                    moduleSystemArgs = moduleArgs;
                } else {
                    System.err.println("org.jboss.byteman.agent.Main:\n" +
                            "  illegal agent argument : " + arg + "\n" +
                            "  valid arguments are boot:<path-to-jar>, sys:<path-to-jar>, script:<path-to-script>, resourcescript:<scripts-prefix>,"
                            + "prop:<property-to-set>, address:<address-for-agent>, port:<port-for-agent>, modules:<module-system-class>, "
                            + "policy:<agent-security-policy>, manager:<manager-class> or listener:<true-or-false>");
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
        // create a socket so we can be sure it is loaded before the transformer gets created. otherwise
        // we seem to hit a deadlock when trying to instrument socket

        Socket dummy = new Socket();

        // look up rules in any script files

        for (String scriptPath : scriptPaths) {
            InputStream is = null;
            try {
                if(scriptPath.startsWith("jar:")) {
                    // A jar:<url>!/{entry}
                    URL url = new URL(scriptPath);
                    JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                    is = jarConnection.getJarFile().getInputStream(jarConnection.getJarEntry());
                } else {
                    is = new FileInputStream(scriptPath);
                }
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                String ruleScript = new String(bytes);
                scripts.add(ruleScript);
            } catch (IOException ioe) {
                System.err.println("org.jboss.byteman.agent.Main: unable to read rule script file : " + scriptPath);
                throw ioe;
            } finally {
                if (is != null)
                    is.close();
            }
        }

        // look up rules in any resource script files

        for (String scriptPath : resourcescriptPaths) {
            try {
                InputStream is = ClassLoader.getSystemResourceAsStream(scriptPath);
                if (is == null) {
                    throw new Exception("org.jboss.byteman.agent.Main: could not read rule script resource file : " + scriptPath);
                }
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                String ruleScript = new String(bytes);
                scripts.add(ruleScript);
                // merge the resource and file script paths into one list
                scriptPaths.add(scriptPath);
            } catch (IOException ioe) {
                System.err.println("org.jboss.byteman.agent.Main: error reading rule script resource file : " + scriptPath);
                throw ioe;
            }
        }

        // install an instance of Transformer to instrument the bytecode
        // n.b. this is done with boxing gloves on using explicit class loading and method invocation
        // via reflection for a GOOD reason. This class (Main) gets loaded by the System class loader.
        // If we refer to Transformer by name then it also gets loaded via the System class loader.
        // But if we want to transform a bootstrap class we need Transformer (et al) to be visible
        // from the bootstrap class loader. That will not happen until after this method has called
        // inst.appendToBootstrapClassLoaderSearch (see above) to add the byteman jar to the path.
        // Directly referring to Transformer will give us two versions of Transformer et al. Not only
        // does that cause us class mismatch problem it also means that a new done here will not install
        // the new instance in the static field of the one loaded in the bootstrap loader. If instead we
        // use boxing gloves then the byteman code will get loaded in the bootstrap loader and its constructor
        // will be called.
        //
        // Of course, if the user does not supply boot:byteman.jar as a -javaagent option then class references
        // resolve against the system loader and injection into bootstrap classes fails. But that's still ok
        // because the byteman classes are still only found in one place.

        ClassLoader loader = ClassLoader.getSystemClassLoader();

        if (moduleSystemName == null) {
            moduleSystemName = "org.jboss.byteman.modules.NonModuleSystem";
        }
        Class<?/*ModuleSystem*/> moduleSystemInteraceClazz = loader.loadClass(MODULE_SYSTEM_NAME);
        Class<?/*ModuleSystem*/> moduleSystemImplClazz = loader.loadClass(moduleSystemName);
        final Object/*ModuleSystem*/  moduleSystem = moduleSystemImplClazz.newInstance();
        final Method/*String->void*/ moduleSystemInit = moduleSystemInteraceClazz.getMethod("initialize", String.class);
        moduleSystemInit.invoke(moduleSystem, moduleSystemArgs);

        boolean isRedefine = inst.isRedefineClassesSupported();
        Class/*<Transformer>*/ transformerClazz;
        ClassFileTransformer transformer;
        if (managerClassName != null && isRedefine) {
            transformerClazz = loader.loadClass(RETRANSFORMER_NAME);
            //transformer = new Retransformer(inst, moduleSystem, scriptPaths, scripts, true);
            Constructor/*<Transformer>*/ constructor = transformerClazz.getConstructor(Instrumentation.class,moduleSystemInteraceClazz, List.class, List.class, boolean.class);
            transformer = (ClassFileTransformer)constructor.newInstance(new Object[] { inst, moduleSystem, scriptPaths, scripts, isRedefine});
        } else {
            transformerClazz = loader.loadClass(TRANSFORMER_NAME);
            //transformer = new Transformer(inst, moduleSystem, scriptPaths, scripts, isRedefine);
            Constructor/*<Retransformer>*/ constructor = transformerClazz.getConstructor(Instrumentation.class, moduleSystemInteraceClazz, List.class, List.class, boolean.class);
            transformer = (ClassFileTransformer)constructor.newInstance(new Object[] { inst, moduleSystem, scriptPaths, scripts, isRedefine});
        }

        inst.addTransformer(transformer, true);

        if (managerClassName != null && isRedefine) {
            Class managerClazz = loader.loadClass(managerClassName);

            try {
                Method method = managerClazz.getMethod("initialize", transformerClazz, String.class, Integer.class);
                method.invoke(null, transformer, hostname, port);
            } catch (NoSuchMethodException e) {
                Method method = managerClazz.getMethod("initialize", transformerClazz);
                method.invoke(null, transformer);
            }
        }

        if (installPolicy) {
            Method method = transformerClazz.getMethod("installPolicy");
            method.invoke(transformer);
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
     * prefix used to request installation of an access-all-areas security
     * policy at install time for agent code
     */
    private static final String POLICY_PREFIX = "policy:";
    /**
     * prefix used to specify file script argument for agent
     */

    private static final String SCRIPT_PREFIX = "script:";

    /**
     * prefix used to specify resource script argument for agent
     */

    private static final String RESOURCE_SCRIPT_PREFIX = "resourcescript:";

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
     * prefix used to specify the manager class
     */

    private static final String MANAGER_PREFIX = "manager:";

    /**
     * prefix used to specify the module system class
     */

    private static final String MODULE_PREFIX = "modules:";


    /**
     * name of basic transformer class.
     */

    private static final String TRANSFORMER_NAME = "org.jboss.byteman.agent.Transformer";
    
    /**
     * name of retransformer class.
     */
    
    private static final String RETRANSFORMER_NAME = "org.jboss.byteman.agent.Retransformer";

    /**
     * name of default manager class.
     */

    private static final String MANAGER_NAME = "org.jboss.byteman.agent.TransformListener";
    
    /**
     * name of module system interface.
     */

    private static final String MODULE_SYSTEM_NAME = "org.jboss.byteman.modules.ModuleSystem";

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
     * list of paths to resource script files supplied on command line
     */
    private static List<String> resourcescriptPaths = new ArrayList<String>();

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

    /**
     * The name of the manager class responsible for loading/unloading scripts, supplied on the
     * command line (optional argument)
     */
    private static String managerClassName = null;

    /**
     * The name of the module system implementation class, supplied on the
     * command line (optional argument)
     */
    private static String moduleSystemName = null;

    /**
     * The arguments to the module system implementation class, supplied on the
     * command line (optional argument)
     */
    private static String moduleSystemArgs = "";

}
