package org.jboss.byteman.tests.misc;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestEntryExitAuxiliarySub;

/**
 * Test class to ensure injection into overriding methods works as expected
 */
public class TestOverridingInjection extends Test
{
    public TestOverridingInjection()
    {
        super(TestOverridingInjection.class.getName());
    }

    public void test()
    {
        // this is much the same as the TestEntry code but we use a single rule to inject
        // rule trace into both constructors. ditto for both implementations of testMethod.

        try {

        // n.b. it is important that we do not employ the parent class TestEntryExitAuxiliary
        // in the code here. that guarantees when we create the subclass instance here
        // that the parent class has not yet been loaded. this verifies the fix to BYTEMAN-80
        TestEntryExitAuxiliarySub testAuxiliarySub;
        log("creating TestEntryExitAuxiliarySub");
        testAuxiliarySub = new TestEntryExitAuxiliarySub(this);
        log("created TestEntryExitAuxiliarySub");
        log("calling TestEntryExitAuxiliarySub.testMethod");
        testAuxiliarySub.testMethod();
        log("called TestEntryExitAuxiliarySub.testMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating TestEntryExitAuxiliarySub");
        // super constructor should log first then the sub constructor
        logExpected("ENTRY triggered in constructor");
        logExpected("inside TestEntryExitAuxiliary(Test)");
        logExpected("ENTRY triggered in constructor");
        logExpected("inside TestEntryExitAuxiliarySub(Test)");
        logExpected("created TestEntryExitAuxiliarySub");
        // injected ENTRY code should log in subclass method code then
        // body of subclass hsoudl log then injected ENTRY code in superclass method
        // then body of superclass method
        logExpected("calling TestEntryExitAuxiliarySub.testMethod");
        logExpected("ENTRY triggered in ^TestEntryExitAuxiliary.testMethod");
        logExpected("inside TestEntryExitAuxiliarySub.testMethod");
        logExpected("calling TestEntryExitAuxiliary.testMethod");
        logExpected("ENTRY triggered in ^TestEntryExitAuxiliary.testMethod");
        logExpected("inside TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliary.testMethod");
        logExpected("called TestEntryExitAuxiliarySub.testMethod");

        return super.getExpected();
    }
}
