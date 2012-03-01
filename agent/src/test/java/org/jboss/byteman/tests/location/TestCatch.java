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
* @authors James Livingston
*/
package org.jboss.byteman.tests.location;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestCatchAuxiliary;


/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestCatch extends Test {
    public TestCatch()
    {
        super(TestCatch.class.getCanonicalName());
    }

    public void test()
    {
        try {
        	TestCatchAuxiliary testAuxiliary;
	        log("creating TestCatchAuxiliary");
	        testAuxiliary = new TestCatchAuxiliary(this);
	        log("created TestCatchAuxiliary");
	        log("calling TestCatchAuxiliary.testMethod");
	        testAuxiliary.testMethod();
	        log("called TestCatchAuxiliary.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestCatchAuxiliary");
        logExpected("inside TestCatchAuxiliary(Test)");
        logExpected("created TestCatchAuxiliary");
        logExpected("calling TestCatchAuxiliary.testMethod");
        logExpected("inside TestCatchAuxiliary.testMethod");
        logExpected("1");
        logExpected("3");
        // we should see trace from the second catch after printing the 3rd counter
        // and before printing the 4th counter, with the correct exception attached
        logExpected("CATCH IllegalStateException triggered in TestCatchAuxiliary.testMethod: A");
        logExpected("4: IllegalStateException A");
        logExpected("5");
        logExpected("called TestCatchAuxiliary.testMethod");

        return super.getExpected();
    }
}
