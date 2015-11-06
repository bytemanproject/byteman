package testimport;

import byteman.tests.Test;

import runner.Runner;

public class TestImport extends Test
{
    public TestImport() {
        super(TestImport.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling TestImport.triggerMethod");
            triggerMethod();
            log("called TestImport.triggerMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod()
    {
        Runnable r = new TestRunnable(this);
        Runner.run(r);
    }

    @Override
    public String getExpected() {
        logExpected("calling TestImport.triggerMethod");
        logExpected("TestImport $runnable");
        logExpected("Runnable.run");
        logExpected("called TestImport.triggerMethod");

        return super.getExpected();
    }
}
