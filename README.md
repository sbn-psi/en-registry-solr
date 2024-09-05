# Legacy Solr Registry

## Background
This repository is a fork from https://github.com/NASA-PDS/registry-legacy-solr, and is used by SBN PSI to serve as the Solr registry utilized by Archive Navigator to host primary and supplemental metadata.

This setup will create and manage a Solr docker container that runs in Cloud mode and has its own internal zookeeper instance (inside the container, not a separate one).

Archive Loader dynamically creates collections inside Solr to host the supplemental metadata for products we care about. ArcNav then reads from both the EN-hosted registry to find the core set of metadata (products by lid, relationships between products), and this registry (web-friendly names/descriptions, relationship priority, etc), stitches them together and builds the interface.

We also have a backup copy of the EN-hosted registry, stored in our own registry, in the event that the main one goes down.

We use this Registry codebase because it includes the schema necessary to store the backup copy and serve it in the exact way that the frontend expects it to be in.

## Deployment
1. Build this repo with `mvn install`
2. Take the release of registry-mgr-legacy/target/registry-mgr-legacy-{version}-bin.tar.gz and move it to a VM with Docker
3. Make a /var/solr/solrdata/ directory that is owned by UID and group 8983 and has permissions 774
4. Unpack the release somewhere, and from the root directory of the installation, run `DATA_HOME=/var/solr bin/registry_legacy_installer_docker.sh install`
5. Add Basic Authentication to the new Solr server and ensure the credentials are available to Archive Loader and ArcNav (see below)

### Adding Basic Authentication

First you will need to create or copy an existing `security.json` file, that looks like this:

```json
{
    "authentication": {
        "class": "solr.BasicAuthPlugin",
        "blockUnknown": "false",
        "credentials": {
            "solr-admin": "redactedencodedpassword redactedencodedsalt",
            "archive-loader": "redactedencodedpassword redactedencodedsalt"
        }
    },
    "authorization": {
        "class": "solr.RuleBasedAuthorizationPlugin",
        "permissions":[
            {"name":"security-edit", "role":"admin"},
            {"name":"security-read", "role":"admin"},
            {"name":"config-edit", "role":"admin"},
            {"name":"config-read", "role":"admin"},
            {"name":"core-admin-edit", "role":"admin"},
            {"name":"core-admin-read", "role":"admin"},
            {"name":"schema-read", "role":["admin", "loader"]},
            {"name":"schema-edit", "role":["admin", "loader"]},
            {"name":"collection-admin-read", "role":["admin", "loader"]},
            {"name":"collection-admin-edit", "role":["admin", "loader"]},
            {"name":"update", "role":["admin", "loader"]}
        ],
        "user-role": {"solr-admin": "admin", "archive-loader": "loader"}
    }
}
```

You will need to either find the old base64 encoded password and salt, or generate new ones with: https://github.com/ansgarwiechers/solrpasswordhash

The other thing you'll need to do is find the zookeeper instance IP and port. Once the docker container is up and running, go to `<container-ip>:8983/solr/admin/info/system?wt=json` and make a note of the `zkHost` entry (example: `172.17.0.3:9983`).

Now that you have everything you need:

1. Upload the `security.json` file into the container with `docker cp`.
2. Go inside the container (`docker exec -it <container> /bin/bash`) and copy the security file into zookeeper: `bin/solr zk cp path/to/security.json zk:security.json -z 172.17.0.3:9983` (replace the IP/port for zookeeper if necessary)

You now have secured access to Solr. The solr-admin account has admin powers, and the archive-loader account will be able to create/modify/delete/fill/update collections but nothing else.
