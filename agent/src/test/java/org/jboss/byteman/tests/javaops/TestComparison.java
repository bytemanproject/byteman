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
package org.jboss.byteman.tests.javaops;

import org.jboss.byteman.tests.Test;

/**
 * Test to ensure comparison operations compute as expected
 */
public class TestComparison extends Test
{
    public TestComparison() {
        super(TestComparison.class.getCanonicalName());
    }

    static int runNumber = 0;

    public void test()
    {
        String stringRes;
        int intRes;

        runNumber = 1;
        try {
            log("calling TestComparison.triggerMethod1");
            stringRes = triggerMethod1("1233");
            log("called TestComparison.triggerMethod1 : result == " + stringRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestComparison.triggerMethod1");
            // ensure we use a different String object jsut to be sure that the compare ends up using equals
            stringRes = triggerMethod1(new String("1234"));
            log("called TestComparison.triggerMethod1 : result == " + stringRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestComparison.triggerMethod1");
            stringRes = triggerMethod1("1235");
            log("called TestComparison.triggerMethod1 : result == " + stringRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 4;
        try {
            log("calling TestComparison.triggerMethod1");
            stringRes = triggerMethod1(null);
            log("called TestComparison.triggerMethod1 : result == " + stringRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 5;
        try {
            log("calling TestComparison.triggerMethod2");
            intRes = triggerMethod2(1233);
            log("called TestComparison.triggerMethod2 : result == " + intRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 6;
        try {
            log("calling TestComparison.triggerMethod2");
            intRes = triggerMethod2(1234);
            log("called TestComparison.triggerMethod2 : result == " + intRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
        runNumber = 7;
        try {
            log("calling TestComparison.triggerMethod2");
            intRes = triggerMethod2(1235);
            log("called TestComparison.triggerMethod2 : result == " + intRes);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public String triggerMethod1(String arg)
    {
        log("inside TestComparison.triggerMethod1");
        return arg;
    }

    public int triggerMethod2(int arg)
    {
        log("inside TestComparison.triggerMethod2");
        return arg;
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestComparison.triggerMethod1");
                logExpected("inside TestComparison.triggerMethod1");
                logExpected("triggerMethod1 : arg == 1233");
                logExpected("triggerMethod1 : arg == 1234 == false");
                logExpected("triggerMethod1 : arg != 1234 == true");
                logExpected("triggerMethod1 : arg >= 1234 == false");
                logExpected("triggerMethod1 : arg > 1234 == false");
                logExpected("triggerMethod1 : arg < 1234 == true");
                logExpected("triggerMethod1 : arg <= 1234 == true");
                logExpected("called TestComparison.triggerMethod1 : result == 1233");
            }
            break;
            case 2:
            {
                logExpected("calling TestComparison.triggerMethod1");
                logExpected("inside TestComparison.triggerMethod1");
                logExpected("triggerMethod1 : arg == 1234");
                logExpected("triggerMethod1 : arg == 1234 == true");
                logExpected("triggerMethod1 : arg != 1234 == false");
                logExpected("triggerMethod1 : arg >= 1234 == true");
                logExpected("triggerMethod1 : arg > 1234 == false");
                logExpected("triggerMethod1 : arg < 1234 == false");
                logExpected("triggerMethod1 : arg <= 1234 == true");
                logExpected("called TestComparison.triggerMethod1 : result == 1234");
            }
            break;
            case 3:
            {
                logExpected("calling TestComparison.triggerMethod1");
                logExpected("inside TestComparison.triggerMethod1");
                logExpected("triggerMethod1 : arg == 1235");
                logExpected("triggerMethod1 : arg == 1234 == false");
                logExpected("triggerMethod1 : arg != 1234 == true");
                logExpected("triggerMethod1 : arg >= 1234 == true");
                logExpected("triggerMethod1 : arg > 1234 == true");
                logExpected("triggerMethod1 : arg < 1234 == false");
                logExpected("triggerMethod1 : arg <= 1234 == false");
                logExpected("called TestComparison.triggerMethod1 : result == 1235");
            }
            break;
            case 4:
            {
                logExpected("calling TestComparison.triggerMethod1");
                logExpected("inside TestComparison.triggerMethod1");
                logExpected("triggerMethod1 : arg == null");
                logExpected("triggerMethod1 : arg == 1234 == false");
                logExpected("triggerMethod1 : arg != 1234 == true");
                logExpected("triggerMethod1 : arg >= 1234 == false");
                logExpected("triggerMethod1 : arg > 1234 == false");
                logExpected("triggerMethod1 : arg < 1234 == false");
                logExpected("triggerMethod1 : arg <= 1234 == false");
                logExpected("called TestComparison.triggerMethod1 : result == null");
            }
            break;
            case 5:
            {
                logExpected("calling TestComparison.triggerMethod2");
                logExpected("inside TestComparison.triggerMethod2");
                logExpected("triggerMethod2 : arg == 1233");
                logExpected("triggerMethod2 : arg == 1234 == false");
                logExpected("triggerMethod2 : arg != 1234 == true");
                logExpected("triggerMethod2 : arg >= 1234 == false");
                logExpected("triggerMethod2 : arg > 1234 == false");
                logExpected("triggerMethod2 : arg < 1234 == true");
                logExpected("triggerMethod2 : arg <= 1234 == true");
                logExpected("called TestComparison.triggerMethod2 : result == 1233");
            }
            break;
            case 6:
            {
                logExpected("calling TestComparison.triggerMethod2");
                logExpected("inside TestComparison.triggerMethod2");
                logExpected("triggerMethod2 : arg == 1234");
                logExpected("triggerMethod2 : arg == 1234 == true");
                logExpected("triggerMethod2 : arg != 1234 == false");
                logExpected("triggerMethod2 : arg >= 1234 == true");
                logExpected("triggerMethod2 : arg > 1234 == false");
                logExpected("triggerMethod2 : arg < 1234 == false");
                logExpected("triggerMethod2 : arg <= 1234 == true");
                logExpected("called TestComparison.triggerMethod2 : result == 1234");
            }
            break;
            case 7:
            {
                logExpected("calling TestComparison.triggerMethod2");
                logExpected("inside TestComparison.triggerMethod2");
                logExpected("triggerMethod2 : arg == 1235");
                logExpected("triggerMethod2 : arg == 1234 == false");
                logExpected("triggerMethod2 : arg != 1234 == true");
                logExpected("triggerMethod2 : arg >= 1234 == true");
                logExpected("triggerMethod2 : arg > 1234 == true");
                logExpected("triggerMethod2 : arg < 1234 == false");
                logExpected("triggerMethod2 : arg <= 1234 == false");
                logExpected("called TestComparison.triggerMethod2 : result == 1235");
            }
            break;
        }

        return super.getExpected();
    }
}
