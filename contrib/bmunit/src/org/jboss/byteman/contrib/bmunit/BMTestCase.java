/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.byteman.contrib.bmunit;

import junit.framework.TestCase;

/**
 * A subclass of the JUnit test case class which looks for a byteman rule file with the same name as the
 * test case and loads it during setup then removes it during teardown
 * @author Andrew Dinn (adinn@redhat.com) (C) 2010 Red Hat Inc.
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
