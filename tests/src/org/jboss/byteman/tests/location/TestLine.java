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
import org.jboss.byteman.tests.auxiliary.TestLineAuxiliary;

/**
 * Test to ensure at entry trigger points are correctly identified
 */
public class TestLine extends Test
{
    public TestLine()
    {
        super(TestLine.class.getCanonicalName());
    }

    public void test()
    {
        try {
        TestLineAuxiliary testAuxiliary;
        log("creating TestLineAuxiliary");
        testAuxiliary = new TestLineAuxiliary(this);
        log("created TestLineAuxiliary");
        log("calling TestLineAuxiliary.testMethod");
        testAuxiliary.testMethod(this);
        log("called TestLineAuxiliary.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestLineAuxiliary");
        logExpected("AT LINE 35 triggered in constructor");
        logExpected("inside TestLineAuxiliary(Test) at line 35");
        logExpected("inside TestLineAuxiliary(Test) at line 36");
        logExpected("created TestLineAuxiliary");

        logExpected("calling TestLineAuxiliary.testMethod");
        logExpected("AT LINE 41 triggered in TestLineAuxiliary.testMethod(Test)");
        logExpected("inside testMethod(Test) at line 41");
        logExpected("inside testMethod(Test) at line 42");
        logExpected("called TestLineAuxiliary.testMethod");

        return super.getExpected();
    }
}

