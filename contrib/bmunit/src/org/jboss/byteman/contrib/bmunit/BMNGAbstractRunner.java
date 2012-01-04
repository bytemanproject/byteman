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
import org.testng.ITestResult;
import org.testng.TestNGException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * An abstract class which provides the ability to load Byteman rules into TestNG style tests.
 * The two subclasses of this class support the two alternative models for mixing Byteman
 * capability into TestNG test classes.
 */
public abstract class BMNGAbstractRunner implements IHookable
{
    BMScript classSingleScriptAnnotation;
    BMScripts classMultiScriptAnnotation;
    BMRules classMultiRuleAnnotation;
    BMRule classSingleRuleAnnotation;

    /**
     * implement standard run behaviour by devolving control back to the original runner
     * using the hook callback
     * @param callBack
     * @param testResult
     */
    public void run(IHookCallBack callBack, ITestResult testResult)
    {
        callBack.runTestMethod(testResult);
    }

    /**
     * provides behaviour to load rules specified via BMScript or BMRule annotations
     * attached to the supplied test class. the class is cached and used to resolve
     * subsequent requests to load and unload method level rules and to unload the
     * class level rules.
     * @param testKlazz
     * @throws Exception
     */
    public void bmngBeforeClass(Class<?> testKlazz) throws Exception
    {
        // load rules associated with class annotations
        classSingleScriptAnnotation = testKlazz.getAnnotation(BMScript.class);
        classMultiScriptAnnotation = testKlazz.getAnnotation(BMScripts.class);
        classSingleRuleAnnotation = testKlazz.getAnnotation(BMRule.class);
        classMultiRuleAnnotation = testKlazz.getAnnotation(BMRules.class);
        if (classMultiRuleAnnotation != null && classSingleRuleAnnotation != null) {
            throw new TestNGException("Use either BMRule or BMRules annotation but not both");
        }
        if (classMultiScriptAnnotation != null && classSingleScriptAnnotation != null) {
            throw new TestNGException("Use either BMScript or BMScripts annotation but not both");
        }
        // we load scripts before inline rules
        if (classSingleScriptAnnotation != null) {
            String name = BMRunnerUtil.computeBMScriptName(classSingleScriptAnnotation.value());
            String directory = BMRunnerUtil.normaliseLoadDirectory(classSingleScriptAnnotation);
            BMUnit.loadScriptFile(testKlazz, name, directory);
        } else if (classMultiScriptAnnotation != null) {
            BMScript[] scripts = classMultiScriptAnnotation.scripts();
            for (BMScript script : scripts) {
                String name = BMRunnerUtil.computeBMScriptName(script.value());
                String directory = BMRunnerUtil.normaliseLoadDirectory(script);
                BMUnit.loadScriptFile(testKlazz, name, directory);
            }
        }
        if (classSingleRuleAnnotation != null) {
            String scriptText = BMRunnerUtil.constructScriptText(new BMRule[] { classSingleRuleAnnotation });
            BMUnit.loadScriptText(testKlazz, null, scriptText);
        } else if (classMultiRuleAnnotation != null) {
            BMRule[] rules = classMultiRuleAnnotation.rules();
            String scriptText = BMRunnerUtil.constructScriptText(rules);
            BMUnit.loadScriptText(testKlazz, null, scriptText);
        }
    }

    /**
     * provides behaviour to unload rules specified via BMScript or BMRule annotations
     * attached to the supplied test class.
     * @throws Exception
     */
    public void bmngAfterClass(Class<?> testKlazz) throws Exception
    {
        // unload scripts associated with class annotations
        if (classSingleScriptAnnotation != null) {
            String name = BMRunnerUtil.computeBMScriptName(classSingleScriptAnnotation.value());
            BMUnit.unloadScriptFile(testKlazz, name);
        } else if (classMultiScriptAnnotation != null) {
            BMScript[] scripts = classMultiScriptAnnotation.scripts();
            for (BMScript script : scripts) {
                String name = BMRunnerUtil.computeBMScriptName(script.value());
                BMUnit.unloadScriptFile(testKlazz, name);
            }
        }
        // unload rules associated with class annotations
        if (classSingleRuleAnnotation != null) {
            BMUnit.unloadScriptText(testKlazz, null);
        } else if (classMultiRuleAnnotation != null) {
            BMRule[] rules = classMultiRuleAnnotation.rules();
            BMUnit.unloadScriptText(testKlazz, null);
        }
    }

    /**
     * provides behaviour to load rules specified via annotations associated with a specific
     * method of a test class.
     * @param method the test method about to be run
     * @throws Exception
     */
    public void bmngBeforeTest(Method method) throws Exception
    {
        BMScript methodSingleScriptAnnotation = method.getAnnotation(BMScript.class);
        BMScripts methodMultiScriptAnnotation = method.getAnnotation(BMScripts.class);
        BMRule methodSingleRuleAnnotation = method.getAnnotation(BMRule.class);
        BMRules methodMultiRuleAnnotation = method.getAnnotation(BMRules.class);
        if (methodMultiRuleAnnotation != null && methodSingleRuleAnnotation != null) {
            throw new TestNGException("Use either BMRule or BMRules annotation but not both");
        }
        if (methodMultiScriptAnnotation != null && methodSingleScriptAnnotation != null) {
            throw new TestNGException("Use either BMScript or BMScripts annotation but not both");
        }
        Class<?> testKlazz = method.getDeclaringClass();
        // we load scripts before inline rules
        if (methodSingleScriptAnnotation != null) {
            String name = BMRunnerUtil.computeBMScriptName(methodSingleScriptAnnotation.value(), method);
            String directory = BMRunnerUtil.normaliseLoadDirectory(methodSingleScriptAnnotation);
            BMUnit.loadScriptFile(testKlazz, name, directory);
        } else if (methodMultiScriptAnnotation != null) {
            BMScript[] scripts = methodMultiScriptAnnotation.scripts();
            for (BMScript script : scripts) {
                String name = BMRunnerUtil.computeBMScriptName(script.value(), method);
                String directory = BMRunnerUtil.normaliseLoadDirectory(script);
                BMUnit.loadScriptFile(testKlazz, name, directory);
            }
        }
        if (methodSingleRuleAnnotation != null) {
            String scriptText = BMRunnerUtil.constructScriptText(new BMRule[] { methodSingleRuleAnnotation });
            final String name = method.getName();
            BMUnit.loadScriptText(testKlazz, name, scriptText);
        } else if (methodMultiRuleAnnotation != null) {
            BMRule[] rules = methodMultiRuleAnnotation.rules();
            String scriptText = BMRunnerUtil.constructScriptText(rules);
            final String name = method.getName();
            BMUnit.loadScriptText(testKlazz, name, scriptText);
        }
    }

    /**
     * provides behaviour to unload rules specified via annotations associated with a specific
     * method of a test class.
     * @param method the test method about to be run
     * @throws Exception
     */
    public void bmngAfterTest(Method method) throws Exception
    {
        BMScript methodSingleScriptAnnotation = method.getAnnotation(BMScript.class);
        BMScripts methodMultiScriptAnnotation = method.getAnnotation(BMScripts.class);
        BMRule methodSingleRuleAnnotation = method.getAnnotation(BMRule.class);
        BMRules methodMultiRuleAnnotation = method.getAnnotation(BMRules.class);
        Class<?> testKlazz = method.getDeclaringClass();
        // we load scripts before inline rules
        if (methodSingleScriptAnnotation != null) {
            String name = BMRunnerUtil.computeBMScriptName(methodSingleScriptAnnotation.value(), method);
            String directory = BMRunnerUtil.normaliseLoadDirectory(methodSingleScriptAnnotation);
            BMUnit.unloadScriptFile(testKlazz, name);
        } else if (methodMultiScriptAnnotation != null) {
            BMScript[] scripts = methodMultiScriptAnnotation.scripts();
            for (BMScript script : scripts) {
                String name = BMRunnerUtil.computeBMScriptName(script.value(), method);
                String directory = BMRunnerUtil.normaliseLoadDirectory(script);
                BMUnit.unloadScriptFile(testKlazz, name);
            }
        }
        if (methodSingleRuleAnnotation != null) {
            String scriptText = BMRunnerUtil.constructScriptText(new BMRule[] { methodSingleRuleAnnotation });
            final String name = method.getName();
            BMUnit.unloadScriptText(testKlazz, name);
        } else if (methodMultiRuleAnnotation != null) {
            BMRule[] rules = methodMultiRuleAnnotation.rules();
            String scriptText = BMRunnerUtil.constructScriptText(rules);
            final String name = method.getName();
            BMUnit.unloadScriptText(testKlazz, name);
        }
    }
}
