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

import org.jboss.byteman.agent.adapter.BMJSRInliner;
import org.jboss.byteman.agent.adapter.BMLocalScopeAdapter;
import org.jboss.byteman.agent.adapter.RuleCheckAdapter;
import org.jboss.byteman.agent.adapter.RuleTriggerAdapter;
import org.jboss.byteman.agent.check.ClassChecker;
import org.jboss.byteman.rule.exception.CompileException;
import org.jboss.byteman.rule.exception.ParseException;
import org.jboss.byteman.rule.exception.TypeException;
import org.jboss.byteman.rule.exception.TypeWarningException;
import org.jboss.byteman.rule.type.Type;
import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.Rule;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class used to localise the context information employed when creating a rule from a rule script and
 * using it to transform a method
 */
public class TransformContext
{
    public TransformContext(Transformer transformer, RuleScript ruleScript, String triggerClassName, ClassLoader loader, HelperManager helperManager)
    {
        // the target method spec may just be a bare method name or it may optionally include a
        // parameter type list and a return type. With Java syntax the return type appears before
        // the method name. if so we modify the target method spec so that the return type appears
        // after the argument list which means we also accept a spec supplied in this format. The
        // parseMethodDescriptor call below will eat specs in this latter format.
        final String targetMethodSpec = ruleScript.getTargetMethod();
        String mungedMethodSpec = mungeMethodSpecReturnType(targetMethodSpec);
        this.transformer = transformer;
        this.ruleScript =  ruleScript;
        this.triggerClassName = triggerClassName;
        this.targetMethodName = TypeHelper.parseMethodName(mungedMethodSpec);
        this.targetDescriptor = TypeHelper.parseMethodDescriptor(mungedMethodSpec);
        this.loader = loader;
        this.helperManager = helperManager;
        this.ruleMap = new HashMap<String, Rule>();
        this.firstRule = null;


    }

    public byte[] transform(byte[] targetClassBytes)
    {
        final Location handlerLocation = ruleScript.getTargetLocation();

        String ruleName = ruleScript.getName();
        try {
            parseRule();
        } catch (ParseException pe) {
            if (Transformer.isVerbose()) {
                System.out.println("org.jboss.byteman.agent.Transformer : error parsing rule " + ruleName + "\n" + pe);
            }
            recordFailedTransform(pe);
            return targetClassBytes;
        } catch (Throwable th) {
            if (Transformer.isVerbose()) {
                System.out.println("org.jboss.byteman.agent.Transformer : unexpected error parsing rule " + ruleName + "\n" + th);
            }
            recordFailedTransform(th);
            return targetClassBytes;
        }

        // ok, we have a rule with a matching trigger class and a target method and location
        // we need to see if the class has a matching trigger method/location and, if so, add a
        // call to execute the rule when we hit the relevant line

        // there may be more than one matching method. if so we need to associate a separate Rule
        // instance with each transformed method because we bind the argument/local vars using types
        // specific to that method

        // we may have to ignore certain matches because they don't fit the type spec for
        // the rule. if we can queue a type warning exception and carry on injecting other
        // trigger locations then we do so. if not we throw a type exception and invalidate
        // all injection for the trigger class. it would be better if we could just byapss
        // a single injection point or just the injection points in the offending method
        // but sometimes we can only back out by throwing an exception from within a bytecode
        // visitor and th eonly safe ting to do is back out the whole transform.

        ClassReader cr = new ClassReader(targetClassBytes);
        // need to provide a real writer here so that labels get resolved
        ClassWriter dummy = getNonLoadingClassWriter(0);
        RuleCheckAdapter checkAdapter = handlerLocation.getRuleCheckAdapter(dummy, this);
        try {
            // insert a local scope adapter between the reader and the adapter so
            // we see info about vars going in and out of scope
            BMLocalScopeAdapter localScopeAdapter = new BMLocalScopeAdapter(checkAdapter);
            cr.accept(localScopeAdapter, ClassReader.EXPAND_FRAMES);
        } catch (TransformFailure te) {
            // will already be notified
            return targetClassBytes;
        } catch (Throwable th) {
            // hmm, unexpected error
            if (Transformer.isVerbose()) {
                System.out.println("org.jboss.byteman.agent.Transformer : unexpected error applying rule " + ruleScript.getName() + " to class " + triggerClassName + "\n" + th);
                th.printStackTrace(System.out);
            }
            recordFailedTransform(th);
            return targetClassBytes;
        }
        // only insert the rule trigger call if there is a suitable location in the target method
        if (!checkAdapter.isVisited()) {
            //  there was no matching method so ignore
            return targetClassBytes;
        }

        if (Transformer.isVerbose()) {
            System.out.println("org.jboss.byteman.agent.Transformer : possible trigger for rule " + ruleScript.getName() + " in class " + triggerClassName);
        }
        cr = new ClassReader(targetClassBytes);
        ClassWriter cw = getNonLoadingClassWriter(ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
        RuleTriggerAdapter adapter = handlerLocation.getRuleAdapter(cw, this);
        // insert a JSR inliner between the reader and the adapter so we don't see JSR/RET sequences
        // we use a specialised version which provides us with info about vars going in and out of scope
        BMJSRInliner jsrInliner = new BMJSRInliner(adapter);
        try {
            cr.accept(jsrInliner, ClassReader.EXPAND_FRAMES);
        } catch (TransformFailure te) {
            // will already be notified
            return targetClassBytes;
        } catch (Throwable th) {
            if (Transformer.isVerbose()) {
                System.out.println("org.jboss.byteman.agent.Transformer : unexpected error injecting trigger for rule " + ruleScript.getName() + " into class " + triggerClassName + "\n" +  th);
                th.printStackTrace(System.out);
            }
            recordFailedTransform(th);
            return targetClassBytes;
        }
        // hand back the transformed byte code
        if (Transformer.isVerbose()) {
            System.out.println("org.jboss.byteman.agent.Transformer : inserted trigger for " + ruleScript.getName() + " in class " + triggerClassName);
        }

        // record all successfully transformed rules

        if (!notifyRules()) {
            // rule must have been deleted so forget the transform
            return targetClassBytes;
        } else {
            return cw.toByteArray();
        }
    }

    public void parseRule() throws Exception {
        Rule rule = Rule.create(ruleScript, loader, helperManager);
        // stash this rule away under the class name so we can reuse it for the first matching method
        ruleMap.put(triggerClassName, rule);
        // keep a handle on the first rule
        firstRule = rule;
    }

    /**
     * called by a trigger adapter to find a rule specific to a given trigger method,
     * expects to find a rule created by the corresponding check adapter. if no rule is
     * found then injection must be bypassed for this method
     * @param triggerMethodName the name of a candidate method for injection
     * @param triggerMethodDescriptor the descriptor of a candidate method for injection
     * @return the rule if it exists or NULL if not
     */
    public Rule lookupRule(String triggerMethodName, String triggerMethodDescriptor)
    {
        String key = getRuleKey(triggerMethodName, triggerMethodDescriptor);
        return ruleMap.get(key);
    }

    /**
     * called by a check adapter to create a rule specific to a given trigger method.
     * the first such call reuses the rule created by the intiial parse. subsequent calls
     * create a new rule.
     * @param triggerMethodName the name of a candidate method for injection
     * @param triggerMethodDescriptor the descriptor of a candidate method for injection
     * @return the new rule
     */
    public Rule createRule(String triggerMethodName, String triggerMethodDescriptor)
    {
        String key = getRuleKey(triggerMethodName, triggerMethodDescriptor);

        // use the initially parsed rule if we can otherwise create one

        Rule rule = ruleMap.remove(triggerClassName);

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

    /**
     * called by a check adapter to warn that a transform was not possible for a potential match
     * target. this inhibits injection into the method being warned about allowing other injection
     * operations to continue.
     * @param triggerMethodName the name of a candidate method for injection
     * @param triggerMethodDescriptor the descriptor of a candidate method for injection
     * @param warningMessage details of the warning
     */
    public void warn(String triggerMethodName, String triggerMethodDescriptor, String warningMessage)
    {
        // remove the rule so that we don't try to inject into this method
        String key = getRuleKey(triggerMethodName, triggerMethodDescriptor);
        Rule rule = ruleMap.remove(key);
        // now attach an exception to the rule script
        String message = warningMessage + " for method " + triggerMethodName + TypeHelper.internalizeDescriptor(triggerMethodDescriptor);
        TypeWarningException tw = new TypeWarningException(message);
        ruleScript.recordTransform(loader, triggerClassName, triggerMethodName, triggerMethodDescriptor, rule, tw);
    }

    /**
     * called by a check or trigger  adapter to fail a transform because of a type issue. this aborts all
     * injection into the current class not just injection into the current method.
     * @param failMessage details of the failure
     * @param triggerMethodName the name of a candidate method for injection
     * @param triggerMethodDescriptor the descriptor of a candidate method for injection
     */
    public void fail(String failMessage, String triggerMethodName, String triggerMethodDescriptor)
    {
        String key = getRuleKey(triggerMethodName, triggerMethodDescriptor);
        Rule rule = ruleMap.get(key);
        String message = failMessage + " for method " + triggerMethodName + TypeHelper.internalizeDescriptor(triggerMethodDescriptor);
        TypeException te = new TypeException(message);
        ruleScript.recordTransform(loader, triggerClassName, triggerMethodName, triggerMethodDescriptor, rule, te);

        purgeRules();
        throw new TransformFailure();
    }

    public void recordFailedTransform(Throwable th)
    {
        ruleScript.recordFailedTransform(loader, triggerClassName, th);

        purgeRules();
    }

    public boolean matchTargetMethod(int access, String name, String desc)
    {
        // check the method is one we are really targeting
    	/*
        if ((access & (Opcodes.ACC_NATIVE|Opcodes.ACC_ABSTRACT|Opcodes.ACC_SYNTHETIC)) != 0 ||
                !targetMethodName.equals(name) ||
                (!targetDescriptor.equals("") && !TypeHelper.equalDescriptors(targetDescriptor, desc))) {
            return false;
        }
        */
    	if (
    			(access & (Opcodes.ACC_NATIVE|Opcodes.ACC_ABSTRACT|Opcodes.ACC_SYNTHETIC)) != 0
    			||	!(
    					(!targetMethodName.startsWith("^") && targetMethodName.equals(name)) 
    					|| (targetMethodName.startsWith("^") && name.matches(targetMethodName))
    				)
    			||	(!targetDescriptor.equals("") && !TypeHelper.equalDescriptors(targetDescriptor, desc))
    		) {
    		return false;
    	}
    	
        // if the method is blacklisted then reject it with a warning
        if (transformer.isBlacklisted(triggerClassName, targetMethodName, desc)) {
            warn(targetMethodName, targetDescriptor, "Blacklisted method : cannot safely inject into target class " + triggerClassName);
            return false;
        }

        // yes we do want to inject
        return true;
    }

    public boolean injectIntoMethod(String name, String desc)
    {
        return lookupRule(name, desc) != null;
    }


    public String getTriggerClassName()
    {
        return triggerClassName;
    }

    /**
     * private exception class used to throw our way out of the ASM adapter code back into the transform
     * method at the top level. we have to use a RuntimeException for this as we cannot change the ASm
     * API to allow opther exception types to be plumbed through the code
     */
    private class TransformFailure extends RuntimeException
    {
        public TransformFailure()
        {
        }
    }

    /**
     *  this gets called when a transform attempt completes without any exceptions. if there are rules left
     *  in the rule map then they will belong successful injections.
     */
    private boolean notifyRules()
    {
        // if we got here then we have performed a successful injection for each rule in the rule map
        // if the map is empty then we ned to generate a warning that the rule was not injectable

        if (ruleMap.isEmpty() && firstRule != null) {
            // we parsed the rule but failed ever to inject it
            TypeWarningException twe = new TypeWarningException("failed to find any matching trigger method in class " + TypeHelper.internalizeClass(triggerClassName));
            ruleScript.recordTransform(loader, triggerClassName, null, null, firstRule, twe);
        }

        for (String key : ruleMap.keySet()) {
            String triggerMethodName = getKeyTriggerMethodName(key);
            String triggerMethodDescriptor = getKeyTriggerMethodDescriptor(key);
            Rule rule = ruleMap.get(key);
            if (!ruleScript.recordTransform(loader, triggerClassName, triggerMethodName, triggerMethodDescriptor, rule, null))
            {
                // rule script must have been deleted so purge rules and avoid installing the transformed code
                purgeRules();

                return false;
            }

        }

        // ok install the transformed code

        return true;
    }

    /**
     *  this gets called when a transform attempt fails. if there are rules left in the rule map
     *  then they will belong either to earlier successful injections or to the failed transform.
     *  in any case they need to be purged.
     */
    private void purgeRules()
    {
        for (Rule rule : ruleMap.values()) {
            rule.purge();
        }
    }

    /**
     * return a unique string key identifying a specific rule compiled against some class and method/signature in the
     * context of a specific class loader
     * @return a unique string key
     */
    private String getRuleKey(String triggerMethodName, String triggerMethodDescriptor)
    {
            return triggerClassName + "#" + triggerMethodName + "#" + triggerMethodDescriptor;
    }

    /**
     * return the trigger method name used to construct the supplied rule key
     * @param key
     * @return the trigger method name
     */
    private String getKeyTriggerMethodName(String key)
    {
        int firstHash = key.indexOf('#');
        int secondHash = key.lastIndexOf('#');
        return key.substring(firstHash + 1, secondHash);
    }

    /**
     * return the trigger method descriptor used to construct the supplied rule key
     * @param key
     * @return the trigger method descriptor
     */
    private String getKeyTriggerMethodDescriptor(String key)
    {
        int secondHash = key.lastIndexOf('#');
        return key.substring(secondHash + 1);
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
     * @return the method spec in the desired format
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
     * get a class writer which will not attempt to load classes. The default classwriter tries this when a
     * reference type local var frame slot aligns with a slot of reference type in a successor block's
     * frame. This is merely so it can optimize a slot out of the frame change set in the special case where
     * f1[slot].type < f2[slot].type or vice versa by using the least common supereclass. We have to use
     * the Transformer to reuse existing loaded classes and, where a class has not been loaded, to
     * attempt to load the bytecode as a resource and identify supers via the bytecode.
     *
     * @param flags
     * @return a non-loading class writer
     */
    private ClassWriter getNonLoadingClassWriter(int flags)
    {
        final TransformContext finalContext = this;
        return new ClassWriter(flags) {
        TransformContext context = finalContext;
            protected String getCommonSuperClass(final String type1, final String type2) {
                // if we always return Object we cannot go wrong
                return context.findLeastCommonSuper(type1, type2);
            }
        };
    }

    public final static String TOFU = "java/lang/Object";       // TOFU = top of universe

    public String findLeastCommonSuper(final String t1, final String t2)
    {
        // check the simple cases first

        if (TOFU.equals(t1) || TOFU.equals(t2)) {
            return TOFU;
        }
        if (t1.equals(t2)) {
            return t2;
        }
        // switch to canonical names containing "." instead of "/" when
        // checking against names found in bytecode but ensure the returned
        // name contains "/"

        String type1 = t1.replaceAll("/", ".");
        String type2 = t2.replaceAll("/", ".");
        ClassChecker checker1 = transformer.getClassChecker(type1, loader);

        if (checker1 == null) {
            return TOFU;
        }

        ClassChecker checker2 = transformer.getClassChecker(type2, loader);

        if (checker2 == null) {
            return TOFU;
        }

        if (checker1.isInterface()) {
            if (checker2.isInterface()) {
                // both are interfaces so find the first common parent interface
                // (including the original interfaces) or return Object
                LinkedList<String> interfaces2 = listInterfaces(checker2);
                if (interfaces2.contains(type1)) {
                    return t1;
                } else {
                    LinkedList<String> interfaces1 = listInterfaces(checker1);
                    while (!interfaces1.isEmpty()) {
                        String next = interfaces1.pop();
                        if (next.equals(type2)) {
                            return t2;
                        }
                        if (interfaces2.contains(next)) {
                            return next.replaceAll("\\.", "/");
                        }
                    }
                    return TOFU;
                }
            } else {
                // type1 is an interface but type2 is a class so return the
                // first parent interface of type2 which implements either type1 or
                // one of type1's parent interfaces or return Object
                LinkedList<String> interfaces2 = listInterfaces(checker2);
                if (interfaces2.contains(type1)) {
                    // type1 is an interface of type2
                    return t1;
                } else {
                    LinkedList<String> interfaces1 = listInterfaces(checker1);
                    while (!interfaces1.isEmpty()) {
                        String next = interfaces1.pop();
                        if (interfaces2.contains(next)) {
                            return next.replaceAll("\\.", "/");
                        }
                    }
                    return TOFU;
                }
            }
        } else {
            if (checker2.isInterface()) {
                // type2 is an interface but type1 is a class so return the
                // first parent interface of type1 which implements either type1 or
                // one of type1's parent interfaces or return Object
                LinkedList<String> interfaces1 = listInterfaces(checker1);
                if (interfaces1.contains(type2)) {
                    // type2 is an interface of type1
                    return t2;
                } else {
                    LinkedList<String> interfaces2 = listInterfaces(checker2);
                    while (!interfaces2.isEmpty()) {
                        String next = interfaces2.pop();
                        if (interfaces1.contains(next)) {
                            return next.replaceAll("\\.", "/");
                        }
                    }
                    return TOFU;
                }
            } else {
                // see if the classes have a common super class before Object
                LinkedList<String> supers2 = listSupers(checker2);
                if (supers2.contains(type1)) {
                    // type2 is a subclass of type1
                    return t1;
                } else {
                    LinkedList<String> supers1 = listSupers(checker1);
                    while (!supers1.isEmpty()) {
                        String next = supers1.pop();
                        if (next.equals(type2)) {
                            return t2;
                        }
                        if (supers2.contains(next)) {
                            return next.replaceAll("\\.", "/");
                        }
                    }
                    return TOFU;
                }
            }
        }
    }

    private LinkedList<String> listInterfaces(ClassChecker checker)
    {
        LinkedList<String> interfaces = new LinkedList<String>();
        ClassChecker current = checker;
        while (current != null) {
            LinkedList<String> toCheck = new LinkedList<String>();
            int count = current.getInterfaceCount();
            for (int i = 0; i < count; i++) {
                toCheck.add(current.getInterface(i));
            }
            // now recursively work through unchecked interfaces adding them
            // if not already processed and including the interfaces they extend
            // in the toCheck list.
            while (!toCheck.isEmpty()) {
                String next = toCheck.pop();
                if (!interfaces.contains(next)) {
                    interfaces.add(next);
                    ClassChecker newChecker = transformer.getClassChecker(next, loader);
                    if (newChecker != null) {
                        count = newChecker.getInterfaceCount();
                        for (int i = 0; i < count; i++) {
                            toCheck.add(newChecker.getInterface(i));
                        }
                    }
                }
            }
            // repeat for the next super in line up to java/lang/Object
            String superType = current.getSuper();
            if (superType == null || TOFU.equals(superType)) {
                current = null;
            } else {
                current = transformer.getClassChecker(superType, loader);
            }
        }

        return interfaces;
    }

    private LinkedList<String> listSupers(ClassChecker checker)
    {
        LinkedList<String> supers = new LinkedList<String>();
        ClassChecker current = checker;
        while (current != null) {
            String superType = current.getSuper();
            if (superType != null) {
                supers.add(superType);
                current = transformer.getClassChecker(superType, loader);
            } else {
                current = null;
            }
        }

        return supers;
    }

    private Transformer transformer;
    private RuleScript ruleScript;
    private String triggerClassName;
    private String targetMethodName;
    private String targetDescriptor;
    private ClassLoader loader;
    private HelperManager helperManager;

    /**
     * a hashmap indexing Rule instances using key classname.methodnameandsig@loaderhashcode. rules are
     * added to this map when they are created and removed when the transform is recorded as having
     * succeeded or failed. a method check adapter will create a rule when it begins a method scan and
     * a method trigger adpater will look it up in order to reuse it
     */
    private HashMap<String, Rule> ruleMap;

    private Rule firstRule;
}
