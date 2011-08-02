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
public class TestField extends Test
{
    public int value = 0;

    public TestField() {
        super(TestField.class.getCanonicalName());
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
            log("calling TestField.triggerMethod1");
            res = triggerMethod1(aux[0]);
            log("called TestField.triggerMethod1 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestField.triggerMethod1");
            res = triggerMethod1(aux[2]);
            log("called TestField.triggerMethod1 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestField.triggerMethod2");
            res = triggerMethod2(aux[4]);
            log("called TestField.triggerMethod2 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 4;
        try {
            log("calling TestField.triggerMethod2");
            res = triggerMethod2(aux[7]);
            log("called TestField.triggerMethod2 : result == " + res);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public TestFieldMethodAuxiliary triggerMethod1(TestFieldMethodAuxiliary arg)
    {
        log("inside TestField.triggerMethod1");
        return arg;
    }

    public TestFieldMethodAuxiliary triggerMethod2(TestFieldMethodAuxiliary arg)
    {
        log("inside TestField.triggerMethod2");
        return arg;
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestField.triggerMethod1");
                logExpected("inside TestField.triggerMethod1");
                logExpected("triggerMethod1 : arg == " + aux[0]);
                logExpected("triggerMethod1 : arg.left == " + aux[0].left);
                logExpected("triggerMethod1 : arg.right == " + aux[0].right);
                logExpected("called TestField.triggerMethod1 : result == " + aux[0].left.left);
            }
            break;
            case 2:
            {
                logExpected("calling TestField.triggerMethod1");
                logExpected("inside TestField.triggerMethod1");
                logExpected("triggerMethod1 : arg == " + aux[2]);
                logExpected("triggerMethod1 : arg.left.right == " + aux[2].left.right);
                logExpected("triggerMethod1 : arg.right.right.right == " + aux[2].right.right.right);
                logExpected("called TestField.triggerMethod1 : result == " + aux[2].right.right);
            }
            break;
            case 3:
            {
                logExpected("calling TestField.triggerMethod2");
                logExpected("inside TestField.triggerMethod2");
                logExpected("triggerMethod2 : arg == " + aux[4]);
                logExpected("triggerMethod2 : TestFieldMethodAuxiliary.theAuxiliary.left.value == " + aux[0].left.value);
                logExpected("triggerMethod2 : TestFieldMethodAuxiliary.theAuxiliary.left.getRight().right == " + aux[0].left.right.right);
                logExpected("called TestField.triggerMethod2 : result == " + aux[0].left.right);
            }
            break;
            case 4:
            {
                logExpected("calling TestField.triggerMethod2");
                logExpected("inside TestField.triggerMethod2");
                logExpected("triggerMethod2 : arg == " + aux[7]);
                logExpected("triggerMethod2 : TestFieldMethodAuxiliary.theAuxiliary.value == " + aux[0].value);
                logExpected("triggerMethod2 : TestFieldMethodAuxiliary.theAuxiliary.right.right.right.right.value == " + aux[0].right.right.right.right.value);
                logExpected("called TestField.triggerMethod2 : result == " + aux[7].left);
            }
            break;
        }

        return super.getExpected();
    }
}