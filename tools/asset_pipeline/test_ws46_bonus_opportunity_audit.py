"""WS46: guard read-only real-board bonus opportunity study, not game changes."""
from pathlib import Path
import unittest
ROOT=Path(__file__).resolve().parents[2]
AUDIT=ROOT/"core/src/test/kotlin/com/playereden/gridmates/wordsearch/core/BonusWordOpportunityAuditTest.kt"
ACTIONS=ROOT/".github/workflows/ws01-android-qa.yml"
ART=ROOT/".github/workflows/ws12g-qa-art-lab.yml"

class AuditReproducibility(unittest.TestCase):
    def test_kotlin_audit_uses_real_generators_and_actual_ws44_claim(self):
        src=AUDIT.read_text()
        for text in (
            'ReviewedWordPackCodec.decode(tsv)',
            'reviewed-packs/en-cozy-mixed-v1.tsv',
            'WordSearchGeneratorV5.generate(',
            'WordSearchGenerator.generate(',
            'WordSearchGeneratorV2.generate(',
            'WordSearchGeneratorV3.generate(',
            'BonusWordRules.select(puzzle, emptySet(), dictionary,',
            'WordTheme.entries.flatMap { it.words }',
            'repeat(24)', 'repeat(12)', 'repeat(5)',
            'assertEquals(339, allTrials.size)',
            'pct_zero','pct_12plus', 'pct_4letter',
            'builtins_only_release_fallback',
            'qa_670_plus_builtins',
            'File("build/ws46-bonus-opportunities.tsv")',
        ):
            self.assertIn(text,src)
        self.assertNotIn('writeText(', (ROOT/"core/src/main/kotlin/com/playereden/gridmates/wordsearch/core/BonusWordRules.kt").read_text())
    def test_both_independent_ci_jobs_publish_inspectable_reports(self):
        for path in (ACTIONS,ART):
            src=path.read_text()
            self.assertIn('test_ws46_bonus_opportunity_audit.py',src)
            self.assertIn('GridMates-WS46-Bonus-Opportunity-Audit',src)
            self.assertIn('core/build/ws46-bonus-opportunities.tsv',src)
            self.assertIn("feature/ws46-bonus-opportunity-audit-2026-11-19",src)

if __name__=="__main__":
    unittest.main()
