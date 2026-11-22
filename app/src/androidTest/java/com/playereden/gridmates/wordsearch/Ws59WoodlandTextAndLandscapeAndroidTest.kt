package com.playereden.gridmates.wordsearch

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlin.math.abs
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * WS59: genuine WS57 Woodland editorial QA content under text enlargement and
 * a landscape-sized Compose viewport. This tests *layout/semantics and gesture
 * reachability*, not TalkBack speech, real rotation, or user phone readability.
 *
 * These are debug-only Android instrumentation tests; save filenames are
 * unique and disposable. No production board IDs, vocabulary or save schema.
 */
class Ws59WoodlandTextAndLandscapeAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val bookId = Ws57WoodlandPreview.BOOK_ID

    @Test fun at200PercentTextAllSixChaptersAndLastPageActionsStayReachable() {
        val catalog = BookQaPilot.catalog(context)
        val book = requireNotNull(catalog.book(bookId))
        assertEquals(6, book.chapters.size)
        val lastChapter = book.chapters.last()
        val lastChapterFirstPage = lastChapter.pages.first()
        val lastPageNumber = catalog.pages(bookId).indexOf(lastChapterFirstPage) + 1
        var requestedPage: String? = null
        var requestedBookmark: String? = null

        compose.setContent {
            val original = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(original.density, 2.0f)
            ) {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(640.dp)) {
                        PuzzleBookDetailScreen(
                            catalog = catalog,
                            progress = com.playereden.gridmates.wordsearch.core.BookLibraryProgress(),
                            bookId = bookId,
                            onBack = {},
                            onPlayPage = { _, id -> requestedPage = id },
                            onBookmarkPage = { _, id -> requestedBookmark = id },
                            showPrototypeArt = true
                        )
                    }
                }
            }
        }
        val chapters = compose.onNodeWithTag("book-chapters")
        chapters.assertIsDisplayed().performClick()
        compose.onNodeWithTag("book-chapter-jump-" + lastChapter.id)
            .performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText(lastChapter.title).assertIsDisplayed()
        compose.onNodeWithTag("book-detail-list").assertExists()
        compose.onNodeWithContentDescription("Play page $lastPageNumber")
            .performScrollTo().assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(lastChapterFirstPage.id, requestedPage) }
        compose.onNodeWithContentDescription("Bookmark page $lastPageNumber")
            .performScrollTo().assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(lastChapterFirstPage.id, requestedBookmark) }
        capture("chapter-200-percent")
    }

    @Test fun at200PercentTextExpertPortraitRetainsHintToolsAndGrid() {
        playViewport(
            chapter = 5, page = 2, cells = 20,
            width = 320, height = 740, fontScale = 2.0f,
            captureName = "expert-portrait-200-percent"
        )
    }

    @Test fun at150PercentTextClassicLandscapeRetainsHintToolsAndGrid() {
        playViewport(
            chapter = 3, page = 1, cells = 15,
            width = 700, height = 340, fontScale = 1.5f,
            captureName = "classic-landscape-150-percent"
        )
    }

    /** Real row/column accessibility path; never directly claim via core store. */
    @Test fun at200PercentTextExpertPrecisePickerClaimsRealWord() {
        val catalog = BookQaPilot.catalog(context)
        val selected = requireNotNull(catalog.book(bookId)).chapters[5].pages[2]
        val puzzle = BookPageSessionRules.puzzle(catalog, bookId, selected.id)
        assertEquals(20, puzzle.size)
        val target = puzzle.targets.minByOrNull {
            val a = it.positions.first()
            val b = it.positions.last()
            a.row + a.col + abs(b.row - a.row) + abs(b.col - a.col)
        } ?: error("Expert board has no words")
        val suffix = System.nanoTime()
        val progressName = "ws59_precise_progress_$suffix.dat"
        val sessionName = "ws59_precise_session_$suffix.dat"
        try {
            val progress = BookLibraryStore(context, catalog, progressName)
            val sessions = BookPageSessionStore(context, catalog, sessionName)
            compose.setContent {
                val original = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(original.density, 2f)) {
                    GridMatesTheme {
                        Box(Modifier.width(320.dp).height(740.dp)) {
                            BookPageGameplayScreen(catalog = catalog, bookId = bookId,
                                pageId = selected.id, sessionStore = sessions,
                                progressStore = progress, boardFontId = "classic",
                                onBack = {}, showPrototypeArt = true)
                        }
                    }
                }
            }
            compose.waitForIdle()
            compose.onNodeWithContentDescription("Puzzle tools").assertIsDisplayed().performClick()
            compose.onNodeWithText("Select by row and column").performClick()
            compose.onNodeWithText("Select letters precisely").assertExists()
            capture("precise-portrait-200-percent-before")
            fun move(axis: String, delta: Int) {
                val direction = if (delta >= 0) "Next" else "Previous"
                repeat(abs(delta)) {
                    compose.onNodeWithContentDescription("$direction $axis")
                        .performScrollTo().performClick()
                }
            }
            val first = target.positions.first()
            val last = target.positions.last()
            move("row", first.row)
            move("column", first.col)
            compose.onNodeWithText("Set start").performClick()
            move("row", last.row - first.row)
            move("column", last.col - first.col)
            compose.onNodeWithText("Mark word").performClick()
            compose.waitForIdle()
            capture("precise-portrait-200-percent-after")
            val found = BookPageSessionStore(context, catalog, sessionName)
                .session(bookId, selected.id)?.foundIds.orEmpty()
            Log.i("WS59_DIAG", "precise target=${target.id} first=$first last=$last found=$found")
            assertTrue("Precise picker did not persist ${target.id}: found=$found",
                target.id in found)
            compose.onNodeWithText("1 / ${puzzle.targets.size} found")
                .assertIsDisplayed()
            compose.onNodeWithContentDescription(
                "1 of ${puzzle.targets.size} words found"
            ).assertExists()
        } finally {
            listOf(progressName, sessionName).forEach { name ->
                listOf("", ".bak", ".new").forEach { suffix ->
                    File(context.filesDir, name + suffix).delete()
                }
            }
        }
    }

    private fun playViewport(
        chapter: Int, page: Int, cells: Int,
        width: Int, height: Int, fontScale: Float, captureName: String
    ) {
        val catalog = BookQaPilot.catalog(context)
        val selected = requireNotNull(catalog.book(bookId)).chapters[chapter].pages[page]
        val puzzle = BookPageSessionRules.puzzle(catalog, bookId, selected.id)
        assertEquals(cells, puzzle.size)
        val nonce = System.nanoTime()
        val progressName = "ws59_progress_$nonce.dat"
        val sessionName = "ws59_session_$nonce.dat"
        try {
            val progress = BookLibraryStore(context, catalog, progressName)
            val sessions = BookPageSessionStore(context, catalog, sessionName)
            // The system's user-rotation setting alone does not guarantee
            // that the Compose test host Activity rotated. Explicitly request
            // landscape on the *actual Android Activity* before setContent.
            if (width > height) {
                compose.activity.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                compose.waitUntil(timeoutMillis = 15000L) {
                    compose.activity.resources.configuration.orientation ==
                        Configuration.ORIENTATION_LANDSCAPE
                }
                Log.i("WS59_DIAG", "activity orientation confirmed LANDSCAPE for $captureName")
            }
            compose.setContent {
                val original = LocalDensity.current
                if (width > height) {
                    check(LocalConfiguration.current.orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) {
                        "WS59 landscape case requires a physically rotated Android display"
                    }
                }
                CompositionLocalProvider(
                    LocalDensity provides Density(original.density, fontScale)
                ) {
                    GridMatesTheme {
                        Box(Modifier.width(width.dp).height(height.dp)) {
                            BookPageGameplayScreen(
                                catalog = catalog,
                                bookId = bookId,
                                pageId = selected.id,
                                sessionStore = sessions,
                                progressStore = progress,
                                boardFontId = "classic",
                                onBack = {},
                                showPrototypeArt = true
                            )
                        }
                    }
                }
            }
            compose.waitForIdle()
            if (fontScale >= 1.5f && width <= height) {
                val pageNumber = catalog.pages(bookId).indexOf(selected) + 1
                compose.onNodeWithText("Page $pageNumber").assertIsDisplayed()
                compose.onNodeWithContentDescription(
                    "${requireNotNull(catalog.book(bookId)).title.removeSuffix(" · TEST BOOK")}, " +
                        "${puzzle.displayTitle}, Page $pageNumber"
                ).assertExists()
                compose.onNodeWithText("0 / ${puzzle.targets.size} found").assertIsDisplayed()
            }
            compose.onNodeWithContentDescription("Back to book").assertIsDisplayed()
            compose.onNodeWithContentDescription("Puzzle tools")
                .assertIsDisplayed().performClick()
            compose.onNodeWithText("Select by row and column").assertExists()
            compose.onNodeWithText("Done").performClick()

            val grid = compose.onNodeWithTag("word-grid-touch-area")
            grid.assertIsDisplayed()
            val bounds = grid.getUnclippedBoundsInRoot()
            assertTrue("Grid must retain a usable width at enlarged text", bounds.right - bounds.left >= 130.dp)
            assertTrue("Grid must retain a usable height at enlarged text", bounds.bottom - bounds.top >= 130.dp)

            compose.onNodeWithTag("ws14h-hint-lightbulb")
                .assertIsDisplayed().performClick()
            compose.runOnIdle {
                assertEquals(1, sessions.session(bookId, selected.id)?.hints?.values?.sum())
            }

            val target = puzzle.targets.first()
            val start = target.positions.first()
            val end = target.positions.last()
            capture("$captureName-before")
            var measuredGrid = "unmeasured"
            grid.performTouchInput {
                // The enclosing function also has width/height Int parameters.
                // Explicitly measure the REAL grid TouchInjectionScope.
                val gridWidth = this.width.toFloat()
                val gridHeight = this.height.toFloat()
                measuredGrid = "${gridWidth}x${gridHeight}px"
                val dx = gridWidth / puzzle.size
                val dy = gridHeight / puzzle.size
                swipe(
                    start = Offset((start.col + .5f) * dx, (start.row + .5f) * dy),
                    end = Offset((end.col + .5f) * dx, (end.row + .5f) * dy),
                    durationMillis = 400L
                )
            }
            compose.waitForIdle()
            capture("$captureName-after-swipe")
            val saved = BookPageSessionStore(context, catalog, sessionName)
                .session(bookId, selected.id)
            val found = saved?.foundIds.orEmpty()
            Log.i("WS59_DIAG", "drag case=$captureName page=${selected.id} target=${target.id} " +
                "start=$start end=$end grid=$measuredGrid screen=${width}x${height}dp " +
                "font=$fontScale found=$found hints=${saved?.hints}")
            assertTrue(
                "Swipe did not persist target ${target.id}: found=$found, " +
                    "grid=$measuredGrid, screen=${width}x${height}dp",
                target.id in found
            )
            assertEquals(1, saved?.hints?.values?.sum())
            val compactReadability = fontScale >= 1.5f && width <= height
            compose.onNodeWithText(
                if (compactReadability) "1 / ${puzzle.targets.size} found"
                else "1 of ${puzzle.targets.size} words found"
            ).assertIsDisplayed()
            if (compactReadability) {
                compose.onNodeWithContentDescription(
                    "1 of ${puzzle.targets.size} words found"
                ).assertExists()
            }
        } finally {
            listOf(progressName, sessionName).forEach { name ->
                listOf("", ".bak", ".new").forEach { suffix ->
                    File(context.filesDir, name + suffix).delete()
                }
            }
        }
    }

    /** Export real emulator screenshots for later human review, not approvals. */
    private fun capture(name: String) {
        compose.waitForIdle()
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val bitmap = checkNotNull(automation.takeScreenshot()) {
            "WS59 Android screenshot could not be captured"
        }
        try {
            val file = File(checkNotNull(context.getExternalFilesDir(null)), "ws59-$name.png")
            file.outputStream().use { stream ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
