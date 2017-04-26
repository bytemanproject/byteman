/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat and individual contributors
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

package org.jboss.byteman.tests.access;

import org.jboss.byteman.tests.Test;

/**
 * Test to check that jdk9 access to members of classes that don't
 * currently support MethodHandle.Lookup access can still default
 * to using reflection.
 *
 * This only applies for classes in the java.* hierarchy and sun.*
 * hierachy (other than java.lang.Thread and sun.invoke.* classes).
 * The current Lookup code refuses to create private lookups for
 * such classes so the Jigsaw access enabler has to fall back to
 * using reflection when attempting to access private, and
 * protected (or non-exported public) members of these classes.
 */
public class TestNonLookupAccess extends Test
{

    public TestNonLookupAccess()
    {
        super("org.jboss.byteman.tests.access.TestNonLookupAccess");
    }

    static private int i = 1;
    static private boolean b = true;
    
    public void test()
    {
        try {
            log("calling TestNonLookupAccess.triggerMethod()");
            triggerMethod(i, b);
            log("called TestNonLookupAccess.triggerMethod()");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod(int i, boolean b)
    {
        log("inside TestNonLookupAccess.triggerMethod()");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestNonLookupAccess.triggerMethod()");
        logExpected("triggerMethod : int value is " + i);
        logExpected("triggerMethod : bool value is " + b);
        logExpected("inside TestNonLookupAccess.triggerMethod()");
        logExpected("called TestNonLookupAccess.triggerMethod()");

        return super.getExpected();
    }
}
