package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/** Paper frame with a scrolling message area and an always reachable close action. */
@Composable
internal fun GridMatesPaperDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GridMatesIllustratedFrame(
            modifier.widthIn(max = 440.dp).fillMaxWidth(),
            variant = GridMatesFrameVariant.PANEL
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    title,
                    modifier = Modifier.semantics { heading() },
                    color = Forest,
                    style = MaterialTheme.typography.titleLarge
                )
                Column(
                    Modifier.weight(1f, fill = false).fillMaxWidth()
                        .testTag("paper-dialog-scroll").verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    content = content
                )
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End).heightIn(min = 48.dp)
                ) {
                    Text("Close", color = Forest)
                }
            }
        }
    }
}
