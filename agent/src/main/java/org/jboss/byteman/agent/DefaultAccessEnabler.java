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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.jboss.byteman.rule.exception.ExecuteException;
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
     * test whether reference to the class from a classpath
     * class requires the use of reflection or a method handle
     * and possibly also module jiggery-pokery.
     *
     * @param klazz the clas to be checked
     * @return  true if reference to the class from a classpath
     * class requires the use of reflection or a method handle
     * and possibly module jiggery-pokery otherwise false.
     */
    public boolean requiresAccess(Class<?> klazz)
    {
        // we need to handle private or protected classes with kid gloves
        // public classes are fine so long as they are not embedded in
        // private classes
        while (Modifier.isPublic(klazz.getModifiers())) {
            if (!klazz.isMemberClass()) {
                return false;
            }
            try {
                klazz = klazz.getDeclaringClass();
            } catch (SecurityException se) {
                return true;
            }
        }
        return true;
    }

    /**
     * test whether access to the accessible from a classpath
     * class requires the use of reflection or a method handle
     * and possibly also module jiggery-pokery.
     *
     * @param accessible this must be a Member
     * @return  true if access requires reflection or a method handle and
     * possibly also module jiggery-pokery otherwise false.
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
     * ensure that accessible can be accessed using reflection
     * or a method handle
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

    private static class DefaultAccessibleMethodInvoker implements AccessibleMethodInvoker
    {
        private Method method;

        public DefaultAccessibleMethodInvoker(Method method)
        {
            this.method = method;
        }
        @Override
        public Object invoke(Object receiver, Object[] args)
        {
            try {
                return method.invoke(receiver, args);
            } catch (Exception e) {
                throw new ExecuteException("DefaultAccessibleMethodInvoker.invoke : exception invoking method " + method, e);
            }
        }
    }

    private static class DefaultAccessibleConstructorInvoker implements AccessibleConstructorInvoker
    {
        private Constructor constructor;

        public DefaultAccessibleConstructorInvoker(Constructor constructor)
        {
            this.constructor = constructor;
        }
        @Override
        public Object invoke(Object[] args)
        {
            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                throw new ExecuteException("DefaultAccessibleConstructorInvoker.invoke : exception invoking constructor " + constructor, e);
            }
        }
    }

    private static class DefaultAccessibleFieldGetter implements AccessibleFieldGetter
    {
        private Field field;

        public DefaultAccessibleFieldGetter(Field field)
        {
            this.field = field;
        }
        @Override
        public Object get(Object owner)
        {
            try {
                return field.get(owner);
            } catch (Exception e) {
                throw new ExecuteException("DefaultAccessibleFieldGetter.get : exception reading field " + field, e);
            }
        }
    }

    private static class DefaultAccessibleFieldSetter implements AccessibleFieldSetter
    {
        private Field field;

        public DefaultAccessibleFieldSetter(Field field)
        {
            this.field = field;
        }
        @Override
        public void set(Object owner, Object value)
        {
            try {
                field.set(owner, value);
            } catch (Exception e) {
                throw new ExecuteException("DefaultAccessibleFieldGetter.get : exception writing field " + field, e);
            }
        }
    }


    @Override
    public AccessibleMethodInvoker createMethodInvoker(Method method)
    {
        return createMethodInvoker(method, false);
    }

    public AccessibleMethodInvoker createMethodInvoker(Method method, boolean alreadyAccessible)
    {
        if (!alreadyAccessible) {
            // make the method usable
            try {
                method.setAccessible(true);
            } catch (Exception e) {
                Helper.verbose("DefaultAccessEnabler.createMethodInvoker: error enabling access for method " + e);
                Helper.verboseTraceException(e);
            }
        }
        
        return new DefaultAccessibleMethodInvoker(method);
    }

    @Override
    public AccessibleConstructorInvoker createConstructorInvoker(Constructor constructor)
    {
        return createConstructorInvoker(constructor, false);
    }

    public AccessibleConstructorInvoker createConstructorInvoker(Constructor constructor, boolean alreadyAccessible)
    {
        if (!alreadyAccessible) {
            // make the constructor usable
            try {
                constructor.setAccessible(true);
            } catch (Exception e) {
                Helper.verbose("DefaultAccessEnabler.createConstructorInvoker: error enabling access for constructor " + e);
                Helper.verboseTraceException(e);
            }
        }

        return new DefaultAccessibleConstructorInvoker(constructor);
    }

    @Override
    public AccessibleFieldGetter createFieldGetter(Field field)
    {
        return createFieldGetter(field, false);
    }

    public AccessibleFieldGetter createFieldGetter(Field field, boolean alreadyAccessible)
    {
        if (!alreadyAccessible) {
            // make the constructor usable
            try {
                field.setAccessible(true);
            } catch (Exception e) {
                Helper.verbose("DefaultAccessEnabler.createFieldGetter: error enabling access for field " + e);
                Helper.verboseTraceException(e);
            }
        }

        return new DefaultAccessibleFieldGetter(field);
    }

    @Override
    public AccessibleFieldSetter createFieldSetter(Field field)
    {
        return createFieldSetter(field, false);
    }

    public AccessibleFieldSetter createFieldSetter(Field field, boolean alreadyAccessible)
    {
        if (!alreadyAccessible) {
            // make the constructor usable
            try {
                field.setAccessible(true);
            } catch (Exception e) {
                Helper.verbose("DefaultAccessEnabler.createFieldGetter: error enabling access for field " + e);
                Helper.verboseTraceException(e);
            }
        }
        
        return new DefaultAccessibleFieldSetter(field);
    }
}