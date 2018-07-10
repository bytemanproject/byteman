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

package org.jboss.byteman.contrib.bmunit;

import junit.framework.TestCase;

/**
 * A subclass of the JUnit test case class which looks for a byteman rule file with the same name as the
 * test case and loads it during setup then removes it during teardown
 */
public class BMTestCase extends TestCase
{
    private String  loadDirectory;

    @Override
    protected void setUp() throws Exception {
        // load any script associated with this test
        BMUnit.loadScriptFile(this.getClass(), this.getName(), loadDirectory);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // load any script associated with this test
        BMUnit.unloadScriptFile(this.getClass(), this.getName());
    }

    public BMTestCase(String name, String loadDirectory)
    {
        super(name);
        this.loadDirectory = loadDirectory;
    }

    public BMTestCase(String name)
    {
        super(name);
    }

    public BMTestCase()
    {
        super();
    }
}
