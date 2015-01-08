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
@rem batch which can be used to install the Byteman agent into
@rem a JVM which was started without the agent. This provides an
@rem alternative to using the -javaagent java command line flag
@rem
@rem usage: bminstall [-p port] [-h host] [-b] [-s] [-Dname[=value]]* pid
@rem   pid is the process id of the target JVM
@rem   -h host selects the host name or address the agent listener binds to
@rem   -p port selects the port the agent listener binds to
@rem   -b adds the byteman jar to the bootstrap classpath
@rem   -s sets an access-all-areas security policy for the Byteman agent code
@rem   -Dname=value can be used to set system properties whose name starts with "org.jboss.byteman."
@rem   expects to find a byteman agent jar in BYTEMAN_HOME
@rem
@rem -----------------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

if "%~1" == "" goto showUsage

@rem set byteman environment
call "%~dp0\bmsetenv.bat"
if %ERRORLEVEL% == 1 goto exitBatch

@rem the Install class is in the byteman-install jar
if exist "%BYTEMAN_HOME%\lib\byteman-install.jar" goto okInstallJar
echo "Cannot locate byteman install jar"
goto exitBatch

:okInstallJar
set BYTEMAN_INSTALL_JAR=%BYTEMAN_HOME%\lib\byteman-install.jar

set CP=%BYTEMAN_INSTALL_JAR%

@rem we also need a tools jar from JAVA_HOME
if not "%JAVA_HOME%" == "" goto okJavaHome
echo please set JAVA_HOME
@rem carry on anyway as this is legitimate for jdk9
goto noTools

:okJavaHome

if exist "%JAVA_HOME%\lib\tools.jar" goto okTools
echo Cannot locate tools jar
@rem carry on anyway as this is legitimate for jdk9
goto noTools

:okTools
set CP=%BYTEMAN_INSTALL_JAR%;%JAVA_HOME%\lib\tools.jar

:noTools

@rem exception avoidance; java.lang.UnsatisfiedLinkError: no attach in java.library.path
if exist "%JAVA_HOME%\jre\bin" set PATH=%PATH%;%JAVA_HOME%\jre\bin

@rem allow for extra java opts via setting BYTEMAN_JAVA_OPTS
@rem attach class will validate arguments
java %BYTEMAN_JAVA_OPTS% -classpath "%CP%" org.jboss.byteman.agent.install.Install %*

:exitBatch
if "%OS%" == "Windows_NT" endlocal
exit /b

:showUsage
echo usage: bminstall [-p port] [-h host] [-b] [-s] [-Dname[=value]]* pid
echo   pid is the process id of the target JVM
echo   -h host selects the host name or address the agent listener binds to
echo   -p port selects the port the agent listener binds to
echo   -b adds the byteman jar to the bootstrap classpath
echo   -s sets an access-all-areas security policy for the Byteman agent code
echo   -Dname=value can be used to set system properties whose name starts with "org.jboss.byteman."
echo   expects to find a byteman agent jar in BYTEMAN_HOME
goto exitBatch
