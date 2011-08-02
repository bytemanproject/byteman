package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Auxiliary class for BYTEMAN-156 which tests throw injection when an implementing class does
 * not throw an exception declared by a super class or super interface
 */
public class TestThrowRuleSuper extends Test implements TestThrowSuperInterface
{
    public TestThrowRuleSuper(String name)
    {
        super(name);
    }

    public void throwMethod3()
    {
    }

    public void throwMethod4() throws Exception
    {
    }
}
