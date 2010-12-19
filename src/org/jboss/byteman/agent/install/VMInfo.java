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

package org.jboss.byteman.agent.install;

/**
 * Auxiliary class used by Install to provide clients with ids and display names of attachable
 * JVMs.
 */
public class VMInfo
{
    private String id;
    private String displayName;
    public VMInfo(String id, String displayName)
    {
        this.id =id;
        this.displayName = displayName;
    }
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
