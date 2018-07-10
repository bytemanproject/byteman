/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-10, Red Hat and individual contributors
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

import org.jboss.byteman.agent.check.BytecodeChecker;
import org.jboss.byteman.agent.check.CheckerCache;
import org.jboss.byteman.agent.check.ClassChecker;
import org.jboss.byteman.modules.ModuleSystem;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.type.TypeHelper;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

/**
 * byte code transformer used to introduce byteman events into JBoss code
 */
public class Transformer implements ClassFileTransformer {

    /**
     * constructor allowing this transformer to be provided with access to the JVM's instrumentation
     * implementation
     *
     * @param inst the instrumentation object used to interface to the JVM
     * @param scriptPaths list of file paths for each input script
     * @param scriptTexts the text of each input script
     * @param isRedefine true if class redefinition is allowed false if not
     * @param moduleSystem the module system to use in transformation
     * @throws Exception if a script is in error
     */
    public Transformer(Instrumentation inst, ModuleSystem moduleSystem, List<String> scriptPaths, List<String> scriptTexts, boolean isRedefine)
            throws Exception
    {
        this.inst = inst;
        this.isRedefine = isRedefine;
        scriptRepository = new ScriptRepository(skipOverrideRules);
        checkerCache = new CheckerCache();
        helperManager = new HelperManager(inst, moduleSystem);

        Iterator<String> scriptsIter = scriptTexts.iterator();
        Iterator<String> filesIter = scriptPaths.iterator();
        while (scriptsIter.hasNext()) {
            String scriptText = scriptsIter.next();
            String file = filesIter.next();
            List<RuleScript> ruleScripts = scriptRepository.processScripts(scriptText, file);
            for (RuleScript ruleScript : ruleScripts) {
                String name = ruleScript.getName();
                RuleScript previous = scriptRepository.scriptForRuleName(name);
                if (previous == null) {
                    scriptRepository.addScript(ruleScript);
                } else {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("Transformer : duplicate script name ");
                    buffer.append(name);
                    buffer.append("in file ");
                    buffer.append(ruleScript.getFile());
                    buffer.append("  line ");
                    buffer.append(ruleScript.getLine());
                    buffer.append("\n previously defined in file ");
                    buffer.append(previous.getFile());
                    buffer.append("  line ");
                    buffer.append(previous.getLine());
                    Exception ex = new Exception(buffer.toString());
                    throw ex;
                }
            }
        }

        accessEnabler = AccessManager.init(inst);
    }

    /**
     * ensure that scripts which apply to classes loaded before
     * registering the transformer are installed by retransforming the
     * relevant classes
     * @throws Exception if the retransform fails
     */

    public void installBootScripts() throws Exception
    {
        // check for scripts which apply to classes already loaded
        // during bootstrap and retransform those classes so that rule
        // triggers are injected

        List<Class<?>> transformed = new LinkedList<Class<?>>();

        Class<?>[] loaded = inst.getAllLoadedClasses();

        for (Class clazz : loaded) {

            if (isSkipClass(clazz)) {
                continue;
            }

            if (scriptRepository.matchClass(clazz)) {
                transformed.add(clazz);
            }
        }
        // retransform all classes for which we found untransformed rules

        if (!transformed.isEmpty()) {
            Class<?>[] transformedArray = new Class<?>[transformed.size()];
            transformed.toArray(transformedArray);
            for (int i = 0; i < transformed.size(); i++) {
                Helper.verbose("retransforming " + transformedArray[i].getName());
            }

            inst.retransformClasses(transformedArray);
        }
    }

    public void installPolicy()
    {
        BytemanPolicy policy = new BytemanPolicy(Policy.getPolicy());
        Policy.setPolicy(policy);
    }

    /**
     * The implementation of this method may transform the supplied class file and
     * return a new replacement class file.
     *
     *
     * Once a transformer has been registered with
     * {@link java.lang.instrument.Instrumentation#addTransformer Instrumentation.addTransformer},
     * the transformer will be called for every new class definition and every class redefinition.
     * The request for a new class definition is made with
     * {@link ClassLoader#defineClass ClassLoader.defineClass}.
     * The request for a class redefinition is made with
     * {@link java.lang.instrument.Instrumentation#redefineClasses Instrumentation.redefineClasses}
     * or its native equivalents.
     * The transformer is called during the processing of the request, before the class file bytes
     * have been verified or applied.
     *
     *
     * If the implementing method determines that no transformations are needed,
     * it should return <code>null</code>.
     * Otherwise, it should create a new <code>byte[]</code> array,
     * copy the input <code>classfileBuffer</code> into it,
     * along with all desired transformations, and return the new array.
     * The input <code>classfileBuffer</code> must not be modified.
     *
     *
     * In the redefine case, the transformer must support the redefinition semantics.
     * If a class that the transformer changed during initial definition is later redefined, the
     * transformer must insure that the second class output class file is a legal
     * redefinition of the first output class file.
     *
     *
     * If the transformer believes the <code>classFileBuffer</code> does not
     * represent a validly formatted class file, it should throw
     * an <code>IllegalClassFormatException</code>.  Subsequent transformers
     * will still be called and the load or redefine will still
     * be attempted.  Throwing an <code>IllegalClassFormatException</code> thus
     * has the same effect as returning null but facilitates the
     * logging or debugging of format corruptions.
     *
     * @param originalLoader      the defining loader of the class to be transformed,
     *                            may be <code>null</code> if the bootstrap loader
     * @param className           the name of the class in the internal form of fully
     *                            qualified class and interface names as defined in
     *                            <i>The Java Virtual Machine Specification</i>.
     *                            For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined if this is a redefine, the class being redefined,
     *                            otherwise <code>null</code>
     * @param protectionDomain    the protection domain of the class being defined or redefined
     * @param classfileBuffer     the input byte buffer in class file format - must not be modified
     * @return a well-formed class file buffer (the result of the transform),
     *         or <code>null</code> if no transform is performed.
     * @throws java.lang.instrument.IllegalClassFormatException
     *          if the input does not represent a well-formed class file
     * @see java.lang.instrument.Instrumentation#redefineClasses
     */

    public byte[] transform(ClassLoader originalLoader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)
            throws IllegalClassFormatException
    {
        boolean enabled = true;
        ClassLoader loader = originalLoader;
        try {
            enabled = Rule.disableTriggersInternal();

            byte[] newBuffer = classfileBuffer;
            // we only transform certain classes -- we do allow bootstrap classes whose loader is null
            // but we exclude byteman classes and java.lang classes
            String internalName = TypeHelper.internalizeClass(className);

            if (isBytemanClass(internalName) || !isTransformable(internalName)) {
                return null;
            }

            // we will need the super class name any outer class name and the name of the interfaces the class implements

            ClassChecker checker = getClassChecker(newBuffer);// new ClassChecker(newBuffer);

            if (checker == null || checker.isInterface()) {
                return null;
            }

            /*
            if (checker.hasOuterClass()) {
                // we don't transform inner classes for now
                // TODO -- see if we can match and transform inner classes via the outer class
                return null;
            }
            */

            // TODO-- reconsider this as it is a bit dodgy as far as security is concerned
        
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            // if we need to traverse the interfaces then we have a DAG to deal with so
            // we had better find a way to avoid doing things twice

            LinkedList<String> toVisit = null;
            HashSet<String> visited = null;

            // ok, we need to check whether there are any class scripts associated with this class and if so
            // we will consider transforming the byte code

            // TODO -- there are almost certainly concurrency issues to deal with here if rules are being loaded/unloaded

            newBuffer = tryTransform(newBuffer, internalName, loader, internalName, false);

            int dotIdx = internalName.lastIndexOf('.');

            if (dotIdx > 0) {
                newBuffer = tryTransform(newBuffer, internalName, loader, internalName.substring(dotIdx + 1), false);
            }

            if (scriptRepository.checkInterfaces()) {
                // now we need to do the same for any interface scripts
                // n.b. resist the temptation to call classBeingRedefined.getInterfaces() as this will
                // cause the class to be resolved, losing any changes we install

                // we need to check the transitive closure of the binary links
                // Class implements Interface and Interface extends Interface for this class
                // which in general is a DAG.

                toVisit = new LinkedList<String>();
                visited = new HashSet<String>();

                // we start with the original list of implemented interfaces

                int interfaceCount = checker.getInterfaceCount();
                for (int i = 0; i < interfaceCount; i++) {
                    String interfaceName = checker.getInterface(i);
                    toVisit.add(interfaceName);
                }

                // ok now check each interface in turn while pushing its super interfaces
                // until we no longer have any new interfaces to check

                while (!toVisit.isEmpty()) {
                    String interfaceName = toVisit.pop();
                    String internalInterfaceName = TypeHelper.internalizeClass(interfaceName);
                    if (!visited.contains(interfaceName)) {
                        // avoid visiting  this interface again
                        visited.add(interfaceName);
                        // now see if we have any rules for this interface
                        newBuffer = tryTransform(newBuffer, internalName, loader, internalInterfaceName, true);
                        dotIdx = internalInterfaceName.lastIndexOf('.');
                        if (dotIdx >= 0) {
                            newBuffer = tryTransform(newBuffer, internalName, loader, internalInterfaceName.substring(dotIdx + 1), true);
                        }
                        // check the extends list of this interface for new interfaces to consider
                        ClassChecker newChecker = getClassChecker(interfaceName, originalLoader);
                        if (newChecker != null) {
                            interfaceCount = newChecker.getInterfaceCount();
                            for (int i = 0; i < interfaceCount; i++) {
                                interfaceName = newChecker.getInterface(i);
                                toVisit.add(interfaceName);
                            }
                        }
                    }
                }
            }

            // checking supers is expensive so we obey the switch which disables it
            
            if (!skipOverrideRules()) {
                // ok, now check the superclass for this class and so on

                String superName = checker.getSuper();

                while (superName != null) {
                    // we need to check the super class structure
                    // n.b. we use the original loader here because we don't want to search the system loader
                    // when we have a class in the bootstrap loader
                    checker = getClassChecker(superName, originalLoader);

                    if (checker == null) {
                        // we must have a super to continue
                        break;
                    }

                    newBuffer = tryTransform(newBuffer, internalName, loader, superName, false, true);
                    dotIdx = superName.lastIndexOf('.');
                    if (dotIdx > 0) {
                        newBuffer = tryTransform(newBuffer, internalName, loader, superName.substring(dotIdx + 1), false, true);
                    }

                    if (scriptRepository.checkInterfaces()) {
                        // we need to do another DAG visit but only for interfaces not already considered

                        int interfaceCount = checker.getInterfaceCount();
                        for (int i = 0; i < interfaceCount; i++) {
                            String interfaceName = checker.getInterface(i);
                            toVisit.add(interfaceName);
                        }
                        
                        // ok now check each interface in turn while pushing its super interfaces
                        // until we no longer have any new interfaces to check

                        while(!toVisit.isEmpty()) {
                            String interfaceName = toVisit.pop();
                            String internalInterfaceName = TypeHelper.internalizeClass(interfaceName);
                            if (!visited.contains(interfaceName)) {
                                // avoid visiting  this interface again
                                visited.add(interfaceName);
                                // now see if we have any rules for this interface
                                newBuffer = tryTransform(newBuffer, internalName, loader, internalInterfaceName, true, true);
                                dotIdx = interfaceName.lastIndexOf('.');
                                if (dotIdx >= 0) {
                                    newBuffer = tryTransform(newBuffer, internalName, loader, internalInterfaceName.substring(dotIdx + 1), true, true);
                                }
                                // check the extends list of this interface for new interfaces to consider
                                ClassChecker newChecker = getClassChecker(interfaceName, originalLoader);
                                if (newChecker != null) {
                                    interfaceCount = newChecker.getInterfaceCount();
                                    for (int i = 0; i < interfaceCount; i++) {
                                        interfaceName = newChecker.getInterface(i);
                                        toVisit.add(interfaceName);
                                    }
                                }
                            }
                        }
                    }
                    // move on to the next super
                    superName = checker.getSuper();
                }
            }

            if (newBuffer != classfileBuffer) {
                // see if we need to dump the transformed bytecode for checking
                maybeDumpClass(internalName, newBuffer);
                newBuffer = maybeVerifyTransformedBytes(originalLoader, internalName, protectionDomain, newBuffer);
                return newBuffer;
            } else {
                return null;
            }
        } finally {
            if (enabled) {
                Rule.enableTriggersInternal();
            }
        }
    }

    /* switches controlling behaviour of transformer */

    /**
     * prefix for byteman package
     */
    public static final String BYTEMAN_PACKAGE_PREFIX = "org.jboss.byteman.";

    /**
     * prefix for byteman test package
     */
    public static final String BYTEMAN_TEST_PACKAGE_PREFIX = "org.jboss.byteman.tests.";

    /**
     * prefix for byteman sample package
     */
    public static final String BYTEMAN_SAMPLE_PACKAGE_PREFIX = "org.jboss.byteman.sample.";

    /**
     * prefix for org.jboss package
     */
    public static final String JAVA_LANG_PACKAGE_PREFIX = "java.lang.";

    /**
     * system property set (to any value) in order to switch on dumping of generated bytecode to .class files
     */
    public static final String VERBOSE = BYTEMAN_PACKAGE_PREFIX + "verbose";

    /**
     * system property set (to any value) in order to switch on dumping of control flow graph for
     * trigger method at each stage of construction
     */
    public static final String DUMP_CFG_PARTIAL = BYTEMAN_PACKAGE_PREFIX + "dump.cfg.partial";

    /**
     * system property set (to any value) in order to switch on dumping of control flow graph for
     * triger method after construction
     */
    public static final String DUMP_CFG = BYTEMAN_PACKAGE_PREFIX + "dump.cfg";

    /**
     * system property set (to any value) in order to switch on debug statements in the default Helper
     */

    public static final String DEBUG = BYTEMAN_PACKAGE_PREFIX + "debug";

    /**
     * retained for compatibility
     */
    public static final String COMPILE_TO_BYTECODE_COMPATIBILITY = BYTEMAN_PACKAGE_PREFIX + "compileToBytecode";

    /**
     * system property set (to any value) in order to switch on compilation of rules and left unset
     * if rules are to be interpreted.
     */
    public static final String COMPILE_TO_BYTECODE = BYTEMAN_PACKAGE_PREFIX + "compile.to.bytecode";

    /**
     * system property set (to any value) in order to switch on dumping of generated bytecode to .class files
     */
    public static final String DUMP_GENERATED_CLASSES = BYTEMAN_PACKAGE_PREFIX + "dump.generated.classes";

    /**
     * system property set (to any value) in order to switch on dumping of intermediate generated bytecode to .class files
     */
    public static final String DUMP_GENERATED_CLASSES_INTERMEDIATE = BYTEMAN_PACKAGE_PREFIX + "dump.generated.classes.intermediate";

    /**
     * system property identifying directory in which to dump generated bytecode .class files
     */
    public static final String DUMP_GENERATED_CLASSES_DIR = BYTEMAN_PACKAGE_PREFIX + "dump.generated.classes.directory";

    /**
     * system property set to true in order to enable transform of java.lang classes
     */
    public static final String TRANSFORM_ALL = BYTEMAN_PACKAGE_PREFIX + "transform.all";

    /**
     * retained for compatibility
     */
    public static final String TRANSFORM_ALL_COMPATIBILITY = BYTEMAN_PACKAGE_PREFIX + "quodlibet";

    /**
     * system property which turns off injection into overriding methods
     */
    public static final String SKIP_OVERRIDE_RULES = BYTEMAN_PACKAGE_PREFIX + "skip.override.rules";
    
    /**
     * system property which enables the restriction that only byteman specific system properties
     * will be gettable/settable via a client using the LISTSYSPROPS and SETSYSPROPS commands.
     */
    public static final String SYSPROPS_STRICT_MODE = BYTEMAN_PACKAGE_PREFIX + "sysprops.strict";

    /**
     * system property which enables the restriction that only byteman specific system properties
     * will be gettable/settable via a client using the LISTSYSPROPS and SETSYSPROPS commands.
     */
    public static final String VERIFY_TRANSFORMED_BYTES = BYTEMAN_PACKAGE_PREFIX + "verify.transformed.bytes";

    /**
     * system property which determines whether or not byteman configuration can be updated at runtime
     * via the byteman agent listener
     */
    public static final String ALLOW_CONFIG_UPDATE = BYTEMAN_PACKAGE_PREFIX + "allow.config.update";

    /**
     * system property which disables downcasts in bindings
     */
    public static final String DISALLOW_DOWNCAST = BYTEMAN_PACKAGE_PREFIX + "disallow.downcast";

    /**
     * disable triggering of rules inside the current thread
     * @param isUser true if this was called by rule code false if called internally by Byteman
     * @return true if triggering was previously enabled and false if it was already disabled
     */
    public static boolean disableTriggers(boolean isUser)
    {
        Integer enabled = isEnabled.get();
        if (enabled == ENABLED) {
            isEnabled.set((isUser ? DISABLED_USER : DISABLED));

            return true;
        }
        if (enabled == DISABLED && isUser) {
            isEnabled.set(DISABLED_USER);
        }

        return false;
    }

    /**
     * enable triggering of rules inside the current thread
     * @param isReset true if this was called by rule code and hence should reset a setting
     *                enabled by rule code false if called internally by Byteman and hence
     *                should nto reset a setting enabled by rule code
     * @return true if triggering was previously enabled and false if it was already disabled
     */
    public static boolean enableTriggers(boolean isReset)
    {
        Integer enabled = isEnabled.get();
        if (enabled == ENABLED) {
            return true;
        }

        if (isReset || enabled == DISABLED) {
            isEnabled.set(ENABLED);
        }

        return false;
    }

    /**
     * check if triggering of rules is enabled inside the current thread
     * @return true if triggering is enabled and false if it is disabled
     */
    public static boolean isTriggeringEnabled()
    {
        return isEnabled.get() == ENABLED;
    }

    /**
     * check whether verbose mode for rule processing is enabled or disabled
     * @return true if verbose mode is enabled etherwise false
     */
    public static boolean isVerbose()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return verbose;
            }
        }
        return verbose;
    }

    /**
     * check whether dumping of the control flow graph for the trigger class is enabled
     * @return true if dumping is enabled etherwise false
     */
    public static boolean isDumpCFG()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return dumpCFG;
            }
        }
        return dumpCFG;
    }

    /**
     * check whether dumping of the control flow graph for the trigger class during construction is enabled
     * @return true if dumping is enabled etherwise false
     */
    public static boolean isDumpCFGPartial()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return dumpCFGPartial;
            }
        }
        return dumpCFGPartial;
    }

    protected static boolean isDumpGeneratedClasses()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return dumpGeneratedClasses;
            }
        }
        return dumpGeneratedClasses;
    }

    protected static String getDumpGeneratedClassesDir()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return dumpGeneratedClassesDir;
            }
        }
        return dumpGeneratedClassesDir;
    }

    protected static boolean isDumpGeneratedClassesIntermediate()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return dumpGeneratedClassesIntermediate;
            }
        }
        return dumpGeneratedClassesIntermediate;
    }

    /**
     * check whether debug mode for rule processing is enabled or disabled
     * @return true if debug mode is enabled or verbose mode is enabled otherwise false
     */
    public static boolean isDebug()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return debug || verbose;
            }
        }
        return debug || verbose;
    }

    /**
     * check whether compilation of rules is enabled or disabled
     * @return true if compilation of rules is enabled etherwise false
     */
    public static boolean isCompileToBytecode()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return compileToBytecode;
            }
        }
        return compileToBytecode;
    }

    /**
     * check whether downcasts in bindings are disallowed.
     * @return true if downcasts in bindings are disallowed otherwise false
     */
    public static boolean disallowDowncast()
    {
        if (allowConfigUpdate()) {
            synchronized (configLock) {
                return disallowDowncast;
            }
        }
        return disallowDowncast;
    }

    /**
     * check whether compilation of rules is enabled or disabled
     * @return true if compilation of rules is enabled otherwise false
     */
    public boolean skipOverrideRules()
    {
        return scriptRepository.skipOverrideRules();
    }

    /**
     * check whether changes to org.jboss.byteman.* system properties will affect the agent configuration.
     * @return true if changes will affect the agent configuration otherwise false
     */
    public static boolean allowConfigUpdate()
    {
        return allowConfigUpdate;
    }

    /**
     * notify a change to an org.jboss.byteman.* system property so that the agent can choose to update its
     * configuration. n.b. this method is not synchronized because there is an implicit assumption that it is
     * called from the the listener thread immediately after it has updated the property and that no other
     * thread will modify org.jboss.byteman.* properties
     * @param property an org.jboss.byteman.* system property which has been updated.
     */
    public void updateConfiguration(String property)
    {
        if (allowConfigUpdate() && property.startsWith(BYTEMAN_PACKAGE_PREFIX)) {
            checkConfiguration(property);
        }
    }

    /**
     * test whether a class with a given name is a potential candidate for insertion of event notifications
     * @param className name of the class to test
     * @return true if a class is a potential candidate for insertion of event notifications otherwise return false
     */
    protected boolean isTransformable(String className)
    {
        /*
         * java.lang is normally excluded but we can make an exception if asked
         */
        if (className.startsWith(JAVA_LANG_PACKAGE_PREFIX)) {
            return transformAll;
        }

        return true;
    }

    public static void maybeDumpClassIntermediate(String fullName, byte[] bytes)
    {
        if (isDumpGeneratedClassesIntermediate()) {
            dumpClass(fullName, bytes, true);
        }
    }

    public static void maybeDumpClass(String fullName, byte[] bytes)
    {
        if (isDumpGeneratedClasses()) {
            dumpClass(fullName, bytes);
        }
    }

    /* implementation */

    /**
     * The routine which actually does the real bytecode transformation. this is public because it needs to be
     * callable from the type checker script. In normal running the javaagent is the only class which has a handle
     * on the registered transformer so it is the only one which can reach this point.
     * @param ruleScript the script
     * @param loader the loader of the class being injected into
     * @param className the name of the class being injected into
     * @param targetClassBytes the current class bytecode
     * @return the transformed bytecode or NULL if no transform was applied
     */
    public byte[] transform(RuleScript ruleScript, ClassLoader loader, String className, byte[] targetClassBytes)
    {
        TransformContext transformContext = new TransformContext(this, ruleScript, className, loader, helperManager, accessEnabler);

        return transformContext.transform(targetClassBytes);
    }

    /**
     * check whether a class should not be considered for transformation
     * @param clazz the class to check
     * @return true if clazz should not be considered for transformation otherwise false
     */
    protected boolean isSkipClass(Class<?> clazz)
    {
        if (!inst.isModifiableClass(clazz)) {
            return true;
        }

        // we can safely skip array classes, interfaces and primitive classes

        if (clazz.isArray()) {
            return true;
        }

        if (clazz.isInterface()) {
            return true;
        }

        if (clazz.isPrimitive()) {
            return true;
        }

        String name = clazz.getName();

        if (isBytemanClass(name) || !isTransformable(name)) {
            return true;
        }

        return false;
    }

    private byte[] tryTransform(byte[] buffer, String name, ClassLoader loader, String key, boolean isInterface)
    {
        return tryTransform(buffer, name, loader, key, isInterface, false);
    }

    private byte[] tryTransform(byte[] buffer, String name, ClassLoader loader, String key, boolean isInterface, boolean isOverride)
    {
        List<RuleScript> ruleScripts;

        if (isInterface) {
            ruleScripts = scriptRepository.scriptsForInterfaceName(key);
        } else {
            ruleScripts = scriptRepository.scriptsForClassName(key);
        }
        byte[] newBuffer = buffer;

        if (ruleScripts != null) {
//          Helper.verbose("tryTransform : " + name + " for " + key);

            int counter = 0;

            for (RuleScript ruleScript : ruleScripts) {
                try {
                    // we only transform via isOverride rules if isOverride is true
                    // we transform via any matching rules if isOverride is false
                    if (!isOverride || ruleScript.isOverride()) {
                        // only do the transform if the script has not been deleted
                        synchronized (ruleScript) {
                            if (!ruleScript.isDeleted()) {
                                maybeDumpClassIntermediate(name, newBuffer);
                                newBuffer = transform(ruleScript, loader, name, newBuffer);
                            }
                        }
                    }
                } catch (Throwable th) {
                    // yeeeurgh I know this looks ugly with no rethrow but it is appropriate
                    // we do not want to pass on any errors or runtime exceptions
                    // if a transform fails then we should still allow the load to continue
                    // with whatever other transforms succeed. we tarce the throwable to
                    // System.err just to ensure it can be seen.

                    Helper.err("Transformer.transform : caught throwable " + th);
                    Helper.errTraceException(th);
                }
            }
        }
        return newBuffer;
    }

    protected void dumpScript(RuleScript ruleScript)
    {
        String file = ruleScript.getFile();
        int line = ruleScript.getLine();
        if (file != null) {
            Helper.out("# " + file + " line " + line);
        }
        Helper.out("RULE " + ruleScript.getName());
        if (ruleScript.isInterface()) {
            Helper.out("INTERFACE " + ruleScript.getTargetClass());
        } else {
            Helper.out("CLASS " + ruleScript.getTargetClass());
        }
        Helper.out("METHOD " + ruleScript.getTargetMethod());
        if (ruleScript.getTargetHelper() != null) {
            Helper.out("HELPER " + ruleScript.getTargetHelper());
        }
        Helper.out(ruleScript.getTargetLocation() + "");
        Helper.out(ruleScript.getRuleText());
        Helper.out("ENDRULE");
    }

    private boolean isTransformed(Class clazz, String name, boolean isInterface)
    {
        if (isBytemanClass(name) || !isTransformable(name)) {
            return false;
        }

        boolean found = false;
        List<RuleScript> scripts;
        if (isInterface) {
            scripts = scriptRepository.scriptsForInterfaceName(name);
        } else {
            scripts = scriptRepository.scriptsForClassName(name);
        }
        if (scripts != null) {
            for (RuleScript script : scripts) {
                if (script.hasTransform(clazz)) {
                    found = true;
                    Helper.verbose("Retransforming loaded bootstrap class " + clazz.getName());
                    break;
                }
            }
        }

        return found;
    }

    /**
     * return a checker object which can be used to retrieve the super and interfaces of a class from its defining bytecode
     * @param bytecode
     * @return a checker
     */
    private org.jboss.byteman.agent.check.ClassChecker getClassChecker(byte[] bytecode)
    {
        return new org.jboss.byteman.agent.check.BytecodeChecker(bytecode);
    }

    /**
     * return a checker object which can be used to retrieve the super and interfaces of a class from its name and
     * classloader without forcing a load of the class.
     *
     * @param name the name of the superclass being checked
     * @param baseLoader the class loader of the subclass's bytecode
     * @return the requisite checker or null if the class does not need to be checked or cannot be loaded
     */
    public org.jboss.byteman.agent.check.ClassChecker getClassChecker(String name, ClassLoader baseLoader)
    {
        // when looking up the super of a class that is currently being loaded and considered for
        // transformation we would like to just do this
        //
        //   Class superClazz = classBeingLoaded.getSuper();
        //
        // or perhaps we might obtain the superName from the bytecode and then try this
        //
        //   Class superClazz = baseLoader.loadClass(superName)
        //
        // We could then then access details of the super using reflection and so on (ditto for interfaces).
        //
        // however, both the above options are a FAIL! We are in the middle of transforming the subclass and
        // it may not be fully resolved. In particular, that means that the JVM may not even have started
        // loading the super or implemented interfaces (yes, rilly!). if we force a load, whether implicitly
        // via the reflection API or explicitly by calling loadClass, then transforms will not be performed
        // on that recursively loaded class. this may cause us to miss the chance to apply rule injection into
        // classes in the super chain or into other implementors of the interface.

        // so, instead we must load the bytecode as a resource - user-defined loaders may not support this but
        // at least the JVM system and boot loaders should. As a performance optimization we use a cache to
        // retain checker objects derived from the bytecode. if we have already tried to resolve the named class
        // via this loader we can reuse the checker, avoiding a reload of the class bytes as a resource.

        // unfortunately, we normally have to load and then cache the super/interface resource using the loader
        // of the subclass/implementor. That may mean that we end up with multiple checkers for the same class.
        // To see why, assume classes A and B derive, respectively, from loaders CL_A and CL_B and both inherit
        // from class C defined by a third loader CL_C. If we load the class bytes for C from CL_A we will store
        // a checker for them keyed under loader CL_A and name "C". We can detect that CL_A and CL_B both have
        // CL_C as a parent. However, there is normally no way of knowing that the bytes we obtained were provided
        // by CL_A or CL_C. So, when we try to resolve C as the super of B via CL_B we cannot safely re-use the
        // bytes loaded via CL_A. Instead we have to resort to reloading the bytes for C via CL_B and storing a
        // new checker keyed under loader CL_B and name "C".
        //
        // There is one further optimization available. If the class name starts with "java." then it can only be
        // loaded via the bootstrap loader (even when it is being resolved by some other baseLoader). In that case
        // the class bytes can only come from the bootstrap loader. So, a  checker created for any lookup will suffice
        // for all lookups. In this case, we can always do the cache lookup, resource load and cache update relative
        // to the bootstrap loader by ignoring the supplied base loader and passing null instead.

        if(name.startsWith("java.")) {
            baseLoader = null;
        }

        // if a checker is in cache then just reuse it

        BytecodeChecker checker = checkerCache.lookup(baseLoader, name);
        if (checker != null) {
            return checker;
        }

        // ok, we have to look up the class details

        String resourceName = name.replace('.', '/') + ".class";
        try {
            InputStream is;
            // use the system loader if the supplied loader is null
            // the system loader will delegate to the bootstrap loader
            if (baseLoader == null || resourceName.startsWith("java.")) {
                is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
            } else {
                is = baseLoader.getResourceAsStream(resourceName);
            }
            if (is != null) {
                int length = is.available();
                int count = 0;
                byte[] bytecode = new byte[length];
                while (count < length) {
                    int read = is.read(bytecode, count, length - count);
                    if (read < 0) {
                        throw new IOException("unexpected end of file");
                    }
                    count += read;
                }
                checker = new org.jboss.byteman.agent.check.BytecodeChecker(bytecode);
                checkerCache.put(baseLoader, name, checker);
                return checker;
            } else {
                // throw new IOException("unable to load bytecode for for class " + name);
                Helper.verbose("Transformer.getClassChecker : unable to load bytecode for for class " + name);

                return null;
            }
        } catch (IOException e) {
            // log the exception and return null
            Helper.errTraceException(e);
            return null;
        }
    }

    /**
     * hash set naming blacklisted methods we refuse to inject into
     */
    private HashSet<String> blacklisted = initBlackList();

    /**
     * init method to create hash set naming blacklisted methods we refuse to inject into
     *
      * @return the hash set
     */
    private HashSet<String> initBlackList()
    {
        HashSet<String> set = new HashSet<String>();

        set.add("java.lang.Object.<init>");
        set.add("java.lang.ThreadLocal.get");
        set.add("java.lang.ThreadLocal.put");

        return set;
    }

    /**
     * check whether we are unwilling to inject into a given target method
     *
     * @param triggerClassName the name of the target class
     * @param targetMethodName the name of the target method
     * @param targetDescriptor the descriptor of the target method ignored at present
     * @return  true if we are unwilling to inject into the target method
     */
    public boolean isBlacklisted(String triggerClassName, String targetMethodName, String targetDescriptor)
    {
        //
        return blacklisted.contains(triggerClassName + "." + targetMethodName);
    }

    /**
     * classloader used by transformer when verification is switched on to detect errors in transformed bytecode 
     */

    private class VerifyLoader extends ClassLoader
    {
        public VerifyLoader(ClassLoader parent)
        {
            super(parent);
        }

        /**
         * use the supplied bytes to define a class and try creating an instance via the empty constructor
         * printing details of any errors which occur
         * @param classname
         * @param protectionDomain
         * @param bytes
         * @return the bytes if all goes well otherwise null
         */
        public byte[] verify(String classname, ProtectionDomain protectionDomain, byte[] bytes)
        {
            try {
                Class clazz = super.defineClass(classname, bytes, 0, bytes.length, protectionDomain);
                clazz.newInstance();
            } catch (Throwable th) {
                Helper.err("Transformer:verifyTransformedBytes " + th);
                Helper.errTraceException(th);
                return null;
            }
            return bytes;
        }
    }

    /**
     * return the result from calling verifyTransformedBytes if verification is enabled otherwise just return
     * the supplied bytecode
     * @param loader
     * @param classname
     * @param protectionDomain
     * @param bytes
     * @return the verified bytecode or the original
     */
    private byte[] maybeVerifyTransformedBytes(ClassLoader loader, String classname, ProtectionDomain protectionDomain, byte[] bytes)
    {
        if (verifyTransformedBytes) {
            return verifyTransformedBytes(loader, classname, protectionDomain, bytes);
        } else {
            return bytes;
        }
    }

    /**
     * verify the supplied bytecode by converting it to a class and calling newInstance with no args to instantiate.
     * since not all transformed classes have an empty constructor this should only be enabled for testing of Byteman
     * itself in cases where a transformed class is known to have an empty constructor.
     * @param loader
     * @param classname
     * @param protectionDomain
     * @param bytes
     * @return the supplied bytecode if verification succeeds or null if it fails
     */
    private byte[] verifyTransformedBytes(ClassLoader loader, String classname, ProtectionDomain protectionDomain, byte[] bytes)
    {
        VerifyLoader verifyLoader =  new VerifyLoader(loader);
        return verifyLoader.verify(classname, protectionDomain, bytes);
    }

    /**
     * test whether a class with a given name is located in the byteman package
     * @param className the name to be checked
     * @return true if a class is located in the byteman package otherwise return false
     */
    public static boolean isBytemanClass(String className)
    {
        return className.startsWith(BYTEMAN_PACKAGE_PREFIX) &&
                !className.startsWith(BYTEMAN_TEST_PACKAGE_PREFIX) &&
                !className.startsWith(BYTEMAN_SAMPLE_PACKAGE_PREFIX);
    }

    /**
     * the instrumentation interface to the JVM
     */
    protected final Instrumentation inst;

    /**
     * an object we use to enable access to reflective fields where needed
     */
    AccessEnabler accessEnabler;

    /**
     * true if the instrumentor allows redefinition
     */
    protected boolean isRedefine;

    /**
     * a mapping from target class names which appear in rules to a script object holding the
     * rule details
     */

    protected final ScriptRepository scriptRepository;

    protected final CheckerCache checkerCache;

    /**
     * a manager for helper lifecycle events which can be safely handed on to rules
     */
    protected final HelperManager helperManager;

    /* configuration values defined via system property settings */

    /**
     *  switch to control verbose output during rule processing
     */
    private static boolean verbose = computeVerbose();

    /**
     *  switch to control control flow graph output during rule processing
     */
    private static boolean dumpCFGPartial = computeDumpCFGPartial();

    /**
     *  switch to control control flow graph output during rule processing
     */
    private static boolean dumpCFG = computeDumpCFG();

    /**
     *  switch to control debug output during rule processing
     */
    private static boolean debug = computeDebug();

    /**
     *  switch to control whether rules are compiled to bytecode or not
     */
    private static boolean compileToBytecode = computeCompileToBytecode();

    /**
     *  switch to control whether rules are injected into overriding methods
     */
    private static boolean skipOverrideRules = computeSkipOverrideRules();

    /**
     *  switch to control dumping of generated bytecode to .class files
     */
    private static boolean dumpGeneratedClasses = computeDumpGeneratedClasses();

    /**
     *  switch to control dumping of generated bytecode to .class files
     */
    private static boolean dumpGeneratedClassesIntermediate = computeDumpGeneratedClassesIntermediate();

    /**
     *  directory in which to dump generated bytecode .class files (defaults to "."
     */
    private static String dumpGeneratedClassesDir = computeDumpGeneratedClassesDir();

    /**
     *  switch to control whether transformations will be applied to java.lang.* classes
     */
    private static boolean transformAll = computeTransformAll();

    /**
     * switch to control whether we attempt to verify transformed bytecode before returning it by
     * consructing a temporary class from it.
     */
    private static boolean verifyTransformedBytes = computeVerifyTransformedBytes();

    /**
     * switch which determines whether downcasts in binding initialisations are disallowed
     */
    private static boolean disallowDowncast = computeDisallowDowncast();

    /**
     * master switch which determines whether or not config values can be updated
     */
    private static boolean allowConfigUpdate = (System.getProperty(ALLOW_CONFIG_UPDATE) != null);

    /**
     * lock object used to control getters and setters when allowConfigUpdate is true
     */

    private static Object configLock = new Object();

    /* methods which compute values to be used for the verbose configuration setting */

    private static boolean computeVerbose()
    {
        return System.getProperty(VERBOSE) != null;
    }

    private static boolean computeDumpCFGPartial()
    {
        return System.getProperty(DUMP_CFG_PARTIAL) != null;
    }

    private static boolean computeDumpCFG()
    {
        return System.getProperty(DUMP_CFG) != null || System.getProperty(DUMP_CFG_PARTIAL) != null;
    }

    private static boolean computeDebug()
    {
        return System.getProperty(DEBUG) != null;
    }

    private static boolean computeCompileToBytecode()
    {
        return System.getProperty(COMPILE_TO_BYTECODE) != null ||
                System.getProperty(COMPILE_TO_BYTECODE_COMPATIBILITY) != null;
    }

    private static boolean computeSkipOverrideRules()
    {
        return System.getProperty(SKIP_OVERRIDE_RULES) != null;
    }

    public static boolean computeDumpGeneratedClasses()
    {
        return System.getProperty(DUMP_GENERATED_CLASSES) != null;
    }

    public static boolean computeDumpGeneratedClassesIntermediate()
    {
        return System.getProperty(DUMP_GENERATED_CLASSES_INTERMEDIATE) != null;
    }

    public static String computeDumpGeneratedClassesDir()
    {
        String userDir = System.getProperty(DUMP_GENERATED_CLASSES_DIR);
        if (userDir != null) {
            File userFile = new File(userDir);
            if (userFile.exists() && userFile.isDirectory() && userFile.canWrite()) {
                return userDir;
            } else {
                return ".";
            }
        } else {
            return ".";
        }
    }

    private static boolean computeTransformAll()
    {
        return System.getProperty(TRANSFORM_ALL) != null || System.getProperty(TRANSFORM_ALL_COMPATIBILITY) != null;
    }

    private static boolean computeVerifyTransformedBytes()
    {
        return System.getProperty(VERIFY_TRANSFORMED_BYTES) != null;
    }

    private static boolean computeDisallowDowncast() {
        return (System.getProperty(DISALLOW_DOWNCAST) != null);
    }

    private void checkConfiguration(String property)
    {
        // n.b. this needs to be kept up to date with each new config setting that is added

        if (VERBOSE.equals(property)) {
            boolean value = computeVerbose();
            synchronized (configLock) {
                verbose = value;
            }
            return;
        }

        /*
         * hmm. don't think we want to allow this to be overridden
        if (DUMP_CFG_PARTIAL.equals(property)) {
            boolean value = computeDumpCFGPartial();
            boolean value2 = computeDumpCFG();
            synchronized (configLock) {
                dumpCFGPartial = value;
                dumpCFG = value2;
            }
            return;
        }
         */

        /*
         * hmm. don't think we want to allow this to be overridden
        if (DUMP_CFG.equals(property)) {
            boolean value = computeDumpCFG();
            synchronized (configLock) {
                dumpCFG = value;
            }
            return;
        }
         */

        if (DEBUG.equals(property)) {
            boolean value = computeDebug();
            synchronized (configLock) {
                debug = value;
            }
            return;
        }

        // n.b. this deliberately cannot be mixed with the old compatibility property -- user beware!
        if (COMPILE_TO_BYTECODE.equals(property)) {
            boolean value = computeCompileToBytecode();
            synchronized (configLock) {
                compileToBytecode = value;
            }
        }

        /*
         * hmm. don't think we want to allow this to be overridden
        if (SKIP_OVERRIDE_RULES.equals(property)) {
            boolean value = computeSkipOverrideRules();
            synchronized (configLock) {
                skipOverrideRules = value;
            }
            return;
        }
         */

        if (DUMP_GENERATED_CLASSES.equals(property)) {
            boolean value = computeDumpGeneratedClasses();
            synchronized (configLock) {
                dumpGeneratedClasses = value;
            }
        }

        if (DUMP_GENERATED_CLASSES_DIR.equals(property)) {
            String value = computeDumpGeneratedClassesDir();
            synchronized (configLock) {
                dumpGeneratedClassesDir = value;
            }
        }

        if (DUMP_GENERATED_CLASSES_INTERMEDIATE.equals(property)) {
            boolean value = computeDumpGeneratedClassesIntermediate();
            synchronized (configLock) {
                dumpGeneratedClassesIntermediate = value;
            }
        }

        if (TRANSFORM_ALL.equals(property)) {
            boolean value = computeTransformAll();
            synchronized (configLock) {
                transformAll = value;
            }
        }

        if (DISALLOW_DOWNCAST.equals(property)) {
            boolean value = computeDisallowDowncast();
            synchronized (configLock) {
                disallowDowncast = value;
            }
        }
    }

    /* helper methods to dump class files */
    
    private static void dumpClass(String fullName, byte[] bytes)
    {
        dumpClass(fullName, bytes, false);
    }

    private static void dumpClass(String fullName, byte[] bytes, boolean intermediate)
    {
        // wrap this in a try catch in case the file i/o code generates a runtime exception
        // this may happen e.g. because of a security restriction
        try {
            int dotIdx = fullName.lastIndexOf('.');

            String name = (dotIdx < 0 ? fullName : fullName.substring(dotIdx + 1));
            String prefix = (dotIdx > 0 ? File.separator + fullName.substring(0, dotIdx) : "");
            String dir = getDumpGeneratedClassesDir() + prefix.replace('.', File.separatorChar);
            if (!ensureDumpDirectory(dir)) {
                Helper.err("org.jboss.byteman.agent.Transformer : Cannot dump transformed bytes to directory " + dir + File.separator + prefix);
                return;
            }
            String newname;
            if (intermediate) {
                int counter = 0;
                // add _<n> prefix until we come up with a new name
                newname = dir + File.separator + name + "_" + counter + ".class";
                File file = new File(newname);
                while (file.exists()) {
                    counter++;
                    newname = dir + File.separator + name + "_" + counter + ".class";
                    file = new File(newname);
                }
            } else {
                newname = dir + File.separator + name + ".class";
            }
            Helper.out("org.jboss.byteman.agent.Transformer : Saving transformed bytes to " + newname);
            try {
                FileOutputStream fio = new FileOutputStream(newname);
                fio.write(bytes);
                fio.close();
            } catch (IOException ioe) {
                Helper.err("Error saving transformed bytes to" + newname);
                Helper.errTraceException(ioe);
            }
        } catch (Throwable th) {
            Helper.err("org.jboss.byteman.agent.Transformer : Error saving transformed bytes for class " + fullName);
            Helper.errTraceException(th);
        }
    }

    private static boolean ensureDumpDirectory(String fileName)
    {
        File file = new File(fileName);
        if (file.exists()) {
            return (file.isDirectory() && file.canWrite());
        } else {
            return file.mkdirs();
        }
    }
    /**
     * Thread local holding a per thread Boolean which is true if triggering is disabled and false if triggering is
     * enabled
     */
    private static ThreadLocal<Integer> isEnabled = new ThreadLocal<Integer>();
    private final static Integer DISABLED_USER = Integer.valueOf(0);
    private final static Integer DISABLED = Integer.valueOf(1);
    private final static Integer ENABLED = null;

}
