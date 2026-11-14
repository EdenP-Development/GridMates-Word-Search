"""WS16BR: asset-role/source crosswalk and reuse-first UI safeguards."""
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
APP = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
INDEX = ROOT / "design/visual-system/ws16br-runtime-asset-crosswalk.json"
REGISTER = ROOT / "design/visual-system/modular/ws16g-batch1-production-register.json"


class CohesionTests(unittest.TestCase):
    def test_all_original_asset_roles_have_existing_runtime_paths(self):
        original = json.loads(REGISTER.read_text())
        ledger = json.loads(INDEX.read_text())
        roles = {e["id"]: e for e in ledger["entries"]}
        self.assertEqual(ledger["original_roles"], len(original["items"]))
        self.assertEqual(len(roles), len(original["items"]))
        self.assertEqual(set(roles), {e["id"] for e in original["items"]})
        for source in original["items"]:
            item = roles[source["id"]]
            self.assertEqual(item["original_owner_art_approval"],
                             source["owner_art_approval"])
            self.assertEqual(item["original_release_approval"],
                             source["release_approval"])
            self.assertFalse(item["this_audit_production_approval"])
            self.assertTrue(item["runtime_code"])
            for name in item["runtime_code"]:
                self.assertTrue((ROOT / name).is_file(), name)
        self.assertEqual(ledger["recorded_owner_approvals"], 0)
        self.assertEqual(ledger["recorded_release_approvals"], 0)

    def test_home_icons_book_art_and_paper_are_reused(self):
        ledger = json.loads(INDEX.read_text())
        extra = {e["id"]: e for e in ledger["extra_families"]}
        self.assertEqual(len(extra), 4)
        for e in extra.values():
            if "source_file" in e:
                self.assertTrue((ROOT / e["source_file"]).is_file())
            if "source_dir" in e:
                self.assertTrue((ROOT / e["source_dir"]).is_dir())
            for p in e["runtime_code"]:
                self.assertTrue((ROOT / p).is_file())
        self.assertTrue((ROOT / "design/visual-system/masters/ws16i-home-decor/top-left.svg").is_file())

    def test_storybook_paper_remains_home_opt_in(self):
        paper = (APP / "GridMatesParchmentBackground.kt").read_text()
        shell = (APP / "GridMatesShell.kt").read_text()
        self.assertIn("GridMatesPaperFinish { QUIET, STORYBOOK_HOME }", paper)
        self.assertIn("finish: GridMatesPaperFinish = GridMatesPaperFinish.QUIET", paper)
        self.assertIn("val storybookHome = finish == GridMatesPaperFinish.STORYBOOK_HOME", paper)
        self.assertIn("if (storybookHome) {", paper)
        self.assertIn("finish = if (page == Page.PLAY) GridMatesPaperFinish.STORYBOOK_HOME", shell)
        self.assertIn("else GridMatesPaperFinish.QUIET", shell)
        self.assertIn('context.assets.open("ws16aa/parchment.png")', paper)
        self.assertIn("Ws16IHomeEdgeDecor(displayMode)", shell)

    def test_compact_dialog_reuses_existing_assets_and_open_callbacks(self):
        source = (APP / "BookcaseBookshelfScreen.kt").read_text()
        self.assertIn("private fun BookcaseBookPreviewDialog(", source)
        self.assertIn("Dialog(onDismissRequest = onDismiss)", source)
        self.assertIn("variant = GridMatesFrameVariant.PANEL", source)
        self.assertIn('testTag("library-pulled-book-dialog")', source)
        self.assertIn('testTag("library-pulled-cover-" + book.id)', source)
        self.assertIn('testTag("library-view-pages")', source)
        self.assertIn("GridMatesIllustratedPill(", source)
        self.assertIn("LocalDensity.current.fontScale >= 1.30f", source)
        self.assertIn("onOpenBook(chosen.id)", source)
        self.assertIn("BookProgressRules.completedCount(catalog, progress, chosen.id)", source)
        self.assertIn("fieldNotes = fieldNotesArt", source)
        self.assertNotIn("AlertDialog(", source)


if __name__ == "__main__":
    unittest.main()
