package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.DailyChallenge
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordHintPolicy
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val savedDate = "2026-11-05"

/** Runs against the original signed v116 on the disposable emulator. */
class Ws74V116DailySeedAndroidTest {
    @Test fun saveOneWordInAnEarlierDaily() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = SoloStore(context)
        store.openDaily(savedDate)
        val puzzle = SoloProgressRules.puzzle(requireNotNull(store.selectedDaily).run)
        val first = puzzle.targets.first()
        assertEquals(ClaimStatus.FOUND,
            store.dailyClaim(first.positions.first(), first.positions.last()).status)
    }
}

/** Opens the installed application and drives its normal Home, game and result routes. */
class Ws74SoloDailyAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private fun saved() = SoloStore(context).profile

    @Test fun reproduceV116LosingTheSelectedDate() {
        openEarlierDaily()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("No Daily puzzle selected.").assertIsDisplayed()
        capture("v116-daily-recreation")
    }

    @Test fun earlierDailyKeepsItsDateAndProgressAfterRecreation() {
        val before = saved().dailyEntries.single { it.date == savedDate }.run
        val board = openEarlierDaily()
        compose.activityRule.scenario.recreate()
        assertFound(board, 1)
        trace(board, board.targets[1])
        assertFound(board, 2)
        val entry = saved().dailyEntries.single()
        assertEquals(savedDate, entry.date)
        assertEquals(before.puzzleId, entry.run.puzzleId)
        assertEquals(board.targets.take(2).map { it.id }.toSet(), entry.run.foundIds)
        capture("v117-earlier-daily")
        dailyBackToHome()
    }

    @Test fun todayResumesAClueThenFinishesAndReviewsOnce() {
        val today = DailyChallenge.today()
        val run = DailyChallenge.dailyRun(today)
        val puzzle = SoloProgressRules.puzzle(run)
        val initialSolo = saved().active
        val hints = WordHintPolicy.remaining(run, daily = true)

        openDaily()
        todayAction("Play today").performScrollTo().assertIsDisplayed().performClick()
        assertFound(puzzle, 0)
        compose.onNodeWithText("Hint ($hints)").assertIsDisplayed().performClick()
        compose.onNodeWithText("Daily").performClick()
        todayAction("Continue").assertIsDisplayed()
        compose.onNodeWithText("0 of ${puzzle.targets.size} found").assertIsDisplayed()
        capture("v117-daily-clue-resume")

        todayAction("Continue").performClick()
        compose.onNodeWithText("Hint (${hints - 1})").assertIsDisplayed()
        val started = saved().dailyEntries.single { it.date == today }
        assertEquals(run.puzzleId, started.run.puzzleId)
        assertEquals(1, started.run.hints.values.sum())
        puzzle.targets.forEach { trace(puzzle, it) }
        compose.onNodeWithText("Daily complete!").assertIsDisplayed()
        capture("v117-daily-result")
        val completion = saved().dailyEntries.single { it.date == today }
        assertTrue(completion.completed)

        compose.onNode(hasClickAction() and hasText("Daily journal"))
            .performScrollTo().performClick()
        compose.onNodeWithText("Completed today").assertIsDisplayed()
        todayAction("Review").performClick()
        compose.onNodeWithText("Review completed board").performScrollTo().performClick()
        assertFound(puzzle, puzzle.targets.size)
        dailyBackToHome()

        val profile = saved()
        assertEquals(1, profile.dailyCompletions)
        assertEquals(completion, profile.dailyEntries.single { it.date == today })
        assertEquals(initialSolo, profile.active)
    }

    @Test fun quickPlayResumesThenStartsAFreshPuzzleAtTheSameSize() {
        compose.onNodeWithTag("ws39-quick-play").performScrollTo().performClick()
        compose.onNodeWithTag("quick-size-12").performClick()
        val run = saved().active
        val puzzle = SoloProgressRules.puzzle(run)
        assertEquals(PuzzleBoardSize.BOOK_SHORT, run.boardSize)
        assertTrue(run.frozen != null)
        assertFound(puzzle, 0)
        val first = puzzle.targets.first()
        trace(puzzle, first)
        assertFound(puzzle, 1)
        compose.onNodeWithText("Home").performClick()

        compose.onNodeWithTag("ws16-resume-solo").performScrollTo().performClick()
        compose.activityRule.scenario.recreate()
        assertFound(puzzle, 1)
        assertEquals(run.puzzleId, saved().active.puzzleId)
        assertEquals(setOf(first.id), saved().active.foundIds)
        puzzle.targets.drop(1).forEach { trace(puzzle, it) }
        compose.onNodeWithText("Puzzle complete!").assertIsDisplayed()
        capture("v117-quick-play-result")

        compose.onNodeWithText("Change size").performScrollTo().performClick()
        compose.onNodeWithTag("quick-size-20").assertExists()
        compose.onNodeWithText("Back").performScrollTo().performClick()
        compose.onNodeWithText("Play another puzzle").performScrollTo().performClick()
        val next = saved().active
        assertEquals(run.boardSize, next.boardSize)
        assertNotEquals(run.puzzleId, next.puzzleId)
        assertFound(SoloProgressRules.puzzle(next), 0)
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithContentDescription("Continue saved solo word search")
            .performScrollTo().assertIsDisplayed()
        assertEquals(run.puzzleId, saved().results.single().puzzleId)
    }

    private fun openDaily() {
        compose.onNodeWithContentDescription("Open Daily Puzzle").performScrollTo().performClick()
        compose.onNodeWithText("Daily Word Search").assertIsDisplayed()
    }

    private fun todayAction(label: String) = compose.onNode(
        hasAnyAncestor(hasTestTag("ws16ca-daily-today")) and hasClickAction() and hasText(label)
    )

    private fun openEarlierDaily(): WordSearchPuzzle {
        openDaily()
        compose.onNodeWithText(savedDate).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Resume").performScrollTo().performClick()
        val puzzle = SoloProgressRules.puzzle(DailyChallenge.dailyRun(savedDate))
        assertFound(puzzle, 1)
        return puzzle
    }

    private fun dailyBackToHome() {
        compose.onNodeWithText("Daily").performClick()
        compose.onNodeWithText("Back to Play").performScrollTo().performClick()
        compose.onNodeWithTag("ws16-home-header").assertIsDisplayed()
    }

    private fun assertFound(puzzle: WordSearchPuzzle, count: Int) {
        compose.onNodeWithText("$count / ${puzzle.targets.size} found").assertIsDisplayed()
    }

    private fun trace(puzzle: WordSearchPuzzle, target: WordTarget) {
        val first = target.positions.first()
        val last = target.positions.last()
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val cellX = width.toFloat() / puzzle.size
            val cellY = height.toFloat() / puzzle.size
            swipe(
                Offset((first.col + .5f) * cellX, (first.row + .5f) * cellY),
                Offset((last.col + .5f) * cellX, (last.row + .5f) * cellY),
                durationMillis = 400L
            )
        }
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput("ws74-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
