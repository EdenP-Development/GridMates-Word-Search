package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.SoloProfile
import com.playereden.gridmates.wordsearch.core.RaceOutcome
import java.text.DateFormat
import java.util.Date

@Composable
internal fun SoloRecordsScreen(profile: SoloProfile) {
    // WS16BW: the shell already provides the Records page title. Use a short
    // description here so Solo/Race/Duel are discoverable above the fold.
    Text("Your Solo puzzles and nearby match results stay on this device.",
        modifier = Modifier.fillMaxWidth().testTag("ws16bw-records-intro"),
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    var journalTab by rememberSaveable { mutableStateOf("Solo") }
    var soloPage by rememberSaveable { mutableIntStateOf(0) }
    var racePage by rememberSaveable { mutableIntStateOf(0) }
    var duelPage by rememberSaveable { mutableIntStateOf(0) }

    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Solo", "Race", "Duel").forEach { section ->
            FilterChip(selected = journalTab == section,
                modifier = Modifier.testTag("ws16bw-tab-" + section.lowercase()),
                onClick = { journalTab = section },
                label = { Text(section) })
        }
    }
    // Only the Solo tab needs Solo and Daily summary cards; putting them
    // above Race/Duel makes the selected match history inaccessible without
    // scrolling past unrelated statistics on compact screens.
    if (journalTab == "Solo") {
    // WS16BT: three compressed metrics on a narrow phone truncate labels.
    // Keep all values visible and readable at larger system font scales.
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("ws16bt-solo-metrics")) {
        val fontScale = LocalDensity.current.fontScale
        if (fontScale >= 1.8f) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Completed", profile.completions.toString(), Modifier.fillMaxWidth())
                MetricCard("Words found", profile.totalWords.toString(), Modifier.fillMaxWidth())
                MetricCard("With hints", profile.assistedCompletions.toString(), Modifier.fillMaxWidth())
            }
        } else if (maxWidth < 350.dp || fontScale >= 1.3f) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard("Completed", profile.completions.toString(), Modifier.weight(1f))
                    MetricCard("Words found", profile.totalWords.toString(), Modifier.weight(1f))
                }
                MetricCard("With hints", profile.assistedCompletions.toString(), Modifier.fillMaxWidth())
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Completed", profile.completions.toString(), Modifier.weight(1f))
                MetricCard("Words found", profile.totalWords.toString(), Modifier.weight(1f))
                MetricCard("With hints", profile.assistedCompletions.toString(), Modifier.weight(1f))
            }
        }
    }

    GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
        Column(Modifier.padding(12.dp)) {
            Text("Daily Journal", color = Forest, fontWeight = FontWeight.SemiBold)
            Text(profile.dailyCompletions.toString() + " dated challenges completed · " +
                profile.dailyEntries.count { !it.completed } + " unfinished",
                color = Muted, style = MaterialTheme.typography.bodyMedium)
            Text("Daily progress is saved separately from Solo records.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }

    }

    // Each category has its own remembered page. No journal is silently
    // truncated at 12/15/25 entries and no stored result is deleted.
    if (journalTab == "Race") {
    Text("Shared Board Race", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Shared Board Race results saved locally. Co-op isn't counted as a win or loss.",
        color = Muted, style = MaterialTheme.typography.bodySmall)
    val racing = profile.raceSummary
    MatchSummaryMetrics(
        listOf("Played" to racing.played.toString(),
            "Wins" to racing.wins.toString(),
            "Losses" to racing.losses.toString(),
            "Ties" to racing.ties.toString()),
        "race"
    )
    if (profile.raceResults.isEmpty()) {
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("No Versus matches recorded yet.",
                    fontWeight = FontWeight.SemiBold, color = Forest)
                Text("Finish a Shared Board Race with a nearby partner to start your journal.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        Text("Your most recent races", color = Forest,
            fontWeight = FontWeight.SemiBold)
        val page = racePage.coerceIn(0, (profile.raceResults.size - 1).coerceAtLeast(0) / 4)
        profile.raceResults.drop(page * 4).take(4).forEach { race ->
            GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
                Column(Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column(Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(when (race.outcome) {
                            RaceOutcome.WIN -> "Victory"
                            RaceOutcome.LOSS -> "Defeat"
                            RaceOutcome.TIE -> "Tie"
                        }, color = Forest, fontWeight = FontWeight.Bold)
                        Text("You ${race.ownWords} · Partner ${race.partnerWords}",
                            color = Ink, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(race.theme.label + " · " + race.board.cells +
                        "×" + race.board.cells + " · " + race.challenge.label,
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    Text(DateFormat.getDateTimeInstance(
                        DateFormat.MEDIUM, DateFormat.SHORT).format(Date(race.completedAtMs)),
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (profile.raceResults.isNotEmpty()) JournalPages(
        count = profile.raceResults.size, page = racePage, pageSize = 4,
        onPage = { racePage = it })
    }
    if (journalTab == "Duel") {
    Text("Speed Duel", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Separate private-board matches. First host-confirmed completion wins. " +
        "These results are not mixed with Shared Board Race.",
        color = Muted, style = MaterialTheme.typography.bodySmall)
    val dueling = profile.duelSummary
    MatchSummaryMetrics(
        listOf("Played" to dueling.played.toString(),
            "Wins" to dueling.wins.toString(),
            "Losses" to dueling.losses.toString(),
            "Words found" to dueling.totalWordsFound.toString()),
        "duel"
    )
    if (profile.duelResults.isEmpty()) {
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("No Speed Duels recorded yet.",
                    fontWeight = FontWeight.SemiBold, color = Forest)
                Text("Finish a private-board Duel with a nearby friend to save your result.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        Text("Recent Speed Duels", color = Forest, fontWeight = FontWeight.SemiBold)
        val page = duelPage.coerceIn(0, (profile.duelResults.size - 1).coerceAtLeast(0) / 4)
        profile.duelResults.drop(page * 4).take(4).forEach { duel ->
            GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
                Column(Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column(Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(if (duel.won) "Victory" else "Defeat",
                            color = Forest, fontWeight = FontWeight.Bold)
                        Text("You " + duel.ownWords + " · Partner " + duel.partnerWords,
                            color = Ink, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(duel.theme.label + " · " + duel.board.cells +
                        "×" + duel.board.cells + " · " + duel.challenge.label,
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    Text(DateFormat.getDateTimeInstance(
                        DateFormat.MEDIUM, DateFormat.SHORT).format(Date(duel.completedAtMs)),
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (profile.duelResults.isNotEmpty()) JournalPages(
        count = profile.duelResults.size, page = duelPage, pageSize = 4,
        onPage = { duelPage = it })
    }
    if (journalTab == "Solo") {
    Text("Recent puzzles", color = Forest,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    if (profile.results.isEmpty()) {
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Your story starts with a puzzle.", fontWeight = FontWeight.SemiBold)
                Text("Finish a Solo game and you'll see it here.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        val page = soloPage.coerceIn(0, (profile.results.size - 1).coerceAtLeast(0) / 4)
        for (record in profile.results.drop(page * 4).take(4)) {
            GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Column(Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(record.theme.label + " · " +
                            (record.boardSize?.let { it.cells.toString() + "×" + it.cells } ?:
                                (record.difficulty.gridSize.toString() + "×" +
                                    record.difficulty.gridSize)) +
                            " · " + (record.challenge?.label ?: record.difficulty.label),
                            color = Forest, fontWeight = FontWeight.SemiBold)
                        Text(if (record.assisted) "Assisted" else "No hints",
                            color = Gold, style = MaterialTheme.typography.labelMedium)
                    }
                    Text(record.wordCount.toString() + " words · " +
                        (record.elapsedSeconds / 60L).toString() + "m " +
                        (record.elapsedSeconds % 60L).toString() + "s · " +
                        record.hintSteps.toString() + " hint steps",
                        style = MaterialTheme.typography.bodyMedium)
                    Text(
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                            .format(Date(record.completedAtMs)),
                        color = Muted, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
    if (profile.results.isNotEmpty()) JournalPages(
        count = profile.results.size, page = soloPage, pageSize = 4,
        onPage = { soloPage = it })
    }
}

/** Keep the four Race/Duel summary metrics in a 2x2 grid on normal phones.
 * The previous <350dp content-width rule stacked even on a normal 320dp
 * handset after the shell padding. Stack only when two columns cannot fit
 * comfortably, or at accessibility-scale type.
 */
internal fun stackMatchSummaryMetrics(availableWidthDp: Float, fontScale: Float): Boolean =
    availableWidthDp < 280f || fontScale >= 1.8f

@Composable
private fun MatchSummaryMetrics(values: List<Pair<String, String>>, mode: String) {
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("ws16bw-" + mode + "-metrics")) {
        val stacked = stackMatchSummaryMetrics(maxWidth.value, LocalDensity.current.fontScale)
        if (stacked) {
            Column(Modifier.fillMaxWidth().testTag("ws16bx-" + mode + "-stacked"),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                values.forEach { (label, amount) ->
                    MetricCard(label, amount, Modifier.fillMaxWidth())
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                values.chunked(2).forEachIndexed { index, pair ->
                    Row(Modifier.fillMaxWidth()
                        .testTag("ws16bx-" + mode + "-row-" + index),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { (label, amount) ->
                            MetricCard(label, amount, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/** Pure page math; every saved record remains reachable by the pager. */
internal fun journalPageCount(count: Int, pageSize: Int): Int =
    ((count.coerceAtLeast(0) + pageSize - 1) / pageSize).coerceAtLeast(1)

@Composable
private fun JournalPages(count: Int, page: Int, pageSize: Int, onPage: (Int) -> Unit) {
    val pages = journalPageCount(count, pageSize)
    if (pages <= 1) return
    val current = page.coerceIn(0, pages - 1)
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = { onPage(current - 1) }, enabled = current > 0) {
            Text("Previous")
        }
        Text("Page ${current + 1} of $pages",
            color = Muted, style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = { onPage(current + 1) }, enabled = current < pages - 1) {
            Text("Next")
        }
    }
}

@Composable
private fun MetricCard(label: String, amount: String, modifier: Modifier = Modifier) {
    GridMatesIllustratedFrame(
        modifier = modifier.testTag("ws16bt-metric-" + label.lowercase().replace(" ", "-")),
        variant = GridMatesFrameVariant.PANEL
    ) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(amount, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = Forest)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }
}
