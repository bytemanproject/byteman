/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat and individual contributors
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

package org.jboss.byteman.tests.bugfixes;

import java.math.BigInteger;
import java.util.Random;

import org.jboss.byteman.tests.Test;

public class TestAtNewWithCountAll extends Test {
    public TestAtNewWithCountAll() {
        super(TestAtNewWithCountAll.class.getCanonicalName());
    }

    static Test theTest = null;

    static BigInteger newBigInteger(boolean shouldInflate, Random random) {
      final BigInteger b;
      if (shouldInflate) {
        final byte[] val = new byte[random.nextInt(1024) + 1];
        b = new BigInteger(val);
      }
      else {
        b = new BigInteger(0, new byte[0]);
      }

      return b;
    }

    public void test() {
        theTest = this;

        Random random = new Random();

        log("calling newBigInteger(true, random)");
        newBigInteger(true, random);
        log("called newBigInteger(true, random)");
        log("calling newBigInteger(false, random)");
        newBigInteger(false, random);
        log("called newBigInteger(false, random)");

        checkOutput(true);
    }
    
    @Override
    public String getExpected() {
        logExpected("calling newBigInteger(true, random)");
        logExpected("triggered AFTER NEW byte[]");
        logExpected("triggered AFTER NEW BigInteger");
        logExpected("called newBigInteger(true, random)");
        logExpected("calling newBigInteger(false, random)");
        logExpected("triggered AFTER NEW BigInteger");
        logExpected("called newBigInteger(false, random)");
        return super.getExpected();
    }
}
