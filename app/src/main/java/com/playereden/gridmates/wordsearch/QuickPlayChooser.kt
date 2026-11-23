package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize

/** Two taps from Home to a fresh game. Size is the only setup choice. */
@Composable
internal fun QuickPlayChooser(message: String, onPick: (PuzzleBoardSize) -> Unit,
                              onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        GridMatesIllustratedFrame(Modifier.padding(20.dp).widthIn(max = 420.dp).fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL) {
            Column(Modifier.heightIn(max = 580.dp).verticalScroll(rememberScrollState())
                .padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Quick Play", color = Forest, style = MaterialTheme.typography.headlineMedium)
                Text("Fresh words from Woodland, Space, Garden and everyday life.", color = Muted)
                val options = listOf("Small" to PuzzleBoardSize.BOOK_SHORT,
                    "Medium" to PuzzleBoardSize.BOOK_CLASSIC, "Large" to PuzzleBoardSize.BOOK_BIG)
                for ((label, board) in options) {
                    GridMatesIllustratedFrame(Modifier.fillMaxWidth()
                        .testTag("quick-size-${board.cells}"),
                        variant = GridMatesFrameVariant.MEDIUM, onClick = { onPick(board) }) {
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("$label · ${board.cells}×${board.cells}", color = Forest,
                                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${board.targets} words" +
                                if (board == PuzzleBoardSize.BOOK_CLASSIC) " · Recommended" else "",
                                color = Muted, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                if (message.isNotBlank()) Text(message, color = Forest,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Back") }
            }
        }
    }
}
