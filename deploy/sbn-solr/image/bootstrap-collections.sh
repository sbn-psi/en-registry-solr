#!/usr/bin/env bash
set -euo pipefail

SOLR_URL=${SOLR_URL:-http://solr:8983/solr}
SOLR_ADMIN_USER=${SOLR_ADMIN_USER:?SOLR_ADMIN_USER is required}
SOLR_ADMIN_PASSWORD=${SOLR_ADMIN_PASSWORD:?SOLR_ADMIN_PASSWORD is required}
SOLR_COLLECTIONS=${SOLR_COLLECTIONS:-registry data}
SOLR_NUM_SHARDS=${SOLR_NUM_SHARDS:-1}
SOLR_REPLICATION_FACTOR=${SOLR_REPLICATION_FACTOR:-1}
ZK_HOST=${ZK_HOST:-zookeeper:2181}

auth=(--user "${SOLR_ADMIN_USER}:${SOLR_ADMIN_PASSWORD}")

wait_for_solr() {
  for _ in $(seq 1 60); do
    if curl --silent --fail "${auth[@]}" "${SOLR_URL}/admin/info/system?wt=json" >/dev/null; then
      return 0
    fi
    sleep 2
  done
  echo "Timed out waiting for secured Solr at ${SOLR_URL}" >&2
  return 1
}

collection_exists() {
  local collection=$1
  curl --silent --fail "${auth[@]}" \
    "${SOLR_URL}/admin/collections?action=LIST&wt=json" \
    | grep -q "\"${collection}\""
}

create_collection() {
  local collection=$1
  echo "Creating collection ${collection}"
  curl --silent --show-error --fail "${auth[@]}" \
    "${SOLR_URL}/admin/collections?action=CREATE&name=${collection}&collection.configName=${collection}&numShards=${SOLR_NUM_SHARDS}&replicationFactor=${SOLR_REPLICATION_FACTOR}&wt=json" \
    >/dev/null
}

upload_configset() {
  local configset=$1
  local config_dir="/opt/solr/server/solr/configsets/${configset}/conf"
  if [ ! -d "$config_dir" ]; then
    echo "Missing configset directory: ${config_dir}" >&2
    return 1
  fi

  echo "Uploading configset ${configset} to ZooKeeper"
  solr zk upconfig -n "$configset" -d "$config_dir" -z "$ZK_HOST"
}

wait_for_solr

for collection in ${SOLR_COLLECTIONS}; do
  upload_configset "$collection"
  if collection_exists "$collection"; then
    echo "Collection ${collection} already exists"
  else
    create_collection "$collection"
  fi
done

curl --silent --show-error --fail "${auth[@]}" \
  "${SOLR_URL}/admin/collections?action=LIST&wt=json"
