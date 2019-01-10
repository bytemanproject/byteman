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

/**
 * regression test for BYTEMAN-376
 */
public class TestBindNull extends Test
{
    public TestBindNull()
    {
        super(TestBindNull.class.getCanonicalName());
    }

    private static int[] ints = new int[] { 0, 1, 2 };

    public static int[] getInts(int i) {
        return (i == 0 ? null : ints);
    }

    public void test()
    {
        try {
            triggerMethod();
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    public static String makeString(int[] ints)
    {
        if (ints == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        String prefix = "[";

        for(int i = 0; i < ints.length; i++) {
            builder.append(prefix);
            builder.append(ints[i]);
            prefix = ",";
        }
        builder.append("]");
        return builder.toString();
    }

    public void triggerMethod()
    {
        log("TestBindNull.triggerMethod()");
    }

    @Override
    public String getExpected()
    {
        logExpected("getInts(0) is " + makeString(getInts(0)));
        logExpected("getInts(1) is " + makeString(getInts(1)));
        logExpected("TestBindNull.triggerMethod()");

        return super.getExpected();
    }
}
