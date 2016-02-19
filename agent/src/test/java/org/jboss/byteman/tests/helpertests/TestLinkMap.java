/*
* JBoss, Home of Professional Open Source
* Copyright 2016, Red Hat and individual contributors
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
public class TestLinkMap extends Test
{
    public TestLinkMap()
    {
        super(TestLinkMap.class.getCanonicalName());
    }

    static int runNumber = 0;

    public void test()
    {
        runNumber = 1;
        try {
            log("calling TestLinkMap.triggerMethod1");
            triggerMethod1();
            log("called TestLinkMap.triggerMethod1");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestLinkMap.triggerMethod2");
            triggerMethod2();
            log("called TestLinkMap.triggerMethod2");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod1()
    {
        log("inside TestLinkMap.triggerMethod1");
    }

    public void triggerMethod2()
    {
        log("inside TestLinkMap.triggerMethod2");
    }

    @Override
    public String getExpected() {
        int lineTriggerMethod1 = 94;
        int lineTest = 41;
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestLinkMap.triggerMethod1");
                logExpected("triggerMethod1 : created link map " + this);
                logExpected("triggerMethod1 : lookup of 1 == " + null);
                logExpected("triggerMethod1 : link(1, one) == " + null);
                logExpected("triggerMethod1 : link(1, uno) == one");
                logExpected("triggerMethod1 : unlink(1) == uno");
                logExpected("triggerMethod1 : unlink(1) == " + null);
                logExpected("triggerMethod1 : deleted link map " + this);
                logExpected("inside TestLinkMap.triggerMethod1");
                logExpected("called TestLinkMap.triggerMethod1");
            }
	    break;
            case 2:
            {
                logExpected("calling TestLinkMap.triggerMethod2");
                logExpected("triggerMethod2 : link(one, 1) == " + null);
                logExpected("triggerMethod2 : link(uno, one) == " + null);
                logExpected("triggerMethod2 : linked(linked(uno)) == 1");
                logExpected("triggerMethod2 : linked(default, linked(default, uno)) == 1");
                logExpected("triggerMethod2 : deleteLinkMap(default) == " + true);
                logExpected("inside TestLinkMap.triggerMethod2");
                logExpected("called TestLinkMap.triggerMethod2");
            }
	    break;
        }

	return super.getExpected();
    }
}
