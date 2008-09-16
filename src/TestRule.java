import org.jboss.jbossts.orchestration.rule.Condition;
import org.jboss.jbossts.orchestration.rule.Event;
import org.jboss.jbossts.orchestration.rule.Action;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestRule
{
    public static void main(String[] args)
    {
        ClassLoader loader = TestRule.class.getClassLoader();
        Event event = null;
        Condition condition = null;
        Action action = null;
        Rule rule = null;
        String className = null;
        String methodName = null;
        String descriptor = null;
        for (int i = 0; i < args.length ; i++) {
            if ("-class".equals(args[i])) {
                System.out.println("Using class name " + args[++i]);
                className = args[i];
            } else if ("-method".equals(args[i])) {
                System.out.println("Using method name " + args[++i]);
                methodName = args[i];
            } else if ("-descriptor".equals(args[i])) {
                System.out.println("Using method descriptor " + args[++i]);
                descriptor = args[i];
            } else if ("-rule".equals(args[i])) {
                System.out.println("Creating rule from " + args[++i]);
                try {
                    rule = Rule.create("TestRule" + i, args[i], loader);
                    System.out.print(rule);
                    if (methodName != null &&
                            className != null) {
                        maybeTypeCheck(rule, className, methodName, descriptor);
                    }
                } catch (Throwable th) {
                    // bad rule event
                    System.err.println(": error " + th);
                    th.printStackTrace(System.err);
                }
            } else if ("-event".equals(args[i])) {
                System.out.println("Creating event from " + args[++i]);
                try {
                    rule = Rule.create("TestRule" + i, loader);
                    rule.setEvent(args[i]);
                    System.out.print(rule);
                } catch (Throwable th) {
                    // bad rule event
                    System.err.println(": error " + th);
                    th.printStackTrace(System.err);
                }
            } else if ("-condition".equals(args[i])) {
                System.out.println("Creating condition from " + args[++i]);
                try {
                    if (rule == null || rule.getCondition() != null) {
                        rule = Rule.create("TestRule" + i, loader);
                    }
                    if (rule.getEvent() == null) {
                        rule.setEvent("");
                    }
                    rule.setCondition(args[i]);
                    System.out.print(rule);
                } catch (Throwable th) {
                    // bad rule event
                    System.err.println(": error " + th);
                    th.printStackTrace(System.err);
                }
            } else if ("-action".equals(args[i])) {
                System.out.println("Creating action from " + args[++i]);
                try {
                    if (rule == null || rule.getAction() != null) {
                        rule = Rule.create("TestRule" + i, loader);
                    }
                    if (rule.getEvent() == null) {
                        rule.setEvent("");
                    }
                    if (rule.getCondition() == null) {
                        rule.setCondition("");
                    }
                    rule.setAction(args[i]);
                    System.out.print(rule);
                    if (methodName != null &&
                            className != null) {
                        maybeTypeCheck(rule, className, methodName, descriptor);
                    }
                } catch (Throwable th) {
                    // bad rule event
                    System.err.println(": error " + th);
                    th.printStackTrace(System.err);
                }
            } else if ("-file".equals(args[i])) {
                String fileName = args[++i];
                System.out.println("Creating rule from file " + fileName);
                try {
                    byte[] bytes;
                    String text;
                    FileInputStream fis = new FileInputStream(fileName);
                    bytes = new byte[fis.available()];
                    fis.read(bytes);
                    text = new String(bytes);
                    rule = Rule.create("TestRule" + i, text, loader);
                    System.out.print(rule);
                    if (methodName != null &&
                            className != null) {
                        maybeTypeCheck(rule, className, methodName, descriptor);
                    }
                } catch (Throwable th) {
                    // bad rule event
                    System.err.println(": error " + th);
                    th.printStackTrace(System.err);
                }
            }
        }
    }

    public static void maybeTypeCheck(Rule rule, String className, String methodName, String descriptor)
    {
        Type type = rule.getTypeGroup().create(className);

        if (type != null && !type.isUndefined()) {
            Class clazz = type.getClass();
            Method[] methods = type.getTargetClass().getMethods();
            Method method = null;

            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals(methodName)) {
                    if (descriptor != null) {
                        Class<?>[] paramTypes = methods[i].getParameterTypes();
                        Class<?> retType = methods[i].getReturnType();
                        String methDescriptor = makeDescriptor(paramTypes, retType);
                        if (TypeHelper.equalDescriptors(descriptor, methDescriptor)) {
                            method = methods[i];
                            break;
                        }
                    } else if (method != null) {
                            // hmm, cannot resolve alternatives
                            System.out.println("ambiguous method name " + className + "." + methodName);
                            method = null;
                            break;
                    } else {
                        method = methods[i];
                        // carry on in case the method name is ambiguous
                    }
                }
            }
            if (method != null) {
                if (descriptor == null) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    Class<?> retType = method.getReturnType();
                    descriptor = makeDescriptor(paramTypes, retType);
                }
                try {
                    int modifiers = method.getModifiers();
                    int access = 0;
                    if ((modifiers & Modifier.STATIC) != 0) {
                        access |= Opcodes.ACC_STATIC;
                    }
                    rule.setTypeInfo(className, access, methodName,  descriptor);
                    rule.typeCheck();
                } catch (TypeException te) {
                    System.out.println("error typechecking against " + className + "." + methodName + " : " + te);
                }
            }
        }
    }

    public static String makeDescriptor(Class<?>[] paramTypes, Class<?> retType)
    {
        String descriptor = "(";

        for (int i = 0; i < paramTypes.length; i++) {
            descriptor += TypeHelper.externalizeType(paramTypes[i].getCanonicalName());
        }
        descriptor += ") ";

        descriptor += TypeHelper.externalizeType(retType.getCanonicalName());

        return descriptor;
    }
}