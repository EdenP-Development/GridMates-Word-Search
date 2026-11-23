#!/usr/bin/env python3
"""WS66-A fixture cannot drift from original unsigned candidate sources."""
import json
from pathlib import Path
import tempfile
import unittest

from ws63_quality_audit import ROOT
from ws66a_test_fixture_guard import main, verify


class Ws66aKotlinFixtureGuardTests(unittest.TestCase):
    def test_fixture_matches_89_original_proposals_and_exact_tiers(self):
        receipt = verify(ROOT)
        self.assertEqual(receipt["test_fixture_proposal_occurrences"], 89)
        self.assertEqual(receipt["chapters"], {
            "Flower Borders": 30,
            "Our Solar System": 33,
            "Quiet Clearings": 26,
        })
        self.assertEqual(receipt["familiar_by_chapter"], {
            "Flower Borders": 29,
            "Our Solar System": 9,
            "Quiet Clearings": 15,
        })
        self.assertEqual(receipt["solar_relaxed12_familiar_shortage_at_least"], 3)
        self.assertEqual(receipt["new_owner_public_approvals"], 0)
        self.assertEqual(receipt["new_android_playable_answers"], 0)

    def test_fixture_integrity_determinism_and_no_authoring_write(self):
        paths = [
            ROOT / "data/lexicon/authoring/en-v1/words_candidate.csv",
            ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv",
            ROOT / "core/src/test/resources/ws66a/qa-only-v5-candidate-pools.tsv",
        ]
        before = [p.read_bytes() for p in paths]
        source = verify(ROOT)
        self.assertEqual(source, verify(ROOT))
        with tempfile.TemporaryDirectory() as out:
            self.assertEqual(main(["--repo-root", str(ROOT), "--out-dir", out]), 0)
            self.assertEqual(
                json.loads((Path(out) / "trial-fixture-integrity.json").read_text()),
                source,
            )
        self.assertEqual([p.read_bytes() for p in paths], before)


if __name__ == "__main__":
    unittest.main()
