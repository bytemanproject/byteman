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

/**
 * interface hiding how we check the names of a class's super, outer class and implemented interfaces.
 */
public interface ClassChecker {
    /**
     * see if the checked class is an interface or really a class
     * @return true if the checked class is an interface and false if it is really a class
     */
    public boolean isInterface();

    /**
     * identify the name of the super class for the checked class
     * @return the name of the super class for the checked class
     */
    public String getSuper();

    /**
     * identify if the checked class is embedded in an outer class
     * @return true if the checked class is embedded in an outer class otherwise false
     */
    public boolean hasOuterClass();

    /**
     * identify how many interfaces are in the implements list of this class
     * @return how many interfaces are in the implements list of this class
     */
    public int getInterfaceCount();

    /**
     * identify the name of a specific interface in the implements list of this class
     * @param idx the index of the interface in the list
     * @return the name of a specific interface in the implements list of this class
     */
    public String getInterface(int idx);
}