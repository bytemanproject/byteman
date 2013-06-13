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
@rem batch which type checks a byteman rule set
@rem
@rem usage: bmcheck [-cp classpath]* [-p package]* [-v] script1 . . . scriptN
@rem
@rem -----------------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

@rem set byteman environment
call "%~dp0\bmsetenv.bat"
if %ERRORLEVEL% == 1 goto exitBatch

set CP=%BYTEMAN_JAR%
set PACKAGES=
set VERBOSE=
@rem for debugging purposes we will also pass through sys prop defines
set DEFINES=
@rem include application classes upplied via -cp flag and check for -v flag

@rem ===================================================================================
@rem start parse args section.
@rem ===================================================================================
:startArgsLoop

set ARG=%~1

@rem if there are no arguments, or not an option.
if "%ARG%"          == ""  goto endArgsLoop
if not "%ARG:~0,1%" == "-" goto endArgsLoop

if "%ARG%"      == "-cp" goto addClasspass
if "%ARG%"      == "-p"  goto addPackage
if "%ARG%"      == "-v"  goto setVerbose
if "%ARG:~0,2%" == "-D"  goto addDefine

goto showUsage

:addClasspass
set CP=%CP%;%~2
shift
shift
goto startArgsLoop

:addPackage
shift
set ARG=%~1
if "%ARG%" == "" goto showUsage
set PACKAGES=%PACKAGES% -p %ARG%
shift
goto startArgsLoop

:setVerbose
set VERBOSE=-v
shift
goto startArgsLoop

:addDefine
set DEFINES=%DEFINES% %ARG%
shift
goto startArgsLoop

:endArgsLoop
if "%~1" == "" goto showUsage
@rem ===================================================================================
@rem end parse args section.
@rem ===================================================================================


@rem ===================================================================================
@rem start parse file section.
@rem ===================================================================================
set error=0
:startFileLoop

@rem if there are no arguments.
if "%~1" == "" goto endFileLoop

@rem extract file attribute, and validate.
SET A=%~a1
if not "%A:~0,1%" == "-" goto showInvalidFileMessage

set FILES=%FILES% "%~1"

shift
goto startFileLoop

:showInvalidFileMessage
set error=1
echo %~1 is not a readable file
shift
goto startFileLoop

:endFileLoop
if %error% == 1 goto exitBatch
@rem ===================================================================================
@rem end parse file section.
@rem ===================================================================================


@rem Execute java program.
java %BYTEMAN_JAVA_OPTS% -classpath "%CP%" %DEFINES% org.jboss.byteman.check.TestScript %PACKAGES% %VERBOSE% %FILES%


:exitBatch
if "%OS%" == "Windows_NT" endlocal
exit /b

:showUsage
echo usage: bmcheck [-cp classpath]* [-p package]* [-v] script1 . . . scriptN
goto exitBatch
