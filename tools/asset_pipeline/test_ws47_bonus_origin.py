"""WS47 source contract for geometric bonus-word provenance and reproducible report."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
CORE=ROOT/"core/src/test/kotlin/com/playereden/gridmates/wordsearch/core"
WORKFLOWS=[ROOT/".github/workflows/ws01-android-qa.yml",
           ROOT/".github/workflows/ws12g-qa-art-lab.yml"]

class BonusOriginAuditContract(unittest.TestCase):
    def test_geometry_not_spelling_only_and_no_runtime_source_edit(self):
        src=(CORE/"SideWordOriginStudy.kt").read_text()
        for required in (
            "TARGET_FRAGMENT_ONLY", "CROSSING_ONLY", "FREE_PATH_AVAILABLE",
            "placedPaths = puzzle.targets.map { it.positions }",
            "isContiguousSegment(placed, path)",
            "isContiguousSegment(placed.asReversed(), path)",
            "path.any { it in occupied }",
            "result[text] = kind",
            "return result.toSortedMap()",
        ):
            self.assertIn(required,src)
        test=(CORE/"SideWordOriginStudyTest.kt").read_text()
        for word in ('"WOLF"','"FLOW"','"DOGS"','"ROSE"',
                     "repeatedIndependentOccurrenceOutranksAWordEmbeddedInsideATarget"):
            self.assertIn(word,test)
    def test_piggyback_on_ws46_unchanged_real_board_set(self):
        src=(CORE/"BonusWordOpportunityAuditTest.kt").read_text()
        for required in (
            "SideWordOriginStudy.classify(puzzle, qaDictionary)",
            "assertEquals(\"Exactly the same eligible QA words as WS46\", qa, origin.keys)",
            "assertEquals(339, allTrials.size)",
            "build/ws46-bonus-opportunities.tsv",
            "build/ws47-bonus-origin.tsv",
            "pct_boards_no_free_path", "pct_words_fragment_only",
            "qa_670_plus_builtins", "builtins_only_release_fallback",
        ):
            self.assertIn(required,src)
    def test_independent_workflows_upload_new_report(self):
        for path in WORKFLOWS:
            src=path.read_text()
            self.assertIn("test_ws47_bonus_origin.py",src)
            self.assertIn("GridMates-WS47-Bonus-Origin-Audit",src)
            self.assertIn("core/build/ws47-bonus-origin.tsv",src)
            self.assertIn("feature/ws47-bonus-origin-and-fragment-audit-2026-11-19",src)

if __name__=="__main__":
    unittest.main()
