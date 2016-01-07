/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat and individual contributors
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
import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.Child;
import org.jboss.byteman.tests.auxiliary.Parent;

/**
 * test for BYTEMAN-304 ensuring that arguments passed into constructors
 * wil be upcast to a superclass or implemented interface when matched
 * against the corresponding parameter during candidate selection
 */
public class TestConstructorArgUpcast extends Test
{
    // to test this properly we need a class which has
    // multiple constructors wiht  the right arity only
    // one of which is valid but that one must employ
    // a parameter whose class is a super of the call arg

    public static class Z
    {
        public Parent p;

        public Z(Parent p)
        {
            this.p = p;
        }

        public Z(String s)
        {
            this.p = null;
        }
    }

    public TestConstructorArgUpcast()
    {
        super(TestConstructorArgUpcast.class.getCanonicalName());
    }

    static  Z z = null;

    public void test()
    {
        Child c = new Child();
        z = triggerMethod(c);

        checkOutput();
    }

    public Z triggerMethod(Child c)
    {
        log("inside triggerMethod");
        // overridden by rule!
        return null;
    }

    @Override
    public String getExpected() {
        logExpected("inside triggerMethod");
        logExpected("created Z " + z);

        return super.getExpected();
    }
}
