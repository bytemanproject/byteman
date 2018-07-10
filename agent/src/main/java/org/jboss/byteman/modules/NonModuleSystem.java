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

import org.jboss.byteman.rule.helper.Helper;

public class NonModuleSystem implements ModuleSystem<ClassbyteClassLoader>
{

    public void initialize(String args)
    {
        if (!args.isEmpty())
            Helper.err("Unexpcted module system arguments: " + args);
    }

    public ClassbyteClassLoader createLoader(ClassLoader triggerClassLoader, String[] imports)
    {
        if (imports.length > 0) {
            reportUnexpectedImports(imports);
        }

        // create the helper class in a classloader derived from the trigger class
        // this allows the injected code to refer to the triggger class type and related
        // application types. the default helper will be accessible because it is loaded by the
        // bootstrap loader. custom helpers need to be made available to the application either
        // by deployng them with it or by locating them in the JVM classpath.
        return new ClassbyteClassLoader(triggerClassLoader);
    }

    public void destroyLoader(ClassbyteClassLoader helperLoader)
    {
        // do nothing
    }

    public Class<?> loadHelperAdapter(ClassbyteClassLoader helperLoader, String helperAdapterName, byte[] classBytes)
    {
        return helperLoader.addClass(helperAdapterName, classBytes);
    }

    protected void reportUnexpectedImports(String[] imports)
    {
        throw new IllegalArgumentException("Using IMPORT requires a module system");
    }
}
