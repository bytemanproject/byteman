/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

/**
 * information about a single rule derived from a rule script
 */

public class Script
{
    private String name;
    private String targetClass;
    private String targetMethod;
    private String targetHelper;
    private Location targetLocation;
    private String ruleText;
    int line;
    String file;

    Script (String name, String targetClass, String targetMethod, String targetHelper, Location targetLocation, String ruleText, int line, String file)
    {
        this.name = name;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.targetHelper = targetHelper;
        this.targetLocation = targetLocation;
        this.ruleText = ruleText;
        this.line = line;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public String getTargetHelper() {
        return targetHelper;
    }

    public String getTargetMethod() {
        return targetMethod;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public String getRuleText() {
        return ruleText;
    }

    public int getLine()
    {
        return line;
    }

    public String getFile()
    {
        return file;
    }
}
