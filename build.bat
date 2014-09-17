if "%DEBUG%"=="" echo off
rem build.bat -- Build Script for the "RETS Reference Implementation" Application
rem $Id: build.bat,v 1.1.1.1 2003/11/21 16:16:34 rsegelman Exp $

set _CP=%CP%

rem Identify the custom class path components we need
REM set CP=%TOMCAT_HOME%\lib\ant.jar;%TOMCAT_HOME%\lib\servlet.jar
REM set CP=%CP%;%TOMCAT_HOME%\lib\jaxp.jar;%TOMCAT_HOME%\lib\parser.jar
REM set CP=%CP%;%JAVA_HOME%\lib\tools.jar

set CP=%ANT_HOME%\lib\ant.jar;%ANT_HOME%\lib\jaxp.jar;%ANT_HOME%\lib\crimson.jar
set CP=%CP%;%JAVA_HOME%\lib\tools.jar

rem Execute ANT to perform the requird build target
java -classpath "%CP%;%CLASSPATH%" org.apache.tools.ant.Main -Dtomcat.home=%TOMCAT_HOME% %1 %2 %3 %4 %5 %6 %7 %8 %9

set CP=%_CP%
set _CP=
