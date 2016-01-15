package testimport;

import byteman.tests.Test;

public class TestRunnable implements Runnable
{
    private final Test ti;

    public TestRunnable(Test ti)
    {
        this.ti = ti;
    }

    public void run()
    {
        ti.log("Runnable.run");
    }

    public void log(String msg)
    {
        ti.log(msg);
    }
}
