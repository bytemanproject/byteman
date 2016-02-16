package testimport;

import org.jboss.byteman.rule.exception.TypeException;

import byteman.tests.Test;
import runner.Runner;

public class TestImportFailure extends Test
{
    public TestImportFailure()
    {
        super(TestImportFailure.class.getCanonicalName());
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
        // this will be missing, since the rule failed to compile: logExpected("TestImport $runnable");
        logExpected("Runnable.run");
        logExpected("called TestImport.triggerMethod");

        return super.getExpected();
    }
}
