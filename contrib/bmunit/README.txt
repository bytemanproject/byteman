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
#  @author Andrew Dinn (adinn@redhat.com) 2010-12


File
----

README.txt for Byteman contrib BMUnit

Summary
-------

BMUnit is a user package which automates loading of Byteman rules into JUnit tests.

Usage Guide
-----------

This package simplifies use of  Byteman in JUnit  tests. It supports both old and
new style testing either by subclassing the old JUnit TestCase class or by annotating
your test class with @RunWith(BMUnitRunner) and adding @BMScript or @BMRule/@BMRules
annotations to the test class or individual test methods.

JUnit 4.8 Style Tests
If  your class is annotated with @RunWith(BMUnitRunner) then the runner will load the
Byteman agent on demand and will load and unload rules in response to the presence of
@BMScript, @BMRule or @BMRules annotations. If the class is annotated then the associated
rules will be loaded before running any tests and only unloaded until after all tests
have executed. If a test method (@Test annotated method) is also annotated with @BMScript
or @BMRule/@BMRules then rules will be loaded specifically for that test and unloaded after
the test completes.

@BMScript annotation

This annotation is used  to load rules from a script file. It can be configured with
a directory name (field dir) and a test name (field value). If not supplied these values
will be defaulted and the rule file is searched for in a series of standard locations. The
test will fail if the script cannot be found.

The name of a class rules script file is determined using the annotation value and the test
class name or, if the value is omitted, just the class name. The name of a method rule script
file is determined using the annotation value and the test class name or, if the value
is omitted, the method name and class name (exact details follow below).

The directory from which to search for scripts can be configured using the dir field of
the BMScript annotation. When set in a class level annotation it provides a value
used for all tests. When set in a method level annotation it provides a value for that
specific test, overriding any class level setting. If it is left unconfigured in both
class and method annotations then the default location is used (exact details follow below).

@BMRule(s) annotation

A @BMRule annotation can be used to specify the text of a single rule. Annotation
fields define the target class, method, location, etc. Multiple rules can be
specified using the @BMRules annotation whose rules field can be initialised with
a series of @BMRule annotations. These annotations may be attached to either the
test class or a test method but their use at each location is exclusive i.e. it is not
legitimate for a class to be annotated with both annotations, ditto for a test method.
These annotations may also be used in combination with @BMScript annotations. Note
that @BMScript rules are always loaded before rules specified via @BMRule.

JUnit 3 Style Tests
If your test class inherits from BMTestCase then your test will load the Byteman
agent on demand and will automatically install Byteman rules before running a test
method then unload rules after the test has ended. The name of the rule script
is computed using the test class name and/or the test name. The test name can be
set in the constructor for the BMTestCase. You can optionally pass a String in the
constructor, identifying the directory from which to search for scripts. If this
argument is omitted then the default location is used (see below).

@BMScript fle Lookup
File lookup employs the computed test name and/or the test class name to locate
the rule script, trying various alternative combinations of these two values. If you
have configured a lookup directory then files are searched for below that directory.
Otherwise, System property org.jboss.byteman.contrib.bmunit.script.directory will be
checked and, if set, used as the search directory. Failing that the search will proceed
using the working directory of the test.

Files are searched for as follows:

Let

  testName be the test name ("" or null means cases with * are ignored)
  org.my.TestCaseClass be the name of the test class
  <dir> be the configured script lookup directory

Look for

1) <dir>/testName.btm *
2) <dir>/testName.txt *
3) <dir>/org/my/TestCaseClass-testName.btm *
4) <dir>/org/my/TestCaseClass-testName.txt *
5) <dir>/org/my/TestCaseClass.btm
6) <dir>/org/my/TestCaseClass.txt
7) <dir>/TestCaseClass.btm
8) <dir>/TestCaseClass.txt

In order to run tests using BMTestCase you need several jars in your classpath

byteman-bmunit,jar  -- the build product from this contrib package
byteman-install.jar -- the jar which contains the class needed to install the agent
tools.jar -- the JVM tools API jar which is normally found in $JAVA_HOME/lib.

You also need to provide your test process with an explicit location for the Byteman
agent jar, byteman.jar, by setting environment variable BYTEMAN_HOME to the directory
in which Byteman has been installed. The agent jar should be in the lib subdirectory.

Note that you should not install the Byteman agent jar into your application classpath.
When the agent is autoloaded it is automatically installed into the bootstrap classpath.
If you locate the agent jar either in the system classpath or in your application deployment
then all sorts of weird $#!+ will happen.

Note also that JAVA_HOME is the location where you installed a Java JDK (not just a Java JRE)
This jar is not normally added to the Java runtime path. That normally only includes jars
from $JAVA_HOME/jre/lib. If you have only installed a Java runtime rather then a full JDK
you may not find a tools jar.
