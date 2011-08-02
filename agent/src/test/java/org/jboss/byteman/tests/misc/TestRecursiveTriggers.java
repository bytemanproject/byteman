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
package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;

/**
 * Test to ensure arithmetic operations compute as expected
 */
public class TestRecursiveTriggers extends Test
{
    public TestRecursiveTriggers()
    {
        super(TestRecursiveTriggers.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling TestRecursiveTriggers.triggerMethod1(1)");
            triggerMethod1(1);
            log("called TestRecursiveTriggers.triggerMethod1(1)");

            log("calling TestRecursiveTriggers.triggerMethod2()");
            triggerMethod2();
            log("called TestRecursiveTriggers.triggerMethod2()");

            log("calling TestRecursiveTriggers.triggerMethod3()");
            triggerMethod3();
            log("called TestRecursiveTriggers.triggerMethod3()");

            log("calling TestRecursiveTriggers.triggerMethod2()");
            triggerMethod2();
            log("called TestRecursiveTriggers.triggerMethod2()");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod1(int i)
    {
        log("inside TestRecursiveTriggers.triggerMethod1(" + i + ")");
    }

    public void triggerMethod2()
    {
        log("inside TestRecursiveTriggers.triggerMethod2()");
    }

    public void triggerMethod3()
    {
        log("inside TestRecursiveTriggers.triggerMethod3()");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestRecursiveTriggers.triggerMethod1(1)");
        logExpected("triggerMethod1 : triggered with 1");
        logExpected("inside TestRecursiveTriggers.triggerMethod1(1)");
        logExpected("called TestRecursiveTriggers.triggerMethod1(1)");

        logExpected("calling TestRecursiveTriggers.triggerMethod2()");
        logExpected("triggerMethod2 : triggered");
        logExpected("triggerMethod1 : triggered with 2");
        logExpected("inside TestRecursiveTriggers.triggerMethod1(2)");
        logExpected("inside TestRecursiveTriggers.triggerMethod2()");
        logExpected("called TestRecursiveTriggers.triggerMethod2()");

        logExpected("calling TestRecursiveTriggers.triggerMethod3()");
        logExpected("triggerMethod3 : triggered");
        logExpected("inside TestRecursiveTriggers.triggerMethod1(3)");
        logExpected("triggerMethod1 : triggered with 4");
        logExpected("inside TestRecursiveTriggers.triggerMethod1(4)");
        logExpected("inside TestRecursiveTriggers.triggerMethod3()");
        logExpected("called TestRecursiveTriggers.triggerMethod3()");

        logExpected("calling TestRecursiveTriggers.triggerMethod2()");
        logExpected("triggerMethod2 : triggered");
        logExpected("triggerMethod1 : triggered with 2");
        logExpected("inside TestRecursiveTriggers.triggerMethod1(2)");
        logExpected("inside TestRecursiveTriggers.triggerMethod2()");
        logExpected("called TestRecursiveTriggers.triggerMethod2()");

        return super.getExpected();
    }
}