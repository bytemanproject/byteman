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
# shell script which type checks a byteman rule set
#
# usage: bmcheck [-cp classpath]* [-p package]* [-v] script1 . . . scriptN
#
# use BYTEMAN_HOME to locate installed byteman release
if [ -z "$BYTEMAN_HOME" ]; then
# use the root of the path to this file to locate the byteman jar
    BYTEMAN_HOME=${0%*/bin/bmcheck.sh}
# allow for rename to plain bmcheck
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	BYTEMAN_HOME=${0%*/bin/bmcheck}
    fi
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	echo "Unable to find byteman home"
	exit
    fi
fi

# check that we can find  the byteman jar via BYTEMAN_HOME
if [ -r ${BYTEMAN_HOME}/lib/byteman.jar ]; then
    BYTEMAN_JAR=${BYTEMAN_HOME}/lib/byteman.jar
else
    echo "Cannot locate byteman jar"
    exit
fi
CP=${BYTEMAN_JAR}
PACKAGES=""
VERBOSE=""
# for debugging purposes we will also pass through sys prop defines
DEFINES=""
# include application classes upplied via -cp flag and check for -v flag
while [ $# -ne 0 -a ${1#-*} != ${1} ]; 
do
  if [ "$1" == "-cp" ] ; then
    CP=${CP}:$2
    shift
    shift
  elif [ "$1" == "-p" ] ; then
      shift
      if [ $# -ne 0 ] ; then
	  PACKAGES="$PACKAGES -p $1"
	  shift;
      else
	  echo "usage: bmcheck [-cp classpath]* [-p package]* [-v] script1 . . . scriptN"
      fi
  elif [ "$1" == "-v" ] ; then
    VERBOSE="-v"
    shift
  elif [ "${1#-D*}" != "$1" ] ; then
    DEFINES="$DEFINES $1"
    shift
  else
    echo "usage: bmcheck [-cp classpath]* [-p package]* [-v] script1 . . . scriptN"
    exit
  fi
done


SCRIPT_OPTS=""

if [ $# -eq 0 ] ; then
   echo "usage: bmcheck [-cp classpath]* [-p package]* [-v] script1 . . . scriptN"
   exit
fi

error=0
while [ $# -ne 0 ]
do
  if [ ! -f $1 -o ! -r $1 ] ; then
    echo "$1 is not a readable file";
    error=1
  fi
  FILES="${FILES} $1";
  shift
done

if [ $error -ne 0 ] ; then
  exit
fi

# allow for extra java opts via setting BYTEMAN_JAVA_OPTS

java ${BYTEMAN_JAVA_OPTS} -classpath ${CP} $DEFINES org.jboss.byteman.check.TestScript $PACKAGES $VERBOSE $FILES
