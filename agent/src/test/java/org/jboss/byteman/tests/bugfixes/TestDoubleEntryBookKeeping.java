/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat and individual contributors
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
 * test for BYTEMAN-302 passing a local boolean var into a rule causes a ClassCastException
  * because the local bool is saved using an isave and hence thought to be an int
 */
public class TestDoubleEntryBookKeeping extends Test
{
    public TestDoubleEntryBookKeeping()
    {
        super(TestDoubleEntryBookKeeping.class.getCanonicalName());
    }

    public static int value = 123;
    public void test()
    {
        try {
            log("calling TestDoubleEntryBookKeeping.triggerMethod");
            triggerMethod(value);
            log("called TestDoubleEntryBookKeeping.triggerMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

    }

    public void triggerMethod(int value)
    {
        log("inside TestDoubleEntryBookKeeping.triggerMethod");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestDoubleEntryBookKeeping.triggerMethod");
        logExpected("TestDoubleEntryBookKeeping 1 value = " + value);
        logExpected("TestDoubleEntryBookKeeping 2 value = " + value);
        logExpected("inside TestDoubleEntryBookKeeping.triggerMethod");
        logExpected("called TestDoubleEntryBookKeeping.triggerMethod");
        return super.getExpected();
    }
}
