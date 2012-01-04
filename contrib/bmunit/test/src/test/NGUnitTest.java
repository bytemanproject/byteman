/**
 * Simple test program to exercise BMUnitRunner code
 */
package test;

import org.jboss.byteman.contrib.bmunit.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

// this runner extends the BMNGRunner which allows it to be executed via TestNG
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
@BMRule(name="NGUnitTest tryAlways trace rule",
        targetClass = "NGUnitTest",
        targetMethod = "tryAlways",
        condition = "TRUE",
        action="traceln(\"Byteman: intercepted at entry in tryAlways from class @BMRules rule\");"
)
// annottaing the class with BMNGListener mixes in the behaviour which processes
// @BMRule and @BMScript annotations. ANother option is to have the test class
// inherit from class BMNGRunner
@Listeners(BMNGListener.class)
public class NGUnitTest
{
    @Test()
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

    @Test()
    // If you supply a value then this is used when looking for the script otherwise the method name is used
    @BMScript(value="two", dir="test/scripts")
    public void testTwo()
    {
        tryOne();
        tryTwo();
        tryThree();
        tryAlways();
    }

    @Test()
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
            @BMRule(name="NGUnitTest.testThree tryThree trace rule",
                    targetClass = "NGUnitTest",
                    targetMethod = "tryThree",
                    condition = "TRUE",
                    action="traceln(\"Byteman: intercepted at entry in tryThree from method @BMRule rule\");"
            ),
            @BMRule(name="NGUnitTest.testThree tryAlways trace rule",
                    targetClass = "NGUnitTest",
                    targetMethod = "tryAlways",
                    binding = "test = $0;",
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
