package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Interface used to test injection through interfaces
 */
public interface TestInterface {
    void testMethod();

    Test getTest();
}
