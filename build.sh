#!/bin/sh
# build.sh -- Build Script for the "Hello, World" Application
# $Id: build.sh,v 1.1.1.1 2003/11/21 16:16:34 rsegelman Exp $

# Identify the custom class path components we need
CP=$TOMCAT_HOME/lib/ant.jar:$TOMCAT_HOME/lib/servlet.jar
CP=$CP:$TOMCAT_HOME/lib/jaxp.jar:$TOMCAT_HOME/lib/parser.jar
CP=$CP:$JAVA_HOME/lib/tools.jar

# Execute ANT to perform the requested build target
java -classpath $CP:$CLASSPATH org.apache.tools.ant.Main \
  -Dtomcat.home=$TOMCAT_HOME "$@"
