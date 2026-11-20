"""WS45 bonus journal and real Android execution wiring regression guards."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCREEN = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/WordSearchScreen.kt"
RUNNER = ROOT / "scripts/ws45_bonus_emulator.sh"
PIPELINE = ROOT / ".github/workflows/ws01-android-qa.yml"

class BonusJournalGuards(unittest.TestCase):
    def test_optional_journal_discoverability_and_no_extra_target_claims(self):
        src=SCREEN.read_text()
        for needle in (
            'var bonusJournalOpen by rememberSaveable',
            'GridMatesPaperDialog("Extra words"',
            'run.bonusWords.sorted().joinToString(" · ")',
            'if (run.bonusWords.isEmpty()) "No extras discovered yet."',
            '"Extra words (${run.bonusWords.size}/${BonusWordRules.MAX_SIDE_WORDS})"',
            'BonusClaimStatus.ALREADY_FOUND',
            'BonusClaimStatus.FULL',
            'store.claimSideWord(start, end, daily)',
            'PaperProgress(puzzle.targets.size, puzzle.targets.size)',
        ):
            self.assertIn(needle,src)

    def test_real_native_execution_is_requested_not_only_compiled(self):
        runner=RUNNER.read_text()
        for needle in (
            'adb install -r app/build/outputs/apk/debug/app-debug.apk',
            'adb install -r -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk',
            'SideWordScoringAndroidTest',
            'QuickPlayAndroidTest',
            'SoloPersistenceTest',
            'DailyPersistenceTest',
            'GameBackupAndroidTest',
            'SoloSchemaGuardsAndroidTest',
            'adb shell am instrument -w -r',
            'qa-ws45-bonus',
            'FAILURES!!!',
        ):
            self.assertIn(needle,runner)
        yml=PIPELINE.read_text()
        self.assertIn('WS45 Run side-word, Solo, Daily and backup regression on Android',yml)
        self.assertIn('script: bash scripts/ws45_bonus_emulator.sh',yml)
        self.assertIn('GridMates-WS45-Score-Android-Execution',yml)
        self.assertIn('reactivecircus/android-emulator-runner@v2',yml)

if __name__=='__main__':
    unittest.main()
