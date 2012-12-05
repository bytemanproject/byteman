/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat and individual contributors
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

package org.jboss.byteman.tests.misc;
import org.jboss.byteman.tests.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * class to test use of downcast in rule binding
 */
public class TestDowncast extends Test
{
    public TestDowncast()
    {
        super(TestDowncast.class.getCanonicalName());
    }

    public void test()
    {
        List<String> names = new ArrayList<String>();

        names.add("Andrew");
        
        try {
            log("calling TestDowncast.triggerMethod((Andrew))");
            triggerMethod(names);
            log("called TestDowncast.triggerMethod((Andrew))");
        } catch (Exception e) {
            log(e);
        }

        checkOutput(true);
    }

    public void triggerMethod(List<String> names)
    {
        log("inside TestDowncast.triggerMethod()");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestDowncast.triggerMethod((Andrew))");
        logExpected("inside TestDowncast.triggerMethod()");
        logExpected("triggerMethod : list.get(0) == Andrew");
        logExpected("called TestDowncast.triggerMethod((Andrew))");

        return super.getExpected();
    }
}
