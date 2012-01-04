/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat and individual contributors as identified
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

package org.jboss.byteman.contrib.bmunit;

import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNGException;

import java.lang.reflect.Method;

/**
 * Class which provides the ability to run laod Byteman rules into TestNG style tests.
 * A class which inherits from this class will inherit theability to have BMScript and BMRule
 * annotations processed during testing.
 */
public class BMNGListener extends BMNGAbstractRunner implements IInvokedMethodListener, ITestListener
{

    // TODO work out what to do if tests are run in parallel and their rule sets overlap or have ocnflicting behaviour

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        Method javaMethod = method.getTestMethod().getMethod();
        try {
            bmngBeforeTest(javaMethod);
        } catch (Exception e) {
            throw new TestNGException(e);
        }
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        Method javaMethod = method.getTestMethod().getMethod();
        try {
            bmngAfterTest(javaMethod);
        } catch (Exception e) {
            throw new TestNGException(e);
        }
    }

    public void onTestStart(ITestResult result) {
    }

    public void onTestSuccess(ITestResult result) {
    }

    public void onTestFailure(ITestResult result) {
    }

    public void onTestSkipped(ITestResult result) {
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    public void onStart(ITestContext context) {
        Class<?> testClass = context.getCurrentXmlTest().getXmlClasses().get(0).getSupportClass();
        try {
            bmngBeforeClass(testClass);
        } catch (Exception e) {
            throw new TestNGException(e);
        }
    }

    public void onFinish(ITestContext context) {
        Class<?> testClass = context.getCurrentXmlTest().getXmlClasses().get(0).getSupportClass();
        try {
            bmngAfterClass(testClass);
        } catch (Exception e) {
            throw new TestNGException(e);
        }
    }
}
