'use strict';

const REPOSITORY = 'sbn-psi/en-registry-solr';
const WORKFLOW = '.github/workflows/container-security.yml';
const MARKER = '<!-- sbn-solr-container-security:v1 -->';
const STEPS = {
  scan: 'Scan all image OS and library vulnerabilities',
  policy: 'Apply reviewed exceptions to a separate report',
  report: 'Summarize fixable HIGH and CRITICAL findings',
  artifact: 'Retain the full scan and actionable reports',
  gate: 'Fail on fixable HIGH or CRITICAL findings',
  sarif: 'Upload actionable SARIF to GitHub code scanning',
};

// Called only by trusted default-branch workflow_run code. All PR input is metadata;
// no PR checkout, logs, artifact downloads, or artifact execution are needed.
module.exports = async function comment({github, context, core}) {
  const {owner, repo} = context.repo;
  const event = context.payload.workflow_run;
  const skip = reason => core.info(`Container security feedback skipped: ${reason}`);
  if (`${owner}/${repo}` !== REPOSITORY || !event || event.name !== 'Container security'
      || event.path !== WORKFLOW || event.event !== 'pull_request' || event.status !== 'completed') {
    return skip('out of scope');
  }
  const {data: run} = await github.rest.actions.getWorkflowRun({owner, repo, run_id: event.id});
  if (run.id !== event.id || run.head_sha !== event.head_sha || run.run_attempt !== event.run_attempt
      || run.name !== 'Container security' || run.path !== WORKFLOW
      || run.event !== 'pull_request' || run.status !== 'completed') return skip('stale or unexpected run');
  if (!['success', 'failure', 'timed_out', 'action_required', 'startup_failure'].includes(run.conclusion)) {
    return skip('cancelled or skipped run');
  }
  const runs = await github.paginate(github.rest.actions.listWorkflowRuns, {
    owner, repo, workflow_id: run.workflow_id, event: 'pull_request', head_sha: run.head_sha, per_page: 100,
  });
  if (runs.some(candidate => candidate.head_sha === run.head_sha
      && candidate.head_branch === run.head_branch
      && candidate.head_repository?.full_name === run.head_repository?.full_name
      && (candidate.id > run.id || (candidate.id === run.id && candidate.run_attempt > run.run_attempt)))) {
    return skip('a newer run exists');
  }
  let prs = run.pull_requests || [];
  if (!prs.length) {
    prs = await github.paginate(github.rest.repos.listPullRequestsAssociatedWithCommit, {
      owner, repo, commit_sha: run.head_sha, per_page: 100,
    });
  }
  const numbers = [...new Set(prs.map(pr => pr.number).filter(Number.isSafeInteger))];
  if (!numbers.length) return skip('no associated PR');
  const jobs = await github.paginate(github.rest.actions.listJobsForWorkflowRun, {
    owner, repo, run_id: run.id, filter: 'latest', per_page: 100,
  });
  const job = jobs.find(candidate => candidate.name === 'trivy');
  const outcome = name => job?.steps?.find(step => step.name === name)?.conclusion;
  const reportsReady = ['scan', 'policy', 'report', 'artifact'].every(key => outcome(STEPS[key]) === 'success');
  const otherFailure = jobs.some(candidate => candidate.name !== 'trivy'
    && !['success', 'skipped'].includes(candidate.conclusion))
    || job?.steps?.some(step => ['failure', 'timed_out', 'cancelled'].includes(step.conclusion)
      && ![STEPS.gate, STEPS.sarif].includes(step.name));
  let result = 'error';
  if (reportsReady && !otherFailure && run.conclusion === 'success' && outcome(STEPS.gate) === 'success') {
    result = 'clean';
  } else if (reportsReady && !otherFailure && run.conclusion === 'failure' && outcome(STEPS.gate) === 'failure') {
    result = 'findings';
  }
  const runURL = `https://github.com/${REPOSITORY}/actions/runs/${run.id}`;
  const docs = `https://github.com/${REPOSITORY}/blob/${run.head_sha}/deploy/sbn-solr/README.md#reviewed-exceptions`;
  const title = result === 'findings' ? '🔴 Security gate failed: fixable HIGH/CRITICAL findings remain'
    : result === 'clean' ? '✅ Security gate passed for the current PR head'
      : '🔴 Container security could not complete: build/scanner/reporting error';
  const explanation = result === 'findings'
    ? 'The check intentionally remains red. Review the remaining findings; no exceptions are supplied by default.'
    : result === 'clean'
      ? 'No findings remain in the HIGH/CRITICAL + fixed-version gate after the reviewed policy. This does not mean the full image has no vulnerabilities.'
      : '**Fix the failed build, scanner, or reporting step. A CVE exception cannot resolve this error.** Reports may be incomplete or unavailable; do not treat this as a clean scan.';
  const body = `${MARKER}\n## ${title}\n\n${explanation}\n\n`
    + `[Run, failed steps, summary and artifacts](${runURL}) · attempt ${run.run_attempt} · PR head \`${run.head_sha}\`\n\n`
    + 'Download the **solr-trivy-<commit>** artifact (14-day retention). **full.json** is the truly unfiltered scan: all severities, unfixed findings and package inventory. '
    + '**policy-filtered.json** applies the reviewed policy; **actionable.json / actionable.sarif** and **summary.md** describe the remaining fixed HIGH/CRITICAL findings. '
    + 'The artifact also records the image identity, scan commit and policy used. GitHub code scanning receives SARIF where permissions allow.\n\n'
    + `**Reviewed exceptions:** see [the scoped exception example](${docs}). Edit \`.trivyignore.yaml\` only after review: use exact package \`paths\` from \`PkgPath\`, `
    + 'record rationale, evidence and reviewer in **statement**, and set a short future **expired_at** (YYYY-MM-DD). Review each installed copy separately. '
    + 'Exceptions affect actionable results, never full.json. Do not weaken the severity/fixed gate or use an exception to hide scanner errors. '
    + 'Trivy does not create base-image fix PRs or change deployed containers.\n';
  for (const number of numbers) {
    const current = async () => (await github.rest.pulls.get({owner, repo, pull_number: number})).data;
    const valid = pr => pr.state === 'open' && pr.base?.repo?.full_name === REPOSITORY && pr.head?.sha === run.head_sha;
    if (!valid(await current())) { skip(`PR ${number} has a different head or base`); continue; }
    const comments = await github.paginate(github.rest.issues.listComments, {
      owner, repo, issue_number: number, per_page: 100,
    });
    const existing = comments.find(item => item.user?.type === 'Bot'
      && item.user?.login === 'github-actions[bot]' && item.body?.startsWith(MARKER));
    if (result === 'clean' && !existing) continue; // No clean-run comment spam.
    if (!valid(await current())) { skip(`PR ${number} changed while preparing feedback`); continue; }
    if (existing) {
      await github.rest.issues.updateComment({owner, repo, comment_id: existing.id, body});
    } else {
      await github.rest.issues.createComment({owner, repo, issue_number: number, body});
    }
  }
};
module.exports.MARKER = MARKER;
module.exports.STEPS = STEPS;
