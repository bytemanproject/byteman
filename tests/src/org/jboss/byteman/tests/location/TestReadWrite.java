package org.jboss.byteman.tests.location;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestReadWriteAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestReadWrite extends Test
{
    public TestReadWrite()
    {
        super(TestReadWrite.class.getCanonicalName());
    }

    public void test()
    {
        try {
            TestReadWriteAuxiliary testAuxiliary;
            log("creating TestReadWriteAuxiliary");
            testAuxiliary = new TestReadWriteAuxiliary(this);
            log("created TestReadWriteAuxiliary");
            log("calling TestReadWriteAuxiliary.testMethod");
            testAuxiliary.testMethod();
            log("called TestReadWriteAuxiliary.testMethod");
        } catch (Exception e) {
            log(e, true);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestReadWriteAuxiliary");
        logExpected("inside TestReadWriteAuxiliary(Test)");
        logExpected("created TestReadWriteAuxiliary");
        logExpected("calling TestReadWriteAuxiliary.testMethod");
        logExpected("inside TestReadWriteAuxiliary.testMethod");
        logExpected("AT READ 1 triggered in TestReadWriteAuxiliary.testMethod : counter == 0");
        logExpected("1: currentCounter == 0");
        logExpected("AT WRITE 1 triggered in TestReadWriteAuxiliary.testMethod : counter == 0");
        logExpected("2: currentCounter == 1");
        logExpected("AFTER READ 2 triggered in TestReadWriteAuxiliary.testMethod : counter == 1");
        // AFTER
        logExpected("AFTER WRITE 2 triggered in TestReadWriteAuxiliary.testMethod : counter == 2");
        logExpected("3: currentCounter == 2");
        logExpected("called TestReadWriteAuxiliary.testMethod");

        return super.getExpected();
    }
}