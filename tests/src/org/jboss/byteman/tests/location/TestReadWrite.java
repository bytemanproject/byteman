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
import org.jboss.byteman.tests.auxiliary.TestReadWriteAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestReadWrite extends Test
{
    public TestReadWrite()
    {
        super(TestReadWrite.class.getCanonicalName());
    }

    public void test()
    {
        try {
            TestReadWriteAuxiliary testAuxiliary;
            log("creating TestReadWriteAuxiliary");
            testAuxiliary = new TestReadWriteAuxiliary(this);
            log("created TestReadWriteAuxiliary");
            log("calling TestReadWriteAuxiliary.testMethod");
            testAuxiliary.testMethod();
            log("called TestReadWriteAuxiliary.testMethod");
        } catch (Exception e) {
            log(e, true);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestReadWriteAuxiliary");
        logExpected("inside TestReadWriteAuxiliary(Test)");
        logExpected("created TestReadWriteAuxiliary");
        logExpected("calling TestReadWriteAuxiliary.testMethod");
        logExpected("inside TestReadWriteAuxiliary.testMethod");
        logExpected("AT READ 1 triggered in TestReadWriteAuxiliary.testMethod : counter == 0");
        logExpected("AT READ 1 again triggered in TestReadWriteAuxiliary.testMethod : counter == 0");
        logExpected("1: currentCounter == 0");
        logExpected("AT WRITE 1 triggered in TestReadWriteAuxiliary.testMethod : counter == 0");
        logExpected("AT WRITE 1 again triggered in TestReadWriteAuxiliary.testMethod : counter == 0");
        logExpected("2: currentCounter == 1");
        logExpected("AFTER READ 2 again triggered in TestReadWriteAuxiliary.testMethod : counter == 1");
        logExpected("AFTER READ 2 triggered in TestReadWriteAuxiliary.testMethod : counter == 1");
        // AFTER
        logExpected("AFTER WRITE 2 again triggered in TestReadWriteAuxiliary.testMethod : counter == 2");
        logExpected("AFTER WRITE 2 triggered in TestReadWriteAuxiliary.testMethod : counter == 2");
        logExpected("3: currentCounter == 2");
        logExpected("called TestReadWriteAuxiliary.testMethod");

        return super.getExpected();
    }
}