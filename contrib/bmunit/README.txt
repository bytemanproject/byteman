#  JBoss, Home of Professional Open Source
#  Copyright 2010, Red Hat, Inc. and/or its affiliates,
#  and individual contributors as indicated by the @author tags.
#  See the copyright.txt in the distribution for a
#  full listing of individual contributors.
#  This copyrighted material is made available to anyone wishing to use,
#  modify, copy, or redistribute it subject to the terms and conditions
#  of the GNU Lesser General Public License, v. 2.1.
#  This program is distributed in the hope that it will be useful, but WITHOUT A
#  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
#  PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
#  You should have received a copy of the GNU Lesser General Public License,
#  v.2.1 along with this distribution; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA  02110-1301, USA.
#
# (C) 2010,
#  @author JBoss, by Red Hat.


README.txt for Byteman contrib bmunit, a distributed test helper.

@author Andrew Dinn (adinn@redhat.com) 2010-12

This package simplifies use of  Byteman in JUnit  tests. It supports both old and
new style testing either by subclassing the old JUnit Testcase class or by annotating
your code  with @RunWith(BMUnitRunner) and @BMRules annotations.

If your test class inherits from BMTestCase then your test setup will load the Byteman
agent on demand and will automatically install Byteman rules before running a test
method then unload rules after the test has ended. The name of the rule script
is computed using the test class name and/or the test method name.

If  your class is annotated with @RunWith(BMUnitRunner) then the runner will load the
Byteman agent on demand and will load and unload rules in response to thepresence of
@BMRules annotations. If the class is annotated then the associated rules will be loaded
before running any tests and only unloaded until after all tests have executed. If a
test method (@Test annotated method) is also annotated with @BMRules then rules will
be loaded specifically for that test and unloaded after the test completes. The name
of the class rules  script is determined using the annotation value and test class name
or, if the value is defaulted to "", just the class name. The name of the method rules
script is determined using the annotation value and the test class name
or, if the value is defaulted to "", the method name and class name.


The script lookup employs the computed test name and/or the test class name to locate
the rule script. It will look for script below the working directory but you can override
this by setting System property org.jboss.byteman.contrib.bmunit.script.directory.
Files are searched for as follows:

Let

  testName be the computed name ("" or null means cases with * are ignored)
  org.my.TestCaseClass be the name of the test class
  <dir> be the configured script directory (default is the dir in which the test is run)

Look for

1) <dir>/testName.bmr *
2) <dir>/testName.txt *
3) <dir>/org/my/TestCaseClass-testName.bmr *
4) <dir>/org/my/TestCaseClass-testName.txt *
5) <dir>/org/my/TestCaseClass.bmr
6) <dir>/org/my/TestCaseClass.txt
7) <dir>/TestCaseClass.bmr
8) <dir>/TestCaseClass.txt

In order to run tests using BMTestCase you need several jars in your classpath

byteman-bmunit,jar  -- the build product from this contrib package
byteman-install.jar -- the jar which contains the class needed to install the agent
tools.jar -- the JVM tools API jar which is normally found in $JAVA_HOME/lib.

You also need to provide your test process with an explicit location for the Byteman
agent jar, byteman.jar, by setting environment variable BYTEMAN_HOME to the directory
in which Byteman has been installed. The jar should be located in the lib subdirectory.

Note that JAVA_HOME is the location where you installed a Java JDK (not just a Java JRE)
This jar is not normally added to the Java runtime path. That normally only includes jars
from $JAVA_HOME/jre/lib. If you have only installed a Java runtime rather then a full JDK
you may not find a tools jar.
