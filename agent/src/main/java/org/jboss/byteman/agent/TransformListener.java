/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-10 Red Hat and individual contributors
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
 * (C) 2009-10,
 * @authors Andrew Dinn
 */
package org.jboss.byteman.agent;

import org.jboss.byteman.agent.Retransformer;
import org.jboss.byteman.rule.Rule;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * a socket based listener class which reads scripts from stdin and installs them in the current runtime
 */
public class TransformListener extends Thread
{
    public static int DEFAULT_PORT = 9091;
    public static String DEFAULT_HOST = "localhost";
    private static TransformListener theTransformListener = null;
    private static ServerSocket theServerSocket;
    private Retransformer retransformer;

    private TransformListener(Retransformer retransformer)
    {
        this.retransformer = retransformer;
        setDaemon(true);        
    }

    public static synchronized boolean initialize(Retransformer retransformer, String hostname, Integer port)
    {
        if (theTransformListener == null) {
            try {
                if (hostname == null) {
                    hostname = DEFAULT_HOST;
                }
                if (port == null) {
                    port = Integer.valueOf(DEFAULT_PORT);
                }
                theServerSocket = new ServerSocket();
                theServerSocket.bind(new InetSocketAddress(hostname, port.intValue()));
                if (Transformer.isVerbose()) {
                    System.out.println("TransformListener() : accepting requests on " + hostname + ":" + port);
                }
            } catch (IOException e) {
                System.out.println("TransformListener() : unexpected exception opening server socket " + e);
                e.printStackTrace();
                return false;
            }

            theTransformListener = new TransformListener(retransformer);

            theTransformListener.start();
        }

        return true;
    }

    public static synchronized boolean terminate()
    {
        // we don't want the listener shutdown to be aborted because of triggered rules
        boolean enabled = true;
        try {
        enabled = Rule.disableTriggersInternal();

        if (theTransformListener != null) {
            try {
                theServerSocket.close();
                if (Transformer.isVerbose()) {
                    System.out.println("TransformListener() :  closing port " + DEFAULT_PORT);
                }
            } catch (IOException e) {
                // ignore -- the thread should exit anyway
            }
            try {
                theTransformListener.join();
            } catch (InterruptedException e) {
                // ignore
            }

            theTransformListener = null;
            theServerSocket = null;
        }

        return true;
        } finally {
            if (enabled) {
                Rule.enableTriggersInternal();
            }
        }
    }

    public void run()
    {
        // we don't want to see any triggers in the listener thread
        
        Rule.disableTriggersInternal();

        while (true) {
            if (theServerSocket.isClosed()) {
                return;
            }
            Socket socket = null;
            try {
                socket = theServerSocket.accept();
            } catch (IOException e) {
                if (!theServerSocket.isClosed()) {
                    System.out.println("TransformListener.run : exception from server socket accept " + e);
                    e.printStackTrace();
                }
                return;
            }

            if (Transformer.isVerbose()) {
                System.out.println("TransformListener() : handling connection on port " + socket.getLocalPort());
            }
            try {
                handleConnection(socket);
            } catch (Exception e) {
                System.out.println("TransformListener() : error handling connection on port " + socket.getLocalPort());
                try {
                    socket.close();
                } catch (IOException e1) {
                    // do nothing
                }
            }
        }
    }

    private void handleConnection(Socket socket)
    {
        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            // oops. cannot handle this
            System.out.println("TransformListener.run : error opening socket input stream " + e);
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                System.out.println("TransformListener.run : exception closing socket after failed input stream open" + e1);
                e1.printStackTrace();
            }
            return;
        }

        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            // oops. cannot handle this
            System.out.println("TransformListener.run : error opening socket output stream " + e);
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                System.out.println("TransformListener.run : exception closing socket after failed output stream open" + e1);
                e1.printStackTrace();
            }
            return;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(os));

        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            System.out.println("TransformListener.run : exception " + e + " while reading command");
            e.printStackTrace();
        }

        try {
            if (line == null) {
                out.println("ERROR");
                out.println("Expecting input command");
                out.println("OK");
            } else if (line.equals("BOOT")) {
                loadJars(in, out, true);
            } else if (line.equals("SYS")) {
                loadJars(in, out, false);
            } else if (line.equals("LOAD")) {
                loadScripts(in, out);
            } else if (line.equals("DELETE")) {
                deleteScripts(in, out);
            } else if (line.equals("LIST")) {
                listScripts(in, out);
            } else if (line.equals("DELETEALL")) {
                purgeScripts(in, out);
            } else if (line.equals("VERSION")) {
                getVersion(in, out);
            } else if (line.equals("LISTBOOT")) {
                listBootJars(in, out);
            } else if (line.equals("LISTSYS")) {
                listSystemJars(in, out);
            } else if (line.equals("LISTSYSPROPS")) {
                listSystemProperties(in, out);
            } else if (line.equals("SETSYSPROPS")) {
                setSystemProperties(in, out);
            } else {
                out.println("ERROR");
                out.println("Unexpected command " + line);
                out.println("OK");
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("TransformListener.run : exception " + e + " processing command " + line);
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                System.out.println("TransformListener.run : exception closing socket " + e1);
                e.printStackTrace();
            }
        }
    }

    private void getVersion(BufferedReader in, PrintWriter out) {
        String version = this.getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = "0";
        }
        out.println(version);
        out.println("OK");
        out.flush();
    }

    private void loadScripts(BufferedReader in, PrintWriter out) throws IOException
    {
        handleScripts(in, out, false);
    }

    private void loadJars(BufferedReader in, PrintWriter out, boolean isBoot) throws IOException
    {
        final String endMarker = (isBoot) ? "ENDBOOT" : "ENDSYS";
        String line = in.readLine().trim();
        while (line != null && !line.equals(endMarker)) {
            try {
                JarFile jarfile = new JarFile(new File(line));
                retransformer.appendJarFile(out, jarfile, isBoot);
            } catch (Exception e) {
                out.append("EXCEPTION ");
                out.append("Unable to add jar file " + line + "\n");
                out.append(e.toString());
                out.append("\n");
                e.printStackTrace(out);
            }
            line = in.readLine().trim();
        }
        if (line == null || !line.equals(endMarker)) {
            out.append("ERROR\n");
            out.append("Unexpected end of line reading " + ((isBoot) ? "boot" : "system") + " jars\n");
        }
        out.println("OK");
        out.flush();
    }

    private void deleteScripts(BufferedReader in, PrintWriter out) throws IOException
    {
        handleScripts(in, out, true);
    }

    private void handleScripts(BufferedReader in, PrintWriter out, boolean doDelete) throws IOException
    {
        List<String> scripts = new LinkedList<String>();
        List<String> scriptNames = new LinkedList<String>();

        String line = in.readLine().trim();
        String scriptName = "<unknown>";
        while (line.startsWith("SCRIPT ")) {
            StringBuffer stringBuffer = new StringBuffer();
            scriptName  = line.substring("SCRIPT ".length());
            line = in.readLine();
            while (line != null && !line.equals("ENDSCRIPT")) {
                stringBuffer.append(line);
                stringBuffer.append('\n');
                line = in.readLine();
            }

            if (line == null || !line.equals("ENDSCRIPT")) {
                out.append("ERROR\n");
                out.append("Unexpected end of line reading script " + scriptName + "\n");
                out.append("OK");
                out.flush();
                return;
            }
            String script = stringBuffer.toString();
            scripts.add(script);
            scriptNames.add(scriptName);

            line = in.readLine();
        }

        if ((doDelete && !line.equals("ENDDELETE")) ||
                (!doDelete && !line.equals("ENDLOAD"))) {
            out.append("ERROR ");
            out.append("Unexpected end of line reading script " + scriptName + "\n");
            out.println("OK");
            out.flush();
            return;
        }

        try {
            if (doDelete) {
                retransformer.removeScripts(scripts, out);
            } else {
                retransformer.installScript(scripts, scriptNames, out);
            }
        } catch (Exception e) {
            out.append("EXCEPTION ");
            out.append(e.toString());
            out.append('\n');
            e.printStackTrace(out);
        }
        out.println("OK");
        out.flush();
    }

    private void purgeScripts(BufferedReader in, PrintWriter out) throws Exception
    {
        retransformer.removeScripts(null, out);
        out.println("OK");
        out.flush();
    }

    private void listScripts(BufferedReader in, PrintWriter out) throws Exception
    {
        retransformer.listScripts(out);
        out.println("OK");
        out.flush();
    }

    private void listBootJars(BufferedReader in, PrintWriter out) throws Exception
    {
        Set<String> jars = retransformer.getLoadedBootJars();
        for (String jar : jars) {
            out.println(new File(jar).getAbsolutePath());
        }
        out.println("OK");
        out.flush();
    }

    private void listSystemJars(BufferedReader in, PrintWriter out) throws Exception
    {
        Set<String> jars = retransformer.getLoadedSystemJars();
        for (String jar : jars) {
            out.println(new File(jar).getAbsolutePath());
        }
        out.println("OK");
        out.flush();
    }

    private void listSystemProperties(BufferedReader in, PrintWriter out) throws Exception
    {
        Properties sysProps = System.getProperties();
        boolean strictMode = false;
        if (Boolean.parseBoolean(sysProps.getProperty(Transformer.SYSPROPS_STRICT_MODE, "true"))) {
            strictMode = true;
        }
                
        for (Map.Entry<Object, Object> entry : sysProps.entrySet()) {
            String name = entry.getKey().toString();
            if (!strictMode || name.startsWith("org.jboss.byteman.")) {
               String value = entry.getValue().toString();
               out.println(name + "=" + value.replace("\n", "\\n").replace("\r", "\\r"));
            }
        }
        out.println("OK");
        out.flush();
    }
    
    private void setSystemProperties(BufferedReader in, PrintWriter out) throws Exception
    {
        boolean strictMode = false;
        if (Boolean.parseBoolean(System.getProperty(Transformer.SYSPROPS_STRICT_MODE, "true"))) {
            strictMode = true;
        }
        
        final String endMarker = "ENDSETSYSPROPS";
        String line = in.readLine().trim();
        while (line != null && !line.equals(endMarker)) {
            try {
                String[] nameValuePair = line.split("=", 2);
                if (nameValuePair.length != 2 ) {
                    throw new Exception("missing '='");
                }
                String name = nameValuePair[0];
                String value = nameValuePair[1];
                if (strictMode && !name.startsWith("org.jboss.byteman.")) {
                    throw new Exception("strict mode is enabled, cannot set non-byteman system property");                    
                }
                if (name.equals(Transformer.SYSPROPS_STRICT_MODE) && !value.equals("true")) {
                    // nice try
                    throw new Exception("cannot turn off strict mode");
                }

                // everything looks good and we are allowed to set the system property now
                if (value.length() > 0) {
                	// "some.sys.prop=" means the client wants to delete the system property
                	System.setProperty(name, value);                	
                	out.append("Set system property [" + name + "] to value [" + value + "]\n");
                } else {
                	System.clearProperty(name);
                	out.append("Deleted system property [" + name + "]\n");
                }
                // ok, now tell the transformer a property has changed
                retransformer.updateConfiguration(name);
            } catch (Exception e) {
                out.append("EXCEPTION ");
                out.append("Unable to set system property [" + line + "]\n");
                out.append(e.toString());
                out.append("\n");
                e.printStackTrace(out);
            }
            line = in.readLine().trim();
        }
        if (line == null || !line.equals(endMarker)) {
            out.append("ERROR\n");
            out.append("Unexpected end of line reading system properties\n");
        }
        out.println("OK");
        out.flush();
    }
}