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
 * Test to ensure new operations work as expected
 */
public class TestNew extends Test
{
    public TestNew()
    {
        super(TestNew.class.getCanonicalName());
    }

    public void test()
    {
        String input = "easy as abc";
        String result;

        try {
            log("calling TestNew.triggerMethod");
            result = triggerMethod(input);
            log("called TestNew.triggerMethod : result == " + result);
            log("called TestNew.triggerMethod : result.equals(input) == " + result.equals(input));
            log("called TestNew.triggerMethod : (result == input) == " + (result == input));
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public String triggerMethod(String input)
    {
        log("inside TestNew.triggerMethod");
        return input;
    }

    @Override
    public String getExpected() {
        logExpected("calling TestNew.triggerMethod");
        logExpected("inside TestNew.triggerMethod");
        logExpected("triggerMethod : input == easy as abc");
        logExpected("triggerMethod : new input == easy as 123");
        logExpected("triggerMethod : throwable == java.lang.Exception: hello");
        logExpected("triggerMethod : newStrArray[0] == null");
        logExpected("triggerMethod : newStrArray[0] == easy as abc");
        logExpected("triggerMethod : newStrArrayArray[0] == null");
        logExpected("triggerMethod : newStrArrayArray[0][0] == null");
        logExpected("triggerMethod : newStrArrayArray[0][0] == easy as abc");
        logExpected("triggerMethod : newDoubleArray[0] == 0.0");
        logExpected("triggerMethod : newDoubleArray[0] == 1.0");
        logExpected("triggerMethod : newDoubleArrayArray[0] == null");
        logExpected("triggerMethod : newDoubleArrayArray[0][0] == 1.0");
        logExpected("triggerMethod : newDoubleArrayArrayArray[0] == null");
        logExpected("triggerMethod : newDoubleArrayArrayArray[0][0][0] == 1.0");
        logExpected("triggerMethod : newDoubleArrayArrayArray2[0][0] == null");
        logExpected("triggerMethod : newDoubleArrayArrayArray2[0][0][0] == 1.0");
        logExpected("called TestNew.triggerMethod : result == easy as abc");
        logExpected("called TestNew.triggerMethod : result.equals(input) == true");
        logExpected("called TestNew.triggerMethod : (result == input) == false");

        return super.getExpected();
    }
}