"""WS57 enforce strictly Debug-only, 18 pinned pages + 63 old compatible pages.

This does NOT claim Android emulator tests ran; CI builds the real APK and
compiles instrumentation. Actual handset/editorial acceptance is separate.
"""
from pathlib import Path
from collections import defaultdict
import csv
import re
import unittest

ROOT = Path(__file__).resolve().parents[2]
MAIN = ROOT / "app/src/main/java/com/playereden/gridmates/wordsearch"
DEBUG = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch"
RELEASE = ROOT / "app/src/release/java/com/playereden/gridmates/wordsearch"
MAN = ROOT / "app/src/debug/assets/ws57_woodland_manifest.tsv"
PINS = ROOT / "app/src/debug/assets/ws57_woodland_expected_boards.tsv"
AUTHOR = ROOT / "data/lexicon/authoring/chapter-recipes-v1"

def rows(file):
    with file.open(newline="") as handle:
        return list(csv.DictReader(handle, delimiter="\t"))

class Ws57PlayablePreviewContract(unittest.TestCase):
    def test_18_pages_are_exactly_original_qa_manuscript_word_lists(self):
        manifest = rows(MAN)
        self.assertEqual(312, len(manifest))
        self.assertEqual(264, len({x["answer"] for x in manifest}))
        self.assertEqual(122, sum(x["provenance"].endswith("candidate_unapproved")
                                  for x in manifest))
        groups = defaultdict(list)
        for row in manifest:
            groups[int(row["chapter"]),int(row["page"])].append(row)
        self.assertEqual(18, len(groups))
        for (ch,page),values in groups.items():
            self.assertIn(ch,(3,4,5,6,7,8))
            self.assertIn(page,(1,2,3))
            self.assertEqual((12,16,24)[page-1],len(values))
            self.assertEqual(len(values),len({x["answer"] for x in values}))
            self.assertEqual(len(values),len({x["word_id"] for x in values}))
        originals = defaultdict(list)
        for x in rows(AUTHOR/"woodland-ws52-manuscript.tsv"):
            if int(x["chapter"]) in (3,4):
                originals[int(x["chapter"]),int(x["page"])].append(
                    (x["answer"],x["word_id"],x["display"],x["tier"],
                     x["word_subtopic"],x["provenance"]))
        for x in rows(AUTHOR/"birdsong-ws51-qa-manuscript.tsv"):
            sub = "birds" if x["provenance"]=="existing_woodland_birds_QA" else "birdlife"
            originals[5,int(x["page"])].append(
                (x["answer"],x["word_id"],x["display"],x["tier"],sub,x["provenance"]))
        for chapter,file in ((6,"woodland-ws54-stream-manuscript.tsv"),
                             (7,"woodland-ws56-clearings-manuscript.tsv"),
                             (8,"woodland-ws55-wildlife-manuscript.tsv")):
            for x in rows(AUTHOR/file):
                originals[chapter,int(x["page"])].append(
                    (x["answer"],x["word_id"],x["display"],x["tier"],
                     x["subtopic"],x["provenance"]))
        for key,values in groups.items():
            self.assertEqual(originals[key],[
                (x["answer"],x["word_id"],x["display"],x["tier"],
                 x["subtopic"],x["provenance"]) for x in values
            ],"Authored order/IDs drifted: "+str(key))
        pins=rows(PINS)
        self.assertEqual(18,len(pins))
        self.assertEqual(18,len({(p["chapter"],p["page"]) for p in pins}))
        self.assertTrue(all(re.fullmatch(r"[0-9a-f]{64}",p["expected_board_sha256"])
                            for p in pins))

    def test_saves_release_and_legacy_pages_are_isolated(self):
        self.assertIn("const val enabled = false",(RELEASE/"BookQaPilot.kt").read_text())
        self.assertFalse((RELEASE/"Ws57WoodlandPreview.kt").exists())
        debug=(DEBUG/"BookQaPilot.kt").read_text()
        self.assertIn('volume("woodland", "Woodland", WordTheme.WOODLAND, 171L)',debug)
        self.assertIn("Ws57WoodlandPreview.appendLegacy(context, cachedCatalog)",debug)
        code=(DEBUG/"Ws57WoodlandPreview.kt").read_text()
        for needle in ("BookBoardDigest.sha256(frozen.puzzle)",
                       "require(digest == pins.getValue(chapter to page))",
                       "legacy.requireCompatibleUpgrade(it)",
                       "WordSearchGeneratorV5.generate(",
                       "ws57_woodland_manifest.tsv",
                       "ws57_woodland_expected_boards.tsv",
                       "woodland-ws57-qa-volume-two"):
            self.assertIn(needle,code)
        runtime=(MAIN/"QaBookshelfRuntime.kt").read_text()
        self.assertIn('it != "woodland-ws57-qa-volume-two"',runtime)
        self.assertIn("gridmates_wordsearch_ws11_qa_progress_v1.dat",runtime)
        self.assertIn("gridmates_wordsearch_ws11_qa_sessions_v1.dat",runtime)
        self.assertIn("BookQaPilot.catalog(applicationContext)",runtime)
        self.assertIn("gridmates_wordsearch_ws57_qa_progress_v1.dat",runtime)
        self.assertIn("gridmates_wordsearch_ws57_qa_sessions_v1.dat",runtime)
        self.assertIn("importManualBackup(savedBytes)",runtime)
        self.assertIn("fun catalog(): BookCatalog = cachedCatalog",debug)
        instrument=(ROOT/"app/src/androidTest/java/com/playereden/gridmates/wordsearch/Ws57PlayableWoodlandPreviewAndroidTest.kt").read_text()
        for needle in ("original.requireCompatibleUpgrade(added)",
                       "assertEquals(63,","assertEquals(81,",
                       "BookProgressRules.continuePage(",
                       "BookPageSessionRules.puzzle("):
            self.assertIn(needle,instrument)

if __name__ == "__main__":
    unittest.main()
