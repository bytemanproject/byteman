/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates,
 * and individual contributors as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
 *
 * (C) 2016,
 * @author JBoss, by Red Hat.
 */
package org.jboss.byteman.contrib.dtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class InstrumentorTest {
    private static Instrumentor instrumentor;
    private static final String lineSeparator = System.getProperty("line.separator");

    private static final Class<?> clazz = InstrumentorTest.class;
    private static final String clazzName = clazz.getName();
    private static final String method = "method";

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws RemoteException {
        instrumentor = new Instrumentor();
    }

    @Before
    public void startUp() throws Exception {
        instrumentor.removeLocalState();
    }

    @Test
    public void ruleCrashAtMethodExit() throws Exception {
        File ruleFile = tmpDir.newFile("crashAtMethodExit.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        instrumentor.crashAtMethodExit(clazzName, method);

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
            .clazz(clazzName)
            .method(method)
            .atExit()
            .ifTrue()
            .doo("killJVM")
            .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleCrashAtInterfaceMethodEntry() throws Exception {
        File ruleFile = tmpDir.newFile("crashAtInterfaceMethodEntry.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        instrumentor.crashAtMethodEntry(InstrumentorTestingInterface.class, method);

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .interfaze(InstrumentorTestingInterface.class.getSimpleName())
                .method(method)
                .atEntry()
                .ifTrue()
                .doo("killJVM")
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleCrashAtMethodEntry() throws Exception {
        File ruleFile = tmpDir.newFile("crashAtMethodEntry.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        instrumentor.crashAtMethodEntry(clazzName, method);
        
        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .atEntry()
                .ifTrue()
                .doo("killJVM")
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleCrashAtMethod() throws Exception {
        File ruleFile = tmpDir.newFile("crashAtMethod.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        String atLine = "LINE 123";
        instrumentor.crashAtMethod(clazzName, method, atLine);

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .at(atLine)
                .ifTrue()
                .doo("killJVM")
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleInjectOnCall() throws Exception {
        File ruleFile = tmpDir.newFile("injectOnCall.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        String action = "debug(\"here\");";
        instrumentor.injectOnCall(clazz, method, action);

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .atEntry()
                .ifTrue()
                .doo(action)
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleInjectOnExit() throws Exception {
        File ruleFile = tmpDir.newFile("injectOnExit.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        String action = "debug(\"here\");";
        instrumentor.injectOnExit(clazz, method, action);

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .atExit()
                .ifTrue()
                .doo(action)
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleInjectOnMethod() throws Exception {
        File ruleFile = tmpDir.newFile("injectOnMethod.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        String at = "AFTER READ i";
        String action = "debug(\"here\");";
        String iff = "recovered";
        instrumentor.injectOnMethod(clazz, method, iff, action, at);

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .at(at)
                .iff(iff)
                .doo(action)
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleInjectFault() throws Exception {
        File ruleFile = tmpDir.newFile("injectFault.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        Class exception = NullPointerException.class;
        Object[] args = {"hello"};
        instrumentor.injectFault(clazz, method, exception, args);
        
        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .atEntry()
                .ifTrue()
                .doo("throw new " + exception.getName() + "(\"" + args[0])
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    @Test
    public void ruleInstall() throws Exception {
        File ruleFile = tmpDir.newFile("installRule.btm");
        instrumentor.setRedirectedSubmissionsFile(ruleFile);

        Class exception = NullPointerException.class;
        Object[] args = {"hello"};
        instrumentor.installRule(RuleConstructor.createRule("install rule")
            .onClass(clazz).inMethod(method).atEntry().helper(BytemanTestHelper.class).ifTrue()
            .doAction("throw new " + exception.getName() + "(\"" + args[0] + "\")"));

        String ruleString = readFileToString(ruleFile);

        Pattern pattern = new RegexRuleBuilder()
                .clazz(clazzName)
                .method(method)
                .ifTrue()
                .atEntry()
                .doo("throw new " + exception.getName() + "(\"" + args[0])
                .build();

        Assert.assertTrue("Pattern\n" + pattern.pattern() + "\ndoes not match rule\n" + ruleString,
                pattern.matcher(ruleString).matches());
    }

    private String readFileToString(File file) throws FileNotFoundException {
        Scanner in = null;
        try {
            in = new Scanner(file);
    
            StringBuffer buffer = new StringBuffer();
            while (in.hasNext()) {
                buffer.append(in.nextLine() + lineSeparator);
            }
            return buffer.toString();
        } finally {
            in.close();
        }
    }

    private static class RegexRuleBuilder {
        private String clazz, interfaze, method, at, iff, doo;

        public RegexRuleBuilder clazz(String clazz) {
            this.clazz = clazz;
            return this;
        }
        public RegexRuleBuilder interfaze(String interfaze) {
            this.interfaze = interfaze;
            return this;
        }
        public RegexRuleBuilder method(String method) {
            this.method = method;
            return this;
        }
        public RegexRuleBuilder at(String at) {
            this.at = at;
            return this;
        }
        public RegexRuleBuilder atEntry() {
            this.at = "ENTRY";
            return this;
        }
        public RegexRuleBuilder atExit() {
            this.at = "EXIT";
            return this;
        }
        public RegexRuleBuilder iff(String iff) {
            this.iff = iff;
            return this;
        }
        public RegexRuleBuilder ifTrue() {
            this.iff = "true";
            return this;
        }
        public RegexRuleBuilder doo(String doo) {
            this.doo = doo;
            return this;
        }

        private String cleanForRegexp(String str) {
            return str
                    .replaceAll("\\.", "\\\\.")
                    .replaceAll("\\(", "\\\\(")
                    .replaceAll("\\)", "\\\\)");
        }

        public Pattern build() {
            String regex = String.format(
                "^RULE.*" +
                "%s%s.*" +
                "METHOD%s.*" +
                "%s.*" + // AT
                "HELPER " + BytemanTestHelper.class.getName() + ".*" +
                "%s.*" + // IF
                "%s.*" + // DO
                "ENDRULE.*",
                clazz == null ? "" : "CLASS.*" + cleanForRegexp(clazz),
                interfaze == null ? "" : "INTERFACE.*" + cleanForRegexp(interfaze),
                method == null ? "" : ".*" + cleanForRegexp(method),
                at == null ? "" : "AT.*" + at,
                iff == null ? "" : "IF.*" + iff,
                        doo == null ? "" : "DO.*" + cleanForRegexp(doo));
            return Pattern.compile(regex, Pattern.DOTALL);
        }
    }
}
