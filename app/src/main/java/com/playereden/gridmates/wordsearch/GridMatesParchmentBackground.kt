package com.playereden.gridmates.wordsearch

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/**
 * GM_SHARED: a near-flat, screen-filling warm paper surface.
 *
 * Neither artwork nor game text is baked into a fixed-size background image.
 * The stable, very faint paper specks tile mathematically at any phone/tablet
 * size; a radial edge wash has no seams or empty gutters on wide viewports.
 *
 * Design-source counterparts:
 * design/visual-system/modular/masters/shared/gm-parchment-tile-v1.svg
 * design/visual-system/modular/masters/shared/gm-parchment-edge-wash-v1.svg
 *
 * This is a decorative drawing with no input/semantics. Other GridMates apps
 * can reuse it deliberately, without depending on Word Search or its books.
 */
/** Opt-in Home richness; ordinary screens retain the quieter series paper. */
internal enum class GridMatesPaperFinish { QUIET, STORYBOOK_HOME }

@Composable
internal fun GridMatesParchmentBackground(
    modifier: Modifier = Modifier,
    finish: GridMatesPaperFinish = GridMatesPaperFinish.QUIET
) {
    val context = LocalContext.current
    val paperTooth = remember(context) {
        runCatching {
            context.assets.open("ws16aa/parchment.png").use {
                BitmapFactory.decodeStream(it)?.asImageBitmap()
            }
        }.getOrNull()
    }
    Canvas(modifier.fillMaxSize()
        .clearAndSetSemantics { }
        .testTag("gm-shared-parchment-background")) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas
        val unit = 1.dp.toPx()
        val storybookHome = finish == GridMatesPaperFinish.STORYBOOK_HOME

        // Home-only patina; do not force decorative paper behind puzzle grids.
        drawRect(if (storybookHome) Color(0xFFF7F0E2) else Color(0xFFF5F1E7))
        drawRect(brush = Brush.verticalGradient(listOf(
            Color(0x13FFF8E9), Color(0x00FFFDF5), Color(0x0AE6CB9C)
        )))
        drawRect(brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0x09C59551),
                Color(0x1CCDA66B)),
            center = Offset(w*.51f, h*.47f),
            radius = maxOf(w, h) * .79f
        ))

        // WS-16L: the soft, irregular warmth of an aged paperback leaf.
        // These washes are deliberately lower contrast than any native text:
        // they give the paper depth, not obvious corners or a painted vignette.
        drawRect(brush = Brush.horizontalGradient(listOf(
            Color(0x13C58E4E), Color(0x02C58E4E),
            Color(0x00C58E4E), Color(0x0DBF8F58)
        )))
        drawCircle(
            brush = Brush.radialGradient(listOf(
                Color(0x13D8B77C), Color(0x00D8B77C)
            ), center = Offset(w*.09f, h*.25f),
                radius = maxOf(w*.56f, 145f*unit)),
            radius = maxOf(w*.56f, 145f*unit),
            center = Offset(w*.09f, h*.25f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(
                Color(0x0FC99A5C), Color(0x00C99A5C)
            ), center = Offset(w*.94f, h*.83f),
                radius = maxOf(w*.60f, 165f*unit)),
            radius = maxOf(w*.60f, 165f*unit),
            center = Offset(w*.94f, h*.83f)
        )

        // WS16BR: reuse the existing paper-tooth texture, but give only Home
        // a warmer watercolor edge and natural, low-contrast pigment blooms.
        if (storybookHome) {
            drawRect(brush = Brush.verticalGradient(listOf(
                Color(0x17A66D31), Color(0x04D6B278), Color(0x13AF7A39)
            )))
            for (i in 0 until 28) {
                val seed = (i * 1103515245) xor 0x6f13a
                val fx = ((seed ushr 8) and 1023) / 1023f
                val fy = ((seed ushr 19) and 1023) / 1023f
                val edgeX = if (i % 2 == 0) w * (.015f + fx * .19f)
                    else w * (.79f + fx * .195f)
                val edgeY = h * fy
                val radius = (8f + ((seed ushr 3) and 15)) * unit
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0x0DA67A44), Color.Transparent),
                        center = Offset(edgeX, edgeY), radius = radius
                    ),
                    radius = radius, center = Offset(edgeX, edgeY)
                )
            }
        }

        // Deliberately restrained, deterministic flecks: no random flicker,
        // no bitmap resampling, and no seams at unusual aspect ratios.
        val cell = 18f*unit
        val cols = ceil(w / cell).toInt().coerceAtMost(180)
        val rows = ceil(h / cell).toInt().coerceAtMost(200)
        for (y in 0..rows) {
            for (x in 0..cols) {
                val seed = (x*73856093) xor (y*19349663) xor 0x13ab9d
                val sx = ((seed ushr 4) and 255) / 255f
                val sy = ((seed ushr 13) and 255) / 255f
                val cx = (x+sx)*cell
                val cy = (y+sy)*cell
                if (cx < w && cy < h) {
                    drawCircle(
                        color = if ((seed and 1) == 0)
                            (if (storybookHome) Color(0x28A27842)
                             else Color(0x0DA27842))
                            else (if (storybookHome) Color(0x1CFFFFFF)
                             else Color(0x11FFFFFF)),
                        radius = (.29f + (seed and 7)*.08f)*unit,
                        center = Offset(cx,cy)
                    )
                }
            }
        }

        // A sparse second pass of tiny paper fibers. All positions come from
        // integer coordinates: no random changes on recomposition, animation
        // flicker, bitmap tiling seams or new assets in the release package.
        val fiberCell = 61f*unit
        val fiberCols = ceil(w/fiberCell).toInt().coerceAtMost(80)
        val fiberRows = ceil(h/fiberCell).toInt().coerceAtMost(100)
        for (y in 0..fiberRows) {
            for (x in 0..fiberCols) {
                val seed = (x*28411) xor (y*71933) xor 0x5a13
                val fx = (x + ((seed ushr 5) and 127)/128f)*fiberCell
                val fy = (y + ((seed ushr 13) and 127)/128f)*fiberCell
                if (fx < w && fy < h) {
                    val dx = (2.4f + (seed and 3))*unit
                    val dy = ((seed ushr 3) and 3)*.48f*unit
                    drawLine(
                        color = if ((seed and 1) == 0)
                            (if (storybookHome) Color(0x34B08C5A)
                             else Color(0x14B08C5A))
                            else (if (storybookHome) Color(0x22FFFFFF)
                             else Color(0x13FFFFFF)),
                        start = Offset(fx,fy),
                        end = Offset((fx+dx).coerceAtMost(w),
                            (fy+dy).coerceAtMost(h)),
                        strokeWidth = .33f*unit
                    )
                }
            }
        }

        // Home's faint close-up grain stays deterministic and is skipped
        // everywhere else; no repaint animation or bitmap/network loading.
        if (storybookHome) {
            val grainCell = 22f * unit
            val grainCols = ceil(w / grainCell).toInt().coerceAtMost(120)
            val grainRows = ceil(h / grainCell).toInt().coerceAtMost(150)
            for (y in 0..grainRows) for (x in 0..grainCols) {
                val seed = (x * 92821) xor (y * 68917) xor 0x31A74
                val cx = (x + ((seed ushr 7) and 255) / 255f) * grainCell
                val cy = (y + ((seed ushr 17) and 255) / 255f) * grainCell
                if (cx < w && cy < h && (seed and 3) != 0) {
                    drawLine(
                        color = if ((seed and 1) == 0) Color(0x20B38B50)
                            else Color(0x1AFFFFFF),
                        start = Offset(cx, cy),
                        end = Offset((cx + (1.2f + (seed and 7) * .3f) * unit)
                            .coerceAtMost(w), cy),
                        strokeWidth = .28f * unit
                    )
                }
            }
        }

        // WS16AA QA-only immutable original paper-tooth illustration.
        // The existing parchment base remains the release fallback.
        // Tile with intrinsic source proportions; never stretch one full-page
        // bitmap or let a repeat seam cross a phone safe area.
        paperTooth?.let { tile ->
            val tilePx = (128f * unit).roundToInt().coerceAtLeast(1)
            val widthPx = w.roundToInt()
            val heightPx = h.roundToInt()
            var y = 0
            while (y < heightPx) {
                var x = 0
                while (x < widthPx) {
                    val dw = minOf(tilePx, widthPx - x)
                    val dh = minOf(tilePx, heightPx - y)
                    val sw = ((dw.toFloat() / tilePx) * tile.width)
                        .roundToInt().coerceIn(1, tile.width)
                    val sh = ((dh.toFloat() / tilePx) * tile.height)
                        .roundToInt().coerceIn(1, tile.height)
                    drawImage(image = tile, srcOffset = IntOffset.Zero,
                        srcSize = IntSize(sw, sh), dstOffset = IntOffset(x, y),
                        dstSize = IntSize(dw, dh), alpha = .86f)
                    x += tilePx
                }
                y += tilePx
            }
        }

    }
}
