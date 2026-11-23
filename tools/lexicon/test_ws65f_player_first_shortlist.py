#!/usr/bin/env python3
"""WS65-F editorial shortlist test: no backwards tier drift or fake boards."""
from pathlib import Path
import json
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65f_player_first_shortlist import review, main


class Ws65fPlayerFirstTests(unittest.TestCase):
    def test_exact_source_and_approval_boundary(self):
        rows, samples, r = review(ROOT)
        self.assertEqual(len(rows), 80)
        self.assertEqual(r["prior_individually_referenced_senses"], 105)
        self.assertEqual(r["new_topic_context_shortlists_not_independently_verified"], 80)
        self.assertEqual(r["remaining_promising_without_individual_context"], 82)
        self.assertEqual(r["source_proposals_total"], 267)
        self.assertEqual(r["original_candidate_new_spellings_not_approved"], 200)
        self.assertEqual(len(samples), 36)
        self.assertEqual(r["concept_sample_pages"], 3)
        self.assertEqual(r["new_editor_release_approvals"], 0)
        self.assertEqual(r["new_owner_release_approvals"], 0)
        self.assertEqual(r["new_android_words"], 0)
        self.assertTrue(all(not x["owner_release_decision"] and
                            not x["human_editor_decision"] for x in rows))
        self.assertTrue(all(x["rights_status"] == "NOT_CLEARED" for x in rows))

    def test_chapter_variety_and_review_cautions(self):
        rows, _, r = review(ROOT)
        self.assertEqual(r["shortlist_by_chapter"], {
            "Birdsong": 12, "By the Stream": 12,
            "Quiet Clearings": 12, "Forest Discoveries": 12,
            "Our Solar System": 16, "Flower Borders": 16,
        })
        by_key = {(x["chapter"], x["answer"]): x for x in rows}
        self.assertEqual(by_key["Birdsong", "TALON"]["provisional_reader_tier"], "intermediate")
        self.assertEqual(by_key["By the Stream", "MAYFLY"]["provisional_reader_tier"], "intermediate")
        self.assertEqual(by_key["Our Solar System", "ASTEROID"]["provisional_reader_tier"], "intermediate")
        self.assertEqual(by_key["Flower Borders", "ZINNIA"]["provisional_reader_tier"], "familiar")

    def test_samples_explicitly_not_generated_grids(self):
        _, samples, _ = review(ROOT)
        self.assertEqual({x["board_tier"] for x in samples}, {"RELAXED12", "CLASSIC15"})
        self.assertEqual(len({x["sample_page_id"] for x in samples}), 3)
        self.assertEqual({x["sample_status"] for x in samples},
                         {"UNSIGNED_WORD_LIST_CONCEPT_NOT_GENERATED_BOARD"})
        by_page = {}
        for row in samples:
            by_page.setdefault(row["sample_page_id"], []).append(row["answer"])
        self.assertEqual(len(by_page["BIRDSONG_OPENING"]), 12)
        self.assertEqual(len(by_page["SOLAR_SYSTEM_OPENING"]), 12)
        self.assertEqual(len(by_page["FLOWER_BORDERS_OPENING"]), 12)

    def test_source_reproducibility_and_no_authoring_writes(self):
        paths = [
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
            ROOT / "data/lexicon/intake/ws65f-player-first-shortlist-v1.tsv",
            ROOT / "data/lexicon/intake/ws65f-sample-page-wordlists-v1.tsv",
        ]
        before = [x.read_bytes() for x in paths]
        original = review(ROOT)
        self.assertEqual(original, review(ROOT))
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", tmp]), 0)
            output = Path(tmp)
            self.assertEqual(json.loads((output / "player-first-receipt.json").read_text()), original[2])
            self.assertTrue((output / "unsigned-player-shortlist.csv").exists())
            self.assertTrue((output / "concept-only-opening-pages.csv").exists())
        self.assertEqual(before, [x.read_bytes() for x in paths])


if __name__ == "__main__":
    unittest.main()
