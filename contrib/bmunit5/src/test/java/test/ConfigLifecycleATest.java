/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019 Red Hat and individual contributors
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
 */
package test;

import org.jboss.byteman.contrib.bmunit.BMUnitConfigState;
import org.jboss.byteman.contrib.bmunit.WithByteman;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * First link in the sequential class chain that checks BMUnit configuration
 * setup and teardown stay symmetric. This class deliberately omits
 * {@code @BMUnitConfig}, which the BMUnit documentation permits. The chain
 * classes are named so that alphabetical run order keeps them in sequence and
 * they must all execute in a single JVM to be meaningful.
 */
@WithByteman
public class ConfigLifecycleATest
{
    @Test
    public void configurationIsInstalledForThisClass()
    {
        assertNotNull(BMUnitConfigState.getCurrentConfigState(),
                "BMUnit should have installed a class configuration state");
    }
}
