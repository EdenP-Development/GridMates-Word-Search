#!/usr/bin/env python3
"""WS65 strict draft intake regression; no owner approval is inferred."""
from copy import deepcopy
import json
from pathlib import Path
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65_specialist_review_audit import audit, load_rows, main


class Ws65SpecialistAuditTests(unittest.TestCase):
    def test_source_snapshot_has_exact_candidate_and_reuse_counts(self):
        r = audit(load_rows(ROOT), ROOT)
        self.assertEqual(r["candidate_rows"], 145)
        self.assertEqual(r["distinct_answers"], 145)
        self.assertEqual(r["chapter_rows"], {"Flower Borders": 73, "Our Solar System": 72})
        self.assertEqual(r["chapter_canonical_reuse"], {"Flower Borders": 24, "Our Solar System": 15})
        self.assertEqual(r["not_in_english_or_fall_catalog"], 106)
        self.assertEqual(r["old_woodland_draft_overlap"], 4)
        self.assertEqual(r["new_owner_public_approvals"], 0)
        self.assertEqual(r["new_android_activated_words"], 0)

    def test_all_owner_and_editor_decisions_remain_blank(self):
        rows = load_rows(ROOT)
        self.assertTrue(all(x["review_status"] == "UNREVIEWED_CANDIDATE" for x in rows))
        self.assertTrue(all(not x["owner_decision"] and not x["editor_decision"]
                            and not x["review_notes"] for x in rows))

    def test_duplicate_insertion_is_rejected(self):
        rows = load_rows(ROOT)
        with self.assertRaisesRegex(ValueError, "Duplicate candidate"):
            audit(rows + [dict(rows[0])], ROOT)

    def test_canonical_identity_drift_is_rejected(self):
        rows = load_rows(ROOT)
        altered = deepcopy(rows)
        altered[0]["canonical_existing_word_id"] = "invented"
        with self.assertRaisesRegex(ValueError, "Canonical ID drift"):
            audit(altered, ROOT)

    def test_owner_approval_or_bad_display_is_rejected(self):
        rows = load_rows(ROOT)
        approved = deepcopy(rows)
        approved[0]["owner_decision"] = "approved"
        with self.assertRaisesRegex(ValueError, "Review field filled"):
            audit(approved, ROOT)
        bad_display = deepcopy(rows)
        bad_display[0]["display"] = "Definitely not Mercury"
        with self.assertRaisesRegex(ValueError, "Display normalization"):
            audit(bad_display, ROOT)

    def test_report_is_deterministic_and_source_file_is_read_only(self):
        source = ROOT / "data/lexicon/intake/ws65-specialist-chapter-candidates-v1.tsv"
        before = source.read_bytes()
        a = audit(load_rows(ROOT), ROOT)
        b = audit(load_rows(ROOT), ROOT)
        self.assertEqual(json.dumps(a, sort_keys=True), json.dumps(b, sort_keys=True))
        with tempfile.TemporaryDirectory() as out:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", out]), 0)
            self.assertTrue((Path(out) / "unsigned-candidate-review.csv").exists())
            self.assertEqual(json.loads((Path(out) / "editorial-receipt.json").read_text()),
                             a)
        self.assertEqual(source.read_bytes(), before)


if __name__ == "__main__":
    unittest.main()
