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
 * Class which provides the ability to run laod Byteman rules into TestNG style tests.
 * A class which inherits from this class will inherit theability to have BMScript and BMRule
 * annotations processed during testing.
 */
public class BMNGRunner implements IHookable
{

    // TODO work out what to do if tests are run in parallel and their rule sets overlap or have ocnflicting behaviour

    BMScript classSingleScriptAnnotation;
    BMScripts classMultiScriptAnnotation;
    BMRules classMultiRuleAnnotation;
    BMRule classSingleRuleAnnotation;
    Class<?> testKlazz;

    public void run(IHookCallBack callBack, ITestResult testResult)
    {
        callBack.runTestMethod(testResult);
    }

    @BeforeClass(alwaysRun = true)
    public void bmngBeforeClass() throws Exception
    {
        // TODO - track what we have loaded and ensure we always (try to) unload exavctly what we loaded
        // load rules associated with class annotations
        testKlazz = getClass();
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

    @AfterClass(alwaysRun = true)
    public void bmngAfterClass() throws Exception
    {
        // TODO - track what we have loaded and ensure we always (try to) unload exavctly what we loaded
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

    @BeforeMethod(alwaysRun = true)
    public void bmngBeforeTest(Method method) throws Exception
    {
        // TODO - track what we have loaded and ensure we always (try to) unload exavctly what we loaded
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

    @AfterMethod(alwaysRun = true)
    public void bmngAfterTest(Method method) throws Exception
    {
        // TODO - track what we have loaded and ensure we always (try to) unload exavctly what we loaded
        BMScript methodSingleScriptAnnotation = method.getAnnotation(BMScript.class);
        BMScripts methodMultiScriptAnnotation = method.getAnnotation(BMScripts.class);
        BMRule methodSingleRuleAnnotation = method.getAnnotation(BMRule.class);
        BMRules methodMultiRuleAnnotation = method.getAnnotation(BMRules.class);
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
