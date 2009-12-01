package org.jboss.byteman.agent;

import org.jboss.byteman.rule.type.TypeHelper;
import org.jboss.byteman.rule.Rule;

import java.util.HashMap;

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

    public TransformContext(RuleScript ruleScript, String triggerClass, String targetMethodSpec, ClassLoader loader)
    {
        this.ruleScript =  ruleScript;
        this.triggerClass =  triggerClass;
        this.targetMethodName = TypeHelper.parseMethodName(targetMethodSpec);
        this.targetDescriptor = TypeHelper.parseMethodDescriptor(targetMethodSpec);
        this.loader = loader;
        this.ruleMap = new HashMap<String, Rule>();

    }

    public void parseRule() throws Throwable
    {
        try {
            Rule rule = Rule.create(ruleScript, loader);
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
                rule = Rule.create(ruleScript, loader);
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
     * a hashmap indexing Rule instances using key classname.methodnameandsig@loaderhashcode. rules are
     * added to this map when they are created and removed when the transform is recorded as having
     * succeeded or failed. a method check adapter will create a rule when it begins a method scan and
     * a method trigger adpater will look it up in order to reuse it
     */
    private HashMap<String, Rule> ruleMap;

}
