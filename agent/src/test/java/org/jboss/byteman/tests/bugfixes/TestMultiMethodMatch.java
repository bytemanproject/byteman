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
 * Test for BYTEMAN-65 to check that an ambiguous method name which matches more than one method is handled correctly
 */
public class TestMultiMethodMatch extends Test
{
    public TestMultiMethodMatch()
    {
        super(TestMultiMethodMatch.class.getCanonicalName());
    }

    public void test()
    {
        try {
            multiMatch("bar", "baz");
        } catch (Exception e) {
            log(e);
        }

        try {
            multiMatch(1);
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    /**
     * the multiMatch rule will match this method and its $1 parameter should be processed as a String
     * @param bar
     * @param baz
     */
    public void multiMatch(String bar, String baz)
    {
        log("inside multiMatch(String, String) " + this + " " + bar + " " + baz);
    }

    /**
     * the multiMatch rule will match this method and its $1 parameter should be processed as an int
     * @param foo
     */
    public void multiMatch(int foo)
    {
        log("inside multiMatch(int) " + this + " " + foo);
    }

    @Override
    public String getExpected() {
        logExpected("injected multiMatch() " + this + " bar");
        logExpected("inside multiMatch(String, String) " + this + " bar baz");
        logExpected("injected multiMatch() " + this + " 1");
        logExpected("inside multiMatch(int) " + this + " 1");

        return super.getExpected();
    }
}