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

package org.jboss.byteman.jigsaw;
import org.jboss.byteman.agent.AccessibleFieldSetter;
import org.jboss.byteman.rule.exception.ExecuteException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/**
 * Jigsaw implementation of field getter interface
 */

public class JigsawAccessibleFieldSetter implements AccessibleFieldSetter
{
    private MethodHandle handle;
    boolean isStatic;
    private Field field;

    public JigsawAccessibleFieldSetter(MethodHandles.Lookup theLookup, Field field)
    {
        isStatic = Modifier.isStatic(field.getModifiers());
        try {
            if (isStatic) {
                this.handle = theLookup.findStaticSetter(field.getDeclaringClass(), field.getName(), field.getType());
            } else {
                this.handle = theLookup.findSetter(field.getDeclaringClass(), field.getName(), field.getType());
            }
        } catch (Exception e) {
            throw new RuntimeException("JigsawAccessibleFieldSetter : exception creating setter method handle for field " + field, e);
        }
    }
    @Override
    public void set(Object owner, Object value)
    {
        try {
            if (isStatic) {
                if (owner != null) {
                       throw new ExecuteException("JigsawAccessibleFieldSetter.set : expecting null owner for static set!");
                }
                handle.invokeWithArguments(value);
            } else {
                handle.invokeWithArguments(owner, value);
            }
        } catch (Throwable e) {
            throw new ExecuteException("JigsawAccessibleFieldSetter.set : exception invoking getter methodhandle " + handle, e);
        }
    }
}

