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
@rem batch which submits a request to the Byteman agent listener
@rem either to list, install or uninstall rule scripts
@rem
@rem usage: bmsubmit [-o outfile] [-p port] [-h host] [-l|-u] [script1 . . . scriptN]
@rem        bmsubmit [-o outfile] [-p port] [-h host] [-b | -s] bootjar1 . . .
@rem        bmsubmit [-o outfile] [-p port] [-h host] -c
@rem        bmsubmit [-o outfile] [-p port] [-h host] -y [prop1[=[value1]]. . .]
@rem        bmsubmit [-o outfile] [-p port] [-h host] -v
@rem   -o redirects output from System.out to outfile
@rem   -p specifies the listener port (default 9091)
@rem   -h specifies the listener host name (default localhost)
@rem   -l (default) install rules in script1 . . . scriptN
@rem      with no scripts list all installed rules
@rem   -u uninstall rules in script1 . . . scriptN
@rem      with no scripts uninstall all installed rules
@rem
@rem   -b install jar files bootjar1 etc into bootstrap classpath
@rem
@rem   -s install jar files bootjar1 etc into system classpath
@rem
@rem   -c print the jars that have been added to the system and boot classloaders
@rem
@rem   -y with no args list all byteman config system properties
@rem      with args modifies specified byteman config system properties
@rem        prop=value sets system property 'prop' to value
@rem        prop= sets system property 'prop' to an empty string
@rem        prop unsets system property 'prop'
@rem
@rem   -v print the version of the byteman agent and this client 
@rem
@rem -----------------------------------------------------------------------------------
if "%OS%" == "Windows_NT" setlocal

if "%~1" == "" goto showUsage

@rem set byteman environment
call "%~dp0\bmsetenv.bat"
if %ERRORLEVEL% == 1 goto exitBatch

if exist "%BYTEMAN_HOME%\lib\byteman-submit.jar" goto okSubmitJar
echo "Cannot locate byteman-submit jar"
goto exitBatch

:okSubmitJar
set BYTEMAN_SUBMIT_JAR=%BYTEMAN_HOME%\lib\byteman-submit.jar

@rem Execute Java Program
java %BYTEMAN_JAVA_OPTS% -classpath "%BYTEMAN_JAR%;%BYTEMAN_SUBMIT_JAR%" org.jboss.byteman.agent.submit.Submit %*

:exitBatch
if "%OS%" == "Windows_NT" endlocal
exit /b

:showUsage
echo shell script which submits a request to the Byteman agent listener
echo either to list, install or uninstall rule scripts
echo.
echo usage: bmsubmit [-o outfile] [-p port] [-h host] [-l^|-u] [script1 . . . scriptN]
echo        bmsubmit [-o outfile] [-p port] [-h host] [-b ^| -s] bootjar1 . . .
echo        bmsubmit [-o outfile] [-p port] [-h host] -c
echo        bmsubmit [-o outfile] [-p port] [-h host] -y [prop1[=[value1]]. . .]
echo        bmsubmit [-o outfile] [-p port] [-h host] -v
echo   -o redirects output from System.out to outfile
echo   -p specifies the listener port (default 9091)
echo   -h specifies the listener host name (default localhost)
echo   -l (default) install rules in script1 . . . scriptN
echo      with no scripts list all installed rules
echo   -u uninstall rules in script1 . . . scriptN
echo      with no scripts uninstall all installed rules
echo.
echo   -b install jar files bootjar1 etc into bootstrap classpath
echo.
echo   -s install jar files bootjar1 etc into system classpath
echo.
echo   -c print the jars that have been added to the system and boot classloaders
echo.
echo   -y with no args list all byteman config system properties
echo      with args modifies specified byteman config system properties
echo        prop=value sets system property 'prop' to value
echo        prop= sets system property 'prop' to an empty string
echo        prop unsets system property 'prop'
echo.
echo   -v print the version of the byteman agent and this client 
goto exitBatch
