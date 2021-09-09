/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat and individual contributors
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
import org.jboss.byteman.tests.auxiliary.StringsArray;
import org.jboss.byteman.tests.auxiliary.StringsLinked;

/*
 * Check that type checking of AS TARGET rules uses the type specified in the
 * rule CLASS/INTERFACE clause to type check the rule and that type checking of
 * AS TARGET rules uses the type of the class the rule is injected into
 *
 * NOTE: This test expects to see 2 type check errors for the AS TARGET rule on
 * i/face Strings that tries to reference method append. These occur whether
 * the trigger class is StringsLinked or StringsArray. It should expects to see a
 * type check error for the AS TRIGGER rule on i/face Strings when injecting into
 * class StringsArray since it also tries to reference method append.
 */

public class TestAsTarget extends Test {
    public TestAsTarget() {
        super(TestAsTarget.class.getCanonicalName());
    }

    public void test()
    {
        StringsArray strings = new StringsArray();
        StringsLinked strings2 = new StringsLinked();

        strings.add("world!");
        strings2.add("world!");

        for (String string : strings) {
            log("Found " + string);
        }

        for (String string : strings2) {
            log("Found " + string);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("Found Hello");
        logExpected("Found world!");

        logExpected("Found Hello");
        logExpected("Found funny");
        logExpected("Found old");
        logExpected("Found world!");

        return super.getExpected();
    }
}
