package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Use the same accessible 150% breakpoint across Daily and Nearby live panels. */
internal fun shouldStackLobbyActions(fontScale: Float): Boolean = fontScale >= 1.5f

/** WS16BT shared live-data panel: decorative paint only, never a baked card.
 * Reused by Collection today, and available to future secondary screens.
 * The child's native text, click, unlock and progress state is untouched.
 */
@Composable
internal fun GridMatesInfoPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    GridMatesIllustratedFrame(
        modifier = modifier,
        variant = GridMatesFrameVariant.PANEL
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}
