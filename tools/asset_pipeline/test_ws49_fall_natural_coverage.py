"""WS49: study-only Fall answers must match canonical QA source, never release."""
import csv
import json
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/BonusWordOpportunityAuditTest.kt"
WORDS = ROOT / "data/lexicon/authoring/fall-v1/words_candidate.csv"
RESOURCE = ROOT / "core/src/test/resources/research-packs/en-fall-v1-nonrelease-answers.txt"
MIXED = ROOT / "core/src/test/resources/reviewed-packs/en-cozy-mixed-v1.tsv"
REPORT = "build/ws49-fall-natural-increment.tsv"
PER_WORD = "build/ws49-fall-candidate-frequencies.tsv"
WORKFLOWS = (
    ROOT / ".github/workflows/ws01-android-qa.yml",
    ROOT / ".github/workflows/ws12g-qa-art-lab.yml",
)

class NaturalFallCoverageContract(unittest.TestCase):
    def test_exact_qa_corpus_no_release_promotion(self):
        with WORDS.open(newline="") as f:
            catalog = list(csv.DictReader(f))
        answers = {row["answer"] for row in catalog}
        resource = RESOURCE.read_text().splitlines()
        self.assertEqual(150, len(catalog))
        self.assertEqual(150, len(answers))
        self.assertEqual(sorted(answers), resource)
        self.assertTrue(all(w.isalpha() and w.isupper() for w in resource))
        self.assertTrue(all(row["review_status"] == "approved" for row in catalog))
        mixed_words = {line.split("\t")[1] for line in MIXED.read_text().splitlines()
                       if line.startswith("gm")}
        self.assertEqual(670, len(mixed_words))
        self.assertEqual(33, len(answers & mixed_words))
        metadata = json.loads((ROOT / "data/lexicon/authoring/en-v1/review-report.json").read_text())
        self.assertFalse(metadata["owner_release_approved"])
        self.assertIn("internal editorial QA",
                      (ROOT / "data/lexicon/authoring/fall-v1/README.md").read_text())
    def test_existing_339_board_scanner_not_generator_change(self):
        src = SRC.read_text()
        for evidence in (
            'assertEquals(339, allTrials.size)',
            'SideWordOriginStudy.classify(puzzle, qaDictionary + fallQaAnswers)',
            'eligibleSideWords(puzzle, qaDictionary + fallQaAnswers)',
            'origin, withFall.filterKeys { it in qa }',
            'incrementalFall = withFall.filterKeys { it !in qa }',
            'File("build/ws46-bonus-opportunities.tsv")',
            'File("build/ws47-bonus-origin.tsv")',
            REPORT, PER_WORD,
            'editorial_QA_only_not_release',
        ):
            self.assertIn(evidence, src)
        self.assertNotIn("WordSearchGeneratorV6", src)
    def test_both_workflows_publish_study_only_data(self):
        for workflow in WORKFLOWS:
            text = workflow.read_text()
            self.assertIn("test_ws49_fall_natural_coverage.py", text)
            self.assertIn("GridMates-WS49-Natural-Fall-Coverage", text)
            self.assertIn("feature/ws49-fall-natural-bonus-coverage-2026-11-19", text)
            self.assertIn("core/" + REPORT, text)
            self.assertIn("core/" + PER_WORD, text)

if __name__ == "__main__":
    unittest.main()
