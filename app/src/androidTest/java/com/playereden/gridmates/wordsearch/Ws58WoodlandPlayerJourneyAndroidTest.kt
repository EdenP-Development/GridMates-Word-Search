package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * WS58: execute the real WS57 frozen QA volume's native Compose surfaces.
 * Unlike WS57's catalog/store tests, these exercise the chapter dialog and
 * actual on-screen finger traces in the V5 grid, at a narrow 320dp viewport.
 * Every test uses disposable AtomicFile names; never touch owner save files.
 * CI emulator success is NOT a claim of physical handset acceptance.
 */
class Ws58WoodlandPlayerJourneyAndroidTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val bookId = Ws57WoodlandPreview.BOOK_ID

    @Test fun sixChapterPickerScrollsToForestDiscoveriesInRealBookDetail() {
        val catalog = BookQaPilot.catalog(context)
        val book = requireNotNull(catalog.book(bookId))
        assertEquals(6, book.chapters.size)
        val progressName = "ws58_picker_" + System.nanoTime() + ".dat"
        try {
            val progress = BookLibraryStore(context, catalog, progressName)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(720.dp)) {
                        PuzzleBookDetailScreen(
                            catalog = catalog,
                            progress = progress.progress,
                            bookId = bookId,
                            onBack = {},
                            onPlayPage = { _, _ -> },
                            onBookmarkPage = { _, _ -> },
                            showPrototypeArt = true
                        )
                    }
                }
            }
            compose.waitForIdle()
            compose.onNodeWithTag("book-chapters").assertIsDisplayed().performClick()
            compose.onNodeWithTag("book-chapter-picker").assertExists()
            val last = book.chapters.last()
            compose.onNodeWithTag("book-chapter-jump-" + last.id)
                .performScrollTo().performClick()
            compose.waitForIdle()
            compose.onNodeWithText("Forest Discoveries").assertIsDisplayed()
            compose.onNodeWithTag("book-detail-list").assertExists()
            // The chapter jump is navigation, not an accidental completion
            // or bookmark write in the user's library.
            assertTrue(progress.progress.volumes[bookId]?.completedPageIds.orEmpty().isEmpty())
        } finally {
            clear(progressName)
        }
    }

    @Test fun twelveCellFrozenPreviewAcceptsRealDragHintAndRestart() {
        playFrozenPage(chapter = 0, page = 0, expectedSize = 12)
    }

    @Test fun twentyCellFrozenPreviewAcceptsRealDragHintAndRestart() {
        playFrozenPage(chapter = 5, page = 2, expectedSize = 20)
    }

    private fun playFrozenPage(chapter: Int, page: Int, expectedSize: Int) {
        val catalog = BookQaPilot.catalog(context)
        val book = requireNotNull(catalog.book(bookId))
        val selected = book.chapters[chapter].pages[page]
        val puzzle = BookPageSessionRules.puzzle(catalog, bookId, selected.id)
        assertEquals(expectedSize, puzzle.size)
        val progressName = "ws58_progress_" + System.nanoTime() + ".dat"
        val sessionName = "ws58_session_" + System.nanoTime() + ".dat"
        try {
            val progress = BookLibraryStore(context, catalog, progressName)
            val sessions = BookPageSessionStore(context, catalog, sessionName)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(720.dp)) {
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
            compose.waitForIdle()
            compose.onNodeWithText("0 of ${puzzle.targets.size} words found", substring = true)
                .assertExists()
            compose.onNodeWithTag("ws14h-hint-lightbulb").assertIsDisplayed()
                .performClick()
            compose.runOnIdle {
                assertEquals(1, sessions.session(bookId, selected.id)?.hints?.values?.sum())
            }

            // Send a real pointer swipe through the ACTUAL Compose board.
            // Callers never cheat by directly invoking BookPageSessionStore.claim.
            val target = puzzle.targets.first()
            val first = target.positions.first()
            val last = target.positions.last()
            compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
                val cellW = width.toFloat() / puzzle.size
                val cellH = height.toFloat() / puzzle.size
                swipe(
                    start = Offset((first.col + .5f) * cellW, (first.row + .5f) * cellH),
                    end = Offset((last.col + .5f) * cellW, (last.row + .5f) * cellH),
                    durationMillis = 400L
                )
            }
            compose.waitForIdle()
            compose.onNodeWithText("1 of ${puzzle.targets.size} words found", substring = true)
                .assertExists()
            val reopened = BookPageSessionStore(context, catalog, sessionName)
            val saved = requireNotNull(reopened.session(bookId, selected.id))
            assertTrue(target.id in saved.foundIds)
            assertEquals(1, saved.hints.values.sum())
            assertTrue(progress.progress.volumes[bookId]?.completedPageIds.orEmpty().isEmpty())
        } finally {
            clear(progressName, sessionName)
        }
    }

    private fun clear(vararg names: String) {
        names.forEach { name ->
            listOf("", ".bak", ".new").forEach { suffix ->
                File(context.filesDir, name + suffix).delete()
            }
        }
    }
}
