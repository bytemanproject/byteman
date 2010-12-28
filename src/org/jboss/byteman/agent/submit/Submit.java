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

package org.jboss.byteman.agent.submit;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private PrintStream out;

    /**
     * Create a client that will connect to a Byteman agent on the default host
     * and port and writing output to System.out.
     */
    public Submit() {
        this(DEFAULT_ADDRESS, DEFAULT_PORT, System.out);
    }

    /**
     * Create a client that will connect to a Byteman agent on the given host
     * and port and writing output to System.out.
     *
     * @param address
     *            the hostname or IP address of the machine where Byteman agent
     *            is located. If <code>null</code>, the default host is used.
     * @param port
     *            the port that the Byteman agent is listening to.
     *            If 0 or less, the default port is used.
     */
    public Submit(String address, int port) {
        this(address, port, System.out);
    }

    /**
     * Create a client that will connect to a Byteman agent on the given host
     * and port and writing output to System.out.
     *
     * @param address
     *            the hostname or IP address of the machine where Byteman agent
     *            is located. If <code>null</code>, the default host is used.
     * @param port
     *            the port that the Byteman agent is listening to.
     *            If 0 or less, the default port is used.
     */
    public Submit(String address, int port, PrintStream out) {
        if (address == null) {
            address = DEFAULT_ADDRESS;
        }

        if (port <= 0) {
            port = DEFAULT_PORT;
        }

        if (out == null) {
            out = System.out;
        }

        this.address = address;
        this.port = port;
        this.out = out;
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
     * Returns the version of the remote Byteman agent.
     *
     * @return the version of the remote Byteman agent
     *
     * @throws Exception
     *             if the request failed
     */
    public String getAgentVersion() throws Exception {
        String version = submitRequest("VERSION\n");
        return (version != null) ? version.trim() : "0";
    }

    /**
     * Returns the version of this Byteman submit client.
     *
     * @return the version of the submit client, or <code>null</code> if unknown
     *
     * @throws Exception
     *             if the request failed
     */
    public String getClientVersion() throws Exception {
        return this.getClass().getPackage().getImplementationVersion();
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
        return submitRequest("DELETEALL\n");
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
        return submitRequest("LIST\n");
    }

    /**
     * Gets all deployed rules from the agent just as
     * {@link #listAllRules()}, but will return the rules
     * organized by script (i.e. rule file). Each "script",
     * or rule file, has a set of rules associated with it.
     *
     * @return all the scripts deployed in the Byteman agent
     *         the keys are the script names (typically this is
     *         the filenames where the rule definitions were found);
     *         the values are the rule definitions in the scripts
     *
     * @throws Exception
     *             if the request failed
     */
    public List<ScriptText> getAllScripts() throws Exception {
        // we use this to retain the order in which script file names occur
        List<String> scriptFileNames = new ArrayList<String>();
        Map<String, String> rulesByScript = new HashMap<String, String>();

        Pattern scriptHeaderPattern = Pattern.compile("# File (.*) line \\d+\\s*");
        Pattern ruleNamePattern = Pattern.compile("\\s*RULE\\s+(.+)\\s*");
        Pattern endRuleNamePattern = Pattern.compile("\\s*ENDRULE\\s*");
        Matcher matcher;

        String currentScriptName = null;
        String currentRuleName = null; // will be non-null while we are parsing actual rule code
        StringBuilder currentScriptText = new StringBuilder();

        String allRules = listAllRules();
        BufferedReader reader = new BufferedReader(new StringReader(allRules));
        String line = reader.readLine();
        while (line != null) {
            matcher = scriptHeaderPattern.matcher(line);
            if (matcher.matches()) {
                // found a new script header; squirrel away the current script text
                // and get ready for this new script
                if (currentScriptName != null) {
                    rulesByScript.put(currentScriptName, currentScriptText.toString());
                }
                currentScriptName = matcher.group(1);
                if (rulesByScript.containsKey(currentScriptName)) {
                    currentScriptText = new StringBuilder(rulesByScript.get(currentScriptName));
                } else {
                    currentScriptText = new StringBuilder();
                    // track the new file name
                    scriptFileNames.add(currentScriptName);
                }
            } else {
                matcher = ruleNamePattern.matcher(line);
                if (matcher.matches()) {
                    // found a new rule; definition that follows belong to this new rule
                    currentRuleName = matcher.group(1);
                    currentScriptText.append(line).append('\n');
                } else {
                    matcher = endRuleNamePattern.matcher(line);
                    if (matcher.matches()) {
                        // reached the end of the current rule
                        if (currentRuleName != null) {
                            currentRuleName = null;
                            currentScriptText.append(line).append('\n');
                        }
                    } else {
                        // line is basic rule text, a comment or miscellaneous.
                        // if we are currently processing inside a rule, we'll add the line to the script
                        // otherwise, we ignore the line since its not part of a rule definition.
                        if (currentRuleName != null) {
                            currentScriptText.append(line).append('\n');
                        }
                    }
                }
            }
            line = reader.readLine();
        }

        // finish up by adding the last script we encountered
        if (currentScriptName != null && currentScriptText.length() > 0) {
            rulesByScript.put(currentScriptName, currentScriptText.toString());
        }

        // now create a script text object for each script file we found

        List<ScriptText> scriptTexts = new ArrayList<ScriptText>(scriptFileNames.size());
        for (String fileName : scriptFileNames) {
            String text = rulesByScript.get(fileName);
            scriptTexts.add(new ScriptText(fileName, text));
        }

        return scriptTexts;
    }

    /**
     * old version which returns a map rather than a list of scripts
     * @return
     * @throws Exception
     */
    @Deprecated
    public Map<String,String> getAllRules() throws Exception {
        Map<String, String> rulesByScript = new HashMap<String, String>();

        Pattern scriptHeaderPattern = Pattern.compile("# File (.*) line \\d+\\s*");
        Pattern ruleNamePattern = Pattern.compile("\\s*RULE\\s+(.+)\\s*");
        Pattern endRuleNamePattern = Pattern.compile("\\s*ENDRULE\\s*");
        Matcher matcher;

        String currentScriptName = null;
        String currentRuleName = null; // will be non-null while we are parsing actual rule code
        StringBuilder currentScriptText = new StringBuilder();

        String allRules = listAllRules();
        BufferedReader reader = new BufferedReader(new StringReader(allRules));
        String line = reader.readLine();
        while (line != null) {
            matcher = scriptHeaderPattern.matcher(line);
            if (matcher.matches()) {
                // found a new script header; squirrel away the current script text
                // and get ready for this new script
                if (currentScriptName != null) {
                    rulesByScript.put(currentScriptName, currentScriptText.toString());
                }
                currentScriptName = matcher.group(1);
                if (rulesByScript.containsKey(currentScriptName)) {
                    currentScriptText = new StringBuilder(rulesByScript.get(currentScriptName));
                } else {
                    currentScriptText = new StringBuilder();
                }
            } else {
                matcher = ruleNamePattern.matcher(line);
                if (matcher.matches()) {
                    // found a new rule; definition that follows belong to this new rule
                    currentRuleName = matcher.group(1);
                    currentScriptText.append(line).append('\n');
                } else {
                    matcher = endRuleNamePattern.matcher(line);
                    if (matcher.matches()) {
                        // reached the end of the current rule
                        if (currentRuleName != null) {
                            currentRuleName = null;
                            currentScriptText.append(line).append('\n');
                        }
                    } else {
                        // line is basic rule text, a comment or miscellaneous.
                        // if we are currently processing inside a rule, we'll add the line to the script
                        // otherwise, we ignore the line since its not part of a rule definition.
                        if (currentRuleName != null) {
                            currentScriptText.append(line).append('\n');
                        }
                    }
                }
            }
            line = reader.readLine();
        }

        // finish up by adding the last script we encountered
        if (currentScriptName != null && currentScriptText.length() > 0) {
            rulesByScript.put(currentScriptName, currentScriptText.toString());
        }

        return rulesByScript;
    }

    /**
     * Given the content of a script (which will be one or more
     * rule definitions), this will return each rule definition
     * as an individual string within the returned list.
     * The returned list will be ordered - that is, the first
     * rule in the list is the first rule encountered in the script.
     *
     * One usage of this method is to pass in map values from the results
     * of {@link #getAllScripts()} in case you need the scripts' individual rules.
     *
     * @param scriptContent
     *            the actual content of a script (i.e. the rule definitions)
     *
     * @return all the rule definitions found in the given script
     */
    public List<String> splitAllRulesFromScript(String scriptContent) throws Exception {
        List<String> rules = new ArrayList<String>();

        if (scriptContent == null || scriptContent.length() == 0) {
            return rules;
        }

        Pattern ruleNamePattern = Pattern.compile("\\s*RULE\\s+(.+)\\s*");
        Pattern endRuleNamePattern = Pattern.compile("\\s*ENDRULE\\s*");
        Matcher matcher;

        String currentRuleName = null; // will be non-null while we are parsing actual rule code
        StringBuilder currentRuleText = new StringBuilder();

        BufferedReader reader = new BufferedReader(new StringReader(scriptContent));
        String line = reader.readLine();
        while (line != null) {
            matcher = ruleNamePattern.matcher(line);
            if (matcher.matches()) {
                // found a new rule; definition that follows belong to this new rule
                currentRuleName  = matcher.group(1);
                currentRuleText = new StringBuilder();
                currentRuleText.append(line).append('\n');
            } else {
                matcher = endRuleNamePattern.matcher(line);
                if (matcher.matches()) {
                    // reached the end of the current rule
                    if (currentRuleName != null) {
                        currentRuleName = null;
                        currentRuleText.append(line).append('\n');
                        rules.add(currentRuleText.toString());
                    }
                } else {
                    // line is basic rule text, a comment or miscellaneous.
                    // if we are currently processing inside a rule, we'll add the line to the script
                    // otherwise, we ignore the line since its not part of a rule definition.
                    if (currentRuleName != null) {
                        currentRuleText.append(line).append('\n');
                    }
                }
            }
            line = reader.readLine();
        }

        // finish up by adding the last script we encountered.
        // the only time this code would be needed is if we were missng an ENDRULE line,
        // but that would not be valid rule code, so we should never really get inside this if-statement
        if (currentRuleName != null && currentRuleText.length() > 0) {
            rules.add(currentRuleText.toString());
        }

        return rules;
    }


    /**
     * Given the content of an individual rule definition, this will
     * return the name of that rule.
     *
     * @param ruleDefinition
     *            the actual content of an individual rule
     *
     * @return the name of the given rule, or <code>null</code> if it could not be determined
     */
    public String determineRuleName(String ruleDefinition) throws Exception {
        Pattern ruleNamePattern = Pattern.compile("\\s*RULE\\s+(.+)\\s*");
        Matcher matcher;
        String ruleName = null;
        BufferedReader reader = new BufferedReader(new StringReader(ruleDefinition));
        String line = reader.readLine();
        while (line != null && ruleName == null) {
            matcher = ruleNamePattern.matcher(line);
            if (matcher.matches()) {
                ruleName = matcher.group(1);
            }
            line = reader.readLine();
        }
        return ruleName;
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
    public String addJarsToBootClassloader(List<String> jarPaths) throws Exception {
        if (jarPaths == null || jarPaths.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("BOOT\n");
        for (String jarPath : jarPaths) {
            str.append(jarPath).append("\n");
        }
        str.append("ENDBOOT\n");

        return submitRequest(str.toString());
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
    public String addJarsToSystemClassloader(List<String> jarPaths) throws Exception {
        if (jarPaths == null || jarPaths.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("SYS\n");
        for (String jarPath : jarPaths) {
            str.append(jarPath).append("\n");
        }
        str.append("ENDSYS\n");

        return submitRequest(str.toString());
    }

    /**
     * Returns a list of jars that were added to the Byteman agent's boot classloader.
     *
     * @return list of jars that were added to the boot classloader
     *
     * @throws Exception
     *             if the request failed
     */
    public List<String> getLoadedBootClassloaderJars() throws Exception {
        String results = submitRequest("LISTBOOT\n");
        List<String> jars = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(results));
        String line = reader.readLine();
        while (line != null) {
            jars.add(line);
            line = reader.readLine();
        }
        return jars;
    }

    /**
     * Returns a list of jars that were added to the Byteman agent's system classloader.
     *
     * @return list of jars that were added to the system classloader
     *
     * @throws Exception
     *             if the request failed
     */
    public List<String> getLoadedSystemClassloaderJars() throws Exception {
        String results = submitRequest("LISTSYS\n");
        List<String> jars = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(results));
        String line = reader.readLine();
        while (line != null) {
            jars.add(line);
            line = reader.readLine();
        }
        return jars;
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
        List<ScriptText> scripts = getRulesFromRuleFiles(filePaths);
        return addScripts(scripts);
    }

    /**
     * Deploys rule scripts into Byteman
     *
     * @param scripts
     *            scripts to be deployed to Byteman
     *
     * @return the results of the deployment
     *
     * @throws Exception
     *             if the request failed
     */
    public String addScripts(List<ScriptText> scripts) throws Exception {
        if (scripts == null || scripts.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("LOAD\n");
        for (ScriptText scriptText : scripts) {
            str.append("SCRIPT " + scriptText.getFileName() + '\n');
            str.append(scriptText.getText()).append('\n');
            str.append("ENDSCRIPT\n");
        }
        str.append("ENDLOAD\n");

        return submitRequest(str.toString());
    }

    /**
     * old version which uses a Map
     * @param rules
     * @return
     * @throws Exception
     */
    @Deprecated
    public String addRules(Map<String,String> rules) throws Exception {
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

        return submitRequest(str.toString());
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
        List<ScriptText> scripts = getRulesFromRuleFiles(filePaths);
        return deleteScripts(scripts);
    }

    /**
     * Deletes rules from Byteman.
     *
     * @param scripts
     *            rule scripts to be deleted from Byteman
     *
     * @return the results of the deletion
     *
     * @throws Exception
     *             if the request failed
     */
    public String deleteScripts(List<ScriptText> scripts) throws Exception {
        if (scripts == null || scripts.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("DELETE\n");
        for (ScriptText scriptText : scripts) {
            str.append("SCRIPT " + scriptText.getFileName() + '\n');
            str.append(scriptText.getText()).append('\n');
            str.append("ENDSCRIPT\n");
        }
        str.append("ENDDELETE\n");

        return submitRequest(str.toString());
    }

    /**
     * old version which uses a Map
     * @param rules
     * @return
     * @throws Exception
     */
    @Deprecated
    public String deleteRules(Map<String,String> rules) throws Exception {
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

        return submitRequest(str.toString());
    }
    /**
     * Sets system properties in the Byteman agent VM.
     * If Byteman was configured for strict mode, only Byteman related
     * system properties will be allowed to be set.
     * 
     * @param propsToSet
     *            system properties to set in the Byteman agent VM
     *
     * @return response from the Byteman agent
     *
     * @throws Exception
     *             if the request failed
     */
    public String setSystemProperties(Properties propsToSet) throws Exception {
        if (propsToSet == null || propsToSet.size() == 0) {
            return "";
        }

        StringBuilder str = new StringBuilder("SETSYSPROPS\n");
        for (Map.Entry<Object, Object> entry : propsToSet.entrySet()) {
            str.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
        }
        str.append("ENDSETSYSPROPS\n");

        return submitRequest(str.toString());
    }

    /**
     * Returns the system properties set in the Byteman agent VM.
     * If Byteman was configured for strict mode, only Byteman related
     * system properties will be returned.
     *
     * @return system properties defined in the Byteman agent VM
     *
     * @throws Exception
     *             if the request failed
     */
    public Properties listSystemProperties() throws Exception {
        String results = submitRequest("LISTSYSPROPS\n");
        Properties props = new Properties();
        BufferedReader reader = new BufferedReader(new StringReader(results));
        String line = reader.readLine();
        while (line != null) {
            String[] nameValuePair = line.split("=", 2);
            if (nameValuePair.length != 2) {
                throw new Exception("Invalid name/value pair in line [" + line + "]. Full response below:\n" + results);
            }
            props.setProperty(nameValuePair[0], nameValuePair[1].replace("\\n", "\n").replace("\\r", "\r"));
            line = reader.readLine();
        }
        return props;
    }

    /**
     * Submits the generic request string to the Byteman agent for processing.
     *
     * @param request
     *            the request to submit
     *
     * @return the response that the Byteman agent replied with
     *
     * @throws Exception
     *             if the request failed
     */
    public String submitRequest(String request) throws Exception {
        Comm comm = new Comm(this.address, this.port);
        try {
            comm.print(request);
            String results = comm.readResponse();
            return results;
        } finally {
            comm.close();
        }
    }

    private List<ScriptText> getRulesFromRuleFiles(List<String> filePaths) throws Exception {
        if (filePaths == null || filePaths.size() == 0) {
            return new ArrayList<ScriptText>(0);
        }

        final char[] readBuffer = new char[4096];
        List<ScriptText> scripts = new ArrayList<ScriptText>(filePaths.size());

        for (String filePath : filePaths) {
            // abort if a script file was invalid - we never submit the request if at least one was invalid
            if (!confirmRuleFileValidity(filePath)) {
                throw new Exception("Invalid rule file: " + filePath);
            }

            // read in the current rule file
            StringBuilder scriptText = new StringBuilder();
            try {
                FileInputStream fis = new FileInputStream(filePath);
                InputStreamReader reader = new InputStreamReader(fis);
                int read = reader.read(readBuffer);
                while (read > 0) {
                    scriptText.append(readBuffer, 0, read);
                    read = reader.read(readBuffer);
                }
                reader.close();

                // put the current rule definition in our list of rules to add
                scripts.add(new ScriptText(filePath, scriptText.toString()));
            } catch (IOException e) {
                throw new Exception("Error reading from rule file: " + filePath, e);
            }
        }

        return scripts;
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
            StringBuilder errorStr = null; // will be non-null if an error was reported by the agent

            String line = this.commInput.readLine();
            while (line != null && !line.trim().equals("OK")) {
                line = line.trim();

                if (line.startsWith("ERROR") || line.startsWith("EXCEPTION")) {
                    if (errorStr == null) {
                        errorStr = new StringBuilder();
                    }
                }

                // if an error was detected, gobble up the text coming over the wire as part of the error message
                if (errorStr != null) {
                    errorStr.append(line).append('\n');
                }

                str.append(line).append('\n');
                line = this.commInput.readLine();
            }

            if (errorStr != null) {
                StringBuilder msg = new StringBuilder();
                msg.append("The remote byteman agent reported an error:\n").append(errorStr);
                if (!errorStr.toString().equals(str.toString())) {
                    msg.append("\nThe full response received from the byteman agent follows:\n").append(str);
                }
                throw new Exception(msg.toString());
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
        String outfile = null;
        int port = DEFAULT_PORT;
        String hostname = DEFAULT_ADDRESS;
        int startIdx = 0;
        int maxIdx = args.length;
        boolean deleteRules = false;
        boolean addBoot = false;
        boolean addSys = false;
        boolean showVersion = false;
        boolean showAddedClassloaderJars = false;
        boolean sysProps = false;
        int optionCount = 0;
        PrintStream out = System.out;

        while (startIdx < maxIdx && args[startIdx].startsWith("-")) {
            if (maxIdx >= startIdx + 2 && args[startIdx].equals("-o")) {
                outfile = args[startIdx+1];
                File file =  new File(outfile);
                if (file.exists()) {
                    // open for append
                    if (file.isDirectory() || !file.canWrite()) {
                        out.println("Submit : invalid output file " + outfile);
                        System.exit(1);
                    }
                    FileOutputStream fos = null;
                    try {
                        fos =  new FileOutputStream(file, true);
                    } catch (FileNotFoundException e) {
                        out.println("Submit : error opening output file " + outfile);
                    }
                    out = new PrintStream(fos);
                } else {
                    FileOutputStream fos = null;
                    try {
                        fos =  new FileOutputStream(file, true);
                    } catch (FileNotFoundException e) {
                        out.println("Submit : error opening output file " + outfile);
                    }
                    out = new PrintStream(fos);
                }
                startIdx += 2;
            } else if (maxIdx >= startIdx + 2 && args[startIdx].equals("-p")) {
                try {
                    port = Integer.valueOf(args[startIdx+1]);
                } catch (NumberFormatException e) {
                    out.println("Submit : invalid port " + args[startIdx+1]);
                    System.exit(1);
                }
                if (port <= 0) {
                    out.println("Submit : invalid port " + args[startIdx+1]);
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
            } else if (args[startIdx].equals("-v")) {
                showVersion = true;
                startIdx ++;
                optionCount++;
            } else if (args[startIdx].equals("-c")) {
                showAddedClassloaderJars = true;
                startIdx ++;
                optionCount++;
            } else if (args[startIdx].equals("-s")) {
                addSys = true;
                startIdx ++;
                optionCount++;
            } else if (args[startIdx].equals("-y")) {
                sysProps = true;
                startIdx++;
                optionCount++;
            } else {
                break;
            }
        }

        if (startIdx < maxIdx && args[startIdx].startsWith("-") || optionCount > 1) {
            usage(out, 1);
        }

        // must have some file args if adding to sys or boot classpath

        if (startIdx == maxIdx && (addBoot || addSys)) {
            usage(out, 1);
        }

        Submit client = new Submit(hostname, port, out);
        String results = null;
        List<String> argsList = null;

        try {
            if (showVersion) {
                String agentVersion = client.getAgentVersion();
                String clientVersion = client.getClientVersion();
                results = "Agent Version: " + agentVersion + "\nClient Version: " + clientVersion;
            } else if (showAddedClassloaderJars) {
                List<String> bootJars = client.getLoadedBootClassloaderJars();
                List<String> sysJars = client.getLoadedSystemClassloaderJars();
                StringBuilder str = new StringBuilder();
                str.append("Boot Classloader Jars:").append('\n');
                if (bootJars.isEmpty()) {
                    str.append("\t<none>\n");
                } else {
                    for (String jar : bootJars) {
                        str.append('\t').append(jar).append('\n');
                    }
                }
                str.append("System Classloader Jars:").append('\n');
                if (sysJars.isEmpty()) {
                    str.append("\t<none>\n");
                } else {
                    for (String jar : sysJars) {
                        str.append('\t').append(jar).append('\n');
                    }
                }
                results = str.toString();
            } else {
                if (startIdx == maxIdx) {
                    // no args means list or delete all current scripts or list sysprops
                    if (deleteRules) {
                        results = client.deleteAllRules();
                    } else if (sysProps) {
                        Properties props = client.listSystemProperties();
                        StringBuilder str = new StringBuilder();
                        for (Map.Entry<Object, Object> prop : props.entrySet()) {
                            str.append(prop.getKey()).append('=').append(prop.getValue()).append('\n');
                        }
                        results = str.toString();
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
                        results = client.addJarsToBootClassloader(argsList);
                    } else if (addSys) {
                        results = client.addJarsToSystemClassloader(argsList);
                    } else if (sysProps) {
                        Properties propsToSet = new Properties();
                        for (String arg : argsList) {
                            String[] nameValuePair = arg.split("=", 2);
                            if (nameValuePair.length != 2) {
                                throw new Exception("Invalid name/value pair: " + arg);
                            }
                            propsToSet.setProperty(nameValuePair[0], nameValuePair[1]);
                        }
                        results = client.setSystemProperties(propsToSet);
                    } else {
                        if (deleteRules) {
                            results = client.deleteRulesFromFiles(argsList);
                        } else {
                            // the default behavior (or if -l was explicitly specified) is to do this
                            results = client.addRulesFromFiles(argsList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            out.println("Failed to process request: " + e);
            if (argsList != null) {
                out.println("-- Args were: " + argsList);
            }
            if (results != null) {
                // rarely will results be non-null on error, but just in case, print it if we got it
                out.println("-- Results were: " + results);
            }
            e.printStackTrace();
            System.exit(1);
        }

        out.println(results);
        if (out != System.out) {
            out.close();
        }
    }

    private static void usage(PrintStream out, int exitCode)
    {
        out.println("usage : Submit [-o outfile] [-p port] [-h hostname] [-l|-u] [scriptfile . . .]");
        out.println("        Submit [-o outfile] [-p port] [-h hostname] [-b|-s] jarfile . . .");
        out.println("        Submit [-o outfile] [-p port] [-h hostname] [-c]");
        out.println("        Submit [-o outfile] [-p port] [-h hostname] [-y] [prop1[=[value1]]. . .]");
        out.println("        Submit [-o outfile] [-p port] [-h hostname] [-v]");
        out.println("        -o redirects output from System.out to outfile");
        out.println("        -p specifies listener port");
        out.println("        -h specifies listener host");
        out.println("        -l (default) with scriptfile(s) means load/reload all rules in scriptfile(s)");
        out.println("                     with no scriptfile means list all currently loaded rules");
        out.println("        -u with scriptfile(s) means unload all rules in scriptfile(s)");
        out.println("           with no scriptfile means unload all currently loaded rules");
        out.println("        -b with jarfile(s) means add jars to bootstrap classpath");
        out.println("        -s with jarfile(s) means add jars to system classpath");
        out.println("        -c prints the jars that have been added to the system and boot classloaders");
        out.println("        -y with no args list all byteman config system properties");
        out.println("           with args modifies specified byteman config system properties");
        out.println("             prop=value sets system property 'prop' to value");
        out.println("             prop= sets system property 'prop' to an empty string");
        out.println("             prop unsets system property 'prop'");
        out.println("        -v prints the version of the byteman agent and this client");
        if (out != System.out) {
            out.close();
        }
        System.exit(exitCode);
    }
}
