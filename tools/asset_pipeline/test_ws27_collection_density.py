"""WS27: screenshot-based first-screen density without sacrificing controls."""
import unittest
from pathlib import Path

APP = Path(__file__).resolve().parents[2] / "app/src/main/java/com/playereden/gridmates/wordsearch"

class CollectionDensityTests(unittest.TestCase):
    def test_real_profile_edit_stays_available_and_large_font_falls_back(self):
        src=(APP/"CollectionScreen.kt").read_text()
        self.assertIn('testTag("ws27-compact-collection-identity")',src)
        self.assertIn("val roomy = fontScale < 1.3f",src)
        self.assertIn("if (roomy)",src)
        self.assertIn("Large type remains stacked",src)
        self.assertEqual(src.count('testTag("ws16bu-edit-profile-name")'),2)
        self.assertEqual(src.count("showNameDialog = true"),2)
        self.assertIn("store.updateDisplayName(draftName)",src)
        self.assertIn("min = 48.dp",src)

    def test_rewards_have_short_heading_and_no_redundant_intro(self):
        src=(APP/"CollectionScreen.kt").read_text()
        self.assertIn('title = "Woodland"',src)
        self.assertIn('" companions unlocked"',src)
        self.assertNotIn("Choose a Woodland companion. More portraits unlock as you play.",src)
        self.assertIn('if (!originalImagesAvailable)',src)
        self.assertIn("New companions unlock through completed Solo and Daily puzzles.",src)
        self.assertIn('testTag("ws16bu-gallery")',src)
        self.assertIn('testTag("ws16bt-collection-progress")',src)

    def test_collection_unlocks_and_gallery_layout_remain_reachable(self):
        src=(APP/"CollectionScreen.kt").read_text()
        for marker in (
            'listOfNotNull("Portraits", "Fonts", "Achievements", bookAlbum?.let { "Albums" })',
            'for (label in tabs)',
            'if (collectionTab == "Albums" && bookAlbum != null)',
            "BookAlbumContent(bookAlbum)",
            "maxWidth < 320.dp || fontScale >= 1.3f",
            "if (columns == 2)",
            "PortraitTile(item, remaining",
            "PortraitCard(item, remaining",
            "WoodlandCollection.remaining(",
            "store.equipPortrait(item.portrait.id)",
            "store.equipBoardFont(item.font.id)",
            "CollectionPager(current, portraitPages",
            "CollectionPager(current, pages, id = \"achievements\"",
        ):
            self.assertIn(marker,src)

if __name__ == "__main__":
    unittest.main()
