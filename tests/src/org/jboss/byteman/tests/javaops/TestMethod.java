/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.byteman.tests.javaops;

import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.TestFieldMethodAuxiliary;

/**
 * Test to ensure static and instance field accesses work as expected
 */
public class TestMethod extends Test
{
    public int value = 0;

    public TestMethod() {
        super(TestMethod.class.getCanonicalName());
    }

    static int runNumber = 0;

    private TestFieldMethodAuxiliary[] aux;
    public void test()
    {
        // create a network of linked objects

        aux = new TestFieldMethodAuxiliary[15];

        for (int i = 0; i < 15; i++) {
            aux[i] = new TestFieldMethodAuxiliary(i);
        }

        aux[0].left = aux[1];
        aux[0].right = aux[2];
        aux[1].left = aux[3];
        aux[1].right = aux[4];
        aux[2].left = aux[5];
        aux[2].right = aux[6];
        aux[3].left = aux[7];
        aux[3].right = aux[8];
        aux[4].left = aux[9];
        aux[4].right = aux[10];
        aux[5].left = aux[11];
        aux[5].right = aux[12];
        aux[6].left = aux[13];
        aux[6].right = aux[14];
        aux[7].left = aux[0];
        aux[7].right = aux[0];
        aux[8].left = aux[0];
        aux[8].right = aux[0];
        aux[9].left = aux[0];
        aux[9].right = aux[0];
        aux[10].left = aux[0];
        aux[10].right = aux[0];
        aux[11].left = aux[0];
        aux[11].right = aux[0];
        aux[12].left = aux[0];
        aux[12].right = aux[0];
        aux[13].left = aux[0];
        aux[13].right = aux[0];
        aux[14].left = aux[0];
        aux[14].right = aux[0];

        TestFieldMethodAuxiliary.theAuxiliary = aux[0];

        TestFieldMethodAuxiliary res;

        runNumber = 1;
        try {
            log("calling TestMethod.triggerMethod1");
            res = triggerMethod1(aux[0]);
            log("called TestMethod.triggerMethod1 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestMethod.triggerMethod1");
            res = triggerMethod1(aux[2]);
            log("called TestMethod.triggerMethod1 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestMethod.triggerMethod2");
            res = triggerMethod2(aux[4]);
            log("called TestMethod.triggerMethod2 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 4;
        try {
            log("calling TestMethod.triggerMethod2");
            res = triggerMethod2(aux[7]);
            log("called TestMethod.triggerMethod2 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public TestFieldMethodAuxiliary triggerMethod1(TestFieldMethodAuxiliary arg)
    {
        log("inside TestMethod.triggerMethod1");
        return arg;
    }

    public TestFieldMethodAuxiliary triggerMethod2(TestFieldMethodAuxiliary arg)
    {
        log("inside TestMethod.triggerMethod2");
        return arg;
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestMethod.triggerMethod1");
                logExpected("inside TestMethod.triggerMethod1");
                logExpected("triggerMethod1 : arg == " + aux[0]);
                logExpected("triggerMethod1 : arg.getLeft() == " + aux[0].getLeft());
                logExpected("triggerMethod1 : arg.getRight() == " + aux[0].getRight());
                logExpected("called TestMethod.triggerMethod1 : result == " + aux[0].getLeft().getLeft());
            }
            break;
            case 2:
            {
                logExpected("calling TestMethod.triggerMethod1");
                logExpected("inside TestMethod.triggerMethod1");
                logExpected("triggerMethod1 : arg == " + aux[2]);
                logExpected("triggerMethod1 : arg.left.getRight() == " + aux[2].left.getRight());
                logExpected("triggerMethod1 : arg.right.getRight().right == " + aux[2].right.getRight().right);
                logExpected("called TestMethod.triggerMethod1 : result == " + aux[2].getRight().right);
            }
            break;
            case 3:
            {
                logExpected("calling TestMethod.triggerMethod2");
                logExpected("inside TestMethod.triggerMethod2");
                logExpected("triggerMethod2 : arg == " + aux[4]);
                logExpected("triggerMethod2 : org.jboss.byteman.tests.auxiliary.TestFieldMethodAuxiliary.getTheAuxiliary().left.value == " + (org.jboss.byteman.tests.auxiliary.TestFieldMethodAuxiliary.getTheAuxiliary()).left.value);
                logExpected("triggerMethod2 : TestFieldMethodAuxiliary.getTheAuxiliary().left.getRight().right == " + TestFieldMethodAuxiliary.getTheAuxiliary().left.getRight().right);
                logExpected("called TestMethod.triggerMethod2 : result == " + org.jboss.byteman.tests.auxiliary.TestFieldMethodAuxiliary.getTheAuxiliary().getLeft().right);
            }
            break;
            case 4:
            {
                logExpected("calling TestMethod.triggerMethod2");
                logExpected("inside TestMethod.triggerMethod2");
                logExpected("triggerMethod2 : arg == " + aux[7]);
                logExpected("triggerMethod2 : TestFieldMethodAuxiliary.getTheAuxiliary().getValue() == " + TestFieldMethodAuxiliary.getTheAuxiliary().getValue());
                logExpected("triggerMethod2 : org.jboss.byteman.tests.auxiliary.TestFieldMethodAuxiliary.getTheAuxiliary().right.right.getRight().right.getValue() == " + org.jboss.byteman.tests.auxiliary.TestFieldMethodAuxiliary.getTheAuxiliary().right.right.getRight().right.getValue());
                logExpected("called TestMethod.triggerMethod2 : result == " + aux[7].getLeft());
            }
            break;
        }

        return super.getExpected();
    }
}