package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.tests.Test;

/**
 * Class to test fix for BYTEMAN-93
 */
public class TestPromotePrimitiveToObject extends Test
{
    public TestPromotePrimitiveToObject() {
        super(TestPromotePrimitiveToObject.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("calling triggerMethod");
            triggerMethod(1);
            log("called triggerMethod");
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    public void triggerMethod(int i)
    {
        log("inside triggerMethod " + i);
    }

    @Override
    public String getExpected() {
        logExpected("calling triggerMethod");
        logExpected("calling helper.testPromote 1");
        logExpected("inside triggerMethod 1");
        logExpected("called triggerMethod");

        return super.getExpected();
    }
}
