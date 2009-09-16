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
package org.jboss.byteman.test;

import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.agent.LocationType;
import org.jboss.byteman.agent.Location;
import org.jboss.byteman.agent.RuleScript;
import org.jboss.byteman.agent.adapter.RuleCheckAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

/**
 * utility which parses and typechecks all rules in a rule script.
 *
 * usage : java org.jboss.byteman.TestScript [scriptfile]
 *
 * n.b. the byteman jar and any classes mentioned in the script rules need to be in the classpath
 */
public class TestScript
{
    public static void main(String[] args)
    {
        if (args.length == 0 || args[0].equals("-h")) {
            System.out.println("usage : java org.jboss.byteman.TestScript [scriptfile1 ...]");
            System.out.println("        n.b. place the byteman jar and classes mentioned in the ");
            System.out.println("        scripts in the classpath");
            return;
        }
        TestScript testScript = new TestScript();
        testScript.testScript(args);
    }

    public void testScript(String[] scriptFiles)
    {
        for (String script : scriptFiles) {
            try {
                FileInputStream fis = new FileInputStream(new File(script));
                System.out.println("checking rules in " + script);
                List<String> rules = processRules(fis);
                checkRules(rules, script);
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
        String[] lines = text.split("\n");
        int length = lines.length;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(lines[i]);
            buffer.append("\n");
            if (lines[i].trim().equals("ENDRULE")) {
                rules.add(buffer.toString());
                buffer = new StringBuffer();
            }
        }
        if (buffer.length() > 0) {
            rules.add(buffer.toString());
        }

        return rules;
    }

    private void checkRules(List<String> ruleScripts, String file)
    {
        ClassLoader loader = getClass().getClassLoader();
        int errorCount = 0;
        int parseErrorCount = 0;
        int typeErrorCount = 0;
        int compileErrorCount = 0;
        int baseline = 1;

        for (String script : ruleScripts) {
            String ruleName = "";
            String[] lines = script.split("\n");
            int len = lines.length;
            try {
                String targetClassName;
                String targetMethodName;
                String targetHelperName = null;
                LocationType locationType = null;
                Location targetLocation = null;
                String text = "";
                String sepr = "";
                int idx = 0;
                int lineNumber = 0;

                while (idx < len && (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#"))) {
                    idx++;
                }
                if (idx == len) {
                    // empty rule -- just skip
                    baseline += len;
                    continue;
                }
                if (lines[idx].startsWith("RULE ")) {
                    ruleName = lines[idx].substring(5).trim();
                    idx++;
                } else {
                    throw new ParseException("Rule should start with RULE :\n" + lines[idx]);
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule does not specify CLASS :\n" + script);
                    }
                }
                if (lines[idx].startsWith("CLASS ")) {
                    targetClassName = lines[idx].substring(6).trim();
                    idx++;
                } else {
                    throw new ParseException("CLASS should follow RULE :\n" + lines[idx]) ;
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule does not specify METHOD\n: " + script);
                    }
                }
                if (lines[idx].startsWith("METHOD ")) {
                    targetMethodName = lines[idx].substring(7).trim();
                    idx++;
                } else {
                    throw new ParseException("METHOD should follow CLASS :\n" + lines[idx]) ;
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule is incomplete :\n" + script);
                    }
                }
                if (lines[idx].startsWith("HELPER ")) {
                    targetHelperName = lines[idx].substring(7).trim();
                    idx++;
                    while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                        idx++;
                        if (idx == len) {
                            throw new ParseException("Rule is incomplete :\n" + script);
                        }
                    }
                }
                locationType = LocationType.type(lines[idx]);
                if (locationType != null) {
                    String parameters = LocationType.parameterText(lines[idx]);
                    targetLocation = Location.create(locationType, parameters);
                    if (targetLocation == null) {
                        throw new ParseException("Invalid parameters for location specifier\n" + locationType.specifierText() + " in rule " + ruleName);
                    }
                    idx++;
                }
                lineNumber = idx;
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
                if (idx == len) {
                    throw new ParseException("Missing ENDRULE :\n" + script);
                }

                Class targetHelperClass = null;
                if (targetHelperName != null) {
                    try {
                        targetHelperClass = loader.loadClass(targetHelperName);
                    } catch (ClassNotFoundException e) {
                        System.out.println("TestScript : unknown helper class " + targetHelperName + " for rule " + ruleName);
                    }
                }
                RuleScript ruleScript = new RuleScript(ruleName, targetClassName, targetMethodName, targetHelperName, targetLocation, text, baseline + lineNumber, file);
                Rule rule = Rule.create(ruleScript, targetHelperClass, loader);
                System.err.println("TestScript: parsed rule " + rule.getName());
                System.err.println(rule);
                
                String targetName = TypeHelper.parseMethodName(targetMethodName);
                String targetDesc = TypeHelper.parseMethodDescriptor(targetMethodName);
                boolean found = false;
                boolean multiple = false;
                try {
                    Class targetClass = loader.loadClass(targetClassName);
                    if (targetName.equals("<clinit>")) {
                        System.err.println("TestScript: cannot type check <clinit> rule " + ruleName);
                    } else if (targetName.equals("<init>")) {
                        Constructor[] constructors = targetClass.getConstructors();
                        for (Constructor constructor : constructors) {
                            String candidateName = constructor.getName();
                            String candidateDesc = makeDescriptor(constructor);
                            if (targetName.equals("<init>")) {
                                if (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc)) {
                                    System.err.println("TestScript: checking rule " + ruleName);
                                    if (found) {
                                        multiple = true;
                                        break;
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
                                    rule.setTypeInfo(targetClassName, access, candidateName, candidateDesc, exceptionNames);
                                    // the param and local var types are normally set by the check adapter but we
                                    // cannot run that without accessing the byte[] version of the class so we have
                                    // to install the param types by hand and we cannot check local var types
                                    int paramErrorCount = installParamTypes(rule, targetClassName, access, candidateName, candidateDesc);
                                    if (paramErrorCount == 0) {
                                        rule.typeCheck();
                                        System.err.println("TestScript: type checked rule " + ruleName);
                                        System.err.println();
                                    } else {
                                        errorCount += paramErrorCount;
                                        typeErrorCount += paramErrorCount;
                                        System.err.println("TestScript: failed to type check rule " + ruleName);
                                        System.err.println();
                                    }
                                }
                            }
                        }
                    } else {
                        Method[] candidates = targetClass.getDeclaredMethods();
                        for (Method candidate : candidates) {
                            String candidateName = candidate.getName();
                            String candidateDesc = makeDescriptor(candidate);
                            if (targetName.equals(candidateName)) {
                                if (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc)) {
                                    System.err.println("TestScript: checking rule " + ruleName);
                                    if (found) {
                                        multiple = true;
                                        break;
                                    }
                                    found = true;
                                    int access = 0;
                                    Class<?>[] exceptionClasses = candidate.getExceptionTypes();
                                    int l = exceptionClasses.length;
                                    String[] exceptionNames = new String[l];
                                    for (int i = 0; i < l; i++) {
                                        exceptionNames[i] = exceptionClasses[i].getCanonicalName();
                                    }
                                    if ((candidate.getModifiers() & Modifier.STATIC) != 0) {
                                        access = Opcodes.ACC_STATIC;
                                    }
                                    rule.setTypeInfo(targetClassName, access, candidateName, candidateDesc, exceptionNames);
                                    // the param and local var types are normally set by the check adapter but we
                                    // cannot run that without accessing the byte[] version of the class so we have
                                    // to install the param types by hand and we cannot check local var types
                                    int paramErrorCount = installParamTypes(rule, targetClassName, access, candidateName, candidateDesc);
                                    if (paramErrorCount == 0) {
                                        rule.typeCheck();
                                        System.err.println("TestScript: type checked rule " + ruleName);
                                        System.err.println();
                                    } else {
                                        errorCount += paramErrorCount;
                                        typeErrorCount += paramErrorCount;
                                        System.err.println("TestScript: failed to type check rule " + ruleName);
                                        System.err.println();
                                    }
                                }
                            }
                        }
                    }
                } catch(ClassNotFoundException cfe) {
                    errorCount++;
                    System.err.println("TestScript: unable to load class " + targetClassName);
                    System.err.println();
                }
                if (!found) {
                    errorCount++;
                    System.err.println("TestScript: no matching method for rule " + ruleName);
                    System.err.println();
                } else if (multiple) {
                    errorCount++;
                    System.err.println("TestScript: multiple matching methods for rule " + ruleName);
                    System.err.println();
                }
            } catch (ParseException e) {
                errorCount++;
                parseErrorCount++;
                System.err.println("TestScript: failed to parse rule " + ruleName);
                e.printStackTrace(System.err);
                System.err.println();
            } catch (TypeException e) {
                typeErrorCount++;
                errorCount++;
                e.printStackTrace(System.err);
                System.err.println();
            } catch (CompileException e) {
                compileErrorCount++;
                errorCount++;
                e.printStackTrace(System.err);
                System.err.println();
            }

            baseline += len;
        }
        if (errorCount != 0) {
            System.err.println("TestScript: " + errorCount + " total errors");
            System.err.println("            " + parseErrorCount + " parse errors");
            System.err.println("            " + typeErrorCount + " type errors");

        } else {
            System.err.println("TestScript: no errors");
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

    public int installParamTypes(Rule rule, String targetClassName, int access, String candidateName, String candidateDesc)
    {
        List<String> paramTypes = Type.parseMethodDescriptor(candidateDesc, false);
        int paramCount = paramTypes.size();
        int errorCount = 0;

        TypeGroup typegroup = rule.getTypeGroup();

        Bindings bindings = rule.getBindings();
        Iterator<Binding> iterator = bindings.iterator();

        while (iterator.hasNext()) {
            Binding binding = iterator.next();

            if (binding.getType() == Type.UNDEFINED) {
                if (binding.isRecipient()) {
                    binding.setDescriptor(targetClassName);
                } else if (binding.isParam()) {
                    int idx = binding.getIndex();
                    // n.b. param indices are 1-based so use > here not >=
                    if (idx > paramCount) {
                        errorCount++;
                        System.err.println("TestScript: invalid method parameter reference $" + idx  + " in rule " + rule.getName());
                    } else {
                        binding.setDescriptor(paramTypes.get(idx - 1));
                    }
                } else if (binding.isLocalVar()) {
                    errorCount++;
                    System.err.println("TestScript: cannot typecheck local variable " + binding.getName()  + " in rule " + rule.getName());
                }
            }
        }

        return errorCount;
    }

    /**
     * suffix found on end of .class files (doh :-)
     */

    private static final String CLASS_FILE_SUFFIX = ".class";
}