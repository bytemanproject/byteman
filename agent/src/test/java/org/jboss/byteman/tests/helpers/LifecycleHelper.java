/*
* JBoss, Home of Professional Open Source
* Copyright 2010, Red Hat and individual contributors
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
package org.jboss.byteman.tests.helpers;

import org.jboss.byteman.rule.Rule;

/**
 */
public class LifecycleHelper extends Default
{
    public LifecycleHelper(Rule rule) {
        super(rule);
    }

    static StringBuffer output = new StringBuffer();

    public void log(String string)
    {
        output.append(string);
        output.append('\n');
    }

    public static void logShared(String string)
    {
        output.append(string);
        output.append('\n');
    }

    public static String getOutput()
    {
        String result = output.toString();
        output = new StringBuffer();

        return result;
    }

    public static void activated()
    {
        output.append("activated " + LifecycleHelper.class.getName());
        output.append('\n');
    }

    public static void deactivated()
    {
        output.append("deactivated " + LifecycleHelper.class.getName());
        output.append('\n');
    }

    public static void installed(Rule rule)
    {
        output.append("installed " + rule.getName());
        output.append('\n');
    }

    public static void uninstalled(Rule rule)
    {
        output.append("uninstalled " + rule.getName());
        output.append('\n');
    }
}
