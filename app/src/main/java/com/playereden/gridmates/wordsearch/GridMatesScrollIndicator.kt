package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Apply outside verticalScroll so the rail stays aligned with the scroll viewport. */
internal fun Modifier.gridMatesScrollIndicator(
    state: ScrollState,
    endInset: Dp = 3.dp,
    opacity: Float = 1f
): Modifier = drawWithContent {
    drawContent()
    val range = state.maxValue
    val inset = 2.dp.toPx()
    val railHeight = size.height - inset * 2
    if (range in 1 until Int.MAX_VALUE && railHeight > 0f) {
        val railX = size.width - endInset.toPx()
        val thumbHeight = (railHeight * size.height / (size.height + range))
            .coerceAtLeast(minOf(18.dp.toPx(), railHeight))
        val top = inset + (railHeight - thumbHeight) * (state.value.toFloat() / range)
        val stroke = 2.dp.toPx()
        drawLine(Forest.copy(alpha = .18f * opacity), Offset(railX, inset),
            Offset(railX, size.height - inset), stroke, StrokeCap.Round)
        drawLine(Forest.copy(alpha = .60f * opacity), Offset(railX, top),
            Offset(railX, top + thumbHeight), stroke, StrokeCap.Round)
    }
}
