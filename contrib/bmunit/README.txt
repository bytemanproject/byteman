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

This package provides a subclass of the standard JUnit class testCase called BMTestCase
which simplifies using  Byteman in JUnit  tests. If your test class inherits from
BMTestCase then your test setup will automatically load the Byteman agent and install
Byteman rules before running the test and the teardown will unload the rules after the
test has ended.

BMtestCase uses the name of the test case and/or the name of the test class to locate
the rule script. It will look for script below the working directory but you can override
this by setting System property org.jboss.byteman.contrib.bmunit.script.directory.
Files are searched for as follows:

Let

  testName be the name of the test
  org.my.TestCaseClass be the name of the test class
  <dir> be the configured script directory (default is the dir in which the test is run)

Look for

1) <dir>/testName.bmr
2) <dir>/testName.txt
3) <dir>/org/my/TestCaseClass-testName.bmr
4) <dir>/org/my/TestCaseClass-testName.txt
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
