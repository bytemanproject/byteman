/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010 Red Hat and individual contributors
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
 * (C) 2010,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.agent.check;

public class LoadedClassChecker implements ClassChecker {
    final static Class[] EMPTY = new Class[0];
    boolean isInterface;
    String superName;
    boolean hasOuterClass;
    Class[] interfaces;

    public LoadedClassChecker(Class<?> clazz) {
        isInterface = clazz.isInterface();
        Class superClazz = clazz.getSuperclass();
        superName = (superClazz == null ? null : superClazz.getName());
        // this is not foolproof and probably implies some false positives
        // but if we call getEnclosingClass or getDeclaringClass we get risk tripping a ClassCircularityException
        hasOuterClass = (clazz.getName().contains("$"));
        if (!hasOuterClass) {
            interfaces = clazz.getInterfaces();
        } else {
            interfaces = EMPTY;
        }
    }

    public boolean isInterface() {
        return isInterface;
    }

    public String getSuper() {
        return superName;
    }

    public boolean hasOuterClass() {
        return hasOuterClass;
    }

    public int getInterfaceCount() {
        return interfaces.length;
    }

    public String getInterface(int idx) {
        return interfaces[idx].getName();
    }
}
