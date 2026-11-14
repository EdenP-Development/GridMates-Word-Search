package com.playereden.gridmates.wordsearch

import android.animation.ValueAnimator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * WS16BK — bookcase paging hardware, replacing redundant book-count label.
 * WS16BO: no extra walnut-and-brass panel; retain native green buttons over cabinet wood.
 * Inputs: the real HorizontalPager; no invented ownership state or fake taps.
 * WS16BL adds short physical feedback to native arrows and status-only dots.
 */
@Composable
internal fun WS16BKBookcasePager(
    currentPage: Int,
    pageCount: Int,
    styled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    require(pageCount >= 1)
    val selected = currentPage.coerceIn(0, pageCount - 1)
    // WS16BO: no decorative backplate. The cabinet backboard itself is the
    // surface behind the green paging buttons and readable page dots.
    // Keep the same 54dp button targets, pager bounds and page callbacks.
    Box(
        modifier.width(242.dp).height(62.dp)
            .semantics { contentDescription = "Bookcase ${selected + 1} of $pageCount" }
            .testTag("ws16bk-bookcase-pager"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WS16BIAdaptiveControl(
                glyph = Ws16AZCabinetGlyph.PREVIOUS,
                description = "Previous bookcase",
                styled = styled,
                enabled = selected > 0,
                onClick = onPrevious,
                modifier = Modifier.testTag("ws16at-prev")
            )
            WS16BKPageDots(
                currentPage = selected,
                pageCount = pageCount,
                modifier = Modifier.weight(1f)
            )
            WS16BIAdaptiveControl(
                glyph = Ws16AZCabinetGlyph.NEXT,
                description = "Next bookcase",
                styled = styled,
                enabled = selected < pageCount - 1,
                onClick = onNext,
                modifier = Modifier.testTag("ws16at-next")
            )
        }
    }
}

/**
 * Compact 7-dot sliding viewport for very large real catalogs.
 * All pages stay reachable by arrows/swipe. Dots are indicators, not
 * undersized touch buttons; the row offers one spoken page-position label.
 */
@Composable
private fun WS16BKPageDots(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    val motionAllowed = ValueAnimator.areAnimatorsEnabled()
    val visible = minOf(pageCount, 7)
    val first = (currentPage - visible / 2)
        .coerceIn(0, pageCount - visible)
    Row(
        modifier.semantics {
            contentDescription = "Page ${currentPage + 1} of $pageCount"
        }.testTag("ws16bk-page-dots"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(visible) { i ->
            val page = first + i
            val active = page == currentPage
            val selectedAmount by animateFloatAsState(
                targetValue = if (active) 1f else 0f,
                animationSpec = tween(durationMillis = if (motionAllowed) 165 else 0),
                label = "ws16bl-active-page-dot"
            )
            Canvas(Modifier.size(16.dp).testTag("ws16bk-dot-$page")) {
                val radius = (2.8f + 1.5f * selectedAmount).dp.toPx()
                // Library dot colors now derive from GridMates shared
                // forest/cream/gold roles rather than a separate brass UI.
                drawCircle(
                    color = Color(0xFF9EAA95),
                    radius = 2.8.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = StorybookTokens.warmPaper,
                    radius = radius,
                    center = center,
                    alpha = selectedAmount
                )
                if (selectedAmount > 0f) {
                    drawCircle(
                        color = Gold,
                        radius = radius + 1.3.dp.toPx(),
                        center = center,
                        style = Stroke(1.dp.toPx()),
                        alpha = selectedAmount
                    )
                }
            }
        }
    }
}
