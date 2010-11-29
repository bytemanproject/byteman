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

/**
 * Test to ensure arithmetic operations compute as expected
 */
public class TestArithmetic extends Test
{
    public TestArithmetic()
    {
        super(TestArithmetic.class.getCanonicalName());
    }

    static int runNumber = 0;

    public void test()
    {
        int ires;
        char cres;
        short sres;
        byte bres;

        runNumber = 1;
        try {
            log("calling TestArithmetic.triggerMethod1");
            ires = triggerMethod1(0, '0', (short)99, (byte)0xf);
            log("called TestArithmetic.triggerMethod1 : result == " + ires);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 2;
        try {
            log("calling TestArithmetic.triggerMethod2");
            cres = triggerMethod2(0, '0', (short)99, (byte)0xf);
            log("called TestArithmetic.triggerMethod2 : result == " + cres);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 3;
        try {
            log("calling TestArithmetic.triggerMethod3");
            sres = triggerMethod3(0, '0', (short)99, (byte)0xf);
            log("called TestArithmetic.triggerMethod3 : result == " + sres);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);

        runNumber = 4;
        try {
            log("calling TestArithmetic.triggerMethod4");
            bres = triggerMethod4(0, '0', (short)99, (byte)0xf);
            log("called TestArithmetic.triggerMethod4 : result == " + bres);
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public int triggerMethod1(int i, char c, short s, byte b)
    {
        log("inside TestArithmetic.triggerMethod1");
        return 0;
    }

    public char triggerMethod2(int i, char c, short s, byte b)
    {
        log("inside TestArithmetic.triggerMethod2");
        return '0';
    }

    public short triggerMethod3(int i, char c, short s, byte b)
    {
        log("inside TestArithmetic.triggerMethod3");
        return (short)0;
    }

    public byte triggerMethod4(int i, char c, short s, byte b)
    {
        log("inside TestArithmetic.triggerMethod4");
        return (byte)0;
    }

    public float triggerMethod5(int i, char c, short s, byte b, float f, double d)
    {
        log("inside TestArithmetic.triggerMethod5");
        return (byte)0;
    }

    public double triggerMethod6(int i, char c, short s, byte b, float f, double d)
    {
        log("inside TestArithmetic.triggerMethod6");
        return (byte)0;
    }

    @Override
    public String getExpected() {
        switch (runNumber) {
            case 1:
            {
                logExpected("calling TestArithmetic.triggerMethod1");
                logExpected("inside TestArithmetic.triggerMethod1");
                logExpected("triggerMethod1 : i == 0");
                logExpected("triggerMethod1 : c == '0'");
                logExpected("triggerMethod1 : s == 99");
                logExpected("triggerMethod1 : b == 15");
                logExpected("triggerMethod1 : i + 255 == " + (0 + 255));
                logExpected("triggerMethod1 : c + 255 == " + ('0' + 255));
                logExpected("triggerMethod1 : s + 255 == " + ((short)99 + 255));
                logExpected("triggerMethod1 : b + 255 == " + ((byte)0xf + 255));
                logExpected("called TestArithmetic.triggerMethod1 : result == " + ((short)99 + '0'));
            }
            break;
            case 2:
            {
                logExpected("calling TestArithmetic.triggerMethod2");
                logExpected("inside TestArithmetic.triggerMethod2");
                logExpected("triggerMethod2 : i == 0");
                logExpected("triggerMethod2 : c == '0'");
                logExpected("triggerMethod2 : s == 99");
                logExpected("triggerMethod2 : b == 15");
                logExpected("triggerMethod2 : i - 255 == " + (0 - 255));
                logExpected("triggerMethod2 : c - 255 == " + ('0' - 255));
                logExpected("triggerMethod2 : s - 255 == " + ((short)99 - 255));
                logExpected("triggerMethod2 : b - 255 == " + ((byte)0xf - 255));
                logExpected("called TestArithmetic.triggerMethod2 : result == " + (char)('0' + '0'));
            }
            break;
            case 3:
            {
                logExpected("calling TestArithmetic.triggerMethod3");
                logExpected("inside TestArithmetic.triggerMethod3");
                logExpected("triggerMethod3 : i == 0");
                logExpected("triggerMethod3 : c == '0'");
                logExpected("triggerMethod3 : s == 99");
                logExpected("triggerMethod3 : b == 15");
                logExpected("triggerMethod3 : 255 % b == " + (255 % (byte)0xf));
                logExpected("triggerMethod3 : c / 2 == " + ('0' / 2));
                logExpected("triggerMethod3 : s * 1.5 == " + ((short)99 * 1.5));
                logExpected("triggerMethod3 : b * b == " + ((byte)(0xf * 0xf)));
                logExpected("called TestArithmetic.triggerMethod3 : result == " + (short)(99 * 0xf));
            }
            break;
            case 4:
            {
                logExpected("calling TestArithmetic.triggerMethod4");
                logExpected("inside TestArithmetic.triggerMethod4");
                logExpected("triggerMethod4 : i == 0");
                logExpected("triggerMethod4 : c == '0'");
                logExpected("triggerMethod4 : s == 99");
                logExpected("triggerMethod4 : b == 15");
                logExpected("triggerMethod4 : 255 * b == " + (255 * (byte)0xf));
                logExpected("triggerMethod4 : c / 0.5 == " + ('0' / 0.5));
                logExpected("triggerMethod4 : s * c == " + ((short)99 * '0'));
                logExpected("triggerMethod4 : b % 0.5 == " + (0xf % 0.5));
                logExpected("called TestArithmetic.triggerMethod4 : result == " + (byte)('0' * (99 + 0xf)));
            }
            break;
        }

        return super.getExpected();
    }
}