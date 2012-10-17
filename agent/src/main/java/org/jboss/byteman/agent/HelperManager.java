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
     * @param inst will be non-null if 
     */
    public HelperManager(Instrumentation inst)
    {
        this.inst = inst;
        this.helperDetailsMap = new ConcurrentHashMap<Class<?>, LifecycleDetails>();
    }

    public void installed(Rule rule)
    {
        Class helperClass = rule.getHelperClass();
        if (Transformer.isVerbose()) {
            System.out.println("HelperManager.install for helper class " + helperClass.getName());
        }
        // synchronize on the lifecycle class to ensure it is not uninstalled
        // while we are deciding whether or not to install it
        synchronized (helperClass) {
            LifecycleDetails details;
            details = getDetails(helperClass, true);
            if (details.installCount == 0 && details.activated != null) {
                if (Transformer.isVerbose()) {
                    System.out.println("calling activated() for helper class " + helperClass.getName());
                }
                try {
                    details.activated.invoke(null);
                } catch (Exception e) {
                    System.out.println("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".activate() : " + e);
                    e.printStackTrace();
                }
            }
            if (details.installed != null) {
                if (Transformer.isVerbose()) {
                    System.out.println("calling installed(" + rule.getName() + ") for helper class" + helperClass.getName());
                }
                try {
                    if (details.installedTakesRule) {
                        details.installed.invoke(null, rule);
                    } else {
                        details.installed.invoke(null, rule.getName());
                    }
                } catch (Exception e) {
                    System.out.println("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".installed(String) : " + e);
                    e.printStackTrace();
                }
            }
            details.installCount++;
        }
    }

    public void uninstalled(Rule rule)
    {
        Class helperClass = rule.getHelperClass();
        if (Transformer.isVerbose()) {
            System.out.println("HelperManager.uninstall for helper class " + helperClass.getName());
        }
        // synchronize on the lifecycle class to ensure it is not uninstalled
        // while we are deciding whether or not to install it
        synchronized (helperClass) {
            LifecycleDetails details;
            details = getDetails(helperClass, false);
            if (details == null) {
                System.out.println("HelperManager.uninstalled : shouldn't happen! uninstall failed to locate helper details for " + helperClass.getName());
                return;
            }
            details.installCount--;
            if (details.uninstalled != null) {
                if (Transformer.isVerbose()) {
                    System.out.println("calling uninstalled(" + rule.getName() + ") for helper class " + helperClass.getName());
                }
                try {
                    if (details.uninstalledTakesRule) {
                        details.uninstalled.invoke(null, rule);
                    } else {
                        details.uninstalled.invoke(null, rule.getName());
                    }
                } catch (Exception e) {
                    System.out.println("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".uninstalled(String) : " + e);
                    e.printStackTrace();
                }
            }
            if (details.installCount == 0 && details.deactivated != null) {
                if (Transformer.isVerbose()) {
                    System.out.println("calling deactivated() for helper class" + helperClass.getName());
                }
                try {
                    details.deactivated.invoke(null);
                } catch (Exception e) {
                    System.out.println("HelperManager.installed : unexpected exception from " + helperClass.getName() + ".deactivate() : " + e);
                    e.printStackTrace();
                }
            }
            if (details.installCount == 0) {
                purgeDetails(details);
            }
        }
    }

    /**
     * This method exposes a capability of the Byteman agent's
     * Instrumentation instance while avoding exposing the instance
     * itself. It returns an estimate of the object size or -1 in case
     * an agent has not been installed.
     */
    public long getObjectSize(Object o)
    {
        if (inst == null) {
            System.out.println("Cannot calculate object size since a Byteman agent has not been installed");
            return -1;
        }
        return this.inst.getObjectSize(o);
    }

    // private parts

    /**
     * the instrumentation object used to install the transformer. If this is null then we are not running in
     * a real agent so  we do no work.
     */
    private Instrumentation inst;

    /**
     * a hashmap from helper classes to their corresponding helper details. we don't use weak references here
     * because there is only ever an entry here if there is a rule installed which references the class. entries
     * get cleared when the rules are uninstalled.
     */
    private ConcurrentHashMap<Class<?>, LifecycleDetails> helperDetailsMap;

    /**
     * a record of a specific helper class tracking the number of installed rules which reference it
     * and referencing a table detailing the lifecycle methods it implements
     */
    private static class LifecycleDetails
    {
        /**
         * the helper class whose lifecycle this record details
         */
        public Class<?> lifecycleClass;
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

        public LifecycleDetails(Class<?> lifecycleClass)
        {
            this.lifecycleClass = lifecycleClass;
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
     * @return the relevant details
     */
    private LifecycleDetails getDetails(Class<?> helperClass, boolean createIfAbsent)
    {
        LifecycleDetails details = helperDetailsMap.get(helperClass);
        if (details == null && createIfAbsent) {
            details = new LifecycleDetails(helperClass);
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
     * @return
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
