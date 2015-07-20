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
* @authors Gary Brown
*/
package org.jboss.byteman.tests.location;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestExceptionExitAuxiliary;

/**
 * Test to ensure at exception exit trigger points are correctly identified.
 */
public class TestExceptionExit extends Test
{
    public TestExceptionExit()
    {
        super(TestExceptionExit.class.getCanonicalName());
    }

    public void test()
    {
        TestExceptionExitAuxiliary testAuxiliary;
        testAuxiliary = new TestExceptionExitAuxiliary(this);
        testAuxiliary.testMethod();

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("inside TestExceptionExitAuxiliary(Test)");
        logExpected("inside TestExceptionExitAuxiliary.testMethod");

        // testVoidMethod
        logExpected("inside TestExceptionExitAuxiliary.testVoidMethod");
        logExpected("EXCEPTIONAL EXIT: testVoidMethod exception");
        logExpected("caught: testVoidMethod exception");

        // testStringMethod
        logExpected("inside TestExceptionExitAuxiliary.testStringMethod");
        logExpected("EXCEPTIONAL EXIT: testStringMethod exception");
        logExpected("caught: testStringMethod exception");

        // testMethodTryMultiCatch
        logExpected("inside TestExceptionExitAuxiliary.testMethodTryMultiCatch");
        logExpected("EXCEPTIONAL EXIT: testMethodTryMultiCatch exception");
        logExpected("caught: testMethodTryMultiCatch exception");

        // testMethodNestedTryCatch
        logExpected("inside TestExceptionExitAuxiliary.testMethodNestedTryCatch");
        logExpected("finally testMethodNestedTryCatch");
        logExpected("EXCEPTIONAL EXIT: testMethodNestedTryCatch exception");
        logExpected("caught: testMethodNestedTryCatch exception");

        logExpected("exiting TestExceptionExitAuxiliary.testMethod");

        return super.getExpected();
    }
}