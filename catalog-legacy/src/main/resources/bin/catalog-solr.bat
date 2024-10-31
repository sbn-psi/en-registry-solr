:: Batch file that allows easy execution of the Catalog Tool
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.pds.citool.CITool ...)

@echo off

:: Expects the Catalog jar file to be located in the ../lib directory.

:: Check if the JAVA_HOME environment variable is set.
if not defined JAVA_HOME (
echo The JAVA_HOME environment variable is not set.
goto END
)

:: Setup environment variables.
set SCRIPT_DIR=%~dp0
set PARENT_DIR=%SCRIPT_DIR%..
set LIB_DIR=%PARENT_DIR%\lib

:: Check for dependencies.
if exist "%LIB_DIR%\catalog-*.jar" (
set CATALOG_JAR=%LIB_DIR%\catalog-*.jar
) else (
echo Cannot find Catalog jar file in %LIB_DIR%
goto END
)

:: Finds the jar file in LIB_DIR and sets it to CATALOG_JAR
for %%i in ("%LIB_DIR%"\catalog-*.jar) do set CATALOG_JAR=%%i

:: Executes Catalog via the executable jar file
:: The special variable '%*' allows the arguments
:: to be passed into the executable.
"%JAVA_HOME%"\bin\java -Dpds.registry="https://sbnpds4.psi.edu/solr" -jar "%CATALOG_JAR%" %*

:END
