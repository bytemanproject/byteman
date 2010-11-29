/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010 Red Hat and individual contributors
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
 * (C) 2010,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.agent.install;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarFile;

/**
 * A program which uses the sun.com.tools.attach.VirtualMachine class to install the Byteman agent into a
 * running JVM. This provides an alternative to using the -javaagent option to install the agent.
 */
public class Install
{
    private String agentJar;
    private int pid;
    private int port;
    private String host;
    private boolean addToBoot;
    private String props;
    private VirtualMachine vm;

    private static final String BYTEMAN_PREFIX="org.jboss.byteman.";

    /**
     * main routine
     * <p/>
     * Install [-h host] [-p port] [-b] pid
     * <p/>
     * see method {@link #usage} for details of the command syntax
     * @param args the command options
     */
    public static void main(String[] args)
    {
        Install attachTest = new Install();
        attachTest.parseArgs(args);
        attachTest.locateAgent();
        attachTest.attach();
        attachTest.injectAgent();
    }

    /**
     *  only this class creates instances
     */
    private Install()
    {
        agentJar = null;
        pid = 0;
        port = 0;
        addToBoot = false;
        props="";
        vm = null;
    }

    /**
     * check the supplied arguments and stash away te relevant data
     * @param args the value supplied to main
     */
    private void parseArgs(String[] args)
    {
        int argCount = args.length;
        int idx = 0;
        if (idx == argCount) {
            usage(0);
        }

        String nextArg = args[idx];

        while (nextArg.length() != 0 &&
                nextArg.charAt(0) == '-') {
            if (nextArg.equals("-p")) {
                idx++;
                if (idx == argCount) {
                    usage(1);
                }
                nextArg = args[idx];
                idx++;
                try {
                    port = Integer.decode(nextArg);
                } catch (NumberFormatException e) {
                    System.out.println("Install : invalid value for port " + nextArg);
                    usage(1);
                }
            } else if (nextArg.equals("-h")) {
                idx++;
                if (idx == argCount) {
                    usage(1);
                }
                nextArg = args[idx];
                idx++;
                host = nextArg;
            } else if (nextArg.equals("-b")) {
                idx++;
                addToBoot = true;
            } else if (nextArg.startsWith("-D")) {
                idx++;
                String prop=nextArg.substring(2);
                if (!prop.startsWith(BYTEMAN_PREFIX) || prop.contains(",")) {
                    System.out.println("Install : invalid property setting " + prop);
                    usage(1);
                }
                props = props + ",prop:" + prop;
            } else if (nextArg.equals("--help")) {
                    usage(0);
            } else {
                System.out.println("Install : invalid option " + args[idx]);
                usage(1);
            }
            if (idx == argCount) {
                usage(1);
            }
            nextArg = args[idx];
        }

        if (idx != argCount - 1) {
            usage(1);
        }

        try {
            pid = Integer.decode(nextArg);
        } catch (NumberFormatException e) {
            System.out.println("Install : invalid value for process id " + nextArg);
            usage(1);
        }
    }

    /**
     * check for environment setting BYTEMAN_HOME and use it to identify the location of
     * the byteman agent jar.
     */
    private void locateAgent()
    {
        String bmHome = System.getenv("BYTEMAN_HOME");
        if (bmHome == null || bmHome.length() == 0) {
            System.out.println("Install : please set environment variable BYTEMAN_HOME");
        }

        if (bmHome.endsWith("/")) {
            bmHome = bmHome.substring(0, bmHome.length() - 1);
        }

        File bmHomeFile = new File(bmHome);
        if (!bmHomeFile.isDirectory()) {
            System.out.println("Install : ${BYTEMAN_HOME} does not identify a directory");
        }

        File bmLibFile = new File(bmHome + "/lib");
        if (!bmLibFile.isDirectory()) {
            System.out.println("Install : ${BYTEMAN_HOME}/lib does not identify a directory");
        }

        try {
            JarFile bytemanJarFile = new JarFile(bmHome + "/lib/byteman.jar");
        } catch (IOException e) {
            System.out.println("Install : ${BYTEMAN_HOME}/lib/byteman.jar is not a valid jar file");
        }

        agentJar = bmHome + "/lib/byteman.jar";
    }

    /**
     * attach to the Java process identified by the process id supplied on the command line
     */
    private void attach()
    {
        Properties properties = null;
        try {
             vm = VirtualMachine.attach(Integer.toString(pid));
        } catch (AttachNotSupportedException e) {
            System.out.println("Install : unable to attach to process " + pid);
            e.printStackTrace();
            System.exit(2);
        } catch (IOException e) {
            System.out.println("Install : I/O exception attaching to process " + pid);
            e.printStackTrace();
            System.exit(3);
        }

        try {
            properties = vm.getAgentProperties();
        } catch (IOException e) {
            System.out.println("Install : I/O exception fetching agent properties " + pid);
            e.printStackTrace();
            System.exit(4);
        }

        /*
        System.out.println("agent properties:");
        for (String name : properties.stringPropertyNames()) {
            System.out.print("  ");
            System.out.print(name);
            System.out.print("=");
            System.out.println(properties.getProperty(name));
        }
        */
    }

    /**
     * get the attached process to upload and install the agent jar using whatever agent options were
     * configured on the command line
     */
    private void injectAgent()
    {
        if (agentJar != null) {
            // we need at the very least to enable the listener so that scripts can be uploaded
            String agentOptions = "listener:true";
            if (host != null && host.length() != 0) {
                agentOptions += ",address:" + host;
            }
            if (port != 0) {
                agentOptions += ",port:" + port;
            }
            if (addToBoot) {
                agentOptions += ",boot:" + agentJar;
            }
            if (props != null) {
                agentOptions += props;
            }
            try {
                vm.loadAgent(agentJar, agentOptions);
            } catch (AgentLoadException e) {
                System.out.println("Install :load exception loading agent");
                e.printStackTrace();
                System.exit(5);
            } catch (AgentInitializationException e) {
                System.out.println("Install : initialization exception loading agent");
                e.printStackTrace();
                System.exit(6);
            } catch (IOException e) {
                System.out.println("Install : I/O exception loading agent");
                e.printStackTrace();
                System.exit(7);
            }
        }
        
        if (vm != null) {
            try {
                vm.detach();
            } catch (IOException e) {
                System.out.println("Install : I/O exception detaching from process " + pid);
                e.printStackTrace();
                System.exit(8);
            }
        }
    }

    /**
     * print usage information and exit with a specific exit code
     * @param exitValue the value to be supplied to the exit call
     */
    private static void usage(int exitValue)
    {
        System.out.println("usage : Install [-h host] [-p porm] [-b] [-Dprop[=value]]* pid");
        System.out.println("        upload the byteman agent into a running JVM");
        System.out.println("    pid is the process id of the target JVM");
        System.out.println("    -h host selects the host name or address the agent listener binds to");
        System.out.println("    -p port selects the port the agent listener binds to");
        System.out.println("    -b adds the byteman jar to the bootstrap classpath");
        System.out.println("    -Dname=value can be used to set system properties whose name starts with \"org.jboss.byteman.\"");        
        System.out.println("    expects to find a byteman agent jar in $BYTEMAN_HOME/lib/byteman.jar");
        System.exit(exitValue);
    }
}
