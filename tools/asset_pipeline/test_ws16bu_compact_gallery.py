"""WS16BU: compact portraits, consistent pagination, readable states."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
class CompactCollectionTests(unittest.TestCase):
    def test_paging_and_columns_share_actual_content_width(self):
        src=(APP/"CollectionScreen.kt").read_text()
        self.assertNotIn("LocalConfiguration.current.screenWidthDp",src)
        self.assertIn('BoxWithConstraints(Modifier.fillMaxWidth().testTag("ws16bu-gallery"))',src)
        self.assertIn("val columns = if (maxWidth < 320.dp || fontScale >= 1.3f) 1 else 2",src)
        self.assertIn("val portraitPageSize = if (columns == 1) 3 else 4",src)
        self.assertIn("val portraitPages = ((portraits.size + portraitPageSize - 1) /",src)
        self.assertIn("portraits.drop(current * portraitPageSize).take(portraitPageSize)",src)
        self.assertIn('CollectionPager(current, portraitPages, id = "portraits"',src)
        self.assertIn('testTag("ws16bu-portrait-" + item.portrait.id)',src)
    def test_compact_cards_keep_live_unlock_and_equipped_behavior(self):
        src=(APP/"CollectionScreen.kt").read_text()
        for needle in (
            "PortraitBadge(item.portrait.id, item.portrait.label, 54, item.unlocked)",
            "if (item.unlocked && !item.equipped)",
            "store.equipPortrait(item.portrait.id)",
            "if (item.equipped) Forest else Color(0xFFDFE0D6)",
            "WoodlandCollection.remaining(",
            "profile, item.portrait.id) ?: 0",
            "PaperProgress(completed, item.portrait.requiredCompletions)",
            'item.equipped -> "Equipped"',
            'item.unlocked -> "Unlocked · Ready to equip"',
            'Text("Equip")',
            'testTag("ws16bu-equip-" + item.portrait.id)',
            "heightIn(min = 48.dp)",
            "store.updateDisplayName(draftName)",
            'Text("Edit display name", color = Forest)',
            'testTag("ws16bt-collection-progress")'
        ):
            self.assertIn(needle,src)
    def test_single_collection_title_and_unwrapped_profile_metrics(self):
        src=(APP/"CollectionScreen.kt").read_text()
        shell=(APP/"GridMatesShell.kt").read_text()
        profile=(APP/"Ws16CProfileHub.kt").read_text()
        self.assertNotIn('StorybookHeading(\n                eyebrow = "Your profile & rewards"',src)
        self.assertIn('title = if (page == Page.COLLECTION) "Your Collection" else page.label',shell)
        self.assertIn("Earn portraits and fonts by finishing puzzles. Everything is free.",src)
        self.assertIn('testTag("ws16bu-profile-completed")',profile)
        self.assertIn('testTag("ws16bu-profile-words")',profile)
        self.assertNotIn("puzzles completed · ",profile)
        for route in ('ws16c-profile-collection','ws16c-profile-records',
                      'ws16c-profile-my-puzzles'):
            self.assertIn(route,profile)
if __name__=="__main__":
    unittest.main()
