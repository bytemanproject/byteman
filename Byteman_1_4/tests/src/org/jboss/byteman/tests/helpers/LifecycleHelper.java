package org.jboss.byteman.tests.helpers;

import org.jboss.byteman.rule.Rule;

/**
 */
public class LifecycleHelper extends Default
{
    public LifecycleHelper(Rule rule) {
        super(rule);
    }

    static StringBuffer output = new StringBuffer();

    public void log(String string)
    {
        output.append(string);
        output.append('\n');
    }

    public static void logShared(String string)
    {
        output.append(string);
        output.append('\n');
    }

    public static String getOutput()
    {
        String result = output.toString();
        output = new StringBuffer();

        return result;
    }

    public static void activated()
    {
        output.append("activated " + LifecycleHelper.class.getName());
        output.append('\n');
    }

    public static void deactivated()
    {
        output.append("deactivated " + LifecycleHelper.class.getName());
        output.append('\n');
    }

    public static void installed(Rule rule)
    {
        output.append("installed " + rule.getName());
        output.append('\n');
    }

    public static void uninstalled(Rule rule)
    {
        output.append("uninstalled " + rule.getName());
        output.append('\n');
    }
}
