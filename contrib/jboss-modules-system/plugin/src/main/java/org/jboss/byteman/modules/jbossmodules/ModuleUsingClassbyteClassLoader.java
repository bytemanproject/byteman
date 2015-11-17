/*
* JBoss, Home of Professional Open Source
* Copyright 2015 Red Hat and individual contributors
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
* @authors James Livingston
*/

package org.jboss.byteman.modules.jbossmodules;

import org.jboss.byteman.modules.ClassbyteClassLoader;
import org.jboss.modules.Module;

public class ModuleUsingClassbyteClassLoader extends ClassbyteClassLoader
{
    private final Module module;

    public ModuleUsingClassbyteClassLoader(Module module, ClassLoader triggerClassLoader)
    {
        super(triggerClassLoader);
        this.module = module;
    }

    protected Class<?> findClass(String className) throws ClassNotFoundException
    {
        // search normal classes first, so that they are never hidden by imports
        try {
            Class<?> klass = super.findClass(className);
            if (klass != null) {
                return klass;
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }

        return module.getClassLoader().loadClass(className);
    }

    Module getModule()
    {
        return module;
    }
}
