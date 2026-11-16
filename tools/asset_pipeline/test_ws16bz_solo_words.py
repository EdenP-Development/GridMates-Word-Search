"""WS16BZ: long Solo word index scrolls without changing boards, Daily, or saves."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
class ContinuousSoloIndexTest(unittest.TestCase):
    def test_selector_and_both_puzzle_orientations(self):
        src=(APP/"WordSearchScreen.kt").read_text()
        self.assertIn('!daily && targetCount >= 12',src)
        self.assertIn('val continuousSoloIndex = scrollSoloWordIndex(daily, puzzle.targets.size)',src)
        self.assertEqual(src.count('scrollAll = continuousSoloIndex'),2)
        self.assertIn('scrollAll = true', (APP/"BookPageGameplayScreen.kt").read_text())
    def test_real_scroll_and_unpaged_all_targets(self):
        src=(APP/"WordSearchScreen.kt").read_text()
        self.assertIn('if (scrollAll) targets.size.coerceAtLeast(1)',src)
        self.assertIn('if (scrollAll) Modifier.verticalScroll(listScroll) else Modifier',src)
        self.assertIn('testTag("word-list-scrollable")',src)
        self.assertIn('testTag("word-list-target-" + target.id)',src)
        self.assertIn('val targets = if (giantWordList)',src)
        self.assertIn('else if (remainingOnly) puzzle.targets.filter { it.id !in found }',src)
        self.assertIn('textDecoration = if (solved)',src)
        self.assertIn('if (pageCount > 1)',src)
    def test_selection_and_save_call_paths_unchanged(self):
        src=(APP/"WordSearchScreen.kt").read_text()
        for text in ('store.dailyClaim(start, end)','store.claim(start, end)',
                     'ZoomableWordGrid(puzzle, found, hintPaths,',
                     'onBookmark = { if (!daily) store.saveLargeBoardBookmark(it) }',
                     'val hintsRemaining = WordHintPolicy.remaining(run, daily)',
                     'store.flush(); onBack()'):
            self.assertIn(text,src)
if __name__=="__main__":
    unittest.main()
