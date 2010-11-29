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
package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Auxiliary subclass used by read and write location test classes
 */
public class TestReadWriteAuxiliary
{
    protected Test test;
    private int counter;

    public TestReadWriteAuxiliary(Test test)
    {
        this.test = test;
        this.counter = 0;
        test.log("inside TestReadWriteAuxiliary(Test)");
    }

    public void testMethod() throws Exception
    {
        test.log("inside TestReadWriteAuxiliary.testMethod");

        int currentCounter = counter;

        test.log("1: currentCounter == " + currentCounter);

        currentCounter++;

        counter = currentCounter;

        test.log("2: currentCounter == " + currentCounter);

        currentCounter = counter;

        currentCounter++;

        counter = currentCounter;

        test.log("3: currentCounter == " + currentCounter);
    }

    public void testMethod2(String arg1, int arg2) throws Exception
    {
        test.log("inside TestReadWriteAuxiliary.testMethod2");

        test.log("1: arg1 == " + arg1);

        arg1 = "goodbye";

        test.log("2: arg1 == " + arg1);

        arg2 = 2;

        arg2++;

        test.log("3: arg2 == " + arg2);
    }

    public void testMethod3(String arg1, int arg2) throws Exception
    {
        test.log("inside TestReadWriteAuxiliary.testMethod3");

        double d = 0.0;

        test.log("1: arg1 == " + arg1);

        arg1 = "goodbye";

        test.log("2: arg1 == " + arg1);

        arg2 = 5;

        arg2+= 5;

        test.log("3: arg2 == " + arg2);

        d += 1.0;

        d++;

        test.log("4: d == " + d);
    }

    public Test getTest()
    {
        return test;
    }

    public int setCounter(int newValue)
    {
        int oldValue = counter;
        counter = newValue;

        return oldValue;
    }

    public int getCounter()
    {
        return counter;
    }
}
