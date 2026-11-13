#!/usr/bin/env python3
"""Read-only, weekly watch for explicitly signaled upstream security changes."""
import json
import os
from pathlib import Path
import subprocess
import sys
from urllib.parse import quote

UPSTREAM = "NASA-PDS/registry-legacy-solr"
FORK = "sbn-psi/en-registry-solr"


def api(path, **parameters):
    command = ["gh", "api", "--method", "GET", path]
    for key, value in parameters.items():
        command += ["-f", f"{key}={value}"]
    result = subprocess.run(command, capture_output=True, text=True, timeout=30)
    if result.returncode:
        raise RuntimeError(f"GitHub API request failed: {path} (exit {result.returncode})")
    return json.loads(result.stdout)


def pages(path, key=None, **parameters):
    for page in range(1, 11):
        response = api(path, per_page=100, page=page, **parameters)
        if isinstance(response, dict) and response.get("incomplete_results"):
            raise RuntimeError("GitHub returned incomplete security search results")
        items = response[key] if key else response
        yield from items
        if len(items) < 100:
            return
    raise RuntimeError("Security results exceeded the checked pagination limit")


def check():
    repository = os.environ.get("GITHUB_REPOSITORY", FORK)
    if repository != FORK:
        raise RuntimeError(f"This watch is scoped to {FORK}")
    ref = os.environ.get("FORK_REF") or api(f"repos/{FORK}")["default_branch"]
    upstream_ref = api(f"repos/{UPSTREAM}")["default_branch"]
    head = quote(FORK.split("/")[0] + ":" + ref, safe=":")
    comparison = api(f"repos/{UPSTREAM}/compare/{upstream_ref}...{head}")
    baseline = comparison["merge_base_commit"]
    since = baseline["commit"]["committer"]["date"]
    query = f"repo:{UPSTREAM} is:pr is:merged merged:>={since[:10]}"
    candidates = {}
    # A generic 'dependencies' label does not imply a security update.
    for selector in ["label:security", "security in:title", "CVE in:title", "GHSA in:title",
                     "vulnerability in:title", "vulnerabilities in:title"]:
        for item in pages("search/issues", key="items", q=f"{query} {selector}"):
            candidates[item["number"]] = item
    findings = []
    for number in sorted(candidates):
        pull = api(f"repos/{UPSTREAM}/pulls/{number}")
        if not pull["merged_at"]:
            continue
        included = api(f"repos/{UPSTREAM}/compare/{pull['merge_commit_sha']}...{head}")
        if included["status"] not in ("ahead", "identical"):
            findings.append(f"- Unapplied security PR: [{pull['title']}]({pull['html_url']})")
    for advisory in pages(f"repos/{UPSTREAM}/security-advisories", state="published"):
        if (advisory.get("updated_at") or advisory["published_at"]) > since:
            findings.append(f"- Advisory updated since our shared upstream commit: "
                            f"[{advisory['ghsa_id']}: {advisory['summary']}]({advisory['html_url']}) "
                            "(review applicability)")
    report = ["# Upstream security watch", "",
              f"Compared `{FORK}@{ref}` with `{UPSTREAM}@{upstream_ref}`.",
              f"Last shared upstream commit: `{baseline['sha']}` ({since}).", ""]
    report += findings or ["No unapplied, explicitly signaled upstream security changes found."]
    report += ["", "This filter uses security labels, PR-title keywords and published advisories. "
               "Unmarked fixes can be missed. The fork's Dependabot security alerts remain the "
               "dependency-vulnerability check. This does not scan container OS packages.",
               "", "No merge, deployment, issue or repository-setting changes are performed."]
    text = "\n".join(report) + "\n"
    print(text)
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with Path(os.environ["GITHUB_STEP_SUMMARY"]).open("a") as summary:
            summary.write(text)
    # A failed weekly run produces GitHub's normal workflow-failure notification.
    return 1 if findings else 0


if __name__ == "__main__":
    try:
        sys.exit(check())
    except (RuntimeError, KeyError, subprocess.TimeoutExpired, json.JSONDecodeError) as error:
        print(f"Security watch could not complete: {error}", file=sys.stderr)
        sys.exit(2)
