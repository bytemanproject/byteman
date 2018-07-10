/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-2018 Red Hat and individual contributors
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
 */

package testimport;

import byteman.tests.Test;

import runner.Runner;

public class TestImport extends Test
{
    public TestImport()
    {
        super(TestImport.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling TestImport.triggerMethod");
            triggerMethod();
            log("called TestImport.triggerMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod()
    {
        Runnable r = new TestRunnable(this);
        Runner.run(r);
    }

    @Override
    public String getExpected() {
        logExpected("calling TestImport.triggerMethod");
        logExpected("TestImport $runnable");
        logExpected("Runnable.run");
        logExpected("called TestImport.triggerMethod");

        return super.getExpected();
    }
}
