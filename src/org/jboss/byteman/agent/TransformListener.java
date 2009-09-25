package org.jboss.byteman.agent;

import org.jboss.byteman.agent.Retransformer;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.*;
import java.util.List;
import java.util.LinkedList;

/**
 * a socket based listener class which reads scripts from stdin and installs them in the current runtime
 */
public class TransformListener extends Thread
{
    public static int DEFAULT_PORT = 9091;
    private static TransformListener theTransformListener = null;
    private static ServerSocket theServerSocket;
    private Retransformer retransformer;

    private TransformListener(Retransformer retransformer)
    {
        this.retransformer = retransformer;
        setDaemon(true);        
    }

    public static synchronized boolean initialize(Retransformer retransformer)
    {
        if (theTransformListener == null) {
            try {
                theServerSocket = new ServerSocket();
                theServerSocket.bind(new InetSocketAddress("localhost", DEFAULT_PORT));
                if (Transformer.isVerbose()) {
                    System.out.println("TransformListener() : accepting requests on port " + DEFAULT_PORT);
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
    }

    public void run()
    {
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
            handleConnection(socket);
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
            } else if (line.equals("LOAD")) {
                loadScripts(in, out);
            } else if (line.equals("LIST")) {
                listScripts(in, out);
            } else {
                out.println("ERROR");
                out.println("Unexpected command " + line);
                out.println("OK");
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

    private void loadScripts(BufferedReader in, PrintWriter out) throws IOException
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
        }

        line = in.readLine();
        
        if (!line.equals("ENDLOAD")) {
            out.append("ERROR ");
            out.append("Unexpected end of line reading script " + scriptName + "\n");
            out.println("OK");
            out.flush();
            return;
        }

        try {
            retransformer.installScript(scripts, scriptNames);
            out.println("OK");
            out.flush();
        } catch (Exception e) {
            out.append("EXCEPTION ");
            out.append(e.toString());
            out.append('\n');
            e.printStackTrace(out);
            out.println("OK");
            out.flush();
        }
    }

    private void listScripts(BufferedReader in, PrintWriter out) throws Exception
    {
        retransformer.listScripts(out);
        out.println("OK");
        out.flush();
    }
}