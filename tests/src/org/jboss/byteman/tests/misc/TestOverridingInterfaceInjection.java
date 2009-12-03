package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliarySub;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliary;

/**
 * Test class to ensure injection into interfaces with overriding works as expected
 */
public class TestOverridingInterfaceInjection extends Test
{
    public TestOverridingInterfaceInjection()
    {
        super(TestOverridingInterfaceInjection.class.getName());
    }

    public void test()
    {
        // this is much the same as the TestEntry code but we use an interface rule to inject
        // rule trace into the parent testMethod. note we cannot inject into constructors via
        // an interface rule

        try {
        TestEntryExitAuxiliary testAuxiliary;
        log("creating TestEntryExitAuxiliarySub");
        testAuxiliary = new TestEntryExitAuxiliarySub(this);
        log("created TestEntryExitAuxiliarySub");
        log("calling TestEntryExitAuxiliarySub.testMethod");
        testAuxiliary.testMethod();
        log("called TestEntryExitAuxiliarySub.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestEntryExitAuxiliarySub");
        // parent constructor will log first
        logExpected("inside TestEntryExitAuxiliary(Test)");
        logExpected("inside TestEntryExitAuxiliarySub(Test)");
        logExpected("created TestEntryExitAuxiliarySub");
        // we see injected coede in the subclass method and then in the superclass method
        logExpected("calling TestEntryExitAuxiliarySub.testMethod");
        logExpected("ENTRY triggered in ^TestInterface.testMethod");
        logExpected("inside TestEntryExitAuxiliarySub.testMethod");
        logExpected("calling TestEntryExitAuxiliary.testMethod");
        logExpected("ENTRY triggered in ^TestInterface.testMethod");
        logExpected("inside TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliarySub.testMethod");

        return super.getExpected();
    }
}