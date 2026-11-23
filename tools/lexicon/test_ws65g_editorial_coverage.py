#!/usr/bin/env python3
"""WS65-G: all outstanding player-context source rows, with strict nonapproval QA."""
from pathlib import Path
import json
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65g_editorial_coverage import read_82, build, main


class Ws65gEditorialCoverage(unittest.TestCase):
    def test_complete_82_remaining_context_notes_without_public_approval(self):
        rows, report = build(ROOT)
        self.assertEqual(len(rows), 82)
        self.assertEqual(report["ws65cde_word_specific_source_notes"], 105)
        self.assertEqual(report["ws65f_topic_linked_player_glosses"], 80)
        self.assertEqual(report["ws65g_new_topic_linked_player_glosses"], 82)
        self.assertEqual(report["total_source_proposal_occurrences"], 267)
        self.assertEqual(report["promising_without_player_context_after_ws65g"], 0)
        self.assertEqual(report["new_human_editor_approvals"], 0)
        self.assertEqual(report["new_owner_public_approvals"], 0)
        self.assertEqual(report["new_android_playable_answers"], 0)
        self.assertEqual(report["original_potential_new_answer_spellings_not_approved"], 200)

    def test_exact_chapter_counts_and_not_all_independent_verification(self):
        rows, report = build(ROOT)
        self.assertEqual(report["ws65g_by_chapter"], {
            "Birdsong": 8, "By the Stream": 17,
            "Forest Discoveries": 12, "Quiet Clearings": 14,
            "Our Solar System": 17, "Flower Borders": 14,
        })
        self.assertTrue(all("NOT_INDEPENDENT_WORD_VALIDATION" in x["research_type"] for x in rows))
        self.assertTrue(all(not x["owner_public_approval"] and not x["candidate_human_decision"] for x in rows))
        self.assertTrue(all(x["source_rights"] == "NOT_CLEARED" for x in rows))

    def test_pair_risks_and_board_eligibility_constraints(self):
        rows, report = build(ROOT)
        by_word = {(r["chapter"], r["answer"]): r for r in rows}
        self.assertEqual(by_word["Our Solar System", "VALLESMARINERIS"]["board_15_eligibility_only"], "False")
        self.assertEqual(by_word["Flower Borders", "FLOWERBORDER"]["board_12_eligibility_only"], "False")
        self.assertTrue(any(x["answer"] == "FOXGLOVE" for x in report["cross_book_same_spelling_reuse"]))
        self.assertTrue(any(x["chapter"] == "Birdsong" and x["shorter"] == "NEST"
                            and x["longer"] == "NESTLING"
                            for x in report["within_chapter_substring_pairs"]))
        self.assertTrue(any(x["chapter"] == "Birdsong" and x["shorter"] == "WING"
                            and x["longer"] == "WINGSPAN"
                            for x in report["within_chapter_substring_pairs"]))
        self.assertTrue(len(report["potential_concept_overlap_groups_for_editor"]) >= 5)

    def test_exact_input_and_stable_results_without_catalog_mutation(self):
        rows = read_82(ROOT)
        self.assertEqual(len(rows), 82)
        paths = [
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
            ROOT / "data/lexicon/intake/ws65f-player-first-shortlist-v1.tsv",
            ROOT / "data/lexicon/intake/ws65g-remaining-player-context-v1.tsv",
        ]
        before = [p.read_bytes() for p in paths]
        original = build(ROOT)
        self.assertEqual(original, build(ROOT))
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", tmp]), 0)
            r = json.loads((Path(tmp) / "editorial-coverage-and-page-risk.json").read_text())
            self.assertEqual(r, original[1])
            self.assertTrue((Path(tmp) / "unsigned-remaining-82.csv").exists())
        self.assertEqual(before, [p.read_bytes() for p in paths])


if __name__ == "__main__":
    unittest.main()
