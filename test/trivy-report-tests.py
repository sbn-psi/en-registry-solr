#!/usr/bin/env python3
import importlib.util
import json
import os
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location("trivy_report", ROOT / ".github/scripts/report-trivy.py")
reporter = importlib.util.module_from_spec(spec)
spec.loader.exec_module(reporter)


class TrivyReportTests(unittest.TestCase):
    def run_report(self, vulnerabilities):
        scan = {"SchemaVersion": 2, "ArtifactName": "en-registry-solr:test", "Results": [
            {"Target": "Ubuntu", "Class": "os-pkgs", "Type": "ubuntu", "Vulnerabilities": vulnerabilities},
            {"Target": "Java", "Class": "lang-pkgs", "Type": "jar", "Vulnerabilities": []}]}
        work = tempfile.TemporaryDirectory()
        self.addCleanup(work.cleanup)
        directory = Path(work.name)
        source = directory / "full.json"
        original = json.dumps(scan)
        source.write_text(original)
        with patch.dict(os.environ, {"GITHUB_STEP_SUMMARY": str(directory / "step-summary.md")}):
            count = reporter.report(source, directory)
        self.assertEqual(source.read_text(), original)
        self.assertEqual((directory / "summary.md").read_text(), (directory / "step-summary.md").read_text())
        return count, json.loads((directory / "actionable.json").read_text()), (directory / "summary.md").read_text()

    def test_gate_selects_only_fixed_high_and_critical_without_losing_full_report(self):
        vulnerabilities = [{"VulnerabilityID": str(i), "Severity": severity, "FixedVersion": fixed,
                            "PkgName": "package", "InstalledVersion": "1.0"}
                           for i, (severity, fixed) in enumerate([
                               ("HIGH", "1.1"), ("CRITICAL", "2.0"), ("CRITICAL", ""),
                               ("HIGH", None), ("HIGH", " "), ("MEDIUM", "1.1"), ("LOW", "1.1")])]
        count, actionable, summary = self.run_report(vulnerabilities)
        self.assertEqual(count, 2)
        self.assertEqual([v["VulnerabilityID"] for v in actionable["Results"][0]["Vulnerabilities"]], ["0", "1"])
        self.assertIn("| CRITICAL | 2 |", summary)
        self.assertIn("| MEDIUM | 1 |", summary)

    def test_clean_image_report_is_not_a_failure(self):
        count, actionable, summary = self.run_report([])
        self.assertEqual(count, 0)
        self.assertEqual(actionable["Results"], [])
        self.assertIn("No findings meet", summary)

    def test_unfixed_findings_remain_visible_but_do_not_fail_fixed_gate(self):
        count, actionable, summary = self.run_report([{"Severity": "CRITICAL", "FixedVersion": ""}])
        self.assertEqual(count, 0)
        self.assertIn("| CRITICAL | 1 |", summary)

    def test_summary_escapes_package_metadata(self):
        _, _, summary = self.run_report([{"Severity": "HIGH", "FixedVersion": "2", "PkgName": "a|b\nc"}])
        self.assertIn("a\\|b c", summary)

    def test_incomplete_or_partial_image_report_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory)
            for scan in [{}, {"SchemaVersion": 2, "Results": []},
                         {"SchemaVersion": 2, "Results": [{"Class": "os-pkgs"}]},
                         {"SchemaVersion": 2, "Results": [{"Type": "jar"}]}]:
                with self.subTest(scan=scan):
                    source = path / "full.json"
                    source.write_text(json.dumps(scan))
                    with self.assertRaises(ValueError):
                        reporter.report(source, path)


if __name__ == "__main__":
    unittest.main()
