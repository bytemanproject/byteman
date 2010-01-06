package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;

/**
 * class used to test binding of a return value in an AT EXIT rule
 */
public class TestReturnBinding extends Test
{
    public TestReturnBinding()
    {
        super(TestReturnBinding.class.getCanonicalName());
    }

    public void test()
    {
        String result;
        
        try {
            log("calling TestReturnBinding.triggerMethod(0)");
            result = triggerMethod(0);
            log("called TestReturnBinding.triggerMethod(0) ==> " + result);
            log("calling TestReturnBinding.triggerMethod(1)");
            result = triggerMethod(1);
            log("called TestReturnBinding.triggerMethod(1) ==> " + result);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public String triggerMethod(int i)
    {
        log("inside TestReturnBinding.triggerMethod()");
        if (i == 0) {
            return "expected " + i;
        } else {
            int j = i + 1;
            return "expected " +  j;
        }
    }

    @Override
    public String getExpected() {
        logExpected("calling TestReturnBinding.triggerMethod(0)");
        logExpected("inside TestReturnBinding.triggerMethod()");
        logExpected("triggerMethod : triggered with expected 0");
        logExpected("called TestReturnBinding.triggerMethod(0) ==> unexpected");
        logExpected("calling TestReturnBinding.triggerMethod(1)");
        logExpected("inside TestReturnBinding.triggerMethod()");
        logExpected("triggerMethod : triggered with expected 2");
        logExpected("called TestReturnBinding.triggerMethod(1) ==> unexpected");

        return super.getExpected();
    }
}
