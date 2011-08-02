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
package org.jboss.byteman.tests.helpertests;

import org.jboss.byteman.tests.Test;

/**
 * Test to ensure arithmetic operations compute as expected
 */
public class TestCallerMatches extends Test
{
    public TestCallerMatches()
    {
        super(TestCallerMatches.class.getCanonicalName());
    }

    static int runNumber = 0;

    public void test()
    {
        runNumber = 1;
        try {
            log("calling TestCallerMatches.triggerMethod1");
            triggerMethod1();
            log("called TestCallerMatches.triggerMethod1");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestCallerMatches.triggerMethod2");
            triggerMethod2();
            log("called TestCallerMatches.triggerMethod2");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestCallerMatches.triggerMethod3");
            triggerMethod3();
            log("called TestCallerMatches.triggerMethod3");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod1()
    {
        log("inside TestCallerMatches.triggerMethod1");
    }

    public void triggerMethod2()
    {
        log("inside TestCallerMatches.triggerMethod2");
        triggerMethod21();
        triggerMethod22();
    }

    public void triggerMethod21()
    {
        log("inside TestCallerMatches.triggerMethod21");
        triggerMethod211();
        triggerMethod212();
    }

    public void triggerMethod211()
    {
        log("inside TestCallerMatches.triggerMethod211");
    }

    public void triggerMethod212()
    {
        log("inside TestCallerMatches.triggerMethod212");
    }

    public void triggerMethod22()
    {
        log("inside TestCallerMatches.triggerMethod22");
    }

    public void triggerMethod3()
    {
        log("inside TestCallerMatches.triggerMethod3");
        triggerMethod31();
    }

    public void triggerMethod31()
    {
        log("inside TestCallerMatches.triggerMethod31");
        triggerMethod311();
    }

    public void triggerMethod311()
    {
        log("inside TestCallerMatches.triggerMethod311");
        triggerMethod3111();
    }

    public void triggerMethod3111()
    {
        log("inside TestCallerMatches.triggerMethod3111");
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestCallerMatches.triggerMethod1");
                logExpected("caller match first caller");
                logExpected("inside TestCallerMatches.triggerMethod1");
                logExpected("called TestCallerMatches.triggerMethod1");
            }
            break;
            case 2:
            {
                logExpected("calling TestCallerMatches.triggerMethod2");
                logExpected("caller match first caller with class");
                logExpected("inside TestCallerMatches.triggerMethod2");
                logExpected("caller match first caller with class and package");
                logExpected("inside TestCallerMatches.triggerMethod21");
                logExpected("caller match 2nd caller in range");
                logExpected("inside TestCallerMatches.triggerMethod211");
                logExpected("caller match 1st caller in range");
                logExpected("inside TestCallerMatches.triggerMethod212");
                logExpected("inside TestCallerMatches.triggerMethod22");
                logExpected("called TestCallerMatches.triggerMethod2");
            }
            break;
            case 3:
            {
                logExpected("calling TestCallerMatches.triggerMethod3");
                logExpected("inside TestCallerMatches.triggerMethod3");
                logExpected("inside TestCallerMatches.triggerMethod31");
                logExpected("inside TestCallerMatches.triggerMethod311");
                logExpected("caller match caller's caller in range");
                logExpected("inside TestCallerMatches.triggerMethod3111");
                logExpected("called TestCallerMatches.triggerMethod3");
            }
            break;
        }

        return super.getExpected();
    }
}