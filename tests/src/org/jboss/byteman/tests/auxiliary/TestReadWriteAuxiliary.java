package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Auxiliary subclass used by read and write location test classes
 */
public class TestReadWriteAuxiliary
{
    protected Test test;
    public int counter;

    public TestReadWriteAuxiliary(Test test)
    {
        this.test = test;
        this.counter = 0;
        test.log("inside TestReadWriteAuxiliary(Test)");
    }

    public void testMethod() throws Exception
    {
        test.log("inside TestReadWriteAuxiliary.testMethod");
        int currentCounter = counter;

        test.log("1: currentCounter == " + currentCounter);

        currentCounter++;

        counter = currentCounter;

        test.log("2: currentCounter == " + currentCounter);

        currentCounter = counter;

        currentCounter++;
        
        counter = currentCounter;

        test.log("3: currentCounter == " + currentCounter);
    }

    public Test getTest()
    {
        return test;
    }

    public int setCounter(int newValue)
    {
        int oldValue = counter;
        counter = newValue;

        return oldValue;
    }

    public int getCounter()
    {
        return counter;
    }
}
