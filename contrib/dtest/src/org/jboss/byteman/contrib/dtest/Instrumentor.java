/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2010,
 * @author JBoss, by Red Hat.
 */
package org.jboss.byteman.contrib.dtest;

import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * The Instrumentor provides for installing tracing and other rules into a remote JVM.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-05
 */
public class Instrumentor
{
    private static final int DEFAULT_RMI_PORT = 1099;

    private final Submit submit;
    private final Registry registry;
    private final int rmiRegistryPort;
    private final Map<String, InstrumentedClass> instrumentedClasses = new HashMap<String, InstrumentedClass>();
    private final List<ScriptText> installedScripts = new LinkedList<ScriptText>();
    private File redirectedSubmissionsFile;

    public Instrumentor(Submit submit, int rmiRegistryPort) throws RemoteException
    {
        this.submit = submit;
        this.rmiRegistryPort = rmiRegistryPort;
        this.registry = LocateRegistry.createRegistry(rmiRegistryPort);
    }

    public Instrumentor() throws RemoteException
    {
        this(new Submit(), DEFAULT_RMI_PORT);
    }

    public Instrumentor(String address, int port) throws RemoteException
    {
        this(new Submit(address, port), DEFAULT_RMI_PORT);
    }

    public Instrumentor(String address, int port, int rmiPort) throws RemoteException
    {
        this(new Submit(address, port), rmiPort);
    }

    /**
     * Add the specified jar to the remote app's system classpath.
     *
     * @param path the absolute path to the .jar file.
     * @throws Exception in case of failure.
     */
    public void installHelperJar(String path) throws Exception
    {
        List<String> jarPaths = new LinkedList<String>();
        jarPaths.add(path);
        submit.addJarsToSystemClassloader(jarPaths);
        Properties properties = new Properties();
        properties.setProperty(BytemanTestHelper.RMIREGISTRY_PORT_PROPERTY_NAME, ""+rmiRegistryPort);
        submit.setSystemProperties(properties);
    }

    /**
     * Add method tracing rules to the specified class.
     *
     * @param clazz the Class to instrument.
     * @return a local proxy for the instrumentation.
     * @throws Exception in case of failure.
     */
    public InstrumentedClass instrumentClass(Class clazz) throws Exception
    {
        return instrumentClass(clazz, null);
    }

    /**
     * Add method tracing rules to the specified class.
     * If a non-null set of method names is supplied, only those methods are instrumented.
     *
     * @param clazz the Class to instrument.
     * @param methodNames the selection of methods to instrument.
     * @return a local proxy for the instrumentation.
     * @throws Exception in case of failure.
     */
    public InstrumentedClass instrumentClass(Class clazz, Set<String> methodNames) throws Exception
    {
        String className = clazz.getCanonicalName();

        StringBuilder ruleScriptBuilder = new StringBuilder();
        for(Method method : clazz.getDeclaredMethods()) {

            String methodName = method.getName();
            if(methodNames == null || methodNames.contains(methodName)) {

                String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_remotetrace_entry";

                RuleBuilder ruleBuilder = new RuleBuilder(ruleName);
                ruleBuilder.onClass(className).inMethod(methodName).atEntry();
                ruleBuilder.usingHelper(BytemanTestHelper.class);
                ruleBuilder.doAction("setTriggering(false), debug(\"firing "+ruleName+"\", $0), remoteTrace(\""+className+"\", \""+methodName+"\", $*)");
                ruleScriptBuilder.append(ruleBuilder.toString());
            }
        }

        String scriptString = ruleScriptBuilder.toString();
        installScript(className+".instrumentationScript", scriptString);

        return publish(className);
    }

    /**
     * Returns the file to which Rule submission is currently redirected
     *
     * @return a file, or null if no redirection is in effect.
     */
    public File getRedirectedSubmissionsFile()
    {
        return redirectedSubmissionsFile;
    }

    /**
     * Sets the file to which Rule submissions should be redirected.
     *
     * @param redirectedSubmissionsFile a file, or null to cancel any existing redirection.
     */
    public void setRedirectedSubmissionsFile(File redirectedSubmissionsFile)
    {
        this.redirectedSubmissionsFile = redirectedSubmissionsFile;
    }

    /**
     * Inject an action to take place upon the invocation of the specified class.method
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @throws Exception in case of failure.
     */
    public void injectOnCall(Class clazz, String methodName, String action) throws Exception
    {
        injectOnMethod(clazz, methodName, "true", action, "ENTRY");
    }

    /**
     * Inject an action to take place upon exit of the specified class.method
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @throws Exception in case of failure.
     */
    public void injectOnExit(Class clazz, String methodName, String action) throws Exception
    {
        injectOnMethod(clazz, methodName, "true", action, "EXIT");
    }

    /**
     * Inject an action to take place at a given point within the specified class.method
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @param where the injection point e.g. "ENTRY".
     * @throws Exception in case of failure.
     */
    public void injectOnMethod(Class clazz, String methodName, String condition, String action, String where) throws Exception
    {
        String className = clazz.getCanonicalName();
        String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_injectionat"+where;

        RuleBuilder ruleBuilder = new RuleBuilder(ruleName);
        ruleBuilder.onClass(className).inMethod(methodName).at(where);
        ruleBuilder.usingHelper(BytemanTestHelper.class);
        ruleBuilder.when(condition).doAction(action);

        String ruleText = ruleBuilder.toString();
        installScript("onCall"+className+"."+methodName+"."+where, ruleText);
    }

    /**
     * Inject a fault (i.e. Exception) to be thrown upon the invocation of the specified Class.method()
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param fault The type of Exception to be throw. If a checked exception, must be declared thrown by the specified method.
     * @param faultArgs Optional constructor arguments for the Exception.
     * @throws Exception in case of failure.
     */
    public void injectFault(Class clazz, String methodName, Class<? extends Throwable> fault, Object[] faultArgs) throws Exception
    {
        String className = clazz.getCanonicalName();
        String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_faultinjection";

        StringBuilder actionBuilder = new StringBuilder();
        actionBuilder.append("setTriggering(false), debug(\"firing "+ruleName+"\", $0), ");
        actionBuilder.append("throw new "+fault.getCanonicalName()+"(");
        if(faultArgs != null) {
            for(int i = 0; i < faultArgs.length; i++) {
                String argClassName = faultArgs[i].getClass().getCanonicalName();
                boolean requireQuotes = true;
                if(argClassName.startsWith("java.lang.") && !argClassName.equals("java.lang.String")) {
                    requireQuotes = false;
                }
                if(requireQuotes) {
                    actionBuilder.append("\"");
                }
                actionBuilder.append(faultArgs[i]);
                if(requireQuotes) {
                    actionBuilder.append("\"");
                }
                if(i != faultArgs.length-1) {
                    actionBuilder.append(", ");
                }
            }
        }
        actionBuilder.append(")"+"\n");

        RuleBuilder ruleBuilder = new RuleBuilder(ruleName);
        ruleBuilder.onClass(className).inMethod(methodName).atEntry();
        ruleBuilder.usingHelper(BytemanTestHelper.class);
        ruleBuilder.whenTrue().doAction(actionBuilder.toString());        

        installScript("fault"+className+"."+methodName, ruleBuilder.toString());
    }

    /**
     * Inject a Rule to kill the target JVM upon exit of the specified Class.method()
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @throws Exception in case of failure.
     */
    public void crashAtMethodExit(Class clazz, String methodName) throws Exception
    {
        String className = clazz.getCanonicalName();
        crashAtMethod(className, methodName, "EXIT");
    }

    /**
     * Inject a Rule to kill the target JVM upon exit of the specified Class.method()
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @throws Exception in case of failure.
     */
    public void crashAtMethodExit(String className, String methodName) throws Exception
    {
        crashAtMethod(className, methodName, "EXIT");
    }

    /**
     * Inject a Rule to kill the target JVM upon entry to the specified Class.method()
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @throws Exception in case of failure.
     */
    public void crashAtMethodEntry(Class clazz, String methodName) throws Exception
    {
        String className = clazz.getCanonicalName();
        crashAtMethod(className, methodName, "ENTRY");
    }

    /**
     * Inject a Rule to kill the target JVM upon entry to the specified Class.method()
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @throws Exception in case of failure.
     */
    public void crashAtMethodEntry(String className, String methodName) throws Exception
    {
        crashAtMethod(className, methodName, "ENTRY");
    }

    /**
     * Inject a Rule to kill the target JVM at a given point within the specified Class.method()
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param where the injection point e.g. "ENTRY".
     * @throws Exception in case of failure.
     */
    public void crashAtMethod(String className, String methodName, String where) throws Exception
    {
        String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_crashat"+where;

        String action = "debug(\"killing JVM\"), killJVM()";

        RuleBuilder ruleBuilder = new RuleBuilder(ruleName);
        ruleBuilder.onClass(className).inMethod(methodName).at(where);
        ruleBuilder.usingHelper(BytemanTestHelper.class);
        ruleBuilder.whenTrue().doAction(action);
        
        installScript("crash"+className+"."+methodName+"."+where, ruleBuilder.toString());
    }

    /**
     * Pass the assembled script to the remote JVM, either via. the Submit or, if redirection is in effect, to a file
     * which will then be read at restart of the remote JVM. Keep a local handle on the script, such that it can be
     * removed on request.
     *
     * @param scriptName The name of the script. Should be unique.
     * @param scriptString The text of the script i.e. one or more Rules.
     * @throws Exception in case of failure.
     */
    public void installScript(String scriptName, String scriptString)
            throws Exception
    {
        System.out.println("installing: "+scriptString);

        if(scriptString.length() > 0) {
            if(redirectedSubmissionsFile == null) {
                ScriptText scriptText = new ScriptText(scriptName, scriptString);
                List<ScriptText> scriptTexts = new LinkedList<ScriptText>();
                scriptTexts.add(scriptText);
                submit.addScripts(scriptTexts);
                installedScripts.addAll(scriptTexts);
            } else {
                appendToFile(redirectedSubmissionsFile, scriptString);
                ScriptText installedScriptText = null;
                ScriptText updatedScriptText = null;
                for(ScriptText scriptText : installedScripts) {
                    if(scriptText.getFileName().equals(redirectedSubmissionsFile.getCanonicalPath())) {
                        installedScriptText = scriptText;
                    }
                }
                if(installedScriptText != null) {
                    installedScripts.remove(installedScriptText);
                    updatedScriptText = new ScriptText(installedScriptText.getFileName(), installedScriptText.getText()+scriptString);
                } else {
                    updatedScriptText = new ScriptText(redirectedSubmissionsFile.getCanonicalPath(), scriptString);
                }
                installedScripts.add(updatedScriptText);
            }
        }
    }

    /**
     * Write the given text to the end of the file.
     *
     * @param file a file name.
     * @param rule the text to append to the file.
     * @throws Exception in case of failure.
     */
    private void appendToFile(File file, String rule) throws Exception
    {
        Writer writer = new FileWriter(file, true);
        writer.write(rule);
        writer.close();
    }

    /**
     * Create a local communication endpoint for the the given class.
     *
     * @param className the class to create the wrapper for.
     * @return a local handle for accessing trace information received from the remote instrumentation.
     * @throws Exception in case of failure.
     */
    private synchronized InstrumentedClass publish(String className) throws Exception
    {
        if(instrumentedClasses.containsKey(className))
        {
            return instrumentedClasses.get(className);
        }

        InstrumentedClass instrumentedClass = new InstrumentedClass(className);
        RemoteInterface stub = (RemoteInterface) UnicastRemoteObject.exportObject(instrumentedClass, 0);
        registry.rebind(className, stub);
        instrumentedClasses.put(className, instrumentedClass);
        return instrumentedClass;
    }

    /**
     * Remove the local communication endpoint for the given class.
     *
     * @param className the class to remove.
     * @throws Exception in case of failure.
     */
    private void unpublish(String className) throws Exception
    {
        registry.unbind(className);
    }

    /**
     * Flush the local cache of scripts and proxies to remote instrumented classes.
     * Useful to reset local state when a remote JVM is crashed and hence reset.
     *
     * @throws Exception in case of failure.
     */
    public void removeLocalState() throws Exception
    {
        for(String instrumentedClassName : instrumentedClasses.keySet())
        {
            unpublish(instrumentedClassName);
        }
        instrumentedClasses.clear();
        installedScripts.clear();
    }

    /**
     * Flush any instrumentation for the given class in the remote system and clean up the local cache.
     *
     * @throws Exception in case of failure.
     */
    public void removeAllInstrumentation() throws Exception
    {
        submit.deleteScripts(installedScripts);
        removeLocalState();
    }
}
