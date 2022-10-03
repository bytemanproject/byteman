/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * details of a single rule obtained from a rule file. RuleScript instances are stored in the script repository
 * attached to the transformer. They are used to generate Rule instances at transform time. The RuleScript contains
 * a list of Transforms which detail failed or successful transforms performed using the script.
 */

public class RuleScript
{
    /**
     * a counter used to ensure rule identifiers are unique
     */
    private static int nextId = 0;

    /**
     * a method to return the next available counter for use in constructing a key for a rule
     * @return the next id
     */
    private synchronized static int nextId()
    {
        return nextId++;
    }

    /**
     * the name of the rule from which this script is derived
     */
    private String name;
    /**
     * the name supplied in the CLASS or INTERFACE clause of the rule identifying which class(es)
     * triggers should be injected into
     */
    private String targetClass;
    /**
     * true if the target is an interface or false if the target is a class, in the former case the
     * rule should be injected into methods of classes which implement the interface.
     */
    private boolean isInterface;
    /**
     * the name of the method of the target class or interface into which the rule should be injected
     */
    private String targetMethod;
    /**
     * true if the rule should be injected into overriding implementations of the target method false
     * if it should only be injected into the implementation defined by the target class or, in the
     * case of an interface rule, by the class directly implementing the target interface
     */
    private boolean isOverride;
    /**
     * the name of a class whose public instance methods define the built-in methods available for use
     * in the rule body
     */
    private String targetHelper;
    /**
     * the details of the IMPORT lines
     */
    private String[] imports;
    /**
     * identifies the location in the method if the trigger point at which the rule code should be injected.
     * note that for an AT EXIT rule there may be multiple trigger points.
     */
    private Location targetLocation;
    /**
     * the text of the rule's BIND IF and DO clauses which are parsed using a grammar based parser
     */
    private String ruleText;
    /**
     * this is set to true if the rule is dynamically deleted or updated so as to inhibit execution of
     * trigger code between the delete/update and recompilation/reinstatement of the affected bytecode.
     */
    private boolean deleted;
    /**
     * the line number at which the rule text starts
     */
    private int line;
    /**
     * the name of the file from which the rule has been loaded, if defined, or some suitable dummy string if it
     * was noti obtained from a file
     */
    private String file;
    /**
     * true if this rule should be compiled to bytecode otherwise false
     */
    private final boolean compileToBytecode;
    /**
     * true if this rule should be type checked using the target type for this rather
     * than the trigger type
     */
    private final boolean asTarget;
    /**
     * hash map used to lookup a key used at injection time to identify a
     * rule cloned from this script for injection into a specific trigger
     * method. the map translates a string constructed from the trigger class
     * name, method name, method descriptor and class loader hash to a unique
     * key based on the rule name. This ensures that concurrent attempts to inject
     * the rule into the same trigger method will employ the same key and hence
     * perform exactly the same transformation. That way it does not matter which
     * of the transformations are accepted or dropped by the JVM when defining a
     * newly loaded class. Any transform result for a given key is as valid as
     * any other.
     */
    private final HashMap<String, String> keySet;
    /**
     * base string from which to construct rule injection keys
     */
    private final String key_base;

    /**
     * a list of records identifying transforms associated with a specific class.
     * each set is identified by the name of a trigger class and the class's
     * associated loader i.e. it corresponds with an attempt to transform a unique
     * class using this rule script.
     *
     * A transform set may contain more than one transform because the rule's
     * METHOD clause may omit a descriptor, leading to injection into multiple
     * methods. Each transform references the specific method it applies to with
     * a name and and descriptor string. Also, not all transforms record successful
     * injections. Entries are added to record parse errors or warnings, including
     * failure to inject a rule at all. There is at most one successful Transform
     * for a given class+method, at most one failure or, possibly, one or more
     * warnings.
     */
    private List<TransformSet> transformSets;

    /**
     * standard constructor for a rule
     * @param name the name of the rule
     * @param targetClass the name of the class or interface to which the rule applies
     * @param isInterface true if the ruel applies to an interface false if it appies ot a class
     * @param isOverride true if the rule should inject down class hierarchies false if it should inly inject into direct implementations
     * @param targetMethod the name of the method to which the rule applies
     * @param targetHelper the name of the helper class to be used
     * @param imports the list of imports for the module system
     * @param targetLocation description of where the rule should be injected
     * @param ruleText the body of the rule as text including the BIND, IF and DO clasue
     * @param line the line at which the rule starts in it's rule script
     * @param file the path to the file containing the rule
     * @param compileToBytecode true if the rule should be compiled otherwise false
     * @param asTarget true if the rule should be typed using the target type for this rather than the trigger type
     */
    public RuleScript(String name, String targetClass, boolean isInterface, boolean isOverride, String targetMethod, String targetHelper, String[] imports, Location targetLocation, String ruleText, int line, String file, boolean compileToBytecode, boolean asTarget)
    {
        this.name = name;
        this.targetClass = targetClass;
        this.isInterface =  isInterface;
        this.isOverride = isOverride;
        this.targetMethod = targetMethod;
        this.targetHelper = targetHelper;
        this.imports = imports;
        this.targetLocation = (targetLocation != null ? targetLocation : Location.create(LocationType.ENTRY, ""));
        this.ruleText = ruleText;
        this.line = line;
        this.file = file;
        this.compileToBytecode = compileToBytecode;
        this.asTarget = asTarget;
        this.transformSets = new ArrayList<TransformSet>();
        this.keySet = new HashMap<String, String>();
        this.key_base = name + "_" + nextId();
    }

    public String getName() {
        return name;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public String getTargetHelper() {
        return targetHelper;
    }

    public String[] getImports() {
        return imports;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public boolean isOverride() {
        return isOverride;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public String getRuleText() {
        return ruleText;
    }

    public int getLine()
    {
        return line;
    }

    public String getFile()
    {
        return file;
    }

    public boolean isCompileToBytecode() { return compileToBytecode; }

    public boolean isAsTarget() { return asTarget; }

    public synchronized String getRuleKey(String triggerClassName, String triggerMethodName, String triggerMethodDescriptor, ClassLoader loader) {
        if (triggerMethodName == null) {
            // this can happen when we get errors ???
            return key_base;
        }
        String lookup =  triggerClassName + "." + triggerMethodName + TypeHelper.internalizeDescriptor(triggerMethodDescriptor) + "_" + loader.hashCode();
        String result = keySet.get(lookup);
        if (result == null) {
            result = key_base + ":" + keySet.size();
            keySet.put(lookup, result);
        }
        return result;
    }

    /**
     * getter for list of transforms applied for this script. must be called synchronized on the script.
     * @return the list of transforms
     */
    public List<TransformSet> getTransformSets()
    {
        return transformSets;
    }

    /**
     * return a count of the number of transforms applied for this script. must be called synchronized on the script.
     * @return the size of the list of transforms
     */
    public int getTransformSetsCount()
    {
        return (transformSets != null ? transformSets.size() : 0);
    }

    public List<Transform> allTransforms()
    {
        List<Transform> allTransforms = new ArrayList<Transform>();
        for (TransformSet transformSet : transformSets) {
            allTransforms.addAll(transformSet.getTransforms());
        }
        return allTransforms;
    }
    
    /**
     * invoked by the scriptmanager when a rule is redefined to inhibit further transformations via this script
     * @return the previous setting of deleted
     */
    public synchronized boolean setDeleted()
    {
        if (!deleted) {
            deleted = true;
            return false;
        }
        return true;
    }

    /**
     * called when indexing a script to ensure that it has not already been deleted. it must only be called
     * when synchronized on the script. This avoids a race where a script can be added by thread A, deleted by
     * thread B, unindexed -- unsuccessfully -- by thread B then indexed by thread A
     * @return the previous setting of deleted
     */
    public boolean isDeleted()
    {
        return deleted;
    }

    /**
     * record the fact that an error was thrown when attempting to transform a given class using this rule script
     * @param loader the loader of the class for which injection was attempted
     * @param internalClassName the internal Java name of the class
     * @param th the Throwable reocrding details of the failure
     * @return true if the failure was recorded false if not
     */
    public synchronized boolean recordFailedTransform(ClassLoader loader, String internalClassName, Throwable th)
    {
        return recordTransform(loader, internalClassName, null, null, null, th);
    }

    /**
     * record the fact that a trigger call has succeeded or else failed to install into bytecode
     * associated with a specific class and loader
     * @param loader the loader of the class for which injection was attempted
     * @param internalClassName the internal Java name of the class
     * @param triggerMethodName the name of the method injected into
     * @param desc the descriptor of the method injected into
     * @param rule the rule which was injected
     * @param th throwable generated during the attempt to parse the rule text or inject code at the trigger point
     * @return true if the successful injection was recorded false if not
     */
    public synchronized boolean recordTransform(ClassLoader loader, String internalClassName, String triggerMethodName, String desc, Rule rule, Throwable th)
    {
        if (deleted) {
            return false;
        }

        String fullMethodName = null;
        if (triggerMethodName !=  null) {
            fullMethodName = triggerMethodName + TypeHelper.internalizeDescriptor(desc);
        }

        // make sure we know about this specific loader and classname combination
        TransformSet transformSet = ensureTransformSet(loader, internalClassName, null);

        // and install the transform in the set
        transformSet.add(new Transform(loader, internalClassName, fullMethodName, rule, th));

        return true;
    }

    /**
     * check whether a rule has been used to transform a specific class. this can be used when
     * rules are redefined to decide whether or not a class needs to be retransformed. Note that
     * it must only be called after the script has been deleted by calling setDeleted.
     * @param clazz the class for which a transform is being sought.
     * @return true if the class has been transformed using this script otherwise false.
     */                                       
    public synchronized boolean hasTransform(Class<?> clazz)
    {
        ClassLoader loader = clazz.getClassLoader();

        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }

        for (TransformSet transformSet : transformSets) {
            if (transformSet.isFor(loader, clazz.getName())) {
                return !transformSet.isEmpty();
            }
        }
        return false;
    }
    /**
     * record the fact that a rule has been compiled with or without success
     * @param triggerClass the name of the trigger class to which the rule is attached
     * @param loader the classloader of the trigger class
     * @param successful true if the rule compiled successfully and false if it suffered from parse,
     * type or compile errors
     * @param detail text describing more details of the compilation outcome
     * @return true if the rule needs to be installed otherwise false
     */
    public synchronized boolean recordCompile(Rule rule, String triggerClass, ClassLoader loader, boolean successful, String detail)
    {
        if(deleted) {
            return false;
        }

        // find an existing transform set or create a new one
        TransformSet transformSet = ensureTransformSet(loader, triggerClass, null);
        for (Transform transform : transformSet.getTransforms()) {
            // transform may not employ the same rule
            // but it may have the same key.
            Rule transformRule = transform.getRule();
            if (transformRule != null && transformRule.getKey() == rule.getKey()) {
                transform.setCompiled(successful, detail);
                boolean isInstalled = transformSet.isInstalled();
                // record this as the latest rule to be installed
                transformSet.setInstalled(rule);
                // if this is the first installed rule then
                // we need to perform lifecycle processing
                return !isInstalled;
            }
        }
        // no such rule so no lifecycle processing
        return false;
    }

    /**
     * delete any transforms associated with a specific trigger class and loader for
     * deletion. this is called just before any attempt to retransform the class
     * to inject the script's associated rule. it ensures that records of previous
     * transforms associated with a prior retransformation of the class are removed
     * before any new ones are added
     */
    public synchronized void purge(ClassLoader loader, String triggerClassName)
    {
        TransformSet transformSet = lookupTransformSet(loader, triggerClassName);
        if (transformSet != null) {
            for (Transform transform : transformSet.getTransforms()) {
                Rule rule = transform.getRule();
                if(rule != null) {
                    rule.purge();
                }
            }
            transformSet.clearTransforms();
        }
    }


    /**
     * uninstall all transforms associated with this script. this is called after marking the script as
     * deleted and regenerating the methods for any associated transformed class to ensure that it does
     * not cause a rule trigger call to fail.
     */
    public synchronized void purge()
    {
        for (TransformSet transformSet : transformSets) {
            for (Transform transform : transformSet.getTransforms()) {
                Rule rule = transform.getRule();
                if(rule != null) {
                    rule.purge();
                }
            }
            transformSet.clearTransforms();
        }
        transformSets.clear();
    }

    public TransformSet ensureTransformSet(ClassLoader loader, String triggerClass, Rule installedRule)
    {
        TransformSet transformSet = lookupTransformSet(loader, triggerClass);
        if (transformSet == null) {
            transformSet = new TransformSet(loader, triggerClass);
            transformSet.setInstalled(installedRule);
            transformSets.add(transformSet);
        }
        return transformSet;
    }


    public TransformSet lookupTransformSet(ClassLoader loader, String triggerClass)
    {
        for (TransformSet transformSet : transformSets) {
            if (transformSet.isFor(loader, triggerClass)) {
                return transformSet;
            }
        }

        return null;
    }

    public String toString()
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        writeTo(writer);
        writer.flush();
        return stringWriter.toString();
    }
    
    public void writeTo(PrintWriter writer)
    {
        writer.print("# File ");
        writer.print(file);
        writer.print(" line ");
        writer.println(line);
        writer.print("RULE ");
        writer.println(name);
        if (isInterface) {
            writer.print("INTERFACE ");
        } else {
            writer.print("CLASS ");
        }
        if (isOverride) {
            writer.print("^");
        }
        writer.println(targetClass);
        writer.print("METHOD ");
        writer.println(targetMethod);
        if (imports != null) {
            for (int i = 0; i < imports.length ; i++) {
                writer.print("IMPORT ");
                writer.println(imports[i]);
            }
        }
        if (targetHelper != null) {
            writer.print("HELPER ");
            writer.println(targetHelper);
        }
        if (compileToBytecode) {
            writer.write("COMPILE\n");
        } else {
            writer.write("NOCOMPILE\n");
        }
        writer.println(targetLocation.toString());
        writer.println(ruleText);
        writer.println("ENDRULE");
    }
}
