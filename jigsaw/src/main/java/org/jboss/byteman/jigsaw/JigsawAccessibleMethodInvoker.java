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
import org.jboss.byteman.agent.AccessibleMethodInvoker;
import org.jboss.byteman.rule.exception.ExecuteException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Jigsaw implementation of method invoker interface
 */
public class JigsawAccessibleMethodInvoker implements AccessibleMethodInvoker
{
    private MethodHandle handle;
    private boolean isStatic;

    public JigsawAccessibleMethodInvoker(MethodHandles.Lookup theLookup, Method method)
    {
        try {
            MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
            isStatic = Modifier.isStatic(method.getModifiers());
            MethodHandle h;
            if (isStatic) {
                h = theLookup.findStatic(method.getDeclaringClass(), method.getName(), methodType);
            } else {
                h = theLookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
            }
            if (method.isVarArgs()) {
                h = h.asFixedArity();
            }
            this.handle = h;
        } catch (Exception e) {
            // throw new RuntimeException("JigsawAccessibleMethodInvoker : exception creating methodhandle for method " + method, e);
            throw new RuntimeException("JigsawAccessibleMethodInvoker.invoke : exception creating methodhandle for method ", e);
        }
    }

    @Override
    public Object invoke(Object receiver, Object[] args)
    {
        try {
            if (isStatic) {
                return handle.invokeWithArguments(args);
            } else {
                // return handle.bindTo(receiver).invokeWithArguments(args);
                return handle.bindTo(receiver).invokeWithArguments(args);
            }
        } catch (Throwable e) {
            // throw new ExecuteException("JigsawAccessibleMethodInvoker.invoke : exception invoking methodhandle " + handle, e);
            throw new ExecuteException("JigsawAccessibleMethodInvoker.invoke : exception invoking methodhandle ", e);
        }
    }
}
