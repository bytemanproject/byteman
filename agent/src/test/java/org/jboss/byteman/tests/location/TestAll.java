/*
* JBoss, Home of Professional Open Source
* Copyright 2010, Red Hat and individual contributors
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
import org.jboss.byteman.tests.auxiliary.TestAllAuxiliary;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: Apr 29, 2010
 * Time: 4:10:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestAll extends Test
{
    private int run;

    public TestAll()
    {
        super(TestAll.class.getCanonicalName());
        run = 1;
    }

    public void test()
    {
        run = 1;

        try {
            TestAllAuxiliary testAuxiliary;
            log("creating TestAllAuxiliary");
            testAuxiliary = new TestAllAuxiliary(this);
            log("created TestAllAuxiliary");
            log("calling TestAllAuxiliary.testMethod");
            testAuxiliary.testMethod(0);
            log("called TestAllAuxiliary.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        run = 2;

        try {
            TestAllAuxiliary testAuxiliary;
            log("creating TestAllAuxiliary");
            testAuxiliary = new TestAllAuxiliary(this);
            log("created TestAllAuxiliary");
            log("calling TestAllAuxiliary.testMethod2");
            testAuxiliary.testMethod2(0);
            log("called TestAllAuxiliary.testMethod2");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        run = 3;

        try {
            TestAllAuxiliary testAuxiliary;
            log("creating TestAllAuxiliary");
            testAuxiliary = new TestAllAuxiliary(this);
            log("created TestAllAuxiliary");
            log("calling TestAllAuxiliary.testMethod3");
            testAuxiliary.testMethod3(1);
            log("called TestAllAuxiliary.testMethod3");
        } catch (Exception e) {
            log(e.getMessage());
        }

        checkOutput(true);

        run = 4;

        try {
            TestAllAuxiliary testAuxiliary;
            log("creating TestAllAuxiliary");
            testAuxiliary = new TestAllAuxiliary(this);
            log("created TestAllAuxiliary");
            log("calling TestAllAuxiliary.testMethod3");
            testAuxiliary.testMethod3(2);
            log("called TestAllAuxiliary.testMethod3");
        } catch (Exception e) {
            log(e.getMessage());
        }

        checkOutput(true);

        run = 5;

        try {
            TestAllAuxiliary testAuxiliary;
            log("creating TestAllAuxiliary");
            testAuxiliary = new TestAllAuxiliary(this);
            log("created TestAllAuxiliary");
            log("calling TestAllAuxiliary.testMethod3");
            testAuxiliary.testMethod3(3);
            log("called TestAllAuxiliary.testMethod3");
        } catch (Exception e) {
            log(e.getMessage());
        }

        checkOutput(true);

        run = 6;

        try {
            TestAllAuxiliary testAuxiliary;
            log("creating TestAllAuxiliary");
            testAuxiliary = new TestAllAuxiliary(this);
            log("created TestAllAuxiliary");
            log("calling TestAllAuxiliary.testMethod3");
            testAuxiliary.testMethod3(4);
            log("called TestAllAuxiliary.testMethod3");
        } catch (Exception e) {
            log(e.getMessage());
        }

        checkOutput(true);
    }

    @Override
    public String getExpected() {
        if (run == 1) {
            logExpected("creating TestAllAuxiliary");
            logExpected("inside TestAllAuxiliary(Test)");
            logExpected("created TestAllAuxiliary");
            logExpected("calling TestAllAuxiliary.testMethod");
            logExpected("inside testMethod currentCounter = 0");
            logExpected("testMethod CALL setCounter " + 0);
            logExpected("inside testMethod currentCounter = 0");
            logExpected("testMethod CALL getCounter " + 0);
            logExpected("inside testMethod currentCounter = 0");
            logExpected("testMethod CALL setCounter " + 0);
            logExpected("inside testMethod currentCounter = 0");
            logExpected("testMethod CALL getCounter " + 0);
            logExpected("inside testMethod currentCounter = 1");
            logExpected("testMethod CALL setCounter " + 1);
            logExpected("inside testMethod currentCounter = 1");
            logExpected("testMethod CALL getCounter " + 1);
            logExpected("inside testMethod currentCounter = 2");
            logExpected("called TestAllAuxiliary.testMethod");

            return super.getExpected();
        } else if (run == 2) {
            logExpected("creating TestAllAuxiliary");
            logExpected("inside TestAllAuxiliary(Test)");
            logExpected("created TestAllAuxiliary");
            logExpected("calling TestAllAuxiliary.testMethod2");
            logExpected("inside testMethod2 currentCounter = 0");
            // we should see trace from the first call to getCounter before printing the counter
            logExpected("testMethod2 SYNCHRONIZE " + 0);
            logExpected("inside testMethod2 currentCounter = 0");
            logExpected("inside testMethod2 currentCounter = 0");
            logExpected("testMethod2 SYNCHRONIZE " + 0);
            logExpected("inside testMethod2 currentCounter = 0");
            logExpected("inside testMethod2 currentCounter = 1");
            logExpected("testMethod2 SYNCHRONIZE " + 1);
            logExpected("inside testMethod2 currentCounter = 1");
            logExpected("inside testMethod2 currentCounter = 2");
            logExpected("called TestAllAuxiliary.testMethod2");

            return super.getExpected();
        } else if (run == 3) {
            logExpected("creating TestAllAuxiliary");
            logExpected("inside TestAllAuxiliary(Test)");
            logExpected("created TestAllAuxiliary");
            logExpected("calling TestAllAuxiliary.testMethod3");
            logExpected("inside testMethod3 currentCounter = 1");
            logExpected("testMethod3 THROW 1");
            logExpected("inside testMethod3 currentCounter = 1");

            return super.getExpected();
        } else if (run == 4) {
            logExpected("creating TestAllAuxiliary");
            logExpected("inside TestAllAuxiliary(Test)");
            logExpected("created TestAllAuxiliary");
            logExpected("calling TestAllAuxiliary.testMethod3");
            logExpected("inside testMethod3 currentCounter = 2");
            logExpected("testMethod3 THROW 2");
            logExpected("inside testMethod3 currentCounter = 2");

            return super.getExpected();
        } else if (run == 5) {
            logExpected("creating TestAllAuxiliary");
            logExpected("inside TestAllAuxiliary(Test)");
            logExpected("created TestAllAuxiliary");
            logExpected("calling TestAllAuxiliary.testMethod3");
            logExpected("inside testMethod3 currentCounter = 3");
            logExpected("testMethod3 THROW 3");
            logExpected("inside testMethod3 currentCounter = 3");

            return super.getExpected();
        } else {
            logExpected("creating TestAllAuxiliary");
            logExpected("inside TestAllAuxiliary(Test)");
            logExpected("created TestAllAuxiliary");
            logExpected("calling TestAllAuxiliary.testMethod3");
            logExpected("inside testMethod3 currentCounter = 4");
            logExpected("testMethod3 THROW 4");
            logExpected("inside testMethod3 currentCounter = 4");

            return super.getExpected();
        }
    }
}
