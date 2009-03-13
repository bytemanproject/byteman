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
package org.jboss.jbossts.orchestration.test;

import org.jboss.jbossts.orchestration.rule.type.TypeHelper;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.jboss.jbossts.orchestration.rule.exception.ParseException;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.CompileException;
import org.jboss.jbossts.orchestration.agent.LocationType;
import org.jboss.jbossts.orchestration.agent.Location;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;

/**
 * utility which parses and typechecks all rules in a rule script.
 *
 * usage : java org.jboss.jbossts.orchestration.TestScript [scriptfile]
 *
 * n.b. the orchestration jar and any classes mentioned in the script rules need to be in the classpath
 */
public class TestScript
{
    public static void main(String[] args)
    {
        if (args.length == 0 || args[0].equals("-h")) {
            System.out.println("usage : java org.jboss.jbossts.orchestration.TestScript [scriptfile1 ...]");
            System.out.println("        n.b. place the orchestration jar and classes mentioned in the ");
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

    private void checkRules(List<String> ruleScripts)
    {
        ClassLoader loader = getClass().getClassLoader();
        int errorCount = 0;
        int parseErrorCount = 0;
        int typeErrorCount = 0;
        int compileErrorCount = 0;

        for (String script : ruleScripts) {
            String ruleName = "";
            try {
                String[] lines = script.split("\n");
                String targetClassName;
                String targetMethodName;
                String targetHelperName = null;
                LocationType locationType = null;
                Location targetLocation = null;
                String text = "";
                String sepr = "";
                int idx = 0;
                int len = lines.length;

                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule contains no text : " + script);
                    }
                }
                if (lines[idx].startsWith("RULE ")) {
                    ruleName = lines[idx].substring(5).trim();
                    idx++;
                } else {
                    throw new ParseException("Rule should start with RULE : " + lines[idx]);
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule does not specify CLASS : " + script);
                    }
                }
                if (lines[idx].startsWith("CLASS ")) {
                    targetClassName = lines[idx].substring(6).trim();
                    idx++;
                } else {
                    throw new ParseException("CLASS should follow RULE : " + lines[idx]) ;
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule does not specify METHOD : " + script);
                    }
                }
                if (lines[idx].startsWith("METHOD ")) {
                    targetMethodName = lines[idx].substring(7).trim();
                    idx++;
                } else {
                    throw new ParseException("METHOD should follow CLASS : " + lines[idx]) ;
                }
                while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                    idx++;
                    if (idx == len) {
                        throw new ParseException("Rule is incomplete : " + script);
                    }
                }
                if (lines[idx].startsWith("HELPER ")) {
                    targetHelperName = lines[idx].substring(7).trim();
                    idx++;
                    while (lines[idx].trim().equals("") || lines[idx].trim().startsWith("#")) {
                        idx++;
                        if (idx == len) {
                            throw new ParseException("Rule is incomplete : " + script);
                        }
                    }
                }
                locationType = LocationType.type(lines[idx]);
                if (locationType != null) {
                    String parameters = LocationType.parameterText(lines[idx]);
                    targetLocation = Location.create(locationType, parameters);
                    if (targetLocation == null) {
                        throw new ParseException("Invalid parameters for location specifier " + locationType.specifierText() + " in rule " + ruleName);
                    }
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
                if (idx == len) {
                    throw new ParseException("Missing ENDRULE : " + script);
                }

                Class targetHelperClass = null;
                if (targetHelperName != null) {
                    try {
                        targetHelperClass = loader.loadClass(targetHelperName);
                    } catch (ClassNotFoundException e) {
                        System.out.println("org.jboss.jbossts.orchestration.agent.Transformer : unknown helper class " + targetHelperName + " for rule " + ruleName);
                    }
                }
                Rule rule = Rule.create(ruleName, targetClassName, targetMethodName, targetHelperClass, targetLocation, text, loader);
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
                                    rule.typeCheck();
                                    System.err.println("TestScript: type checked rule " + ruleName);
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
                                    rule.typeCheck();
                                    System.err.println("TestScript: type checked rule " + ruleName);
                                }
                            }
                        }
                    }
                } catch(ClassNotFoundException cfe) {
                    errorCount++;
                    System.err.println("TestScript: unable to load class " + targetClassName);
                }
                if (!found) {
                    errorCount++;
                    System.err.println("TestScript: no matching method for rule " + ruleName);
                } else if (multiple) {
                    errorCount++;
                    System.err.println("TestScript: multiple matching methods for rule " + ruleName);
                }
            } catch (ParseException e) {
                errorCount++;
                parseErrorCount++;
                System.err.println("TestScript: parse exception for rule " + ruleName + " : " + e);
                e.printStackTrace(System.err);
            } catch (TypeException e) {
                typeErrorCount++;
                errorCount++;
                System.err.println("TestScript: type exception for rule " + ruleName + " : " + e);
                e.printStackTrace(System.err);
            } catch (CompileException e) {
                compileErrorCount++;
                errorCount++;
                System.err.println("TestScript: createHelperAdapter exception for rule " + " : " + ruleName + e);
                e.printStackTrace(System.err);
            }
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

    /**
     * suffix found on end of .class files (doh :-)
     */

    private static final String CLASS_FILE_SUFFIX = ".class";
}