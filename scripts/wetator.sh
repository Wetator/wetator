#!/bin/bash

# ---------------------------------------------------------
# Simple Wetator start script
#
#  requires JAVA 1.8 or higher installed
# ---------------------------------------------------------

# ---------------------------------------------------------
# additional java opts
# ---------------------------------------------------------
# WETATOR_JAVA_OPT=$WETATOR_JAVA_OPT "-Xmx512m"


# ---------------------------------------------------------
# additional jar files to be added to the classpath
#
# you can simply add jar files by puting them into the
# lib folder
# if you like to reference other jar files you have to add
# them here (separated by ;)
# ---------------------------------------------------------
# WETATOR_ADDITIONAL_LIBS=c:\orcale\jdbc\ojdbc8.jar


WETATOR_HOME=.
WETATOR_LIB=$WETATOR_HOME/lib
WETATOR_MAIN_CLASS=org.wetator.Wetator
JAVA_EXE=java

# start command for Wetator
CMD="$JAVA_EXE $WETATOR_JAVA_OPT -cp \".:$WETATOR_LIB/*:$WETATOR_ADDITIONAL_LIBS\" $WETATOR_MAIN_CLASS $*"

echo "Starting Wetator"

echo $CMD
eval $CMD
