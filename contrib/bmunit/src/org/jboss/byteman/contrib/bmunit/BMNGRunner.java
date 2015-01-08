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
     * @throws Exception if the test cannto be run
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
