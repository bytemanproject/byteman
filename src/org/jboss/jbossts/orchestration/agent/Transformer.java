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
    public Transformer(Instrumentation inst, List<Class> ruleClasses)
    {
        this.inst = inst;
        this.ruleClasses = ruleClasses;
        targetToHandlerClassMap = new HashMap<String, List<Annotation>>();
        targetToHandlerMethodMap = new HashMap<String, List<Method>>();

        // insert all event handling methods into the map indexed by the associated target class

        for (Class ruleClass : ruleClasses) {
            Annotation classAnnotation = ruleClass.getAnnotation(EventHandlerClass.class);
            for (Method method : ruleClass.getDeclaredMethods()) {
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                if (eventHandler != null) {
                    String target = eventHandler.targetClass();
                    if (isTransformable(target) && !isOrchestrationClass(target)) {
                        List<Annotation> clazzes = targetToHandlerClassMap.get(target);
                        if (clazzes == null) {
                            clazzes = new ArrayList<Annotation>();
                        }
                        if (!clazzes.contains(classAnnotation)) {
                            clazzes.add(classAnnotation);
                        }
                        List<Method> methods = targetToHandlerMethodMap.get(target);
                        if (methods == null) {
                            methods = new ArrayList<Method>();
                            targetToHandlerMethodMap.put(target, methods);
                        }
                        methods.add(method);
                    } else {
                        System.err.println("org.jboss.jbossts.orchestration.agent.Transformer: requested to transform invalid class " + target);
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

        // ok, we need to check whether there are any event handlers associated with this class and if so
        // we will consider transforming the byte code

        List<Method> handlerMethods = targetToHandlerMethodMap.get(internalClassName);

        if (handlerMethods != null) {
            for (Method handlerMethod : handlerMethods) {
                try {
                    newBuffer = transform(handlerMethod.getAnnotation(EventHandler.class), loader, internalClassName,  classBeingRedefined, newBuffer);
                } catch (Throwable th) {
                    System.err.println("transform  : caught throwable " + th);
                    th.printStackTrace(System.err);
                }
            }
        }

        // if the class is not in the defautl package then we also need to look for handlers
        // which specify the class without the package qualification

        int dotIdx = internalClassName.lastIndexOf('.');

        if (dotIdx >= 0) {
            handlerMethods = targetToHandlerMethodMap.get(internalClassName.substring(dotIdx + 1));
            if (handlerMethods != null) {
                for (Method handlerMethod : handlerMethods) {
                    try {
                        newBuffer = transform(handlerMethod.getAnnotation(EventHandler.class), loader, internalClassName,  classBeingRedefined, newBuffer);
                    } catch (Throwable th) {
                        System.err.println("transform  : caught throwable " + th);
                        th.printStackTrace(System.err);
                    }
                }
            }
        }

        if (newBuffer != classfileBuffer) {
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

    private byte[] transform(EventHandler handler, ClassLoader loader, String className, Class classBeingRedefined, byte[] targetClassBytes)
    {
        final String handlerClass = handler.targetClass();
        final String handlerMethod = handler.targetMethod();
        final int handlerLine = handler.targetLine();
        System.out.println("org.jboss.jbossts.orchestration.agent.Transformer: Inserting trigger event");
        System.out.println("  class " + handlerClass);
        System.out.println("  method " + handlerMethod);
        System.out.println("  line " + handlerLine);
        final Rule rule;
        String ruleName = handlerClass + "::" + handlerMethod;
        if (handlerLine >= 0) {
            ruleName += "@" + handlerLine;
        }
        try {
            rule = Rule.create(ruleName, handler.event(), handler.condition(), handler.action(), loader);
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
     * the set of rule classes supplied to the agent program
     */
    private final List<Class> ruleClasses;

    /**
     * a mapping from class names which appear in rule targets to the associated event handling methods
     */

    private final HashMap<String, List<Method>> targetToHandlerMethodMap;

    /**
     * a mapping from class names which appear in rule targets to the associated event handling classes
     */

    private final HashMap<String, List<Annotation>> targetToHandlerClassMap;
    
    /**
     * external form of ECA rule class from which synthesised ECA handler classes are derived
     */
    private static final String ECA_RULE_CLASS_NAME = "org/jboss/jbossts/orchestration/rule/ECARule";
}
