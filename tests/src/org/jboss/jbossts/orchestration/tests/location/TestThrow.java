package org.jboss.jbossts.orchestration.tests.location;

import org.jboss.jbossts.orchestration.tests.Test;
import org.jboss.jbossts.orchestration.tests.auxiliary.TestCallThrowSynchAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestThrow extends Test
{
    public TestThrow()
    {
        super(TestThrow.class.getCanonicalName());
    }

    private int runNumber;

    public void test()
    {
        runNumber = 1;

        try {
            TestCallThrowSynchAuxiliary testAuxiliary;
            log("creating TestCallThrowSynchAuxiliary");
            testAuxiliary = new TestCallThrowSynchAuxiliary(this);
            log("created TestCallThrowSynchAuxiliary");
            testAuxiliary.counter = 1;
            log("assigned TestCallThrowSynchAuxiliary.counter = 1");
            log("calling TestCallThrowSynchAuxiliary.testMethod");
            testAuxiliary.testMethod();
            log("called TestCallThrowSynchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e, true);
        }

        checkOutput(true);

        runNumber = 2;
        
        try {
            TestCallThrowSynchAuxiliary testAuxiliary;
            log("creating TestCallThrowSynchAuxiliary");
            testAuxiliary = new TestCallThrowSynchAuxiliary(this);
            log("created TestCallThrowSynchAuxiliary");
            testAuxiliary.counter = 2;
            log("assigned TestCallThrowSynchAuxiliary.counter = 1");
            log("calling TestCallThrowSynchAuxiliary.testMethod");
            testAuxiliary.testMethod();
            log("called TestCallThrowSynchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e, true);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestCallThrowSynchAuxiliary");
        logExpected("inside TestCallThrowSynchAuxiliary(Test)");
        logExpected("created TestCallThrowSynchAuxiliary");
        logExpected("assigned TestCallThrowSynchAuxiliary.counter = 1");
        logExpected("calling TestCallThrowSynchAuxiliary.testMethod");
        logExpected("inside TestCallThrowSynchAuxiliary.testMethod");
        if (runNumber == 1) {
            logExpected("1: currentCounter == 1");
            logExpected("THROW 1 triggered in TestCallThrowSynchAuxiliary.testMethod");
            logExpected(new Exception("counter == 1"));
        }
        if (runNumber == 2) {
            logExpected("1: currentCounter == 2");
            logExpected("2: currentCounter == 3");
            logExpected("THROW 2 triggered in TestCallThrowSynchAuxiliary.testMethod");
            logExpected(new Exception("counter == 4"));
        }

        return super.getExpected();
    }
}