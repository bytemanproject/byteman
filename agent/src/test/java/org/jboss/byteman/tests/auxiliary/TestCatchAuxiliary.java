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
 * Auxiliary class used by call, throw and synchronization location test classes
 */
public class TestCatchAuxiliary
{
    protected Test test;

    public TestCatchAuxiliary(Test test)
    {
        this.test = test;
        test.log("inside TestCatchAuxiliary(Test)");
    }

    public void testMethod() throws Exception
    {
        test.log("inside TestCatchAuxiliary.testMethod");

        test.log("1");
        
        try {
            // do nothing
        } catch (IllegalArgumentException e) {
            test.log("2: IllegalArgumentException " + e.getMessage());
        }

        test.log("3");

        try {
        	throw new IllegalStateException("A");
        } catch (IllegalStateException e) {
            test.log("4: IllegalStateException " + e.getMessage());
        }

        test.log("5");
    }

    public Test getTest()
    {
        return test;
    }
}
