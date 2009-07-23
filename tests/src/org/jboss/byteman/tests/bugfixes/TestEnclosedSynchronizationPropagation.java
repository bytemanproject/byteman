package org.jboss.byteman.tests.bugfixes;

import junit.framework.TestCase;

/**
 * test that propagation of open sychronizations to try catch blocks is done correctly. this manifested an error
 * identified by JIRA BYTEMAN-15
 *
 * The error happens when a try catch nested in a synchronized block follows another nested synchronized block
 * as happens in case 2 in the following switch. The algorithm was reversing the order of the code locations
 * for the inner and outer synchronized blocks and the deciding that the monitorexit in the catch block applied
 * to the outer syncrhonization not the inner one. Thsi subsequently causes the catch block to have the wrong
 * open monitor enter count, tripping warning trace in the control flow graph. This does not appear to cause any
 * error in the trigger insertion.
 */
public class TestEnclosedSynchronizationPropagation extends TestCase
{
    public TestEnclosedSynchronizationPropagation() {
        super(TestEnclosedSynchronizationPropagation.class.getCanonicalName());
    }
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();
    public static boolean alwaysFalse = false;

    public void test()
    {
        int value = 1;
        
        synchronized(lock1) {
            boolean result = true;
            switch(value) {
                case 1:
                {
                    break;
                }
                case 2:
                    synchronized(lock2) {
                    }
                    try {
                        result = testAuxiliary();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                default:
                    System.out.println("unexpected value " + value);
                    break;
            }
        }
    }
    
    public boolean testAuxiliary() throws Exception
    {
        if (alwaysFalse) {
            throw new Exception("shouldn't");
        }
        return true;
    }
}
