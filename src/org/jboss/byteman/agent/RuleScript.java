/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import java.util.ArrayList;
import java.util.List;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * information about a single rule obtained from a rule script including any failed or successful transforms
 * performed using the rule
 */

public class RuleScript
{
    private String name;
    private String targetClass;
    private String targetMethod;
    private String targetHelper;
    private Location targetLocation;
    private String ruleText;
    private boolean deleted;
    private int line;
    private String file;
    private List<Transform> transformed;

    public RuleScript(String name, String targetClass, String targetMethod, String targetHelper, Location targetLocation, String ruleText, int line, String file)
    {
        this.name = name;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.targetHelper = targetHelper;
        this.targetLocation = (targetLocation != null ? targetLocation : Location.create(LocationType.ENTRY, ""));
        this.ruleText = ruleText;
        this.line = line;
        this.file = file;
        this.transformed = null;
    }

    public String getName() {
        return name;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public String getTargetHelper() {
        return targetHelper;
    }

    public String getTargetMethod() {
        return targetMethod;
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
     * invoked by the retransformer code when a rule is redefined to inhibit further transformations via this script
     */
    public synchronized void setDeleted()
    {
        deleted = true;
    }

    /**
     * record the fact that a trigger call has been successfully installed into bytecode associated with a specific
     * class and loader and a corresponding rule instance been installed
     * @param loader
     * @param internalClassName
     * @param rule
     * @return
     */
    public synchronized boolean recordTransform(ClassLoader loader, String internalClassName, Rule rule)
    {
        return recordTransform(loader, internalClassName, rule, null);
    }

    /**
     * record the fact that a trigger call has failed to install into bytecode associated with a specific
     * class and loader
     * @param loader the loader of the class being transformed
     * @param internalClassName the internal name of the class being transformed
     * @param rule the rule resulting from the parse of the rule text or null if a parse error occurred
     * @param th throwable generated during the attempt to parse the rule text or inject code at the trigger point
     * @return
     */
    public synchronized boolean recordTransform(ClassLoader loader, String internalClassName, Rule rule, Throwable th)
    {
        if (deleted) {
            return false;
        }

        addTransform(new Transform(loader, internalClassName, rule, th));

        return true;
    }

    private void addTransform(Transform transform)
    {
        if (transformed == null) {
            transformed = new ArrayList<Transform>();
        }

        transformed.add(transform);
    }


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
     * record the fact that a rule has been compiled wiht or without success
     * @param triggerClass the name of the trigger class to which the rule is attached
     * @param loader the classloader of the trigger class
     * @param successful true if the rule compiled successfully and false if it suffered form parse,
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
        writer.print("CLASS ");
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
