"""WS28: regression test for actual v89 Profile card overlap."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
PROFILE = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch/Ws16CProfileHub.kt"

class ProfileOverlapTests(unittest.TestCase):
    def test_one_vertical_parent_stacks_the_two_profile_sections(self):
        src = PROFILE.read_text()
        card = src.split('testTag("ws16c-profile-summary")', 1)[1].split('Text("Explore"', 1)[0]
        assert card.index('Column(Modifier.fillMaxWidth().testTag("ws28-profile-layout")') < card.index('testTag("ws28-profile-identity-row")') < card.index('testTag("ws28-profile-progress-section")')
        self.assertIn("verticalArrangement = Arrangement.spacedBy(2.dp)", card)
        self.assertIn("PaperProgress(unlockedCompanions, WoodlandCollection.portraits.size", card)
        self.assertIn('testTag("ws26-profile-collection-progress")', card)

    def test_identity_metrics_and_profile_navigation_remain_local_and_usable(self):
        src = PROFILE.read_text()
        for required in (
            "WoodlandCollection.progress(profile).count { it.unlocked }",
            "WordSearchAchievements.progress(profile).count { it.unlocked }",
            'testTag("ws16bu-profile-completed")',
            'testTag("ws16bu-profile-words")',
            'testTag("ws16c-profile-collection")',
            'testTag("ws16c-profile-records")',
            'testTag("ws16c-profile-my-puzzles")',
            "onClick = onCollection",
            "onClick = onRecords",
            "onClick = onMyPuzzles",
            "Your profile and progress stay on this device",
        ):
            self.assertIn(required, src)

if __name__ == "__main__":
    unittest.main()
