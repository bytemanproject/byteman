import org.jboss.jbossts.orchestration.annotation.EventHandlerClass;
import org.jboss.jbossts.orchestration.annotation.EventHandler;
import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.CompileException;
import org.objectweb.asm.Opcodes;

import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

public class TestJar
{
    public static void main(String[] args)
    {
        TestJar testJar = new TestJar();
        testJar.testJar(args);
    }

    public void testJar(String[] jarFiles)
    {
        for (String ruleJarPath : jarFiles) {
            try {
                JarFile jarFile = new JarFile(new File(ruleJarPath));
                List<Class> ruleClasses = processRules(jarFile);
                System.out.println("checking classes in " + ruleJarPath);
                checkRules(ruleClasses);
            } catch (IOException ioe) {
                System.err.println("TestJar: unable to open rule jar file : " + ruleJarPath);
            }
        }
    }



    private List<Class> processRules(JarFile jarFile)
    {
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[] { new URL("file:" + jarFile.getName()) });
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            List<Class> ruleClasses = new ArrayList<Class>();

            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String entryName = jarEntry.getName();

                if (entryName.endsWith(CLASS_FILE_SUFFIX)) {
                    // try to load the rule class
                    int classNameLength = entryName.length() - CLASS_FILE_SUFFIX.length();
                    String className = entryName.substring(0, classNameLength).replaceAll("/", ".");
                    try {
                        Class clazz = classLoader.loadClass(className);
                        Annotation a = clazz.getAnnotation(EventHandlerClass.class);
                        if (a != null) {
                            ruleClasses.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to load class " + className + " from : " + jarFile.getName());
                    }
                }
            }
            return ruleClasses;
        } catch (MalformedURLException mue) {
            System.err.println("org.jboss.jbossts.orchestration.agent.Main: unable to load classes from : " + jarFile.getName());
        }
        
        return null;
    }

    private void checkRules(List<Class> ruleClasses)
    {
        ClassLoader loader = this.getClass().getClassLoader();

        for (Class ruleClass : ruleClasses) {
            Annotation classAnnotation = ruleClass.getAnnotation(EventHandlerClass.class);
            for (Method method : ruleClass.getDeclaredMethods()) {
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                if (eventHandler != null) {
                    String targetClassName = eventHandler.targetClass();
                    String targetMethodName = eventHandler.targetMethod();
                    int targetLine = eventHandler.targetLine();
                    String ruleName = targetClassName + "::" + targetMethodName + (targetLine < 0 ? "" : "@" + targetLine);
                    try {
                        Class targetClass = loader.loadClass(targetClassName);
                        Method[] candidates = targetClass.getDeclaredMethods();
                        boolean found = false;
                        boolean multiple = false;
                        for (Method candidate : candidates) {
                            String targetName = TypeHelper.parseMethodName(targetMethodName);
                            String targetDesc = TypeHelper.parseMethodDescriptor(targetMethodName);
                            String candidateName = candidate.getName();
                            String candidateDesc = makeDescriptor(candidate);
                            if (targetName.equals(candidateName)) {
                                if (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc)) {
                                    System.err.println("TestJar: checking rule " + ruleName);
                                    if (found) {
                                        multiple = true;
                                    }
                                    found = true;
                                    int access = 0;
                                    Class<?>[] exceptionClasses = method.getExceptionTypes();
                                    int l = exceptionClasses.length;
                                    String[] exceptionNames = new String[l];
                                    for (int i = 0; i < l; i++) {
                                        exceptionNames[i] = exceptionClasses[i].getCanonicalName();
                                    }
                                    if ((candidate.getModifiers() & Modifier.STATIC) != 0) {
                                        access = Opcodes.ACC_STATIC;
                                    }
                                    String event = eventHandler.event();
                                    String condition = eventHandler.condition();
                                    String action = eventHandler.action();
                                    Rule rule = Rule.create(ruleName, targetClassName, targetMethodName, targetLine, event, condition, action, loader);
                                    System.err.println("TestJar: parsed rule " + ruleName);
                                    System.err.println(rule);
                                    rule.setTypeInfo(targetClassName, access, candidateName, candidateDesc, exceptionNames);
                                    rule.typeCheck();
                                    System.err.println("TestJar: type checked rule " + ruleName);
                                }
                            }
                        }
                        if (!found) {
                            Constructor[] constructors = targetClass.getConstructors();
                            for (Constructor constructor : constructors) {
                                String targetName = TypeHelper.parseMethodName(targetMethodName);
                                String targetDesc = TypeHelper.parseMethodDescriptor(targetMethodName);
                                String candidateName = constructor.getName();
                                String candidateDesc = makeDescriptor(constructor);
                                if (targetName.equals("<init>")) {
                                    if (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc)) {
                                        System.err.println("TestJar: checking rule " + ruleName);
                                        if (found) {
                                            multiple = true;
                                        }
                                        found = true;
                                        int access = 0;
                                        Class<?>[] exceptionClasses = constructor.getExceptionTypes();
                                        int l = exceptionClasses.length;
                                        String[] exceptionNames = new String[l];
                                        for (int i = 0; i < l; i++) {
                                            exceptionNames[i] = exceptionClasses[i].getCanonicalName();
                                        }
                                        if ((constructor.getModifiers() & Modifier.STATIC) != 0) {
                                            access = Opcodes.ACC_STATIC;
                                        }
                                        String event = eventHandler.event();
                                        String condition = eventHandler.condition();
                                        String action = eventHandler.action();
                                        Rule rule = Rule.create(ruleName, targetClassName, targetMethodName, targetLine, event, condition, action, loader);
                                        System.err.println("TestJar: parsed rule " + ruleName);
                                        System.err.println(rule);
                                        rule.setTypeInfo(targetClassName, access, candidateName, candidateDesc, exceptionNames);
                                        rule.typeCheck();
                                        System.err.println("TestJar: type checked rule " + ruleName);
                                    }
                                }
                            }
                        }
                        if (!found) {
                            System.err.println("TestJar: no matching method for rule " + ruleName);
                        } else if (multiple) {
                            System.err.println("TestJar: multiple matching methods for rule " + ruleName);
                        }
                    } catch (ParseException e) {
                        System.err.println("TestJar: parse exception for rule " + ruleName + " : " + e);
                        e.printStackTrace(System.err);
                    } catch (TypeException e) {
                        System.err.println("TestJar: type exception for rule " + ruleName + " : " + e);
                        e.printStackTrace(System.err);
                    } catch (CompileException e) {
                        System.err.println("TestJar: compile exception for rule " + " : " + ruleName + e);
                        e.printStackTrace(System.err);
                    } catch(ClassNotFoundException cfe) {
                        System.err.println("TestJar: unable to load class " + targetClassName);
                    }
                }
            }
        }
    }

    static String makeDescriptor(Method method)
    {
        Class<?> paramTypes[] = method.getParameterTypes();
        Class<?> retType = method.getReturnType();
        String desc = "(";

        for (Class<?> paramType : paramTypes) {
            String name = paramType.getCanonicalName();
            desc += TypeHelper.externalizeType(name);
        }
        desc += ")";
        desc += TypeHelper.externalizeType(retType.getCanonicalName());

        return desc;
    }

    static String makeDescriptor(Constructor constructor)
    {
        Class<?> paramTypes[] = constructor.getParameterTypes();
        String desc = "(";

        for (Class<?> paramType : paramTypes) {
            String name = paramType.getCanonicalName();
            desc += TypeHelper.externalizeType(name);
        }
        desc += ")";

        return desc;
    }

    /**
     * suffix found on end of .class files (doh :-)
     */

    private static final String CLASS_FILE_SUFFIX = ".class";
}