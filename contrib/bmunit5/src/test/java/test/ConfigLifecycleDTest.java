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

import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitConfigState;
import org.jboss.byteman.contrib.bmunit.WithByteman;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unannotated class carrying a method level {@code @BMUnitConfig}. A method
 * configuration can only be pushed on top of a class configuration, so this
 * class also pins the requirement that the class level install stays
 * unconditional; making setup conditional instead of fixing teardown would
 * break this case.
 */
@WithByteman
public class ConfigLifecycleDTest
{
    /**
     * the configuration installed for the class, captured before any method
     * level configuration is pushed on top of it
     */
    private static BMUnitConfigState classConfigState;

    @BeforeAll
    public static void recordClassConfiguration()
    {
        classConfigState = BMUnitConfigState.getCurrentConfigState();
    }

    @Test
    public void methodConfigurationStacksOnClassConfiguration()
    {
        assertNotNull(classConfigState,
                "a class configuration state must exist without a class annotation");
    }

    @Test
    @BMUnitConfig(bmunitVerbose = true)
    public void methodConfigurationIsInstalled()
    {
        BMUnitConfigState state = BMUnitConfigState.getCurrentConfigState();
        assertNotNull(state, "a method configuration state must be installed");
        assertNotSame(classConfigState, state,
                "the method configuration should be the active state");
        // the identity check alone cannot tell an applied annotation from a method
        // level push carrying a null configuration, since both allocate a state
        assertTrue(state.isBMUnitVerbose(),
                "the method configuration's values should be in force");
    }
}
