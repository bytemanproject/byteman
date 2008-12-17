/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jbossts.orchestration.agent;


import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

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
                if (Transformer.isVerbose()) {
                    System.out.println("processing script file " + scriptPath);
                }

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
