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
package org.jboss.byteman.tests.javaops;

import org.jboss.byteman.tests.Test;

/**
 * Test to ensure array operations compute as expected
 */
public class TestArray extends Test
{
    public TestArray()
    {
        super(TestArray.class.getCanonicalName());
    }

    static int[] iarray;
    static Object[][] oarray;
    static int runNumber = 0;

    public void test()
    {
        iarray = new int[1];
        oarray = new Object[1][1];
        // oarray[0][0] = new Object();
        oarray[0][0] = null;
        Object[] ores;

        try {
            log("calling TestArray.triggerMethod1");
            ores = triggerMethod1(iarray, oarray);
            log("called TestArray.triggerMethod1 : result == " + ores);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

	runNumber++;

	oarray[0] = new Object[2];
	oarray[0][0] = oarray[0][1] = "hello";

        try {
            log("calling TestArray.triggerMethod2");
            triggerMethod2(iarray, oarray);
            log("called TestArray.triggerMethod2 : oarray[0][1] == " + oarray[0][1]);
        } catch (Exception e) {
            log(e);
        }

	checkOutput(true);
    }

    public Object[] triggerMethod1(int[] iarray, Object[][] oarray)
    {
        log("inside TestArray.triggerMethod1");
        return null;
    }

    public void triggerMethod2(int[] iarray, Object[][] oarray)
    {
        log("inside TestArray.triggerMethod2");
    }

    @Override
    public String getExpected() {
	if (runNumber == 0) {
	    logExpected("calling TestArray.triggerMethod1");
	    logExpected("inside TestArray.triggerMethod1");
	    logExpected("triggerMethod1 : iarray[0] == " + iarray[0]);
	    logExpected("triggerMethod1 : oarray[0][0] == " + oarray[0][0]);
	    logExpected("called TestArray.triggerMethod1 : result == " + oarray[0]);
	} else {
	    logExpected("calling TestArray.triggerMethod2");
	    logExpected("inside TestArray.triggerMethod2");
	    logExpected("triggerMethod2 : iarray.length == " + 1);
	    logExpected("triggerMethod2 : oarray[0].length == " + 2);
	    logExpected("triggerMethod2 : oarray[0][1] == hello");
	    logExpected("called TestArray.triggerMethod2 : oarray[0][1] == goodbye");
	}

        return super.getExpected();
    }
}