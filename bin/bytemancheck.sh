#!/bin/bash
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
