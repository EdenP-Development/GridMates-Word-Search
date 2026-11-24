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
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import com.playereden.gridmates.wordsearch.core.DailyChallenge
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordTarget
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val gardenBookId = "garden-qa-volume-one"

/** Run in the installed v118 package before the signed v119 upgrade. */
class Ws76V118ActivitySeedAndroidTest {
    @Test fun saveEarlierDailyDatesQuickPlaySizesAndGardenProgress() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val store = SoloStore(context)
        val today = LocalDate.parse(DailyChallenge.today())
        for (day in 1L..6L) {
            store.openDaily(today.minusDays(day * 2).toString())
            val puzzle = SoloProgressRules.puzzle(requireNotNull(store.selectedDaily).run)
            puzzle.targets.forEach { target ->
                assertEquals(ClaimStatus.FOUND,
                    store.dailyClaim(target.positions.first(), target.positions.last()).status)
            }
        }
        assertEquals(6, store.profile.dailyCompletions)
        for (size in listOf(PuzzleBoardSize.BOOK_SHORT, PuzzleBoardSize.BOOK_BIG)) {
            assertTrue(store.startQuickPlay(size))
            SoloProgressRules.puzzle(store.profile.active).targets.forEach { target ->
                assertEquals(ClaimStatus.FOUND,
                    store.claim(target.positions.first(), target.positions.last()).status)
            }
        }
        assertEquals(2, store.profile.results.size)

        val library = QaBookshelfRuntime.installed(context)
        val progress = requireNotNull(library.progressStore)
        val page = library.catalog.pages(gardenBookId).first()
        val board = BookPageSessionRules.puzzle(library.catalog, gardenBookId, page.id)
        assertTrue(progress.recordCompletion(gardenBookId, page.id, board,
            board.targets.mapTo(mutableSetOf()) { it.id }))
        assertTrue(library.syncBookRewards(store))
        assertEquals(1, store.profile.bookCompletions)
        assertEquals(null, store.warning)
    }
}

class Ws76ActivityAlbumAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun earnedStampsCarryForwardAndFollowRealDailyAndQuickPlaySolves() {
        openAlbums()
        compose.onNodeWithTag("book-album-link").assertIsDisplayed().performClick()
        compose.onNodeWithText("Garden album").assertIsDisplayed()
        compose.onNodeWithText("1 of 4 stamps earned").assertIsDisplayed()
        pressBack()
        compose.onNodeWithTag("activity-album-link").assertIsDisplayed().performClick()
        assertAlbum(2, "Next stamp · All Sizes")
        compose.onNodeWithText("Still to solve · 15×15").performScrollTo().assertIsDisplayed()
        capture("upgraded-activity")
        compose.onNodeWithText("6 of 7").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Back to albums").performClick()
        compose.onNodeWithContentDescription("Back to Home").performClick()

        compose.onNodeWithContentDescription("Open Daily Puzzle").performScrollTo().performClick()
        compose.onNode(hasAnyAncestor(hasTestTag("ws16ca-daily-today")) and
            hasClickAction() and hasText("Play today")).performScrollTo().performClick()
        val today = DailyChallenge.today()
        val daily = SoloProgressRules.puzzle(DailyChallenge.dailyRun(today))
        daily.targets.forEach { trace(daily, it) }
        compose.onNodeWithText("Daily complete!").assertIsDisplayed()
        compose.onNodeWithText("Stamp earned · Seven Good Days").performScrollTo().assertIsDisplayed()
        capture("daily-stamp-earned")
        compose.onNodeWithTag("activity-album-link").performScrollTo().performClick()
        assertAlbum(3, "Next stamp · All Sizes")
        compose.activityRule.scenario.recreate()
        assertAlbum(3, "Next stamp · All Sizes")
        compose.onNodeWithContentDescription("Back to results").performClick()
        compose.onNodeWithText("Daily complete!").assertIsDisplayed()
        compose.onNodeWithText("Stamp earned · Seven Good Days").assertDoesNotExist()
        val profile = SoloStore(context).profile
        assertEquals(7, profile.dailyCompletions)
        assertTrue(profile.dailyEntries.single { it.date == today }.completed)
        compose.onNodeWithText("Daily").performClick()
        compose.onNodeWithText("Back to Play").performScrollTo().performClick()

        compose.onNodeWithTag("ws39-quick-play").performScrollTo().performClick()
        compose.onNodeWithTag("quick-size-15").performClick()
        val medium = SoloStore(context).profile.active
        assertEquals(PuzzleBoardSize.BOOK_CLASSIC, medium.boardSize)
        val quick = SoloProgressRules.puzzle(medium)
        quick.targets.forEach { trace(quick, it) }
        compose.onNodeWithText("Puzzle complete!").assertIsDisplayed()
        compose.onNodeWithText("Stamp earned · All Sizes").performScrollTo().assertIsDisplayed()
        capture("quick-stamp-earned")
        compose.onNodeWithTag("activity-album-link").performScrollTo().performClick()
        assertAlbum(4, "Album complete")
        capture("activity-complete")
        pressBack()
        compose.onNodeWithText("Puzzle complete!").assertIsDisplayed()
        assertEquals(3, SoloStore(context).profile.results.size)
    }

    @Test fun gardenShowsAndPlaysTheStepToTwentyByTwenty() {
        val catalog = QaBookshelfRuntime.installed(context).catalog
        val pages = catalog.pages(gardenBookId).take(3)
        val boards = pages.map { BookPageSessionRules.puzzle(catalog, gardenBookId, it.id) }
        saveGardenBoardMetrics(boards)

        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithTag("ws16bh-spine-$gardenBookId").assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
        val details = compose.onNodeWithTag("book-detail-list")
        val play = hasTestTag("book-continue")
        details.performScrollToNode(play)
        compose.onNode(play).performClick()
        compose.onNodeWithText("Page 2").assertIsDisplayed()
        val pageTwo = boards[1]
        pageTwo.targets.forEach { trace(pageTwo, it) }
        compose.onNodeWithText("20×20 · Classic · 24 words").performScrollTo().assertIsDisplayed()
        capture("garden-next-page")

        compose.onNodeWithTag("book-result-primary").performScrollTo().performClick()
        compose.onNodeWithText("Page 3").assertIsDisplayed()
        compose.onNodeWithTag("ws14h-hint-lightbulb").assertIsDisplayed().performClick()
        val pageThree = boards[2]
        trace(pageThree, pageThree.targets.first())
        capture("garden-page-three")
        compose.onNodeWithContentDescription("Back to book").performClick()
        details.performScrollToNode(play)
        compose.onNodeWithText("Resume page 3").assertIsDisplayed()
        compose.onNode(play).performClick()
        compose.onNodeWithText("1 of 24 words found").assertIsDisplayed()
        pageThree.targets.drop(1).forEach { trace(pageThree, it) }
        compose.onNodeWithText("Page complete").assertIsDisplayed()
        compose.onNodeWithText("3 of 21 pages completed").assertIsDisplayed()
        compose.onNodeWithText("12×12 · Relaxed · 12 words").performScrollTo().assertIsDisplayed()
        capture("garden-page-three-complete")
    }

    @Test fun bothAlbumsAreReachableAtLargeText() {
        val configuration = compose.activity.resources.configuration
        assertEquals(320, configuration.screenWidthDp)
        assertEquals(1.5f, configuration.fontScale, .01f)
        openAlbums()
        compose.onNodeWithTag("book-album-link").assertIsDisplayed()
        compose.onNodeWithTag("activity-album-link").assertIsDisplayed()
        capture("large-text-album-index")
        compose.onNodeWithTag("activity-album-link").performClick()
        assertAlbum(4, "Album complete")
        compose.onNodeWithTag("activity-stamp-seven_dailies").performScrollTo().assertIsDisplayed()
        capture("large-text-activity")
    }

    private fun openAlbums() {
        compose.onNodeWithTag("ws16d-home-collection").performScrollTo().performClick()
        compose.onNodeWithTag("ws25-collection-tab-albums").assertIsDisplayed().performClick()
    }

    private fun assertAlbum(earned: Int, next: String) {
        compose.onNodeWithTag("activity-album-content").assertIsDisplayed()
        compose.onNodeWithText("$earned of 4 stamps earned").assertIsDisplayed()
        compose.onNodeWithText(next).assertIsDisplayed()
    }

    private fun pressBack() {
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
    }

    private fun trace(board: WordSearchPuzzle, target: WordTarget) {
        val first = target.positions.first()
        val last = target.positions.last()
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val cellWidth = width.toFloat() / board.size
            val cellHeight = height.toFloat() / board.size
            swipe(
                Offset((first.col + .5f) * cellWidth, (first.row + .5f) * cellHeight),
                Offset((last.col + .5f) * cellWidth, (last.row + .5f) * cellHeight),
                durationMillis = 380L
            )
        }
    }

    private fun saveGardenBoardMetrics(boards: List<WordSearchPuzzle>) {
        context.openFileOutput("ws76-garden-boards.tsv", 0).bufferedWriter().use { writer ->
            writer.appendLine("page\tcells\twords\tdiagonal\tbackward")
            boards.forEachIndexed { index, board ->
                val directions = board.targets.map { target ->
                    val start = target.positions.first()
                    val end = target.positions.last()
                    (end.row - start.row) to (end.col - start.col)
                }
                val diagonal = directions.count { (row, col) -> row != 0 && col != 0 }
                val backward = directions.count { (row, col) -> row < 0 || (row == 0 && col < 0) }
                writer.appendLine("${index + 1}\t${board.size * board.size}\t${board.targets.size}\t$diagonal\t$backward")
            }
        }
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput("ws76-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
