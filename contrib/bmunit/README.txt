#  JBoss, Home of Professional Open Source
#  Copyright 2010, 2011 Red Hat, Inc. and/or its affiliates,
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
# (C) 2010-11,
#  @author JBoss, by Red Hat.
#  @author Andrew Dinn (adinn@redhat.com) 2010-11


File
----

README.txt for Byteman contrib BMUnit

Summary
-------

BMUnit is a user package which automates loading of Byteman rules into JUnit
or TestNG tests. For guidance on how to use BMUnit from maven or ant see the
2nd Byteman tutorial at

  http://community.jboss.org/wiki/FaultInjectionTestingWithByteman#top


Usage Guide
-----------

This package simplifies use of  Byteman in JUnit and TestNG tests. It provides
two annotations, BMRule and BMScript, which you attach either to a test class
or to a test method. These annotations specify, respectively, the text of a
Byteman rule or the location of a Byteman rule script file which should be
installed before running your tests and deinstalled after the tests have
completed.

Class annotations identify rules which should be present for all tests in the
class. These rules are loaded before running any of the class's test methods
and only unloaded once all test methods have been executed. Method annoations
identify rules required for a specifc test. They are loaded just before the
test method gets called and unloaded after it has finished (or failed) before
executing the next test method.

The package also provides two grouping annotations, BMScripts and BMRules,
which allow multiple BMScript or BMRules annotations to be attached to a class
or method. These grouping annotations contain a field whose value is an array
of BMScript or BMRule annotations, respectively.

TestNG Style Tests
------------------
Your test class should extend class BMNGRunner. It inherits Before/AfterClass
and Before/AfterMethod behaviour to load the Byteman agent on demand and
load and unload rules in response to the presence of @BMScript, @BMScripts,
@BMRule or @BMRules annotations. So, your test class declaration will look
something like this

@BMScript(dir="test/scripts")
public class MyTestClass extends BMNGRunner
{
  @Test
  @BMRule(name="trace thread exit",
          targetClass = "FileInputStream",
          targetMethod = "<init>(String)",
          condition="$1.contains(\"andrew\")",
          action="throw new FileNotFoundException(\"ha ha\")")
  public void myTestMethod() {
    . . .

If you are using Byteman version 2.0.0 or later you can attach the Before
and After load behaviour to your test class by annotating it with class
org.testng.Listeners, supplying class BMNGListener as argument to the
annotation.

@BMRule(. . .)
@Listeners(BMNGListener.class)
public class MyTestClass
{
  @Test
  @BMScript(. . .)
  public void myTestMethod() {
    . . .

This is particulary useful when your test class needs to inherit
behaviour from a libray class.

JUnit 4 Style Tests
-------------------
Your test class should be annotated with annotation class org.junit.RunWith
supplyng class BMUnitRunner as argument to the annotation. BMUnitRunner will
load the Byteman agent on demand and will load and unload rules in response
to the presence of @BMScript, @BMScripts,  @BMRule or @BMRules annotations.

@BMScript(. . .)
@RunWith(BMUnitRunner.class)
public class MyTestClass
{
  @Test
  @BMScript(. . .)
  public void myTestMethod() {
    . . .


@BMScript(s) annotation
-----------------------
A @BMScript annotation is used  to load rules from a script file. It can be configured with
a directory name (field dir) and a test name (field value). If not supplied these values
will be defaulted and the rule file is searched for in a series of standard locations. The
test will fail if the script cannot be found. Multiple scripts can be specified using the
@BMScripts annotation whose scripts field can be initialised with a series of @BMScript
annotations.

The name of a class rules script file is determined using the annotation value and the test
class name or, if the value is omitted, just the class name. The name of a method rule script
file is determined using the annotation value and the test class name or, if the value
is omitted, the method name and class name (exact details follow below).

The directory from which to search for scripts can be configured using the dir field of
the BMScript annotation. If it is left unconfigured then the default location is used
(exact details follow below).

@BMRule(s) annotation
---------------------
A @BMRule annotation can be used to specify the text of a single rule. Annotation
fields define the target class, method, location, etc. Multiple rules can be
specified using the @BMRules annotation whose rules field can be initialised with
a series of @BMRule annotations. These annotations may be attached to either the
test class or a test method but their use at each location is exclusive i.e. it is not
legitimate for a class to be annotated with both annotations, ditto for a test method.
These annotations may also be used in combination with @BMScript annotations. Note
that @BMScript rules are always loaded before rules specified via @BMRule.

@BMScript file Lookup
---------------------
File lookup employs the computed test name and/or the test class name to locate
the rule script, trying various alternative combinations of these two values. If you
have configured a lookup directory then files are searched for below that directory.
Otherwise, System property org.jboss.byteman.contrib.bmunit.load.directory will be
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

Files are also searched for as classloader resources. If you have configured
a lookup directory then that directory is used as a prefix for the resource name.
Otherwise, System property org.jboss.byteman.contrib.bmunit.load.directory will be
checked and, if set, used as the resource name prefix. If this property is unset
then the value of org.jboss.byteman.contrib.bmunit.load.directory will be used
instead. If neither is set then no resource prefix will be used.

Note that when running on Windows any '/' separator occurring in a file name
will be substituted with a'\' character. If you wnat your tests to run on both
Windows and Linux/Unix then you should specify dir paths usng '/' as a separator.

JUnit 3 Style Tests
-------------------
If your test class inherits from BMTestCase then your test will load the Byteman
agent on demand and will automatically install Byteman rules before running a test
method then unload rules after the test has ended. The name of the rule script
is computed using the test class name and/or the test name. The test name can be
set in the constructor for the BMTestCase. You can optionally pass a String in the
constructor, identifying the directory from which to search for scripts. If this
argument is omitted then the default location is used (see below).

Note that for JUnit 3 style tests the same script is loaded and unloaded before and
after each test in the setup and tear down method. JUnit 3 stule tests do not
provide a means fo configuring different setup/tear down behaviour for individual
methods.

Test Environment Setup
----------------------
In order to run tests using BMUnit you need several jars in your classpath

byteman-bmunit,jar  -- the build product from this contrib package
byteman-install.jar -- the jar which contains the class needed to install the agent
byteman-submit.jar  -- the jar which contains the class needed to upload and unload rules
byteman.jar         -- the jar which contains agent itself
tools.jar           -- the JVM tools API jar which is normally found in $JAVA_HOME/lib.
junit.jar/          -- one or both depending on which test model you are using.
testng.jar             BMUnit has been tested with JUnit 4.8 or TestNG 5.14.6. Earlier
                       versions may also work, later ones should be fine

Note also that JAVA_HOME is the location where you installed a Java _JDK_ (not just a Java
_JRE_). The tools jar is not normally added to the Java runtime path. That normally only
includes jars from $JAVA_HOME/jre/lib. If you have only installed a Java runtime rather
then a full JDK you may not find a tools jar.

When running with surefire under maven you can simply add the byteman and testng/junit
jars as test dependencies, for example:

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.8.2</version>
        </dependency>
        <dependency>
            <groupId>org.jboss.byteman</groupId>
            <artifactId>byteman</artifactId>
            <scope>test</scope>
            <version>${byteman.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jboss.byteman</groupId>
            <artifactId>byteman-submit</artifactId>
            <scope>test</scope>
            <version>${byteman.version}</version>
        </dependency>
        . . .

You can also add the tools jar as a system dependency:

        . . .
        <dependency>
            <groupId>com.sun</groupId>
            <artifactId>tools</artifactId>
            <version>1.6</version>
            <scope>system</scope>
            <systemPath>${tools.jar}</systemPath>
        </dependency>
        . . .

Note that you must ensure that your test does not directly reference classes from the
agent jar. When the agent is autoloaded this jar is automatically installed into the
bootstrap classpath. If you load agent classes from your application (i.e. via the
system classpath) then all sorts of weird $#!+ will happen.

BMUnit Configuration
--------------------
You can configure some of the behaviour of the BMUnit package by setting various System
properties in the test JVM. See the BMUnit javadoc for full details.

Annotation based configuration of BMUnit is planned for the next release.
