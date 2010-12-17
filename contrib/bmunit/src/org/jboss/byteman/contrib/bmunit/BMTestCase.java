package org.jboss.byteman.contrib.bmunit;

import junit.framework.TestCase;

/**
 * A subclass of the JUnit test case class which looks for a byteman rule file with the same name as the
 * test case and loads it during setup then removes it during teardown
 */
public class BMTestCase extends TestCase
{
    @Override
    protected void setUp() throws Exception {
        // load any script associated with this test
        BMUnit.loadTestScript(this);
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // load any script associated with this test
        BMUnit.unloadTestScript(this);
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
