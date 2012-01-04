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
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void bmngBeforeClass() throws Exception
    {
        super.bmngBeforeClass(getClass());
    }

    /**
     * method inherited by a subclass and recognized by TestNG which ensures that
     * Byteman rules specified using @BMRule or @BMScript annotations attached to
     * the subclass are unloaded automatically after executing all of its test methods.
     * @throws Exception
     */
    @AfterClass(alwaysRun = true)
    public void bmngAfterClass() throws Exception
    {
        super.bmngAfterClass(getClass());
    }

    /**
     * method inherited by a subclass and recognized by TestNG which ensures that
     * Byteman rules specified using @BMRule or @BMScript annotations attached to
     * a test method are unloaded automatically before executing the method.
     * @throws Exception
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
     * @throws Exception
     */
    @AfterMethod(alwaysRun = true)
    public void bmngAfterTest(Method method) throws Exception
    {
        super.bmngAfterTest(method);
    }
}
