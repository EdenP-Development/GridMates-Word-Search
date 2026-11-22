#!/usr/bin/env python3
"""WS63 read-only quality reporting tests. No approvals or Android data writes."""
import copy
import json
from pathlib import Path
import tempfile
import unittest

from ws63_quality_audit import (
    ROOT, analyze, conflicts_between_editions, markdown, relationship_flags, usable,
    main,
)


class Ws63QualityAuditTests(unittest.TestCase):
    def test_v5_eligibility_is_not_a_dictionary_ban(self):
        self.assertTrue(usable("TREE", 12, "familiar", "familiar"))
        self.assertFalse(usable("RADAR", 20, "familiar", "all"))
        self.assertFalse(usable("VERYVERYLONG", 12, "familiar", "familiar"))
        self.assertFalse(usable("TREE", 12, "advanced", "familiar"))
        self.assertFalse(usable("TREE", 15, "advanced", "not-advanced"))
        self.assertTrue(usable("TREE", 20, "advanced", "all"))

    def test_related_words_are_review_flags_not_auto_rejections(self):
        rows = {
            "a": {"answer": "RIVER"},
            "b": {"answer": "RIVERBANK"},
            "c": {"answer": "BIRD"},
            "d": {"answer": "BIRDS"},
        }
        f = relationship_flags(rows)
        self.assertGreaterEqual(f["contains_related_pair_count"], 2)
        self.assertIn(["BIRD", "BIRDS"], f["plurals_to_review_examples"])
        self.assertGreaterEqual(f["plurals_to_review_count"], 1)

    def test_cross_edition_id_drift_is_hard_failure(self):
        e = {
            "en-v1": {"word_rows": {"gm00001": {"answer": "FOREST"}}},
            "fall-v1": {"word_rows": {"gm00001": {"answer": "WOODLAND"}}},
        }
        problems = conflicts_between_editions(e)
        self.assertTrue(any("changed answer" in p for p in problems))

    def test_cross_edition_duplicate_answer_different_id_is_hard_failure(self):
        e = {
            "en-v1": {"word_rows": {"gm00001": {"answer": "FOREST"}}},
            "fall-v1": {"word_rows": {"gm00002": {"answer": "FOREST"}}},
        }
        problems = conflicts_between_editions(e)
        self.assertTrue(any("two IDs" in p for p in problems))

    def test_current_authoring_counts_and_chapter_contracts(self):
        r = analyze(ROOT)
        self.assertEqual(r["en_v1_qa_unique"], 670)
        self.assertEqual(r["fall_v1_qa_unique"], 150)
        self.assertEqual(r["fall_overlap_with_en_v1"], 33)
        self.assertEqual(r["fall_new_unique_over_en_v1"], 117)
        self.assertEqual(r["authoring_edition_qa_unique_union"], 787)
        self.assertEqual(r["qa_topic_families_active_total"], 6)
        self.assertEqual(r["production_approved_new_words"], 0)
        self.assertEqual(r["status"], "INTERNAL_QA_DIAGNOSTIC_NOT_RELEASE_APPROVAL")
        self.assertEqual(len(r["proposed_chapters"]), 18)
        birds = [ch for ch in r["proposed_chapters"]
                 if ch["book"] == "WOODLAND" and ch["chapter"] == 5]
        self.assertEqual(len(birds), 1)
        self.assertEqual(birds[0]["title"], "Birdsong")
        self.assertEqual(birds[0]["three_page_target_slots"], 52)
        # A low-coverage specialist topic must be visible as a warning,
        # not made to disappear by padding with off-topic words.
        self.assertGreater(birds[0]["minimum_cross_page_reused_slots_from_pool_limit"], 0)
        doc = markdown(r)
        self.assertIn("Birdsong", doc)
        self.assertIn("787", doc)
        self.assertIn("Not owner-approved release content", doc)

    def test_read_only_report_is_byte_deterministic_and_does_not_touch_authored_sources(self):
        file = ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv"
        start = file.read_bytes()
        a = analyze(ROOT)
        b = analyze(ROOT)
        self.assertEqual(json.dumps(a, sort_keys=True), json.dumps(b, sort_keys=True))
        self.assertEqual(file.read_bytes(), start)
        with tempfile.TemporaryDirectory() as d:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", d]), 0)
            saved = json.loads((Path(d) / "quality-baseline.json").read_text())
            self.assertEqual(saved["authoring_edition_qa_unique_union"], 787)
            self.assertTrue((Path(d) / "quality-baseline.md").is_file())
        self.assertEqual(file.read_bytes(), start)


if __name__ == "__main__":
    unittest.main()
