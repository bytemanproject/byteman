package org.jboss.jbossts.orchestration.tests.auxiliary;

import org.jboss.jbossts.orchestration.tests.Test;

/**
 * Auxiliary class used by call, throw and synchronization location test classes
 */
public class TestCallThrowSynchAuxiliary
{
    protected Test test;
    public int counter;

    public TestCallThrowSynchAuxiliary(Test test)
    {
        this.test = test;
        this.counter = 0;
        test.log("inside TestCallThrowSynchAuxiliary(Test)");
    }

    public void testMethod() throws Exception
    {
        test.log("inside TestCallThrowSynchAuxiliary.testMethod");
        int currentCounter = getCounter();

        test.log("1: currentCounter == " + currentCounter);

        if (currentCounter == 1) {
            throw new Exception("counter == " + currentCounter);
        }

        synchronized(this) {
            setCounter(currentCounter + 1);
            currentCounter = getCounter();

            test.log("2: currentCounter == " + currentCounter);
        }

        setCounter(currentCounter + 1);
        currentCounter = getCounter();

        if (currentCounter == 4) {
            throw new Exception("counter == " + currentCounter);
        }

        synchronized(this) {
            test.log("3: currentCounter == " + currentCounter);
        }
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
