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
