package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val PuzzlePaperShape = RoundedCornerShape(12.dp)

internal fun Modifier.puzzlePaper(): Modifier =
    background(Paper, PuzzlePaperShape)
        .border(1.dp, Gold.copy(alpha = 0.38f), PuzzlePaperShape)

@Composable
internal fun PuzzleTitleBar(
    title: String,
    subtitle: String? = null,
    leading: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val heading: @Composable (Modifier) -> Unit = { modifier ->
        Column(modifier, verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(title, color = Forest, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, color = Muted, style = MaterialTheme.typography.labelSmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
    BoxWithConstraints(Modifier.fillMaxWidth().testTag("puzzle-title-bar")) {
        if (maxWidth < 238.dp || LocalDensity.current.fontScale >= 1.5f) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    leading()
                    Spacer(Modifier.weight(1f))
                    actions()
                }
                heading(Modifier.fillMaxWidth())
            }
        } else {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically) {
                leading()
                heading(Modifier.weight(1f))
                actions()
            }
        }
    }
}

@Composable
internal fun PuzzleProgressPanel(
    label: String,
    detail: String?,
    found: Int,
    total: Int,
    action: @Composable () -> Unit = {}
) {
    Column(
        Modifier.fillMaxWidth().testTag("puzzle-progress-panel").puzzlePaper()
            .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 42.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(label, color = Forest, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
                if (!detail.isNullOrBlank()) {
                    Text(detail, color = Muted, style = MaterialTheme.typography.labelSmall)
                }
            }
            action()
        }
        PaperProgress(found, total)
    }
}

@Composable
internal fun PuzzleTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false
) {
    GridMatesIllustratedFrame(
        modifier = modifier.semantics { role = Role.Button },
        variant = if (primary) GridMatesFrameVariant.PRIMARY_PILL else GridMatesFrameVariant.MEDIUM,
        onClick = onClick,
        enabled = enabled
    ) {
        Box(
            Modifier.align(Alignment.Center).heightIn(min = 48.dp).widthIn(min = 48.dp)
                .padding(horizontal = 11.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = if (primary) Color(0xFFFFF8E8) else Forest,
                style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
        }
    }
}

@Composable
internal fun PuzzleIconAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false,
    icon: @Composable () -> Unit
) {
    GridMatesIllustratedFrame(
        modifier = modifier.size(48.dp).semantics {
            contentDescription = label
            role = Role.Button
        },
        variant = if (primary) GridMatesFrameVariant.GREEN_ICON else GridMatesFrameVariant.MEDIUM,
        onClick = onClick,
        enabled = enabled
    ) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { icon() }
    }
}
