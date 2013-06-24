#  JBoss, Home of Professional Open Source
#  Copyright 2013 Red Hat, Inc. and/or its affiliates,
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
# (C) 2013,
#  @author JBoss, by Red Hat.
#  @author Amos Feng (zfeng@redhat.com)
#  @author Andrew Dinn (adinn@redhat.com)

The Byteman rule check maven plugin has been contributed by Amos Feng.
It automates parsing and type checking of your Byteman test rule
scripts as part of your maven build. It does the same job as the
bmcheck script provided in the bin directory of your Byteman download
but without the need for you to run checks by hand.

The plugin is run when maven executes the maven test goal before it
runs your unit or integration tests. The Byteman parser and type
checker check all the rule scripts configured in the plugin:

  - if there are any parse errors or type errors in your scripts the
    plugin will print the errors and abort the test run.

  - if there are any type warnings or if any of your rules fail to be
    injected because there is no matching target class or method then,
    by default, the plugin will print the warnings but allow your test
    run to continue.

  - if you configure fail on warning then any warnings will cause the
    test run to be aborted.

  - if you configure fail on warning but specify an expected warning
    count then the test run will be allowed to continue only if the
    expected number of warnings are generated.

An example of how to configure the plugin in your pom file is provided
in subdirectory example. n.b. a copnfiguration error will abort the
test run.

elements in the configuration section of the pom include

includes -- a list of directory + file patterns used to locate the
            Byteman rule scripts to be type checked (default is the
            list containing the single entry **/*.btm)

excludes -- a list of directory + file patterns used to locate the
            Byteman rule scripts to be excluded from type checking
            (default is the empty list)

packages -- a list of package names which may be used to resolve
            classes (or interfaces) referenced from the CLASS (or
            INTERFACE) clause of a rule without a package qualifier
            (default is an empty list)

additionalClassPath -- a ':' separated list of directories identifying
            extra directories or jars (i.e. extra beyond those used
            for the tests themsleves) in which classes mentioned in
            the Byteman scripts may be located (default is empty)

failOnError -- flag set to true if a check failure should cause the
            test run to fail or false if tests should continue anyway.
            (default true)

failOnWarning -- flag set to true if a check warning should cause the
            test run to fail or false if tests should continue anyway.
            it is a configuration error if failOnWarning is set to
            false when failOnError is set to true (defalt true)

expectWarnings -- count of how many warnings are to be expected. the
            test run will fail if the number of check warnings does
            not equal this count. it is a configuration error if
            expectWarnings is set to a positive count when
            failOnWarning is set to false. (default 0)

skip        -- flag set to true if rule checks shoudl be skipped (default
            false)
