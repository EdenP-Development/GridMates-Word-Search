package com.playereden.gridmates.wordsearch

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.WordTheme
import kotlin.math.roundToInt

internal class LibraryBindingArt(
    val playable: Map<WordTheme, ImageBitmap>,
    val decorative: List<ImageBitmap>
)

@Composable
internal fun rememberLibraryBindingArt(): LibraryBindingArt? {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            fun load(path: String): ImageBitmap =
                context.assets.open(path).use {
                    checkNotNull(BitmapFactory.decodeStream(it)).asImageBitmap()
                }
            LibraryBindingArt(
                WordTheme.entries.associateWith { theme ->
                    load(requireNotNull(matchedQaBookArtPath(theme, BookArtKind.SPINE)))
                },
                listOf("tobacco", "auburn", "slate").map { load("ws35/spines/$it.png") }
            )
        }.getOrNull()
    }
}

@Composable
internal fun LibraryBindingShelf(
    books: List<PuzzleBook>,
    art: LibraryBindingArt,
    shelfIndex: Int,
    bookSlot: Dp,
    height: Dp,
    onBookSelected: (String) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth().height(height)) {
        // Only real catalog books reserve touch cells. Distribute background
        // books across the remaining width without adding pages or interactions.
        val room = (maxWidth - bookSlot * books.size).coerceAtLeast(0.dp)
        val decorativeCount = if (room >= 18.dp)
            (room.value / 32.dp.value).roundToInt().coerceIn(1, 12) else 0
        val decorativeSlot = if (decorativeCount > 0) room / decorativeCount else 0.dp
        val bindingHeight = (maxHeight - 6.dp).coerceAtLeast(12.dp)
        Row(
            Modifier.align(Alignment.BottomCenter)
                .testTag("ws35-binding-shelf-$shelfIndex"),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            books.forEach { book ->
                val bitmap = art.playable.getValue(book.theme)
                val title = book.title.removeSuffix(" · TEST BOOK")
                val heightScale = when (book.theme) {
                    WordTheme.WOODLAND -> .93f
                    WordTheme.SPACE -> 1f
                    WordTheme.GARDEN -> .96f
                }
                val size = fittedBindingHeight(bitmap, bookSlot - 3.dp, bindingHeight) * heightScale
                val interaction = remember(book.id) { MutableInteractionSource() }
                val pressed by interaction.collectIsPressedAsState()
                val lift by animateFloatAsState(
                    if (pressed) 1f else 0f,
                    tween(if (ValueAnimator.areAnimatorsEnabled()) 140 else 0),
                    label = "library-binding"
                )
                Box(
                    Modifier.width(bookSlot).height(size.coerceAtLeast(48.dp))
                        .semantics { contentDescription = "Open $title" }
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = { onBookSelected(book.id) }
                        )
                        .testTag("ws16bh-spine-" + book.id),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ShelfBinding(bitmap, size, title, lift)
                }
            }
            repeat(decorativeCount) { index ->
                val style = shelfIndex + index
                val bitmap = art.decorative[style % art.decorative.size]
                val heightScale = when (style % 4) {
                    0 -> .86f
                    1 -> 1f
                    2 -> .92f
                    else -> .97f
                }
                val size = fittedBindingHeight(bitmap, decorativeSlot - 1.dp, bindingHeight) *
                    heightScale
                Box(Modifier.width(decorativeSlot).clearAndSetSemantics { },
                    contentAlignment = Alignment.BottomCenter) {
                    ShelfBinding(bitmap, size)
                }
            }
        }
    }
}

private fun fittedBindingHeight(image: ImageBitmap, maxWidth: Dp, maxHeight: Dp): Dp =
    minOf(maxHeight, maxWidth * (image.height.toFloat() / image.width))

@Composable
private fun ShelfBinding(
    bitmap: ImageBitmap,
    height: Dp,
    title: String? = null,
    lift: Float = 0f
) {
    val width = height * (bitmap.width.toFloat() / bitmap.height)
    Box(Modifier.size(width, height).offset(y = 2.dp)) {
        Canvas(Modifier.matchParentSize()) {
            val spread = 4.dp.toPx()
            val contact = 5.dp.toPx()
            val shadowColor = Color(0xFF160C07)
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    0f to shadowColor.copy(alpha = .36f),
                    .68f to shadowColor.copy(alpha = .38f),
                    1f to Color.Transparent,
                    startX = size.width * .55f,
                    endX = size.width + spread
                ),
                topLeft = Offset(size.width * .55f, size.height * .035f),
                size = Size(size.width * .45f + spread, size.height * .98f),
                cornerRadius = CornerRadius(spread)
            )
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, shadowColor.copy(alpha = .75f - lift * .22f),
                        Color.Transparent),
                    startY = size.height - contact,
                    endY = size.height + contact
                ),
                topLeft = Offset(-spread, size.height - contact),
                size = Size(size.width + spread * 2, contact * 2),
                cornerRadius = CornerRadius(contact)
            )
        }
        Box(Modifier.fillMaxSize().graphicsLayer {
            translationY = -3.dp.toPx() * lift
            scaleX = 1f + .028f * lift
            scaleY = 1f + .028f * lift
        }) {
            Image(bitmap, contentDescription = null, contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize())
            if (title != null) LibraryBindingTitle(title, width)
        }
    }
}

@Composable
private fun LibraryBindingTitle(title: String, bindingWidth: Dp) {
    val density = LocalDensity.current
    val fontSize = (bindingWidth * .34f).coerceIn(9.dp, 17.dp)
    val style = with(density) {
        TextStyle(
            color = Color(0xFFF9E1AF),
            fontSize = fontSize.toSp(),
            lineHeight = (fontSize * 1.2f).toSp(),
            fontFamily = GridMatesStoryTypeface,
            fontWeight = FontWeight.SemiBold,
            shadow = Shadow(Color(0xB9231608), Offset(0f, 1.dp.toPx()), 1.dp.toPx())
        )
    }
    Layout(
        content = {
            Text(title, modifier = Modifier.clearAndSetSemantics { },
                style = style, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        modifier = Modifier.fillMaxSize()
    ) { children, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val label = children.single().measure(Constraints(maxWidth = (height * .51f).roundToInt()))
        layout(width, height) {
            // Center rotated text on the leather face, inside the raised edge.
            label.placeWithLayer(
                (width * .44f - label.width / 2f).roundToInt(),
                (height * .58f - label.height / 2f).roundToInt()
            ) { rotationZ = 90f }
        }
    }
}
