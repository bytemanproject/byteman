package org.jboss.jbossts.orchestration.agent;

import org.jboss.jbossts.orchestration.annotation.EventHandler;
import org.jboss.jbossts.orchestration.annotation.EventHandlerClass;
import org.jboss.jbossts.orchestration.rule.grammar.ECATokenLexer;
import org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser;
import org.jboss.jbossts.orchestration.rule.Event;
import org.jboss.jbossts.orchestration.rule.Condition;
import org.jboss.jbossts.orchestration.rule.Action;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.objectweb.asm.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

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
                    String target = externalize(eventHandler.targetClass());
                    if (isTransformable(target)) {
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
        // we only transform certain classes -- in  particular, we exclude bootstrap classes whose loader is null
        // and we exclude orchestration classes
        if (loader == null || className.startsWith("org/jboss/jbossts/orchestration/") || !isTransformable(className)) {
            return classfileBuffer;
        }

        // ok, we need to check whether there are any event handlers associated with this class and if so
        // we will consider transforming the byte code

        List<Method> handlerMethods = targetToHandlerMethodMap.get(className);

        if (handlerMethods != null) {
            for (Method handlerMethod : handlerMethods) {
                try {
                    transform(handlerMethod.getAnnotation(EventHandler.class), loader, className,  classBeingRedefined, classfileBuffer);
                } catch (Throwable th) {
                    System.err.println("transform  : caught throwable " + th);
                }
            }
        }

        return classfileBuffer;
    }

    private byte[] transform(EventHandler handler, ClassLoader loader, String className, Class targetClass, byte[] targetClassBytes)
    {
        // set up trees for parse;
        CommonTree eventTree = null;
        CommonTree conditionTree = null;
        CommonTree actionTree = null;

        System.out.println("org.jboss.jbossts.orchestration.agent.Transformer: Inserting trigger event");
        System.out.println("                                                 : class " + className);
        System.out.println("                                                 : method " + handler.targetMethod());
        System.out.println("                                                 : line " + handler.targetLine());
        Event event;
        TypeGroup typeGroup;
        Bindings bindings;
        Condition condition;
        Action action;
        if ("".equals(handler.event())) {
            System.out.println("                                                 : WHEN []");
        } else {
            System.out.println("                                                : " + handler.event());
        }
        event = Event.create(handler.event());
        typeGroup = event.getTypeGroup();
        bindings = event.getBindings();
        if ("".equals(handler.condition())) {
            System.out.println("                                                 : IF true");
        } else {
            System.out.println("                                                : " + handler.condition());
        }
        condition = Condition.create(typeGroup, bindings, handler.condition());
        if ("".equals(handler.action())) {
            System.out.println("                                                 : DO nothing");
        } else {
            System.out.println("                                                 : " + handler.action());
        }
        action = Action.create(typeGroup, bindings, handler.action());
        final String finalClassName = className;
        final EventHandler finalHandler = handler;
        final ClassLoader finalLoader = loader;
        final Class finalClass = targetClass;
        final String targetMethod = handler.targetMethod();
        final int targetLine = handler.targetLine();
        final String targetName = parseMethodName(targetMethod);
        final String targetSignature = parseMethodSignature(targetMethod);
        final String ecaRuleFieldName = generateFieldName(targetName, targetSignature);

        final ClassReader cr = new ClassReader(targetClassBytes);
        ClassWriter cw = new ClassWriter(0);
        ClassAdapter adapter = new ClassAdapter(cw) {
            public void visit(final int version,
                    final int access,
                    final String name,
                    final String signature,
                    final String superName,
                    final String[] interfaces)
            {
                // create a class derived from ECARule to implement the event handler and
                // instantiate it with a singleton instance
                //
                // n.b. we have to wait until here to do this because we need to resolve type names
                // in the rule dynamically aginst types which are only available via the target class
                // and its class loader.
                //
                // TODO see if we need to throw up if we try to resolve types which are not yet loaded

                Class handlerClazz = generateHandlerClass(finalHandler, finalLoader, finalClassName, finalClass);
                Object handlerSingleton = null;
                try {
                    // TODO do we want to give the handler class some arguments?
                    handlerSingleton = handlerClazz.newInstance();
                } catch (InstantiationException e) {
                    // should not happen!!!
                } catch (IllegalAccessException e) {
                    // should not happen!!!
                }

                // add a static field to the class to hold the singleton
/*                FieldVisitor visitor = visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                        ecaRuleFieldName,
                        handlerClazz.getName(),
                        null,
                        handlerSingleton);
*/
            }

            public MethodVisitor visitMethod(final int access,
                                             final String name,
                                             final String desc,
                                             final String signature,
                                             final String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (name.equals(targetName) &&
                        (targetSignature.length() == 0 || signature.equals(targetSignature))) {
                    return new MethodAdapter(mv) {
                        public void visitCode()
                        {
                            // generate try catch block for kill thread/JVM
                            System.out.println("                                                 : // generate wrapper");
                            System.out.println("                                                 : try { ");
                            super.visitCode();
                            System.out.println("                                                 : } catch (KillThreadException kte) { ");
                            System.out.println("                                                 :   killThread();");
                            System.out.println("                                                 : } catch (KillJVMException kje) { ");
                            System.out.println("                                                 :   killJVM();");
                            System.out.println("                                                 : }");
                            // and the rest
                        }
                        public void visitLineNumber(final int line, final Label start)
                        {
                            if (line == targetLine) {
                                // insert call to relevant event handler instance
                                System.out.println("                                                 : // generate event notification");
                                System.out.println("                                                 :    " + finalClassName + "." + ecaRuleFieldName + ".event(this, . . .);");
                                System.out.println("                                                 : }");
                            }
                            super.visitLineNumber(line, start);
                        }
                    };
                }
                return mv;
            }
        };

        cr.accept(adapter, 0);
        return targetClassBytes;
    }

    /**
     * convert a classname from canonical form to the form used to represent it externally i.e. replace
     * all dots with slashes
     *
     * @param className
     * @return
     */
    private String externalize(String className)
    {
        return className.replaceAll("\\.", "/");
    }

    /**
     * test whether a class witha given name is a potential candidate for insertion of event notifications
     * @param className
     * @return true if a class is a potential candidate for insertion of event notifications otherwise return false
     */
    private boolean isTransformable(String className)
    {
        return (className.startsWith("com/arjuna/"));
    }

    /**
     * split off the method name preceding the signature and return it
     * @param targetMethod - the unqualified method name, possibly including signature
     * @return
     */
    private String parseMethodName(String targetMethod) {
        int sigIdx = targetMethod.indexOf("(");
        if (sigIdx > 0) {
            return targetMethod.substring(0, sigIdx).trim();
        } else {
            return targetMethod;
        }
    }

    /**
     * split off the signature following the method name and return it
     * @param targetMethod - the unqualified method name, possibly including signature
     * @return
     */
    private String parseMethodSignature(String targetMethod) {
        int sigIdx = targetMethod.indexOf("(");
        if (sigIdx >= 0) {
            return targetMethod.substring(sigIdx, targetMethod.length()).trim();
        } else {
            return "";
        }
    }

    /**
     * split off the signature following the method name and return it
     * @param targetName the unqualified method name, not including signature
     * @param targetSignature the method signature including brackets types and return type
     * @return
     */
    private String generateFieldName(String targetName, String targetSignature) {
        String result = targetName;
        int startIdx = targetSignature.indexOf("(");
        int endIdx = targetSignature.indexOf(")");
        if (startIdx < 0) {
            startIdx = 0;
        }
        if (endIdx < 0) {
            endIdx = targetSignature.length() - 1;
        }

        String args = targetSignature.substring(startIdx, endIdx + 1);

        // remove any brackets, commas, spaces, semi-colons, slashes and '[' characters
        args = args.replaceAll("\\(", "\\$_\\$");
        args = args.replaceAll("\\)", "\\$_\\$");
        args = args.replaceAll(",", "\\$1\\$");
        args = args.replaceAll(" ", "\\$2\\$");
        args = args.replaceAll(";", "\\$3\\$");
        args = args.replaceAll("\\/", "\\$4\\$");
        args = args.replaceAll("\\[", "\\$5\\$");

        return result + args;
    }

    private Class generateHandlerClass(EventHandler handler, ClassLoader loader, String targetClassName, Class targetClass)
    {
        // TODO -- write this but use Object for now
        return Object.class;
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
