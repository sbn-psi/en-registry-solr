#!/usr/bin/env bash
# Exercise the actual Docker command function without a daemon or registry.
set -euo pipefail
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
python3 - "${1:-$ROOT/registry-mgr-legacy/src/main/resources/bin/registry_legacy_installer_docker.sh}" <<'PY'
import json, os, pathlib, re, subprocess, sys, tempfile
source = pathlib.Path(sys.argv[1]).read_text()
function = re.search(r'^start_registry_container\(\) \{\n.*?^\}', source, re.M | re.S)
assert function, 'container-start function missing'
with tempfile.TemporaryDirectory() as directory:
    work = pathlib.Path(directory)
    docker = work / 'docker'
    docker.write_text('#!/usr/bin/env python3\nimport json,os,sys\nopen(os.environ["INSTALLER_TEST_ARGS"],"w").write(json.dumps(sys.argv[1:]))\n')
    docker.chmod(0o755)
    script = work / 'run.sh'
    script.write_text('''#!/usr/bin/env bash
LOG="$INSTALLER_TEST_WORK/log"
DOCKER_IMAGE=registry-legacy
VERSION=5.7.0-SNAPSHOT
DATA_HOME="$INSTALLER_TEST_WORK/data"
DOCKER_VOLUME=solrdata
BACKUP_HOME="$INSTALLER_TEST_WORK/backup"
SOLR_BACKUP_MOUNT=/backups
SOLR_HEAP=2048m
print_status() { return "$1"; }
''' + function.group() + '\nstart_registry_container\n')
    env = dict(os.environ, PATH=directory+os.pathsep+os.environ['PATH'],
               INSTALLER_TEST_WORK=directory, INSTALLER_TEST_ARGS=str(work/'args.json'))
    result = subprocess.run(['bash', str(script)], env=env, capture_output=True, text=True)
    args = json.loads((work/'args.json').read_text())
    assert args[0] == 'run' and args[-1] == 'registry-legacy:5.7.0-SNAPSHOT', \
        'docker run lost its image argument: ' + repr(args)
    assert args.count('--restart') == 1, 'restart policy must appear exactly once'
    assert '-Dsolr.allowPaths=/backups' in ' '.join(args), 'backup allowPaths option missing'
    assert 'command not found' not in result.stderr, result.stderr
    assert result.returncode == 0, result.stderr
print('PASS: installer starts the intended image with one restart policy and backup allowPaths')
PY
