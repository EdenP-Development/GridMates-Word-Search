"""WS16BV: compact bundled font cards and live native controls."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
CORE=ROOT/"core/src/main/kotlin/com/playereden/gridmates/wordsearch/core"
class CompactFontsTests(unittest.TestCase):
    def test_four_fonts_and_license_access_are_not_lost(self):
        src=(APP/"CollectionScreen.kt").read_text()
        defs=(CORE/"BoardFontCollection.kt").read_text()
        register=(APP/"BoardFontRendering.kt").read_text()
        for font_id in ("atkinson","plex","dmmono","patrick"):
            self.assertIn('"'+font_id+'"',defs)
            self.assertIn('"'+font_id+'" to R.font.',register)
        for phrase in ("font_licenses/", "_ofl.txt", "· Open Font License",
                       'onLicense = { licenseFontId = item.font.id }',
                       "store.equipBoardFont(item.font.id)",
                       "BoardFontPreview(item.font.id)",
                       "Every font is bundled for offline use"):
            self.assertIn(phrase,src)
    def test_compact_preview_and_real_equipped_locked_actions(self):
        src=(APP/"CollectionScreen.kt").read_text()
        for phrase in ('testTag("ws16bv-font-gallery")',
                       "maxWidth < 600.dp || fontScale >= 1.3f",
                       'testTag("ws16bv-font-" + item.font.id)',
                       'testTag("ws16bv-font-license-" + item.font.id)',
                       'testTag("ws16bv-font-equip-" + item.font.id)',
                       'testTag("ws16bv-font-status-" + item.font.id)',
                       "if (item.unlocked && !item.equipped)",
                       "else if (!item.unlocked)",
                       'Text("Locked", color = Ink',
                       'Text("Equipped", color = Forest',
                       'Text("License", color = Forest)',
                       'if (item.equipped) 2.dp else 1.dp',
                       "heightIn(min = 48.dp)"):
            self.assertIn(phrase,src)
        self.assertNotIn('enabled = item.unlocked && !item.equipped',src)
        self.assertNotIn('Text(if (item.equipped) "Selected"',src)
    def test_profile_heading_and_routes(self):
        profile=(APP/"Ws16CProfileHub.kt").read_text()
        self.assertIn('Text("Explore", color = Forest,',profile)
        self.assertNotIn('Text("Your profile", color = Forest,',profile)
        for tag in ("ws16c-profile-collection","ws16c-profile-records","ws16c-profile-my-puzzles"):
            self.assertIn('testTag("'+tag+'")',profile)
if __name__=="__main__":
    unittest.main()
