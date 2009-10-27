package org.jboss.byteman.agent.submit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Java API that can be used to submit requests to a remote Byteman agent.
 * This also includes a main routine for use as a command-line utility.
 * This object provides a means by which you communicate with the Byteman agent at runtime allowing loading,
 * reloading, unloading of rules and listing of the current rule set and any successful or failed attempts
 * to inject, parse and typecheck the rules.
 * 
 * Note that this class is completely standalone and has no dependencies on any other Byteman class.
 * It can be shipped alone in a client jar to be used as a very small app. 
 */
public class Submit
{
    public static final String DEFAULT_ADDRESS = "localhost";
    public static final int DEFAULT_PORT= 9091;

    private final int port;
    private final String address;

    /**
     * Create a client that will connect to a Byteman agent on the default host
     * and port.
     */
    public Submit() {
        this(DEFAULT_ADDRESS, DEFAULT_PORT);
    }

    /**
     * Create a client that will connect to a Byteman agent on the given host
     * and port.
     *
     * @param address
     *            the hostname or IP address of the machine where Byteman agent
     *            is located. If <code>null</code>, the default host is used.
     * @param port
     *            the port that the Byteman agent is listening to.
     *            If 0 or less, the default port is used.
     */
    public Submit(String address, int port) {
        if (address == null) {
            address = DEFAULT_ADDRESS;
        }

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        this.address = address;
        this.port = port;
    }

    /**
     * @return identifies the host where this client expects a Byteman agent to
     *         be running.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * @return the port that this client expects a Byteman agent to be listening
     *         to on the given {@link #getAddress() host}.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Tells the Byteman agent to delete all rules. This will effectively revert
     * the Byteman's VM to its original state; that is, no Byteman injected
     * byte-code will be invoked.
     *
     * @return the results of the delete-all request to the Byteman agent
     *
     * @throws Exception
     *             if the request failed
     */
    public String deleteAllRules() throws Exception {
        Comm comm = new Comm(address, port);
        comm.println("DELETEALL");
        String results = comm.readResponse();
        comm.close();
        return results;
    }

    /**
     * Tells the Byteman agent to list all deployed rules.
     *
     * @return all the rules deployed in the Byteman agent
     *
     * @throws Exception
     *             if the request failed
     */
    public String listAllRules() throws Exception {
        Comm comm = new Comm(address, port);
        comm.println("LIST");
        String results = comm.readResponse();
        comm.close();
        return results;
    }

    /**
     * This adds the given list of files to the Byteman agent's <em>boot</em>
     * classloader. Note that if the Byteman agent is running on a remote
     * machine, the paths must resolve on that remote host (i.e. the file must
     * exist on the remote machine at the paths given to this method).
     *
     * @param jarPaths
     *            the paths to the library .jar files that will be loaded
     *
     * @return the result of the load as reported by Byteman
     *
     * @throws Exception
     *             if the request failed
     */
    public String addJarToBootClassloader(List<String> jarPaths) throws Exception {
        if (jarPaths == null || jarPaths.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("BOOT\n");
        for (String jarPath : jarPaths) {
            str.append(jarPath).append("\n");
        }
        str.append("ENDBOOT\n");

        Comm comm = new Comm(this.address, this.port);
        comm.print(str.toString());
        String results = comm.readResponse();
        comm.close();
        return results;
    }

    /**
     * This adds the given list of files to the Byteman agent's <em>system</em>
     * classloader. Note that if the Byteman agent is running on a remote
     * machine, the paths must resolve on that remote host (i.e. the file must
     * exist on the remote machine at the paths given to this method).
     *
     * @param jarPaths
     *            the paths to the library .jar files that will be loaded
     *
     * @return the result of the load as reported by Byteman
     *
     * @throws Exception
     *             if the request failed
     */
    public String addJarToSystemClassloader(List<String> jarPaths) throws Exception {
        if (jarPaths == null || jarPaths.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("SYS\n");
        for (String jarPath : jarPaths) {
            str.append(jarPath).append("\n");
        }
        str.append("ENDSYS\n");

        Comm comm = new Comm(this.address, this.port);
        comm.print(str.toString());
        String results = comm.readResponse();
        comm.close();
        return results;
    }

    /**
     * Deploys rules into Byteman, where the rule definitions are found in the
     * local files found at the given paths. The rule definitions found in the
     * files are actually passed down directly to Byteman, not the file paths
     * themselves. Therefore, these files must exist on the machine where this
     * client is running (i.e. the files are not loaded directly by the Byteman
     * agent).
     *
     * @param filePaths
     *            the local files containing the rule definitions to be deployed
     *            to Byteman
     *
     * @return the results of the deployment
     *
     * @throws Exception
     *             if the request failed
     */
    public String addRulesFromFiles(List<String> filePaths) throws Exception {
        Map<String, String> rules = getRulesFromRuleFiles(filePaths);
        return addRules(rules);
    }

    /**
     * Deploys rules into Byteman, where the rule definitions are found in the
     * given map's value set. The names of the rule definitions are found as
     * keys in the given map.
     *
     * @param rules
     *            rules to be deployed to Byteman (key=rule name, value=rule
     *            definition)
     *
     * @return the results of the deployment
     *
     * @throws Exception
     *             if the request failed
     */
    public String addRules(Map<String, String> rules) throws Exception {
        if (rules == null || rules.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("LOAD\n");
        for (Map.Entry<String, String> entry : rules.entrySet()) {
            str.append("SCRIPT " + entry.getKey() + '\n');
            str.append(entry.getValue()).append('\n');
            str.append("ENDSCRIPT\n");
        }
        str.append("ENDLOAD\n");

        Comm comm = new Comm(this.address, this.port);
        comm.print(str.toString());
        String results = comm.readResponse();
        comm.close();
        return results;

    }

    /**
     * Deletes rules from Byteman, where the rule definitions are found in the
     * local files found at the given paths. After this method is done, the
     * given rules will no longer be processed by Byteman. The rule definitions
     * found in the files are actually passed down directly to Byteman, not the
     * file paths themselves. Therefore, these files must exist on the machine
     * where this client is running (i.e. the files are not read directly by the
     * Byteman agent).
     *
     * @param filePaths
     *            the local files containing the rule definitions to be deleted
     *            from Byteman
     *
     * @return the results of the deletion
     *
     * @throws Exception
     *             if the request failed
     */
    public String deleteRulesFromFiles(List<String> filePaths) throws Exception {
        Map<String, String> rules = getRulesFromRuleFiles(filePaths);
        return deleteRules(rules);
    }

    /**
     * Deletes rules from Byteman, where the rule definitions are found in the
     * given map's value set. The names of the rule definitions are found as
     * keys in the given map. After this method is done, the given rules will no
     * longer be processed by Byteman.
     *
     * @param rules
     *            rules to be deleted from Byteman (key=rule name, value=rule
     *            definition)
     *
     * @return the results of the deletion
     *
     * @throws Exception
     *             if the request failed
     */
    public String deleteRules(Map<String, String> rules) throws Exception {
        if (rules == null || rules.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("DELETE\n");
        for (Map.Entry<String, String> entry : rules.entrySet()) {
            str.append("SCRIPT " + entry.getKey() + '\n');
            str.append(entry.getValue()).append('\n');
            str.append("ENDSCRIPT\n");
        }
        str.append("ENDDELETE\n");

        Comm comm = new Comm(this.address, this.port);
        comm.print(str.toString());
        String results = comm.readResponse();
        comm.close();
        return results;

    }

    private Map<String, String> getRulesFromRuleFiles(List<String> filePaths) throws Exception {
        if (filePaths == null || filePaths.size() == 0) {
            return new HashMap<String, String>(0);
        }

        final char[] readBuffer = new char[4096];
        Map<String, String> rules = new HashMap<String, String>(filePaths.size());

        for (String filePath : filePaths) {
            // abort if a rule file was invalid - we never submit the request if at least one was invalid
            if (!confirmRuleFileValidity(filePath)) {
                throw new Exception("Invalid rule file: " + filePath);
            }

            // read in the current rule file
            StringBuilder ruleText = new StringBuilder();
            try {
                FileInputStream fis = new FileInputStream(filePath);
                InputStreamReader reader = new InputStreamReader(fis);
                int read = reader.read(readBuffer);
                while (read > 0) {
                    ruleText.append(readBuffer, 0, read);
                    read = reader.read(readBuffer);
                }
                reader.close();

                // put the current rule definition in our list of rules to add
                rules.put(filePath, ruleText.toString());
            } catch (IOException e) {
                throw new Exception("Error reading from rule file: " + filePath, e);
            }
        }

        return rules;
    }

    private boolean confirmRuleFileValidity(String path) {
        // right now, we only check if its a readable file, do we want to see if
        // its parsable, too?
        File file = new File(path);
        if (!file.isFile() || !file.canRead()) {
            return false;
        }
        return true;
    }

    private class Comm {
        private Socket commSocket;
        private BufferedReader commInput;
        private PrintWriter commOutput;

        public Comm(String address, int port) throws Exception {
            this.commSocket = new Socket(address, port);

            InputStream is;
            try {
                is = this.commSocket.getInputStream();
            } catch (Exception e) {
                // oops. cannot handle this
                try {
                    this.commSocket.close();
                } catch (Exception e1) {
                }
                throw e;
            }

            OutputStream os;
            try {
                os = this.commSocket.getOutputStream();
            } catch (Exception e) {
                // oops. cannot handle this
                try {
                    this.commSocket.close();
                } catch (Exception e1) {
                }
                throw e;
            }

            this.commInput = new BufferedReader(new InputStreamReader(is));
            this.commOutput = new PrintWriter(new OutputStreamWriter(os));

            return;
        }

        public void close() {
            try {
                this.commSocket.close(); // also closes the in/out streams
            } catch (Exception e) {
                // TODO what should I do here? no need to abort, we are closing this object anyway
            } finally {
                // this object cannot be reused anymore, therefore, null everything out
                // which will force NPEs if attempts to reuse this object occur later
                this.commSocket = null;
                this.commInput = null;
                this.commOutput = null;
            }
        }

        public void println(String line) {
            this.commOutput.println(line);
            this.commOutput.flush();
        }

        public void print(String line) {
            this.commOutput.print(line);
            this.commOutput.flush();
        }

        public String readResponse() throws Exception {
            StringBuilder str = new StringBuilder();

            String line = this.commInput.readLine();
            while (line != null && !line.trim().equals("OK")) {
                str.append(line.trim()).append('\n');
                line = this.commInput.readLine();
            }

            return str.toString();
        }
    }

    /**
     * A main routine which submits requests to the Byteman agent utilizing the Java API.
     * @param args see {@link #usage(int)} for a description of the allowed arguments
     */
    public static void main(String[] args)
    {
        int port = DEFAULT_PORT;
        String hostname = DEFAULT_ADDRESS;
        int startIdx = 0;
        int maxIdx = args.length;
        boolean deleteRules = false;
        boolean addBoot = false;
        boolean addSys = false;
        int optionCount = 0;

        while (startIdx < maxIdx && args[startIdx].startsWith("-")) {
            if (maxIdx >= startIdx + 2 && args[startIdx].equals("-p")) {
                try {
                    port = Integer.valueOf(args[startIdx+1]);
                } catch (NumberFormatException e) {
                    System.out.println("Submit : invalid port " + args[startIdx+1]);
                    System.exit(1);
                }
                if (port <= 0) {
                    System.out.println("Submit : invalid port " + args[startIdx+1]);
                    System.exit(1);
                }
                startIdx += 2;
            } else if (maxIdx >= startIdx + 2 && args[startIdx].equals("-h")) {
                hostname = args[startIdx+1];
                startIdx += 2;
            } else if (args[startIdx].equals("-u")) {
                deleteRules = true;
                startIdx++;
                optionCount++;
            } else if (args[startIdx].equals("-l")) {
                startIdx++;
                optionCount++;
            } else if (args[startIdx].equals("-b")) {
                addBoot = true;
                startIdx ++;
                optionCount++;
            } else if (args[startIdx].equals("-s")) {
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

        // must have some file args if adding to sys or boot classpath

        if (startIdx == maxIdx && (addBoot || addSys)) {
            usage(1);
        }

        Submit client = new Submit(hostname, port);
        String results = null;
        List<String> argsList = null;

        try {
            if (startIdx == maxIdx) {
                // no args means list or delete all current scripts
                if (deleteRules) {
                    results = client.deleteAllRules();
                } else {
                    // the default behavior (or if -l was explicitly specified) is to do this
                    results = client.listAllRules();
                }
            } else {
                argsList = new ArrayList<String>();
                for (int i = startIdx; i < maxIdx; i++) {
                    argsList.add(args[i]);
                }
                if (addBoot) {
                    results = client.addJarToBootClassloader(argsList);
                } else if (addSys) {
                    results = client.addJarToSystemClassloader(argsList);
                } else {
                    if (deleteRules) {
                        results = client.deleteRulesFromFiles(argsList);
                    } else {
                        // the default behavior (or if -l was explicitly specified) is to do this
                        results = client.addRulesFromFiles(argsList);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to process request: " + e);
            if (argsList != null) {
                System.out.println("-- Args were: " + argsList);
            }
            if (results != null) {
                // rarely will results be non-null on error, but just in case, print it if we got it
                System.out.println("-- Results were: " + results);
            }
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println(results);
    }

    private static void usage(int exitCode)
    {
        System.out.println("usage : Submit [-p port] [-h hostname] [-l|-u] [scriptfile . . .]");
        System.out.println("        Submit [-p port] [-h hostname] [-b|-s] jarfile . . .");
        System.out.println("        -p specifies listener port");
        System.out.println("        -h specifies listener host");
        System.out.println("        -l (default) with scriptfile(s) means load/reload all rules in scriptfile(s)");
        System.out.println("                     with no scriptfile means list all currently loaded rules");
        System.out.println("        -u with scriptfile(s) means unload all rules in scriptfile(s)");
        System.out.println("           with no scriptfile means unload all currently loaded rules");
        System.out.println("        -b with jarfile(s) means add jars to bootstrap classpath");
        System.out.println("        -s with jarfile(s) means add jars to system classpath");
        System.exit(exitCode);
    }
}
