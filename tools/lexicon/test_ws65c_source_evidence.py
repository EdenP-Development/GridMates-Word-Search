#!/usr/bin/env python3
"""WS65-C: verified sense references cannot silently become approvals."""
from pathlib import Path
from copy import deepcopy
import json
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65c_source_evidence import load_evidence, build, main


class Ws65cSourceEvidenceTests(unittest.TestCase):
    def test_ledger_preserves_267_rows_and_reports_actual_evidence_coverage(self):
        rows, receipt = build(ROOT)
        self.assertEqual(len(rows), 267)
        self.assertEqual(receipt["full_source_rows"], 267)
        self.assertEqual(receipt["source_checked_spelling_senses"], 38)
        self.assertEqual(receipt["still_without_individual_evidence"], 229)
        self.assertEqual(sum(receipt["baseline_preflight_lanes_unchanged"].values()), 267)
        self.assertEqual(receipt["new_qa_editor_signoffs"], 0)
        self.assertEqual(receipt["new_owner_public_approvals"], 0)
        self.assertEqual(receipt["new_playable_android_answers"], 0)
        self.assertEqual(receipt["possible_new_distinct_canonical_total_not_approved"], 200)

    def test_selected_word_sense_and_chapter_recommendations_are_traceable(self):
        rows, _ = build(ROOT)
        by_answer = {(r["source_pool"],r["answer"]):r for r in rows}
        self.assertEqual(by_answer["WS65","ARIEL"]["editorial_recommendation"], "KEEP_SPECIALIST")
        self.assertEqual(by_answer["WS65","COSMOS"]["editorial_recommendation"], "KEEP_WITH_SENSE_NOTE")
        self.assertEqual(by_answer["WS65","STAMEN"]["candidate_target_chapter"], "Flower Anatomy / Botany")
        self.assertEqual(by_answer["WS65","BLACKEYEDSUSAN"]["editorial_recommendation"], "HOLD_CONCEPT_OVERLAP")
        self.assertEqual(by_answer["WS64","WATERLILY"]["candidate_target_chapter"], "Ponds & Wetlands")
        self.assertTrue(by_answer["WS64","WILDGINGER"]["source_url"].startswith("https://"))
        self.assertEqual(by_answer["WS64","WILDGINGER"]["owner_decision"], "")
        self.assertEqual(by_answer["WS65","MERCURY"]["source_evidence_status"], "NOT_IN_THIS_EVIDENCE_BATCH")

    def test_source_evidence_records_are_unsigned_and_rights_not_cleared(self):
        evidence = load_evidence(ROOT)
        self.assertEqual(len(evidence), 38)
        for r in evidence.values():
            self.assertEqual(r["rights_status"], "NOT_CLEARED")
            self.assertEqual(r["qa_editor_decision"], "")
            self.assertEqual(r["owner_decision"], "")
            self.assertTrue(r["source_checked"].startswith("https://"))

    def test_duplicate_and_false_approval_would_fail_validation(self):
        rows = load_evidence(ROOT)
        self.assertEqual(len(rows), len(set(rows)))
        self.assertNotIn(("WS65", "UNREAL"), rows)

    def test_byte_stability_and_no_authoring_writes(self):
        files = [
            ROOT / "data/lexicon/intake/ws65c-source-evidence-v1.tsv",
            ROOT / "data/lexicon/intake/ws65b-editorial-annotations-v1.tsv",
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
        ]
        start = [f.read_bytes() for f in files]
        first = build(ROOT)
        self.assertEqual(first, build(ROOT))
        with tempfile.TemporaryDirectory() as out:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", out]), 0)
            report = json.loads((Path(out) / "source-evidence-receipt.json").read_text())
            self.assertEqual(report, first[1])
            self.assertTrue((Path(out) / "source-evidence-ledger.csv").is_file())
        self.assertEqual(start, [f.read_bytes() for f in files])


if __name__ == "__main__":
    unittest.main()
