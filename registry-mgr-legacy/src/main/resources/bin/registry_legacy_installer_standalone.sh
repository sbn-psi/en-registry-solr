#!/bin/sh

# Bourne Shell script that allows easy execution of the Registry Installer
# without the need to set the CLASSPATH or having to type in that long java
# command (java gov.nasa.pds.search.RegistryInstaller ...)

# Expects the Registry jar file to be in the ../lib directory.

# Check if the JAVA_HOME environment variable is set.
if [ -z "${JAVA_HOME}" ]; then
   JAVA_CMD=`which java`
   if [ $? -ne 0 ]; then
     echo "JAVA_HOME is not set as an environment variable"
     exit 1
   fi
else
   JAVA_CMD="${JAVA_HOME}"/bin/java
fi

function print_usage() {
  echo ""
  echo "Usage: $0 COMMAND"
  echo ""
  echo "Commands:"
  echo "  install     Install registry into standalone Solr"
  echo "  uninstall   Uninstall all registry components from standalone Solr"
  echo ""
}

# Check number of command line parameters
if [[ $# -eq 0 ]]; then
  print_usage
  exit 1
fi

if [ "$1" == "install" ]; then
	PARAMS="--install"
elif [ "$1" == "uninstall" ]; then
	PARAMS="--uninstall"
else
	  print_usage
	  exit 1
fi

# Setup environment variables.
SCRIPT_DIR=`cd "$( dirname $0 )" && pwd`
PARENT_DIR=`cd ${SCRIPT_DIR}/.. && pwd`
LIB_DIR=${PARENT_DIR}/dist
EXTRA_LIB_DIR=${PARENT_DIR}/lib

REGISTRY=${PARENT_DIR}

# Create Registry Solr Doc Directory
mkdir -p ${REGISTRY}/../registry-data/solr-docs

# Check for dependencies.
if [ ! -f ${LIB_DIR}/registry*.jar ]; then
    echo "Cannot find Registry jar file in ${LIB_DIR}" 1>&2
    exit 1
fi

# Finds the jar file in LIB_DIR and sets it to REGISTRY_JAR.
REGISTRY_JAR=`ls ${LIB_DIR}/registry-*.jar`
EXTRA_LIB_JAR=`ls ${EXTRA_LIB_DIR}/*.jar`
EXTRA_LIB_JAR=`echo ${EXTRA_LIB_JAR} | sed 'y/ /:/'`
#echo $REGISTRY_JAR
#echo $EXTRA_LIB_JAR
CLASSPATH=$REGISTRY_JAR:$EXTRA_LIB_JAR export CLASSPATH

REGISTRY_INSTALLER_PRESET_FILE=`ls ${SCRIPT_DIR}/registry.properties` export REGISTRY_INSTALLER_PRESET_FILE
REGISTRY_VER=`cat ${PARENT_DIR}/VERSION.txt` export REGISTRY_VER

# Executes Registry Installer via the executable jar file
# Arguments are passed in to the tool via '$@'
"${JAVA_HOME}"/bin/java gov.nasa.pds.search.RegistryInstaller $PARAMS
