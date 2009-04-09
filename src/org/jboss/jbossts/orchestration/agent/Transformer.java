/*
* JBoss, Home of Professional Open Source
* Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.agent.adapter.RuleTriggerAdapter;
import org.jboss.jbossts.orchestration.agent.adapter.RuleCheckAdapter;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.EmptyVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

/**
 * byte code transformer used to introduce orchestration events into JBoss code
 */
public class Transformer implements ClassFileTransformer {

    private static Transformer theTransformer = null;

    public static Transformer getTheTransformer()
    {
        return theTransformer;
    }

    /**
     * constructor allowing this transformer to be provided with access to the JVM's instrumentation
     * implementation
     *
     * @param inst the instrumentation object used to interface to the JVM
     */
    public Transformer(Instrumentation inst, List<String> scriptPaths, List<String> scriptTexts)
            throws Exception
    {
        theTransformer = this;
        this.inst = inst;
        targetToScriptMap = new HashMap<String, List<Script>>();

        Iterator<String> iter = scriptTexts.iterator();
        int scriptIdx = 0;
        while (iter.hasNext()) {
            String scriptText = iter.next();
            String file = scriptPaths.get(scriptIdx);
            if (scriptText != null) {
                // split rules into separate lines
                String[] lines = scriptText.split("\n");
                List<String> rules = new ArrayList<String>();
                String nextRule = "";
                String sepr = "";
                String name = null;
                String targetClass = null;
                String targetMethod = null;
                String targetHelper = null;
                LocationType locationType = null;
                Location targetLocation = null;
                int lineNumber = 0;
                int maxLines = lines.length;
                boolean inRule = false;
                for (String line : lines) {
                    lineNumber++;
                    int startNumber = -1;
                    if (line.trim().startsWith("#")) {
                        if (inRule) {
                            // add a blank line in place of the comment so the line numbers
                            // are reported consistently during parsing
                            nextRule += sepr;
                            sepr = "\n";
                        } // else { // just drop comment line }
                    } else if (line.startsWith("RULE ")) {
                        inRule = true;
                        name = line.substring(5).trim();
                    } else if (!inRule) {
                        if (!line.trim().equals("")) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer : invalid text outside of RULE/ENDRULE " + "at line " + lineNumber + " in script " + file);
                        }
                    } else if (line.startsWith("CLASS ")) {
                        targetClass = line.substring(6).trim();
                    } else if (line.startsWith("METHOD ")) {
                        targetMethod = line.substring(7).trim();
                    } else if (line.startsWith("HELPER ")) {
                        targetHelper = line.substring(7).trim();
                    } else if ((locationType = LocationType.type(line)) != null) {
                        String parameters = LocationType.parameterText(line);
                        targetLocation = Location.create(locationType, parameters);
                        if (targetLocation == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer : invalid target location at line " + lineNumber + " in script " + scriptPaths.get(scriptIdx));
                        }
                    } else if (line.startsWith("ENDRULE")) {
                        if (name == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer : no matching RULE for ENDRULE at line " + lineNumber + " in script " + scriptPaths.get(scriptIdx));
                        } else if (targetClass == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer : no CLASS for RULE  " + name + " in script " + scriptPaths.get(scriptIdx));
                        } else if (targetMethod == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer : no METHOD for RULE  " + name + " in script " + scriptPaths.get(scriptIdx));
                        } else {
                            List<Script> scripts = targetToScriptMap.get(targetClass);
                            if (scripts == null) {
                                scripts = new ArrayList<Script>();
                                targetToScriptMap.put(targetClass, scripts);
                            }
                            if (targetLocation == null) {
                                targetLocation = Location.create(LocationType.ENTRY, "");
                            }
                            Script script = new Script(name, targetClass, targetMethod, targetHelper, targetLocation, nextRule, startNumber, file);
                            scripts.add(script);
                            if (isVerbose()) {
                                System.out.println("RULE " + script.getName());
                                System.out.println("CLASS " + script.getTargetClass());
                                System.out.println("METHOD " + script.getTargetMethod());
                                if (script.getTargetHelper() != null) {
                                    System.out.println("HELPER " + script.getTargetHelper());
                                }
                                if (targetLocation != null) {
                                    System.out.println(targetLocation);
                                } else {
                                    System.out.println("AT ENTRY");
                                }
                                System.out.println(script.getRuleText());
                                System.out.println("ENDRULE");
                            }
                        }
                        name = null;
                        targetClass = null;
                        targetMethod = null;
                        targetLocation = null;
                        nextRule = "";
                        sepr = "";
                        inRule = false;
                    } else if (lineNumber == maxLines && !nextRule.trim().equals("")) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer : no matching ENDRULE for RULE " + name + " in script " + scriptPaths.get(scriptIdx));
                    } else {
                        if (startNumber < 0) {
                            startNumber = lineNumber;
                        }
                        nextRule += sepr + line;
                        sepr = "\n";
                    }
                }
            }
        }
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
        byte[] newBuffer = classfileBuffer;
        // we only transform certain classes -- in  particular, we exclude bootstrap classes whose loader is null
        // and we exclude orchestration classes
        String internalClassName = TypeHelper.internalizeClass(className);

        if (loader == null || isOrchestrationClass(internalClassName) || !isTransformable(internalClassName)) {
            return null;
        }

        // ok, we need to check whether there are any scripts associated with this class and if so
        // we will consider transforming the byte code

        List<Script> scripts = targetToScriptMap.get(internalClassName);

        if (scripts != null) {
            for (Script script : scripts) {
                try {
                    newBuffer = transform(script, loader, internalClassName,  classBeingRedefined, newBuffer);
                } catch (Throwable th) {
                    System.err.println("Transformer.transform : caught throwable " + th);
                    th.printStackTrace(System.err);
                }
            }
        }

        // if the class is not in the default package then we also need to look for scripts
        // which specify the class without the package qualification

        int dotIdx = internalClassName.lastIndexOf('.');

        if (dotIdx >= 0) {
            scripts = targetToScriptMap.get(internalClassName.substring(dotIdx + 1));

            if (scripts != null) {
                for (Script script : scripts) {
                    try {
                        newBuffer = transform(script, loader, internalClassName,  classBeingRedefined, newBuffer);
                    } catch (Throwable th) {
                        System.err.println("Transformer.transform : caught throwable " + th);
                        th.printStackTrace(System.err);
                    }
                }
            }

        }

        if (newBuffer != classfileBuffer) {
            // see if we need to dump the transformed bytecode for checking
            if (dumpGeneratedClasses) {
                dumpClass(internalClassName, newBuffer);
            }
            return newBuffer;
        } else {
            return null;
        }
    }

    /**
     * this is a classloader used to define classes from bytecode
     *
     * TODO -- we probably need to use the protection domain of the trigger class somewhere here
     */
    private static class ClassbyteClassLoader extends ClassLoader
    {
        ClassbyteClassLoader(ClassLoader cl)
        {
            super(cl);
        }
        
        public Class addClass(String name, byte[] bytes)
                throws ClassFormatError
        {
            Class cl = defineClass(name, bytes, 0, bytes.length);
            resolveClass(cl);

            return cl;
        }
    }

    /**
     * a singleton instance of the classloader used to define classes from bytecode
     */
    private static ClassbyteClassLoader theLoader = new ClassbyteClassLoader(Transformer.class.getClassLoader());

    /**
     * a helper method which allows dynamic creation of generated helper adapter classes
     * @param helperAdapterName
     * @param classBytes
     * @return
     */
    public Class<?> loadHelperAdapter(Class<?> helperClass, String helperAdapterName, byte[] classBytes)
    {
         //return theLoader.addClass(helperAdapterName, classBytes);

        ClassbyteClassLoader loader = new ClassbyteClassLoader(helperClass.getClassLoader());

        return loader.addClass(helperAdapterName, classBytes);
    }

    /* switches controlling behaviour of transformer */

    /**
     * prefix for orchestration package
     */
    private static final String ORCHESTRATION_PACKAGE_PREFIX = "org.jboss.jbossts.orchestration.";

    /**
     * prefix for orchestration test package
     */
    private static final String ORCHESTRATION_TEST_PACKAGE_PREFIX = "org.jboss.jbossts.orchestration.tests.";

    /**
     * prefix for com.arjuna package
     */
    private static final String COM_ARJUNA_PACKAGE_PREFIX = "com.arjuna.";

    /**
     * prefix for org.jboss package
     */
    private static final String JAVA_LANG_PACKAGE_PREFIX = "java.lang.";

    /**
     * prefix for org.jboss package
     */
    private static final String ORG_JBOSS_PACKAGE_PREFIX = "org.jboss.";

    /**
     * system property set (to any value) in order to switch on dumping of generated bytecode to .class files
     */
    public static final String VERBOSE = ORCHESTRATION_PACKAGE_PREFIX + "verbose";

    /**
     * system property set (to any value) in order to switch on debug statements in the default Helper
     */

    public static final String DEBUG = ORCHESTRATION_PACKAGE_PREFIX + "debug";
    /**
     * system property set (to any value) in order to switch on compilation of rules and left unset
     * if rules are to be interpreted.
     */
    public static final String COMPILE_TO_BYTECODE = ORCHESTRATION_PACKAGE_PREFIX + "compileToBytecode";

    /**
     * system property set (to any value) in order to switch on dumping of generated bytecode to .class files
     */
    public static final String DUMP_GENERATED_CLASSES = ORCHESTRATION_PACKAGE_PREFIX + "dump.generated.classes";

    /* implementation */

    /**
     * system property identifying directory in which to dump generated bytecode .class files
     */
    public static final String DUMP_GENERATED_CLASSES_DIR = ORCHESTRATION_PACKAGE_PREFIX + "dump.generated.classes.directory";

    private byte[] transform(Script script, ClassLoader loader, String className, Class classBeingRedefined, byte[] targetClassBytes)
    {
        final String handlerClass = script.getTargetClass();
        final String handlerMethod = script.getTargetMethod();
        final String helperName = script.getTargetHelper();
        final Location handlerLocation = script.getTargetLocation();
        final int lineNumber = script.getLine();
        final String file = script.getFile();
        Class helperClass = null;
        if (helperName != null) {
            try {
                helperClass = loader.loadClass(helperName);
            } catch (ClassNotFoundException e) {
                System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : unknown helper class " + helperName + " for rule " + script.getName());
            }
        }
        if (isVerbose()) {
            System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : Inserting trigger event");
            System.out.println("  class " + handlerClass);
            System.out.println("  method " + handlerMethod);
            System.out.println("  " + handlerLocation);
        }
        final Rule rule;
        String ruleName = script.getName();
        try {
            rule = Rule.create(ruleName, handlerClass, handlerMethod, helperClass, handlerLocation, script.getRuleText(), lineNumber, file, loader);
        } catch (ParseException pe) {
            System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : error parsing rule " + ruleName + " : " + pe);
            return targetClassBytes;
        } catch (TypeException te) {
            System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : error checking rule " + ruleName + " : " + te);
            return targetClassBytes;
        } catch (Throwable th) {
            System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : error processing rule " + ruleName + " : " + th);
            return targetClassBytes;
        }
        if (isVerbose()) {
            System.out.println(rule);
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
            cr.accept(checkAdapter, 0);
        } catch (Throwable th) {
            System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : error applying rule " + rule.getName() + " to class " + className + th);
            th.printStackTrace(System.out);
            return targetClassBytes;
        }
        // only insert the rule trigger call if there is a suitable location in the target method
        if (checkAdapter.isVisitOk()) {
            cr = new ClassReader(targetClassBytes);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            // PrintWriter pw = new PrintWriter(System.out);
            // ClassVisitor traceAdapter = new TraceClassVisitor(cw, pw);
            // RuleTriggerAdapter adapter = handlerLocation.getRuleAdapter(traceAdapter, rule, className, handlerMethod);
            RuleTriggerAdapter adapter = handlerLocation.getRuleAdapter(cw, rule, className, handlerMethod);
            try {
                cr.accept(adapter, 0);
            } catch (Throwable th) {
                System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : error compiling rule " + rule.getName() + " for class " + className + th);
                th.printStackTrace(System.out);
                return targetClassBytes;
            }
            // hand back the transformed byte code
            return cw.toByteArray();
        }

        return targetClassBytes;
    }

    /**
     * test whether a class with a given name is located in the orchestration package
     * @param className
     * @return true if a class is located in the orchestration package otherwise return false
     */
    private boolean isOrchestrationClass(String className)
    {
        return className.startsWith(ORCHESTRATION_PACKAGE_PREFIX) && !className.startsWith(ORCHESTRATION_TEST_PACKAGE_PREFIX);
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
    private boolean isTransformable(String className)
    {
        /*
         * ok, we are now going to allow any code to be transformed so long as it is not in the java.lang package
        return (className.startsWith(COM_ARJUNA_PACKAGE_PREFIX) || className.startsWith(ORG_JBOSS_PACKAGE_PREFIX));
        */
        if (className.startsWith(JAVA_LANG_PACKAGE_PREFIX)) {
            return false;
        }

        return true;
    }
    /**
     * the instrumentation interface to the JVM
     */
    private final Instrumentation inst;

    /**
     * a mapping from class names which appear in rule targets to a script object holding the
     * rule details
     */

    private final HashMap<String, List<Script>> targetToScriptMap;

    /**
     *  switch to control verbose output during rule processing
     */
    private final static boolean verbose = (System.getProperty(VERBOSE) != null);

    /**
     *  switch to control verbose output during rule processing
     */
    private final static boolean debug = (System.getProperty(DEBUG) != null);

    /**
     *  switch to control verbose output during rule processing
     */
    private final static boolean compileToBytecode = (System.getProperty(COMPILE_TO_BYTECODE) != null);

    /**
     *  switch to control dumping of generated bytecode to .class files
     */
    private final static boolean dumpGeneratedClasses = (System.getProperty(DUMP_GENERATED_CLASSES) != null);

    /**
     *  directory in which to dump generated bytecode .class files (defaults to "."
     */
    private final static String dumpGeneratedClassesDir;

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

    public void maybeDumpClass(String fullName, byte[] bytes)
    {
        if (dumpGeneratedClasses) {
            dumpClass(fullName, bytes);
        }
    }

    private void dumpClass(String fullName, byte[] bytes)
    {
        int dotIdx = fullName.lastIndexOf('.');

        String name = (dotIdx < 0 ? fullName : fullName.substring(dotIdx + 1));
        String prefix = (dotIdx > 0 ? fullName.substring(0, dotIdx) : "");
        String dir = dumpGeneratedClassesDir + File.separator + prefix.replaceAll("\\.", File.separator);
        if (!ensureDumpDirectory(dir)) {
            System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : Cannot dump transformed bytes to directory " + dir + File.separator + prefix);
            return;
        }
        name = dir + File.separator + name + ".class";
        System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : Saving transformed bytes to " + name);
        try {
            FileOutputStream fio = new FileOutputStream(name);
            fio.write(bytes);
            fio.close();
        } catch (IOException ioe) {
            System.out.println("Error saving transformed bytes to" + name);
            ioe.printStackTrace(System.out);
        }
    }

    private boolean ensureDumpDirectory(String fileName)
    {
        File file = new File(fileName);
        if (file.exists()) {
            return (file.isDirectory() && file.canWrite());
        } else {
            return file.mkdirs();
        }
    }
}
