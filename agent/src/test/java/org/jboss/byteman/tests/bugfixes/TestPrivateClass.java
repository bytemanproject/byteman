/*
* JBoss, Home of Professional Open Source
* Copyright 2016, Red Hat and individual contributors
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
* @authors Ion Savin
*/
package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.C5;

/**
 * Test for BYTEMAN-318 where a call is made to a public method of a private
 * class resulting in IllegalAccessException.
 */
public class TestPrivateClass extends Test
{
    public TestPrivateClass() {
        super(TestPrivateClass.class.getCanonicalName());
    }

    public void test() {
        C5 c5 = new C5();
        log("calling C5.testMethod");
        c5.testMethod(this);
        log("called C5.testMethod");

        checkOutput(true);
    }

    @Override
    public String getExpected() {
        logExpected("calling C5.testMethod");
        logExpected("inside C5.testMethod");
        logExpected("inside C5.PC.testMethod");
        logExpected("called C5.testMethod");
        return super.getExpected();
    }
}
