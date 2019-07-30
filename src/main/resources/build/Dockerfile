FROM solr:7.7.2
MAINTAINER Jordan Padams <jordan.padams@jpl.nasa.gov>
ADD VERSION.txt .
COPY conf/pds /opt/solr/server/solr/configsets/pds/conf
#RUN mkdir /opt/solr/lib /opt/solr/server/solr/zoo_data/ /opt/solr/server/solr/pds_shard1_replica_n1 /opt/solr/server/solr/.system_shard1_replica_n1 /opt/solr/server/solr/xpath_shard1_replica_n1
COPY dist /opt/solr/contrib/pds/lib
COPY lib /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
ENTRYPOINT ["/bin/bash", "-c", "solr start -c -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl -Dsolr.pds.home=/opt/solr/server/solr/ && tail -f /dev/null"]
