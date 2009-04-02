package org.jboss.jbossts.orchestration.tests.auxiliary;

import org.jboss.jbossts.orchestration.tests.Test;

/**
 * Auxiliary subclass used by entry and exit location test classes
 */
public class TestEntryExitAuxiliarySub extends TestEntryExitAuxiliary
{
    public TestEntryExitAuxiliarySub(Test test)
    {
        super(test);
        test.log("inside TestEntryExitAuxiliarySub(Test)");
    }

    public void testMethod()
    {
        test.log("inside TestEntryExitAuxiliarySub.testMethod");
        test.log("calling TestEntryExitAuxiliary.testMethod");
        super.testMethod();
        test.log("called TestEntryExitAuxiliary.testMethod");
    }
}
