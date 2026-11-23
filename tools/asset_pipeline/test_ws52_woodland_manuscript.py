"""WS52 editorial manuscript input guard; no playable or release promotion."""
import csv
from pathlib import Path
from collections import Counter
import unittest

ROOT = Path(__file__).resolve().parents[2]
MANUSCRIPT = ROOT / "data/lexicon/authoring/chapter-recipes-v1/woodland-ws52-manuscript.tsv"
MIRROR = ROOT / "core/src/test/resources/chapter-recipes/ws52-woodland-manuscript.tsv"
SOURCE = ROOT / "core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"
RECIPES = ROOT / "data/lexicon/authoring/chapter-recipes-v1/chapter-map.tsv"
PILOT = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
RELEASE = ROOT / "app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
KOTLIN = ROOT / "core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/WoodlandChapterManuscriptTest.kt"
WORKFLOWS = (
    ROOT / ".github/workflows/ws01-android-qa.yml",
    ROOT / ".github/workflows/ws12g-qa-art-lab.yml",
)

def rows(path):
    with path.open(newline="") as stream:
        return list(csv.DictReader(stream, delimiter="\t"))

class WoodlandChapterSourceContract(unittest.TestCase):
    def test_260_distinct_within_each_five_chapters_and_page_progression(self):
        self.assertEqual(MANUSCRIPT.read_bytes(), MIRROR.read_bytes())
        all_rows = rows(MANUSCRIPT)
        self.assertEqual(260, len(all_rows))
        self.assertEqual({3,4,6,7,8}, {int(x["chapter"]) for x in all_rows})
        self.assertTrue(all(x["provenance"] == "existing_woodland_QA_not_release"
                            for x in all_rows))
        for ch in (3,4,6,7,8):
            group = [r for r in all_rows if int(r["chapter"]) == ch]
            self.assertEqual(52, len(group))
            self.assertEqual(52, len({r["answer"] for r in group}))
            means = []
            for page, count, upper, needed_primary in ((1,12,7,5), (2,16,10,6), (3,24,15,9)):
                terms = [r for r in group if int(r["page"]) == page]
                self.assertEqual(count, len(terms))
                self.assertTrue(all(4 <= len(x["answer"]) <= upper for x in terms))
                if page == 1:
                    self.assertTrue(all(x["tier"] == "familiar" for x in terms))
                self.assertGreaterEqual(
                    sum(x["word_subtopic"] == x["primary_subtopic"] for x in terms),
                    needed_primary
                )
                for a in terms:
                    for b in terms:
                        if a is not b:
                            self.assertNotIn(a["answer"], b["answer"])
                means.append(sum(len(x["answer"]) for x in terms) / len(terms))
            self.assertLess(means[0],means[1])
            self.assertLess(means[1],means[2])
    def test_original_canonical_words_and_allowed_source_categories(self):
        original = {}
        for line in SOURCE.read_text().splitlines():
            if line.startswith("gm"):
                ident, answer, display, tags = line.split("\t")
                original[ident] = (answer, display, set(tags.split("|")))
        recipes = {(r["theme"],int(r["chapter_number"])):r for r in rows(RECIPES)}
        all_rows = rows(MANUSCRIPT)
        self.assertEqual(159,len(original))
        self.assertEqual(124,len({x["answer"] for x in all_rows}))
        for x in all_rows:
            self.assertIn(x["word_id"],original)
            ans,display,tags=original[x["word_id"]]
            self.assertEqual((ans,display),(x["answer"],x["display"]))
            self.assertIn("woodland:" + x["word_subtopic"] + ":" + x["tier"],tags)
            recipe=recipes[("WOODLAND",int(x["chapter"]))]
            self.assertEqual(recipe["chapter_title"],x["chapter_title"])
            self.assertEqual(recipe["primary_subtopic"],x["primary_subtopic"])
            self.assertEqual(recipe["allowed_subtopics"],x["allowed_subtopics"])
            self.assertIn(x["word_subtopic"],x["allowed_subtopics"].split(","))
    def test_actual_frozen_regression_wired_but_releases_unchanged(self):
        self.assertIn("const val enabled = false", RELEASE.read_text())
        original = PILOT.read_text()
        self.assertIn("volume(\"woodland\", \"Woodland\", WordTheme.WOODLAND, 171L)",original)
        self.assertIn("(3..8).map { chapter ->",original)
        source=KOTLIN.read_text()
        for needle in ("WordSearchGeneratorV5.generate(",
                       "BookBoardDigest.sha256(", "BookPageSessionRules.puzzle(",
                       "assertEquals(52, used.size)", "assertEquals(15, bookChapters.sumOf",
                       "ws52-woodland-five-chapter-frozen-boards.tsv",
                       "ws52-woodland-chapter-quality.tsv",
                       "QA_only_not_release_approved"):
            self.assertIn(needle,source)
        for wf in WORKFLOWS:
            text=wf.read_text()
            for needle in ("test_ws52_woodland_manuscript.py",
                           "GridMates-WS52-Woodland-Chapter-QA",
                           "feature/ws52-woodland-five-chapter-manuscripts-2026-11-20",
                           "core/build/ws52-woodland-five-chapter-frozen-boards.tsv",
                           "core/build/ws52-woodland-chapter-quality.tsv"):
                self.assertIn(needle,text)

if __name__=="__main__":
    unittest.main()
