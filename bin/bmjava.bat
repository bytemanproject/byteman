@echo off
@rem -----------------------------------------------------------------------------------
@rem
@rem JBoss, Home of Professional Open Source
@rem Copyright 2009-11, Red Hat and individual contributors
@rem by the @authors tag. See the copyright.txt in the distribution for a
@rem full listing of individual contributors.
@rem
@rem This is free software; you can redistribute it and/or modify it
@rem under the terms of the GNU Lesser General Public License as
@rem published by the Free Software Foundation; either version 2.1 of
@rem the License, or (at your option) any later version.
@rem
@rem This software is distributed in the hope that it will be useful,
@rem but WITHOUT ANY WARRANTY; without even the implied warranty of
@rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
@rem Lesser General Public License for more details.
@rem You should have received a copy of the GNU Lesser General Public
@rem License along with this software; if not, write to the Free
@rem Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
@rem 02110-1301 USA, or see the FSF site: http://www.fsf.org.
@rem
@rem @authors Kenji Suzuki
@rem 
@rem batch which starts a java program with the byteman
@rem agent installed
@rem
@rem usage: bmjava [-p port] [-h host] \
@rem          [-l rulescript | -b bootjar | -s sysjar | -nl | -nb ]* [--] javaargs
@rem   -p use the number which follows as the port when opening the listener
@rem      socket
@rem
@rem   -h use the string which follows as the host name when opening
@rem      the listener socket
@rem
@rem   -l pass the file whose name follows this flag to the agent as
@rem      a rule script
@rem
@rem   -b pass the file whose name follows this flag to the agent as
@rem      a jar to be added to the bootstrap classpath
@rem
@rem   -s pass the file whose name follows this flag to the agent as
@rem      a jar to be added to the system classpath
@rem
@rem   -nl do not enable the agent listener (it is enabled by default)
@rem
@rem   -nb do not add the byteman jar to the bootstrap classpath (it is
@rem       added by default)
@rem
@rem
@rem   -- optional separator to distinguish trailing arguments
@rem
@rem   javaargs trailing arguments to be supplied to the java command
@rem
@rem The script employs the java command found in the current execution
@rem PATH. If BYTEMAN_JAVA_ARGS is set then this is inserted to the
@rem java command line before the -javaagent argument and before any
@rem arguments in javaargs.
@rem
@rem -----------------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

if "%~1" == "" goto showUsage

@rem set byteman environment
call "%~dp0\bmsetenv.bat"
if %ERRORLEVEL% == 1 goto exitBatch

set AGENT_PREFIX=-javaagent:%BYTEMAN_JAR%
set AGENT_OPTS=

@rem default is to use listener and add byteman jar to bootstrap classpath
set LISTENER=1
set BYTEMAN_BOOT_JAR=1
set INJECT_JAVA_LANG=1
set PORT=
set HOST=

@rem ===================================================================================
@rem start parse args section.
@rem ===================================================================================
:startArgsLoop

set ARG=%~1

@rem if there are no arguments, or not an option.
if     "%ARG%"      == ""  goto endArgsLoop
if not "%ARG:~0,1%" == "-" goto endArgsLoop

@rem parse arg.
if "%ARG%" == "-l" if not "%~2" == "" goto addScript
if "%ARG%" == "-b" if not "%~2" == "" goto addBootJar
if "%ARG%" == "-s" if not "%~2" == "" goto addSystemJar
if "%ARG%" == "-p" if not "%~2" == "" goto setPort
if "%ARG%" == "-h" if not "%~2" == "" goto setHost
if "%ARG%" == "-nl"                   goto notUseListener
if "%ARG%" == "-nb"                   goto notUseBytemanBootJar
if "%ARG%" == "-nj"                   goto notInjectJavaLang
if "%ARG%" == "--"                    goto breakArgsLoop

@rem unrecognised option -- must be start of javaargs
goto endArgsLoop

@rem set or add option label --------------------
:addScript
call :addFileOption "script" "%~2" "Cannot read script"
if %ERRORLEVEL% == 1 goto exitBatch
goto shift2AndToNext

:addBootJar
call :addFileOption "boot" "%~2" "Cannot read boot jar"
if %ERRORLEVEL% == 1 goto exitBatch
goto shift2AndToNext

:addSystemJar
call :addFileOption "sys" "%~2" "Cannot read system jar"
if %ERRORLEVEL% == 1 goto exitBatch
goto shift2AndToNext

:setPort
set PORT=%~2
goto shift2AndToNext

:setHost
set HOST=%~2
goto shift2AndToNext

:notUseListener
set LISTENER=0
goto shiftAndToNext

:notUseBytemanBootJar
set BYTEMAN_BOOT_JAR=0
goto shiftAndToNext

:notInjectJavaLang
set INJECT_JAVA_LANG=0
goto shiftAndToNext

:breakArgsLoop
shift
goto endArgsLoop
@rem -------------------------------------------

@rem util label --------------------------------
:shiftAndToNext
shift
goto startArgsLoop

:shift2AndToNext
shift
shift
goto startArgsLoop
@rem -------------------------------------------

@rem subroutine label --------------------------
@rem /******************************************
@rem  * add option arg as file.
@rem  * %1 name of option
@rem  * %2 value of option
@rem  * %3 error message 
@rem  *****************************************/
:addFileOption
SET A=%~a2
if "%A:~0,1%" == "-" goto doAddFileOption
@rem show error message
echo %~3 %~2 
exit /b 1

:doAddFileOption
set AGENT_OPTS=%AGENT_OPTS%,%~1:%~2
exit /b 0
@rem -------------------------------------------

:endArgsLoop
@rem ===================================================================================
@rem end parse args section.
@rem ===================================================================================


@rem ===================================================================================
@rem start trailing arg section --------------------------------------------------------
@rem ===================================================================================
:startTrailingArgsLoop
set ARG=%~1
if "%ARG%" == "" goto endTrailingArgsLoop
set TRAILING_ARGS=%TRAILING_ARGS% %ARG%
shift
goto startTrailingArgsLoop
:endTrailingArgsLoop
@rem ===================================================================================
@rem end trailing arg section ----------------------------------------------------------
@rem ===================================================================================

@rem ===================================================================================

if %BYTEMAN_BOOT_JAR% == 1 set AGENT_OPTS=%AGENT_OPTS%,boot:%BYTEMAN_JAR%

@rem --------------------------------------------------------------------
if %LISTENER% == 1 (
  goto setUseListenerValue
) else (
  goto setNotUseListenerValue
)

:setUseListenerValue
set USE_LISTENER=true
if not "%PORT%" == "" set AGENT_OPTS=%AGENT_OPTS%,port:%PORT%
if not "%HOST%" == "" set AGENT_OPTS=%AGENT_OPTS%,address:%HOST%
goto okListener

:setNotUseListenerValue
set USE_LISTENER=false
if "%PORT%" == "" goto okPortIncompatible
echo incompatible opions -p and -nl
goto exitBatch

:okPortIncompatible
if "%HOST%" == "" goto okListener
echo incompatible opions -h and -nl
goto exitBatch

:okListener
set AGENT_OPTS=listener:%USE_LISTENER%%AGENT_OPTS%
@rem --------------------------------------------------------------------

if %INJECT_JAVA_LANG% == 1 (
  set INJECT_JAVA_LANG_OPTS=-Dorg.jboss.byteman.transform.all
) else (
  set INJECT_JAVA_LANG_OPTS=
)

set AGENT_ARGUMENT=%AGENT_PREFIX%=%AGENT_OPTS%

@rem ===================================================================================

@rem Execute Java Program
java %BYTEMAN_JAVA_OPTS% "%AGENT_ARGUMENT%" %INJECT_JAVA_LANG_OPTS% %TRAILING_ARGS%

goto exitBatch

:exitBatch
if "%OS%" == "Windows_NT" endlocal
exit /b

@rem ---------------------------------------------------------------------------------------
@rem Usage
@rem ---------------------------------------------------------------------------------------
:showUsage
echo usage: bmjava  [-p port] [-h host] [-l rulescript ^| -b bootjar ^| -s sysjar ^| -nl ^| -nb ^| -nj ]* [--] javaargs
echo.
echo terms enclosed between [ ] are optional
echo terms separated by ^| are alternatives
echo a * means zero or more occurences
echo.
echo    -p use the number which follows as the port when opening the
echo       listener socket (default is 9091)
echo.
echo    -h use the string which follows as the host name when opening
echo       the listener socket (default is localhost)
echo.
echo   -l  pass the file whose name follows this flag to the agent as
echo       a rule script to be loaded during startup
echo.
echo   -b  pass the file whose name follows this flag to the agent as
echo       a jar to be added to the bootstrap classpath
echo.
echo   -s  pass the file whose name follows this flag to the agent as
echo       a jar to be added to the system classpath
echo.
echo   -nl  do not enable the agent listener (it is enabled by default)
echo.
echo   -nb  do not add the byteman jar to the bootstrap classpath (it is
echo        added by default)
echo.
echo   -nj  do not inject into java.lang classes (it is enabled by default)
echo.
echo   --  optional separator to distinguish trailing arguments
echo.
echo   javaargs  trailing arguments to be supplied to the java command
echo.
echo The script constructs a -javaagent argument to pass to the java
echo command found in the current execution PATH. If BYTEMAN_JAVA_ARGS
echo is set then this is inserted to the java command line before
echo the -javaagent argument and before any arguments in javaargs.
goto exitBatch
