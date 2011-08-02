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
 * Test for bug reported by Kabir Khan pre-JIRA where the Transformer failed to accept a CALL
 * trigger location specified with an empty argument list.
 */
public class TestMethodActualAgainstFormal extends Test
{
    public TestMethodActualAgainstFormal()
    {
        super(TestMethodActualAgainstFormal.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling checkMethod()");
            checkMethod();
            log("called checkMethod()");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    public void checkMethod()
    {
        log("inside checkMethod()");
    }

    @Override
    public String getExpected() {
        logExpected("calling checkMethod()");
        logExpected("AT ENTRY trace for checkMethod()");
        logExpected("inside checkMethod()");
        logExpected("called checkMethod()");

        return super.getExpected();
    }
}