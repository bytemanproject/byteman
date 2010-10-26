package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;

/**
 * Auxiliary class used to test trigger locations which specify count ALL
 */
public class TestAllAuxiliary
{
    protected Test test;
    public int counter;

    public TestAllAuxiliary(Test test)
    {
        this.test = test;
        this.counter = 0;
        test.log("inside TestAllAuxiliary(Test)");
    }

    /**
     * method used to ensure that AT INVOKE XXX ALL works ok
     * @throws Exception
     */

    public void testMethod(int i)
    {
        int currentCounter = i;

        test.log("inside testMethod currentCounter = " + currentCounter);

        setCounter(currentCounter);

        test.log("inside testMethod currentCounter = " + currentCounter);

        currentCounter =  getCounter();

        test.log("inside testMethod currentCounter = " + currentCounter);

        setCounter(currentCounter + 1);

        test.log("inside testMethod currentCounter = " + currentCounter);

        currentCounter =  getCounter();

        test.log("inside testMethod currentCounter = " + currentCounter);

        setCounter(currentCounter + 1);

        test.log("inside testMethod currentCounter = " + currentCounter);

        currentCounter =  getCounter();

        test.log("inside testMethod currentCounter = " + currentCounter);
    }

    /**
     * method used to ensure that AT SYNCHRONIZE ALL works ok
     * @throws Exception
     */

    public void testMethod2(int i)
    {
        int currentCounter = i;

        test.log("inside testMethod2 currentCounter = " + currentCounter);

        synchronized(this) {
            setCounter(currentCounter);

            test.log("inside testMethod2 currentCounter = " + currentCounter);

            currentCounter =  getCounter();

            test.log("inside testMethod2 currentCounter = " + currentCounter);
        }

        synchronized(this) {
            setCounter(currentCounter + 1);

            test.log("inside testMethod2 currentCounter = " + currentCounter);

            currentCounter =  getCounter();

            test.log("inside testMethod2 currentCounter = " + currentCounter);
        }
        
        synchronized(this) {
            setCounter(currentCounter + 1);

            test.log("inside testMethod2 currentCounter = " + currentCounter);

            currentCounter =  getCounter();

            test.log("inside testMethod2 currentCounter = " + currentCounter);
        }
    }

    /**
     * method used to ensure that AT THROW ALL works ok
     * @throws Exception
     */

    public void testMethod3(int i) throws Exception
    {
        int currentCounter = i;

        test.log("inside testMethod3 currentCounter = " + currentCounter);

        if (currentCounter == 1) {
            throw new Exception("inside testMethod3 currentCounter = 1");
        }

        if (currentCounter == 2) {
            throw new Exception("inside testMethod3 currentCounter = 2");
        }

        if (currentCounter == 3) {
            throw new Exception("inside testMethod3 currentCounter = 3");
        }

        throw new Exception("inside testMethod3 currentCounter = 4");
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

    public Test getTest()
    {
        return test;
    }
}
