package com.playereden.gridmates.wordsearch

import android.content.res.Configuration
import android.graphics.Bitmap
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
import androidx.compose.ui.test.junit4.createComposeRule
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
    @get:Rule val compose = createComposeRule()
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
            grid.performTouchInput {
                val dx = width.toFloat() / puzzle.size
                val dy = height.toFloat() / puzzle.size
                swipe(
                    start = Offset((start.col + .5f) * dx, (start.row + .5f) * dy),
                    end = Offset((end.col + .5f) * dx, (end.row + .5f) * dy),
                    durationMillis = 400L
                )
            }
            compose.waitForIdle()
            compose.onNodeWithText("1 of ${puzzle.targets.size} words found", substring = true)
                .assertExists()
            val saved = requireNotNull(
                BookPageSessionStore(context, catalog, sessionName)
                    .session(bookId, selected.id)
            )
            assertTrue(target.id in saved.foundIds)
            assertEquals(1, saved.hints.values.sum())
            capture(captureName)
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
