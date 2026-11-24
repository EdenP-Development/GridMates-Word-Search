"""WS30 screenshot follow-up: no Android emoji color leakage for locked medals."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
COLLECTION = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/CollectionScreen.kt"
MODEL = ROOT / "core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/CollectionProgress.kt"

class MedalSymbolTests(unittest.TestCase):
    def test_four_vector_families_replace_platform_unicode_emoji(self):
        src=COLLECTION.read_text()
        section=src.split("private fun Ws30AchievementSymbol(",1)[1]
        for needle in (
            'Canvas(modifier)',
            'drawCircle(ink',
            'drawLine(ink,',
            'drawPath(p, ink)',
            '"sun" ->',
            '"flower" ->',
            '"sparkle" ->',
            'else -> {',
            'if (earned) Color(0xFF875C1C) else Color(0xFF798376)',
        ):
            self.assertIn(needle,section)
        card=src.split("internal fun Ws29AchievementMedal(",1)[1].split(
            "private fun Ws30AchievementSymbol(",1)[0]
        self.assertIn("Ws30AchievementSymbol(motif, earned",card)
        self.assertIn('testTag("ws30-medal-symbol-" + id)',card)
        self.assertNotIn("Text(motif",card)
        for color_emoji in ("☀","✦","✿","★"):
            self.assertNotIn(color_emoji, card)

    def test_unlocks_and_progress_preserved(self):
        src=COLLECTION.read_text()
        model=MODEL.read_text()
        for entry in ("first_puzzle","five_puzzles","twenty_puzzles",
                      "one_daily","seven_dailies","no_hints","hard_solo","all_themes"):
            self.assertIn('AchievementDefinition("'+entry+'"',model)
        for needle in (
            'testTag("ws16bt-achievement-" + item.id)',
            'testTag("ws29-achievement-medal-" + id)',
            'Ws29AchievementMedal(item.id, item.unlocked)',
            'PaperProgress(item.current, item.target)',
            'CollectionPager(current, pages, id = "achievements"',
            '" more to earn this achievement"',
        ):
            self.assertIn(needle,src)

if __name__=="__main__":
    unittest.main()
