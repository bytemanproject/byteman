/**
 * Simple test program to exercise BMUnitRunner code
 */
package test;

import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

// this runner is like the normal JUnit4 runner i.e. it runs all methods with an @Test annotation
// but it also pays atention to BMRules annotations. it will autoload the agent into the JVM if needed
@RunWith(BMUnitRunner.class)
// A class level annotation identifies a rule script which is loaded before any test is run and only unloaded
// at the end of testing
@BMScript(dir="scripts")
public class UnitTest
{
    @Test
    // A method annotation identifies a rule script which is loaded before calling the test method and
    // then unloaded after the test has run
    @BMScript(dir="scripts")
    public void testOne()
    {
        tryOne();
        tryTwo();
        tryThree();
        tryAlways();
    }

    @Test
    // If you supply a value then this is used when looking for the script otherwise the method name is used
    @BMScript(value="two", dir="scripts")
    public void testTwo()
    {
        tryOne();
        tryTwo();
        tryThree();
        tryAlways();
    }

    @Test
    // At some point soon you should be able to specify rules directly using annotations
    // either a one off rule using @BMRule(...)
    // or a set of rules using @BMRules( @BMRUle(...), ... @BMRule(...))
    @BMRule(name="UnitTest tryThree trace rule",
            targetClass = "UnitTest",
            targetMethod = "tryThree",
            condition = "TRUE",
            action="traceln(\"Byteman: intercepted call to tryThree\");"
    )
    public void testThree()
    {
        tryOne();
        tryTwo();
        tryThree();
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

    public void tryThree()
    {
        // eventually message will be injected by Byteman in testThree
    }

    public void tryAlways()
    {
        // message injected by Byteman in all tests
    }

}
