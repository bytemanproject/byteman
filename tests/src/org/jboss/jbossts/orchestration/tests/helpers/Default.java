package org.jboss.jbossts.orchestration.tests.helpers;

import org.jboss.jbossts.orchestration.tests.Test;

/**
 * default helper used in TOAST unit tests providing simple logging capability
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
