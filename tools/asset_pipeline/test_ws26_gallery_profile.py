"""WS26: two-column collectible gallery, safe large-type fallback and dossier."""
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"

class GalleryProfileTests(unittest.TestCase):
    def test_gallery_adapts_to_content_width_and_font_scale(self):
        source = (APP / "CollectionScreen.kt").read_text()
        for expected in (
            "maxWidth < 320.dp || fontScale >= 1.3f",
            "val portraitPageSize = if (columns == 1) 3 else 4",
            "portraits.drop(current * portraitPageSize).take(portraitPageSize)",
            "if (columns == 2) {",
            "PortraitTile(item, remaining, Modifier.weight(1f)",
            "PortraitCard(item, remaining, Modifier.weight(1f)",
            "CollectionPager(current, portraitPages",
            'testTag("ws26-portrait-tile-content")',
        ):
            self.assertIn(expected, source)

    def test_equips_and_locks_remain_live_without_new_profile_fields(self):
        source = (APP / "CollectionScreen.kt").read_text()
        tile = source.split("private fun PortraitTile(",1)[1].split("private fun PortraitCard(",1)[0]
        for phrase in (
            "PortraitBadge(item.portrait.id, item.portrait.label, 68, item.unlocked)",
            "if (item.unlocked && !item.equipped)",
            "if (!item.unlocked && item.portrait.requiredCompletions > 0)",
            "PaperProgress(earned, item.portrait.requiredCompletions)",
            "onClick = onEquip",
            'testTag("ws16bu-equip-" + item.portrait.id)',
            "heightIn(min = 48.dp)",
            "if (item.equipped) \"Equipped\" else \"Locked\"",
        ):
            self.assertIn(phrase, tile)
        self.assertIn("store.equipPortrait(item.portrait.id)", source)
        self.assertIn("store.updateDisplayName(draftName)", source)

    def test_dossier_is_derived_from_existing_profile_and_uses_real_routes(self):
        profile = (APP / "Ws16CProfileHub.kt").read_text()
        for phrase in (
            "WoodlandCollection.progress(profile).count { it.unlocked }",
            "WordSearchAchievements.progress(profile).count { it.unlocked }",
            'testTag("ws26-profile-collection-progress")',
            "PaperProgress(unlockedCompanions, WoodlandCollection.portraits.size",
            "onClick = onCollection",
            "onClick = onRecords",
            "onClick = onMyPuzzles",
            "Ws15SectionGlyph.PORTRAIT",
            "Ws15SectionGlyph.AWARD",
            "Ws15SectionGlyph.JOURNAL",
            "Your profile and progress stay on this device",
        ):
            self.assertIn(phrase,profile)

if __name__ == "__main__":
    unittest.main()
