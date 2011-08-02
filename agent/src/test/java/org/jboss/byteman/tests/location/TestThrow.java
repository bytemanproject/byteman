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
public class TestThrow extends Test
{
    public TestThrow()
    {
        super(TestThrow.class.getCanonicalName());
    }

    private int runNumber;

    public void test()
    {
        /*
        runNumber = 1;

        try {
            TestCallThrowSynchAuxiliary testAuxiliary;
            log("creating TestCallThrowSynchAuxiliary");
            testAuxiliary = new TestCallThrowSynchAuxiliary(this);
            log("created TestCallThrowSynchAuxiliary");
            testAuxiliary.counter = 1;
            log("assigned TestCallThrowSynchAuxiliary.counter = 1");
            log("calling TestCallThrowSynchAuxiliary.testMethod");
            testAuxiliary.testMethod();
            log("called TestCallThrowSynchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e, true);
        }

        checkOutput(true);
        */

        runNumber = 2;
        
        try {
            TestCallThrowSynchAuxiliary testAuxiliary;
            log("creating TestCallThrowSynchAuxiliary");
            testAuxiliary = new TestCallThrowSynchAuxiliary(this);
            log("created TestCallThrowSynchAuxiliary");
            testAuxiliary.counter = 2;
            log("assigned TestCallThrowSynchAuxiliary.counter = 1");
            log("calling TestCallThrowSynchAuxiliary.testMethod");
            testAuxiliary.testMethod();
            log("called TestCallThrowSynchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e, true);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestCallThrowSynchAuxiliary");
        logExpected("inside TestCallThrowSynchAuxiliary(Test)");
        logExpected("created TestCallThrowSynchAuxiliary");
        logExpected("assigned TestCallThrowSynchAuxiliary.counter = 1");
        logExpected("calling TestCallThrowSynchAuxiliary.testMethod");
        logExpected("inside TestCallThrowSynchAuxiliary.testMethod");
        if (runNumber == 1) {
            logExpected("1: currentCounter == 1");
            logExpected("THROW 1 triggered in TestCallThrowSynchAuxiliary.testMethod");
            logExpected(new Exception("counter == 1"));
        }
        if (runNumber == 2) {
            logExpected("1: currentCounter == 2");
            logExpected("2: currentCounter == 3");
            logExpected("THROW 2 triggered in TestCallThrowSynchAuxiliary.testMethod");
            logExpected(new Exception("counter == 4"));
        }

        return super.getExpected();
    }
}