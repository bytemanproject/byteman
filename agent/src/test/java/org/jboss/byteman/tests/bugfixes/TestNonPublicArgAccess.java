/*
 * JBoss, Home of Professional Open Source
 * Copyright 2022, Red Hat and individual contributors
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

/*
 * Test case for BYTEMAN-425
 *
 * Check that rules which receive a value with a non-public type can
 * invoke a method which expects an argument belonging to the non-public
 * type, its super or one of its interfaces. This is never a problem for
 * non-compiled rules because all operations are performed either
 * reflectively or using method handles where arguments are stored and
 * passed as generic objects. However, with rules that are compiled to
 * bytecode the non-public type cannot be referenced from the generated
 * bytecode. The compiler needs to recognize such cases and execute the
 * method by passing the value as a generic object to a reflective method
 * or method handle rather than using a direct or virtual call.
 *
 */

public class TestNonPublicArgAccess extends Test {
    public TestNonPublicArgAccess() {
        super("org.jboss.byteman.tests.access.TestNonPublicArgAccess");
    }

    public void test()
    {
        try {
            TestArg arg = new TestArg("Byteman!");
            log("calling TestAccess.triggerMethod()");
            triggerMethod(arg);
            log("called TestAccess.triggerMethod()");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    // Method into which rule will be injected
    private void triggerMethod(TestArg arg)
    {
        log("inside TestAccess.triggerMethod()");
    }

    // Method rule will call to test private argtype detection
    public void logArgValue(TestArgAbstract arg)
    {
        log("Argument is " + arg.value());
    }

    @Override
    public String getExpected() {
        logExpected("calling TestAccess.triggerMethod()");
        logExpected("triggerMethod arg = Byteman!");
        logExpected("Argument is Byteman!");
        logExpected("inside TestAccess.triggerMethod()");
        logExpected("called TestAccess.triggerMethod()");

        return super.getExpected();
    }

    public abstract static class TestArgAbstract {
        public abstract String value();
    }

    static class TestArg extends TestArgAbstract {
        private final String value;
        public TestArg(String value) {
            this.value = value;
        }
        @Override
        // Method rule will call to test private owner type detection
        public String value() {
            return value;
        }
    }
}
