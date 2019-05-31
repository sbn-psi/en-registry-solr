:: Copyright 2019-present, by the California Institute of Technology.
:: ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
:: Any commercial use must be negotiated with the Office of Technology Transfer
:: at the California Institute of Technology.
::
:: This software is subject to U. S. export control laws and regulations
:: (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
:: is subject to U.S. export control laws and regulations, the recipient has
:: the responsibility to obtain export licenses or other export authority as
:: may be required before exporting such information to foreign countries or
:: providing access to foreign nationals.
::
:: $Id$

:: Batch file that allows easy execution of the Registry
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.pds.search.SearchInstaller ...)

:: Expects Registry jar file to be located in the ..\dist directory.

@echo off

:: Check if the JAVA_HOME environment variable is set.
if not defined JAVA_HOME (
    echo The JAVA_HOME environment variable is not set.
    goto END
)

:: Setup environment variables.
set SCRIPT_DIR=%~dps0
set PARENT_DIR=%SCRIPT_DIR%..
set LIB_DIR=%PARENT_DIR%\dist
set EXTRA_LIB_DIR=%PARENT_DIR%\lib

set CLASSPATH=

:: Check for dependencies.
if exist "%LIB_DIR%\registry*.jar" (
   	set CLASSPATH=%LIB_DIR%\registry-2.0.0.jar
) 

::for %%i in ("%EXTRA_LIB_DIR%"\*.jar) do (
::	set CLASSPATH=%CLASSPATH%;%%i
::	echo %CLASSPATH%
::)
set CLASSPATH=%CLASSPATH%;%EXTRA_LIB_DIR%\log4j-1.2.16.jar;%EXTRA_LIB_DIR%\slf4j-api-1.6.6.jar;%EXTRA_LIB_DIR%\slf4j-log4j12-1.6.6.jar;%EXTRA_LIB_DIR%\commons-io-2.5.jar;%EXTRA_LIB_DIR%\commons-logging-1.0.4.jar
echo %CLASSPATH%

if exist "%SCRIPT_DIR%\search.properties" (
	set SEARCH_INSTALLER_PRESET_FILE=%SCRIPT_DIR%\search.properties
	echo %SEARCH_INSTALLER_PRESET_FILE%
)

:: Executes Search Installer via the executable jar file
"%JAVA_HOME%"\bin\java gov.nasa.pds.search.SearchInstaller  %*

:END
