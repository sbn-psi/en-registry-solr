FROM solr:9.10.1

USER root

COPY .build/registry-dist/VERSION.txt /opt/sbn-registry/VERSION.txt
COPY .build/registry-dist/collections/registry /opt/solr/server/solr/configsets/registry/conf/
COPY .build/registry-dist/collections/data /opt/solr/server/solr/configsets/data/conf/
COPY .build/registry-dist/dist /opt/solr/contrib/pds/lib/
COPY .build/registry-dist/lib/Saxon* /opt/solr/server/solr-webapp/webapp/WEB-INF/lib/
COPY image/entrypoint.sh /opt/sbn-registry/bin/entrypoint.sh
COPY image/bootstrap-collections.sh /opt/sbn-registry/bin/bootstrap-collections.sh

RUN chmod 755 /opt/sbn-registry/bin/*.sh \
  && chown -R solr:solr /opt/sbn-registry /opt/solr/server/solr/configsets /opt/solr/contrib/pds

USER solr

ENTRYPOINT ["/opt/sbn-registry/bin/entrypoint.sh"]
