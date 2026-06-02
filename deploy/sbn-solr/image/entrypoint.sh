#!/usr/bin/env bash
set -euo pipefail

ZK_HOST=${ZK_HOST:-zookeeper:2181}
SOLR_HEAP=${SOLR_HEAP:-2048m}

mkdir -p /var/solr/data
if [ ! -f /var/solr/log4j2.xml ]; then
  cp /opt/solr/server/resources/log4j2.xml /var/solr/log4j2.xml
fi
if [ ! -f /var/solr/data/solr.xml ]; then
  cp /opt/solr/server/solr/solr.xml /var/solr/data/solr.xml
fi

exec solr start -f -c -z "$ZK_HOST" \
  -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl \
  -Dsolr.pds.home=/opt/solr/server/solr/ \
  -Dsolr.modules=scripting \
  -Dsolr.config.lib.enabled=true
