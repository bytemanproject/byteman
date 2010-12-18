/**
 * Simple test program to exercise BMUnitRunner code
 */
package test;

import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMTestCase;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

// this runner is like the normal JUnit4 runner i.e. it runs all methods with an @Test annotation
// but it also pays atentionto BMRules annotations. it will autoload the agent into the JVM if needed
@RunWith(BMUnitRunner.class)
// A class level annotation identifies a rule script which is loaded before any test is run and only unloaded
// at the end of testing
@BMRules
public class UnitTest
{
    @Test
    // A method annotation identifies a rule script which is loaded before calling the tes method and
    // then unloaded after the test has run
    @BMRules
    public void testOne()
    {
        tryOne();
        tryTwo();
        tryAlways();
    }

    @Test
    // If you supply a value then this is used when looking for the script otherwise the method name is used
    @BMRules("two")
    public void testTwo()
    {
        tryOne();
        tryTwo();
        tryAlways();
    }

    // The remaining methods have code injected into by the scripts for the class and test methods

    public void tryOne()
    {
        // message injected by Byteman in testOne
    }

    public void tryTwo()
    {
        // message injected by Byteman in testTwo
    }

    public void tryAlways()
    {
        // message injected by Byteman in all tests
    }

}
