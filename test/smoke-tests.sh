#!/bin/bash

check_status() {
    status=$1
    msg=$2
    if [ $status -ne 0 ]; then
        echo "ERROR: $msg"
        exit $status
    fi
}

SCRIPTDIR=$(cd "$( dirname $0 )" && pwd)
PARENTDIR=$(cd ${SCRIPTDIR}/.. && pwd)

# Check REGISTRY_MGR_LEGACY_HOME is set
if [ -z "$LEGACY_REGISTRY_HOME" ] || [ -z "$LEGACY_HARVEST_HOME" ]; then
  echo "ERROR: LEGACY_REGISTRY_HOME and LEGACY_HARVEST_HOME environment variables must be set for executing tools."
  exit 1
fi

if [ -z "$DATA_HOME" ]; then
  echo "ERROR: DATA_HOME environment variable must be set for maintaining solr docs."
  exit 1
fi

mkdir -p $DATA_HOME/solr-docs

# Install Solr with preconfigured collections
$LEGACY_REGISTRY_HOME/bin/registry_legacy_installer_docker.sh install
check_status $? "[ERROR] Registry Manager Install Failure"
echo "[SUCCESS] Harvest Successful"

echo "+++ TEST 1 +++"
TEST_DATA_HOME=$PARENTDIR/src/test/resources/data/test1/a17leamcal_custom
TEST_DATA_HOME=${TEST_DATA_HOME//\//\\\/}

# Create config file based upon current environment
sed "s/{TEST_DATA_HOME}/$TEST_DATA_HOME/g" $PARENTDIR/src/test/resources/conf/harvest-policy-master.xml > test-policy.xml
echo "[INFO] Created new config file with TEST_DATA_HOME=$TEST_DATA_HOME"

echo "[INFO] Harvest test data"
$LEGACY_HARVEST_HOME/bin/harvest-solr -c $(pwd)/test-policy.xml \
                                      -C $LEGACY_HARVEST_HOME/conf/search/defaults \
                                      -o $DATA_HOME/solr-docs --verbose 0

check_status $? "[ERROR] Harvest Failure"
echo "[SUCCESS] Harvest Successful"

echo "[INFO] Check solr docs match expected"
SOLR_EXPECTED=$PARENTDIR/src/test/resources/data/test1/expected/solr_doc_expected.xml
# Cleanse output file of elements that are specific to where the code is run
egrep -v package_id $DATA_HOME/solr-docs/solr-docs/solr_doc_0.xml | egrep -v file_ref_location | egrep -v file_ref_url > solr_doc_actual.xml

# Diff
test=$(diff solr_doc_actual.xml $SOLR_EXPECTED)
if [ -z "$test" ]; then
    echo "[SUCCESS] Solr Doc Diff Successful"
else
    echo "[ERROR] Solr Doc Diff Test Failed"
    echo $test
    exit 1
fi


# Load test data
echo "[INFO] Registry Manager Load"
$LEGACY_REGISTRY_HOME/bin/registry-mgr-solr $DATA_HOME/solr-docs

check_status $? "[ERROR] Registry Manager Load Failure"

echo "[SUCCESS] Registry Manager Load Successful"

exit 0
