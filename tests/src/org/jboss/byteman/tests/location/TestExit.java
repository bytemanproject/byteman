package org.jboss.byteman.tests.location;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliary;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliarySub;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestExit extends Test
{
    public TestExit()
    {
        super(TestExit.class.getCanonicalName());
    }

    public void test()
    {
        try {
        TestEntryExitAuxiliary testAuxiliary;
        log("creating TestEntryExitAuxiliarySub");
        testAuxiliary = new TestEntryExitAuxiliarySub(this);
        log("created TestEntryExitAuxiliarySub");
        log("calling TestEntryExitAuxiliarySub.testMethod");
        testAuxiliary.testMethod();
        log("called TestEntryExitAuxiliarySub.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestEntryExitAuxiliarySub");
        // super constructor should log first then constructor body then injected EXIT code
        logExpected("inside TestEntryExitAuxiliary(Test)");
        logExpected("inside TestEntryExitAuxiliarySub(Test)");
        logExpected("EXIT triggered in constructor");
        logExpected("created TestEntryExitAuxiliarySub");
        // body of subclass should log then body of superclass method
        // then injected EXIT code in superclass method then
        // injected ENTRY code in subclass method code
        logExpected("calling TestEntryExitAuxiliarySub.testMethod");
        logExpected("inside TestEntryExitAuxiliarySub.testMethod");
        logExpected("calling TestEntryExitAuxiliary.testMethod");
        logExpected("inside TestEntryExitAuxiliary.testMethod");
        logExpected("EXIT triggered in TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliary.testMethod");
        logExpected("EXIT triggered in TestEntryExitAuxiliarySub.testMethod");
        logExpected("called TestEntryExitAuxiliarySub.testMethod");

        return super.getExpected();
    }
}