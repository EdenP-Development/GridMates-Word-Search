package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Called by the workflow only while the original signed v108 APK is installed. */
class Ws66PreviewUpgradeSeedAndroidTest {
    @Test fun bookmarkAndStartAnOriginalWoodlandPage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = BookQaPilot.catalog(context)
        val book = requireNotNull(catalog.book("woodland-ws57-qa-volume-two"))
        val page = book.chapters.first().pages.first()
        val progress = BookLibraryStore(context, catalog, "gridmates_wordsearch_ws57_qa_progress_v1.dat")
        val sessions = BookPageSessionStore(context, catalog, "gridmates_wordsearch_ws57_qa_sessions_v1.dat")
        assertTrue(progress.bookmark(book.id, page.id))
        assertTrue(sessions.open(book.id, page.id))
    }
}

class Ws66PreviewAndroidTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun installedPreviewRestoresV108AndExtendsTheExactLibrary() {
        val legacy = BookQaPilot.catalog(context)
        val runtime = QaBookshelfRuntime.installed(context)
        val expanded = runtime.catalog
        legacy.requireCompatibleUpgrade(expanded)
        assertEquals(81, legacy.books.sumOf { legacy.pages(it.id).size })
        assertEquals(87, expanded.books.sumOf { expanded.pages(it.id).size })
        val oldBook = requireNotNull(legacy.book(Ws57WoodlandPreview.BOOK_ID))
        val oldPage = oldBook.chapters.first().pages.first()
        assertEquals(oldPage.id, runtime.progressStore?.progress?.volumes?.get(oldBook.id)?.bookmarkedPageId)
        assertNotNull(runtime.sessionStore?.session(oldBook.id, oldPage.id))
        for (id in listOf(Ws66ChapterPreview.SOLAR, Ws66ChapterPreview.CLEARINGS)) {
            val book = requireNotNull(expanded.book(id))
            assertEquals(1, book.chapters.size)
            assertEquals(3, expanded.pages(id).size)
            for (page in expanded.pages(id)) {
                val board = BookPageSessionRules.puzzle(expanded, id, page.id)
                assertTrue(expanded.matchesPublishedPuzzle(id, page.id, board))
            }
            assertTrue(id in BookQaPilot.editorialBookIds)
        }
    }

    @Test fun solarSystemAcceptsDragAndHintAndRestoresThePage() =
        interact(Ws66ChapterPreview.SOLAR, 12)

    @Test fun quietClearingsAcceptsDragAndHintAndRestoresThePage() =
        interact(Ws66ChapterPreview.CLEARINGS, 20)

    private fun interact(bookId: String, size: Int) {
        val catalog = QaBookshelfRuntime.installed(context).catalog
        val page = catalog.pages(bookId).first()
        val board = BookPageSessionRules.puzzle(catalog, bookId, page.id)
        assertEquals(size, board.size)
        val progressName = "ws66_progress_${System.nanoTime()}.dat"
        val sessionName = "ws66_sessions_${System.nanoTime()}.dat"
        try {
            val progress = BookLibraryStore(context, catalog, progressName)
            val sessions = BookPageSessionStore(context, catalog, sessionName)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(720.dp)) {
                        BookPageGameplayScreen(catalog = catalog, bookId = bookId, pageId = page.id,
                            sessionStore = sessions, progressStore = progress, boardFontId = "classic",
                            onBack = {}, showPrototypeArt = true)
                    }
                }
            }
            compose.waitForIdle()
            compose.onNodeWithTag("ws14h-hint-lightbulb").assertIsDisplayed().performClick()
            val target = board.targets.first()
            val first = target.positions.first()
            val last = target.positions.last()
            compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
                val cellW = width.toFloat() / board.size
                val cellH = height.toFloat() / board.size
                swipe(Offset((first.col + .5f) * cellW, (first.row + .5f) * cellH),
                    Offset((last.col + .5f) * cellW, (last.row + .5f) * cellH),
                    durationMillis = 400L)
            }
            compose.waitForIdle()
            val restored = requireNotNull(BookPageSessionStore(context, catalog, sessionName)
                .session(bookId, page.id))
            assertTrue(target.id in restored.foundIds)
            assertEquals(1, restored.hints.values.sum())
        } finally {
            listOf(progressName, sessionName).forEach { name ->
                listOf("", ".bak", ".new").forEach { suffix ->
                    File(context.filesDir, name + suffix).delete()
                }
            }
        }
    }
}
