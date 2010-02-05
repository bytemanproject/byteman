/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
    public int testInt;
    public long testLong;
    public static long staticLong;
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
            log("calling TestArithmetic.triggerMethod");
            ires = triggerMethod(0);
            log("called TestArithmetic.triggerMethod : result == " + ires);
            log("                                    : testInt == " + testInt);
            log("                                    : testLong == " + testLong);
            log("                                    : staticLong == " + staticLong);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public int triggerMethod(int i)
    {
        int local = 99;
        long local2 = 1234567890;
        log("inside TestArithmetic.triggerMethod");
        return 999;
    }

    @Override
    public String getExpected() {
        logExpected("calling TestArithmetic.triggerMethod");
        logExpected("inside TestArithmetic.triggerMethod");
        logExpected("triggerMethod1 : local == 99");
        logExpected("triggerMethod1 : $local == 99");
        logExpected("triggerMethod1 : $local2 == 1234567890");
        logExpected("triggerMethod1 : local == 100");
        logExpected("triggerMethod1 : $local == 98");
        logExpected("triggerMethod1 : $local2 == -1234567890");
        logExpected("triggerMethod1 : $1 == 2");
        logExpected("called TestArithmetic.triggerMethod : result == " + 1000);
        logExpected("                                    : testInt == 1");
        logExpected("                                    : testLong == 1234567891");
        logExpected("                                    : staticLong == 1234567892");

        return super.getExpected();
    }
}