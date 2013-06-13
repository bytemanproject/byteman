/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat and individual contributors
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
 * @authors Amos Feng
 */
package org.jboss.byteman.tests.api;

import java.io.File;
import java.util.List;

import org.jboss.byteman.api.RuleCheck;
import org.jboss.byteman.api.RuleCheckResult;
import org.jboss.byteman.tests.Test;

public class TestRuleCheck extends Test
{

    public TestRuleCheck()
    {
        super(TestRuleCheck.class.getCanonicalName());
    }

    public void test()
    {
        RuleCheck checker = new RuleCheck();
        addBtmScript(checker, new File("target/test-classes/scripts"));
        checker.addPackage("org.jboss.byteman.tests.auxiliary");
        checker.addPackage("org.jboss.byteman.tests.bugfixes");
        checker.addPackage("org.jboss.byteman.tests.javaops");
        checker.addPackage("org.jboss.byteman.tests.helpertests");
        checker.checkRules();
        RuleCheckResult result= checker.getResult();
        if(result.isOK() == false) {
            System.out.println(result.getErrorCount());
            List<String> errors = result.getErrorMessages();
            for(String error : errors) {
                System.out.println(error);
            }
        }
        assertTrue(result.isOK());
    }
    
    private void addBtmScript(RuleCheck checker, File dir) {
        if(dir.isDirectory()) {
            String[] files = dir.list();
            for(String name : files) {
                File file = new File(dir+"/"+name);
                if(file.isDirectory()) addBtmScript(checker, file);
                else checker.addRuleFile(dir+"/"+name);
            }
        }
    }

}
