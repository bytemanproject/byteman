/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat and individual contributors
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
package org.jboss.byteman.tests.submit;

import org.jboss.byteman.agent.ScriptRepository;
import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;
import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.helpers.LifecycleHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Test to ensure dynamic rule submit and unsubmit works ok
 */
public class TestSubmit extends Test
{
    public TestSubmit()
    {
        super(TestSubmit.class.getCanonicalName());
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

        try {
            log("calling TestSubmit.triggerMethod");
            triggerMethod();
            log("called TestSubmit.triggerMethod");
        } catch (Exception e) {
            log(e);
        } catch (Throwable th) {
            System.out.println("unexpected throwable " + th);
            fail();
        }

        try {
            submit.deleteScripts(scripts);
        } catch (Exception e) {
            System.out.println("exception deleting script");
            fail();
        }

        checkOutput();
    }

    public String getRuleText()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("HELPER org.jboss.byteman.tests.helpers.LifecycleHelper\n");

        buffer.append("RULE rule 1\n");
        buffer.append("CLASS TestSubmit\n");
        buffer.append("METHOD triggerMethod\n");
        buffer.append("AT ENTRY\n");
        buffer.append("IF TRUE\n");
        buffer.append("DO log(\"triggered rule 1\")\n");
        buffer.append("ENDRULE\n");

        buffer.append("RULE rule 2\n");
        buffer.append("CLASS TestSubmit\n");
        buffer.append("METHOD triggerMethod\n");
        buffer.append("AT EXIT\n");
        buffer.append("IF TRUE\n");
        buffer.append("DO log(\"triggered rule 2\")\n");
        buffer.append("ENDRULE\n");

        return buffer.toString();
    }

    public void triggerMethod()
    {
        log("inside TestSubmit.triggerMethod");
    }

    @Override
    public String getExpected() {
        logExpected("calling TestSubmit.triggerMethod");
        logExpected("activated org.jboss.byteman.tests.helpers.LifecycleHelper");
        logExpected("installed rule 1");
        logExpected("triggered rule 1");
        logExpected("inside TestSubmit.triggerMethod");
        logExpected("installed rule 2");
        logExpected("triggered rule 2");
        logExpected("called TestSubmit.triggerMethod");
        logExpected("uninstalled rule 1");
        logExpected("uninstalled rule 2");
        logExpected("deactivated org.jboss.byteman.tests.helpers.LifecycleHelper");

        return super.getExpected();
    }

    // redirect output to the lifecycle helper so we can also check interleaved output from its
    // lifecycle methods

    public void log(String string)
    {
        LifecycleHelper.logShared(string);
    }

    public String getOutput()
    {
        return LifecycleHelper.getOutput();
    }
}