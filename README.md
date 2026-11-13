# Legacy Solr Registry

## Background
This repository is a fork from https://github.com/NASA-PDS/registry-legacy-solr, and is used by SBN PSI to serve as the Solr registry utilized by Archive Navigator to host primary and supplemental metadata.

The SBN image build creates a secret-free SolrCloud image. Archive Loader owns the Docker Compose deployment, ZooKeeper pairing, runtime credentials, and `security.json` upload.

Archive Loader dynamically creates collections inside Solr to host the supplemental metadata for products we care about. ArcNav then reads from both the EN-hosted registry to find the core set of metadata (products by lid, relationships between products), and this registry (web-friendly names/descriptions, relationship priority, etc), stitches them together and builds the interface.

We also have a backup copy of the EN-hosted registry, stored in our own registry, in the event that the main one goes down.

We use this Registry codebase because it includes the schema necessary to store the backup copy and serve it in the exact way that the frontend expects it to be in.

## Build
This upstream line now requires OpenJDK 17 or higher.

Build this repo with:

```sh
mvn install
```

Schema changes require redeploying and reindexing Solr before clients can rely on the new fields.

## SBN Docker Image
Use the SBN image overlay in `deploy/sbn-solr`.

On a build machine with Maven and Docker:

```sh
deploy/sbn-solr/scripts/build-image.sh
```

This builds the Maven registry manager package and then builds a secret-free Docker image containing the Solr configsets, schemas, XSLT, and registry jars.

The build script automatically loads `deploy/sbn-solr/.env` and uses only these environment variables:

- `REGISTRY_IMAGE`: optional Docker image tag. Defaults to `en-registry-solr:local`.
- `SKIP_MAVEN=true`: optional shortcut when `registry-mgr-legacy/target` already contains the binary tarball.

Deploy this image from the Archive Loader repository. Archive Loader provides the Compose services, generated runtime credentials, Solr `security.json`, collection bootstrap, health checks, and bind-mount-to-volume migration helper.

The old packaged `registry_legacy_installer_docker.sh` remains in the upstream-style release artifact, but SBN deployments should prefer the Archive Loader deployment path because it handles named volumes, security generation, ZooKeeper upload, configset upload, collection bootstrap, and health checks.

## Upstream tools

[Running the tools](docs/running-the-tools.md) and [upgrading a release](docs/upgrading-a-release.md) describe the upstream standalone installer. SBN SolrCloud deployments continue to use Archive Loader; do not run the standalone upgrade procedure against that deployment.
