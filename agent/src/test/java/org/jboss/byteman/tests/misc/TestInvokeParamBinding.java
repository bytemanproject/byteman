/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009-10, Red Hat and individual contributors
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
 * (C) 2009-10,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;

/**
 * class used to test binding of an invoked method param binding $@
 */
public class TestInvokeParamBinding extends Test
{
    public TestInvokeParamBinding()
    {
        super(TestInvokeParamBinding.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling TestInvokeParamBinding.triggerMethod()");
            triggerMethod();
            log("called TestInvokeParamBinding.triggerMethod()");
        } catch (Exception e) {
            log(e);
        } catch(Throwable th) {
            System.out.println("Unexpected throwable : + th");
            th.printStackTrace();
        }
        checkOutput(true);
    }

    public void triggerMethod() throws Exception
    {
        log("calling subMethod(1, 1L, \"one\")");
        subMethod(1, 1L, "one");
        log("called subMethod(1, 1L, \"one\")");

        log("calling subMethod(2, 2L, \"two\")");
        subMethod(2, 2L, "two");
        log("called subMethod(2, 2L, \"two\")");
    }

    public void subMethod(int i, long l, String s)
    {
        log("inside subMethod(" + i + ", " + l + ", " + s + ")");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestInvokeParamBinding.triggerMethod()");
        logExpected("calling subMethod(1, 1L, \"one\")");
        logExpected("triggerMethod : $@[0] = " + this);
        logExpected("triggerMethod : $@[1] = " + 1);
        logExpected("triggerMethod : $@[2] = " + 1L);
        logExpected("triggerMethod : $@[3] = one");
        logExpected("inside subMethod(" + 1 + ", " + 1L + ", " + "one" + ")");
        logExpected("called subMethod(1, 1L, \"one\")");

        logExpected("calling subMethod(2, 2L, \"two\")");
        logExpected("triggerMethod : $@[0] = " + this);
        logExpected("triggerMethod : $@[1] = " + 2);
        logExpected("triggerMethod : $@[2] = " + 2L);
        logExpected("triggerMethod : $@[3] = two");
        logExpected("inside subMethod(" + 2 + ", " + 2L + ", " + "two" + ")");
        logExpected("called subMethod(2, 2L, \"two\")");

        logExpected("called TestInvokeParamBinding.triggerMethod()");

        return super.getExpected();
    }
}