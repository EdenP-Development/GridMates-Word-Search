"""WS51 independent source integrity: no page identity or release promotion."""
import csv
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "data/lexicon/authoring/chapter-recipes-v1/birdsong-ws51-qa-manuscript.tsv"
MIRROR = ROOT / "core/src/test/resources/chapter-recipes/ws51-birdsong-qa-manuscript.tsv"
WOODLAND = ROOT / "core/src/test/resources/reviewed-packs/en-woodland-v1.tsv"
PILOT = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
RELEASE = ROOT / "app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
KOTLIN = ROOT / "core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/BirdsongDiverseManuscriptTest.kt"
WORKFLOWS = (
    ROOT / ".github/workflows/ws01-android-qa.yml",
    ROOT / ".github/workflows/ws12g-qa-art-lab.yml",
)

def manuscript_rows():
    with SOURCE.open(newline="") as f:
        return list(csv.DictReader(f, delimiter="\t"))

class BirdsongManuscriptContract(unittest.TestCase):
    def test_exact_source_mirror_and_challenge_progression(self):
        self.assertEqual(SOURCE.read_bytes(), MIRROR.read_bytes())
        rows = manuscript_rows()
        self.assertEqual(52, len(rows))
        self.assertEqual(52, len({r["answer"] for r in rows}))
        self.assertEqual(52, len({r["word_id"] for r in rows}))
        self.assertEqual([12,16,24], [sum(r["page"]==str(n) for r in rows)
                                      for n in (1,2,3)])
        self.assertEqual(["Forest Friends","Feathers and Flight","Birdsong Challenge"],
            [next(r["page_title"] for r in rows if r["page"]==str(n))
             for n in (1,2,3)])
        avg = [sum(len(r["answer"]) for r in rows if r["page"] == str(n)) /
               sum(r["page"] == str(n) for r in rows) for n in (1,2,3)]
        self.assertTrue(avg[0] < avg[1] < avg[2], avg)
        for n in (1,2,3):
            words = [r["answer"] for r in rows if r["page"] == str(n)]
            self.assertTrue(all(w.isalpha() and w.isupper() for w in words))
            self.assertTrue(all(not a in b for a in words for b in words if a != b))
        self.assertTrue(all(len(r["answer"]) <= 7 and r["tier"] == "familiar"
            for r in rows if r["page"] == "1"))
    def test_existing_ids_unchanged_and_23_new_are_not_approved(self):
        source_rows = {}
        for line in WOODLAND.read_text().splitlines():
            if line.startswith("gm"):
                word_id, answer, display, tags = line.split("\t")
                if any(t.startswith("woodland:birds:") for t in tags.split("|")):
                    source_rows[word_id]=(answer,display)
        self.assertEqual(29, len(source_rows))
        rows=manuscript_rows()
        existing=[r for r in rows if r["provenance"]=="existing_woodland_birds_QA"]
        proposed=[r for r in rows if r["provenance"]=="ws51_editorial_candidate_unapproved"]
        self.assertEqual(29,len(existing))
        self.assertEqual(23,len(proposed))
        self.assertEqual(set(source_rows),{r["word_id"] for r in existing})
        self.assertTrue(all(source_rows[r["word_id"]] == (r["answer"],r["display"])
                        for r in existing))
        self.assertTrue(all(r["word_id"].startswith("ws51b") for r in proposed))
        self.assertTrue(all(4 <= len(r["answer"]) <= 16 for r in proposed))
    def test_old_library_untouched_and_ci_exercises_real_frozen_board_code(self):
        pilot=PILOT.read_text()
        self.assertIn("volume(\"woodland\", \"Woodland\", WordTheme.WOODLAND, 171L)",pilot)
        self.assertIn("(3..8).map { chapter ->",pilot)
        self.assertIn("const val enabled = false",RELEASE.read_text())
        source=KOTLIN.read_text()
        for needle in ("WordSearchGeneratorV5.generate(", "BookBoardDigest.sha256(",
                       "BookPageSessionRules.puzzle(", "assertEquals(52, selectedWords.size)",
                       "assertTrue(catalog.matchesPublishedPuzzle", "QA_editorial_candidate_NOT_RELEASED"):
            self.assertIn(needle,source)
        for wf in WORKFLOWS:
            text=wf.read_text()
            for needle in ("test_ws51_birdsong_manuscript.py",
                           "GridMates-WS51-Birdsong-Manuscript",
                           "feature/ws51-birdsong-diverse-page-manuscript-2026-11-20",
                           "core/build/ws51-birdsong-diverse-frozen-pages.tsv"):
                self.assertIn(needle,text)

if __name__=="__main__":
    unittest.main()
