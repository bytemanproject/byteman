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

package org.jboss.byteman.contrib.bmunit;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.TestNGException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * A TestNG runner class which can be subclassed by a test class in order to inherit the
 * ability to process @BMRule and @BMScript annotations.
 */
public class BMNGRunner extends BMNGAbstractRunner
{
    /**
     * method inherited by a subclass and recognized by TestNG which ensures that
     * Byteman rules specified using @BMRule or @BMScript annotations attached to
     * the subclass are loaded automatically before executing any of its test methods.
     * @throws Exception if the test cannot be run
     */
    @BeforeClass(alwaysRun = true)
    public void bmngBeforeClass() throws Exception
    {
        Class<?> clazz = getClass();
        switchClass(clazz);
    }

    /**
     * method inherited by a subclass and recognized by TestNG which ensures that
     * Byteman rules specified using @BMRule or @BMScript annotations attached to
     * the subclass are unloaded automatically after executing all of its test methods.
     * @throws Exception if cleanup fails
     */
    @AfterClass(alwaysRun = true)
    public void bmngAfterClass() throws Exception
    {
        Class<?> clazz = getClass();
        switchClass(null);
    }

    /**
     * method inherited by a subclass and recognized by TestNG which ensures that
     * Byteman rules specified using @BMRule or @BMScript annotations attached to
     * a test method are unloaded automatically before executing the method.
     * @throws Exception if the test cannot be run
     */
    @BeforeMethod(alwaysRun = true)
    public void bmngBeforeTest(Method method) throws Exception
    {
        super.bmngBeforeTest(method);
    }

    /**
     * method inherited by a subclass and recognized by TestNG which ensures that
     * Byteman rules specified using @BMRule or @BMScript annotations attached to
     * a test method are unloaded automatically before executing the method.
     * @throws Exception if cleanup fails
     */
    @AfterMethod(alwaysRun = true)
    public void bmngAfterTest(Method method) throws Exception
    {
        super.bmngAfterTest(method);
    }
}
