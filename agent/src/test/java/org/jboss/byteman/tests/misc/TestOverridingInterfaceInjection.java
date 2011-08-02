/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-10, Red Hat and individual contributors
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
 * (C) 2009-10,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliarySub;

/**
 * Test class to ensure injection into interfaces with overriding works as expected
 */
public class TestOverridingInterfaceInjection extends Test
{
    public TestOverridingInterfaceInjection()
    {
        super(TestOverridingInterfaceInjection.class.getName());
    }

    public void test()
    {
        // this is much the same as the TestEntry code but we use an interface rule to inject
        // rule trace into the parent testMethod. note we cannot inject into constructors via
        // an interface rule

        try {
        // n.b. it is important that we do not employ the parent class TestEntryExitAuxiliary
        // in the code here. that guarantees when we create the subclass instance here
        // that the parent class has not yet been loaded. this verifies the fix to BYTEMAN-80
        TestEntryExitAuxiliarySub testAuxiliarySub;
        log("creating TestEntryExitAuxiliarySub");
        testAuxiliarySub = new TestEntryExitAuxiliarySub(this);
        log("created TestEntryExitAuxiliarySub");
        log("calling TestEntryExitAuxiliarySub.testMethod");
        testAuxiliarySub.testMethod();
        log("called TestEntryExitAuxiliarySub.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestEntryExitAuxiliarySub");
        // parent constructor will log first
        logExpected("inside TestEntryExitAuxiliary(Test)");
        logExpected("inside TestEntryExitAuxiliarySub(Test)");
        logExpected("created TestEntryExitAuxiliarySub");
        // we see injected code in the subclass method and then in the superclass method
        logExpected("calling TestEntryExitAuxiliarySub.testMethod");
        logExpected("ENTRY triggered in ^TestInterface.testMethod");
        logExpected("inside TestEntryExitAuxiliarySub.testMethod");
        logExpected("calling TestEntryExitAuxiliary.testMethod");
        logExpected("ENTRY triggered in ^TestInterface.testMethod");
        logExpected("inside TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliarySub.testMethod");

        return super.getExpected();
    }
}