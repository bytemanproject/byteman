package org.jboss.byteman.tests.helpers;

import org.jboss.byteman.tests.Test;

/**
 * default helper used in byteman unit tests providing simple logging capability
 */
public class Default
{
    public void log(String message)
    {
        System.out.println(message);
    }

    public void log(Test test, String message)
    {
        test.log(message);
    }
}
