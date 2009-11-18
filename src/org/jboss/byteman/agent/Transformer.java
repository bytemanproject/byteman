/*
* JBoss, Home of Professional Open Source
* Copyright 2008-9, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.agent.adapter.RuleTriggerAdapter;
import org.jboss.byteman.agent.adapter.RuleCheckAdapter;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
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
     */
    public Transformer(Instrumentation inst, List<String> scriptPaths, List<String> scriptTexts, boolean isRedefine)
            throws Exception
    {
        this.inst = inst;
        this.isRedefine = isRedefine;
        scriptRepository = new ScriptRepository();

        Iterator<String> iter = scriptTexts.iterator();
        int scriptIdx = 0;
        while (iter.hasNext()) {
            String scriptText = iter.next();
            String file = scriptPaths.get(scriptIdx);
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
    }

    protected void dumpScript(RuleScript ruleScript)
    {
        String file = ruleScript.getFile();
        int line = ruleScript.getLine();
        if (file != null) {
            System.out.println("# " + file + " line " + line);
        }
        System.out.println("RULE " + ruleScript.getName());
        if (ruleScript.isInterface()) {
            System.out.println("INTERFACE " + ruleScript.getTargetClass());
        } else {
            System.out.println("CLASS " + ruleScript.getTargetClass());
        }
        System.out.println("METHOD " + ruleScript.getTargetMethod());
        if (ruleScript.getTargetHelper() != null) {
            System.out.println("HELPER " + ruleScript.getTargetHelper());
        }
        System.out.println(ruleScript.getTargetLocation());
        System.out.println(ruleScript.getRuleText());
        System.out.println("ENDRULE");
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
                if (!script.hasTransform(clazz)) {
                    found = true;
                    if (isVerbose()) {
                        System.out.println("Retransforming loaded bootstrap class " + clazz.getName());
                    }
                    break;
                }
            }
        }

        return found;
    }

    /**
     * ensure that scripts which apply to classes loaded before registering the transformer get
     * are installed by retransforming the relevant classes
     */

    public void installBootScripts() throws Exception
    {
        // check for scripts which apply to classes already loaded during bootstrap and retransform those classes
        // so that rule triggers are injected

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
            if (Transformer.isVerbose()) {
                for (int i = 0; i < transformed.size(); i++) {
                    System.out.println("retransforming " + transformedArray[i].getName());
                }
            }
            inst.retransformClasses(transformedArray);
        }
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

    /**
     * The implementation of this method may transform the supplied class file and
     * return a new replacement class file.
     * <p/>
     * <p/>
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
     * <p/>
     * <p/>
     * If the implementing method determines that no transformations are needed,
     * it should return <code>null</code>.
     * Otherwise, it should create a new <code>byte[]</code> array,
     * copy the input <code>classfileBuffer</code> into it,
     * along with all desired transformations, and return the new array.
     * The input <code>classfileBuffer</code> must not be modified.
     * <p/>
     * <p/>
     * In the redefine case, the transformer must support the redefinition semantics.
     * If a class that the transformer changed during initial definition is later redefined, the
     * transformer must insure that the second class output class file is a legal
     * redefinition of the first output class file.
     * <p/>
     * <p/>
     * If the transformer believes the <code>classFileBuffer</code> does not
     * represent a validly formatted class file, it should throw
     * an <code>IllegalClassFormatException</code>.  Subsequent transformers
     * will still be called and the load or redefine will still
     * be attempted.  Throwing an <code>IllegalClassFormatException</code> thus
     * has the same effect as returning null but facilitates the
     * logging or debugging of format corruptions.
     *
     * @param loader              the defining loader of the class to be transformed,
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

    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)
            throws IllegalClassFormatException
    {
        boolean enabled = true;
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

            ClassChecker checker = new ClassChecker(newBuffer);

            if (checker.isInterface()) {
                return null;
            }

            if (checker.getOuterClass() != null) {
                // we don't transform inner classes for now
                // TODO -- see if we can match and transform inner classes via the outer class
                return null;
            }

            // TODO-- reconsider this as it is a bit dodgy as far as security is concerned
        
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            // ok, we need to check whether there are any class scripts associated with this class and if so
            // we will consider transforming the byte code

            // TODO -- there are almost certainly concurrency issues to deal with here if rules are being loaded/unloaded

            newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, internalName, false);

            int dotIdx = internalName.lastIndexOf('.');

            if (dotIdx > 0) {
                newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, internalName.substring(dotIdx + 1), false);
            }

            if (scriptRepository.checkInterfaces()) {
                // now we need to do the same for any interface scripts
                // n.b. resist the temptation to call classBeingRedefined.getInterfaces() as this will
                // cause the class to be resolved, losing any changes we install

                String[] interfaceNames = checker.getInterfaces();

                for (int i = 0; i < interfaceNames.length; i++) {
                    String interfaceName = interfaceNames[i];
                    String internalInterfaceName = TypeHelper.internalizeClass(interfaceName);
                    newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, internalInterfaceName, true);
                    dotIdx = internalInterfaceName.lastIndexOf('.');
                    if (dotIdx >= 0) {
                        newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, internalInterfaceName.substring(dotIdx + 1), true);
                    }
                }
            }

            // ok, now find the superclass for this class and check the superclass chain

            String superName = TypeHelper.internalizeClass(checker.getSuper());
            Class superClazz = null;

            if (superName != null) {
                try {
                    superClazz = loader.loadClass(superName);
                } catch (ClassNotFoundException e) {
                    // should not happen!
                    // TODO - what happens when the bytecode is for class Object? is the supername null? or ""?
                    System.err.println("Transformer.transform : error looking up superclass!");
                    e.printStackTrace(System.err);
                }
            }

            while (superClazz != null) {
                newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, superName, false, true);
                dotIdx = superName.lastIndexOf('.');
                if (dotIdx > 0) {
                    newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, superName.substring(dotIdx + 1), false, true);
                }

                // ok, now check any interfaces implemented by the superclass
                Class[] interfaces = superClazz.getInterfaces();

                for (int i = 0; i < interfaces.length; i++) {
                    // TODO -- do we ever find that a super declares an interface also declared by its subclass
                    // TODO -- we probably don't want to inject twice in such cases so we ought to remember whether
                    // TODO -- we have seen an interface before
                    String interfaceName = interfaces[i].getName();
                    newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, interfaceName, false, true);
                    dotIdx = interfaceName.lastIndexOf('.');
                    if (dotIdx >= 0) {
                        newBuffer = tryTransform(newBuffer, internalName, loader, classBeingRedefined, interfaceName.substring(dotIdx + 1), false, true);
                    }
                }

                superClazz = superClazz.getSuperclass();
                if (superClazz != null) {
                    superName = superClazz.getName();
                }
            }

            if (newBuffer != classfileBuffer) {
                // see if we need to dump the transformed bytecode for checking
                if (dumpGeneratedClasses) {
                    dumpClass(internalName, newBuffer, classfileBuffer);
                }
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

    private byte[] tryTransform(byte[] buffer, String name, ClassLoader loader, Class classBeingRedefined, String key, boolean isInterface)
    {
        return tryTransform(buffer, name, loader, classBeingRedefined, key, isInterface, false);
    }

    private byte[] tryTransform(byte[] buffer, String name, ClassLoader loader, Class classBeingRedefined, String key, boolean isInterface, boolean isOverride)
    {
        List<RuleScript> ruleScripts;

        if (isInterface) {
            ruleScripts = scriptRepository.scriptsForInterfaceName(key);
        } else {
            ruleScripts = scriptRepository.scriptsForClassName(key);
        }
        byte[] newBuffer = buffer;

        if (ruleScripts != null) {
//            if (isVerbose()) {
//                System.out.println("tryTransform : " + name + " for " + key);
//            }
            for (RuleScript ruleScript : ruleScripts) {
                try {
                    // we only transform via isOverride rules if isOverride is true
                    // we tarsnform via any matchign rules if isOverride is false
                    if (!isOverride || ruleScript.isOverride()) {
                        // only do the transform if the script has not been deleted
                        synchronized (ruleScript) {
                            if (!ruleScript.isDeleted()) {
                                newBuffer = transform(ruleScript, loader, name, classBeingRedefined, newBuffer);
                            }
                        }
                    }
                } catch (Throwable th) {
                    // yeeeurgh I know this looks ugly with no rethrow but it is appropriate
                    // we do not want to pass on any errors or runtime exceptions
                    // if a transform fails then we should still allow the load to continue
                    // with whatever other transforms succeed. we tarce the throwable to
                    // System.err just to ensure it can be seen.

                    System.err.println("Transformer.transform : caught throwable " + th);
                    th.printStackTrace(System.err);
                }
            }
        }
        return newBuffer;
    }

    /* switches controlling behaviour of transformer */

    /**
     * prefix for byteman package
     */
    private static final String BYTEMAN_PACKAGE_PREFIX = "org.jboss.byteman.";

    /**
     * prefix for byteman test package
     */
    private static final String BYTEMAN_TEST_PACKAGE_PREFIX = "org.jboss.byteman.tests.";

    /**
     * prefix for org.jboss package
     */
    private static final String JAVA_LANG_PACKAGE_PREFIX = "java.lang.";

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
     * system property set to true in order to enable transform of java.lang classes
     */
    public static final String TRANSFORM_ALL = BYTEMAN_PACKAGE_PREFIX + "transform.all";

    /**
     * retained for compatibility
     */
    public static final String TRANFORM_ALL_COMPATIBILITY = BYTEMAN_PACKAGE_PREFIX + "quodlibet";

    /* implementation */

    /**
     * system property identifying directory in which to dump generated bytecode .class files
     */
    public static final String DUMP_GENERATED_CLASSES_DIR = BYTEMAN_PACKAGE_PREFIX + "dump.generated.classes.directory";

    protected byte[] transform(RuleScript ruleScript, ClassLoader loader, String className, Class classBeingRedefined, byte[] targetClassBytes)
    {
        final String handlerMethod = ruleScript.getTargetMethod();
        final Location handlerLocation = ruleScript.getTargetLocation();
        /**
         * we cannot afford to lookup the helper class at this point because that involves valling a synchronized
         * method on loader while we are synchronized on the transformer. That is no problem if the transform request
         * has itself been initiated under a load but it presents a potential deadlock when the transform request
         * occurs under Instrumentation.retransformClasses(). So, we have to install the helper name and defer
         * checking for the helper until type check time when the rule is first executed
         */
//        Class helperClass = null;
//        if (helperName != null) {
//            try {
//                helperClass = loader.loadClass(helperName);
//            } catch (ClassNotFoundException e) {
//                System.out.println("org.jboss.byteman.agent.Transformer : unknown helper class " + helperName + " for rule " + ruleScript.getName());
//            }
//        }
        final Rule rule;
        String ruleName = ruleScript.getName();
        try {
            rule = Rule.create(ruleScript, loader);
        } catch (ParseException pe) {
            System.out.println("org.jboss.byteman.agent.Transformer : error parsing rule " + ruleName + "\n" + pe);
            ruleScript.recordTransform(loader, className, null, pe);
            return targetClassBytes;
        } catch (TypeException te) {
            System.out.println("org.jboss.byteman.agent.Transformer : error checking rule " + ruleName + "\n" + te);
            ruleScript.recordTransform(loader, className, null, te);
            return targetClassBytes;
        } catch (Throwable th) {
            System.out.println("org.jboss.byteman.agent.Transformer : error processing rule " + ruleName + "\n" + th);
            ruleScript.recordTransform(loader, className, null, th);
            return targetClassBytes;
        }

        // ok, we have a rule with a matchingclass and a candidiate method and location
        // we need to see if the class has a matching method and, if so, add a call to
        // execute the rule when we hit the relevant line

        ClassReader cr = new ClassReader(targetClassBytes);
        // need to provide a real writer here so that labels get resolved
        ClassWriter dummy = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        RuleCheckAdapter checkAdapter = handlerLocation.getRuleCheckAdapter(dummy, rule, className, handlerMethod);
        // PrintWriter pw = new PrintWriter(System.out);
        // ClassVisitor traceAdapter = new TraceClassVisitor(cw, pw);
        // RuleCheckAdapter adapter = handlerLocation.getRuleCheckAdapter(traceAdapter, rule, className, handlerMethod);
        try {
            cr.accept(checkAdapter, ClassReader.EXPAND_FRAMES);
        } catch (Throwable th) {
            System.out.println("org.jboss.byteman.agent.Transformer : error applying rule " + rule.getName() + " to class " + className + "\n" + th);
            th.printStackTrace(System.out);
            ruleScript.recordTransform(loader, className, rule, th);
            return targetClassBytes;
        }
        // only insert the rule trigger call if there is a suitable location in the target method
        if (checkAdapter.isVisitOk()) {
            if (isVerbose()) {
                System.out.println("org.jboss.byteman.agent.Transformer : possible trigger for rule " + rule.getName() + " in class " + className);
            }
            cr = new ClassReader(targetClassBytes);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            // PrintWriter pw = new PrintWriter(System.out);
            // ClassVisitor traceAdapter = new TraceClassVisitor(cw, pw);
            // RuleTriggerAdapter adapter = handlerLocation.getRuleAdapter(traceAdapter, rule, className, handlerMethod);
            RuleTriggerAdapter adapter = handlerLocation.getRuleAdapter(cw, rule, className, handlerMethod);
            try {
                cr.accept(adapter, ClassReader.EXPAND_FRAMES);
            } catch (Throwable th) {
                System.out.println("org.jboss.byteman.agent.Transformer : error injecting trigger for rule " + rule.getName() + " into class " + className + "\n" +  th);
                th.printStackTrace(System.out);
                ruleScript.recordTransform(loader, className, rule, th);
                return targetClassBytes;
            }
            // only return transformed code if ruleScript is still active

            if (ruleScript.recordTransform(loader, className, rule)) {
                // hand back the transformed byte code
                if (isVerbose()) {
                    System.out.println("org.jboss.byteman.agent.Transformer : inserted trigger for " + rule.getName() + " in class " + className);
                }
                return cw.toByteArray();
            }
        }

        return targetClassBytes;
    }

    /**
     * a simple adapter used to scan a class's bytecode definition for the name of its superclass, its enclosing
     * class and the interfaces it implements directly
     */
    
    private static class ClassCheckAdapter implements ClassVisitor
    {
        private boolean isInterface = false;
        private String[] interfaces = null;
        private String superName = null;
        private String outerClass = null;

        public boolean isInterface() {
            return isInterface;
        }

        public String getSuper()
        {
            return superName;
        }

        public String getOuterClass()
        {
            return outerClass;
        }

        public String[] getInterfaces()
        {
            return interfaces;
        }

        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
            this.interfaces = interfaces;
            this.superName = superName;
        }

        public void visitSource(String source, String debug) {
            // do nothimg
        }

        public void visitOuterClass(String owner, String name, String desc) {
            outerClass = owner;
        }

        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            return null;
        }

        public void visitAttribute(Attribute attr) {
            // do nothimg
        }

        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            // do nothimg
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return null;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return null;
        }

        public void visitEnd() {
            // do nothimg
        }
    }

    /**
     * a private class which can be used to derive the super and interfaces of a class from its defining bytecode
     */
    private static class ClassChecker
    {
        ClassCheckAdapter adapter;

        public ClassChecker(byte[] buffer)
        {
            // run a pass over the bytecode to identify the interfaces
            ClassReader cr = new ClassReader(buffer);
            adapter = new ClassCheckAdapter();
            cr.accept(adapter, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }

        public boolean isInterface()
        {
            return adapter.isInterface();
        }

        public String getSuper()
        {
            return adapter.getSuper();
        }

        public String getOuterClass()
        {
            return adapter.getOuterClass();
        }

        public String[] getInterfaces()
        {
            return adapter.getInterfaces();
        }
    }

    /**
     * disable triggering of rules inside the current thread
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
     * test whether a class with a given name is located in the byteman package
     * @param className
     * @return true if a class is located in the byteman package otherwise return false
     */
    protected boolean isBytemanClass(String className)
    {
        return className.startsWith(BYTEMAN_PACKAGE_PREFIX) && !className.startsWith(BYTEMAN_TEST_PACKAGE_PREFIX);
    }

    /**
     * check whether verbose mode for rule processing is enabled or disabled
     * @return true if verbose mode is enabled etherwise false
     */
    public static boolean isVerbose()
    {
        return verbose;
    }

    /**
     * check whether dumping of the control flow graph for the trigger class is enabled
     * @return true if dumping is enabled etherwise false
     */
    public static boolean isDumpCFG()
    {
        return dumpCFG;
    }

    /**
     * check whether dumping of the control flow graph for the trigger class during construction is enabled
     * @return true if dumping is enabled etherwise false
     */
    public static boolean isDumpCFGPartial()
    {
        return dumpCFGPartial;
    }

    /**
     * check whether debug mode for rule processing is enabled or disabled
     * @return true if debug mode is enabled or verbose mode is enabled otherwise false
     */
    public static boolean isDebug()
    {
        return debug || verbose;
    }

    /**
     * check whether compilation of rules is enabled or disabled
     * @return true if compilation of rules is enabled etherwise false
     */
    public static boolean isCompileToBytecode()
    {
        return compileToBytecode;
    }

    /**
     * test whether a class with a given name is a potential candidate for insertion of event notifications
     * @param className
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
    /**
     * the instrumentation interface to the JVM
     */
    protected final Instrumentation inst;

    /**
     * true if the instrumentor allows redefinition
     */
    protected boolean isRedefine;

    /**
     * a mapping from target class names which appear in rules to a script object holding the
     * rule details
     */

    protected final ScriptRepository scriptRepository;

    /**
     *  switch to control verbose output during rule processing
     */
    private final static boolean verbose = (System.getProperty(VERBOSE) != null);

    /**
     *  switch to control control flow graph output during rule processing
     */
    private final static boolean dumpCFGPartial = (System.getProperty(DUMP_CFG_PARTIAL) != null);

    /**
     *  switch to control control flow graph output during rule processing
     */
    private final static boolean dumpCFG = (dumpCFGPartial || (System.getProperty(DUMP_CFG) != null));

    /**
     *  switch to control debug output during rule processing
     */
    private final static boolean debug = (System.getProperty(DEBUG) != null);

    /**
     *  switch to control whether rules are compiled to bytecode or not
     */
    private final static boolean compileToBytecode =
            (System.getProperty(COMPILE_TO_BYTECODE) != null ||
                    System.getProperty(COMPILE_TO_BYTECODE_COMPATIBILITY) != null);

    /**
     *  switch to control dumping of generated bytecode to .class files
     */
    private final static boolean dumpGeneratedClasses = (System.getProperty(DUMP_GENERATED_CLASSES) != null);

    /**
     *  directory in which to dump generated bytecode .class files (defaults to "."
     */
    private final static String dumpGeneratedClassesDir;

    /**
     *  switch to control whether transformations will be applied to java.lang.* classes
     */
    private final static boolean transformAll =
            (System.getProperty(TRANSFORM_ALL) != null || System.getProperty(TRANFORM_ALL_COMPATIBILITY) != null);

    static {
        String userDir = System.getProperty(DUMP_GENERATED_CLASSES_DIR);
        if (userDir != null) {
            File userFile = new File(userDir);
            if (userFile.exists() && userFile.isDirectory() && userFile.canWrite()) {
                dumpGeneratedClassesDir = userDir;
            } else {
                dumpGeneratedClassesDir = ".";
            }
        } else {
            dumpGeneratedClassesDir =  ".";
        }
    }

    public static void maybeDumpClass(String fullName, byte[] bytes)
    {
        if (dumpGeneratedClasses) {
            dumpClass(fullName, bytes);
        }
    }

    private static void dumpClass(String fullName, byte[] bytes)
    {
        dumpClass(fullName, bytes, null);
    }

    private static void dumpClass(String fullName, byte[] bytes, byte[] oldBytes)
    {
        int dotIdx = fullName.lastIndexOf('.');

        String name = (dotIdx < 0 ? fullName : fullName.substring(dotIdx + 1));
        String prefix = (dotIdx > 0 ? fullName.substring(0, dotIdx) : "");
        String dir = dumpGeneratedClassesDir + File.separator + prefix.replaceAll("\\.", File.separator);
        if (!ensureDumpDirectory(dir)) {
            System.out.println("org.jboss.byteman.agent.Transformer : Cannot dump transformed bytes to directory " + dir + File.separator + prefix);
            return;
        }
        String newname = dir + File.separator + name + ".class";
        System.out.println("org.jboss.byteman.agent.Transformer : Saving transformed bytes to " + newname);
        try {
            FileOutputStream fio = new FileOutputStream(newname);
            fio.write(bytes);
            fio.close();
        } catch (IOException ioe) {
            System.out.println("Error saving transformed bytes to" + newname);
            ioe.printStackTrace(System.out);
        }
        if (oldBytes != null) {
            String oldname = dir + File.separator + name + "_orig.class";
            System.out.println("org.jboss.byteman.agent.Transformer : Saving original bytes to " + oldname);
            try {
                FileOutputStream fio = new FileOutputStream(oldname);
                fio.write(oldBytes);
                fio.close();
            } catch (IOException ioe) {
                System.out.println("Error saving transformed bytes to" + oldname);
                ioe.printStackTrace(System.out);
            }
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
    private final static Integer DISABLED_USER = new Integer(0);
    private final static Integer DISABLED = new Integer(1);
    private final static Integer ENABLED = null;
}
