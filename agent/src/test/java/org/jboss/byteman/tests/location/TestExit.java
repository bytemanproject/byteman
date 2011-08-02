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
package org.jboss.byteman.tests.location;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliary;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliarySub;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestExit extends Test
{
    public TestExit()
    {
        super(TestExit.class.getCanonicalName());
    }

    public void test()
    {
        try {
        TestEntryExitAuxiliary testAuxiliary;
        log("creating TestEntryExitAuxiliarySub");
        testAuxiliary = new TestEntryExitAuxiliarySub(this);
        log("created TestEntryExitAuxiliarySub");
        log("calling TestEntryExitAuxiliarySub.testMethod");
        testAuxiliary.testMethod();
        log("called TestEntryExitAuxiliarySub.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestEntryExitAuxiliarySub");
        // super constructor should log first then constructor body then injected EXIT code
        logExpected("inside TestEntryExitAuxiliary(Test)");
        logExpected("inside TestEntryExitAuxiliarySub(Test)");
        logExpected("EXIT triggered in constructor");
        logExpected("created TestEntryExitAuxiliarySub");
        // body of subclass should log then body of superclass method
        // then injected EXIT code in superclass method then
        // injected ENTRY code in subclass method code
        logExpected("calling TestEntryExitAuxiliarySub.testMethod");
        logExpected("inside TestEntryExitAuxiliarySub.testMethod");
        logExpected("calling TestEntryExitAuxiliary.testMethod");
        logExpected("inside TestEntryExitAuxiliary.testMethod");
        logExpected("EXIT triggered in TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliary.testMethod");
        logExpected("EXIT triggered in TestEntryExitAuxiliarySub.testMethod");
        logExpected("called TestEntryExitAuxiliarySub.testMethod");

        return super.getExpected();
    }
}