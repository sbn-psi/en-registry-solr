#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DEPLOY_DIR=$(cd "${SCRIPT_DIR}/.." && pwd)
REPO_ROOT=$(cd "${DEPLOY_DIR}/../.." && pwd)
ENV_FILE="${DEPLOY_DIR}/.env"

if [ -f "$ENV_FILE" ]; then
  set -a
  # shellcheck disable=SC1090
  source "$ENV_FILE"
  set +a
fi

IMAGE=${REGISTRY_IMAGE:-en-registry-solr:local}
SKIP_MAVEN=${SKIP_MAVEN:-false}

if [ "$SKIP_MAVEN" != "true" ]; then
  (cd "$REPO_ROOT" && mvn -pl registry-mgr-legacy -am package)
fi

dist_tar=$(find "${REPO_ROOT}/registry-mgr-legacy/target" -maxdepth 1 -name 'registry-mgr-legacy-*-bin.tar.gz' -print | sort | tail -1)
if [ -z "$dist_tar" ]; then
  echo "No registry-mgr-legacy binary tarball found in registry-mgr-legacy/target" >&2
  exit 1
fi

rm -rf "${DEPLOY_DIR}/.build"
mkdir -p "${DEPLOY_DIR}/.build"
tar -xzf "$dist_tar" -C "${DEPLOY_DIR}/.build"
extracted=$(find "${DEPLOY_DIR}/.build" -mindepth 1 -maxdepth 1 -type d -name 'registry-mgr-legacy-*' -print | sort | tail -1)
if [ -z "$extracted" ]; then
  echo "Could not find extracted registry distribution under ${DEPLOY_DIR}/.build" >&2
  exit 1
fi
mv "$extracted" "${DEPLOY_DIR}/.build/registry-dist"

docker build -t "$IMAGE" "$DEPLOY_DIR"

echo "Built $IMAGE from $(basename "$dist_tar")"
