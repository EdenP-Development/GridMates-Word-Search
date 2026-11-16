"""WS31: source-backed forest milestone shields and save-safe illustrated My Puzzles."""
import hashlib
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
PACK = ROOT / "design/art-library-v2/ws21"

class MilestoneAndSavedPuzzleTests(unittest.TestCase):
    def test_forest_shield_is_existing_hash_matched_editable_master(self):
        manifest = json.loads((PACK / "manifest.json").read_text(encoding="utf-8"))
        matches = [s for s in manifest["sprites"]
                   if s["id"] == "gm_core_ws21_achievement_slot_forest"]
        self.assertEqual(len(matches), 1)
        master = matches[0]
        source = PACK / master["source"]
        self.assertEqual(hashlib.sha256(source.read_bytes()).hexdigest(),
                         master["details"]["editable_svg_sha256"])
        with tempfile.TemporaryDirectory() as folder:
            script = ROOT / "tools/asset_pipeline/render_ws29_achievement_medals.py"
            subprocess.run([sys.executable, str(script), "--output-dir", folder],
                           capture_output=True, text=True, check=True)
            for tone in ("gold", "cream", "forest"):
                output = (Path(folder) / f"gm_ws29_achievement_{tone}.png").read_bytes()
                self.assertEqual(output[:8], bytes([137,80,78,71,13,10,26,10]))
                self.assertGreater(len(output), 5000)
                self.assertFalse((ROOT / "app/src/release/res/drawable-nodpi" /
                                 f"gm_ws29_achievement_{tone}.png").exists())

    def test_preexisting_eight_rewards_and_exact_thresholds_survive(self):
        src = (APP / "CollectionScreen.kt").read_text()
        rules = (ROOT / "core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/CollectionProgress.kt").read_text()
        self.assertIn('setOf("twenty_puzzles", "seven_dailies", "all_themes")',src)
        self.assertIn('"gm_ws29_achievement_forest"',src)
        self.assertIn('testTag("ws31-milestone-label-" + item.id)',src)
        self.assertIn('id in Ws31MilestoneAchievements',src)
        for phrase in (
            'AchievementDefinition("first_puzzle", "First Page", "Complete your first puzzle.", 1)',
            'AchievementDefinition("five_puzzles", "Puzzle Regular", "Complete five puzzles.", 5)',
            'AchievementDefinition("twenty_puzzles", "Puzzle Keeper", "Complete twenty puzzles.", 20)',
            'AchievementDefinition("seven_dailies", "Seven Good Days", "Finish seven dated puzzles. No streak required.", 7)',
            'AchievementDefinition("all_themes", "Three Chapters", "Complete a Solo puzzle in Woodland, Space and Garden.", 3)',
        ):
            self.assertIn(phrase,rules)
        self.assertIn('PaperProgress(item.current, item.target)',src)

    def test_active_and_paused_save_routes_keep_original_validation(self):
        src = (APP / "GridMatesShell.kt").read_text()
        for phrase in (
            'testTag("ws31-current-solo-card")',
            'testTag("ws31-empty-paused")',
            'testTag("ws31-paused-solo-" + run.puzzleId)',
            'GridMatesFrameVariant.FEATURE',
            'GridMatesFrameVariant.MEDIUM',
            'Ws15SectionGlyph.JOURNAL',
            'store.discardPausedSolo(removeRun.puzzleId)',
            'if (store.discardPausedSolo(removeRun.puzzleId)) removeId = null',
            'onResume(active.puzzleId)',
            'onResume(run.puzzleId)',
            'PaperProgress(run.foundIds.size, puzzle.targets.size)',
            'com.playereden.gridmates.wordsearch.core.SoloSessionLibrary.finished(active)',
            'testTag("ws31-current-resume")',
            'testTag("ws31-paused-resume-" + run.puzzleId)',
            'testTag("ws31-paused-remove-" + run.puzzleId)',
            'title = "No paused puzzles yet"',
            'Text("Keep puzzle")',
        ):
            self.assertIn(phrase,src)
        self.assertIn('Page.MY_PUZZLES -> SavedSoloScreen(soloStore,',src)
        self.assertIn('onNewSolo = { openSoloSetup(Page.MY_PUZZLES) }',src)
        self.assertIn('soloStore.resumeSolo(id)',src)

if __name__ == "__main__":
    unittest.main()
