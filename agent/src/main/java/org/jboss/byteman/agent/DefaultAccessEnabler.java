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

package org.jboss.byteman.agent;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import org.jboss.byteman.rule.helper.Helper;

/**
 * Implementation of AccessEnabler for use in a
 * non-Jigsaw enabled JDK runtime
 */
public class DefaultAccessEnabler implements AccessEnabler
{
    public DefaultAccessEnabler()
    {
    }
    /**
     * test whether access to the accessible from the unnamed module
     * requires the use of reflection and possibly module jiggery-pokery.
     *
     * @param accessible this must be a Member
     * @return  true if access requires reflection and
     * possibly module jiggery-pokery otherwise false
     */
    public boolean requiresAccess(AccessibleObject accessible)
    {
        // member specific checks
        // the accessible has to be a field, method or constructor
        Member member = (Member)accessible;

        // we need to use reflection to access non-public members
        if (!Modifier.isPublic(member.getModifiers())) {
            return true;
        }

        // class level checks
        Class<?> clazz = member.getDeclaringClass();

        // we need to use reflection to access non-public classes
        // (n.b. it won't be in the Byteman package space)
        if (!Modifier.isPublic(clazz.getModifiers())) {
            return true;
        }

        // we need to repeat the same check for outer classes
        while (clazz.isMemberClass()) {
            clazz = clazz.getEnclosingClass();
            if (!Modifier.isPublic(clazz.getModifiers())) {
                return true;
            }
        }

        return false;
    }

    /**
     * ensure that accessible can be accessed from the unnamed module
     * using reflection
     *
     * @param accessible this must be a Member
     */
    public void ensureAccess(AccessibleObject accessible)
    {
        // make the accessor usable
        try {
            accessible.setAccessible(true);
        } catch (Exception e) {
            Helper.verbose("DefaultAccessEnabler.ensureAccess: error enabling access for member " + e);
            Helper.verboseTraceException(e);
        }
    }
}