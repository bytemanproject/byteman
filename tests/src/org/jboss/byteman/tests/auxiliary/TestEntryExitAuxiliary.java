package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Auxiliary class used by entry and exit location test classes
 */
public class TestEntryExitAuxiliary
{
    protected Test test;

    public TestEntryExitAuxiliary(Test test)
    {
        this.test = test;
        test.log("inside TestEntryExitAuxiliary(Test)");
    }

    public void testMethod()
    {
        test.log("inside TestEntryExitAuxiliary.testMethod");
    }

    public Test getTest()
    {
        return test;
    }
}
