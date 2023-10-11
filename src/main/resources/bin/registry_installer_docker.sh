#!/usr/bin/env bash

# Copyright 2019, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
# this list of conditions and the following disclaimer.
# * Redistributions must reproduce the above copyright notice, this list of
# conditions and the following disclaimer in the documentation and/or other
# materials provided with the distribution.
# * Neither the name of Caltech nor its operating division, the Jet Propulsion
# Laboratory, nor the names of its contributors may be used to endorse or
# promote products derived from this software without specific prior written
# permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

SOLR_HEAP=2048m

maxShardsPerNode=1
numShards=1
replicationFactor=1

DOCKER_IMAGE=registry-legacy
DOCKER_VOLUME="solrdata"

PROMPT=true
COMMAND=""

SCRIPT_DIR=$(cd "$( dirname $0 )" && pwd)
PARENT_DIR=$(cd ${SCRIPT_DIR}/.. && pwd)
LOG=${PARENT_DIR}/registry_installer_docker-log-$(date '+%Y%m%d_%H%M%S').txt

VERSION_FILE=${PARENT_DIR}/VERSION.txt
VERSION="latest"


function print_usage() {
  echo ""
  echo "Usage: $0 [OPTIONS] COMMAND"
  echo ""
  echo "Options:"
  echo "  -n   No prompt"
  echo ""
  echo "Commands:"
  echo "  install     Install registry in docker"
  echo "  uninstall   Uninstall all registry components from docker"
  echo ""
}

# Check number of command line parameters
if [[ $# -eq 0 ]]; then
  print_usage
  exit
fi

# Parse command line parameters
while [ $# -gt 0 ]; do
  if [[ "$1" == "-n" ]]; then
    PROMPT=false
  elif [[ ("$1" == "install" || "$1" == "uninstall") ]]; then
    COMMAND="$1"
  else
    echo -e "\nUnrecognized argument: $1\n"
    exit 1
  fi
  shift
done

# Check DATA_HOME is set
if [ -z "$DATA_HOME" ]; then
  echo "ERROR: DATA_HOME environment variable must be set for maintaining Solr index."
  exit 1
fi

# Version
if [ -f "$VERSION_FILE" ]; then
  VERSION=$(head -n 1 "$VERSION_FILE")
  if [[ $VERSION != '${project.version}' ]]; then
    echo "WARNING: Invalid version '${project.version}'. Will use default version 'latest'"
    VERSION="latest"
  fi
else
  echo "WARNING: $VERSION_FILE doesn't exist. Will use default version 'latest'"
fi

print_status() {
  if [ $1 -eq 0 ]; then
    echo "SUCCESS"
  else
    echo "FAILED"
    echo "See $LOG"
    echo
    exit 1
  fi
}

print_solr_status() {
    status=$(echo $1 | tr -d '\n' | awk -F"status" '{print $2}' | awk -F, '{print $1}' | awk -F: '{print $2}')
    print_status $((status))
}

build_docker_image() {
    echo -ne "Building Registry Docker Image.                               " | tee -a $LOG
    cd ${PARENT_DIR}/build
    docker build -t $DOCKER_IMAGE:$VERSION -f Dockerfile ../ | tee -a $LOG 2>&1
    print_status $?
}

create_docker_volumes() {
    echo "Creating docker volumes and associated data directory        " | tee -a $LOG
    mkdir -p $DATA_HOME/$DOCKER_VOLUME/data
    docker volume create $DOCKER_VOLUME >>$LOG 2>&1
}

remove_registry_container() {
    containerId=$(docker ps -a | grep "registry" | awk '{print $1}')
    if [ ! -z "$containerId" ]; then
        docker stop $containerId >> $LOG 2>&1
        docker rm $containerId >> $LOG 2>&1
    fi
}

wait_for_solr() {
    attempt_counter=0
    max_attempts=12

    echo "Waiting for Solr server to start." | tee -a $LOG

    until $(curl --output /dev/null --max-time 5 --silent --head --fail http://localhost:8983/solr); do
        if [ ${attempt_counter} -eq ${max_attempts} ];then
            echo "Could not start Solr."
            exit 1
        fi
        attempt_counter=$(($attempt_counter+1))
        sleep 5
    done
}

start_registry_container() {
    echo -ne "Starting Registry Docker Container                            " | tee -a $LOG
    docker run --name ${DOCKER_IMAGE} \
      -u solr \
      -d -p 8983:8983 \
      -v ${DATA_HOME}/${DOCKER_VOLUME}:/var/solr/ \
      -e SOLR_HEAP=$SOLR_HEAP \
      $DOCKER_IMAGE:$VERSION | tee -a $LOG 2>&1

    print_status $?
}

create_solr_collections() {
    # Create the Registry collections
    echo -ne "Creating Registry collection (registry)                       " | tee -a $LOG
    docker exec --user=solr ${DOCKER_IMAGE} solr create -c registry -d registry -s ${numShards} -rf ${replicationFactor} >>$LOG 2>&1
    print_status $?

    # Create the Search collection 
    echo -ne "Creating Search collection (data)                             " | tee -a $LOG
    docker exec --user=solr ${DOCKER_IMAGE} solr create -c data -d data -s ${numShards} -rf ${replicationFactor} >>$LOG 2>&1
    print_status $?
}

confirm_uninstall() {
    if [ "$PROMPT" = true ]; then
        while true; do
            echo ""
            read -p "Are you sure you want to uninstall the Registry and Search and all associated indices? (y/n) " yn
            case $yn in
                [Yy]* ) break;;
                [Nn]* ) exit 0;;
                * ) echo "Please answer y[es] or n[o].";;
            esac
        done
    fi
}

remove_solr_collections() {
    # Remove 'registry' collection
    echo "Removing the Registry collection.                                 " | tee -a $LOG
    docker exec -it --user=solr ${DOCKER_IMAGE} solr delete -c registry >>$LOG 2>&1

    # Remove 'data' collection
    echo "Removing the Search collection.                                   " | tee -a $LOG
    docker exec -it --user=solr ${DOCKER_IMAGE} solr delete -c data >>$LOG 2>&1
}

stop_solr() {
    echo "Stopping the SOLR instance.                                       " | tee -a $LOG
    docker exec -it ${DOCKER_IMAGE} solr stop >>$LOG 2>&1
}

remove_registry_image() {
    echo "Removing Registry Docker Images.                                  " | tee -a $LOG
    docker rmi -f "$DOCKER_IMAGE:$VERSION" >>$LOG 2>&1
}

remove_docker_volumes() {
	echo "Removing '"$DOCKER_VOLUME"' volume                   " | tee -a $LOG
	docker volume rm $DOCKER_VOLUME  >>$LOG 2>&1
}


# Execute commands
if [[ $COMMAND == "install" ]]; then
  echo "Installing..."
  build_docker_image
  create_docker_volumes
  start_registry_container
  wait_for_solr
  create_solr_collections
  # Print solr status
  docker exec ${DOCKER_IMAGE} solr status | tee -a $LOG 2>&1

elif [[ $COMMAND == "uninstall" ]]; then
  echo "Uninstalling..."
  confirm_uninstall
  remove_solr_collections
  stop_solr
  remove_registry_container
  remove_registry_image
  remove_docker_volumes
fi
