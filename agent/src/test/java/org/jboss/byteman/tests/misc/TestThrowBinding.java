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
 * class used to test binding of a return value in an AT EXIT rule
 */
public class TestThrowBinding extends Test
{
    public TestThrowBinding()
    {
        super(TestThrowBinding.class.getCanonicalName());
    }

    public void test()
    {
        String result;
        
        try {
            log("calling TestThrowBinding.triggerMethod(0)");
            result = triggerMethod(0);
            log("called TestThrowBinding.triggerMethod(0) ==> " + result);
        } catch (Exception e) {
            log("caught " + e.getMessage());
        }
        try {
            log("calling TestThrowBinding.triggerMethod(1)");
            result = triggerMethod(1);
            log("called TestThrowBinding.triggerMethod(1) ==> " + result);
        } catch (Exception e) {
            log("caught " + e.getMessage());
        }

        checkOutput(true);
    }

    public String triggerMethod(int i) throws Exception
    {
        log("inside TestThrowBinding.triggerMethod()");
        if (i == 0) {
            throw new Exception("expected " + i);
        } else {
            int j = i + 1;
            throw new Exception("expected " + j);
        }
    }

    @Override
    public String getExpected() {
        logExpected("calling TestThrowBinding.triggerMethod(0)");
        logExpected("inside TestThrowBinding.triggerMethod()");
        logExpected("triggerMethod : triggered with expected 0");
        logExpected("caught expected 0");
        logExpected("calling TestThrowBinding.triggerMethod(1)");
        logExpected("inside TestThrowBinding.triggerMethod()");
        logExpected("triggerMethod : triggered with expected 2");
        logExpected("caught expected 2");

        return super.getExpected();
    }
}