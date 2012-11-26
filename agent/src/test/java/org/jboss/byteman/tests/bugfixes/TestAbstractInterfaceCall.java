/*
* JBoss, Home of Professional Open Source
* Copyright 2012, Red Hat and individual contributors
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
package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.C3;
import org.jboss.byteman.tests.auxiliary.C4;
import org.jboss.byteman.tests.auxiliary.I5;


/**
 * Test for BYTEMAN-219 where a call to an interface method of an abstract class failed to typecheck
 * because there was no implementation on the abstract class.
 */
public class TestAbstractInterfaceCall extends Test
{
    private int run;

    public TestAbstractInterfaceCall()
    {
        super(TestAbstractInterfaceCall.class.getCanonicalName());
    }

    public void test()
    {
        C4 c4 = new C4();
        try {
            log("calling C4.testMethod");
            c4.testMethod(this);
            log("called C4.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    @Override
    public String getExpected() {
        logExpected("calling C4.testMethod");
        logExpected("inside C4.interfaceMethod");
        logExpected("inside C3.testMethod");
        logExpected("called C4.testMethod");
        return super.getExpected();
    }
}