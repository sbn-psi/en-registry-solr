#!/usr/bin/env bash

COLLECTION="pds"
PROPS=("-Dauto=yes")
RECURSIVE=""
FILES=()

SCRIPT_DIR=$(cd "$( dirname $0 )" && pwd)
PARENT_DIR=$(cd ${SCRIPT_DIR}/.. && pwd)
LIB_DIR=${PARENT_DIR}/lib
TOOL_JAR=(${LIB_DIR}/solr-core-*.jar)

function print_usage() {
  echo ""
  echo "Usage: $0 [OPTIONS] <file|directory>"
  echo ""
  echo "Options:"
  echo "  -host <host> (default: localhost)"
  echo "  -port <port> (default: 8983)"
  echo ""
}

# Check number of command line parameters
if [[ $# -eq 0 ]]; then
  print_usage
  exit
fi

# Parse command line parameters
while [ $# -gt 0 ]; do
  if [[ -d "$1" ]]; then
    # Directory
    RECURSIVE=yes
    FILES+=("$1")
  elif [[ -f "$1" ]]; then
    # File
    FILES+=("$1")
  else
    if [[ "$1" == -* ]]; then
      if [[ "$1" == "-port" ]]; then
        shift
        PROPS+=("-Dport=$1")
      elif [[ "$1" == "-host" ]]; then
        shift
        PROPS+=("-Dhost=$1")
      else
        echo -e "\nUnrecognized argument: $1\n"
        exit 1
      fi
    else
      echo -e "\nUnrecognized argument: $1\n"
      exit 1
    fi
  fi
  shift
done

# Setup Java
if [ -n "$JAVA_HOME" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  JAVA=java
fi

# Test that Java exists and is executable
"$JAVA" -version >/dev/null 2>&1 || { echo >&2 "Java is required to run this tool! Please install Java 8 or greater before running this script."; exit 1; }

# Parameters for Solr post tool
PROPS+=("-Dc=$COLLECTION" "-Ddata=files")
if [[ -n "$RECURSIVE" ]]; then
  PROPS+=("-Drecursive=yes")
fi

# Call Solr post tool
echo "$JAVA" -classpath "${TOOL_JAR[0]}" "${PROPS[@]}" org.apache.solr.util.SimplePostTool "${FILES[@]}"
"$JAVA" -classpath "${TOOL_JAR[0]}" "${PROPS[@]}" org.apache.solr.util.SimplePostTool "${FILES[@]}"
