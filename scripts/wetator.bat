@echo off

REM ---------------------------------------------------------
REM Simple Wetator start script
REM
REM  requires JAVA 1.8 or higher installed
REM ---------------------------------------------------------

SETLOCAL

REM ---------------------------------------------------------
REM overwrite your system JAVA_HOME if you like
REM ---------------------------------------------------------
REM SET JAVA_HOME=

REM ---------------------------------------------------------
REM additional java opts
REM ---------------------------------------------------------
rem SET WETATOR_JAVA_OPT=%WETATOR_JAVA_OPT% "-Xmx512m"


REM ---------------------------------------------------------
REM additional jar files to be added to the classpath
REM
REM you can simply add jar files by puting them into the
REM lib folder
REM if you like to reference other jar files you have to add
REM them here (separated by ;)
REM ---------------------------------------------------------
rem SET WETATOR_ADDITIONAL_LIBS=c:\orcale\jdbc\ojdbc6.jar


set WETATOR_HOME=.
set WETATOR_LIB=%WETATOR_HOME%\lib
set WETATOR_MAIN_CLASS=org.wetator.Wetator
set JAVA_EXE=java.exe

IF "%JAVA_HOME%" == "" GOTO MSG_NOJAVAHOME
IF NOT EXIST %JAVA_HOME%\bin\%JAVA_EXE% GOTO MSG_NOJAVA 2>nul

rem start command for Wetator
set CMD="%JAVA_HOME%\bin\%JAVA_EXE%" %WETATOR_JAVA_OPT% -cp ".;%WETATOR_LIB%\*;%WETATOR_ADDITIONAL_LIBS%" %WETATOR_MAIN_CLASS% %*

echo Starting Wetator

ECHO %CMD%
%CMD%


goto end

:MSG_NOJAVAHOME

ECHO ***********************************************************************
ECHO *                                                                     *
ECHO * ERROR: JAVA_HOME not found in your environment.                     *
ECHO *                                                                     *
ECHO * Please, set the JAVA_HOME variable in your environment to match the *
ECHO * location of the Java Virtual Machine you want to use.               *
ECHO *                                                                     *
ECHO ***********************************************************************
ECHO
PAUSE
GOTO END

:MSG_NOJAVA
ECHO ***********************************************************************
ECHO *                                                                     *
ECHO * Your JAVA_HOME/JAVA_EXE are invalid                                 *
ECHO * File not found:                                                     *
ECHO *   '%JAVA_HOME%\bin\%JAVA_EXE%'
ECHO *                                                                     *
ECHO ***********************************************************************
ECHO
PAUSE

:END
ENDLOCAL
