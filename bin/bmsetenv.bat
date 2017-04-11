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
@rem This batch file to set below environment.
@rem BYTEMAN_HOME and BYTEMAN_JAR
@rem
@rem -----------------------------------------------------------------------------------

@rem use BYTEMAN_HOME to locate installed byteman release
if not "%BYTEMAN_HOME%" == "" goto gotHome
set "CURRENT_DIR=%cd%"
cd %~dp0
cd ..
set "BYTEMAN_HOME=%cd%"
cd "%CURRENT_DIR%"

:gotHome
if exist "%BYTEMAN_HOME%\lib\byteman.jar" goto okJar
echo Cannot locate byteman jar
exit /b 1

:okJar
set "BYTEMAN_JAR=%BYTEMAN_HOME%\lib\byteman.jar"
if exist "%BYTEMAN_HOME%\contrib\jboss-modules-system\byteman-jboss-modules-plugin.jar" goto okPluginJar
echo Cannot locate byteman JBoss modules plugin jar

:okPluginJar
set "BYTEMAN_MODULES_PLUGIN_JAR=%BYTEMAN_HOME%\contrib\jboss-modules-system\byteman-jboss-modules-plugin.jar"