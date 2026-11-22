#!/usr/bin/env python3
"""WS64: independently verify Woodland manuscript editorial reconciliation."""
import json
from pathlib import Path
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws64_woodland_review_queue import build, reconcile, main


class Ws64WoodlandIntakeTests(unittest.TestCase):
    def test_all_source_proposals_and_existing_ids_are_counted_honestly(self):
        rows, report = build(ROOT)
        self.assertEqual(len(rows), 122)
        self.assertEqual(report["proposals"], 122)
        self.assertEqual(report["distinct_proposal_answers"], 122)
        self.assertEqual(report["already_present_in_English_or_Fall_qa"], 28)
        self.assertEqual(report["potential_new_canonical_after_review"], 94)
        self.assertEqual(report["newly_owner_approved_words"], 0)
        self.assertEqual(report["newly_activated_android_words"], 0)
        self.assertEqual(report["by_chapter"], {
            "Birdsong": 23,
            "By the Stream": 33,
            "Forest Discoveries": 30,
            "Quiet Clearings": 36,
        })
        self.assertEqual(len(report["source_sha256"]), 4)
        for row in rows:
            self.assertEqual(row["owner_decision"], "")
            self.assertEqual(row["owner_reviewer"], "")
            self.assertEqual(row["owner_reviewed_on"], "")
            self.assertEqual(row["editorial_notes"], "")
            self.assertTrue(row["source_provenance"].endswith("candidate_unapproved"))

    def test_existing_canonical_answer_must_reuse_original_id(self):
        rows, _ = build(ROOT)
        by_answer = {r["answer"]: r for r in rows}
        for answer, canon in (
            ("WING", "gm000453"), ("NEST", "gm000136"),
            ("WATER", "gm000461"), ("DRAGONFLY", "gm000449"),
            ("ANTLERS", "gm000738"), ("MIST", "gm000695"),
            ("DAISY", "gm000345"),
        ):
            self.assertEqual(by_answer[answer]["canonical_existing_word_id"], canon)
            self.assertEqual(by_answer[answer]["intake_classification"], "REUSE_EXISTING_QA_CANONICAL")
            self.assertNotEqual(by_answer[answer]["candidate_id"], canon)

    def test_unapproved_proposals_never_get_fake_new_canonical_ids_or_reviews(self):
        rows, _ = build(ROOT)
        by_answer = {r["answer"]: r for r in rows}
        self.assertEqual(by_answer["BEAK"]["intake_classification"],
                         "PROPOSED_NEW_CANONICAL_NEEDS_REVIEW")
        self.assertEqual(by_answer["BEAK"]["canonical_existing_word_id"], "")
        self.assertEqual(by_answer["BEAK"]["owner_decision"], "")
        self.assertEqual(by_answer["FLEDGLING"]["owner_decision"], "")

    def test_duplicate_source_candidate_is_a_hard_fail(self):
        example = {
            "candidate_id": "ws64-test", "answer": "RIVER", "display": "River",
            "source_chapter": "Test", "proposed_tier": "familiar",
        }
        with self.assertRaisesRegex(ValueError, "Duplicate proposed candidate"):
            reconcile([example, dict(example)], {})

    def test_report_is_deterministic_and_source_is_never_modified(self):
        source = ROOT / "data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv"
        before = source.read_bytes()
        first_rows, first_report = build(ROOT)
        second_rows, second_report = build(ROOT)
        self.assertEqual(first_rows, second_rows)
        self.assertEqual(first_report, second_report)
        with tempfile.TemporaryDirectory() as d:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", d]), 0)
            self.assertTrue((Path(d) / "woodland-proposals-review.csv").is_file())
            report = json.loads((Path(d) / "source-reconciliation.json").read_text())
            self.assertEqual(report["potential_new_canonical_after_review"], 94)
        self.assertEqual(source.read_bytes(), before)


if __name__ == "__main__":
    unittest.main()
