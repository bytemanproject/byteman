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
have configured a lookup directory then files are searched for below that directory
as a path to the file. Files will also be looked up as resources on the class path
using the same directory setting as a resource path.

If the BMScript dir attribute is omitted the then any currently configured load
directory setting is used as the file lookup path and any currently configured
resource load directory is used the resource lookup path. See below for how to
configure these paths either via @BMUNitConfig annotations or using system
properties.

If the BMScript dir attribute is omitted and no load directory or resource directory
is specified then file lookups use the current working directory and resource lookups
use an empty resource path.

Files (or resources) are searched for as follows:

Let

  testName be the test name ("" or null means cases with * are ignored)
  org.my.TestCaseClass be the name of the test class
  <dir> be the configured script lookup directory (or resource lookup path)

Look for

1) <dir>/testName.btm *
2) <dir>/testName.txt *
3) <dir>/org/my/TestCaseClass-testName.btm *
4) <dir>/org/my/TestCaseClass-testName.txt *
5) <dir>/org/my/TestCaseClass.btm
6) <dir>/org/my/TestCaseClass.txt
7) <dir>/TestCaseClass.btm
8) <dir>/TestCaseClass.txt

Note that when running on Windows any '/' separator occurring in a file name
will be substituted with a'\' character (of course, '/' separators in resource
paths will be used unmodified). If you want your tests to run on both
Windows and Linux/Unix then you should specify dir paths using '/' as a
separator.

BMUnit Configuration and the @BMUnitConfig Annotation
-----------------------------------------------------

You can configure the behaviour of the BMUnit package (and also the
behaviour of the Byteman agent) using the BMUnitConfig annotation.
This annotation can be attached to your test classes to define a
configuration for all tests in the class. It can also be attached to a
test method to override the class-specific configuration for the
duration of that test method run.

The configuration associated with the first test class in a test run
is referred to as the default configuration. Subsequent class-level
configurations are interpreted as specifying configuration changes
relative to this default configuration. Method-level configurations
are interpreted relative to their class configuration. If a test
class (other than the first test class) omits a BMUnitConfig annotation
then the default configuration is used as its class configuration.

This is significant because the default configuration defines certain
settings which are used when auto-loading the Byteman agent into the
current JVM (specifically, these settings are configured from annotation
attributes inhibitAgentLoad, agentHost, agentPort, verbose, debug,
allowConfigUpdate and policy). This means that subsequently encountered
class or method configurations may not be able to reconfigure some of these
values.

If the first test class in a test run does not have a BMUnitConfig
annotation then a default configuration is generated using standard
values and these standard values will be used when auto-loading the
Byteman agent. As with earlier versions of BMUnit these standard values
may be configured using system property settings. Note these standard
values are not exactly the ame as the values used to fill in omitted
annotation fields. This disparity is needed to retain consistent
operation for older tests while ensuring that annotation-based configurations
can support overriding of the configuration from one class or test method
to the next.

The following table lists the attributes provided in a BMUnitConfig
annotation. The table displays the annotation default, the value used to
populate the default configuration if no annotation is present on the
first test class encountered and, where appropriate, the system property
which can be set to provide an alternative configuration.

    enforce                 false        false
    agentHost               ""           ""           org.jboss.byteman.contrib.bmunit.agent.host
    agentPort               ""           ""           org.jboss.byteman.contrib.bmunit.agent.port
    inhibitAgentLoad        false        false        org.jboss.byteman.contrib.bmunit.agent.inhibit
    loadDirectory           ""           ""           org.jboss.byteman.contrib.bmunit.load.directory
    resourceLoadDirectory   ""           ""           org.jboss.byteman.contrib.bmunit.resource.load.directory
    allowAgentConfigUpdate  true         false
    verbose                 false        false        org.jboss.byteman.verbose
    debug                   false        false        org.jboss.byteman.debug
    bmunitVerbose           false        false        org.jboss.byteman.contrib.bmunit.verbose
    policy                  false        false        org.jboss.byteman.contrib.bmunit.agent.policy

The meaning of each annotation is as follows:

enforce
-------
if true then /any/ incompatibilities between the current configuration
and the configuration specified in this annotation will lead to an
exception. if false incompatibilities will be ignored by using
the current config setting (as described below). enforce only applies
to certain of the attributes used to configure agent autoload i.e.
inhibitAgentLoad, verbose, debug, allowConfigUpdate and policy

agentHost
---------
this is the host name to be used when uploading rules found in BMScript
or BMRule annotations. if it is an empty string then the value active
in the current configuration is used. if the default configuration
value is provided as an empty string then the standard value
"localhost" will be used unless it is redefined by setting system
property org.jboss.byteman.contrib.bmunit.agent.host

n.b. if BMUnit autoloads the agent into the current JVM it will use this
hostname when opening its socket listener. you can use a different name
in other annotations but, at best, your rules will be uploaded to some
other Byteman agent and, at worst, rule upload will fail. This option
is most useful if you want to load the agent into a separate test JVM which
your test will drive via a network interface (e.g. as an HTTP client).

agentPort
---------
this is the port number to be used when uploading rules found in BMScript
or BMRule annotations. if it is an empty string then the value active
in the current configuration is used. if the default configuration
value is provided as an empty string then the standard value "9090"
will be used unless it is redefined by setting system property
org.jboss.byteman.contrib.bmunit.agent.port Note that any supplied
value must parse to a positive integer.

n.b. if BMUnit autoloads the agent into the current JVM it will use this
port when opening its socket listener. you can use a different port
in other annotations but, at best, your rules will be uploaded to some
other Byteman agent and, at worst, rule upload will fail. This option
is most useful if you want to load the agent into a separate test JVM which
your test will drive via a network interface (e.g. as an HTTP client).

inhibitAgentLoad
----------------
if true then BMUnit will not attempt to autoload the Byteman agent into the
current JVM. if false then BMUnit will autoload the agent before running
any tests. note that this attribute is only effective in the default
configuration which is used when BMUnit decides whether or not to load
the agent. So, if you don't autoload the agent before the first test it will
not be autoloaded for subsequent tests.

This option is most useful if you want to load the agent into a separate
test JVM which your test will drive via a network interface (e.g. as an
HTTP client). You can also use it if you want to perform loading of the
agent from the java command line or have your test manage loading of the
agent using the Byteman Install class..

loadDirectory
-------------
if this is a non-empty string then it will be used as the root path when
looking up scripts specified in BMScript annotations. (n.b. the BMScript
annotation can bypass this value by setting the its dir attribute to a
non-empty string). if loadDirectory is provided as an empty string then
the value from the current configuration is used instead. if the default
configuration specifies an empty string then the standard value
"" is used (i.e. no prefix) unless it is redefined by setting system
property org.jboss.byteman.contrib.bmunit.load.directory

resourceLoadDirectory
---------------------
if this is a non-empty string then it will be used as the root path when
looking up scripts specified in BMScript annotations as resources located
in the classpath. (n.b. the BMScript annotation can bypass this value by
setting the its dir attribute to a non-empty string). if resourceLoadDirectory
is provided as an empty string then the value from the current configuration
is used instead. if the default configuration specifies an empty
string then the standard value "" is used (i.e. no prefix) unless it is
redefined either by setting one or other of the two system propertes
org.jboss.byteman.contrib.bmunit.resource.load.directory or (failing that)
org.jboss.byteman.contrib.bmunit.load.directory.

allowAgentConfigUpdate
----------------------
this option is only effective in the default configuration as it
controls the behaviour of the autoloaded Byteman agent. if it is
true then the agent will respond to configuration changes e.g. you
will be able to switch on or off Byteman verbose trace per test
or test class. if it is false then any agent settings defined in
the default configuration will not be modifiable from subsequent
configurations.

n.b. the annotation default sets this to true. However, when generating
a default configuration this value is set to false, mimicking the
old behaviour. This is because enabling configuration means that
the agent must take a lock whenever it reads a configuration setting.
Although this is not very costly it may affect performance, causing
some older tests to give invalid results.

verbose
-------
if set to true this option enables the Byteman agent's verbose
tracing mode. if set to false it disables verbose tracing. n.b. this
setting is only effective when the default configuration set
allowAgentConfigUpdate to true. The standard value used when generating
a default configuration is false unless it is overridden by setting
system property org.jboss.byteman.verbose to any non-null value (yes,
that includes "true", "", "false" and "bazinga!")

debug
-----
if set to true this option enables the Byteman agent's debug
tracing mode. if set to false it disables debug tracing. n.b. this
setting is only effective when the default configuration set
allowAgentConfigUpdate to true. The standard value used when generating
a default configuration is false unless it is overridden by setting
system property org.jboss.byteman.debug to any non-null value (yes,
once again that includes "true", "", "false" and "bazinga!")

bmunitVerbose
-------------
if set to true this option enables BMUnit's verbose tracing mode.
if set to false it disables verbose tracing. The standard value used
when generating a default configuration is false unless it is overridden
by setting system property org.jboss.byteman.contrib.bmunit.verbose to
any non-null value (yes, you guessed it, that includes "true", "",
"false", "bazinga!" and "any non-zero-length string")

policy
------
this option is only effective in the default configuration as it
controls the behaviour of the autoloaded Byteman agent. if set to
true it requests that the agent install a security policy. if false
the agent will nto install a security policy. The standard value used
when generating a default configuration is false unless it is overridden
by setting system property org.jboss.byteman.contrib.bmunit.agent.policy
to "true" (no, this time "bazinga!" is not going to work).

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
testng.jar             BMUnit has been tested with JUnit 4.8 or TestNG 6.3.1
                       and 6.8.5. Earlier wil not work, later ones should be
                       fine

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

If you are running on jdk6/7/8 you *must* also add the tools jar as
a system dependency:

        . . .
        <dependency>
            <groupId>com.sun</groupId>
            <artifactId>tools</artifactId>
            <version>1.6</version>
            <scope>system</scope>
            <systemPath>${tools.jar}</systemPath>
        </dependency>
        . . .

Full details of how to specify pom dependencies and configure the
surefire plugin are provided in the byteman bmunit tutorial which
is linked from the documentation page at http://byteman.jboss.org

Note that you must ensure that your test does not directly reference classes from the
agent jar. When the agent is autoloaded this jar is automatically installed into the
bootstrap classpath. If you load agent classes from your application (i.e. via the
system classpath) then all sorts of weird $#!+ will happen.
