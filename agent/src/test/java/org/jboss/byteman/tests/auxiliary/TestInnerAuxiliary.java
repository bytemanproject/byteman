/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat and individual contributors
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

package org.jboss.byteman.tests.auxiliary;

import org.jboss.byteman.tests.Test;
/**
 * Auxiliary class which defines various types of inner classes.
 * It can be used to test the success or failure of injection into
 * inner classes.
 */
public class TestInnerAuxiliary
{
    public final Test test;

    public TestInnerAuxiliary(Test test)
    {
        this.test = test;
    }

    public void testInnerClasses()
    {
        PublicInner publicInner = new PublicInner();
        PrivateInner privateInner = new PrivateInner();
        PublicStaticInner publicStaticInner = new PublicStaticInner(test);
        PrivateStaticInner privateStaticInner = new PrivateStaticInner(test);

        publicInner.testPublic();
        publicInner.testPrivate();

        privateInner.testPublic();
        privateInner.testPrivate();

        publicStaticInner.testPublic();
        publicStaticInner.testPrivate();

        privateStaticInner.testPublic();
        privateStaticInner.testPrivate();
    }

    // injection into non-static inner classes will always give errors
    public class PublicInner // extends TestInnerAbstract implements TestInnerInterface
    {
        public PublicInner()
        {
        }

        public void testPublic()
        {
            test.log("inside PublicInner.testPublic");
        }

        public void testPrivate()
        {
            test.log("inside PublicInner.testPrivate");
        }

        public Test getTest()
        {
            return test;
        }
    }

    // injection into non-static inner classes will always give errors
    private class PrivateInner // extends TestInnerAbstract implements TestInnerInterface
    {
        public PrivateInner()
        {
        }

        public void testPublic()
        {
            test.log("inside PrivateInner.testPublic");
        }

        public void testPrivate()
        {
            test.log("inside PrivateInner.testPrivate");
        }

        public Test getTest()
        {
            return test;
        }
    }


    public static class PublicStaticInner extends TestInnerAbstract implements TestInnerInterface
    {
        private final Test test;
        private static Test statictest = null;

        public PublicStaticInner(Test test)
        {
            this.test = test;
            this.statictest = test;
        }

        public void testPublic()
        {
            test.log("inside PublicStaticInner.testPublic");
        }

        public void testPrivate()
        {
            test.log("inside PublicStaticInner.testPrivate");
        }

        public Test getTest()
        {
            return test;
        }
    }

    private static class PrivateStaticInner extends TestInnerAbstract implements TestInnerInterface
    {
        public final Test test;
        public static Test statictest = null;

        public PrivateStaticInner(Test test)
        {
            this.test = test;
            this.statictest = test;
        }

        public void testPublic()
        {
            test.log("inside PrivateStaticInner.testPublic");
        }

        public void testPrivate()
        {
            test.log("inside PrivateStaticInner.testPrivate");
        }

        public Test getTest()
        {
            return test;
        }
    }
}
