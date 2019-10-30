/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat and individual contributors
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
import org.jboss.byteman.tests.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
public class TestTraceOpenAndWrite extends Test
{
    public TestTraceOpenAndWrite()
    {
        super(TestTraceOpenAndWrite.class.getCanonicalName());
    }

    public void test()
    {
        // make sure we have no existing output file

        File file = new File("target/out.txt");
        if (file.exists()) {
            file.delete();
        }
        file.deleteOnExit();
        triggerMethod();

        checkOutput(false);

        assertTrue(file.exists());
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            assertTrue(br.ready());
            String text = br.readLine();
            assertTrue("this is a trace message".equals(text));
            assertFalse(br.ready());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("unexpected exception " + e);
        } catch (IOException e) {
            e.printStackTrace();
            fail("unexpected exception " + e);
        }
    }


    public void triggerMethod() {
    }

    @Override
    public String getExpected() {
        logExpected("opened trace file");
        logExpected("written trace message");
        return super.getExpected();
    }
}
