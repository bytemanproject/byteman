package org.jboss.jbossts.orchestration.tests;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * generic test class extended by specific tests
 */
public abstract class Test extends TestCase
{
    protected String name;
    private StringBuffer output;
    private StringBuffer expected;

    public Test(String name)
    {
        this.name = name;
        this.output = new StringBuffer();
        this.expected = new StringBuffer();
    }

    public void log(String message)
    {
        output.append(message);
        output.append('\n');
    }

    public void log(Exception e)
    {
        log(e, false);
    }
    
    public void log(Exception e, boolean noTrace)
    {
        if (noTrace) {
            output.append(e.toString());
            output.append('\n');
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            output.append(sw.toString());
        }
    }

    public void checkOutput()
    {
        checkOutput(false);
    }
    
    public void checkOutput(boolean reset)
    {
        String output = getOutput();
        String expected = getExpected();
        if (!output.equals(expected)) {
            fail("Test " + name + "fail" + "\n\n<expected>\n" + expected + "</expected>\n\n<log>\n" + output +"</log>\n");
        } else {
            System.out.println("Test " + name + "success" + "\n\n<log>\n" + output + "</log>\n");
        }

        if (reset) {
            this.output = new StringBuffer();
            this.expected = new StringBuffer();
        }
    }

    public String getOutput()
    {
        return output.toString();
    }

    public void logExpected(String message)
    {
        expected.append(message);
        expected.append('\n');
    }

    public void logExpected(Exception e)
    {
        expected.append(e.toString());
        expected.append('\n');
    }

    public String getExpected()
    {
        return expected.toString();
    }
}
