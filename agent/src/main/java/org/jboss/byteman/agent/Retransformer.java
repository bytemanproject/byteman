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
package org.jboss.byteman.agent;

import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import org.jboss.byteman.modules.ModuleSystem;
import org.jboss.byteman.rule.helper.Helper;

/**
 * byte code transformer used to introduce byteman events into JBoss code
 */
public class Retransformer extends Transformer {

    private Set<String> sysJars = new HashSet<String>();  // jar files that were loaded in the sys CL
    private Set<String> bootJars = new HashSet<String>(); // jar files that were loaded in the boot CL

    /**
     * constructor allowing this transformer to be provided with access to the JVM's instrumentation
     * implementation
     *
     * @param inst the instrumentation object used to interface to the JVM
     * @param moduleSystem the module system to use for helper and class loading
     * @param scriptPaths list of file paths for each input script
     * @param scriptTexts the text of each input script
     * @param isRedefine true if class redefinition is allowed false if not
     * @throws Exception if a script is in error
     */
    public Retransformer(Instrumentation inst, ModuleSystem moduleSystem, List<String> scriptPaths, List<String> scriptTexts, boolean isRedefine)
            throws Exception
    {
        super(inst, moduleSystem, scriptPaths, scriptTexts, isRedefine);
        //addTransformListener(hostname, port);
    }

    public void installScript(List<String> scriptTexts, List<String> scriptNames, PrintWriter out) throws Exception
    {
        int length = scriptTexts.size();
        List<RuleScript> toBeAdded = new LinkedList<RuleScript>();
        List<RuleScript> toBeRemoved = new LinkedList<RuleScript>();

        for (int i = 0; i < length ; i++) {
            String scriptText = scriptTexts.get(i);
            String scriptName = scriptNames.get(i);

            List<RuleScript> ruleScripts = scriptRepository.processScripts(scriptText, scriptName);
            toBeAdded.addAll(ruleScripts);
        }

        for (RuleScript ruleScript : toBeAdded) {
            String name = ruleScript.getName();
            RuleScript previous;

            previous = scriptRepository.addScript(ruleScript);
            if (previous != null) {
                out.println("redefine rule " + name);
                toBeRemoved.add(previous);
            } else {
                out.println("install rule " + name);
            }
        }

        // ok, now that we have updated the indexes we need to find all classes which match the scripts and
        // retransform them

        // list all class names for the to be added and to be removed scripts

        List<String> deletedClassNames = new LinkedList<String>();

        for (RuleScript ruleScript : toBeRemoved) {
            List<Transform> transforms = ruleScript.allTransforms();
            for (Transform transform : transforms) {
                // only need to retransform classes which were updated
                // so ignore transforms which include a throwable
                Throwable throwable = transform.getThrowable();
                if(throwable == null) {
                    String className = transform.getInternalClassName();
                    if(!deletedClassNames.contains(className)) {
                        deletedClassNames.add(className);
                    }
                }
            }
        }

        // for added scripts we have to transform anything which might be a match

        ScriptRepository tmpRepository = new ScriptRepository(skipOverrideRules());
        for (RuleScript ruleScript : toBeAdded) {
            tmpRepository.addScript(ruleScript);
        }

        // now look for loaded classes whose names are in the deleted list or which match added rules

        List<Class<?>> transformed = new LinkedList<Class<?>>();

        for (Class clazz : inst.getAllLoadedClasses()) {
            if (isSkipClass(clazz)) {
                continue;
            }
            if (deletedClassNames.contains(clazz.getName())) {
                transformed.add(clazz);
            } else if (tmpRepository.matchClass(clazz)) {
                    transformed.add(clazz);
            }
        }
        // retransform all classes whose rules have changed

        if (!transformed.isEmpty()) {
            Class<?>[] transformedArray = new Class<?>[transformed.size()];
            transformed.toArray(transformedArray);
            for (int i = 0; i < transformed.size(); i++) {
                Helper.verbose("retransforming " + transformedArray[i].getName());
            }
            synchronized(this) {
                try {
                    inst.retransformClasses(transformedArray);
                } catch(VerifyError ve) {
                    Helper.err("Retransformer : VerifyError during retransformation : some rules may not have been correctly injected or uninjected!");
                    Helper.errTraceException(ve);
                    out.println("VerifyError during retransformation : some rules may not have been correctly injected or uninjected!");
                    ve.printStackTrace(out);
                }
            }
        }

        // now we need to ensure that previously installed
        // rules are uninstalled. however, if the rule
        // has been re-injected in an equivalent transform
        // set then we simply mark it as installed, eliding
        // an extra uninstall/install cycle. this also
        // avoids an unhelpful deactivate/activate step
        // that is not really appropriate when redefining
        // an existing rule.
        //
        // n.b. we mark the set using the last installed Rule
        // instance so as to to retain a target for any subsequent
        // uninstall. if/when the newly injected rule gets triggered
        // it will update to use the new rule as the marker.

        for (RuleScript oldRuleScript : toBeRemoved) {
            RuleScript newRuleScript = scriptRepository.scriptForRuleName(oldRuleScript.getName());
            // new script must exist!
            synchronized (newRuleScript) {
                for (TransformSet oldTransformSet : oldRuleScript.getTransformSets()) {
                    // see if we have an equivalent new rule set
                    TransformSet newTransformSet = newRuleScript.lookupTransformSet(oldTransformSet.getLoader(), oldTransformSet.getTriggerClass());
                    if(newTransformSet == null || newTransformSet.isInstalled()) {
                        if(oldTransformSet.isInstalled()) {
                            // we need to run an uninstall for the old transform set
                            oldTransformSet.getRule().uninstalled();
                        }
                    } else {
                        // copy across the rule used for the prior
                        // install so we can use it for a later uninstall
                        // it will be replaced with a new instance
                        // if any of the newly injected rules pass
                        // ensureTypeCheckCompiled
                        if(newTransformSet != null) {
                            newTransformSet.setInstalled(oldTransformSet.getRule());
                        } else {
                            newRuleScript.ensureTransformSet(oldTransformSet.getLoader(), oldTransformSet.getTriggerClass(), oldTransformSet.getRule());
                        }
                    }
                }
            }
        }
    }

    protected void collectAffectedNames(List<RuleScript> ruleScripts, List<String> classList, List<String> interfaceList,
                                   List<String> superClassList, List<String> superInterfaceList)
    {

        for (RuleScript ruleScript : ruleScripts) {
            String targetClassName = ruleScript.getTargetClass();
            boolean isOverride = ruleScript.isOverride();
            if (ruleScript.isInterface()) {
                if (!interfaceList.contains(targetClassName)) {
                    interfaceList.add(targetClassName);
                    if (isOverride) {
                        superInterfaceList.add(targetClassName);
                    }
                }
            } else {
                if (!classList.contains(targetClassName)) {
                    classList.add(targetClassName);
                    if (isOverride) {
                        superClassList.add(targetClassName);
                    }
                }
            }
        }
    }

    public void listScripts(PrintWriter out)  throws Exception
    {
        Iterator<RuleScript> iterator = scriptRepository.currentRules().iterator();

        if (!iterator.hasNext()) {
            out.println("no rules installed");
        } else {
            while (iterator.hasNext()) {
                RuleScript ruleScript = iterator.next();
                ruleScript.writeTo(out);
                synchronized (ruleScript) {
                    List<Transform> transforms = ruleScript.allTransforms();
                    for (Transform transform : transforms) {
                        transform.writeTo(out);
                    }
                }
            }
        }
    }

    public void removeScripts(List<String> scriptTexts, PrintWriter out) throws Exception
    {
        List<RuleScript> toBeRemoved;

        if (scriptTexts != null) {
            toBeRemoved = new LinkedList<RuleScript>();
            int length = scriptTexts.size();
            for (int i = 0; i < length ; i++) {
                String scriptText = scriptTexts.get(i);
                String[] lines = scriptText.split("\n");
                for (int j = 0; j < lines.length; j++) {
                    String line = lines[j].trim();
                    if (line.startsWith("RULE ")) {
                        String name = line.substring(5).trim();
                        RuleScript ruleScript = scriptRepository.scriptForRuleName(name);
                        if (ruleScript ==  null) {
                            out.print("ERROR failed to find loaded rule with name ");
                            out.println(name);
                        } else if (toBeRemoved.contains(ruleScript)) {
                            out.print("WARNING duplicate occurence for rule name ");
                            out.println(name);
                        } else {
                            toBeRemoved.add(ruleScript);
                        }
                    }
                }
            }
        } else {
            toBeRemoved = scriptRepository.currentRules();
        }

        if (toBeRemoved.isEmpty()) {
            out.println("ERROR No rule scripts to remove");
            return;
        }

        for (RuleScript ruleScript : toBeRemoved) {
            if (scriptRepository.removeScript(ruleScript) != ruleScript) {
                out.println("ERROR remove failed to find script " + ruleScript.getName());
            }
        }

        // ok, now that we have updated the maps and deleted the scripts
        // we need to find all classes which were transformed by
        // the scripts and retransform them


        // now look for loaded classes whose names are in the list

        List<Class<?>> transformed = new LinkedList<Class<?>>();
        List<String> deletedClassNames = new LinkedList<String>();

        for (RuleScript ruleScript : toBeRemoved) {
            for (Transform transform : ruleScript.allTransforms()) {
                // only need to retransform classes which were updated
                // so ignore transforms which include a throwable
                Throwable throwable = transform.getThrowable();
                if(throwable == null) {
                    String className = transform.getInternalClassName();
                    if(!deletedClassNames.contains(className)) {
                        deletedClassNames.add(className);
                    }
                }
            }
        }

        for (Class clazz : inst.getAllLoadedClasses()) {
            if (isSkipClass(clazz)) {
                continue;
            }

            if (deletedClassNames.contains(clazz.getName())) {
                transformed.add(clazz);
            }
        }

        // retransform all classes affected by the change

        if (!transformed.isEmpty()) {
            Class<?>[] transformedArray = new Class<?>[transformed.size()];
            transformed.toArray(transformedArray);
            for (int i = 0; i < transformed.size(); i++) {
                Helper.verbose("retransforming " + transformedArray[i].getName());
            }
            try {
                inst.retransformClasses(transformedArray);
            } catch(VerifyError ve) {
                Helper.err("Retransformer : VerifyError during retransformation : some rules may not have been correctly uninjected!");
                Helper.errTraceException(ve);
                out.println("VerifyError during retransformation : some rules may not have been correctly uninjected!");
                ve.printStackTrace(out);
            }
        }

        // now we can safely purge keys for all the deleted scripts -- we need to do this
        // after the retransform because the latter removes the trigger code which uses
        // the rule key

        for (RuleScript oldRuleScript : toBeRemoved) {
            // mark the script as deleted so it doesn't get run any more
            oldRuleScript.setDeleted();
            // now deal with uninstall, allowing for possible reinstall
            RuleScript newRuleScript = scriptRepository.scriptForRuleName(oldRuleScript.getName());
            // new script may not exist!
            if (newRuleScript != null) {
                synchronized (newRuleScript) {
                    for (TransformSet oldTransformSet : oldRuleScript.getTransformSets()) {
                        // see if we have an equivalent new rule set
                        TransformSet newTransformSet = newRuleScript.lookupTransformSet(oldTransformSet.getLoader(), oldTransformSet.getTriggerClass());
                        if(newTransformSet == null || newTransformSet.isInstalled()) {
                            if(oldTransformSet.isInstalled()) {
                                // new rule has already been installed so we need
                                // to run an uninstall for the old transform set
                                oldTransformSet.getRule().uninstalled();
                            }
                        } else {
                            // new rule is not yet installed so we can elide
                            // the uninstalled + installed lifecycle events
                            // we have to copy across the rule used for the prior
                            // install so we can use it as the default argument
                            // for a later uninstall. it will be replaced with a
                            // new instance if any of the newly injected rules
                            // pass ensureTypeCheckCompiled
                            newTransformSet.setInstalled(oldTransformSet.getRule());
                        }
                    }
                }
            } else {
                for (TransformSet oldTransformSet : oldRuleScript.getTransformSets()) {
                    if(oldTransformSet.isInstalled()) {
                        // we need to run an uninstall for the old transform set
                        oldTransformSet.getRule().uninstalled();
                    }
                }
                out.println("uninstall RULE " + oldRuleScript.getName());
            }
        }
        // now purge the rules for the old script
    }

    public void appendJarFile(PrintWriter out, JarFile jarfile, boolean isBoot) throws Exception
    {
        if (isBoot) {
            inst.appendToBootstrapClassLoaderSearch(jarfile);
            bootJars.add(jarfile.getName());
            out.println("append boot jar " + jarfile.getName());
        } else {
            inst.appendToSystemClassLoaderSearch(jarfile);
            sysJars.add(jarfile.getName());
            out.println("append sys jar " + jarfile.getName());
        }
    }

    /**
     * Returns jars that this retransformer was asked to
     * {@link #appendJarFile(PrintWriter, JarFile, boolean) add} to the boot classloader.
     *
     * Note that the returned set will not include those jars that were added to the
     * instrumentor object at startup via the -javaagent command line argument.
     *
     * @return set of jar pathnames for all jars loaded in the boot classloader
     */
    public Set<String> getLoadedBootJars() {
        return new HashSet<String>(bootJars); // returns a copy
    }

    /**
     * Returns jars that this retransformer was asked to
     * {@link #appendJarFile(PrintWriter, JarFile, boolean) add} to the system classloader.
     *
     * Note that the returned set will not include those jars that were added to the
     * instrumentor object at startup via the -javaagent command line argument.
     *
     * @return set of jar pathnames for all jars loaded in the system classloader
     */
    public Set<String> getLoadedSystemJars() {
        return new HashSet<String>(sysJars); // returns a copy
    }
}
