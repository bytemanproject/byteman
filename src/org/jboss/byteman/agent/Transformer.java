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
        targetToScriptMap = new HashMap<String, List<RuleScript>>();

        Iterator<String> iter = scriptTexts.iterator();
        int scriptIdx = 0;
        while (iter.hasNext()) {
            String scriptText = iter.next();
            String file = scriptPaths.get(scriptIdx);
            List<RuleScript> ruleScripts = processScripts(scriptText, file);
            for (RuleScript ruleScript : ruleScripts) {
                addScript(ruleScript);
            }
        }
    }

    private List<RuleScript> processScripts(String scriptText, String scriptFile) throws Exception
    {
        List<RuleScript> ruleScripts = new LinkedList<RuleScript>();

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
            int startNumber = -1;
            int maxLines = lines.length;
            boolean inRule = false;
            for (String line : lines) {
                line = line.trim();
                lineNumber++;
                if (line.startsWith("#")) {
                    if (inRule) {
                        // add a blank line in place of the comment so the line numbers
                        // are reported consistently during parsing
                        nextRule += sepr;
                        sepr = "\n";
                    } // else { // just drop comment line }
                } else if (line.startsWith("RULE ")) {
                    inRule = true;
                    name = line.substring(5).trim();
                    if (name.equals("")) {
                        throw new Exception("org.jboss.byteman.agent.Transformer : RULE with no name at line " + lineNumber + " in script " + scriptFile);
                    }
                } else if (!inRule) {
                    if (!line.equals("")) {
                        throw new Exception("org.jboss.byteman.agent.Transformer : invalid text outside of RULE/ENDRULE " + "at line " + lineNumber + " in script " + scriptFile);
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
                        throw new Exception("org.jboss.byteman.agent.Transformer : invalid target location at line " + lineNumber + " in script " + scriptFile);
                    }
                } else if (line.startsWith("ENDRULE")) {
                    if (name == null) {
                        throw new Exception("org.jboss.byteman.agent.Transformer : no matching RULE for ENDRULE at line " + lineNumber + " in script " + scriptFile);
                    } else if (targetClass == null) {
                        throw new Exception("org.jboss.byteman.agent.Transformer : no CLASS for RULE  " + name + " in script " + scriptFile);
                    } else if (targetMethod == null) {
                        throw new Exception("org.jboss.byteman.agent.Transformer : no METHOD for RULE  " + name + " in script " + scriptFile);
                    } else {
                        if (targetLocation == null) {
                            targetLocation = Location.create(LocationType.ENTRY, "");
                        }
                        ruleScripts.add(new RuleScript(name, targetClass, targetMethod, targetHelper, targetLocation, nextRule, startNumber, scriptFile));
                    }
                    name = null;
                    targetClass = null;
                    targetMethod = null;
                    targetLocation = null;
                    nextRule = "";
                    sepr = "";
                    inRule = false;
                    // reset start nuuber so we pick up the next rule text line
                    startNumber = -1;
                } else if (lineNumber == maxLines && !nextRule.trim().equals("")) {
                    throw new Exception("org.jboss.byteman.agent.Transformer : no matching ENDRULE for RULE " + name + " in script " + scriptFile);
                } else {
                    // this is a line of rule text - see if it is the first one
                    if (startNumber < 0) {
                        startNumber = lineNumber;
                    }
                    nextRule += sepr + line;
                    sepr = "\n";
                }
            }
        }

        return ruleScripts;
    }

    private void indexScriptByTarget(RuleScript ruleScript)
    {
        String targetClass = ruleScript.getTargetClass();

        List<RuleScript> ruleScripts;

        synchronized (targetToScriptMap) {
            ruleScripts = targetToScriptMap.get(targetClass);
            if (ruleScripts == null) {
                ruleScripts = new ArrayList<RuleScript>();
                targetToScriptMap.put(targetClass, ruleScripts);
            }
        }

        ruleScripts.add(ruleScript);
    }

    private void dumpScript(RuleScript ruleScript)
    {
        String file = ruleScript.getFile();
        int line = ruleScript.getLine();
        if (file != null) {
            System.out.println("# " + file + " line " + line);
        }
        System.out.println("RULE " + ruleScript.getName());
        System.out.println("CLASS " + ruleScript.getTargetClass());
        System.out.println("METHOD " + ruleScript.getTargetMethod());
        if (ruleScript.getTargetHelper() != null) {
            System.out.println("HELPER " + ruleScript.getTargetHelper());
        }
        System.out.println(ruleScript.getTargetLocation());
        System.out.println(ruleScript.getRuleText());
        System.out.println("ENDRULE");
    }

    private void addScript(RuleScript ruleScript) throws Exception
    {
        indexScriptByTarget(ruleScript);

        if (isVerbose()) {
            dumpScript(ruleScript);
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
        // we only transform certain classes -- we do allow bootstrap classes whose loader is null
        // but we exclude byteman classes and java.lang classes
        String internalClassName = TypeHelper.internalizeClass(className);

        if (isBytemanClass(internalClassName) || !isTransformable(internalClassName)) {
            return null;
        }

        // TODO-- reconsider this as it is a bit dodgy as far as security is concerned

        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }

        // ok, we need to check whether there are any scripts associated with this class and if so
        // we will consider transforming the byte code

        List<RuleScript> ruleScripts = targetToScriptMap.get(internalClassName);

        if (ruleScripts != null) {
            for (RuleScript ruleScript : ruleScripts) {
                try {
                    newBuffer = transform(ruleScript, loader, internalClassName,  classBeingRedefined, newBuffer);
                } catch (Throwable th) {
                    System.err.println("Transformer.transform : caught throwable " + th);
                    th.printStackTrace(System.err);
                }
            }
        }

        // if the class is not in the default package then we also need to look for ruleScripts
        // which specify the class without the package qualification

        int dotIdx = internalClassName.lastIndexOf('.');

        if (dotIdx >= 0) {
            ruleScripts = targetToScriptMap.get(internalClassName.substring(dotIdx + 1));

            if (ruleScripts != null) {
                for (RuleScript ruleScript : ruleScripts) {
                    try {
                        newBuffer = transform(ruleScript, loader, internalClassName,  classBeingRedefined, newBuffer);
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
                dumpClass(internalClassName, newBuffer, classfileBuffer);
            }
            return newBuffer;
        } else {
            return null;
        }
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
     * system property set (to any value) in order to switch on compilation of rules and left unset
     * if rules are to be interpreted.
     */
    public static final String COMPILE_TO_BYTECODE = BYTEMAN_PACKAGE_PREFIX + "compileToBytecode";

    /**
     * system property set (to any value) in order to switch on dumping of generated bytecode to .class files
     */
    public static final String DUMP_GENERATED_CLASSES = BYTEMAN_PACKAGE_PREFIX + "dump.generated.classes";

    /* implementation */

    /**
     * system property identifying directory in which to dump generated bytecode .class files
     */
    public static final String DUMP_GENERATED_CLASSES_DIR = BYTEMAN_PACKAGE_PREFIX + "dump.generated.classes.directory";

    protected byte[] transform(RuleScript ruleScript, ClassLoader loader, String className, Class classBeingRedefined, byte[] targetClassBytes)
    {
        final String handlerClass = ruleScript.getTargetClass();
        final String handlerMethod = ruleScript.getTargetMethod();
        final String helperName = ruleScript.getTargetHelper();
        final Location handlerLocation = ruleScript.getTargetLocation();
        Class helperClass = null;
        if (helperName != null) {
            try {
                helperClass = loader.loadClass(helperName);
            } catch (ClassNotFoundException e) {
                System.out.println("org.jboss.byteman.agent.Transformer : unknown helper class " + helperName + " for rule " + ruleScript.getName());
            }
        }
        if (isVerbose()) {
            System.out.println("org.jboss.byteman.agent.Transformer : Inserting trigger event");
            System.out.println("  class " + handlerClass);
            System.out.println("  method " + handlerMethod);
            System.out.println("  " + handlerLocation);
        }
        final Rule rule;
        String ruleName = ruleScript.getName();
        try {
            rule = Rule.create(ruleScript, helperClass, loader);
        } catch (ParseException pe) {
            System.out.println("org.jboss.byteman.agent.Transformer : error parsing rule " + ruleName + " : " + pe);
            ruleScript.recordTransform(loader, className, null, pe);
            return targetClassBytes;
        } catch (TypeException te) {
            System.out.println("org.jboss.byteman.agent.Transformer : error checking rule " + ruleName + " : " + te);
            ruleScript.recordTransform(loader, className, null, te);
            return targetClassBytes;
        } catch (Throwable th) {
            System.out.println("org.jboss.byteman.agent.Transformer : error processing rule " + ruleName + " : " + th);
            ruleScript.recordTransform(loader, className, null, th);
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
            cr.accept(checkAdapter, ClassReader.EXPAND_FRAMES);
        } catch (Throwable th) {
            System.out.println("org.jboss.byteman.agent.Transformer : error applying rule " + rule.getName() + " to class " + className + " " + th);
            th.printStackTrace(System.out);
            ruleScript.recordTransform(loader, className, rule, th);
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
                cr.accept(adapter, ClassReader.EXPAND_FRAMES);
            } catch (Throwable th) {
                System.out.println("org.jboss.byteman.agent.Transformer : error injecting trigger for rule " + rule.getName() + " into class " + className + " " +  th);
                th.printStackTrace(System.out);
                ruleScript.recordTransform(loader, className, rule, th);
                return targetClassBytes;
            }
            // only return transformed code if ruleScript is still active

            if (ruleScript.recordTransform(loader, className, rule)) {
                // hand back the transformed byte code
                return cw.toByteArray();
            }
        }

        return targetClassBytes;
    }

    /**
     * test whether a class with a given name is located in the byteman package
     * @param className
     * @return true if a class is located in the byteman package otherwise return false
     */
    private boolean isBytemanClass(String className)
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

    private final HashMap<String, List<RuleScript>> targetToScriptMap;

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
        dumpClass(fullName, bytes, null);
    }

    private void dumpClass(String fullName, byte[] bytes, byte[] oldBytes)
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
