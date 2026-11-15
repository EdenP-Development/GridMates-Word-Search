"""WS16BW: Records UX improvements without any journal truncation or mutation."""
import unittest
from pathlib import Path

ROOT=Path(__file__).resolve().parents[2]
APP=ROOT/"app/src/main/java/com/playereden/gridmates/wordsearch"

class RecordsUXTests(unittest.TestCase):
    def test_one_shell_title_and_early_access_to_all_three_tabs(self):
        screen=(APP/"SoloRecordsScreen.kt").read_text()
        shell=(APP/"GridMatesShell.kt").read_text()
        self.assertNotIn("StorybookHeading(",screen)
        self.assertNotIn("Ws15SectionHeading(",screen)
        self.assertIn('testTag("ws16bw-records-intro")',screen)
        self.assertIn('testTag("ws16bw-tab-" + section.lowercase())',screen)
        self.assertIn('Page.RECORDS -> SoloRecordsScreen(soloStore.profile)',shell)
        self.assertIn('RECORDS("Records")',shell)
        self.assertIn('var journalTab by rememberSaveable { mutableStateOf("Solo") }',screen)
        for mode in ('"Solo"','"Race"','"Duel"'):
            self.assertIn('journalTab == '+mode,screen)

    def test_long_history_and_independent_page_persistence_unchanged(self):
        src=(APP/"SoloRecordsScreen.kt").read_text()
        for state in ('var soloPage by rememberSaveable','var racePage by rememberSaveable',
                      'var duelPage by rememberSaveable'):
            self.assertIn(state,src)
        for expression in ('profile.raceResults.drop(page * 4).take(4)',
                           'profile.duelResults.drop(page * 4).take(4)',
                           'profile.results.drop(page * 4).take(4)',
                           'journalPageCount(count, pageSize)',
                           'page.coerceIn(0, pages - 1)'):
            self.assertIn(expression,src)
        self.assertIn('if (journalTab == "Solo")',src)
        self.assertIn('if (journalTab == "Race")',src)
        self.assertIn('if (journalTab == "Duel")',src)
        self.assertIn('profile.dailyEntries.count { !it.completed }',src)

    def test_accessible_match_metrics_and_wrap_safe_saved_results(self):
        src=(APP/"SoloRecordsScreen.kt").read_text()
        self.assertIn('private fun MatchSummaryMetrics(values: List<Pair<String, String>>, mode: String)',src)
        self.assertIn('testTag("ws16bw-" + mode + "-metrics")',src)
        self.assertIn('stackMatchSummaryMetrics(maxWidth.value, LocalDensity.current.fontScale)',src)
        self.assertIn('values.chunked(2).forEachIndexed { index, pair ->',src)
        for s in ('"Played" to racing.played.toString()',
                  '"Ties" to racing.ties.toString()',
                  '"Words found" to dueling.totalWordsFound.toString()',
                  'RaceOutcome.WIN -> "Victory"',
                  'RaceOutcome.TIE -> "Tie"',
                  'Text(if (duel.won) "Victory" else "Defeat"',
                  'if (record.assisted) "Assisted" else "No hints"',
                  'DateFormat.getDateTimeInstance'):
            self.assertIn(s,src)
        # SpaceBetween is correct in the pager; saved result headers must be
        # vertically stacked so long translated labels cannot collide.
        self.assertEqual(src.count('verticalArrangement = Arrangement.spacedBy(2.dp)) {'),3)
        self.assertEqual(src.count('GridMatesIllustratedFrame('),8)

if __name__=="__main__":
    unittest.main()
