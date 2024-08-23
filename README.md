# Legacy Solr Registry

## Background
This repository is a fork from https://github.com/NASA-PDS/registry-legacy-solr, and is used by SBN PSI to serve as the Solr registry utilized by Archive Navigator to host primary and supplemental metadata.

Archive Loader dynamically creates collections inside Solr to host the supplemental metadata for products we care about. ArcNav then reads from both the EN-hosted registry to find the core set of metadata (products by lid, relationships between products), and this registry (web-friendly names/descriptions, relationship priority, etc), stitches them together and builds the interface.

We also have a backup copy of the EN-hosted registry, stored in our own registry, in the event that the main one goes down.

We use this Registry codebase because it includes the schema necessary to store the backup copy and serve it in the exact way that the frontend expects it to be in.

## Deployment
1. Build this repo with `mvn install`
2. Take the release of registry-mgr-legacy/target/registry-mgr-legacy-{version}-bin.tar.gz and move it to a VM with Docker
3. Make a /var/solr/solrdata/ directory that is owned by UID and group 8983 and has permissions 774
4. Unpack the release somewhere, and from the root directory of the installation, run `DATA_HOME=/var/solr bin/registry_legacy_installer_docker.sh install`
5. Add Basic Authentication to the new Solr server and ensure the credentials are available to Archive Loader and ArcNav.