/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010 Red Hat and individual contributors
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
 * (C) 2010,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.agent;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.modules.ModuleSystem;
import org.jboss.byteman.rule.helper.Helper;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class used to manage lifecycle events for rule helpers
 */
public class HelperManager
{
    // public API

    /**
     * construct a manager
     *
     * @param inst will be non-null if we are running in an agent
     * @param moduleSystem must be non-null, use NonModuleSystem if nothing specal is needed
     */
    public HelperManager(Instrumentation inst, ModuleSystem moduleSystem)
    {
        this.inst = inst;
        this.moduleSystem = moduleSystem;
        this.helperDetailsMap = new ConcurrentHashMap<Class<?>, LifecycleDetails>();
    }

    /**
     * perform install processing for a rule
     * @param rule an instantiation of a specific rule script as a Rule which has been
     * successfully loaded into the agent, injected, type-checked and, optionally,
     * compiled.
     *
     * Note that install processing happens when an injected rule is first triggered,
     * not when it is injected. This ensures that a rule is not installed until it
     * has been successfully type checked. It also ensures that helper life-cycle
     * calls are not made underneath a ClassFileTransformer transform callback. This
     * is so that execution of life-cycle code does not initiate class-loading without
     * the desired associated transforms being applied -- transformation is not entered
     * recursively. The same reasoning accounts for ehy type-checking is delayed until
     * trigger-time.
     *
     * Note also that some given rule script may be injected into more than one method
     * of more than one class. A METHOD spec may match more than one method when the
     * descriptor is omitted. A CLASS or INTERFACE specification may match more than one
     * class for a variety of reasons: multiple deployments of the same named class;
     * omission of the package name in the specification; injection through interfaces;
     * use of overriding injection. Every successful install into a specific method (of
     * some given class) leads to one installed callback even when the rule is injected
     * at multiple matching points in the bytecode of that method.
     */
    public void installed(Rule rule)
    {
        // ignore unless an agent is actually running
        if (inst == null) {
            return;
        }

        Class<?> helperClass = rule.getHelperClass();

        Helper.verbose("HelperManager.install for helper class " + helperClass.getName());

        installed(rule, helperClass);
    }

    private LifecycleDetails installed(Rule rule, Class<?> helperClass)
    {
        // synchronize on the lifecycle class to ensure it is not uninstalled
        // while we are deciding whether or not to install it
        synchronized (helperClass) {

            LifecycleDetails parentDetails = null;
            // give the super chain a chance to install
            Class<?> superClass = helperClass.getSuperclass();
            if(superClass != Object.class) {
                parentDetails = installed(rule, superClass);
            }
            // now run the install for this class
            LifecycleDetails details = getDetails(helperClass, true, parentDetails);
            if (details.installCount == 0 && details.activated != null) {
                Helper.verbose("calling activated() for helper class " + helperClass.getName());

                try {
                    details.activated.invoke(null);
                } catch (Exception e) {
                    Helper.err("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".activate() : " + e);
                    Helper.errTraceException(e);
                }
            }
            if (details.installed != null) {
                Helper.verbose("calling installed(" + rule.getName() + ") for helper class" + helperClass.getName());

                try {
                    if (details.installedTakesRule) {
                        details.installed.invoke(null, rule);
                    } else {
                        details.installed.invoke(null, rule.getName());
                    }
                } catch (Exception e) {
                    Helper.err("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".installed(String) : " + e);
                    Helper.errTraceException(e);
                }
            }
            details.installCount++;

            return details;
        }
    }

    /**
     * perform install processing for a rule
     * @param rule an instantiation of a specific rule script as a Rule which has been
     * successfully loaded into the agent, injected, type-checked and, optionally,
     * compiled.
     *
     * Note that uninstall processing is performed by the Retransformer during unloading
     * (or redefinition) of scripts after any retransform of the classesd affected by the
     * scripts has been performed. When a script is uninstalled an uinstall event should
     * occur for each case where there a prior install event i.e. for each method into
     * which the rule was successfully injected, type checked and, possibly, compiled.
     *
     * In cases where a rule is redefined the process is slightly different. If the
     * redefined rule fails to parse, inject or type check then any previously injected rule
     * gets uninstalled. If a redefined rule is successfully, parsed, injected and type-checked
     * then uninstall and reinstall of the rule is elided. The obvious benefit of elision is
     * that the associated helper manager does not get spuriously deactivated and reactivated
     * by a simple redefinition. However, two  potentially (but only mildly) surprising
     * consequences follow:
     *
     * 1) If a newly injected rule is triggered before a subsequent uninstall then it will
     * be the target of the uninstalled call i.e. callbacks which take a Rule argument cannot
     * rely on seeing the same rule instance at install and uninstall. Of course, both rules
     * will still have the same name.
     *
     * 2) If a newly injected rule is not triggered before the uninstall happens, or is triggered
     * and fails to type check or compile, then the previously installed rule will be used as the
     * target of the uninstalled call i.e. callbacks which take a Rule argument cannot rely upon
     * the uninstalled rule object being derived from the latest installed script text.
     */
    public void uninstalled(Rule rule)
    {
        // ignore unless an agent is actually running
        if (inst == null) {
            return;
        }
        
        Class helperClass = rule.getHelperClass();
        Helper.verbose("HelperManager.uninstall for helper class " + helperClass.getName());

        uninstalled(rule, helperClass);
    }

    public void uninstalled(Rule rule, Class<?> helperClass)
    {
        // synchronize on the lifecycle class to ensure it is not uninstalled
        // while we are deciding whether or not to install it
        synchronized (helperClass) {
            // uninstall this class first then deal with parents
            LifecycleDetails details;
            details = getDetails(helperClass, false, null);
            if (details == null) {
                Helper.err("HelperManager.uninstalled : shouldn't happen! uninstall failed to locate helper details for " + helperClass.getName());
                return;
            }
            details.installCount--;
            if (details.uninstalled != null) {
                Helper.verbose("calling uninstalled(" + rule.getName() + ") for helper class " + helperClass.getName());

                try {
                    if (details.uninstalledTakesRule) {
                        details.uninstalled.invoke(null, rule);
                    } else {
                        details.uninstalled.invoke(null, rule.getName());
                    }
                } catch (Exception e) {
                    Helper.err("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".uninstalled(String) : " + e);
                    Helper.errTraceException(e);
                }
            }
            if (details.installCount == 0 && details.deactivated != null) {
                Helper.verbose("calling deactivated() for helper class" + helperClass.getName());

                try {
                    details.deactivated.invoke(null);
                } catch (Exception e) {
                    Helper.err("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".deactivate() : " + e);
                    Helper.errTraceException(e);
                }
            }
            if (details.installCount == 0) {
                purgeDetails(details);
            }
            // give the super a chance to uninstall if needed
            details = details.parent;
            if (details != null) {
                uninstalled(rule, details.lifecycleClass);
            }
        }
    }

    /**
     * This method exposes a capability of the Byteman agent's
     * Instrumentation instance while avoiding exposing the instance
     * itself. It returns an estimate of the object size or -1 in case
     * an agent has not been installed.
     * @param o the object to be sized
     * @return an estimate of the size or -1
     */
    public long getObjectSize(Object o)
    {
        if (inst == null) {
            Helper.err("Cannot calculate object size since a Byteman agent has not been installed");
            return -1;
        }
        return this.inst.getObjectSize(o);
    }

    public ModuleSystem getModuleSystem()
    {
        return moduleSystem;
    }

    // private parts

    /**
     * the instrumentation object used to install the transformer. If this is null then we are not running in
     * a real agent so  we do no work.
     */
    private Instrumentation inst;

    /**
     * the module system implementation.
     */
    private ModuleSystem moduleSystem;

    /**
     * a hashmap from helper classes to their corresponding helper details. we don't use weak references here
     * because there is only ever an entry here if there is a rule installed which references the class. entries
     * get cleared when the rules are uninstalled.
     */
    private ConcurrentHashMap<Class<?>, LifecycleDetails> helperDetailsMap;

    /**
     * a record of a specific helper class tracking the number of installed rules which reference it
     * and referencing a table detailing the lifecycle methods it implements
     *
     * LifeCycleDetails are daisy-chained to ensure that lifecycle processing
     * associated with a superclass are performed automatically as part of a
     * given Helper class's lifecycle processing.
     */
    private static class LifecycleDetails
    {
        /**
         * the helper class whose lifecycle this record details
         */
        public Class<?> lifecycleClass;
        /**
         * daisy-chain link to the the first parent class which also requires lifecycle processing
         * or null if there is no such parent
         */
        public LifecycleDetails parent;
        /**
         * reference count for installed rules which employ this helper class
         */
        public int installCount;

        /**
         * method called when helper is activated
         */
        public Method activated;
        /**
         * method called when helper is deactivated
         */
        public Method deactivated;
        /**
         * method called when rule is installed
         */
        public Method installed;
        /**
         * flag true if installed takes a Rule argument false if it takes a String argument
         */
        public boolean installedTakesRule;
        /**
         * method called when rule is uninstalled
         */
        public Method uninstalled;
        /**
         * flag true if uninstalled takes a Rule argument false if it takes a String argument
         */
        public boolean uninstalledTakesRule;

        public LifecycleDetails(Class<?> lifecycleClass, LifecycleDetails parent)
        {
            this.lifecycleClass = lifecycleClass;
            this.parent = parent;
            this.installCount = 0;
        }
    }

    /**
     * name of method invoked when helper installed count transitions from 0 to positive
     */
    private final static String ACTIVATED_NAME = "activated";
    /**
     * name of method invoked when helper installed count transitions from positive to 0
     */
    private final static String DEACTIVATED_NAME = "deactivated";
    /**
     * name of method invoked when rule is installed for a given helper
     */
    private final static String INSTALLED_NAME = "installed";
    /**
     * name of method invoked when rule is uninstalled for a given helper
     */
    private final static String UNINSTALLED_NAME = "uninstalled";
    /**
     * param types of method invoked when helper installed count transitions from 0 to positive
     */
    private final static Class[] ACTIVATED_SIGNATURE = null;
    /**
     * param types of method invoked when helper installed count transitions from positive to 0
     */
    private final static Class[] DEACTIVATED_SIGNATURE = null;
    /**
     * param types of method invoked when rule is installed for a given helper
     */
    private final static Class[] INSTALLED_RULE_SIGNATURE = new Class<?>[] { Rule.class };
    /**
     * param types of method invoked when rule is uninstalled for a given helper
     */
    private final static Class[] UNINSTALLED_RULE_SIGNATURE = INSTALLED_RULE_SIGNATURE;
    /**
     * param types of method invoked when rule is installed for a given helper
     */
    private final static Class[] INSTALLED_STRING_SIGNATURE = new Class<?>[] { String.class };
    /**
     * param types of method invoked when rule is uninstalled for a given helper
     */
    private final static Class[] UNINSTALLED_STRING_SIGNATURE = INSTALLED_STRING_SIGNATURE;

    /**
     * lookup or create a record describing the lifecycle methods of a helper class. this must only be
     * called when synchronized on the helper class.
     * @param helperClass
     * @param createIfAbsent if the details are not present and this is true then create and install new details
     * @param parent details for the super of helperClass required only when createIfAbsent is true and
     * the parent class is not Object
     * @return the relevant details
     */
    private LifecycleDetails getDetails(Class<?> helperClass, boolean createIfAbsent, LifecycleDetails parent)
    {
        LifecycleDetails details = helperDetailsMap.get(helperClass);
        if (details == null && createIfAbsent) {
            details = new LifecycleDetails(helperClass, parent);
            details.activated = lookupLifecycleMethod(helperClass, ACTIVATED_NAME, ACTIVATED_SIGNATURE);
            details.deactivated = lookupLifecycleMethod(helperClass, DEACTIVATED_NAME, DEACTIVATED_SIGNATURE);

            // check for methods with Rule arguments first
            details.installed = lookupLifecycleMethod(helperClass, INSTALLED_NAME, INSTALLED_RULE_SIGNATURE);
            details.uninstalled = lookupLifecycleMethod(helperClass, UNINSTALLED_NAME, UNINSTALLED_RULE_SIGNATURE);

            if (details.installed != null) {
                details.installedTakesRule = true;
            } else {
                details.installed = lookupLifecycleMethod(helperClass, INSTALLED_NAME, INSTALLED_STRING_SIGNATURE);
            }

            if (details.uninstalled != null) {
                details.uninstalledTakesRule = true;
            } else {
                details.uninstalled = lookupLifecycleMethod(helperClass, UNINSTALLED_NAME, UNINSTALLED_STRING_SIGNATURE);
            }
            helperDetailsMap.put(helperClass, details);
        }

        return details;
    }

    /**
     * return a static public method with the given parameter types it exists otherwise null
     * @param name
     * @param paramTypes
     * @return the method if found otherwise null
     */
    private Method lookupLifecycleMethod(Class<?> clazz, String name, Class<?>[] paramTypes)
    {
        try {
            Method m = clazz.getMethod(name, paramTypes);
            int mod = m.getModifiers();
            if (Modifier.isStatic(mod)) {
                return m;
            }
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    /**
     * purge the details describing the lifecycle methods of a helper class. this must only be
     * called when synchronized on the helper class.
     * @param details
     */
    private void purgeDetails(LifecycleDetails details)
    {
        helperDetailsMap.remove(details.lifecycleClass);
    }
}
