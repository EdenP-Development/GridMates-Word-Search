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
if __name__=="__main__":
    unittest.main()
