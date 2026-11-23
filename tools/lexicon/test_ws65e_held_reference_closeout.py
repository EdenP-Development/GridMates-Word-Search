#!/usr/bin/env python3
"""WS65-E check 31 last held rows, identity-preserving friendly alternatives."""
from pathlib import Path
import json
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65e_held_reference_closeout import load_input, build, main


class Ws65eHeldReferenceTests(unittest.TestCase):
    def test_exhaustive_31_hold_reference_queue_and_status(self):
        rows, aliases, receipt = build(ROOT)
        self.assertEqual(receipt["new_held_rows_with_source_context"], 31)
        self.assertEqual(receipt["previous_source_referenced_rows"], 74)
        self.assertEqual(receipt["cumulative_research_references"], 105)
        self.assertEqual(receipt["all_source_proposals_unchanged"], 267)
        self.assertEqual(receipt["still_without_individual_reference"], 162)
        self.assertEqual(receipt["remaining_unreferenced_by_preflight_lane"],
                         {"HOLD": 0, "MOVE": 0, "PROMISING": 162})
        self.assertEqual(receipt["new_evidence_scope"],
                         {"DIRECT_WORD_SENSE": 29, "DISPLAY_REVIEW": 1, "WORD_FORM_HOLD": 1})
        self.assertEqual(receipt["new_editor_approved_words"], 0)
        self.assertEqual(receipt["new_owner_public_approvals"], 0)
        self.assertEqual(receipt["new_android_activated_words"], 0)
        self.assertEqual(receipt["candidate_new_spellings_not_reviewed_or_approved"], 200)
        self.assertEqual(len(rows), 267)
        self.assertEqual(len(aliases), receipt["friendly_label_ideas"])

    def test_hold_unresolved_word_form_is_not_mislabeled_as_release_ready(self):
        rows, _, _ = build(ROOT)
        index = {(x["source_pool"], x["answer"]): x for x in rows}
        self.assertEqual(index[("WS64", "RIVERSTONE")]["ws65e_scope"], "WORD_FORM_HOLD")
        self.assertEqual(index[("WS64", "WILDLIFESIGNS")]["ws65e_scope"], "DISPLAY_REVIEW")
        self.assertEqual(index[("WS65", "CHARON")]["ws65e_scope"], "DIRECT_WORD_SENSE")
        self.assertEqual(index[("WS65", "MONARDA")]["ws65e_common_name_idea"], "Bee Balm")
        self.assertEqual(index[("WS65", "MERCURY")]["ws65e_evidence_status"], "NOT_IN_THIS_PASS")
        self.assertTrue(all(x["owner_decision"] == "" and x["qa_editor_decision"] == "" for x in rows))

    def test_alias_display_and_new_word_are_distinct(self):
        _, aliases, _ = build(ROOT)
        x = {a["original_answer"]: a for a in aliases}
        self.assertEqual(x["SHASTADAISY"]["idea_classification"], "SAME_NORMALIZED_DISPLAY")
        self.assertEqual(x["MONARDA"]["idea_classification"],
                         "SEPARATE_CANONICAL_CANDIDATE_NOT_ADDED")
        self.assertEqual(x["MONARDA"]["normalized_answer"], "BEEBALM")
        self.assertEqual(x["RIVERSTONE"]["normalized_answer"], "RIVERSTONE")
        self.assertTrue(all(a["approval"] == "NONE" for a in aliases))

    def test_exact_count_cannot_be_bumped_by_duplicate(self):
        rows = load_input(ROOT)
        self.assertEqual(len(rows), 31)
        self.assertEqual(len(set(rows)), 31)
        self.assertTrue(all(x["rights_status"] == "NOT_CLEARED" for x in rows.values()))

    def test_report_byte_determinism_and_original_inputs_unchanged(self):
        paths = [
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
            ROOT / "data/lexicon/intake/ws65d-second-pass-evidence-v1.tsv",
            ROOT / "data/lexicon/intake/ws65e-final-holds-evidence-v1.tsv",
        ]
        before = [x.read_bytes() for x in paths]
        first = build(ROOT)
        self.assertEqual(first, build(ROOT))
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", tmp]), 0)
            report = json.loads((Path(tmp) / "research-receipt.json").read_text())
            self.assertEqual(report, first[2])
            self.assertTrue((Path(tmp) / "full-unsigned-candidate-ledger.csv").exists())
            self.assertTrue((Path(tmp) / "friendly-wording-ideas.csv").exists())
        self.assertEqual(before, [x.read_bytes() for x in paths])


if __name__ == "__main__":
    unittest.main()
