package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordTarget
import org.junit.Rule
import org.junit.Test

/** Both phases run on the same disposable emulator with the app stopped between them. */
class QaWoodlandBookJourneyAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val catalog = BookQaPilot.catalog()
    private val bookId = "woodland-qa-volume-one"
    private val pages = catalog.pages(bookId)

    @Test fun startAndPauseWoodland() {
        openWoodland(captureShelf = true)
        compose.onNodeWithTag("book-continue").performScrollTo().performClick()
        val board = board(0)
        trace(board, board.targets.first())
        assertFound(board, 1)
        compose.onNodeWithTag("ws14k-back-to-book").performClick()
        compose.onNodeWithText("Resume page 1").assertExists()
        compose.onNodeWithTag("ws15-back").performClick()
        compose.onNodeWithTag("ws16at-back").performClick()
    }

    @Test fun reopenAndCompleteAllWoodlandPages() {
        openWoodland()
        compose.onNodeWithText("Resume page 1").assertExists()
        compose.onNodeWithTag("book-continue").performScrollTo().performClick()
        val first = board(0)
        assertFound(first, 1)
        first.targets.drop(1).forEach { trace(first, it) }
        assertFound(first, first.targets.size)

        for (index in 1 until pages.size) {
            compose.onNodeWithText("Next page").assertIsDisplayed().performClick()
            val board = board(index)
            board.targets.forEach { trace(board, it) }
            assertFound(board, board.targets.size)
        }
        compose.onNodeWithTag("ws14k-back-to-book").performClick()
        compose.onNodeWithText("3 of 3 pages completed").assertIsDisplayed()
        compose.onNodeWithTag("book-volume-complete-" + bookId).assertIsDisplayed()
        screenshot("woodland-complete.png")
        compose.onNodeWithTag("ws15-back").performClick()
        compose.onNodeWithTag("ws16at-back").performClick()
    }

    @Test fun reopenCompletedWoodland() {
        openWoodland()
        compose.onNodeWithText("3 of 3 pages completed").assertIsDisplayed()
        compose.onNodeWithTag("book-volume-complete-" + bookId).assertIsDisplayed()
    }

    private fun openWoodland(captureShelf: Boolean = false) {
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        if (captureShelf) screenshot("woodland-shelf.png")
        compose.onNodeWithTag("ws16bh-spine-" + bookId).assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
    }

    private fun board(index: Int) = BookPageSessionRules.puzzle(catalog, bookId, pages[index].id)

    private fun assertFound(board: WordSearchPuzzle, count: Int) {
        compose.onNodeWithText("$count of ${board.targets.size} words found", substring = true)
            .assertExists()
    }

    private fun trace(board: WordSearchPuzzle, word: WordTarget) {
        val start = word.positions.first()
        val end = word.positions.last()
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val cellX = width.toFloat() / board.size
            val cellY = height.toFloat() / board.size
            swipe(
                start = Offset((start.col + 0.5f) * cellX, (start.row + 0.5f) * cellY),
                end = Offset((end.col + 0.5f) * cellX, (end.row + 0.5f) * cellY),
                durationMillis = 400L
            )
        }
    }

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            instrumentation.targetContext.openFileOutput(name, 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
