package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * interface used to ensure that injection into interface hierarchies works as expected
 */
public interface I3
{
    public void testMethod(Test test);
}
