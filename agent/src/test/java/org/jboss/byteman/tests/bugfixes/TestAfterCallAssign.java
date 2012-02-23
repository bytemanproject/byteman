/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat and individual contributors as identified
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
package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;

/**
 * Test to ensure assign of $! in AFTER CALL rule works as expected
 */
public class TestAfterCallAssign extends Test
{
    protected int testInt;
    public long testLong;
    private static long staticLong;
    public TestAfterCallAssign()
    {
        super(TestAfterCallAssign.class.getCanonicalName());
        testInt = 0;
        testLong = 1234567890;
    }

    public void test()
    {
        int ires;

        try {
            log("calling TestAfterCallAssign.triggerMethod");
            ires = triggerMethod(0);
            log("called TestAfterCallAssign.triggerMethod : result == " + ires);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public int triggerMethod(int i)
    {
        log("inside TestAfterCallAssign.triggerMethod");
        double callResult = computeDouble(i);
        return (int)callResult;
    }

    public double computeDouble(int i)
    {
        return (99.9D + i);
    }

    @Override
    public String getExpected() {
        logExpected("calling TestAfterCallAssign.triggerMethod");
        logExpected("inside TestAfterCallAssign.triggerMethod");
        logExpected("triggerMethod : $! == " + 99.9D);
        logExpected("called TestAfterCallAssign.triggerMethod : result == " + 100);

        return super.getExpected();
    }
}