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
 * Test to ensure logical operations compute as expected
 */
public class TestLogical extends Test
{
    public TestLogical() {
        super(TestLogical.class.getCanonicalName());
    }

    static int runNumber = 0;

    public void test()
    {
        boolean res;

        runNumber = 1;
        try {
            log("calling TestLogical.triggerMethod1");
            res = triggerMethod1(true);
            log("called TestLogical.triggerMethod1 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestLogical.triggerMethod1");
            res = triggerMethod1(false);
            log("called TestLogical.triggerMethod1 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestLogical.triggerMethod2");
            res = triggerMethod2(true);
            log("called TestLogical.triggerMethod2 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 4;
        try {
            log("calling TestLogical.triggerMethod2");
            res = triggerMethod2(false);
            log("called TestLogical.triggerMethod2 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public boolean triggerMethod1(boolean arg)
    {
        log("inside TestLogical.triggerMethod1");
        return arg;
    }

    public boolean triggerMethod2(boolean arg)
    {
        log("inside TestLogical.triggerMethod2");
        return arg;
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestLogical.triggerMethod1");
                logExpected("inside TestLogical.triggerMethod1");
                logExpected("triggerMethod1 : arg == true");
                logExpected("triggerMethod1 : (arg && !arg) == false");
                logExpected("triggerMethod1 : (arg && true) == true");
                logExpected("called TestLogical.triggerMethod1 : result == " + true);
            }
            break;
            case 2:
            {
                logExpected("calling TestLogical.triggerMethod1");
                logExpected("inside TestLogical.triggerMethod1");
                logExpected("triggerMethod1 : arg == false");
                logExpected("triggerMethod1 : (arg || !arg) == true");
                logExpected("triggerMethod1 : (arg || false) == false");
                logExpected("called TestLogical.triggerMethod1 : result == " + true);
            }
            break;
            case 3:
            {
                logExpected("calling TestLogical.triggerMethod2");
                logExpected("inside TestLogical.triggerMethod2");
                logExpected("triggerMethod2 : arg == true");
                logExpected("triggerMethod2 : (arg || !arg) == true");
                logExpected("triggerMethod2 : (arg || false) == true");
                logExpected("called TestLogical.triggerMethod2 : result == " + false);
            }
            break;
            case 4:
            {
                logExpected("calling TestLogical.triggerMethod2");
                logExpected("inside TestLogical.triggerMethod2");
                logExpected("triggerMethod2 : arg == false");
                logExpected("triggerMethod2 : (arg && !arg) == false");
                logExpected("triggerMethod2 : (arg && true) == false");
                logExpected("called TestLogical.triggerMethod2 : result == " + false);
            }
            break;
        }

        return super.getExpected();
    }
}
