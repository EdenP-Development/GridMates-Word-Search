"""WS16BX: Race/Duel 2x2 at normal handset widths; 200% text stacks."""
import unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"
RECORDS=(APP/"SoloRecordsScreen.kt").read_text()
class RecordsMetricDensityTests(unittest.TestCase):
    def test_normal_phone_uses_two_rows_and_high_accessibility_stacks(self):
        self.assertIn("internal fun stackMatchSummaryMetrics(availableWidthDp: Float, fontScale: Float): Boolean",RECORDS)
        self.assertIn("availableWidthDp < 280f || fontScale >= 1.8f",RECORDS)
        self.assertIn("stackMatchSummaryMetrics(maxWidth.value, LocalDensity.current.fontScale)",RECORDS)
        self.assertNotIn("maxWidth < 350.dp || largeText",RECORDS)
        self.assertIn('testTag("ws16bx-" + mode + "-row-" + index)',RECORDS)
        self.assertIn('testTag("ws16bx-" + mode + "-stacked")',RECORDS)
        self.assertIn("values.chunked(2).forEachIndexed { index, pair ->",RECORDS)
    def test_both_mode_summaries_and_records_are_unmodified(self):
        for code in ['"Played" to racing.played.toString()','"Wins" to racing.wins.toString()',
                     '"Losses" to racing.losses.toString()', '"Ties" to racing.ties.toString()',
                     '"Words found" to dueling.totalWordsFound.toString()',
                     'profile.raceResults.drop(page * 4).take(4)',
                     'profile.duelResults.drop(page * 4).take(4)',
                     'profile.results.drop(page * 4).take(4)']:
            self.assertIn(code,RECORDS)
        self.assertIn('var racePage by rememberSaveable',RECORDS)
        self.assertIn('var duelPage by rememberSaveable',RECORDS)
if __name__=="__main__":
    unittest.main()
