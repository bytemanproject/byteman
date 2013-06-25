/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
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
 * Test for BYTEMAN-246 to ensure that comparisons of boolean or Boolean values in Byteman rules are
 * performed correctly
 */
public class TestBooleanComparisons extends Test
{
    public TestBooleanComparisons()
    {
        super(TestBooleanComparisons.class.getCanonicalName());
    }

    public void test()
    {
        int ires;

        try {
            log("calling TestBooleanComparisons.triggerMethod");
            triggerMethod(true, false, new Boolean(true), new Boolean(false));
            log("called TestBooleanComparisons.triggerMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public void triggerMethod(boolean b1, boolean b2, Boolean b3, Boolean b4)
    {
        log("inside TestBooleanComparisons.triggerMethod");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestBooleanComparisons.triggerMethod");

        logExpected("b1 == b1 ==> true");
        logExpected("b1 == b2 ==> false");
        logExpected("b1 != b1 ==> false");
        logExpected("b1 != b2 ==> true");
        logExpected("b2 == b2 ==> true");
        logExpected("b2 == b1 ==> false");
        logExpected("b2 != b2 ==> false");
        logExpected("b2 != b1 ==> true");

        logExpected("B1 == B1 ==> true");
        logExpected("B1 == B2 ==> false");
        logExpected("B1 != B1 ==> false");
        logExpected("B1 != B2 ==> true");
        logExpected("B2 == B2 ==> true");
        logExpected("B2 == B1 ==> false");
        logExpected("B2 != B2 ==> false");
        logExpected("B2 != B1 ==> true");

        logExpected("b1 == B1 ==> true");
        logExpected("b1 == B2 ==> false");
        logExpected("b1 != B1 ==> false");
        logExpected("b1 != B2 ==> true");
        logExpected("B2 == b2 ==> true");
        logExpected("B2 == b1 ==> false");
        logExpected("B2 != b2 ==> false");
        logExpected("B2 != b1 ==> true");

        logExpected("inside TestBooleanComparisons.triggerMethod");
        logExpected("called TestBooleanComparisons.triggerMethod");

        return super.getExpected();
    }

}
