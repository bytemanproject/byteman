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

package byteman.tests.modules.jbossmodules;

import byteman.tests.Test;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

public class TestTrampoline extends Test
{
    private static final String TEST_MODULE = System.getProperty("modulartest.module");
    private static final String TEST_CLASS = System.getProperty("modulartest.class");

    public TestTrampoline()
    {
        super(TestTrampoline.class.getCanonicalName());
    }

    public void test() throws Throwable {
        ModuleLoader bootModuleLoader = Module.getBootModuleLoader();

        Module module = bootModuleLoader.loadModule(ModuleIdentifier.create(TEST_MODULE));
        Class/*<Test>*/ klass = module.getClassLoader().loadClass(TEST_CLASS);
        Object/*Test*/ test = klass.newInstance();
        klass.getMethod("setName", String.class).invoke(test,  "test");
        Object/*TestResult*/ result = klass.getMethod("runBare").invoke(test);
        // TODO: reports the results properly
    }
}
