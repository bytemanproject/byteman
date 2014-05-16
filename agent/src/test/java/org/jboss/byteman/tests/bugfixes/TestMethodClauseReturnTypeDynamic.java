/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
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
package org.jboss.byteman.tests.bugfixes;

import org.jboss.byteman.agent.ScriptRepository;
import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;
import org.jboss.byteman.tests.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for bug reported by Kabir Khan pre-JIRA where the Transformer failed to accept a CALL
 * trigger location specified with an empty argument list.
 */
public class TestMethodClauseReturnTypeDynamic extends Test
{
    public TestMethodClauseReturnTypeDynamic()
    {
        super(TestMethodClauseReturnTypeDynamic.class.getCanonicalName());
    }

    public void test()
    {

        Submit submit = new Submit();
        ScriptRepository repository = new ScriptRepository(false);
        List<ScriptText> scripts = new ArrayList<ScriptText>();
        scripts.add(new ScriptText("dynamic", getRuleText()));

        try {
            submit.addScripts(scripts);
        } catch (Exception e) {
            System.out.println("exception submitting script");
            fail();
        }

        log("calling booleanMethod()");
        booleanMethod();
        log("called booleanMethod()");
        checkOutput();
    }

    public boolean booleanMethod()
    {
        log("inside booleanMethod()");
        return true;
    }

    @Override
    public String getExpected() {
        logExpected("calling booleanMethod()");
        logExpected("triggered for METHOD boolean booleanMethod()");
        logExpected("inside booleanMethod()");
        logExpected("called booleanMethod()");

        return super.getExpected();
    }

    public String getRuleText()
    {
        File file = new File("target/test-classes/scripts/bugfixes/TestMethodClauseReturnTypeDynamic.btm");
        try {
            FileInputStream fis = new FileInputStream(file);
            int expected = fis.available();
            byte[] b = new byte[expected];
            int actual = fis.read(b);
            if (actual != expected) {
                throw new IOException("Expecting " + expected + " bytes but read only returned " + actual);
            }
            return new String(b);
        } catch (Exception e) {
            throw new RuntimeException("oops cannot read script file test-classes/scripts/TestMethodClauseReturnTypeDynamic.btm", e);
        }
    }
}