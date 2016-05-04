/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat and individual contributors
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

package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;

/**
 * class to test use of downcast in rule binding
 */
public class TestIncludeExclude extends Test
{
    public TestIncludeExclude()
    {
        super(TestIncludeExclude.class.getCanonicalName());
    }

    public void test()
    {
        log("calling TestIncludeExclude.sayHello()");
        sayHello();
        log("called TestIncludeExclude.sayHello()");

        checkOutput(true);
    }
    
    private void sayHello() {
        log("Hello !");
    }

    @Override
    public String getExpected() {
        String testcase = System.getProperty("testcase");
        
        if ("INCLUDED".equals(testcase) || "NOT_EXCLUDED".equals(testcase)) {
            logExpected("calling TestIncludeExclude.sayHello()");
            logExpected("Hello !");
            logExpected("CU later...");
            logExpected("called TestIncludeExclude.sayHello()");
        }
        else if ("EXCLUDED".equals(testcase) || "NOT_INCLUDED".equals(testcase)) {
            logExpected("calling TestIncludeExclude.sayHello()");
            logExpected("Hello !");
            logExpected("called TestIncludeExclude.sayHello()");
        }

        return super.getExpected();
    }
}
