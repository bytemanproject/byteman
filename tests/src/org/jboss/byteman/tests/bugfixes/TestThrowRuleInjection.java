/*
* JBoss, Home of Professional Open Source
* Copyright 2011, Red Hat and individual contributors
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

import org.jboss.byteman.tests.auxiliary.TestThrowInterface;
import org.jboss.byteman.tests.auxiliary.TestThrowRuleSuper;

/**
 * Test for  BYTEMAN-156 which tests throw injection when an implementing class does not throw an
 * exception declared by a super class or super interface
 */
public class TestThrowRuleInjection extends TestThrowRuleSuper implements TestThrowInterface
{
    public TestThrowRuleInjection()
    {
        super(TestThrowRuleInjection.class.getCanonicalName());
    }

    public void test()
    {
        try {
            throwMethod();
        } catch (Exception e) {
            log("caught Exception " + e.getClass());
        }

        throwMethod2();

        throwMethod3();

        throwMethod4();

        checkOutput();
    }

    public void throwMethod() throws Exception
    {
        log("inside throwMethod()");
    }

    public void throwMethod2()
    {
        log("inside throwMethod2()");
    }

    public void throwMethod3()
    {
        log("inside throwMethod3()");
    }

    public void throwMethod4()
    {
        log("inside throwMethod4()");
    }

    @Override
    public String getExpected() {
        logExpected("inside throwMethod()");
        logExpected("caught Exception class java.lang.Exception" );
        logExpected("inside throwMethod2()");
        logExpected("inside throwMethod3()");
        logExpected("inside throwMethod4()");

        return super.getExpected();
    }
}

