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
import java.io.FileInputStream;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

public class TestScript
{
    public static void main(String[] args)
    {
        TestScript testScript = new TestScript();
        testScript.testScript(args);
    }

    public void testScript(String[] scriptFiles)
    {
        for (String script : scriptFiles) {
            try {
                FileInputStream fis = new FileInputStream(new File(script));
                System.out.println("checking classes in " + script);
                List<String> rules = processRules(fis);
                checkRules(rules);
            } catch (IOException ioe) {
                System.err.println("TestScript: unable to open rule script file : " + script);
            }
        }
    }



    private List<String> processRules(FileInputStream stream)
            throws IOException
    {
        List<String> rules = new ArrayList<String>();

        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        String text = new String(bytes);
        int length = text.length();
        while (length > 0) {
            int end = text.indexOf("ENDRULE");
            if (end >= 0) {
                end += "ENDRULE".length();
                if (end < length && text.charAt(end) == '\n') {
                    end++;
                }
                rules.add(text.substring(0, end));
                text = text.substring(end).trim();
            } else {
                rules.add(text);
                text = "";
            }
            length = text.length();
        }

        return rules;
    }

    private void checkRules(List<String> ruleScripts)
    {
        ClassLoader loader = getClass().getClassLoader();
        
        for (String script : ruleScripts) {
            String ruleName = "";
            try {
                String[] lines = script.split("\n");
                String targetClassName;
                String targetMethodName;
                int targetLine = -1;
                String text = "";
                String sepr = "";
                int idx = 0;
                int len = lines.length;

                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                }
                if (lines[idx].startsWith("RULE ")) {
                    ruleName = lines[idx].substring(5).trim();
                    idx++;
                } else {
                    throw new ParseException("Rule should start with RULE : " + lines[idx]);
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                }
                if (lines[idx].startsWith("CLASS ")) {
                    targetClassName = lines[idx].substring(6).trim();
                    idx++;
                } else {
                    throw new ParseException("CLASS should follow RULE : " + lines[idx]) ;
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                }
                if (lines[idx].startsWith("METHOD ")) {
                    targetMethodName = lines[idx].substring(7).trim();
                    idx++;
                } else {
                    throw new ParseException("METHOD should follow CLASS : " + lines[idx]) ;
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                }
                if (lines[idx].startsWith("LINE ")) {
                    String targetLineString = lines[idx].substring(5).trim();
                    targetLine = Integer.valueOf(targetLineString);
                    idx++;
                }
                for (;idx < len; idx++) {
                    if (lines[idx].trim().startsWith("#")) {
                        lines[idx] = "";
                    }
                    if (lines[idx].trim().equals("ENDRULE")) {
                        break;
                    }
                    text += sepr + lines[idx];
                    sepr = "\n";
                }
                if (targetMethodName.startsWith("<init>") && (targetLine < 0)) {
                    throw new ParseException("constructor method " + targetMethodName + " must specify target line in rule " + ruleName);
                }
                Rule rule = Rule.create(ruleName, targetClassName, targetMethodName, targetLine, text, loader);
                System.err.println("TestScript: parsed rule " + rule.getName());
                System.err.println(rule);
                
                String targetName = TypeHelper.parseMethodName(targetMethodName);
                String targetDesc = TypeHelper.parseMethodDescriptor(targetMethodName);
                boolean found = false;
                boolean multiple = false;
                try {
                    Class targetClass = loader.loadClass(targetClassName);
                    if (!targetName.equals("<init>")) {
                        Method[] candidates = targetClass.getDeclaredMethods();
                        for (Method candidate : candidates) {
                            String candidateName = candidate.getName();
                            String candidateDesc = makeDescriptor(candidate);
                            if (targetName.equals(candidateName)) {
                                if (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc)) {
                                    System.err.println("TestJar: checking rule " + ruleName);
                                    if (found) {
                                        multiple = true;
                                        break;
                                    }
                                    found = true;
                                    int access = 0;
                                    if ((candidate.getModifiers() & Modifier.STATIC) != 0) {
                                        access = Opcodes.ACC_STATIC;
                                    }
                                    rule.setTypeInfo(targetClassName, access, candidateName, candidateDesc);
                                    rule.typeCheck();
                                    System.err.println("TestJar: type checked rule " + ruleName);
                                }
                            }
                        }
                    } else {
                        Constructor[] constructors = targetClass.getConstructors();
                        for (Constructor constructor : constructors) {
                            String candidateName = constructor.getName();
                            String candidateDesc = makeDescriptor(constructor);
                            if (targetName.equals("<init>")) {
                                if (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc)) {
                                    System.err.println("TestJar: checking rule " + ruleName);
                                    if (found) {
                                        multiple = true;
                                        break;
                                    }
                                    found = true;
                                    int access = 0;
                                    if ((constructor.getModifiers() & Modifier.STATIC) != 0) {
                                        access = Opcodes.ACC_STATIC;
                                    }
                                    rule.setTypeInfo(targetClassName, access, candidateName, candidateDesc);
                                    rule.typeCheck();
                                    System.err.println("TestJar: type checked rule " + ruleName);
                                }
                            }
                        }
                    }
                } catch(ClassNotFoundException cfe) {
                    System.err.println("TestScript: unable to load class " + targetClassName);
                }
                if (!found) {
                    System.err.println("TestJar: no matching method for rule " + ruleName);
                } else if (multiple) {
                    System.err.println("TestJar: multiple matching methods for rule " + ruleName);
                }
            } catch (ParseException e) {
                System.err.println("TestScript: parse exception for rule " + ruleName + " : " + e);
                e.printStackTrace(System.err);
            } catch (TypeException e) {
                System.err.println("TestScript: type exception for rule " + ruleName + " : " + e);
                e.printStackTrace(System.err);
            } catch (CompileException e) {
                System.err.println("TestScript: compile exception for rule " + " : " + ruleName + e);
                e.printStackTrace(System.err);
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