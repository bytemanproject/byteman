package org.jboss.jbossts.orchestration.agent;

/**
 * information about a single rule derived from a rule script
 */

public class Script
{
    private String name;
    private String targetClass;
    private String targetMethod;
    private String targetHelper;
    private Location targetLocation;
    private String ruleText;

    Script (String name, String targetClass, String targetMethod, String targetHelper, Location targetLocation, String ruleText)
    {
        this.name = name;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.targetHelper = targetHelper;
        this.targetLocation = targetLocation;
        this.ruleText = ruleText;
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
}
