/*
 * JBoss, Home of Professional Open Source
 * Copyright 2023, Red Hat and individual contributors
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
package org.jboss.byteman.tests.jar;

import org.jboss.byteman.tests.Test;

/**
 * Validate that a script loaded from a jar file is correctly applied
 * can be run using:
 * mvn package failsafe:integration-test@jar.TestScriptFromJar
 */
public class TestScriptFromJar extends Test {
    public TestScriptFromJar()
    {
        super(TestScriptFromJar.class.getCanonicalName());
    }

    public void test()
    {
        log("calling TestScriptFromJar.triggerMethod1");
        triggerMethod1();
        log("called TestScriptFromJar.triggerMethod1");
    }

    public void triggerMethod1()
    {
        log("inside TestScriptFromJar.triggerMethod1");
    }

    public String getExpected()
    {
        logExpected("calling TestScriptFromJar.triggerMethod1");
        logExpected("caller match first caller");
        logExpected("inside TestScriptFromJar.triggerMethod1");
        logExpected("called TestScriptFromJar.triggerMethod1");

        return super.getExpected();
    }
}
