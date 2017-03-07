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
    private Class<?> helperClass = BytemanTestHelper.class;

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
     * Returns a helper class which this {@link Instrumentor} instance defines
     * as parameter of <code>HELPER</code> clause.
     * @return the helper class
     */
    public Class<?> getHelperClass() {
        return this.helperClass;
    }

    /**
     * <p>
     * Redefine a helper class which is used as parameter of <code>HELPER</code> clause
     * by this instance of {@link Instrumentor}
     * <p>
     * When setting you will probably create your own byteman helper class which extends the default
     * one {@link BytemanTestHelper}.<br>
     * You need to know what you are doing when setting this parameter different from default helper
     * implementation as it provides core functionality for <code>dtest</code> library.
     * @param helperClass the new helper class
     */
    public void setHelperClass(Class<?> helperClass) {
        this.helperClass = helperClass;
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

        Set<String> methodNamesToInstrument = new HashSet<String>();

        for(Method method : clazz.getDeclaredMethods()) {
            String declaredMethodName = method.getName();
            if(methodNames == null || methodNames.contains(declaredMethodName)) {
                methodNamesToInstrument.add(declaredMethodName);
            }
        }

        return instrumentClass(className, methodNamesToInstrument);
    }

    /**
     * Add method tracing rules to the specified class name.<br>
     * If a null set of method names is supplied, {@link NullPointerException} is thrown.
     *
     * @param className the class name to instrument.
     * @param methodNames the selection of methods to instrument.
     * @return a local proxy for the instrumentation.
     * @throws NullPointerException in case of methodNames parameter is null
     * @throws Exception in case of failure.
     */
    public InstrumentedClass instrumentClass(String className, Set<String> methodNames) throws Exception
    {
        if(methodNames == null) {
            throw new NullPointerException("methodNames");
        }

        Set<String> instrumentedMethods = new HashSet<String>();

        StringBuilder ruleScriptBuilder = new StringBuilder();
        for(String methodName : methodNames) {

            if(instrumentedMethods.contains(methodName)) {
                // do not add two identical rules for methods which differ by parameters
                continue;
            }


            String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_remotetrace_entry";

            RuleConstructor.ClassClause builderClassClause = RuleConstructor.createRule(ruleName);
            RuleConstructor.MethodClause builderMethodClause =
                isInterface(className) ? builderClassClause.onInterface(className) : builderClassClause.onClass(className);

                RuleConstructor builder = builderMethodClause
                    .inMethod(methodName)
                    .atEntry()
                    .helper(helperClass)
                    .ifTrue()
                    .doAction("setTriggering(false), debug(\"firing "+ruleName+"\", $0), remoteTrace(\""+className+"\", \""+methodName+"\", $*)");

                ruleScriptBuilder.append(builder.build());

                instrumentedMethods.add(methodName);
        }

        String scriptString = ruleScriptBuilder.toString();
        installScript(className+".instrumentationScript", scriptString);

        return publish(className);
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
        injectOnCall(clazz.getCanonicalName(), methodName, action);
    }

    /**
     * Inject an action to take place upon the invocation of the specified class.method
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @throws Exception in case of failure.
     */
    public void injectOnCall(String className, String methodName, String action) throws Exception
    {
        injectOnMethod(className, methodName, "true", action, "ENTRY");
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
        injectOnExit(clazz.getCanonicalName(), methodName, action);
    }

    /**
     * Inject an action to take place upon exit of the specified class.method
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @throws Exception in case of failure.
     */
    public void injectOnExit(String className, String methodName, String action) throws Exception
    {
        injectOnMethod(className, methodName, "true", action, "EXIT");
    }

    /**
     * Inject an action to take place at a given point within the specified class.method
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @param atInjection the injection point e.g. "ENTRY".
     * @param condition the rule condition
     * @throws Exception in case of failure.
     */
    public void injectOnMethod(Class clazz, String methodName, String condition, String action, String atInjection) throws Exception {
        injectOnMethod(clazz.getCanonicalName(), methodName, condition, action, atInjection);
    }

    /**
     * Inject an action to take place at a given point within the specified class.method
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @param atInjection the injection point e.g. "ENTRY".
     * @param condition the rule condition
     * @throws Exception in case of failure.
     */
    public void injectOnMethod(String className, String methodName, String condition, String action, String atInjection) throws Exception
    {
        String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_injectionat"+atInjection;

        RuleConstructor.ClassClause builderClassClause = RuleConstructor.createRule(ruleName);
        RuleConstructor.MethodClause builderMethodClause =
            isInterface(className) ? builderClassClause.onInterface(className) : builderClassClause.onClass(className);

        RuleConstructor builder = builderMethodClause
            .inMethod(methodName)
            .at(atInjection)
            .helper(helperClass)
            .ifCondition(condition)
            .doAction(action);

        installScript("onCall"+className+"."+methodName+"."+atInjection, builder.build());
    }


    /**
     * <p>
     * Inject an action to take place at a given point within the specified class.method
     * <p>
     * Difference to {@link #injectOnMethod(Class, String, String, String, String)} resides at
     * injection definition. The prior one expects "AT" injection point. This one expects the whole
     * location qualifier.
     *
     * @param clazz The Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @param where the injection definition e.g. "AT ENTRY" or "AFTER SYNCHRONIZATION".
     * @param condition the rule condition
     * @throws Exception in case of failure.
     */
    public void injectOnMethodWhere(Class clazz, String methodName, String condition, String action, String where) throws Exception {
        injectOnMethodWhere(clazz.getCanonicalName(), methodName, condition, action, where);
    }

    /**
     * <p>
     * Inject an action to take place at a given point within the specified class.method
     * <p>
     * Difference to {@link #injectOnMethod(String, String, String, String, String)} resides at
     * injection definition. The prior one expects "AT" injection point. This one expects the whole
     * location qualifier.
     *
     * @param className The name of the Class in which the injection point resides.
     * @param methodName The method which should be intercepted.
     * @param action The action that should take place upon invocation of the method.
     * @param where the injection definition e.g. "AT ENTRY" or "AFTER SYNCHRONIZATION".
     * @param condition the rule condition
     * @throws Exception in case of failure.
     */
    public void injectOnMethodWhere(String className, String methodName, String condition, String action, String where) throws Exception
    {
        String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_injectionat"+where;

        RuleConstructor.ClassClause builderClassClause = RuleConstructor.createRule(ruleName);
        RuleConstructor.MethodClause builderMethodClause =
            isInterface(className) ? builderClassClause.onInterface(className) : builderClassClause.onClass(className);

        RuleConstructor builder = builderMethodClause
            .inMethod(methodName)
            .where(where)
            .helper(helperClass)
            .ifCondition(condition)
            .doAction(action);

        installScript("onCall"+className+"."+methodName+"."+where, builder.build());
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

        RuleConstructor.ClassClause builderClassClause = RuleConstructor.createRule(ruleName);
        RuleConstructor.MethodClause builderMethodClause =
            isInterface(className) ? builderClassClause.onInterface(className) : builderClassClause.onClass(className);

        RuleConstructor builder = builderMethodClause
            .inMethod(methodName)
            .atEntry()
            .helper(helperClass)
            .ifTrue()
            .doAction(actionBuilder.toString());

        installScript("fault"+className+"."+methodName, builder.build());
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
        crashAtMethodExit(clazz.getCanonicalName(), methodName);
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
        crashAtMethodEntry(clazz.getCanonicalName(), methodName);
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
     * @param atInjection the injection point e.g. "ENTRY".
     * @throws Exception in case of failure.
     */
    public void crashAtMethod(String className, String methodName, String atInjection) throws Exception
    {
        String ruleName = this.getClass().getCanonicalName()+"_"+className+"_"+methodName+"_crashat"+atInjection;

        String action = "debug(\"killing JVM\"), killJVM()";

        RuleConstructor.ClassClause builderClassClause = RuleConstructor.createRule(ruleName);
        RuleConstructor.MethodClause builderMethodClause =
            isInterface(className) ? builderClassClause.onInterface(className) : builderClassClause.onClass(className);

        RuleConstructor builder = builderMethodClause
            .inMethod(methodName)
            .at(atInjection)
            .helper(helperClass)
            .ifTrue()
            .doAction(action);

        installScript("crash"+className+"."+methodName+"."+atInjection, builder.build());
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
    public void installScript(String scriptName, String scriptString) throws Exception {
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
     * Installing rule based on definition available by building {@link RuleConstructor}.
     *
     * @param builder  rule builder with a rule definition to be installed as script
     * @return  name of script that rule was installed under
     * @throws Exception  in case of failure
     */
    public String installRule(RuleConstructor builder) throws Exception {
        String scriptName = builder.getRuleName();
        installScript(scriptName, builder.build());
        return scriptName;
    }

    /**
     * <p>
     * Removing particular script from the remote byteman agent.
     * <p>
     * If you submitted a rule directly to remote JVM then the scriptName
     * is the name under the script was installed.
     * <p>
     * If you used {@link #setRedirectedSubmissionsFile(File)} to define
     * a file where the rule will be written then this method won't work
     * and you will get an {@link IllegalStateException}.
     *
     * @param scriptName  name of script that should be removed
     * @throws Exception in case that script can't be removed
     */
    public void removeScript(String scriptName) throws Exception {
        ScriptText script = findInstalledScript(scriptName);
        if(script == null) {
            throw new IllegalStateException("Script name " + scriptName + " can't be removed as "
                + "was not found in list of installed scripts");
        }
        submit.deleteScripts(Arrays.asList(script));
        installedScripts.remove(script);
    }

    /**
     * Removing particular script installed as a rule by {@link RuleConstructor}.
     *
     * @param builder  a rule defining a script to be removed
     * @throws Exception  in case of failure
     */
    public void removeRule(RuleConstructor builder) throws Exception {
        String scriptName = builder.getRuleName();
        removeScript(scriptName);
    }

    /**
     * Flush the local cache of scripts and proxies to remote instrumented classes.
     * Useful to reset local state when a remote JVM is crashed and hence reset.
     *
     * @throws Exception in case of failure.
     */
    public synchronized void removeLocalState() throws Exception
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
     * Trying to load a class and if successful then check if class is interface.
     * If it's then returns true. In all other cases returns false.
     */
    private boolean isInterface(String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (Exception e) {
            // can't find class in the class loader
        }
        if(clazz == null) {
            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (Exception e) {
                // can't find class in the TCCL
            }
        }

        if(clazz != null) {
            return clazz.isInterface();
        } else {
            return false;
        }
    }

    /**
     * Looping through installed scripts and checking if scriptName
     * is there. If so returns it otherwise returns null.
     */
    private ScriptText findInstalledScript(String scriptName) {
        for(ScriptText installedScript: installedScripts) {
            if(installedScript.getFileName().equals(scriptName)) return installedScript;
        }
        return null;
    }
}
