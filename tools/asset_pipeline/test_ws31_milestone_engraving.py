"""WS31: higher milestone decoration must not alter earned/progress rules."""
import unittest
from pathlib import Path
ROOT = Path(__file__).resolve().parents[2]
COLLECTION = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/CollectionScreen.kt"
MODEL = ROOT / "core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/CollectionProgress.kt"

class MilestoneEngravingTests(unittest.TestCase):
    def test_four_milestones_only(self):
        s = COLLECTION.read_text()
        self.assertIn('id in setOf("twenty_puzzles", "seven_dailies", "hard_solo", "all_themes")', s)
        self.assertIn('Ws31MilestoneEngraving(', s)
        self.assertIn('testTag("ws31-milestone-engraving-" + id)', s)
        self.assertIn('if (earned) Color(0xFF956B2C) else Color(0xFF9FA59A)', s)
        self.assertIn('Ws30AchievementSymbol(motif, earned', s)
        self.assertIn('Ws29AchievementMedal(item.id, item.unlocked)', s)
        self.assertIn('PaperProgress(item.current, item.target)', s)
    def test_unchanged_achievement_model(self):
        s = MODEL.read_text()
        self.assertEqual(s.count('AchievementDefinition("'), 8)
        for k in ("first_puzzle", "five_puzzles", "twenty_puzzles",
                  "one_daily", "seven_dailies", "no_hints", "hard_solo", "all_themes"):
            self.assertIn('AchievementDefinition("'+k+'"',s)

if __name__ == "__main__":
    unittest.main()
