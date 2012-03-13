/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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

import org.jboss.byteman.rule.exception.TypeWarningException;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeGroup;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.binding.Bindings;
import org.jboss.byteman.rule.binding.Binding;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.agent.*;
import org.objectweb.asm.Opcodes;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
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
        int length = args.length;
        List<String> packages = new LinkedList<String>();
        
        int start =  0;
        boolean verbose = false;

        while (start < length) {
            if (args[start].equals("-p"))  {
                start++;
                if (start == length) {
                    usage();
                    return;
                }
                String packageName = args[start++];
                packages.add(packageName);
            } else if (args[start].equals("-v")) {
                start++;
                verbose = true;
            } else if (args[start].equals("-h")) {
                usage();
                return;
            } else {
                break;
            }
        }

        // must have some  args
        if (start == length) {
            usage();
            return;
        }


        TestScript testScript = new TestScript(verbose);
        String[] packagesArray = new String[packages.size()];
        testScript.testScript(packages.toArray(packagesArray), args, start);
    }

    public static void usage()
    {
        System.out.println("usage : java org.jboss.byteman.TestScript [-p <package>]* [-v] scriptfile1 ...");
        System.out.println("        -p specify package to lookup non-package qualified classnames");
        System.out.println("        -v display parsed rules");
        System.out.println("        n.b. place the byteman jar and classes mentioned in the ");
        System.out.println("        scripts in the classpath");
    }

    public void testScript(String[] packages, String[] files, int firstFile)
    {
        List<String> ruleTexts = new ArrayList<String>();
        List<String> ruleFiles = new ArrayList<String>();
        for (int i = firstFile; i < files.length; i++) {
            String file = files[i];
            try {
                FileInputStream fis = new FileInputStream(new File(file));
                int max = fis.available();
                int read;
                int count;
                byte[] bytes = new byte[max];
                count = fis.read(bytes);
                read = count;
                while (count > 0 && read < max) {
                    count = fis.read(bytes, read, max - read);
                }
                if (read < max) {
                    System.err.println("ERROR : unable to read full contents of file : " + file);
                    errorCount++;
                }
                String ruleText = new String(bytes);
                ruleTexts.add(ruleText);
                ruleFiles.add(file);
            } catch (IOException ioe) {
                System.err.println("ERROR : unable to open file : " + file);
                errorCount++;
            }
        }
        checkRules(packages, ruleTexts, ruleFiles);
    }



    private void checkRules(String[] packages, List<String> ruleTexts, List<String> ruleFiles)
    {
        ClassLoader loader = getClass().getClassLoader();

        ScriptRepository repository = new ScriptRepository(false);
        List<RuleScript> allScripts = new ArrayList<RuleScript>();
        Iterator<String> textsIter = ruleTexts.iterator();
        Iterator<String> filesIter = ruleFiles.iterator();

        // use a repository to process each file and provide us with a set of
        // rule scripts for checking
        
        while (textsIter.hasNext()) {
            String ruleText = textsIter.next();
            String ruleFile = filesIter.next();
            List<RuleScript> ruleScripts = null;
            try {
                ruleScripts = repository.processScripts(ruleText, ruleFile);
                allScripts.addAll(ruleScripts);
            } catch (Exception e) {
                System.out.println("ERROR : Could not process rule file " + ruleFile + " : " + e);
                errorCount++;
            }
        }

        // ok, now check each of the rules individually

        // these empty lists are used each time  we create a transformer
        List<String> emptyInitialTexts = new ArrayList<String>();
        List<String> emptyInitialFiles = new ArrayList<String>();

        for (RuleScript script : allScripts) {

            // first see if we can locate the bytecode for the class mentioned in the rule

            String targetClassName = script.getTargetClass();
            Class targetClass = null;
            try {
                targetClass = loader.loadClass(targetClassName);
            } catch (ClassNotFoundException e) {
                // hmm, maybe need to try one of the supplied packages
            }

            if (targetClass == null && targetClassName.indexOf('.') < 0) {
                for (int i = 0; i < packages.length; i++) {
                    String qualifiedName = packages[i] + "." + targetClassName;
                    try {
                        targetClass = loader.loadClass(qualifiedName);
                    } catch (ClassNotFoundException e) {
                        // hmm, need to check if it is in one of the supplied packages
                    } catch (Exception e) {
                        // eeuuurrrgghh must be a bad package name
                        System.out.println("ERROR : Unexpected error looking up " + targetClassName + " in package " + packages[i]);
                        return;
                    }
                    if (targetClass != null) {
                        break;
                    }
                }
            }

            if (targetClass == null) {
                System.out.println("ERROR : Could not load class " + targetClassName + " declared in rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                errorCount++;
                continue;
            }

            // make sure it is the right type of class
            if (script.isInterface() && !targetClass.isInterface()) {
                System.out.println("ERROR : Found class instead of interface for rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                errorCount++;
                continue;
            }

            if (!script.isInterface() && targetClass.isInterface()) {
                System.out.println("ERROR : Found interface instead of class for rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                errorCount++;
                continue;
            }

            // if this is a class rule then we can actually try the transform
            // assuming we can find the associated bytecode
            if (!script.isInterface()) {
                String resourceName = targetClass.getName().replace(".", "/") + ".class";
                byte[] bytes = null;
                try {
                    InputStream stream = loader.getResourceAsStream(resourceName);
                    int max = stream.available();
                    bytes = new byte[max];
                    int count = stream.read(bytes);
                    int read = count;
                    while (count > 0 && read < max) {
                        count = stream.read(bytes, read, max - read);
                        read += count;
                    }
                    if (read < max) {
                        System.out.println("ERROR : Could not load bytecode for class " + targetClassName + " declared in rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                        System.out.println();
                        errorCount++;
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("ERROR : Could not load bytecode for class " + targetClassName + " declared in rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                    System.out.println();
                    errorCount++;
                    continue;
                }

                // now try to transform the bytecode and see if we get any errors
                // we use a different transformer each time so the rules don't interfere with each other

                Transformer transformer = null;
                try {
                    transformer = new Transformer(null, emptyInitialTexts, emptyInitialFiles, false);
                } catch (Exception e) {
                    // will not happen!
                }

                // ok, we try transforming the actual class mentioned in the rule -- this may be an interface
                // or an abstract class so we may not get any results out of the transform

                System.out.println("checking rule " + script.getName() + " against class " + targetClass.getName());
                bytes = transformer.transform(script, loader, targetClass.getName(), bytes);
                // maybe dump the transformed bytecode
                Transformer.maybeDumpClass(targetClass.getName(), bytes);
            }

            // see if we have a record of any transform
            if (script.hasTransform(targetClass)) {
                List<Transform> transforms = script.getTransformed();
                int numTransforms = transforms.size();
                for (Transform transform : transforms) {
                    Throwable throwable = transform.getThrowable();
                    Rule rule = transform.getRule();
                    String methodName = transform.getTriggerMethodName();

                    if (throwable != null) {
                        if (throwable  instanceof ParseException) {
                            System.out.println("ERROR : Failed to parse rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                            parseErrorCount++;
                            errorCount++;
                        } else if (throwable instanceof TypeWarningException) {
                            System.out.println("WARNING : Warning type checking rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine() + (methodName == null ? "" : " against method " + methodName));
                            typeWarningCount++;
                            warningCount++;
                        } else if (throwable instanceof TypeException) {
                            System.out.println("ERROR : Failed to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine() + (methodName == null ? "" : " against method " + methodName));
                            typeErrorCount++;
                            errorCount++;
                        } else {
                            System.out.println("ERROR : Unexpected exception transforming class " + targetClassName + " using  rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine() + (methodName == null ? "" : " against method " + methodName));
                            errorCount++;
                        }
                        System.out.println(throwable);
                        System.out.println();
                        continue;
                    }

                    System.out.println("parsed rule \"" + script.getName() + "\" for class " + transform.getInternalClassName());

                    if (verbose) {
                        System.out.println("# File " + script.getFile() + " line " + script.getLine());
                        System.out.println(rule);
                    }

                    // ok, now see if we can type check the rule

                    try {
                        rule.typeCheck();
                    } catch (TypeWarningException te) {
                        System.out.println("WARNING : Unable to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine() + (methodName == null ? "" : " against method " + methodName));
                        typeWarningCount++;
                        warningCount++;
                        System.out.println(te);
                        System.out.println();
                        continue;
                    } catch (TypeException te) {
                        System.out.println("ERROR : Failed to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine() + (methodName == null ? "" : " against method " + methodName));
                        typeErrorCount++;
                        errorCount++;
                        System.out.println(te);
                        System.out.println();
                        continue;
                    }

                    if (script.isOverride()) {
                        System.out.println("type checked overriding rule \"" + script.getName() + "\" against method in declared class");
                    } else {
                        System.out.println("type checked rule \"" + script.getName() + "\"");
                    }
                    System.out.println();
                }
            } else if (targetClass.isInterface() || script.isOverride()) {
                // ok, not necessarily a surprise - let's see if we can create a rule and parse/type check it
                final Rule rule;
                try {
                    rule = Rule.create(script, loader, null);
                } catch (ParseException pe) {
                    System.out.println("ERROR : Failed to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                    parseErrorCount++;
                    errorCount++;
                    System.out.println(pe);
                    System.out.println();
                    continue;
                } catch (TypeWarningException te) {
                    System.out.println("WARNING : Unable to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                    typeWarningCount++;
                    warningCount++;
                    System.out.println(te);
                    System.out.println();
                    continue;
                } catch (TypeException te) {
                    System.out.println("ERROR : Failed to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                    typeErrorCount++;
                    errorCount++;
                    System.out.println(te);
                    System.out.println();
                    continue;
                } catch (Throwable th) {
                    System.out.println("ERROR : Failed to process rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                    errorCount++;
                    System.out.println(th);
                    System.out.println();
                    continue;
                }

                System.out.println("parsed rule \"" + script.getName() + "\"");
                if (verbose) {
                    System.out.println("# File " + script.getFile() + " line " + script.getLine());
                    System.out.println(rule);
                }
                // ok, we need to see if we can generate the required type info to drive the type check process

                typeCheckAgainstMethodDeclaration(rule, script, targetClass, loader);
            } else {
                System.out.println("WARNING : Unable to transform class " + targetClassName + " using rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                System.out.println();
                warningCount++;
            }
        }
        
        if (errorCount != 0) {
            System.err.println("TestScript: " + errorCount + " total errors");
            System.err.println("            " + warningCount + " total warnings");
            System.err.println("            " + parseErrorCount + " parse errors");
            System.err.println("            " + typeErrorCount + " type errors");
            System.err.println("            " + typeWarningCount + " type warnings");

        } else if (warningCount != 0) {
            System.err.println("TestScript: " + warningCount + " total warnings");
            System.err.println("            " + typeWarningCount + " type warnings");
        } else {
            System.err.println("TestScript: no errors");
        }
    }

    /**
     * method called to deal with interface rules or with overriding rules which fail to match a method of the
     * declared class.
     * @param rule
     * @param script
     * @param targetClass
     * @param loader
     * @return
     */
    private void typeCheckAgainstMethodDeclaration(Rule rule, RuleScript script, Class targetClass, ClassLoader loader)
    {
        // ok, we have a rule which cannot be used to transform its declared class, either because
        // it applies to an interface or it can inject into overriding methods but does not
        // apply to the parent method. so we need to find a candidate method for the rule and
        // then see if we can use it to set up the type info needed to type check the rule

        String targetMethodName =  script.getTargetMethod();
        String targetName = TypeHelper.parseMethodName(targetMethodName);
        String targetDesc = TypeHelper.parseMethodDescriptor(targetMethodName);

        if (targetName == "<clinit>") {
            System.err.println("WARNING : cannot type check <clinit> rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
            System.err.println();
            warningCount++;
            return;
        }
        if (targetMethodName == "<init>") {
            // oops this is an error one way or another. firstly constructor rules don't make sense for either
            if (script.isInterface()) {
                System.err.println("ERROR : invalid target method <init> for interface rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                System.err.println();
                errorCount++;
                return;
            } else {
                System.err.println("ERROR : invalid target method <init> for overriding rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                System.err.println();
                errorCount++;
                return;
            }
        } else {
            // ok, search the class's methods for any method which matches
            Method[] candidates = targetClass.getDeclaredMethods();
            int matchCount = 0;
            for (Method candidate : candidates) {
                String candidateName = candidate.getName();
                String candidateDesc = makeDescriptor(candidate);
                if (targetName.equals(candidateName) &&
                        (targetDesc.equals("") || TypeHelper.equalDescriptors(targetDesc, candidateDesc))) {
                    matchCount++;
                    if (matchCount > 1) {
                        // we need a new copy of the rule

                        try {
                            rule = Rule.create(script, loader, null);
                        } catch (ParseException e) {
                            // will not happen
                        } catch (TypeException e) {
                            // will not happen
                        } catch (CompileException e) {
                            // will not happen
                        }
                    }
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

                    rule.setTypeInfo(targetClass.getName(), access, candidateName, candidateDesc, exceptionNames);
                    // we can set param types this way but we cannot verify mention of local variables
                    // since we don't have an implementation

                    int paramErrorCount = installParamTypes(rule, targetClass.getName(), access, candidateName, candidateDesc);
                    if (paramErrorCount == 0) {
                        try {
                            rule.typeCheck();
                        } catch (TypeWarningException te) {
                            System.out.println("WARNING : Unable to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                            typeWarningCount++;
                            warningCount++;
                            System.out.println(te);
                            System.out.println();
                            return;
                        } catch (TypeException te) {
                            System.out.println("ERROR : Failed to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                            typeErrorCount++;
                            errorCount++;
                            System.out.println(te);
                            System.out.println();
                            return;
                        }
                        if (script.isInterface()) {
                            System.err.println("type checked interface rule \"" + script.getName() + "\" against method declaration");
                        } else {
                            System.err.println("type checked overriding rule \"" + script.getName() + "\" against method declaration");
                        }
                        System.err.println();
                    } else {
                        errorCount += paramErrorCount;
                        typeErrorCount += paramErrorCount;
                        System.out.println("ERROR : Failed to type check rule \"" + script.getName() + "\" loaded from " + script.getFile() + " line " + script.getLine());
                        System.err.println();
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

    public int installParamTypes(Rule rule, String targetClassName, int access, String candidateName, String candidateDesc)
    {
        List<String> paramTypes = Type.parseMethodDescriptor(candidateDesc, true);
        int paramCount = paramTypes.size() - 1;
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
                        System.err.println("ERROR : Invalid method parameter reference $" + idx  + " in rule \"" + rule.getName() + "\"");
                    } else {
                        binding.setDescriptor(paramTypes.get(idx - 1));
                    }
                } else if (binding.isReturn()) {
                    if (rule.getTargetLocation().getLocationType() != LocationType.INVOKE_COMPLETED) {
                        // return type is on end of list
                        String returnType = paramTypes.get(paramCount);
                        if ("void".equals(returnType)) {
                            errorCount++;
                            System.err.println("ERROR : Invalid return value reference $! in rule \"" + rule.getName() + "\"");
                        } else {
                            binding.setDescriptor(returnType);
                        }
                    } else {
                        warningCount++;
                        System.err.println("WARNING : cannot infer type for $! in AFTER INVOKE rule \"" + rule.getName() + "\"");
                        binding.setDescriptor("void");
                    }
                } else if (binding.isLocalVar()) {
                    warningCount++;
                    System.err.println("WARNING : Cannot typecheck local variable " + binding.getName()  + " in rule \"" + rule.getName() + "\"");
                    binding.setDescriptor("void");
                }
            }
        }

        return errorCount;
    }

    private TestScript(boolean verbose)
    {
        errorCount = 0;
        warningCount = 0;
        parseErrorCount = 0;
        typeErrorCount =  0;
        typeWarningCount =  0;
        this.verbose = verbose;
    }

    private int errorCount;
    private int warningCount;
    private int parseErrorCount;
    private int typeErrorCount;
    private int typeWarningCount;
    boolean verbose;
}