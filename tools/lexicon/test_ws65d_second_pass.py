#!/usr/bin/env python3
"""WS65-D independent evidence second pass; no release approvals."""
from pathlib import Path
import json
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws65d_second_pass import build, read_second_pass, main


class Ws65dSecondPassTests(unittest.TestCase):
    def test_source_coverage_and_read_only_status(self):
        rows, r = build(ROOT)
        self.assertEqual(len(rows), 267)
        self.assertEqual(r["prior_ws65c_evidence_rows"], 38)
        self.assertEqual(r["new_ws65d_source_referenced_rows"], 36)
        self.assertEqual(r["combined_evidence_rows"], 74)
        self.assertEqual(r["source_rows_without_individual_evidence"], 193)
        self.assertEqual(r["ws65d_by_original_lane"], {"HOLD": 30, "MOVE": 6})
        self.assertEqual(r["remaining_unchecked_by_preflight_lane"],
                         {"HOLD": 31, "MOVE": 0, "PROMISING": 162})
        self.assertTrue(r["original_26_moves_all_have_source_references"])
        self.assertEqual(r["new_qa_editor_approvals"], 0)
        self.assertEqual(r["new_owner_public_approvals"], 0)
        self.assertEqual(r["new_playable_android_words"], 0)
        self.assertEqual(r["candidate_new_answer_spellings_not_approved"], 200)

    def test_earlier_bad_move_is_reconsidered_without_rewriting_original(self):
        rows, r = build(ROOT)
        values = {(v["source_pool"], v["answer"]): v for v in rows}
        sh = values[("WS64", "SHORELINE")]
        self.assertEqual(sh["preflight_lane"], "MOVE")
        self.assertEqual(sh["ws65d_action"], "RECONSIDER_MOVE")
        self.assertEqual(sh["ws65d_suggested_chapter"], "By the Stream")
        self.assertEqual(sh["qa_editor_decision"], "")
        self.assertEqual(sh["owner_decision"], "")
        self.assertEqual(values[("WS65", "GAURA")]["ws65d_action"], "CLARIFY_SENSE")
        self.assertEqual(values[("WS65", "VESTA")]["ws65d_action"], "SPECIALIST_SENSE_CONFIRMED")
        self.assertEqual(values[("WS65", "MERCURY")]["ws65d_evidence_status"], "NOT_IN_THIS_PASS")

    def test_exact_source_and_rights_boundary(self):
        evidence = read_second_pass(ROOT)
        self.assertEqual(len(evidence), 36)
        self.assertEqual(sum(x["evidence_action"] == "RECONSIDER_MOVE"
                             for x in evidence.values()), 1)
        self.assertEqual(sum(x["evidence_action"] == "REAFFIRM_MOVE"
                             for x in evidence.values()), 5)
        for x in evidence.values():
            self.assertEqual(x["source_rights"], "NOT_CLEARED")
            self.assertEqual(x["qa_editor_decision"], "")
            self.assertEqual(x["owner_decision"], "")

    def test_reproducibility_and_old_data_does_not_change(self):
        paths = [
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
            ROOT / "data/lexicon/intake/ws65c-source-evidence-v1.tsv",
            ROOT / "data/lexicon/intake/ws65d-second-pass-evidence-v1.tsv",
        ]
        originals = [p.read_bytes() for p in paths]
        first = build(ROOT)
        self.assertEqual(first, build(ROOT))
        with tempfile.TemporaryDirectory() as tmp:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", tmp]), 0)
            receipt = json.loads((Path(tmp) / "second-pass-evidence-receipt.json").read_text())
            self.assertEqual(receipt, first[1])
            self.assertTrue((Path(tmp) / "unsigned-second-pass-review.csv").exists())
        self.assertEqual(originals, [p.read_bytes() for p in paths])


if __name__ == "__main__":
    unittest.main()
