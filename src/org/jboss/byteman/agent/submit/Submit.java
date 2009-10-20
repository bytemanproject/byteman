package org.jboss.byteman.agent.submit;

import org.jboss.byteman.agent.TransformListener;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide a main routine for an app which communicates with the byteman agent at runtime allowing loading,
 * reloading, unloading of rules and listing of the current rule set and any successful or failed attempts
 * to inject, parse and typecheck the rules.
 */
public class Submit
{
    /**
     * main routine which submits a script to the byteman agent
     * @param args command line arguments specifying the script file(s) to be submitted and, optionally,
     * the byteman agent listener port to use.
     * Submit [-p port] [-l|-u] [scriptfile . . .]
     * Submit [-p port] [-b|-s] jarfile . . .
     * -p port specifies theport to use
     * -l implies load/reload all rules found in supplied scripts
     *    or list all current rules if no scriptfile
     * -u implies unload all rules found in supplied scripts
     *    or unload all rules if no scriptfile
     * -b jarfile implies install jar files into boot class path
     * -s jarfile implies install jar files into system class path
     */
    public static void main(String[] args)
    {
        int port = TransformListener.DEFAULT_PORT;
        int startIdx = 0;
        int maxIdx = args.length;
        boolean loadOrList = false;
        boolean deleteRules = false;
        boolean addBoot = false;
        boolean addSys = false;
        int optionCount = 0;

        while (startIdx < maxIdx && args[startIdx].startsWith("-")) {
            if (maxIdx >= startIdx + 2 && args[startIdx].equals("-p")) {
                try {
                    port = Integer.valueOf(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Submit : invalid port " + args[1]);
                    System.exit(1);
                }
                if (port <= 0) {
                    System.out.println("Submit : invalid port " + args[1]);
                    System.exit(1);
                }
                startIdx += 2;
            } else if (args[startIdx].equals("-u")) {
                deleteRules = true;
                startIdx++;
                optionCount++;
            } else if (args[startIdx].equals("-l")) {
                loadOrList = true;
                startIdx++;
                optionCount++;
            } else if (args[startIdx].equals("-b")) {
                addBoot = true;
                startIdx ++;
                optionCount++;
            } else if (args[startIdx].equals("-b")) {
                addSys = true;
                startIdx ++;
                optionCount++;
            } else {
                break;
            }
        }

        if (startIdx < maxIdx && args[startIdx].startsWith("-") || optionCount > 1) {
            usage(1);
        }

        if (optionCount == 0) {
            loadOrList = true;
        }

        // must have some file args if adding to sys or boot classpath
        
        if (startIdx == maxIdx && (addBoot || addSys)) {
            usage(1);
        }
        
        for (int i = startIdx; i < maxIdx; i++) {
            File file = new File(args[i]);
            if (!file.isFile() || !file.canRead()) {
                System.out.println("Submit : invalid file " + args[i]);
                System.exit(1);
            }
        }

        // ok try to open the socket
        Socket socket = null;

        try {
            socket = new Socket("localhost", port);
        } catch (IOException e) {
            System.out.println("Submit : error opening socket " +  e);
            e.printStackTrace();
            System.exit(1);
        }

        InputStream is = null;
        try {
            is = socket.getInputStream();
        } catch (IOException e) {
            // oops. cannot handle this
            System.out.println("Submit : error opening socket input stream " + e);
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                System.out.println("Submit : exception closing socket after failed input stream open" + e1);
                e1.printStackTrace();
            }
            System.exit(1);
        }

        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            // oops. cannot handle this
            System.out.println("Submit : error opening socket output stream " + e);
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                System.out.println("Submit : exception closing socket after failed output stream open" + e1);
                e1.printStackTrace();
            }
            System.exit(1);
        }

        PrintWriter out = null;

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        out = new PrintWriter(new OutputStreamWriter(os));
        final int READ_BUFFER_LENGTH = 1024;
        char[] readBuffer = new char[READ_BUFFER_LENGTH];

        if (startIdx == maxIdx) {
            // no args means list or delete all current scripts
            if (deleteRules) {
                out.println("DELETEALL");
            } else {
                out.println("LIST");
            }
            out.flush();
            try {
                String line = in.readLine().trim();
                while (line != null && !line.equals("OK"))
                {
                    System.out.println(line);
                    line = in.readLine();
                }
                socket.close();
            } catch (IOException e) {
                System.out.println("Submit : error reading from socket " + e);
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    System.out.println("Submit : exception closing socket after failed read " + e1);
                    e1.printStackTrace();
                }
                System.exit(1);
            }
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            if (addBoot) {
                stringBuffer.append("BOOT\n");
                for (int i = startIdx; i < maxIdx; i++) {
                    String name = args[i];
                    stringBuffer.append(name);
                    stringBuffer.append("\n");
                }
                stringBuffer.append("ENDBOOT\n");
            } else if (addSys) {
                stringBuffer.append("BOOT\n");
                for (int i = startIdx; i < maxIdx; i++) {
                    String name = args[i];
                    stringBuffer.append(name);
                    stringBuffer.append("\n");
                }
                stringBuffer.append("ENDBOOT\n");
            } else {
                if (deleteRules) {
                    stringBuffer.append("DELETE\n");
                } else {
                    stringBuffer.append("LOAD\n");
                }
                for (int i = startIdx; i < maxIdx; i++) {
                    String name = args[i];
                    stringBuffer.append("SCRIPT " + name + "\n");
                    try {
                        FileInputStream fis = new FileInputStream(args[i]);
                        InputStreamReader reader = new InputStreamReader(fis);
                        int read = reader.read(readBuffer);
                        while (read > 0) {
                            stringBuffer.append(readBuffer, 0, read);
                            read = reader.read(readBuffer);
                        }
                        reader.close();
                        stringBuffer.append("ENDSCRIPT\n");
                    } catch (IOException e) {
                        System.out.println("Submit : error reading from  file " + args[i] + " " + e);
                        e.printStackTrace();
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            System.out.println("Submit : exception closing socket after failed file read " + e1);
                            e1.printStackTrace();
                        }

                        System.exit(1);
                    }
                }
                if (deleteRules) {
                    stringBuffer.append("ENDDELETE\n");
                } else {
                    stringBuffer.append("ENDLOAD\n");
                }
            }
            out.append(stringBuffer);
            out.flush();

            try {
                String line = in.readLine().trim();
                while (line != null & !line.equals("OK"))  {
                    System.out.println(line);
                    line = in.readLine();
                }
            } catch (IOException e) {
                System.out.println("Submit : error reading listener reply "+ e);
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    System.out.println("Submit : exception closing socket after failed listener read " + e1);
                    e1.printStackTrace();
                }

                System.exit(1);
            }
        }
    }

    public static void usage(int exitCode)
    {
        System.out.println("usage : Submit [-p port] [-l|-u] [scriptfile . . .]");
        System.out.println("        Submit [-p port] [-b|-s] jarfile . . .");
        System.out.println("        -p specifies listener port");
        System.out.println("        -l (default) with scriptfile(s) means load/reload all rules in scriptfile(s)");
        System.out.println("                     with no scriptfile means list all currently loaded rules");
        System.out.println("        -u with scriptfile(s) means unload all rules in scriptfile(s)");
        System.out.println("           with no scriptfile means unload all currently loaded rules");
        System.out.println("        -b with jarfile(s) means add jars to bootstrap classpath");
        System.out.println("        -s with jarfile(s) means add jars to system classpath");
        System.exit(exitCode);
    }
}
