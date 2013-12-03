/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
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

/**
 * Test for BYTEMAN-254 to ensure that compilation of field expressions in
 * conditions does not fail with an invalid stack height
 */
package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;

public class TestStackHeight extends Test
{
    private boolean testField;

    public TestStackHeight()
    {
        super("TestStackHeight");
        testField = false;
    }

    public void setTestField(boolean value)
    {
        log("setting field " + testField);
        testField = value;
        log("set field " + testField);
    }

    public boolean isTestField()
    {
        return testField;
    }
    public void test()
    {
        TestStackHeight test = new TestStackHeight();
        setTestField(true);
    }

    @Override
    public String getExpected() {
        logExpected("setting field false");
        logExpected("set field true");

        return super.getExpected();
    }
}
