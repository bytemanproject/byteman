package org.jboss.jbossts.orchestration.tests.location;

import org.jboss.jbossts.orchestration.tests.Test;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestEntryExitAuxiliary;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestEntryExitAuxiliarySub;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestCallThrowSynchAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestCall extends Test
{
    public TestCall()
    {
        super(TestCall.class.getCanonicalName());
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
        // we should see trace from the first call to getCounter before printing the counter
        logExpected("CALL getCounter triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("1: currentCounter == 0");
        // we should see trace from the call to setCounter and the second call to getCounter
        // before printing the counter
        logExpected("CALL setCounter triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("CALL getCounter 2 triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("2: currentCounter == 1");
        // we should see trace from the second call to setCounter and the third call to getCounter
        // before printing the counter
        logExpected("CALL setCounter 2 triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("CALL getCounter 3 triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("3: currentCounter == 2");
        logExpected("CALL setCounter 3 triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("called TestCallThrowSynchAuxiliary.testMethod");

        return super.getExpected();
    }
}