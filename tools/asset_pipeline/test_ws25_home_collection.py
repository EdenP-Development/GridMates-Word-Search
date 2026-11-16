"""WS25 Home + Collection: artwork-source and state-preservation checks."""
import hashlib
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
PACK = ROOT / "design/art-library-v2/ws22"


class HomeCollectionIntegrationTests(unittest.TestCase):
    def test_real_motif_source_and_debug_only_import(self):
        manifest = json.loads((PACK / "manifest.json").read_text())
        asset = next(e for e in manifest["entries"]
                     if e["id"] == "gm_theme_woodland_robin_motif")
        original = PACK / asset["path"]
        self.assertEqual(hashlib.sha256(original.read_bytes()).hexdigest(),
                         asset["sha256"])
        importer = ROOT / "tools/asset_pipeline/import_ws25_collection_accent.py"
        with tempfile.TemporaryDirectory() as directory:
            out = Path(directory) / "robin.png"
            subprocess.run([sys.executable, str(importer), "--output", str(out)],
                           check=True, capture_output=True)
            self.assertEqual(out.read_bytes(), original.read_bytes())
        self.assertFalse((ROOT / "app/src/release/res/drawable-nodpi" /
                          "gm_ws25_collection_robin.png").exists())

    def test_collection_selection_and_unlock_logic_survive(self):
        src = (APP / "CollectionScreen.kt").read_text()
        for marker in (
            "GridMatesFrameVariant.GREEN_ICON",
            'testTag("ws25-collection-tab-" + label.lowercase())',
            "onClick = { collectionTab = label }",
            "selected = isSelected",
            "WoodlandCollectionAccent()",
            "gm_ws25_collection_robin",
            "if (resource != 0)",
            "store.equipPortrait(item.portrait.id)",
            "store.equipBoardFont(item.font.id)",
            'testTag("ws16bu-gallery")',
            'testTag("ws16bv-font-gallery")',
            'testTag("ws16bt-collection-progress")',
            "CollectionPager(current, portraitPages",
            "BoardFontPreview(item.font.id)",
        ):
            self.assertIn(marker, src)

    def test_home_progress_is_live_and_safe(self):
        home = (APP / "Ws16HomeScreen.kt").read_text()
        self.assertIn('testTag("ws25-home-book-progress")', home)
        self.assertIn("PaperProgress(count, pages,", home)
        self.assertIn("BookProgressRules.completedCount(catalog, bookProgress",
                      home)
        self.assertIn('onCoverTap = { index, focused ->', home)
        self.assertIn("else scope.launch { pager.animateScrollToPage(index) }",
                      home)


if __name__ == "__main__":
    unittest.main()
