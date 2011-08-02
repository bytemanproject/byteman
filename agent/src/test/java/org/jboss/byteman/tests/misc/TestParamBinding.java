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
 * class used to test binding of a param bindings $# and $*
 */
public class TestParamBinding extends Test
{
    public TestParamBinding()
    {
        super(TestParamBinding.class.getCanonicalName());
    }

    public void test()
    {
        String result;

        try {
            log("calling TestParamBinding.triggerMethod(0)");
            result = triggerMethod(0);
            log("called TestParamBinding.triggerMethod(0) ==> " + result);
        } catch (Exception e) {
            log(e);
        }
        try {
            log("calling TestParamBinding.triggerMethod(1)");
            result = triggerMethod(1);
            log("called TestParamBinding.triggerMethod(1) ==> " + result);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public String triggerMethod(int i) throws Exception
    {
        log("inside TestParamBinding.triggerMethod()");
        if (i == 0) {
            return "" + i;
        } else {
            int j = i + 1;
            return "" + j;
        }
    }

    @Override
    public String getExpected() {
        logExpected("calling TestParamBinding.triggerMethod(0)");
        logExpected("inside TestParamBinding.triggerMethod()");
        logExpected("triggerMethod : triggered with 1 params");
        logExpected("triggerMethod : $*[1] = 0");
        logExpected("called TestParamBinding.triggerMethod(0) ==> 0");
        logExpected("calling TestParamBinding.triggerMethod(1)");
        logExpected("inside TestParamBinding.triggerMethod()");
        logExpected("triggerMethod : triggered with 1 params");
        logExpected("triggerMethod : $*[1] = 1");
        logExpected("called TestParamBinding.triggerMethod(1) ==> 2");

        return super.getExpected();
    }
}