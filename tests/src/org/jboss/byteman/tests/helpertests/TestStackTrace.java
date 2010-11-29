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
public class TestStackTrace extends Test
{
    public TestStackTrace()
    {
        super(TestStackTrace.class.getCanonicalName());
    }

    static int runNumber = 0;

    public void test()
    {
        runNumber = 1;
        try {
            log("calling TestStackTrace.triggerMethod1");
            triggerMethod1();
            log("called TestStackTrace.triggerMethod1");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestStackTrace.triggerMethod2");
            triggerMethod2();
            log("called TestStackTrace.triggerMethod2");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestStackTrace.triggerMethod3");
            triggerMethod3();
            log("called TestStackTrace.triggerMethod3");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod1()
    {
        log("inside TestStackTrace.triggerMethod1");
    }

    public void triggerMethod2()
    {
        log("inside TestStackTrace.triggerMethod2");
        triggerMethod21();
        triggerMethod22();
    }

    public void triggerMethod21()
    {
        log("inside TestStackTrace.triggerMethod21");
        triggerMethod211();
        triggerMethod212();
    }

    public void triggerMethod211()
    {
        log("inside TestStackTrace.triggerMethod211");
    }

    public void triggerMethod212()
    {
        log("inside TestStackTrace.triggerMethod212");
    }

    public void triggerMethod22()
    {
        log("inside TestStackTrace.triggerMethod22");
    }

    public void triggerMethod3()
    {
        log("inside TestStackTrace.triggerMethod3");
        triggerMethod31();
    }

    public void triggerMethod31()
    {
        log("inside TestStackTrace.triggerMethod31");
        triggerMethod311();
    }

    public void triggerMethod311()
    {
        log("inside TestStackTrace.triggerMethod311");
        triggerMethod3111();
    }

    public void triggerMethod3111()
    {
        log("inside TestStackTrace.triggerMethod3111");
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestStackTrace.triggerMethod1");
                logExpected("stacktrace two frames\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod1(TestStackTrace.java:-1)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.test(TestStackTrace.java:45)\n" +
                        "  . . .\n");
                logExpected("inside TestStackTrace.triggerMethod1");
                logExpected("called TestStackTrace.triggerMethod1");
            }
            break;
            case 2:
            {
                logExpected("calling TestStackTrace.triggerMethod2");
                logExpected("inside TestStackTrace.triggerMethod2");
                logExpected("inside TestStackTrace.triggerMethod21");
                logExpected("stacktrace matching frames\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod211(TestStackTrace.java:-1)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod21(TestStackTrace.java:91)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod2(TestStackTrace.java:84)\n");
                logExpected("inside TestStackTrace.triggerMethod211");
                logExpected("stacktrace matching frames with class\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod212(TestStackTrace.java:-1)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod21(TestStackTrace.java:92)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod2(TestStackTrace.java:84)\n");
                logExpected("inside TestStackTrace.triggerMethod212");
                logExpected("inside TestStackTrace.triggerMethod22");
                logExpected("called TestStackTrace.triggerMethod2");
            }
            break;
            case 3:
            {
                logExpected("calling TestStackTrace.triggerMethod3");
                logExpected("inside TestStackTrace.triggerMethod3");
                logExpected("inside TestStackTrace.triggerMethod31");
                logExpected("stacktrace three frames\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod311(TestStackTrace.java:-1)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod31(TestStackTrace.java:119)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod3(TestStackTrace.java:113)\n" +
                        "  . . .\n");
                logExpected("inside TestStackTrace.triggerMethod311");
                logExpected("stacktrace matching frames with class and package\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod3111(TestStackTrace.java:-1)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod311(TestStackTrace.java:125)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod31(TestStackTrace.java:119)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod3(TestStackTrace.java:113)\n");
                logExpected("inside TestStackTrace.triggerMethod3111");
                logExpected("stacktrace between frames matches\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod3111(TestStackTrace.java:131)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod311(TestStackTrace.java:125)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod31(TestStackTrace.java:119)\n");
                logExpected("stacktrace between frames\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod3111(TestStackTrace.java:131)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod311(TestStackTrace.java:125)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod31(TestStackTrace.java:119)\n" +
                        "org.jboss.byteman.tests.helpertests.TestStackTrace.triggerMethod3(TestStackTrace.java:113)\n");
                logExpected("called TestStackTrace.triggerMethod3");
            }
            break;
        }

        return super.getExpected();
    }
}