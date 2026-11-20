"""WS50: verify the 18 source-backed chapter recipes and test-only boundary."""
from pathlib import Path
import csv
import unittest

ROOT = Path(__file__).resolve().parents[2]
MAP = ROOT / "data/lexicon/authoring/chapter-recipes-v1/chapter-map.tsv"
MIRROR = ROOT / "core/src/test/resources/chapter-recipes/ws50-chapter-map.tsv"
KOTLIN = ROOT / "core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/ChapterRecipeLabTest.kt"
QA_RUNTIME = ROOT / "app/src/debug/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
RELEASE = ROOT / "app/src/release/java/com/playereden/gridmates/wordsearch/BookQaPilot.kt"
WORKFLOWS = [ROOT / ".github/workflows/ws01-android-qa.yml",
             ROOT / ".github/workflows/ws12g-qa-art-lab.yml"]

class ChapterRecipeContract(unittest.TestCase):
    def test_exact_chapter_map_preserved_and_all_titles_real(self):
        self.assertEqual(MAP.read_bytes(), MIRROR.read_bytes())
        with MAP.open(newline="") as f:
            rows = list(csv.DictReader(f, delimiter="\t"))
        self.assertEqual(18, len(rows))
        self.assertEqual(len(rows), len({(r["theme"], r["chapter_number"]) for r in rows}))
        self.assertEqual({"WOODLAND", "SPACE", "GARDEN"}, {r["theme"] for r in rows})
        runtime = QA_RUNTIME.read_text()
        for theme in ("WOODLAND", "SPACE", "GARDEN"):
            self.assertEqual(list(range(3,9)), sorted(int(r["chapter_number"])
                for r in rows if r["theme"] == theme))
        for r in rows:
            self.assertIn('"' + r["chapter_title"] + '"', runtime)
            allowed = r["allowed_subtopics"].split(",")
            self.assertIn(r["primary_subtopic"], allowed)
            self.assertEqual(len(allowed), len(set(allowed)))
        self.assertIn('"Birdsong"', runtime)
        self.assertIn('"birds"', MAP.read_text())
    def test_original_qa_books_and_release_not_modified(self):
        runtime = QA_RUNTIME.read_text()
        self.assertIn("(3..8).map { chapter ->", runtime)
        self.assertIn('volume("woodland", "Woodland", WordTheme.WOODLAND, 171L)', runtime)
        self.assertIn('volume("space", "Space", WordTheme.SPACE, 281L)', runtime)
        self.assertIn('volume("garden", "Garden", WordTheme.GARDEN, 391L)', runtime)
        self.assertIn("const val enabled = false", RELEASE.read_text())
        code = KOTLIN.read_text()
        for token in ("WordSearchGeneratorV5.generate(", "BookBoardDigest.sha256(",
            "BookPageSessionRules.puzzle(", "BookCatalog(listOf(book))",
            "assertEquals(29, chapterPack.words.size)",
            "research_QA_only_not_published", "unapproved_frozen_QA_preview",
            "ws50-chapter-pool-coverage.tsv", "ws50-birdsong-qa-preview.tsv"):
            self.assertIn(token, code)
    def test_two_independent_artifacts_are_wired(self):
        for f in WORKFLOWS:
            content = f.read_text()
            self.assertIn("test_ws50_chapter_map.py", content)
            self.assertIn("GridMates-WS50-Chapter-Recipe-QA", content)
            self.assertIn("feature/ws50-chapter-recipe-and-birdsong-lab-2026-11-19", content)
            self.assertIn("core/build/ws50-chapter-pool-coverage.tsv", content)
            self.assertIn("core/build/ws50-birdsong-qa-preview.tsv", content)

if __name__ == "__main__":
    unittest.main()
