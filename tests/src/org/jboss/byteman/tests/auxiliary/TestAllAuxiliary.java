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
 * Auxiliary class used to test trigger locations which specify count ALL
 */
public class TestAllAuxiliary
{
    protected Test test;
    public int counter;

    public TestAllAuxiliary(Test test)
    {
        this.test = test;
        this.counter = 0;
        test.log("inside TestAllAuxiliary(Test)");
    }

    /**
     * method used to ensure that AT INVOKE XXX ALL works ok
     * @throws Exception
     */

    public void testMethod(int i)
    {
        int currentCounter = i;

        test.log("inside testMethod currentCounter = " + currentCounter);

        setCounter(currentCounter);

        test.log("inside testMethod currentCounter = " + currentCounter);

        currentCounter =  getCounter();

        test.log("inside testMethod currentCounter = " + currentCounter);

        setCounter(currentCounter + 1);

        test.log("inside testMethod currentCounter = " + currentCounter);

        currentCounter =  getCounter();

        test.log("inside testMethod currentCounter = " + currentCounter);

        setCounter(currentCounter + 1);

        test.log("inside testMethod currentCounter = " + currentCounter);

        currentCounter =  getCounter();

        test.log("inside testMethod currentCounter = " + currentCounter);
    }

    /**
     * method used to ensure that AT SYNCHRONIZE ALL works ok
     * @throws Exception
     */

    public void testMethod2(int i)
    {
        int currentCounter = i;

        test.log("inside testMethod2 currentCounter = " + currentCounter);

        synchronized(this) {
            setCounter(currentCounter);

            test.log("inside testMethod2 currentCounter = " + currentCounter);

            currentCounter =  getCounter();

            test.log("inside testMethod2 currentCounter = " + currentCounter);
        }

        synchronized(this) {
            setCounter(currentCounter + 1);

            test.log("inside testMethod2 currentCounter = " + currentCounter);

            currentCounter =  getCounter();

            test.log("inside testMethod2 currentCounter = " + currentCounter);
        }
        
        synchronized(this) {
            setCounter(currentCounter + 1);

            test.log("inside testMethod2 currentCounter = " + currentCounter);

            currentCounter =  getCounter();

            test.log("inside testMethod2 currentCounter = " + currentCounter);
        }
    }

    /**
     * method used to ensure that AT THROW ALL works ok
     * @throws Exception
     */

    public void testMethod3(int i) throws Exception
    {
        int currentCounter = i;

        test.log("inside testMethod3 currentCounter = " + currentCounter);

        if (currentCounter == 1) {
            throw new Exception("inside testMethod3 currentCounter = 1");
        }

        if (currentCounter == 2) {
            throw new Exception("inside testMethod3 currentCounter = 2");
        }

        if (currentCounter == 3) {
            throw new Exception("inside testMethod3 currentCounter = 3");
        }

        throw new Exception("inside testMethod3 currentCounter = 4");
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

    public Test getTest()
    {
        return test;
    }
}
