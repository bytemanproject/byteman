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
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * details of a single rule obtained from a rule file. RuleScritp instances are stored in the script repository
 * attached to the transformer. They are used to generate Rule instances at transform time. The RuleScript contains
 * a list of Transforms which detail failed or successful transforms performed using the script.
 */

public class RuleScript
{
    /**
     * the name of the rule from which this scritp is derived
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
     * a list of records identifying contexts in which the rule has been applied.
     */
    private List<Transform> transformed;

    /**
     * standard constructor for a rule
     * @param name
     * @param targetClass
     * @param isInterface
     * @param isOverride
     * @param targetMethod
     * @param targetHelper
     * @param targetLocation
     * @param ruleText
     * @param line
     * @param file
     */
    public RuleScript(String name, String targetClass, boolean isInterface, boolean isOverride, String targetMethod, String targetHelper, Location targetLocation, String ruleText, int line, String file)
    {
        this.name = name;
        this.targetClass = targetClass;
        this.isInterface =  isInterface;
        this.isOverride = isOverride;
        this.targetMethod = targetMethod;
        this.targetHelper = targetHelper;
        this.targetLocation = (targetLocation != null ? targetLocation : Location.create(LocationType.ENTRY, ""));
        this.ruleText = ruleText;
        this.line = line;
        this.file = file;
        this.transformed = new ArrayList<Transform>();
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

    /**
     * getter for list of transforms applied for this script. must be called synchronized on the script.
     * @return the list of transforms
     */
    public List<Transform> getTransformed()
    {
        return transformed;
    }

    /**
     * return a count of the number of transforms applied for this script. must be called synchronized on the script.
     * @return the size of the list of transforms
     */
    public int getTransformedCount()
    {
        return (transformed != null ? transformed.size() : 0);
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
     * @param loader
     * @param internalClassName
     * @return
     */
    public synchronized boolean recordFailedTransform(ClassLoader loader, String internalClassName, Throwable th)
    {
        return recordTransform(loader, internalClassName, null, null, null, th);
    }

    /**
     * record the fact that a trigger call has been successfully installed into bytecode associated with a specific
     * class and loader and a corresponding rule instance been installed
     * @param loader
     * @param internalClassName
     * @return
     */
    public synchronized boolean recordMethodTransform(ClassLoader loader, String internalClassName, String triggerMethodName, String desc, Rule  rule)
    {
        return recordTransform(loader, internalClassName, triggerMethodName, desc, rule, null);
    }

    /**
     * record the fact that a trigger call has failed to install into bytecode associated with a specific
     * class and loader
     * @param loader the loader of the class being transformed
     * @param internalClassName the internal name of the class being transformed
     * @param th throwable generated during the attempt to parse the rule text or inject code at the trigger point
     * @return
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

        // and install the transform in the list

        transformed.add(new Transform(loader, internalClassName, fullMethodName, rule, th));

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

        int count = getTransformedCount();
        for (int i =  0; i < count; i++) {
            Transform transform = transformed.get(i);
            if (transform.getLoader() == loader) {
                return true;
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
     */
    public synchronized void recordCompile(String triggerClass, ClassLoader loader, boolean successful, String detail)
    {
        int count = getTransformedCount();
        for (int i =  0; i < count; i++) {
            Transform transform = transformed.get(i);
            if (transform.getLoader() == loader) {
                transform.setCompiled(successful, detail);
            }
        }
    }

    /**
     * uninstall any rules associated with this script. this is called after marking the script as
     * deleted and regenerating the methods for any associated transformed class to ensure that it does
     * not cause a rule trigger call to fail.
     */
    public synchronized void purge()
    {
        if (transformed != null) {
            int count = transformed.size();
            for (int i =  0; i < count; i++) {
                Transform transform = transformed.get(i);
                Rule rule = transform.getRule();
                if (rule != null) {
                    rule.purge();
                }
            }
        }
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
        if (targetHelper != null) {
            writer.print("HELPER ");
            writer.println(targetHelper);
        }
        writer.println(targetLocation.toString());
        writer.println(ruleText);
        writer.println("ENDRULE");
    }
}
