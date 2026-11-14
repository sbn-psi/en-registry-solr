'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const comment = require('../.github/scripts/container-security-comment.cjs');
const {MARKER, STEPS} = comment;
const REPO = 'sbn-psi/en-registry-solr';

function fixture({result = 'findings', existing = [], prs = [{number: 4}]} = {}) {
  const calls = [];
  const run = {id: 100, run_attempt: 1, workflow_id: 8, name: 'Container security',
    path: '.github/workflows/container-security.yml', event: 'pull_request', status: 'completed',
    conclusion: result === 'clean' ? 'success' : 'failure', head_sha: 'a'.repeat(40),
    head_branch: 'fix-security', head_repository: {full_name: 'contributor/solr'}, pull_requests: prs};
  const pr = {state: 'open', base: {repo: {full_name: REPO}}, head: {sha: run.head_sha}};
  const steps = Object.entries(STEPS).map(([key, name]) => ({name,
    conclusion: key === 'gate' && result !== 'clean' ? 'failure' : 'success'}));
  if (result === 'error') steps.find(s => s.name === STEPS.scan).conclusion = 'failure';
  const state = {run, pr, jobs: [{name: 'trivy', conclusion: run.conclusion, steps}],
    runs: [run], comments: existing, associated: [{number: 4}], reads: 0, beforeRead: () => {}};
  const capture = kind => async args => {calls.push({kind, ...args}); return {data: {}};};
  const github = {rest: {
    actions: {getWorkflowRun: async () => ({data: state.run}),
      listWorkflowRuns: 'runs', listJobsForWorkflowRun: 'jobs'},
    repos: {listPullRequestsAssociatedWithCommit: 'associated'},
    pulls: {get: async () => {state.beforeRead(++state.reads); return {data: state.pr};}},
    issues: {listComments: 'comments', createComment: capture('create'), updateComment: capture('update')},
  }, paginate: async (method, args) => {
    calls.push({kind: 'read', method, ...args}); return state[method];
  }};
  const context = {repo: {owner: 'sbn-psi', repo: 'en-registry-solr'}, payload: {workflow_run: structuredClone(run)}};
  const execute = () => comment({github, context, core: {info: () => {}}});
  return {state, context, github, execute, writes: () => calls.filter(c => c.kind !== 'read'), calls};
}
const bot = {id: 77, user: {login: 'github-actions[bot]', type: 'Bot'}, body: MARKER + '\nold'};

test('failure creates scoped guidance from metadata only, without downloading artifacts', async () => {
  const f = fixture(); await f.execute();
  assert.equal(f.writes().length, 1);
  const write = f.writes()[0];
  assert.equal(write.kind, 'create'); assert.equal(write.issue_number, 4);
  assert.match(write.body, /Security gate failed/); assert.match(write.body, /full.json/);
  assert.match(write.body, /statement/); assert.match(write.body, /expired_at/);
  assert.equal(write.owner + '/' + write.repo, REPO);
  assert.ok(f.calls.filter(c => c.kind === 'read').every(c => ['runs','jobs','comments'].includes(c.method)));
});
test('rerun updates the existing bot-owned marker comment', async () => {
  const f = fixture({existing: [bot]}); await f.execute();
  assert.equal(f.writes()[0].kind, 'update'); assert.equal(f.writes()[0].comment_id, 77);
});
test('success updates existing guidance but never creates clean-run spam', async () => {
  for (const existing of [[], [bot]]) {
    const f = fixture({result: 'clean', existing}); await f.execute();
    assert.equal(f.writes().length, existing.length);
    if (existing.length) assert.match(f.writes()[0].body, /gate passed/);
  }
});
test('human marker and another bot cannot be overwritten', async () => {
  const f = fixture({existing: [{...bot, user: {login: 'mdrum', type: 'User'}},
    {...bot, user: {login: 'other[bot]', type: 'Bot'}}]});
  await f.execute(); assert.equal(f.writes()[0].kind, 'create');
});
test('stale PR head, closed PR and upstream base cause no writes', async () => {
  for (const change of [pr => {pr.head.sha = 'b'.repeat(40);}, pr => {pr.state = 'closed';},
    pr => {pr.base.repo.full_name = 'NASA-PDS/registry-legacy-solr';}]) {
    const f = fixture(); change(f.state.pr); await f.execute(); assert.equal(f.writes().length, 0);
  }
});
test('head change while preparing a comment causes no writes', async () => {
  const f = fixture(); f.state.beforeRead = n => {if (n === 2) f.state.pr.head.sha = 'b'.repeat(40);};
  await f.execute(); assert.equal(f.writes().length, 0);
});
test('newer run, newer rerun attempt and cancelled run do not overwrite feedback', async () => {
  for (const change of [f => {f.state.runs.push({...f.state.run, id: 101, status: 'in_progress'});},
    f => {f.state.run.run_attempt = 2;}, f => {f.state.run.conclusion = 'cancelled';}]) {
    const f = fixture(); change(f); await f.execute(); assert.equal(f.writes().length, 0);
  }
});
test('fork and Dependabot PR with empty run.pull_requests uses commit association', async () => {
  const f = fixture({prs: []}); await f.execute();
  assert.ok(f.calls.some(c => c.method === 'associated')); assert.equal(f.writes().length, 1);
});
test('build, scanner, report and artifact failures are errors, never vulnerability exceptions', async () => {
  for (const key of ['scan', 'policy', 'report', 'artifact']) {
    const f = fixture(); f.state.jobs[0].steps.find(s => s.name === STEPS[key]).conclusion = 'failure';
    await f.execute(); assert.match(f.writes()[0].body, /build\/scanner\/reporting error/);
    assert.match(f.writes()[0].body, /A CVE exception cannot resolve this error/);
  }
  const f = fixture(); f.state.jobs[0].steps.push({name: 'Prepare the SBN registry distribution', conclusion: 'failure'});
  await f.execute(); assert.match(f.writes()[0].body, /build\/scanner\/reporting error/);
});
test('foreign repository, wrong workflow and non-PR events are out of scope', async () => {
  for (const change of [f => {f.context.repo.owner = 'NASA-PDS';},
    f => {f.context.payload.workflow_run.path = '.github/workflows/other.yml';},
    f => {f.context.payload.workflow_run.event = 'push';}]) {
    const f = fixture(); change(f); await f.execute(); assert.equal(f.writes().length, 0);
  }
});
test('API failure propagates without posting speculative results', async () => {
  const f = fixture(); f.github.paginate = async () => {throw new Error('API unavailable');};
  await assert.rejects(f.execute(), /API unavailable/); assert.equal(f.writes().length, 0);
});
