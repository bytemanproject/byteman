package testhelper;

import byteman.tests.Test;

import runner.Runner;

public class TestHelper extends Test
{
    public TestHelper() {
        super(TestHelper.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling TestHelper.triggerMethod");
            triggerMethod();
            log("called TestHelper.triggerMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod()
    {
        
    }

    @Override
    public String getExpected() {
        logExpected("calling TestHelper.triggerMethod");
        logExpected("ModularHelper.logVia");
        logExpected("called TestHelper.triggerMethod");

        return super.getExpected();
    }
}
