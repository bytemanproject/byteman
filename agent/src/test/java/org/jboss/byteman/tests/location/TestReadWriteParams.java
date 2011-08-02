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
public class TestReadWriteParams extends Test
{
    public TestReadWriteParams()
    {
        super(TestReadWriteParams.class.getCanonicalName());
    }

    public void test()
    {
        try {
            TestReadWriteAuxiliary testAuxiliary;
            log("creating TestReadWriteAuxiliary");
            testAuxiliary = new TestReadWriteAuxiliary(this);
            log("created TestReadWriteAuxiliary");
            log("calling TestReadWriteAuxiliary.testMethod2");
            testAuxiliary.testMethod2("hello", 1);
            log("called TestReadWriteAuxiliary.testMethod2");
            log("calling TestReadWriteAuxiliary.testMethod3");
            testAuxiliary.testMethod3("why aye", 0);
            log("called TestReadWriteAuxiliary.testMethod3");
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
        logExpected("calling TestReadWriteAuxiliary.testMethod2");
        logExpected("inside TestReadWriteAuxiliary.testMethod2");
        logExpected("AT READ $1 triggered in TestReadWriteAuxiliary.testMethod2 : $1 == hello");
        logExpected("AFTER READ $arg1 triggered in TestReadWriteAuxiliary.testMethod2 : $arg1 == hello");
        logExpected("1: arg1 == hello");
        logExpected("AT WRITE $1 triggered in TestReadWriteAuxiliary.testMethod2 : $1 == hello");
        logExpected("AFTER WRITE $1 triggered in TestReadWriteAuxiliary.testMethod2 : $1 == goodbye");
        logExpected("AT READ $arg1 2 triggered in TestReadWriteAuxiliary.testMethod2 : $arg1 == goodbye");
        logExpected("AFTER READ $1 2 triggered in TestReadWriteAuxiliary.testMethod2 : $1 == goodbye");
        logExpected("2: arg1 == goodbye");

        logExpected("AT WRITE $2 triggered in TestReadWriteAuxiliary.testMethod2 : $2 == 1");
        logExpected("AFTER WRITE $2 triggered in TestReadWriteAuxiliary.testMethod2 : $2 == 2");
        logExpected("AT READ $2 triggered in TestReadWriteAuxiliary.testMethod2 : $2 == 2");
        logExpected("AFTER READ $arg2 triggered in TestReadWriteAuxiliary.testMethod2 : $arg2 == 2");
        logExpected("AT WRITE $arg2 2 triggered in TestReadWriteAuxiliary.testMethod2 : $arg2 == 2");
        logExpected("AFTER WRITE $arg2 2 triggered in TestReadWriteAuxiliary.testMethod2 : $arg2 == 3");
        logExpected("AT READ $arg2 2 triggered in TestReadWriteAuxiliary.testMethod2 : $arg2 == 3");
        logExpected("AFTER READ $2 2 triggered in TestReadWriteAuxiliary.testMethod2 : $2 == 3");
        logExpected("3: arg2 == 3");
        logExpected("called TestReadWriteAuxiliary.testMethod2");
        logExpected("calling TestReadWriteAuxiliary.testMethod3");
        logExpected("inside TestReadWriteAuxiliary.testMethod3");
        logExpected("AT WRITE $d ALL triggered in TestReadWriteAuxiliary.testMethod3 : $d == 0.0");
        logExpected("AT READ $1 ALL triggered in TestReadWriteAuxiliary.testMethod3 : $1 == why aye");
        logExpected("1: arg1 == why aye");
        logExpected("AT READ $1 ALL triggered in TestReadWriteAuxiliary.testMethod3 : $1 == goodbye");
        logExpected("2: arg1 == goodbye");
        logExpected("AFTER WRITE $arg2 ALL triggered in TestReadWriteAuxiliary.testMethod3 : $arg2 == 5");
        logExpected("AFTER WRITE $arg2 ALL triggered in TestReadWriteAuxiliary.testMethod3 : $arg2 == 10");
        logExpected("3: arg2 == 10");
        logExpected("AT WRITE $d ALL triggered in TestReadWriteAuxiliary.testMethod3 : $d == 0.0");
        logExpected("AT WRITE $d ALL triggered in TestReadWriteAuxiliary.testMethod3 : $d == 1.0");
        logExpected("4: d == 2.0");
        logExpected("called TestReadWriteAuxiliary.testMethod3");

        return super.getExpected();
    }
}