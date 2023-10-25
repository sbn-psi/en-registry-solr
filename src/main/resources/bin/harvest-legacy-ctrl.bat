:: Batch file that allows easy execution of the Crawler Daemon Controller
:: without the need to set the CLASSPATH or having to type in that long java
:: command (java gov.nasa.jpl.oodt.cas.crawl.daemon.CrawlDaemonController ...)

@echo off

:: Check if the JAVA_HOME environment variable is set.
if not defined JAVA_HOME (
echo The JAVA_HOME environment variable is not set.
goto END
)

:: Setup environment variables.
set SCRIPT_DIR=%~dps0
set PARENT_DIR=%SCRIPT_DIR%..
set LIB_DIR=%PARENT_DIR%\lib

set SEARCH_CONF=%PARENT_DIR%\conf\search\defaults

:: Execute the application.
"%JAVA_HOME%"\bin\java -Xms256m -Xmx1024m -Dcom.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize=true -Dpds.search="http://localhost:8983/solr" -Dpds.harvest.search.conf="%SEARCH_CONF%" -Dresources.home="%PARENT_DIR%\resources" -Djava.ext.dirs="%LIB_DIR%" gov.nasa.jpl.oodt.cas.crawl.daemon.CrawlDaemonController %*

:END