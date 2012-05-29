/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.byteman.tests;

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
            fail("Test " + name + "failure\n" + "\n<expected>\n" + expected + "</expected>\n\n<log>\n" + output +"</log>\n");
        } else {
            // System.out.println("Test " + name + " success" + "\n\n<log>\n" + output + "</log>\n");
            System.out.println("Test " + name + " success");
        }

        if (reset) {
            this.output = new StringBuffer();
            this.expected = new StringBuffer();
        }
    }

    // variant of method checkOutput which ensures that all expected
    // lines occur in the correct order in the actual output but
    // tolerates extra lines in output which are not present in
    // expected.
    public void checkOutputPartial(boolean reset)
    {
        String output = getOutput();
        String expected = getExpected();
    	String[] outputLines = output.split("\n");
        String[] expectedLines = expected.split("\n");
        int outlen = outputLines.length;
        int explen = expectedLines.length;
        int outidx = 0;
        int expidx = 0;
        while (expidx < explen) {
            boolean matched = false;
            while (outidx < outlen && !matched) {
                if (outputLines[outidx++].equals(expectedLines[expidx])) {
                    matched = true;
                }
            }
            if (matched) {
                expidx++;
            } else {
                break;
            }
        }

        if (expidx < explen) {
            fail("Test " + name + "failure\n" + "\n<expectedPartial>\n" + expected + "</expectedPartial>\n\n<log>\n" + output +"</log>\n");
        } else {
            System.out.println("Test " + name + " success");
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
