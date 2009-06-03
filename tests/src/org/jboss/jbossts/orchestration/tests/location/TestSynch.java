package org.jboss.jbossts.orchestration.tests.location;

import org.jboss.jbossts.orchestration.tests.Test;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestEntryExitAuxiliary;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestEntryExitAuxiliarySub;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestCallThrowSynchAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestSynch extends Test
{
    public TestSynch()
    {
        super(TestSynch.class.getCanonicalName());
    }

    public void test()
    {
        try {
        TestCallThrowSynchAuxiliary testAuxiliary;
        log("creating TestCallThrowSynchAuxiliary");
        testAuxiliary = new TestCallThrowSynchAuxiliary(this);
        log("created TestCallThrowSynchAuxiliary");
        log("calling TestCallThrowSynchAuxiliary.testMethod");
        testAuxiliary.testMethod();
        log("called TestCallThrowSynchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestCallThrowSynchAuxiliary");
        logExpected("inside TestCallThrowSynchAuxiliary(Test)");
        logExpected("created TestCallThrowSynchAuxiliary");
        logExpected("calling TestCallThrowSynchAuxiliary.testMethod");
        logExpected("inside TestCallThrowSynchAuxiliary.testMethod");
        // we should see trace from the first synchronize after printing the counter
        logExpected("1: currentCounter == 0");
        logExpected("SYNCHRONIZE triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("2: currentCounter == 1");
        // we should see trace from the first synchronize after printing the 2nd counter
        // and before printing the 3rd counter
        logExpected("SYNCHRONIZE 2 triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("3: currentCounter == 2");
        logExpected("4: currentCounter == 2");
        logExpected("called TestCallThrowSynchAuxiliary.testMethod");

        return super.getExpected();
    }
}