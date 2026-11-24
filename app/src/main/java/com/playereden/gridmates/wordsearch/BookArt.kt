package com.playereden.gridmates.wordsearch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.IntSize
import com.playereden.gridmates.wordsearch.core.WordTheme

/**
 * WS-12I / WS-14B2 — presentation only: locally bundled, verified illustrations.
 *
 * Draws small, pre-rasterized local PNGs from debug assets, never WebViews in
 * scrolling book lists. Prefer imported WS-14 originals when present;
 * missing WS-14 art falls back to the existing WS-12 source-backed QA studies. All six symbols have explicit stable paths. The whole
 * feature is gated by showPrototypeArt in the book flow; the release package
 * includes no ws12/raster assets and remains native text-only.
 *
 * Decorative only: the book title, page status, chapter, completion and actions
 * are always native Compose text/controls, including when assets are missing.
 */
internal enum class BookArtKind(val directory: String) {
    COVER("books"),
    SPINE("spines"),
    EMBLEM("emblems"),
    CHAPTER("chapters"),
    BOOKMARK("bookmarks"),
    COMPLETION("seals")
}

internal fun bookArtPath(
    theme: WordTheme, kind: BookArtKind, fieldNotes: Boolean = false
): String? {
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    val dir = if (fieldNotes) "ws12/raster_field_notes/" else "ws12/raster/"
    return dir + kind.directory + "/" + slug + ".png"
}

/**
 * WS-14B: exact paths accepted only from the approved-direction art importer.
 * Never fetch remote art at runtime, never replace any of the 12 portraits.
 */
internal fun ws14QaBookArtPath(theme: WordTheme, kind: BookArtKind): String? {
    val directory = when (kind) {
        BookArtKind.COVER -> "books"
        BookArtKind.SPINE -> "spines"
        else -> return null
    }
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return "ws14/$directory/$slug.png"
}

/** WS16AD: geometry-only QA factory; fallback to WS14/WS12 when absent. */
internal fun ws16adQaBookArtPath(theme: WordTheme, kind: BookArtKind): String? {
    val folder = when (kind) {
        BookArtKind.COVER -> "covers"
        BookArtKind.SPINE -> "spines"
        else -> return null
    }
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return "ws16ad/$folder/$slug.png"
}

/** WS16AE: all three source-retouched V2 covers and independent spines. */
internal fun ws16aeQaBookArtPath(theme: WordTheme, kind: BookArtKind): String? {
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return when (kind) {
        BookArtKind.COVER -> "ws16ae/covers/$slug.png"
        BookArtKind.SPINE -> "ws16ae/spines/$slug.png"
        else -> null
    }
}

/** Source-pinned V3 spine lettering cleanup. Front covers remain WS16AE V2. */
internal fun ws16afCorrectedSpinePath(theme: WordTheme, kind: BookArtKind): String? {
    if (kind != BookArtKind.SPINE) return null
    val slug = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return "ws16af/spines/$slug.png"
}

/** The current QA book edition uses the same binding on both faces of the book. */
internal fun matchedQaBookArtPath(theme: WordTheme, kind: BookArtKind): String? {
    val slug = theme.name.lowercase()
    return when (kind) {
        BookArtKind.COVER -> "ws36/covers/$slug.png"
        BookArtKind.SPINE -> "ws35/spines/$slug.png"
        else -> null
    }
}

/** Compatibility for existing bookshelf cover-availability checks. */
internal fun ws14QaCoverPath(theme: WordTheme, kind: BookArtKind): String? =
    if (kind == BookArtKind.COVER) ws14QaBookArtPath(theme, kind) else null

/** The A/B switch always keeps Study A genuine, even after importing WS-14. */
internal fun preferredWs14QaCoverPath(
    theme: WordTheme, kind: BookArtKind, fieldNotes: Boolean
): String? = if (fieldNotes) ws14QaBookArtPath(theme, kind) else null


/**
 * WS-14J candidate: ONE tiny botanical page accent, only when explicitly
 * requested by the book-page header. Missing debug art falls back to WS-12.
 * Never use this path for other screens, player icons or release assets.
 */
internal fun ws14jQaPageAccentPath(
    theme: WordTheme, kind: BookArtKind, enabledForPageHeader: Boolean
): String? = if (
    enabledForPageHeader && kind == BookArtKind.EMBLEM && theme == WordTheme.WOODLAND
) "ws14j/page-accents/woodland.png" else null

/**
 * WS-14K2 real three-theme original art. All PNGs are generated into debug
 * assets at build time from separate Git-tracked SVG masters. The release build
 * never contains this unapproved art; WS-14J/WS-12 remain available fallback.
 */
internal fun ws14k2QaGameMotifPath(
    theme: WordTheme, kind: BookArtKind, enabledForPageHeader: Boolean
): String? {
    if (!enabledForPageHeader || kind != BookArtKind.EMBLEM) return null
    val themeName = when (theme) {
        WordTheme.WOODLAND -> "woodland"
        WordTheme.SPACE -> "space"
        WordTheme.GARDEN -> "garden"
    }
    return "ws14k2/motifs/$themeName.png"
}

/** Cap bitmap memory before presenting many standing books in a lazy shelf. */
internal fun bookArtSampleSize(width: Int, height: Int): Int {
    if (width <= 0 || height <= 0) return 1
    var sample = 1
    while (width / sample > 512 || height / sample > 768) {
        sample *= 2
    }
    return sample
}

private fun decodeBookArt(context: Context, path: String): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.assets.open(path).use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    val options = BitmapFactory.Options().apply {
        inSampleSize = bookArtSampleSize(bounds.outWidth, bounds.outHeight)
        inPreferredConfig = Bitmap.Config.ARGB_8888 // Keep genuine PNG alpha intact
    }
    return context.assets.open(path).use { BitmapFactory.decodeStream(it, null, options) }
}

/** Trim clear rows below the cover while preserving the bundled bitmap. */
private fun coverHeightAboveShelf(bitmap: Bitmap): Int {
    if (!bitmap.hasAlpha()) return bitmap.height
    val pixels = IntArray(bitmap.width)
    for (y in bitmap.height - 1 downTo 0) {
        bitmap.getPixels(pixels, 0, bitmap.width, 0, y, bitmap.width, 1)
        if (pixels.any { it ushr 24 != 0 }) return y + 1
    }
    return bitmap.height
}

@Composable
internal fun BookArt(
    theme: WordTheme,
    kind: BookArtKind,
    modifier: Modifier = Modifier,
    fieldNotes: Boolean = false,
    pageAccentCandidate: Boolean = false,
    restingOnShelf: Boolean = false
) {
    val context = LocalContext.current
    val existingStudyPath = bookArtPath(theme, kind, fieldNotes) ?: return
    val preferredNewPath = preferredWs14QaCoverPath(theme, kind, fieldNotes)
    val preferredBookFactory = if (fieldNotes) ws16adQaBookArtPath(theme, kind) else null
    val preferredPaintedV2 = if (fieldNotes) ws16aeQaBookArtPath(theme, kind) else null
    val preferredCorrectedSpine = if (fieldNotes) ws16afCorrectedSpinePath(theme, kind) else null
    val preferredMatchedBook = if (fieldNotes) matchedQaBookArtPath(theme, kind) else null
    val preferredPageAccent = ws14jQaPageAccentPath(theme, kind, pageAccentCandidate)
    val preferredThreeThemes = ws14k2QaGameMotifPath(theme, kind, pageAccentCandidate)
    val bitmap = remember(context, existingStudyPath, preferredNewPath,
        preferredCorrectedSpine, preferredPaintedV2, preferredBookFactory,
        preferredPageAccent, preferredThreeThemes, preferredMatchedBook) {
        listOfNotNull(preferredThreeThemes, preferredPageAccent,
            preferredMatchedBook, preferredCorrectedSpine, preferredPaintedV2, preferredBookFactory,
            preferredNewPath, existingStudyPath)
            .firstNotNullOfOrNull { path ->
                runCatching {
                    decodeBookArt(context, path)
                }.getOrNull()
            }
    } ?: return

    val painter = remember(bitmap, restingOnShelf) {
        val height = if (restingOnShelf) coverHeightAboveShelf(bitmap) else bitmap.height
        BitmapPainter(bitmap.asImageBitmap(), srcSize = IntSize(bitmap.width, height))
    }
    Image(
        painter = painter,
        contentDescription = null, // decorative; accessible status is native text
        alignment = if (restingOnShelf) Alignment.BottomCenter else Alignment.Center,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

/**
 * Anonymous non-playable bookcase volume. Debug-only artwork source; no content
 * ID, progress or onClick. Release APK contains no ws16ad placeholder pixels.
 */
@Composable
internal fun BookFactoryBlankSpine(style: String, modifier: Modifier = Modifier) {
    if (style !in listOf("linen", "night", "burgundy")) return
    val context = LocalContext.current
    val path = "ws16ad/placeholders/$style-spine.png"
    val bitmap = remember(context, path) {
        runCatching { decodeBookArt(context, path) }.getOrNull()
    } ?: return
    Image(bitmap.asImageBitmap(), contentDescription = null,
        contentScale = ContentScale.Fit, modifier = modifier)
}
