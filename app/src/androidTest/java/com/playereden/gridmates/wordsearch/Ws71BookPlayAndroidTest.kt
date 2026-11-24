package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordTarget
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

/** Runs on the signed app. The 200% text check reopens the first test's completed page. */
class Ws71BookPlayAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val bookId = "woodland-qa-volume-one"
    private val detail get() = compose.onNodeWithTag("book-detail-list")
    private val start get() = compose.onNodeWithTag("book-continue")
    private val catalog get() = QaBookshelfRuntime.installed(context).catalog
    private val progressJournal get() =
        File(context.filesDir, "gridmates_wordsearch_ws66_v110_qa_progress_v1.dat")
    private val sessionJournal get() =
        File(context.filesDir, "gridmates_wordsearch_ws66_v110_qa_sessions_v1.dat")

    @Test fun completeReviewPracticeAndContinueFromTheLibrary() {
        checkDisplay(widthDp = 393, fontScale = 1f)
        val actualCatalog = catalog
        val pages = actualCatalog.pages(bookId)
        val board = BookPageSessionRules.puzzle(actualCatalog, bookId, pages.first().id)

        openWoodland()
        start.assertIsDisplayed().performClick()
        trace(board, board.targets.first())
        assertFound(board, 1)
        compose.onNodeWithContentDescription("Back to book").performClick()
        compose.onNodeWithText("Resume page 1").assertIsDisplayed()
        capture("resume")

        start.performClick()
        assertFound(board, 1)
        board.targets.drop(1).forEach { trace(board, it) }
        compose.onNodeWithText("Page complete").assertIsDisplayed()
        compose.onNodeWithText("1 of 42 pages completed").assertIsDisplayed()
        compose.onNodeWithText("Elapsed", substring = true).assertDoesNotExist()
        compose.onNodeWithTag("word-grid-touch-area").assertDoesNotExist()
        capture("result")

        val savedProgress = progressJournal.readBytes()
        val savedSessions = sessionJournal.readBytes()
        compose.onNodeWithTag("book-result-review").performClick()
        compose.onNodeWithTag("word-grid-touch-area").assertIsDisplayed()
        assertFound(board, board.targets.size)
        compose.onNodeWithContentDescription("Back to results").performClick()
        compose.onNodeWithText("Page complete").assertIsDisplayed()

        compose.onNodeWithTag("book-result-practice").performClick()
        assertFound(board, 0)
        trace(board, board.targets.first())
        assertFound(board, 1)
        compose.onNodeWithText("Finish practice").performClick()
        compose.onNodeWithText("Page complete").assertIsDisplayed()
        assertTrue("Review and Practice changed the book completion journal",
            savedProgress.contentEquals(progressJournal.readBytes()))
        assertTrue("Review and Practice changed the session journal",
            savedSessions.contentEquals(sessionJournal.readBytes()))

        compose.onNodeWithTag("book-result-primary").assertIsDisplayed().performClick()
        compose.onNodeWithText("Page 2").assertIsDisplayed()
        compose.onNodeWithTag("word-grid-touch-area").assertIsDisplayed()
        val second = BookPageSessionRules.puzzle(actualCatalog, bookId, pages[1].id)
        assertFound(second, 0)
        capture("next-page")
    }

    @Test fun chaptersAndResultsRemainReachableAtLargeText() {
        checkDisplay(widthDp = 320, fontScale = 2f)
        val visibleCatalog = QaBookshelfRuntime.installed(context).shelf.catalog
        val book = requireNotNull(visibleCatalog.book(bookId))
        openWoodland()
        compose.onNodeWithTag("book-chapters").assertIsDisplayed().performClick()
        compose.onNodeWithTag("book-chapter-picker").assertIsDisplayed()
        val lastChapter = book.chapters.last()
        val jump = compose.onNodeWithTag("book-chapter-jump-${lastChapter.id}")
        jump.performScrollTo().assertIsDisplayed()
        capture("chapters-200")
        jump.performClick()
        compose.onNodeWithText(lastChapter.title.removeSuffix(" · TEST")).assertIsDisplayed()

        detail.performScrollToNode(hasText("Review page 1"))
        compose.onNodeWithText("Review page 1").assertIsDisplayed().performClick()
        compose.onNodeWithText("Page complete").assertIsDisplayed()
        capture("result-200")
        compose.onNodeWithTag("book-result-practice").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("book-result-review").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("book-result-primary").performScrollTo().assertIsDisplayed()
    }

    private fun openWoodland() {
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithTag("ws16bh-spine-$bookId").assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
        detail.performScrollToNode(hasTestTag("book-continue"))
    }

    private fun checkDisplay(widthDp: Int, fontScale: Float) {
        val config = compose.activity.resources.configuration
        assertTrue("Expected ${widthDp}dp, received ${config.screenWidthDp}dp",
            abs(widthDp - config.screenWidthDp) <= 1)
        assertTrue("Expected font scale $fontScale, received ${config.fontScale}",
            abs(fontScale - config.fontScale) < .01f)
    }

    private fun assertFound(board: WordSearchPuzzle, count: Int) {
        compose.onNodeWithText("$count of ${board.targets.size} words found", substring = true)
            .assertIsDisplayed()
    }

    private fun trace(board: WordSearchPuzzle, word: WordTarget) {
        val start = word.positions.first()
        val end = word.positions.last()
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val cellX = width.toFloat() / board.size
            val cellY = height.toFloat() / board.size
            swipe(
                start = Offset((start.col + .5f) * cellX, (start.row + .5f) * cellY),
                end = Offset((end.col + .5f) * cellX, (end.row + .5f) * cellY),
                durationMillis = 400L
            )
        }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            context.openFileOutput("ws71-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
