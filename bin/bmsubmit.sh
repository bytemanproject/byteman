#!/bin/bash
#
# JBoss, Home of Professional Open Source
# Copyright 2009-11, Red Hat and individual contributors
# by the @authors tag. See the copyright.txt in the distribution for a
# full listing of individual contributors.
#
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
#
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.
#
# @authors Andrew Dinn
#
# shell script which submits a request to the Byteman agent listener
# either to list, install or uninstall rule scripts
#
# usage: bmsubmit [-o outfile] [-p port] [-h host] [-l|-u] [script1 . . . scriptN]
#        bmsubmit [-o outfile] [-p port] [-h host] [-b | -s] bootjar1 . . .
#        bmsubmit [-o outfile] [-p port] [-h host] -c
#        bmsubmit [-o outfile] [-p port] [-h host] -y [prop1[=[value1]]. . .]
#        bmsubmit [-o outfile] [-p port] [-h host] -v
#   -o redirects output from System.out to outfile
#   -p specifies the listener port (default 9091)
#   -h specifies the listener host name (default localhost)
#   -l (default) install rules in script1 . . . scriptN
#      with no scripts list all installed rules
#   -u uninstall rules in script1 . . . scriptN
#      with no scripts uninstall all installed rules
#
#   -b install jar files bootjar1 etc into bootstrap classpath
#
#   -s install jar files bootjar1 etc into system classpath
#
#   -c print the jars that have been added to the system and boot classloaders
#
#   -y with no args list all byteman config system properties
#      with args modifies specified byteman config system properties
#        prop=value sets system property 'prop' to value
#        prop= sets system property 'prop' to an empty string
#        prop unsets system property 'prop'
#
#   -v print the version of the byteman agent and this client 
#
# use BYTEMAN_HOME to locate installed byteman release
if [ -z "$BYTEMAN_HOME" ]; then
# use the root of the path to this file to locate the byteman jar
    BYTEMAN_HOME=${0%*/bin/bmsubmit.sh}
# allow for rename to plain submit
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	BYTEMAN_HOME=${0%*/bin/bmsubmit}
    fi
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	echo "Unable to find byteman home"
	exit
    fi
fi

# the byteman and byteman-submit jars should be in ${BYTEMAN_HOME}/lib
if [ -r ${BYTEMAN_HOME}/lib/byteman.jar ]; then
    BYTEMAN_JAR=${BYTEMAN_HOME}/lib/byteman.jar
else
    echo "Cannot locate byteman jar"
    exit
fi
if [ -r ${BYTEMAN_HOME}/lib/byteman-submit.jar ]; then
    BYTEMAN_SUBMIT_JAR=${BYTEMAN_HOME}/lib/byteman-submit.jar
else
    echo "Cannot locate byteman-submit jar"
    exit
fi
# allow for extra java opts via setting BYTEMAN_JAVA_OPTS
# Submit class will validate arguments

java ${BYTEMAN_JAVA_OPTS} -classpath ${BYTEMAN_JAR}:${BYTEMAN_SUBMIT_JAR} org.jboss.byteman.agent.submit.Submit $*
