#!/usr/bin/env bash
# Exercise the SBN build/launcher in an isolated fixture with stub executables.
set -euo pipefail
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
mkdir -p "$WORK/repo/deploy/sbn-solr/scripts" "$WORK/repo/registry-mgr-legacy/target" "$WORK/bin" "$WORK/dist/registry-mgr-legacy-test/collections/data"
cp "$ROOT/deploy/sbn-solr/scripts/build-image.sh" "$WORK/repo/deploy/sbn-solr/scripts/"
echo config > "$WORK/dist/registry-mgr-legacy-test/collections/data/managed-schema.xml"
tar -czf "$WORK/repo/registry-mgr-legacy/target/registry-mgr-legacy-test-bin.tar.gz" -C "$WORK/dist" registry-mgr-legacy-test
export CALLS="$WORK/calls"
cat > "$WORK/bin/docker" <<'STUB'
#!/usr/bin/env bash
printf '%s\n' "$*" >> "$CALLS"
[[ -f "${!#}/.build/registry-dist/collections/data/managed-schema.xml" ]]
STUB
cat > "$WORK/bin/mvn" <<'STUB'
#!/usr/bin/env bash
printf 'mvn %s\n' "$*" >> "$CALLS"
STUB
chmod +x "$WORK/bin/"*
export PATH="$WORK/bin:$PATH"
export SKIP_MAVEN=true REGISTRY_IMAGE=en-registry-solr:test
bash "$WORK/repo/deploy/sbn-solr/scripts/build-image.sh"
grep -q '^build -t en-registry-solr:test ' "$CALLS"
! grep -q '^mvn ' "$CALLS"
echo 'PASS: prebuilt distribution is staged and local Docker build receives the intended context'
SKIP_MAVEN=false bash "$WORK/repo/deploy/sbn-solr/scripts/build-image.sh"
grep -q '^mvn -pl registry-mgr-legacy -am package$' "$CALLS"
echo 'PASS: normal build packages the registry manager before image construction'
rm "$WORK/repo/registry-mgr-legacy/target/registry-mgr-legacy-test-bin.tar.gz"
if bash "$WORK/repo/deploy/sbn-solr/scripts/build-image.sh" > "$WORK/error" 2>&1; then
  echo 'FAIL: missing distribution was accepted'; exit 1
fi
grep -q 'No registry-mgr-legacy binary tarball' "$WORK/error"
echo 'PASS: missing distribution fails before image construction'
mkdir -p "$WORK/harvest/bin" "$WORK/harvest/lib" "$WORK/java/bin"
cp "$ROOT/harvest-legacy/src/main/resources/bin/harvest-solr" "$WORK/harvest/bin/"
touch "$WORK/harvest/lib/harvest-test.jar"
cat > "$WORK/java/bin/java" <<'STUB'
#!/usr/bin/env bash
printf '%s\n' "$*" > "$CALLS"
STUB
chmod +x "$WORK/java/bin/java"
# No PDS4_SOLR_DOC_HOME, mail, marker, or registry-mgr process is required by SBN.
unset PDS4_SOLR_DOC_HOME
JAVA_HOME="$WORK/java" PDS_REGISTRY_API=http://127.0.0.1:9999/api bash "$WORK/harvest/bin/harvest-solr" --no-context-products --help
grep -q -- '-Xmx2048m' "$CALLS"
grep -q -- '-Dpds.registry.api=http://127.0.0.1:9999/api' "$CALLS"
grep -q -- '--no-context-products --help' "$CALLS"
echo 'PASS: SBN launcher preserves bounded memory, API override and doc-only execution'
