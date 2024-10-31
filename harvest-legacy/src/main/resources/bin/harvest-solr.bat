:: Batch file that allows easy execution of the Harvest
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.pds.harvest.HarvestLauncher ...)

:: Expects Harvest jar file to be located in the ../lib directory.

@echo off

:: ############################################################################
:: Update Environment Variables as needed

set SOLR_URL=https://sbnpds4.psi.edu/solr
set SCRIPT_DIR=%~dps0
set PARENT_DIR=%SCRIPT_DIR%..
set LIB_DIR=%PARENT_DIR%\lib
set SEARCH_CONF=%PARENT_DIR%\conf\search\defaults

:: JAVA_HOME=

:: #############################################################################

:: #############################################################################
:: WARNING: Should not need to update below. Proceed with caution.
:: #############################################################################

:: Check if the JAVA_HOME environment variable is set.
if not defined JAVA_HOME (
echo The JAVA_HOME environment variable is not set.
goto END
)

set KEYSTORE=%PARENT_DIR%\keystore\tomcat_self_sign_keystore

:: Check for dependencies.
if exist "%LIB_DIR%\harvest-*.jar" (
set HARVEST_JAR=%LIB_DIR%\harvest-*.jar
) else (
echo Cannot find Harvest jar file in %LIB_DIR%
goto END
)

:: Finds the jar file in LIB_DIR and sets it to HARVEST_JAR.
for %%i in ("%LIB_DIR%"\harvest-*.jar) do set HARVEST_JAR=%%i

:: Executes Harvest via the executable jar file
:: The special variable '%*' allows the arguments
:: to be passed into the executable.
"%JAVA_HOME%"\bin\java -Xms256m -Xmx1024m -Dcom.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize=true -Dpds.search="%SOLR_URL%" -Dpds.harvest.search.conf="%SEARCH_CONF%" -Dresources.home="%PARENT_DIR%\resources" -jar "%HARVEST_JAR%" %*

:END
