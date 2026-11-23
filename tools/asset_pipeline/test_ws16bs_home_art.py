"""WS16BS: original asset provenance, source hashing, debug-only output."""
import hashlib,json,struct,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
REG=ROOT/"design/visual-system/ws16bs-art-register.json"
OUT=ROOT/"app/src/debug/assets/ws16bs"
def git_oid(raw):
    return hashlib.sha1(b"blob "+str(len(raw)).encode()+b"\0"+raw).hexdigest()
class HomeArtTests(unittest.TestCase):
    def test_original_painterly_png_is_verified_without_release_approval(self):
        reg=json.loads(REG.read_text())
        receipt=json.loads((OUT/"manifest.json").read_text())
        raw=(ROOT/reg["source"]["path"]).read_bytes()
        png=(OUT/receipt["filename"]).read_bytes()
        self.assertTrue(reg["debug_only"])
        self.assertFalse(reg["owner_approved"])
        self.assertFalse(reg["release_approved"])
        self.assertEqual(git_oid(raw),reg["source"]["git_blob_sha1"])
        self.assertEqual(hashlib.sha256(raw).hexdigest(),receipt["source_sha256"])
        self.assertEqual(hashlib.sha256(png).hexdigest(),receipt["export_sha256"])
        self.assertEqual(struct.unpack(">II",png[16:24]),(640,640))
        self.assertIn(b"radialGradient",raw)
        self.assertNotIn(b"<image",raw)
        self.assertNotIn(b"<text",raw)
    def test_home_only_painted_texture_and_existing_detail_actions(self):
        paper=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch/GridMatesParchmentBackground.kt").read_text()
        detail=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch/PuzzleBookshelfScreen.kt").read_text()
        shell=(ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch/GridMatesShell.kt").read_text()
        self.assertIn('context.assets.open("ws16bs/home-parchment-wash-v1.png")',paper)
        self.assertIn("if (storybookHome) homePatina?.let { tile ->",paper)
        self.assertIn("else GridMatesPaperFinish.QUIET",shell)
        self.assertIn("private fun BookDetailCohesionPanel(",detail)
        self.assertIn("variant = GridMatesFrameVariant.PANEL",detail)
        self.assertIn("PaperPanel(modifier = modifier, content = content)",detail)
        self.assertIn('testTag("book-detail-progress-panel")',detail)
        self.assertIn('testTag("book-detail-page-panel-" + page.id)',detail)
        self.assertIn("onPlayPage(bookId, page.id)",detail)
        self.assertIn("onBookmarkPage(bookId, page.id)",detail)
if __name__=="__main__":
    unittest.main()
