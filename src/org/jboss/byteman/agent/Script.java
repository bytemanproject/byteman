package org.jboss.byteman.agent;

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
    int line;
    String file;

    Script (String name, String targetClass, String targetMethod, String targetHelper, Location targetLocation, String ruleText, int line, String file)
    {
        this.name = name;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.targetHelper = targetHelper;
        this.targetLocation = targetLocation;
        this.ruleText = ruleText;
        this.line = line;
        this.file = file;
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
}
