/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat and individual contributors
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

package test;

import org.jboss.byteman.contrib.bmunit.BMNGRunner;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.testng.annotations.Test;

/**
 * Created by adinn on 04/07/14.
 */
@BMRule(name="ZTest", targetClass = "ZTest", targetMethod = "bar",action = "traceln(\"ZTest!\"")
public class ZTest extends BMNGRunner
{
    @Test
    @BMRule(name="ZTest::bar", targetClass = "ZTest", targetMethod = "bar", action = "traceln(\"ZTest::bar!\"")
    public void bar()
    {
        System.out.println("ZTest::bar()");
    }

}
