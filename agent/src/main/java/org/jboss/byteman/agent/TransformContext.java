/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-10, Red Hat and individual contributors
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
 * (C) 2009-10,
 * @authors Andrew Dinn
 */
package org.jboss.byteman.agent;

import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.Rule;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Class used to localise the context information employed when creating a rule from a rule script and
 * using it to transform a method
 */
public class TransformContext
{
    private RuleScript ruleScript;
    private String triggerClass;
    private String targetMethodName;
    private String targetDescriptor;
    private ClassLoader loader;
    HelperManager helperManager;

    public TransformContext(RuleScript ruleScript, String triggerClass, String targetMethodSpec, ClassLoader loader, HelperManager helperManager)
    {
        // the target method spec may just be a bare method name or it may optionally include a
        // parameter type list and a return type. With Java syntax the return type appears before
        // the method name. if so we modify the target method spec so that the return type appears
        // after the argument list which means we also accept a spec supplied in this format. The
        // parseMethodDescriptor call below will eat specs in this latter format.
        String mungedMethodSpec = mungeMethodSpecReturnType(targetMethodSpec);
        this.ruleScript =  ruleScript;
        this.triggerClass =  triggerClass;
        this.targetMethodName = TypeHelper.parseMethodName(mungedMethodSpec);
        this.targetDescriptor = TypeHelper.parseMethodDescriptor(mungedMethodSpec);
        this.loader = loader;
        this.ruleMap = new HashMap<String, Rule>();
        this.helperManager = helperManager;

    }

    public void parseRule() throws Throwable
    {
        try {
            Rule rule = Rule.create(ruleScript, loader, helperManager);
            // stash this rule away under the class name so we can reuse it for the first matching method
            ruleMap.put(triggerClass, rule);
        } catch (Throwable th) {
            recordFailedTransform(th);
            throw th;
        }
    }

    public Rule getRule(String triggerMethodName, String triggerMethodDescriptor)
    {
        String key = getRuleKey(triggerMethodName, triggerMethodDescriptor);
        Rule rule = ruleMap.get(key);
        if (rule != null) {
            return rule;
        }

        // no existign rule -- use the initially parsed rule if we can otherwise create one

        rule = ruleMap.remove(triggerClass);

        if (rule == null) {
            try {
                rule = Rule.create(ruleScript, loader, helperManager);
            } catch(Throwable th) {
                //  will not happen
            }
        }

        ruleMap.put(key, rule);

        return rule;
    }

    public void recordFailedTransform(Throwable th)
    {
        ruleScript.recordFailedTransform(loader, triggerClass, th);

        //  this gets called when a transform attempt fails. if there is a rule left in the rule map
        // then it will belong to the failed transform so it needs  to be purged.

        Iterator<Rule> iterator = ruleMap.values().iterator();

        for (Rule rule : ruleMap.values()) {
            rule.purge();
        }

        ruleMap.clear();
    }

    public void recordMethodTransform(String triggerMethodName, String triggerMethodDescriptor)
    {
        Rule rule = removeRule(triggerMethodName, triggerMethodDescriptor);

        ruleScript.recordMethodTransform(loader, triggerClass, triggerMethodName, triggerMethodDescriptor, rule);
    }

    public boolean matchTargetMethod(String name, String desc)
    {
        return (targetMethodName.equals(name) &&
                (targetDescriptor.equals("") || TypeHelper.equalDescriptors(targetDescriptor, desc)));
    }

    public String getTriggerClass()
    {
        return triggerClass;
    }
    
    private Rule removeRule(String triggerMethodName, String triggerMethodDescriptor)
    {
        return ruleMap.remove(getRuleKey(triggerMethodName, triggerMethodDescriptor));
    }

    /**
     * return a unique string key identifying a specific rule compiled against some class and method/signature in the
     * context of a specific class loader
     * @return
     */
    private String getRuleKey(String triggerMethodName, String triggerMethodDescriptor)
    {
            return triggerClass + "." + triggerMethodName + TypeHelper.internalizeDescriptor(triggerMethodDescriptor);
    }

    /**
     * pattern used to identify target method specs which include a return type preceding the
     * method name and parameter type list. note that we can only handle a return type in
     * cases where the parameter type list is also specified.
     */
    private static final String JAVA_METHOD_SPEC_PATTERN = "[A-Za-z0-9$.]+ +[A-Za-z0-9$]+\\(.*\\)";

    /**
     * detect a method specification which includes a return type preceding the method name and transform
     * it so that the return type is at the end.
     * @param targetMethodSpec
     * @return
     */
    private String mungeMethodSpecReturnType(String targetMethodSpec)
    {
        // remove any leading or trailing spaces
        targetMethodSpec = targetMethodSpec.trim();
        if (targetMethodSpec.matches(JAVA_METHOD_SPEC_PATTERN)) {
            // put the return type at the end
            int spaceIdx = targetMethodSpec.indexOf(' ');
            String returnType = targetMethodSpec.substring(0, spaceIdx);
            targetMethodSpec = targetMethodSpec.substring(spaceIdx).trim() + returnType;
        }
        return targetMethodSpec;
    }

    /**
     * a hashmap indexing Rule instances using key classname.methodnameandsig@loaderhashcode. rules are
     * added to this map when they are created and removed when the transform is recorded as having
     * succeeded or failed. a method check adapter will create a rule when it begins a method scan and
     * a method trigger adpater will look it up in order to reuse it
     */
    private HashMap<String, Rule> ruleMap;

}
