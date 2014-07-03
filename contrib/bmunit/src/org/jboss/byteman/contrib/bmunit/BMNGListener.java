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

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;
import org.testng.TestNGException;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

/**
 * Class which provides the ability to load Byteman rules into TestNG style tests.
 * A class which inherits from this class will inherit the ability to have BMScript and BMRule
 * annotations processed during testing.
 */
public class BMNGListener extends BMNGAbstractRunner implements IInvokedMethodListener, ITestListener
{

    Class currentClazz = null;
    // TODO work out what to do if tests are run in parallel and their rule sets overlap or have ocnflicting behaviour

    public void switchClass(Class newClazz)
    {
        if (currentClazz != null) {
            try {
                bmngAfterClass(currentClazz);
            } catch (Exception e) {
                try {
                    BMUnitConfigState.resetConfigurationState(currentClazz);
                } catch (Exception e1) {
                }
                throw new TestNGException(e);
            }
        }
        if (newClazz != null) {
            currentClazz = newClazz;
            try {
                bmngBeforeClass(newClazz);
            } catch (Exception e) {
                try {
                    BMUnitConfigState.resetConfigurationState(newClazz);
                } catch (Exception e1) {
                }
                throw new TestNGException(e);
            }
        }
    }

    private boolean checkBMNGListener(Class<?> clazz)
    {
        Listeners listeners = clazz.getAnnotation(Listeners.class);
        if (listeners == null) {
            return false;
        }
        Class<? extends ITestNGListener>[] clazzarray = listeners.value();
        for (int i = 0; i < clazzarray.length; i++) {
            if (clazzarray[i] == BMNGListener.class) {
                return true;
            }
        }
        return false;
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        Method javaMethod = method.getTestMethod().getMethod();
        Class clazz = javaMethod.getDeclaringClass();
        if (!checkBMNGListener(clazz)) {
            return;
        }
        if (clazz != currentClazz) {
            switchClass(clazz);
        }
        try {
            bmngBeforeTest(javaMethod);
        } catch (Exception e) {
            try {
                BMUnitConfigState.resetConfigurationState(javaMethod);
            } catch(Exception e1) {
            }
            throw new TestNGException(e);
        }
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        Method javaMethod = method.getTestMethod().getMethod();
        Class clazz = javaMethod.getDeclaringClass();
        if (!checkBMNGListener(clazz)) {
            return;
        }
        try {
            bmngAfterTest(javaMethod);
            try {
                BMUnitConfigState.resetConfigurationState(javaMethod);
            } catch(Exception e1) {
            }
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
        /*
         * TestNG calls onStart for all classes in a suite before running any
         * of their test methods which is basically a complete pile of pooh.
         *
         * so we don't do class specific before processing at this level.
         * instead we do it lazily when methods are notified by detecting
         * changes in the current class at that point
        Class<?> testClass = context.getCurrentXmlTest().getXmlClasses().get(0).getSupportClass();
        try {
            bmngBeforeClass(testClass);
        } catch (Exception e) {
            try {
                BMUnitConfigState.resetConfigurationState(testClass);
            } catch(Exception e1) {
            }
            throw new TestNGException(e);
        }
        */
    }

    public void onFinish(ITestContext context) {
        /*
         * TestNG calls onFinish for all classes in a suite after running all
         * of their test methods which is basically a complete pile of pooh.
         *
         * so we don't do after class specific processing at this level.
         * instead we do it pre-emptively when methods are notified by
         * detecting changes in the current class at that point
        Class<?> testClass = context.getCurrentXmlTest().getXmlClasses().get(0).getSupportClass();
        try {
            bmngAfterClass(testClass);
        } catch (Exception e) {
            try {
                BMUnitConfigState.resetConfigurationState(testClass);
            } catch(Exception e1) {
            }
            throw new TestNGException(e);
        }
        */
        // run any left over after class processing
        switchClass(null);
    }
}
