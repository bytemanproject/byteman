package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * class used to ensure that injection into interface hierarchies works as expected
 */
public class C1 implements I1, I4
{
    public void testMethod(Test test)
    {
        test.log("inside C1.testMethod()");
    }
}
