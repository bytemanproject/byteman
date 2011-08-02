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
public class TestEntry extends Test
{
    public TestEntry()
    {
        super(TestEntry.class.getCanonicalName());
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
        // super constructor should log first then injected ENTRY code then constructor body
        logExpected("inside TestEntryExitAuxiliary(Test)");
        logExpected("ENTRY triggered in constructor");
        logExpected("inside TestEntryExitAuxiliarySub(Test)");
        logExpected("created TestEntryExitAuxiliarySub");
        // injected ENTRY code should log in subclass method code then
        // body of subclass hsoudl log then injected ENTRY code in superclass method
        // then body of superclass method
        logExpected("calling TestEntryExitAuxiliarySub.testMethod");
        logExpected("ENTRY triggered in TestEntryExitAuxiliarySub.testMethod");
        logExpected("inside TestEntryExitAuxiliarySub.testMethod");
        logExpected("calling TestEntryExitAuxiliary.testMethod");
        logExpected("ENTRY triggered in TestEntryExitAuxiliary.testMethod");
        logExpected("ENTRY 2 triggered in TestEntryExitAuxiliary.testMethod");
        logExpected("inside TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliarySub.testMethod");

        return super.getExpected();
    }
}

