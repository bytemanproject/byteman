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
 * @authors Andrew Dinn
 */

package org.jboss.byteman.tests.bugfixes;
import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestInnerAuxiliary;
/**
 * Test class to ensure injection into inner classes works as expected
 * with both compiled and interpreted rules
 */
public class TestInnerClasses extends Test
{
    public TestInnerClasses()
    {
        super(TestInnerClasses.class.getCanonicalName());
    }

    public void test()
    {
        // create the test auxiliary

        TestInnerAuxiliary auxiliary = new TestInnerAuxiliary(this);

        auxiliary.testInnerClasses();

        checkOutput(true);
    }

    @Override
    public String getExpected() {
        logExpected("inside PublicInner.testPublic");

        logExpected("inside PublicInner.testPrivate");

        logExpected("inside PrivateInner.testPublic");

        logExpected("inside PrivateInner.testPrivate");

        logExpected("overriding injection into PublicStaticInner.testPublic");
        logExpected("interface injection into PublicStaticInner.testPublic");
        logExpected("inside PublicStaticInner.testPublic");

        logExpected("overriding injection into PublicStaticInner.testPrivate");
        logExpected("interface injection into PublicStaticInner.testPrivate");
        logExpected("inside PublicStaticInner.testPrivate");

        logExpected("overriding injection into PrivateStaticInner.testPublic");
        logExpected("interface injection into PrivateStaticInner.testPublic");
        logExpected("accessed public instance field of private class PrivateStaticInner");
        logExpected("inside PrivateStaticInner.testPublic");

        logExpected("overriding injection into PrivateStaticInner.testPrivate");
        logExpected("interface injection into PrivateStaticInner.testPrivate");
        logExpected("accessed public static field of private class PrivateStaticInner");
        logExpected("inside PrivateStaticInner.testPrivate");

        return super.getExpected();
    }
}
