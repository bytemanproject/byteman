package testimport;

public class TestRunnable implements Runnable
{
    private final TestImport ti;

    public TestRunnable(TestImport ti)
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
