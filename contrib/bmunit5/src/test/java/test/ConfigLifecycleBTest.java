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
 * Second unannotated class in the sequential chain. An unannotated class has to
 * be able to set up its own configuration after another unannotated class has
 * already run in the same JVM, which only holds if the preceding class removed
 * the configuration it installed.
 */
@WithByteman
public class ConfigLifecycleBTest
{
    @Test
    public void configurationIsInstalledForThisClass()
    {
        assertNotNull(BMUnitConfigState.getCurrentConfigState(),
                "BMUnit should have installed a class configuration state");
    }
}
