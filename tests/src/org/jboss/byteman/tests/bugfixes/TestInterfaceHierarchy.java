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
package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.C1;
import org.jboss.byteman.tests.auxiliary.C2;
import org.jboss.byteman.tests.auxiliary.I3;

/**
 * Test for bug reported by Flavia Rainone JIRA BYTEMAN-140 where the Transformer injection through interfaces
 * was failing to traverse the interface extends hierarchy.
 */
public class TestInterfaceHierarchy extends Test
{
    private int run;

    public TestInterfaceHierarchy()
    {
        super(TestInterfaceHierarchy.class.getCanonicalName());
        run = 0;
    }

    public void test()
    {
        run = 1;

        I3 c1 = new C1();
        try {
            log("calling C1.testMethod()");
            c1.testMethod(this);
            log("called C1.testMethod()");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        run =  2;

        I3 c2 = new C2();
        try {
            log("calling C2.testMethod()");
            c2.testMethod(this);
            log("called C2.testMethod()");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public void emptySignature()
    {
        log("inside emptySignature()");
    }

    @Override
    public String getExpected() {
        if (run == 1) {
            logExpected("calling C1.testMethod()");
            logExpected("I3.testMethod() AT ENTRY");
            logExpected("^I3.testMethod() AT ENTRY");
            logExpected("inside C1.testMethod()");
            logExpected("called C1.testMethod()");
        } else if (run == 2) {
            logExpected("calling C2.testMethod()");
            logExpected("^I3.testMethod() AT ENTRY");
            logExpected("inside C2.testMethod()");
            logExpected("I3.testMethod() AT ENTRY");
            logExpected("^I3.testMethod() AT ENTRY");
            logExpected("inside C1.testMethod()");
            logExpected("called C2.testMethod()");
        }
        return super.getExpected();
    }
}