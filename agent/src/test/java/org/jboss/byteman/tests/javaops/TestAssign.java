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
 * Test to ensure arithmetic operations compute as expected
 */
public class TestAssign extends Test
{
    protected int testInt;
    public long testLong;
    private static long staticLong;
    public TestAssign()
    {
        super(TestAssign.class.getCanonicalName());
        testInt = 0;
        testLong = 1234567890;
    }

    static int runNumber = 0;

    public void test()
    {
        int ires;

        try {
            log("calling TestAssign.triggerMethod");
            ires = triggerMethod(0);
            log("called TestAssign.triggerMethod : result == " + ires);
            log("                                    : testInt == " + testInt);
            log("                                    : testLong == " + testLong);
            log("                                    : staticLong == " + staticLong);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber++;

        try {
            log("calling TestAssign.triggerMethod2");
            ires = triggerMethod2(0);
            log("called TestAssign.triggerMethod2 : result == " + ires);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public int triggerMethod(int i)
    {
        int local = 99;
        long local2 = 1234567890;
        log("inside TestAssign.triggerMethod");
        return 999;
    }

    public int triggerMethod2(int i)
    {
        int local = 99;
        long local2 = 1234567890;
        log("inside TestAssign.triggerMethod2");
        log("inside TestAssign.triggerMethod2 : i == " + i);
        log("inside TestAssign.triggerMethod2 : local == " + local);
        log("inside TestAssign.triggerMethod2 : local2 == " + local2);
        return 999;
    }

    @Override
    public String getExpected() {
        if (runNumber == 0) {
            logExpected("calling TestAssign.triggerMethod");
            logExpected("inside TestAssign.triggerMethod");
            logExpected("triggerMethod1 : local == 99");
            logExpected("triggerMethod1 : $local == 99");
            logExpected("triggerMethod1 : $local2 == 1234567890");
            logExpected("triggerMethod1 : $! == 999");
            logExpected("triggerMethod1 : local == 100");
            logExpected("triggerMethod1 : $local == 98");
            logExpected("triggerMethod1 : $local2 == -1234567890");
            logExpected("triggerMethod1 : $1 == 2");
            logExpected("called TestAssign.triggerMethod : result == " + 1000);
            logExpected("                                    : testInt == 1");
            logExpected("                                    : testLong == 1234567891");
            logExpected("                                    : staticLong == 1234567892");
        } else {
            logExpected("calling TestAssign.triggerMethod2");
            logExpected("inside TestAssign.triggerMethod2");
            logExpected("inside TestAssign.triggerMethod2 : i == 1");
            logExpected("inside TestAssign.triggerMethod2 : local == 101");
            logExpected("inside TestAssign.triggerMethod2 : local2 == 1234567893");
            logExpected("called TestAssign.triggerMethod2 : result == " + 999);
        }
        return super.getExpected();
    }
}