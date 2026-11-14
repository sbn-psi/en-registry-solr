#!/usr/bin/env python3
"""Keep the full scan intact; derive actionable findings and an Actions summary."""
import argparse
from collections import Counter
from copy import deepcopy
import json
import os
from pathlib import Path


def eligible(vulnerability):
    fixed = vulnerability.get("FixedVersion")
    return (vulnerability.get("Severity") in ("HIGH", "CRITICAL")
            and isinstance(fixed, str) and bool(fixed.strip()))


def records(scan):
    # Compare complete records, including package path and target; retain duplicates.
    return Counter(json.dumps([result.get("Target"), result.get("Type"), vulnerability], sort_keys=True)
                   for result in scan.get("Results", [])
                   for vulnerability in result.get("Vulnerabilities") or [])


def report(source, destination, policy_source=None):
    scan = json.loads(source.read_text())
    results = scan.get("Results")
    if scan.get("SchemaVersion") != 2 or not isinstance(results, list):
        raise ValueError("Expected a complete Trivy schema-v2 image report")
    if not any(result.get("Class") == "os-pkgs" for result in results):
        raise ValueError("The complete Solr image scan did not identify OS packages")
    if not any(result.get("Type") == "jar" for result in results):
        raise ValueError("The complete Solr image scan did not identify Java packages")
    policy = json.loads(policy_source.read_text()) if policy_source else scan
    if (policy.get("SchemaVersion") != 2 or not isinstance(policy.get("Results"), list)
            or policy.get("ArtifactName") != scan.get("ArtifactName")
            or policy.get("ArtifactID") != scan.get("ArtifactID")):
        raise ValueError("Policy report must describe the same complete image scan")
    if records(policy) - records(scan):
        raise ValueError("Policy report contains findings absent from the unfiltered scan")
    raw_eligible = sum(eligible(v) for result in results for v in result.get("Vulnerabilities") or [])
    counts = Counter(v.get("Severity", "UNKNOWN")
                     for result in results for v in result.get("Vulnerabilities") or [])
    actionable = []
    filtered = deepcopy(policy)
    filtered["Results"] = []
    for result in policy["Results"]:
        selected = []
        for vulnerability in result.get("Vulnerabilities") or []:
            if eligible(vulnerability):
                selected.append(vulnerability)
                actionable.append((result.get("Target", ""), vulnerability))
        if selected:
            entry = deepcopy(result)
            entry["Vulnerabilities"] = selected
            filtered["Results"].append(entry)
    destination.mkdir(parents=True, exist_ok=True)
    (destination / "actionable.json").write_text(json.dumps(filtered, indent=2) + "\n")
    (destination / "actionable-count.txt").write_text(str(len(actionable)) + "\n")
    lines = ["# Solr custom-image vulnerability scan", "",
             f"Image: `{scan.get('ArtifactName', 'unknown')}`", "",
             "Coverage: OS packages and Java libraries in the complete built image.", "",
             "| Severity | All findings (including unfixed) |", "| --- | ---: |"]
    for severity in ("CRITICAL", "HIGH", "MEDIUM", "LOW", "UNKNOWN"):
        lines.append(f"| {severity} | {counts[severity]} |")
    lines += ["", f"**Actionable HIGH/CRITICAL findings with a fixed version: {len(actionable)}**", "",
              "Counts are finding records; repeated package installations can repeat a CVE.", "",
              f"Before reviewed exceptions: {raw_eligible}; excluded by policy: {raw_eligible - len(actionable)}.", "",
              "Download the `solr-trivy-<commit>` artifact on this run. `full.json` is unfiltered (all severities, "
              "including unfixed findings); `policy-filtered.json` applies `.trivyignore.yaml`; "
              "`actionable.json` / `actionable.sarif` contain the remaining fixed HIGH/CRITICAL findings.", "",
              "**Reviewing a finding:** use its exact `PkgPath` from `full.json`, document the evidence/reviewer "
              "in `statement`, and set a short future `expired_at` in `.trivyignore.yaml`. Scope `paths` to each "
              "reviewed package copy. See `deploy/sbn-solr/README.md#reviewed-exceptions` for an example. "
              "No exceptions are supplied by default. Never use a CVE exception to hide a build, scanner, or "
              "reporting error; fix the failing step instead."]
    if actionable:
        lines += ["", "| Severity | Package | Installed | Fixed | Vulnerability | Image target |",
                  "| --- | --- | --- | --- | --- | --- |"]
        def cell(value):
            return str(value).replace("|", "\\|").replace("\r", " ").replace("\n", " ")
        ordered = sorted(actionable, key=lambda item: (
            item[1]["Severity"] != "CRITICAL", item[1].get("VulnerabilityID", ""),
            item[1].get("PkgName", ""), item[0]))
        for target, vulnerability in ordered[:100]:
            values = [vulnerability.get(key, "") for key in
                      ("Severity", "PkgName", "InstalledVersion", "FixedVersion", "VulnerabilityID")]
            lines.append("| " + " | ".join(cell(value) for value in values + [target]) + " |")
        if len(actionable) > 100:
            lines += ["", "Showing the first 100 actionable records; the artifact contains every record."]
    else:
        lines += ["", "No findings meet the HIGH/CRITICAL + fixed-version gate."]
    lines += ["", "Trivy reports vulnerabilities; it does not create base-image fix PRs or patch deployments."]
    summary = "\n".join(lines) + "\n"
    (destination / "summary.md").write_text(summary)
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with Path(os.environ["GITHUB_STEP_SUMMARY"]).open("a") as stream:
            stream.write(summary)
    print(f"Full findings: {sum(counts.values())}; actionable HIGH/CRITICAL: {len(actionable)}")
    return len(actionable)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", type=Path)
    parser.add_argument("destination", type=Path)
    parser.add_argument("--policy-report", type=Path)
    arguments = parser.parse_args()
    report(arguments.source, arguments.destination, arguments.policy_report)
