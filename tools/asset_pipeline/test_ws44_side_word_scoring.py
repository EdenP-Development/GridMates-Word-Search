"""WS44 scoped non-monetary scoring / save / release-data guard.

Source guard supplements Kotlin unit and compiled native Android tests.
"""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
SRC=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
CORE=ROOT/"core/src/main/kotlin/com/playereden/gridmates/wordsearch/core"

class SideWordScoringContract(unittest.TestCase):
    def test_pure_rules_exclude_target_duplicates_and_cap(self):
        text=(CORE/"BonusWordRules.kt").read_text()
        for required in (
            "SIDE_WORD_POINTS = 15", "TARGET_POINTS = 100",
            "COMPLETION_POINTS = 100", "MAX_SIDE_WORDS = 12",
            "WordSearchRules.line(start, end, puzzle.size)",
            "puzzle.targets.any { it.word == traced }",
            "traced !in dictionary",
            "BonusClaimStatus.ALREADY_FOUND",
            "BonusClaimStatus.FULL",
            "validateSaved",
            "existsInBoard(puzzle, word)",
        ):
            self.assertIn(required,text)
    def test_old_saves_and_existing_claims_remain_intact(self):
        model=(CORE/"SoloProgress.kt").read_text()
        store=(SRC/"SoloStore.kt").read_text()
        for m in ('val bonusWords: Set<String> = emptySet()',
                  'val bonusWordCount: Int = 0',
                  'BonusWordRules.validateSaved(puzzle, run.bonusWords)',
                  'bonusWordCount = run.bonusWords.size'):
            self.assertIn(m,model)
        for m in ('require(schema in 1..6)',
                  '.put("schema", 6)',
                  'if (run.bonusWords.isNotEmpty())',
                  'data.put("sideWords", JSONArray(run.bonusWords.sorted()))',
                  'if (r.bonusWordCount > 0) put("extraWords", r.bonusWordCount)',
                  'commitSession(updated)',
                  'fun claimSideWord(start: GridPoint, end: GridPoint, daily: Boolean)',
                  'if (dailyEntry?.completed == true) return invalid'):
            self.assertIn(m,store)
        self.assertIn("if (claim.status == ClaimStatus.FOUND",store)
        self.assertIn('QuickPlayContent.mixed?.words?',store)
        self.assertIn('WordTheme.entries.flatMap { it.words }',store)
        self.assertIn("val mixed: ReviewedWordPack? = null",
            (ROOT/"app/src/release/java/com/playereden/gridmates/wordsearch/QuickPlayContent.kt").read_text())
    def test_ui_and_native_tests_track_bonus_and_original_targets(self):
        screen=(SRC/"WordSearchScreen.kt").read_text()
        for m in ('ClaimStatus.INVALID -> {',
                  'store.claimSideWord(start, end, daily)',
                  'BonusClaimStatus.FOUND',
                  'BonusWordRules.points(',
                  'Score ', 'bonus words'):
            self.assertIn(m,screen)
        self.assertIn("record.points",(SRC/"SoloRecordsScreen.kt").read_text())
        self.assertIn("run.bonusWords.size",(SRC/"PuzzleResultShare.kt").read_text())
        test=(ROOT/"app/src/androidTest/java/com/playereden/gridmates/wordsearch/SideWordScoringAndroidTest.kt").read_text()
        self.assertIn("realQuickPlayBonusPersistsAcrossRestartBackupAndDuplicateTrace",test)
        self.assertIn("store.claimSideWord",test)
        self.assertIn("imported.restoreBackup",test)

if __name__=="__main__":
    unittest.main()
