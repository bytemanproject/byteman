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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * interface encapsulating behaviour required both to check
 * for the need to access a member reflectively and to ensure
 * that the member can be so used.
 */
public interface AccessEnabler
{
    /**
     * test whether access to the accessible from the unnamed module
     * requires the use of reflection and possibly module jiggery-pokery.
     *
     * @param accessible this must be a Member
     * @return  true if access requires reflection and
     * possibly module jiggery-pokery otherwise false
     */
    public boolean requiresAccess(AccessibleObject accessible);
    /**
     * ensure that accessible can be accessed from the unnamed module
     * using reflection
     *
     * @param accessible this must be a Member
     */
    public void ensureAccess(AccessibleObject accessible);

    public AccessibleMethodInvoker createMethodInvoker(Method method);
    public AccessibleConstructorInvoker createConstructorInvoker(Constructor constructor);
    public AccessibleFieldGetter createFieldGetter(Field field);
    public AccessibleFieldSetter createFieldSetter(Field field);
}