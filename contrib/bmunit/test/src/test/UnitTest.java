/**
 * Simple test program to exercise BMUnitRunner code
 */
package test;

import org.jboss.byteman.contrib.bmunit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

// this runner is like the normal JUnit4 runner i.e. it runs all methods with an @Test annotation
// but it also pays atention to BMRules annotations. it will autoload the agent into the JVM if needed
@RunWith(BMUnitRunner.class)
// A class level annotation identifies a rule script file which is loaded before any test is run and only
// unloaded at the end of testing.
@BMScript(dir="test/scripts")
// You can also specify rules directly using annotations
// either a one off rule using @BMRule(...)
// or a set of rules using @BMRules( @BMRUle(...), ... @BMRule(...))
// but not both
// note that rules defined using @BMRule get loaded and, hence, injected after rules defined using
// @BMScript
// clearly this is a little clumsy, especially when embedding literal strings in the rule text
// a macro facility as per any bog standard Lisp would be a big help here
// but this does ensure that the rule is right beside the test it applies
@BMRule(name="UnitTest tryAlways trace rule",
        targetClass = "UnitTest",
        targetMethod = "tryAlways",
        condition = "TRUE",
        action="traceln(\"Byteman: intercepted at entry in tryAlways from class @BMRules rule\");"
)
public class UnitTest
{
    @Test
    // A method annotation identifies a rule script which is loaded before calling the test method and
    // then unloaded after the test has run
    @BMScript(dir="test/scripts")
    public void testOne()
    {
        tryOne();
        tryTwo();
        tryThree();
        tryAlways();
    }

    @Test
    // If you supply a value then this is used when looking for the script otherwise the method name is used
    @BMScript(value="two", dir="test/scripts")
    public void testTwo()
    {
        tryOne();
        tryTwo();
        tryThree();
        tryAlways();
    }

    @Test
    // you can load several scripts using the BMScripts annotation. this is useful if you want
    // several test to share some rules but also have their own specific rules
    @BMScripts(
            scripts = { @BMScript(value="three", dir="test/scripts"),  @BMScript(value="three-extra", dir="test/scripts") }
    )
    // BMRule and BMRules annotations can also be used at the method level
    // to configure rules specific to a given test method
    // note that rules defined using @BMRule get loaded and, hence, injected after rules defined using
    // @BMScript
    @BMRules( rules = {
            @BMRule(name="UnitTest.testThree tryThree trace rule",
                    targetClass = "UnitTest",
                    targetMethod = "tryThree",
                    condition = "TRUE",
                    action="traceln(\"Byteman: intercepted at entry in tryThree from method @BMRule rule\");"
            ),
            @BMRule(name="UnitTest.testThree tryAlways trace rule",
                    targetClass = "UnitTest",
                    targetMethod = "tryAlways",
                    binding="test = $0;",
                    condition = "TRUE",
                    action="traceln(\"Byteman: intercepted at entry in tryAlways from method @BMRule rule in test class \" + test.getClass().getName());"
            )
    })
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
