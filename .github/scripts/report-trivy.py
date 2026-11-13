#!/usr/bin/env python3
"""Keep the full scan intact; derive actionable findings and an Actions summary."""
import argparse
from collections import Counter
from copy import deepcopy
import json
import os
from pathlib import Path


def report(source, destination):
    scan = json.loads(source.read_text())
    results = scan.get("Results")
    if scan.get("SchemaVersion") != 2 or not isinstance(results, list):
        raise ValueError("Expected a complete Trivy schema-v2 image report")
    if not any(result.get("Class") == "os-pkgs" for result in results):
        raise ValueError("The complete Solr image scan did not identify OS packages")
    if not any(result.get("Type") == "jar" for result in results):
        raise ValueError("The complete Solr image scan did not identify Java packages")
    counts = Counter()
    actionable = []
    filtered = deepcopy(scan)
    filtered["Results"] = []
    for result in results:
        selected = []
        for vulnerability in result.get("Vulnerabilities") or []:
            severity = vulnerability.get("Severity", "UNKNOWN")
            counts[severity] += 1
            fixed = vulnerability.get("FixedVersion")
            if severity in ("HIGH", "CRITICAL") and isinstance(fixed, str) and fixed.strip():
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
              "The full JSON/package inventory and actionable JSON/SARIF are retained in the scan artifact."]
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
    arguments = parser.parse_args()
    report(arguments.source, arguments.destination)
