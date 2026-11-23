"""WS16BT: secondary visual cohesion with no change to rewards or records."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
class SecondaryCohesionTests(unittest.TestCase):
    def test_collection_keeps_gallery_selection_and_native_rewards(self):
        src=(APP/"CollectionScreen.kt").read_text()
        self.assertEqual(src.count("GridMatesInfoPanel(Modifier.fillMaxWidth()"),3)
        self.assertIn('testTag("ws16bt-collection-profile")',src)
        self.assertIn('testTag("ws16bt-collection-progress")',src)
        self.assertIn('testTag("ws16bt-achievement-" + item.id)',src)
        for needle in ("store.updateDisplayName(draftName)","store.equipPortrait(item.portrait.id)",
                       "store.equipBoardFont(item.font.id)","item.unlocked && !item.equipped",
                       "PortraitCard(","BoardFontCard(","CollectionPager(","AchievementCard(item)"):
            self.assertIn(needle,src)

    def test_records_reuse_frames_without_truncating_histories(self):
        src=(APP/"SoloRecordsScreen.kt").read_text()
        self.assertEqual(src.count("GridMatesIllustratedFrame("),8)
        for needle in ("journalPageCount(count, pageSize)","profile.raceResults.drop(page * 4).take(4)",
                       "profile.duelResults.drop(page * 4).take(4)",
                       "profile.results.drop(page * 4).take(4)",
                       "val fontScale = LocalDensity.current.fontScale",
                       "fontScale >= 1.8f","maxWidth < 350.dp || fontScale >= 1.3f",
                       'testTag("ws16bt-solo-metrics")','journalTab == "Race"',
                       'journalTab == "Duel"'):
            self.assertIn(needle,src)

    def test_shared_info_panel_and_profile_routes_are_intact(self):
        panel=(APP/"GridMatesInfoPanel.kt").read_text()
        profile=(APP/"Ws16CProfileHub.kt").read_text()
        self.assertIn("content: @Composable ColumnScope.() -> Unit",panel)
        self.assertIn("variant = GridMatesFrameVariant.PANEL",panel)
        self.assertIn("content = content",panel)
        for tag in ("summary","collection","records","my-puzzles"):
            self.assertIn(f'testTag("ws16c-profile-{tag}")',profile)

if __name__=="__main__":
    unittest.main()
