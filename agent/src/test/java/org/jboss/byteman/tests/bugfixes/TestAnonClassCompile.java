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

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestAnonAuxiliary;

/**
 * Test for BYTEMAN-235 to check that we can compile references to instances of anonymous classes.
 */
public class TestAnonClassCompile extends Test
{
    public TestAnonClassCompile()
    {
        super(TestAnonClassCompile.class.getCanonicalName());
    }

    public int getFoo()
    {
        return 1;
    }
    public static TestAnonAuxiliary anonInstance = null;

    public void test()
    {
        final int foo = getFoo();
        TestAnonAuxiliary auxiliary = new  TestAnonAuxiliary() {
            int bar = foo;
            public void test(TestAnonClassCompile test) {
                if (bar == 1) {
                    test.log("inside TestAnonClassCompile$1");
                }
                super.test(test);
		anonInstance = this;
            }
        };

        auxiliary.test(this);

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("intercepted TestAnonClassCompile$1 recipient = " + anonInstance);
        logExpected("inside TestAnonClassCompile$1");
        logExpected("inside TestAnonAuxiliary" );

        return super.getExpected();
    }
}
