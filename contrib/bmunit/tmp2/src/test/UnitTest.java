/**
 * Simple test program to exercise BMUnitRunner code
 */
package test;

import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMTestCase;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(BMUnitRunner.class)
@BMRules
public class UnitTest
{
    @Test
    @BMRules
    public void testOne()
    {
        tryOne();
        tryTwo();
        tryAlways();
    }

    @Test
    @BMRules("two")
    public void testTwo()
    {
        tryOne();
        tryTwo();
        tryAlways();
    }

    public void tryAlways()
    {
        // message injected by Byteman in all tests
    }

    public void tryOne()
    {
        // message injected by Byteman in testOne
    }

    public void tryTwo()
    {
        // message injected by Byteman in testTwo
    }
}
