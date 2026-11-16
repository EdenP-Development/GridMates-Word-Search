"""WS29 achievement medals: existing WS21 source art, original earned progress."""
import hashlib
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
PACK = ROOT / "design/art-library-v2/ws21"

class AchievementMedalTests(unittest.TestCase):
    def test_existing_original_ws21_sources_are_hash_verified(self):
        manifest = json.loads((PACK / "manifest.json").read_text())
        for tone in ("gold", "cream"):
            key = f"gm_core_ws21_achievement_slot_{tone}"
            matches = [e for e in manifest["sprites"] if e["id"] == key]
            self.assertEqual(len(matches), 1)
            entry = matches[0]
            actual = hashlib.sha256((PACK / entry["source"]).read_bytes()).hexdigest()
            self.assertEqual(actual, entry["details"]["editable_svg_sha256"])

    def test_builds_real_binary_debug_sprites_and_not_release(self):
        script = ROOT / "tools/asset_pipeline/render_ws29_achievement_medals.py"
        with tempfile.TemporaryDirectory() as folder:
            subprocess.run([sys.executable, str(script), "--output-dir", folder],
                           check=True, capture_output=True, text=True)
            for tone in ("gold", "cream"):
                output = Path(folder) / f"gm_ws29_achievement_{tone}.png"
                data = output.read_bytes()
                self.assertEqual(data[:8], bytes([137, 80, 78, 71, 13, 10, 26, 10]))
                self.assertGreater(len(data), 5000)
                release = ROOT / "app/src/release/res/drawable-nodpi" / output.name
                self.assertFalse(release.exists())

    def test_all_achievement_states_and_8_orig_ids_preserved(self):
        source = (SRC / "CollectionScreen.kt").read_text()
        model = (ROOT / "core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/CollectionProgress.kt").read_text()
        for id in ("first_puzzle","five_puzzles","twenty_puzzles","one_daily",
                   "seven_dailies","no_hints","hard_solo","all_themes"):
            self.assertIn(f'AchievementDefinition("{id}"',model)
        for needle in ('testTag("ws16bt-achievement-" + item.id)',
                       'testTag("ws29-achievement-medal-" + id)',
                       'Ws29AchievementMedal(item.id, item.unlocked)',
                       'PaperProgress(item.current, item.target)',
                       'if (!item.unlocked)',
                       'CollectionPager(current, pages, id = "achievements"',
                       'item.current.toString() + " / " + item.target',
                       '" more to earn this achievement"'):
            self.assertIn(needle,source)
        self.assertEqual(source.count("GridMatesInfoPanel(Modifier.fillMaxWidth()"),3)

if __name__ == "__main__":
    unittest.main()
