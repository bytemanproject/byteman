package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.annotation.EventHandler;
import org.jboss.jbossts.orchestration.annotation.EventHandlerClass;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * byte code transformer used to introduce orchestration events into JBoss code
 */
public class Transformer implements ClassFileTransformer {
    /**
     * constructor allowing this transformer to be provided with access to the JVM's instrumentation
     * implementation
     *
     * @param inst the instrumentation object used to interface to the JVM
     */
    public Transformer(Instrumentation inst, List<String> scriptPaths, List<String> scriptTexts)
            throws Exception
    {
        this.inst = inst;
        targetToScriptMap = new HashMap<String, List<Script>>();

        Iterator<String> iter = scriptTexts.iterator();
        int scriptIdx = 0;
        while (iter.hasNext()) {
            String scriptText = iter.next();
            if (scriptText != null) {
                // split rules into separate lines
                String[] lines = scriptText.split("\n");
                List<String> rules = new ArrayList<String>();
                String nextRule = "";
                String sepr = "";
                String name = null;
                String targetClass = null;
                String targetMethod = null;
                int targetLine = -1;
                int lineNumber = 0;
                int maxLines = lines.length;
                boolean inRule = false;
                for (String line : lines) {
                   lineNumber++;
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
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer: invalid text outside of RULE/ENDRULE " + "at line " + lineNumber + " in script " + scriptPaths.get(scriptIdx));
                        }
                    } else if (line.startsWith("CLASS ")) {
                        targetClass = line.substring(6).trim();
                    } else if (line.startsWith("METHOD ")) {
                        targetMethod = line.substring(7).trim();
                    } else if (line.startsWith("LINE ")) {
                        String lineSpec = line.substring(5).trim();
                        try {
                            targetLine = Integer.valueOf(lineSpec);
                        } catch (NumberFormatException e) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer: invalid LINE specification " + lineSpec + "for RULE " + name + " in script " + scriptPaths.get(scriptIdx));
                        }
                    } else if (line.startsWith("ENDRULE")) {
                        if (name == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer: no matching RULE for ENDRULE at line " + lineNumber + " in script " + scriptPaths.get(scriptIdx));
                        } else if (targetClass == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer: no CLASS for RULE  " + name + " in script " + scriptPaths.get(scriptIdx));
                        } else if (targetMethod == null) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer: no METHOD for RULE  " + name + " in script " + scriptPaths.get(scriptIdx));
                        } else {
                            List<Script> scripts = targetToScriptMap.get(targetClass);
                            if (scripts == null) {
                                scripts = new ArrayList<Script>();
                                targetToScriptMap.put(targetClass, scripts);
                            }
                            Script script = new Script(name, targetClass, targetMethod, targetLine, nextRule);
                            scripts.add(script);
                            System.out.println("RULE " + script.getName());
                            System.out.println("CLASS " + script.getTargetClass());
                            System.out.println("METHOD " + script.getTargetMethod());
                            System.out.println("LINE " + script.getTargetLine());
                            System.out.println(script.getRuleText());
                            System.out.println("ENDRULE");
                        }
                        name = null;
                        targetClass = null;
                        targetMethod = null;
                        nextRule = "";
                        sepr = "";
                        inRule = false;
                    } else if (lineNumber == maxLines && !nextRule.trim().equals("")) {
                            throw new Exception("org.jboss.jbossts.orchestration.agent.Transformer: no matching ENDRULE for RULE " + name + " in script " + scriptPaths.get(scriptIdx));
                    } else {
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
            // switch on to dump transformed bytecode for checking
            if (false) {
                String name = (dotIdx < 0 ? internalClassName : internalClassName.substring(dotIdx + 1));
                name += ".class";
                System.out.println("Saving transformed bytes to " + name);
                try {
                    FileOutputStream fio = new FileOutputStream(name);
                    fio.write(newBuffer);
                    fio.close();
                } catch (IOException ioe) {
                    System.out.println("Error saving transformed bytes to" + name);
                    ioe.printStackTrace(System.out);
                }
            }
            return newBuffer;
        } else {
            return null;
        }
    }


    private byte[] transform(Script script, ClassLoader loader, String className, Class classBeingRedefined, byte[] targetClassBytes)
    {
        final String handlerClass = script.getTargetClass();
        final String handlerMethod = script.getTargetMethod();
        final int handlerLine = script.getTargetLine();
        System.out.println("org.jboss.jbossts.orchestration.agent.Transformer: Inserting trigger event");
        System.out.println("  class " + handlerClass);
        System.out.println("  method " + handlerMethod);
        System.out.println("  line " + handlerLine);
        final Rule rule;
        String ruleName = script.getName();
        try {
            rule = Rule.create(ruleName, handlerClass, handlerMethod, handlerLine, script.getRuleText(), loader);
        } catch (ParseException pe) {
            System.out.println("Transformer : error parsing rule : " + pe);
            return targetClassBytes;
        } catch (TypeException te) {
            System.out.println("Transformer : error checking rule : " + te);
            return targetClassBytes;
        } catch (Throwable th) {
            System.out.println("Transformer : error processing rule : " + th);
            return targetClassBytes;
        }
        System.out.println(rule);

        // ok, we have a rule with a matchingclass and a candidiate method and line number
        // we need to see if the class has a matching method and, if so, add a call to
        // execute the rule when we hit the relevant line

        ClassReader cr = new ClassReader(targetClassBytes);
        // ClassWriter cw = new ClassWriter(0);
        ClassVisitor empty = new EmptyVisitor();
        RuleCheckAdapter checkAdapter = new RuleCheckAdapter(empty, className, handlerMethod, handlerLine);
        // PrintWriter pw = new PrintWriter(System.out);
        // ClassVisitor traceAdapter = new TraceClassVisitor(cw, pw);
        // RuleCheckAdapter adapter = new RuleAdapter(traceAdapter, rule, className, handlerMethod, handlerLine, loader);
        try {
            cr.accept(checkAdapter, 0);
        } catch (Throwable th) {
            System.out.println("Transformer : error applying rule " + rule.getName() + " to class " + className + th);
            th.printStackTrace(System.out);
            return targetClassBytes;
        }
        // only insert the rule trigger call if there is a suitable line in the target method
        if (checkAdapter.isVisitOk()) {
            cr = new ClassReader(targetClassBytes);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            // PrintWriter pw = new PrintWriter(System.out);
            // ClassVisitor traceAdapter = new TraceClassVisitor(cw, pw);
            // RuleAdapter adapter = new RuleAdapter(traceAdapter, rule, className, handlerMethod, handlerLine);
            RuleAdapter adapter = new RuleAdapter(cw, rule, className, handlerMethod, handlerLine);
            try {
                cr.accept(adapter, 0);
            } catch (Throwable th) {
                System.out.println("Transformer : error compiling rule " + rule.getName() + " for class " + className + th);
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
        return className.startsWith("org.jboss.jbossts.orchestration.");
    }

    /**
     * test whether a class with a given name is a potential candidate for insertion of event notifications
     * @param className
     * @return true if a class is a potential candidate for insertion of event notifications otherwise return false
     */
    private boolean isTransformable(String className)
    {
        return (className.startsWith("com.arjuna.") || className.startsWith("org.jboss."));
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
     * information about a single rule derived from a rule script
     */
    
    private static class Script
    {
        private String name;
        private String targetClass;
        private String targetMethod;
        private int targetLine;
        private String ruleText;

        Script (String name, String targetClass, String targetMethod, int targetLine, String ruleText)
        {
            this.name = name;
            this.targetClass = targetClass;
            this.targetMethod = targetMethod;
            this.targetLine = targetLine;
            this.ruleText = ruleText;
        }

        public String getName() {
            return name;
        }

        public String getTargetClass() {
            return targetClass;
        }

        public String getTargetMethod() {
            return targetMethod;
        }

        public int getTargetLine() {
            return targetLine;
        }

        public String getRuleText() {
            return ruleText;
        }
    }
}
