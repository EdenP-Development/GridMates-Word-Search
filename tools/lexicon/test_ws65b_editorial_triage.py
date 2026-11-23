#!/usr/bin/env python3
"""WS65-B: editorial preflight must not impersonate source or owner approval."""
from copy import deepcopy
import json
from pathlib import Path
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65b_editorial_triage import read_annotations, build, main


class Ws65bReviewTests(unittest.TestCase):
    def test_all_source_rows_are_preserved_as_unsigned_review(self):
        rows, receipt = build(ROOT)
        self.assertEqual(receipt["woodland_drafts"], 122)
        self.assertEqual(receipt["specialist_drafts"], 145)
        self.assertEqual(receipt["total_rows"], 267)
        self.assertEqual(receipt["candidate_new_vs_en_and_fall_distinct_combined_not_approved"], 200)
        self.assertEqual(receipt["ws64_candidate_new"], 94)
        self.assertEqual(receipt["ws65_candidate_new"], 106)
        self.assertEqual(len({(x["source_pool"], x["source_id"]) for x in rows}), 267)
        self.assertEqual(receipt["new_qa_editor_approvals"], 0)
        self.assertEqual(receipt["new_owner_public_approvals"], 0)
        self.assertEqual(receipt["new_android_activated_words"], 0)
        self.assertTrue(all(not x["qa_editor_decision"] and not x["owner_decision"]
                            and not x["owner_reviewer"] for x in rows))

    def test_specific_misfit_and_ambiguous_cases_are_visible(self):
        rows, _ = build(ROOT)
        by_key = {(x["source_pool"], x["answer"]): x for x in rows}
        self.assertEqual(by_key["WS65", "COSMOS"]["preflight_lane"], "HOLD")
        self.assertEqual(by_key["WS65", "RUDBECKIA"]["preflight_lane"], "HOLD")
        self.assertEqual(by_key["WS65", "STAMEN"]["preflight_lane"], "MOVE")
        self.assertEqual(by_key["WS65", "STAMEN"]["suggested_chapter"], "Flower Anatomy / Botany")
        self.assertEqual(by_key["WS64", "PICNIC"]["preflight_lane"], "MOVE")
        self.assertEqual(by_key["WS64", "SPRING"]["preflight_lane"], "HOLD")
        self.assertEqual(by_key["WS65", "GREATREDSPOT"]["preflight_lane"], "PROMISING")
        self.assertIn("NO_12X12", by_key["WS65", "GREATREDSPOT"]["mechanics_flags"])
        self.assertTrue(by_key["WS65", "HAUMEA"]["needs_tier_change"])

    def test_every_annotation_is_a_real_source_record(self):
        rows, receipt = build(ROOT)
        annotated = read_annotations(ROOT)
        self.assertGreaterEqual(receipt["review_annotations"], 100)
        self.assertEqual(len(annotated), receipt["review_annotations"])
        self.assertEqual(len(rows), 267)

    def test_duplicate_or_invalid_annotations_fail_hard(self):
        annotations = read_annotations(ROOT)
        self.assertEqual(len(set(annotations)), len(annotations))
        self.assertNotIn(("WS65", "UNREALWORD"), annotations)

    def test_receipt_is_deterministic_and_sources_are_not_touched(self):
        paths = [
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
            ROOT / "data/lexicon/intake/ws65-specialist-chapter-candidates-v1.tsv",
            ROOT / "data/lexicon/intake/ws65b-editorial-annotations-v1.tsv",
        ]
        originals = [x.read_bytes() for x in paths]
        first_rows, first_receipt = build(ROOT)
        next_rows, next_receipt = build(ROOT)
        self.assertEqual(first_rows, next_rows)
        self.assertEqual(first_receipt, next_receipt)
        with tempfile.TemporaryDirectory() as temp:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", temp]), 0)
            report = json.loads((Path(temp) / "editorial-preflight-receipt.json").read_text())
            self.assertEqual(report, first_receipt)
            self.assertTrue((Path(temp) / "unsigned-editorial-triage.csv").is_file())
        self.assertEqual(originals, [x.read_bytes() for x in paths])


if __name__ == "__main__":
    unittest.main()
