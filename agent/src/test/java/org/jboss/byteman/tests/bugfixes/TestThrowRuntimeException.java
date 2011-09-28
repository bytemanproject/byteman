/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat and individual contributors
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

/**
 * Test for BYTEMAN-35 to check that throw actions work.
 */
public class TestThrowRuntimeException extends Test
{
    public TestThrowRuntimeException()
    {
        super(TestThrowRuntimeException.class.getCanonicalName());
    }

    public void test()
    {
        try {
            throwMethod();
        } catch (Exception e) {
            log("caught Exception " + e.getClass());
        }

        checkOutput();
    }

    public void throwMethod()
    {
        log("inside throwMethod()");
    }

    @Override
    public String getExpected() {
        logExpected("inside throwMethod()");
        logExpected("throwing NullPointerException");
        logExpected("caught Exception class java.lang.NullPointerException" );

        return super.getExpected();
    }
}