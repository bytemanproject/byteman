#!/bin/bash
#
# JBoss, Home of Professional Open Source
# Copyright 2009, Red Hat Middleware LLC, and individual contributors
# by the @authors tag. See the copyright.txt in the distribution for a
# full listing of individual contributors.
*
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
*
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
*
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
# @authors Andrew Dinn
#
# shell script which type checks a byteman rule set
#
# usage: bytemancheck -cp classpath script1 . . . scriptN
#
BASE=${0%*/bin/bytemancheck.sh}
CP=${BASE}/build/lib/byteman.jar
CP=${CP}:${BASE}}/ext/asm-all-3.0.jar
if [ $1 == "-cp" ] ; then
  CP=${CP}:$2
  shift
  shift
fi

SCRIPT_OPTS=""

if [ $# -eq 0 ] ; then
   echo "usage: bytemancheck -cp classpath script1 . . . scriptN"
   exit
fi

if [ ${1#-*} != ${1} ]; then
   echo "usage: bytemancheck -cp classpath script1 . . . scriptN"
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

java ${BYTEMAN_JAVA_OPTS} -classpath ${CP} org.jboss.byteman.test.TestScript $FILES
