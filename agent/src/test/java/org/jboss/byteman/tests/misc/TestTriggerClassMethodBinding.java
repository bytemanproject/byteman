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

package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.C1;
import org.jboss.byteman.tests.auxiliary.C2;

/**
 * Created by IntelliJ IDEA.
 * User: adinn
 * Date: 18/06/13
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class TestTriggerClassMethodBinding extends Test {

    public TestTriggerClassMethodBinding()
    {
        super(TestTriggerClassMethodBinding.class.getCanonicalName());
    }

    public void test()
    {
        C1 c1 = new C1();
        C2 c2 = new C2();

        try {
            log("calling C1.testMethod(Test)");
            c1.testMethod(this);
            log("called C1.testMethod(Test)");
            log("calling C2.testMethod(Test)");
            c2.testMethod(this);
            log("called C2.testMethod(Test)");
        } catch (Exception e) {
            log("caught " + e.getMessage());
        }

        checkOutput(true);
    }


    @Override
    public String getExpected() {
        logExpected("calling C1.testMethod(Test)");
        logExpected("Trigger class : org.jboss.byteman.tests.auxiliary.C1");
        logExpected("Trigger method : testMethod(org.jboss.byteman.tests.Test) void");
        logExpected("inside C1.testMethod()");
        logExpected("called C1.testMethod(Test)");
        logExpected("calling C2.testMethod(Test)");
        logExpected("Trigger class : org.jboss.byteman.tests.auxiliary.C2");
        logExpected("Trigger method : testMethod(org.jboss.byteman.tests.Test) void");
        logExpected("inside C2.testMethod()");
        logExpected("Trigger class : org.jboss.byteman.tests.auxiliary.C1");
        logExpected("Trigger method : testMethod(org.jboss.byteman.tests.Test) void");
        logExpected("inside C1.testMethod()");
        logExpected("called C2.testMethod(Test)");

        return super.getExpected();
    }
}
