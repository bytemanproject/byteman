/*
* JBoss, Home of Professional Open Source
* Copyright 2008-9, Red Hat Middleware LLC, and individual contributors
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

import java.lang.instrument.Instrumentation;
import java.util.*;
import java.io.PrintWriter;

/**
 * byte code transformer used to introduce byteman events into JBoss code
 */
public class Retransformer extends Transformer {

    /**
     * constructor allowing this transformer to be provided with access to the JVM's instrumentation
     * implementation
     *
     * @param inst the instrumentation object used to interface to the JVM
     */
    public Retransformer(Instrumentation inst, List<String> scriptPaths, List<String> scriptTexts)
            throws Exception
    {
        super(inst, scriptPaths, scriptTexts, true);
        addTransformListener();
    }

    protected void installScript(List<String> scriptTexts, List<String> scriptNames) throws Exception
    {
        int length = scriptTexts.size();
        List<RuleScript> toBeAdded = new LinkedList<RuleScript>();
        List<RuleScript> toBeRemoved = new LinkedList<RuleScript>();

        for (int i = 0; i < length ; i++) {
            String scriptText = scriptTexts.get(i);
            String scriptName = scriptNames.get(i);

            List<RuleScript> ruleScripts = processScripts(scriptText, scriptName);
            toBeAdded.addAll(ruleScripts);
        }

        for (RuleScript ruleScript : toBeAdded) {
            String name = ruleScript.getName();
            String className = ruleScript.getTargetClass();
            String baseName = null;
            int lastDotIdx = className.lastIndexOf('.');
            if (lastDotIdx >= 0) {
                baseName = className.substring(lastDotIdx + 1);
            }

            RuleScript previous;

            synchronized (nameToScriptMap) {
                previous = nameToScriptMap.get(name);
                if (previous != null) {
                    System.out.println("redefining rule " + name);
                    toBeRemoved.add(previous);
                    previous.setDeleted();
                }
                nameToScriptMap.put(name, ruleScript);
            }

            // remove any old scripts and install the new ones to ensure that
            // automatic loads do the right thing

            synchronized(targetToScriptMap) {
                List<RuleScript> list = targetToScriptMap.get(className);
                if (list != null) {
                    if (previous != null) {
                        list.remove(previous);
                    }
                } else {
                    list = new ArrayList<RuleScript>();
                    targetToScriptMap.put(className, list);
                }
                list.add(ruleScript);
                if (baseName != null) {
                    list = targetToScriptMap.get(baseName);
                    if (list != null) {
                        if (previous != null) {
                            list.remove(previous);
                        }
                    } else {
                        list = new ArrayList<RuleScript>();
                        targetToScriptMap.put(baseName, list);
                    }
                }
            }
        }


        // ok, now that we have updated the maps we need to find all classes which match the scripts and
        // retransform them

        List<Class<?>> transformed = new LinkedList<Class<?>>();

        for (Class clazz : inst.getAllLoadedClasses()) {
            String name = clazz.getName();
            int lastDot = name.lastIndexOf('.');

            if (isBytemanClass(name) || !isTransformable(name)) {
                continue;
            }

            if (targetToScriptMap.containsKey(name)) {
                transformed.add(clazz);
            } else if (lastDot >= 0 && targetToScriptMap.containsKey(name.substring(lastDot+1))) {
                transformed.add(clazz);
            }
        }

        // retransform all classes whose rules have changed

        if (!transformed.isEmpty()) {
            Class<?>[] transformedArray = new Class<?>[transformed.size()];
            inst.retransformClasses(transformed.toArray(transformedArray));
        }
    }


    protected void listScripts(PrintWriter out)  throws Exception
    {
        synchronized (nameToScriptMap) {
            Iterator<RuleScript> iterator = nameToScriptMap.values().iterator();

            if (!iterator.hasNext()) {
                out.println("no rules installed");
            } else {
                while (iterator.hasNext()) {
                    RuleScript ruleScript = iterator.next();
                    ruleScript.writeTo(out);
                    synchronized (ruleScript) {
                        List<Transform> transformed = ruleScript.getTransformed();
                        if (transformed != null) {
                            Iterator<Transform> iter = transformed.iterator();
                            while (iter.hasNext()) {
                                Transform transform = iter.next();
                                transform.writeTo(out);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addTransformListener()
    {
        TransformListener.initialize(this);
    }

    /**
     * ensure that scripts which apply to classes loaded before registering the transformer get
     * are installed by retransforming the relevant classes
     */

    public void installBootScripts() throws Exception
    {
        // check for scrips which apply to classes already loaded during bootstrap and retransform those classes
        // so that rule triggers are injected

        List<Class<?>> omitted = new LinkedList<Class<?>>();

        Class<?>[] loaded = inst.getAllLoadedClasses();
        if (isVerbose()) {
            System.out.println("loaded classes size = " + loaded.length);
        }

        for (Class clazz : loaded) {
            String name = clazz.getName();
            int lastDot = name.lastIndexOf('.');

            if (isBytemanClass(name) || !isTransformable(name)) {
                continue;
            }

            boolean found = false;

            // although this is done synchronized a transformation may sneak in between this check and
            // the retransformClasses call below causing unnecessary redefinition of the some classes
            // TODO -- see if we can tighten up the synchronization here (probably very tricky :-)
            synchronized(targetToScriptMap) {
                List<RuleScript> scripts = targetToScriptMap.get(name);
                if (scripts != null) {
                    for (RuleScript script : scripts) {
                        System.out.println("Checking script " + script.getName());
                        if (!script.hasTransform(clazz)) {
                            omitted.add(clazz);
                            found = true;
                            if (isVerbose()) {
                                System.out.println("Found script for bootstrap class " + clazz.getName());
                            }
                            break;
                        }
                    }
                }
                if (!found && lastDot >= 0) {
                    scripts = targetToScriptMap.get(name.substring(lastDot + 1));
                    if (scripts != null) {
                        for (RuleScript script : scripts) {
                            System.out.println("Checking script " + script.getName());
                            if (!script.hasTransform(clazz)) {
                                omitted.add(clazz);
                                found = true;
                                if (isVerbose()) {
                                    System.out.println("Found script for bootstrap class " + clazz.getName());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        // retransform all classes for which we found untransformed rules

        if (!omitted.isEmpty()) {
            Class<?>[] transformedArray = new Class<?>[omitted.size()];
            inst.retransformClasses(omitted.toArray(transformedArray));
        }
    }
}
