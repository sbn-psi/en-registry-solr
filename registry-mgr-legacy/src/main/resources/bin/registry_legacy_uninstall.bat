:: Batch file that allows easy execution of the Registry
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.pds.search.RegistryInstaller ...)

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


set CP=%EXTRA_LIB_DIR%\*;%LIB_DIR%\*

set /p REGISTRY_VER=<%PARENT_DIR%/VERSION.txt

if exist "%SCRIPT_DIR%\registry.properties" (
    set REGISTRY_INSTALLER_PRESET_FILE=%SCRIPT_DIR%\registry.properties
)

:: Executes Registry Installer via the executable jar file
"%JAVA_HOME%"\bin\java -cp "%CP%" gov.nasa.pds.search.RegistryInstaller -u

:END
