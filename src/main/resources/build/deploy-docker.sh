#!/bin/bash

SOLR_HEAP=2048m

usage() {
    echo "Usage: $0 install/upgrade/uninstall [noPrompt] </path/to/harvest/output>" 1>&2
    echo "       </path/to/harvest/output> - Should match harvest execution output path."
    echo
    exit 1
}

check_exec() {
    # Check if it ran successfully
    if [ $1 -eq 0 ]; then
	echo "SUCCESS"
    else
	echo "FAILED"
	echo "See $LOG"
	echo
	exit 1
    fi
}

check_status() {
    status=$(echo $1 | tr -d '\n' | awk -F"status" '{print $2}' | awk -F, '{print $1}' | awk -F: '{print $2}')
    check_exec $((status))
}

if [[ $# -lt 1 ]]; then
    usage
fi

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

# Solr Default configs for SolrCloud
maxShardsPerNode=2
numShards=1
replicationFactor=1

# Get path to this base dir where script exists
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
LOG=${DIR}/registry-deploy-log-$(date '+%Y%m%d_%H%M%S').txt
DOCK_IMAGE=registry

# Add additional volumes to maintain here. Will also need to update mappings below
VOLUMES="solr_zoo_data solr_pds_shard1_n1 solr_system_shard1_n1 solr_xpath_shard1_n1"

if [ $1 = "install" ]; then
    install=true
    uninstall=false
elif [ $1 = "upgrade" ]; then
    install=false
    uninstall=false
elif [ $1 = "uninstall" ]; then
	install=false
	uninstall=true
else
    usage
fi

noPrompt=false
if [ -n "$2" ]; then
    if [ "$2" = "noPrompt" ]; then
        noPrompt=true
        if [ -n "$3" ]; then
            basepath=$3
        else
            basepath=$(pwd)
        fi
    else 
        basepath=$2
    fi
else 
    basepath=$(pwd)
fi
#echo "basepath = "$basepath
#echo "install = "$install 
#echo "uninstall = "$uninstall
#echo "noPrompt = "$noPrompt

## convert window path name to 'cygwin' (unix) path 
if [ "$TERM" = "cygwin" ]; then
    basepath=${basepath/C://c}
    basepath=${basepath//\\//}
fi
#echo "basepath = "$basepath

# TODO: Add some output logs and ask some questions before proceeding

version=$(cat ${DIR}/../VERSION.txt)
#echo "Version: $version"

if [ "$uninstall"  = true ]; then
    if [ "$noPrompt" = false ]; then
        while true; do
	       echo ""
	       read -p "Are you sure you want to uninstall the Registry and Search and all associated indices? (y/n) " yn
	       case $yn in
            [Yy]* ) break;;
            [Nn]* ) exit 0;;
            * ) echo "Please answer y[es] or n[o].  ";;
	       esac
        done
    fi

    # Remove '.system' collection alias
    echo -ne "Removing the Registry Service Blob collection alias.                 " | tee -a $LOG
    curl "http://localhost:8983/solr/admin/collections?action=DELETEALIAS&name=registry-blob&collections=.system" >>$LOG 2>&1
    check_status "$check"
    echo -ne "Removing the Registry Service XPath collection alias.                " | tee -a $LOG
    curl "http://localhost:8983/solr/admin/collections?action=DELETEALIAS&name=registry-xpath&collections=xpath" >>$LOG 2>&1
    check_status "$check"

    # Remove '.system' collection
    echo "Removing the Registry Blob collection from the SOLR.              " | tee -a $LOG
    docker exec -it --user=solr ${DOCK_IMAGE} solr delete -c .system  >>$LOG 2>&1

    # Remove 'xpath' collection
    echo "Removing the Registry XPath collection.                              " | tee -a $LOG
    docker exec -it --user=solr ${DOCK_IMAGE} solr delete -c xpath  >>$LOG 2>&1

    # Remove 'pds' collection
    echo "Removing the Search collection.                              " | tee -a $LOG
    docker exec -it --user=solr ${DOCK_IMAGE} solr delete -c pds >>$LOG 2>&1

    echo "Stopping the SOLR instance.                                          " | tee -a $LOG
    docker exec -it ${DOCK_IMAGE} solr stop >>$LOG 2>&1

    containerId=`docker ps -a | grep $DOCK_IMAGE | awk '{print $1}'`
    #echo $containerId 
    if [ -n $containerId ]; then
        # Gracefully stop the current container with hashkey
        echo "Stopping Registry Docker Container.                           " | tee -a $LOG
        docker stop $containerId >> $LOG 2>&1
        echo "Removing Registry Docker Container.                   " | tee -a $LOG
        docker rm $containerId >> $LOG 2>&1
        #docker ps -a | grep "$DOCK_IMAGE" | awk '{print $1}' | xargs docker rm >>$LOG 2>&1
    fi

    echo "Removing Registry Docker Images.                              " | tee -a $LOG
    DOCK_VERSION=$DOCK_IMAGE:$version
    #echo $DOCK_VERSION
    docker rmi -f $DOCK_VERSION  >>$LOG 2>&1

    # Remove the volumes
    for vol in $VOLUMES; do
	echo "Removing '"$vol"' volume                   " | tee -a $LOG
	docker volume rm $vol  >>$LOG 2>&1
    done

    # TODO Check out work. Instead of checking throughout the installation. Let's do a final
    # check that all volumes, containers, and images have been removed from docker

    exit 0
fi

# upgrade mode
if [ "$install" = false ]; then
    # 1. Stop currently running solr instance if one is running
    # 2. Check if this version already exists
    # 3. If not, start this new version with "hopefully" the same data
    #       Check the data paths exist already.
    containerId=`docker ps -q` 
    #echo $containerId 
    if [ -n $containerId ]; then
        # Stop the SOLR instance
        echo -ne "Stopping the SOLR instance.                                          " | tee -a $LOG
        docker exec -it ${DOCK_IMAGE} solr stop >>$LOG 2>&1
        check_exec $?

        # Stop and rename old container for upgrade
    	echo -ne "Stopping Registry Docker Container.                           " | tee -a $LOG
        docker stop $containerId
        #check_exec $?
        #status=`check_exec $?`
        #echo $status

        #docker rename $containerId
        #result=`docker ps -a`
        #echo $result
        CURRENT_DATE=$(date '+%Y%m%d_%H%M%S')
        NEW_DOCK_IMG_NAME=${DOCK_IMAGE}_$CURRENT_DATE
        echo -ne "Renaming the container to ${NEW_DOCK_IMG_NAME}.            " | tee -a $LOG
        docker rename ${DOCK_IMAGE} ${NEW_DOCK_IMG_NAME}   
        check_exec $?    
    fi
    #check_exec $?
    #echo $?
fi  

# Build the new Docker image
echo -ne "Building Registry Docker Image.                               " | tee -a $LOG
cd ${DIR}/../build
docker build -t $DOCK_IMAGE:$version -f Dockerfile ../ >>$LOG 2>&1
cd ../../
check_exec $?

# Create volumes
for vol in $VOLUMES; do
    docker volume create $vol >>$LOG 2>&1
done

# TODO: Add some checks to make sure the above executed successfully

# Start up container. TODO: uninstall this up, maybe use docker-compose
echo -ne "Starting Registry Docker Container.                           " | tee -a $LOG
#containerId=`docker ps -a | grep "search-service" | awk '{print $1}'`
#if [ -n $containerId ]; then
#   docker rm $containerId
#fi
docker run --name ${DOCK_IMAGE} -u solr\
    -v $basepath/solr-docs:/data/solr-docs \
    -v solr_zoo_data:/opt/solr/server/solr/zoo_data/ \
    -v solr_pds_shard1_n1:/opt/solr/server/solr/pds_shard1_replica_n1 \
    -v solr_system_shard1_n1:/opt/solr/server/solr/.system_shard1_replica_n1 \
    -v solr_xpath_shard1_n1:/opt/solr/server/solr/xpath_shard1_replica_n1 \
    -d -p 8983:8983 \
    -e SOLR_HEAP=$SOLR_HEAP
    $DOCK_IMAGE:$version >>$LOG 2>&1    
check_exec $?

if [ "$install" = true ]; then
    # Wait 30 seconds for Solr server to start
    sec=60
    echo "Waiting for Solr server to start (${sec} seconds)..." | tee -a $LOG
    sleep $sec

    # Create the Registry collections
    echo -ne "Creating a Registry Service Blob collection (.system)         " | tee -a $LOG
    check=$(curl "http://localhost:8983/solr/admin/collections?action=CREATE&name=.system&maxShardsPerNode=${maxShardsPerNode}&numShards=${numShards}&replicationFactor=${replicationFactor}" 2>>$LOG | tee -a $LOG)
    curl "http://localhost:8983/solr/admin/collections?action=CREATEALIAS&name=registry-blob&collections=.system" >>$LOG 2>&1
    check_status "$check"

    echo -ne "Creating a Registry Service XPath collection (xpath)          " | tee -a $LOG
    check=$(curl "http://localhost:8983/solr/admin/collections?action=CREATE&name=xpath&maxShardsPerNode=${maxShardsPerNode}&numShards=${numShards}&replicationFactor=${replicationFactor}" 2>>$LOG | tee -a $LOG)
    curl "http://localhost:8983/solr/admin/collections?action=CREATEALIAS&name=registry-xpath&collections=xpath" >>$LOG 2>&1
    check_status "$check"

    # Create the Search collection 
    echo -ne "Creating a Search collection (pds)                            " | tee -a $LOG
    #check=$(curl "http://localhost:8983/solr/admin/collections?action=pds&name=xpath&maxShardsPerNode=${maxShardsPerNode}&numShards=${numShards}&replicationFactor=${replicationFactor}" 2>>$LOG | tee -a $LOG)
    #check_status "$check"

    docker exec --user=solr ${DOCK_IMAGE} solr create -c pds -d pds -s ${numShards} -rf ${replicationFactor} >>$LOG 2>&1
    if [ "$noPrompt" = false ]; then
        check_exec $?
    fi
fi

docker exec ${DOCK_IMAGE} solr status >>$LOG 2>&1
check_exec $?

#sleep 5

#exit 0
