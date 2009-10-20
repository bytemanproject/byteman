#!/bin/bash
#
# JBoss, Home of Professional Open Source
# Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
# usage: submit [-l|-u] [script1 . . . scriptN]
#   -l (default) install rules in script1 . . . scriptN
#      with no args list all installed rules
#   -u uninstall rules in script1 . . . scriptN
#      with no args uninstall all installed rules
#
# use BYTEMAN_HOME to locate installed byteman release
if [ -z "$BYTEMAN_HOME" ]; then
# use the root of the path to this file to locate the byteman jar
    BYTEMAN_HOME=${0%*/bin/bmjava.sh}
# allow for rename to plain bmjava
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	BYTEMAN_HOME=${0%*/bin/bmjava}
    fi
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	echo "Unable to find byteman home"
	exit
    fi
fi

# the binary release puts byteman jar in lib while source puts it in
# build/lib so add both paths to the classpath just in case
if [ -r ${BYTEMAN_HOME}/lib/byteman.jar ]; then
    BYTEMAN_JAR=${BYTEMAN_HOME}/lib/byteman.jar
elif [ -r ${BYTEMAN_HOME}/build/lib/byteman.jar ]; then
    BYTEMAN_JAR=${BYTEMAN_HOME}/build/lib/byteman.jar
else
    echo "Cannot locate byteman jar"
    exit
fi
# allow for extra java opts via setting BYTEMAN_JAVA_OPTS
# Submit class will validate arguments

java ${BYTEMAN_JAVA_OPTS} -classpath ${BYTEMAN_JAR} org.jboss.byteman.agent.submit.Submit $*
