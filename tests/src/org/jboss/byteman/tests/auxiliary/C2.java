package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * class used to ensure that injection into interface hierarchies works as expected
 */
public class C2 extends C1
{
    public void testMethod(Test test)
    {
        test.log("inside C2.testMethod()");
        super.testMethod(test);
    }
}
