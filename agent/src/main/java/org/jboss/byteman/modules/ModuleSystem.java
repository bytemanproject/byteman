/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-2018 Red Hat and individual contributors
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

package org.jboss.byteman.modules;

public interface ModuleSystem <CL extends ClassLoader>
{
    void initialize(String args);
    CL createLoader(ClassLoader triggerLoader, String[] imports);
    void destroyLoader(CL helperLoader);

    /**
     * dynamically load and return a generated helper adapter classes using a custom classloader derived from the
     * trigger class's loader
     * @param helperLoader the class loader of the trigger class which has been matched with this
     * helper class's rule
     * @param helperAdapterName the name of the helper adapter class to be loaded
     * @param helperBytes the byte array defining the class
     * @return the new helper class
     */
    Class<?> loadHelperAdapter(CL helperLoader, String helperAdapterName, byte[] helperBytes);
}
