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
import org.jboss.byteman.tests.auxiliary.TestCallThrowSynchAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestSynch extends Test
{
    public TestSynch()
    {
        super(TestSynch.class.getCanonicalName());
    }

    public void test()
    {
        try {
        TestCallThrowSynchAuxiliary testAuxiliary;
        log("creating TestCallThrowSynchAuxiliary");
        testAuxiliary = new TestCallThrowSynchAuxiliary(this);
        log("created TestCallThrowSynchAuxiliary");
        log("calling TestCallThrowSynchAuxiliary.testMethod");
        testAuxiliary.testMethod();
        log("called TestCallThrowSynchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestCallThrowSynchAuxiliary");
        logExpected("inside TestCallThrowSynchAuxiliary(Test)");
        logExpected("created TestCallThrowSynchAuxiliary");
        logExpected("calling TestCallThrowSynchAuxiliary.testMethod");
        logExpected("inside TestCallThrowSynchAuxiliary.testMethod");
        // we should see trace from the first synchronize after printing the counter
        logExpected("1: currentCounter == 0");
        logExpected("SYNCHRONIZE triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("2: currentCounter == 1");
        // we should see trace from the first synchronize after printing the 2nd counter
        // and before printing the 3rd counter
        logExpected("SYNCHRONIZE 2 triggered in TestCallThrowSynchAuxiliary.testMethod");
        logExpected("3: currentCounter == 2");
        logExpected("4: currentCounter == 2");
        logExpected("called TestCallThrowSynchAuxiliary.testMethod");

        return super.getExpected();
    }
}