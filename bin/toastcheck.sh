#!/bin/bash
#
# shell script which type checks a TOAST rule set
#
# usage: toastcheck -cp classpath script1 . . . scriptN
#
BASE=${0%*/bin/toastcheck.sh}
CP=${BASE}/build/lib/orchestration.jar
CP=${CP}:${BASE}}/ext/asm-all-3.0.jar
if [ $1 == "-cp" ] ; then
  CP=${CP}:$2
  shift
  shift
fi

SCRIPT_OPTS=""

if [ $# -eq 0 ] ; then
   echo "usage: toastcheck -cp classpath script1 . . . scriptN"
   exit
fi

if [ ${1#-*} != ${1} ]; then
   echo "usage: toastcheck -cp classpath script1 . . . scriptN"
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

java -classpath ${CP} org.jboss.jbossts.orchestration.test.TestScript $FILES
