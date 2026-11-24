package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
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
import com.playereden.gridmates.wordsearch.core.SoloProgressRules
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle
import com.playereden.gridmates.wordsearch.core.WordTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val woodland = "woodland-qa-volume-one"
private const val garden = "garden-qa-volume-one"
private const val woodlandAlbum = "book-album-link-$woodland"
private const val pendingPage = "woodland-qa-page-two"
private const val lastBirdsongPage = "ws57-woodland-qa-ch5-page3"

/** Runs in the signed v119 target before v120 is installed over it. */
class Ws77V119WoodlandSeedAndroidTest {
    @Test fun saveCompletedPagesAcrossAllFourWoodlandEditions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val runtime = QaBookshelfRuntime.installed(context)
        val catalog = runtime.catalog
        val journal = requireNotNull(runtime.progressStore)
        fun complete(bookId: String, pageId: String) {
            val puzzle = BookPageSessionRules.puzzle(catalog, bookId, pageId)
            assertTrue(journal.recordCompletion(bookId, pageId, puzzle,
                puzzle.targets.mapTo(mutableSetOf()) { it.id }))
        }

        complete(woodland, "woodland-qa-page-one")
        for (index in 1..3) complete(woodland, "woodland-qa-v5-ch5-page$index")
        for (index in 1..2) complete(Ws57WoodlandPreview.BOOK_ID, "ws57-woodland-qa-ch5-page$index")
        for (preview in listOf(Ws66ChapterPreview.CLEARINGS, Ws73VarietyPreview.CLEARINGS)) {
            complete(preview, catalog.pages(preview).first().id)
        }
        complete(garden, "garden-qa-page-one")
        val sessions = requireNotNull(runtime.sessionStore)
        val firstWord = BookPageSessionRules.puzzle(catalog, woodland, pendingPage).targets.first()
        assertTrue(sessions.open(woodland, pendingPage))
        assertEquals(ClaimStatus.FOUND, sessions.claim(
            woodland, pendingPage, firstWord.positions.first(), firstWord.positions.last()).status)

        val solo = SoloStore(context)
        assertTrue(runtime.syncBookRewards(solo))
        assertEquals(5, solo.profile.bookAwards.size)
        val today = DailyChallenge.today()
        solo.openDaily(today)
        SoloProgressRules.puzzle(requireNotNull(solo.selectedDaily).run).targets.forEach { word ->
            assertEquals(ClaimStatus.FOUND,
                solo.dailyClaim(word.positions.first(), word.positions.last()).status)
        }
        assertEquals(1, solo.profile.dailyCompletions)
    }
}

class Ws77WoodlandAlbumAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun oldSolvesAndLiveChapterUnlocksShareOneWoodlandAlbum() {
        openAlbums()
        compose.onNodeWithTag(woodlandAlbum).assertIsDisplayed().performClick()
        assertAlbum(1, "Trailhead")
        compose.onNodeWithText("5 of 6 pages").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("8 of 30 pages").performScrollTo().assertIsDisplayed()
        capture("from-previous-saves")
        compose.onNodeWithContentDescription("Back to albums").performClick()
        compose.onNodeWithContentDescription("Back to Home").performClick()

        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithTag("ws16bh-spine-$woodland").assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").performClick()
        val details = compose.onNodeWithTag("book-detail-list")
        details.performScrollToNode(hasTestTag(woodlandAlbum))
        compose.onNodeWithTag(woodlandAlbum).performClick()
        assertAlbum(1, "Trailhead")
        compose.onNodeWithContentDescription("Back to book").performClick()
        details.performScrollToNode(hasTestTag("book-continue"))
        compose.onNodeWithText("Resume page 2").assertIsDisplayed().performClick()
        compose.onNodeWithText("1 of 16 words found").assertIsDisplayed()
        val runtime = QaBookshelfRuntime.installed(context)
        val pageTwo = BookPageSessionRules.puzzle(runtime.catalog, woodland, pendingPage)
        pageTwo.targets.drop(1).forEach { trace(pageTwo, it) }
        compose.onNodeWithText("Stamp earned · Trailhead").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("20×20 · Classic · 24 words").performScrollTo().assertIsDisplayed()
        capture("trailhead")
        compose.onNodeWithContentDescription("Back to book").performClick()

        val pages = runtime.shelf.catalog.pages(woodland)
        assertEquals(45, pages.size)
        val birdOrdinal = pages.indexOfFirst { it.id == lastBirdsongPage } + 1
        assertTrue(birdOrdinal > 0)
        val playBirdsong = hasContentDescription("Play page $birdOrdinal")
        details.performScrollToNode(playBirdsong)
        compose.onNode(playBirdsong).assertIsDisplayed().performClick()
        val birds = BookPageSessionRules.puzzle(runtime.catalog, Ws57WoodlandPreview.BOOK_ID,
            lastBirdsongPage)
        birds.targets.forEach { trace(birds, it) }
        compose.onNodeWithText("Stamp earned · Dawn Chorus").performScrollTo().assertIsDisplayed()
        capture("dawn-chorus")
        compose.onNodeWithTag(woodlandAlbum).performScrollTo().performClick()
        assertAlbum(3, "Deep in the Woods")
        compose.activityRule.scenario.recreate()
        assertAlbum(3, "Deep in the Woods")
        compose.onNodeWithText("10 of 45 pages").performScrollTo().assertIsDisplayed()
        capture("woodland-album")
        val profile = SoloStore(context).profile
        assertEquals(6, profile.bookAwards.size)
        assertFalse(profile.bookAwards.any { it.bookId in BookQaPilot.editorialBookIds })
        assertEquals(1, profile.dailyCompletions)
    }

    @Test fun allThreeAlbumsOpenOnACompactPhoneWithLargeText() {
        val config = compose.activity.resources.configuration
        assertEquals(320, config.screenWidthDp)
        assertEquals(1.5f, config.fontScale, .01f)
        openAlbums()
        compose.onNodeWithTag(woodlandAlbum).assertIsDisplayed()
        val gardenLink = compose.onNodeWithTag("book-album-link-$garden")
        gardenLink.assertIsDisplayed()
        val activityLink = compose.onNodeWithTag("activity-album-link")
        activityLink.performScrollTo().assertIsDisplayed()
        capture("album-list-large-text")
        activityLink.performClick()
        compose.onNodeWithText("1 of 4 stamps earned").assertIsDisplayed()
        compose.onNodeWithContentDescription("Back to albums").performClick()
        gardenLink.performClick()
        compose.onNodeWithText("Garden album").assertIsDisplayed()
        compose.onNodeWithText("1 of 4 stamps earned").assertIsDisplayed()
        compose.onNodeWithContentDescription("Back to albums").performClick()
        compose.onNodeWithTag(woodlandAlbum).performClick()
        assertAlbum(3, "Deep in the Woods")
        compose.onNodeWithTag("book-stamp-woodland-wanderer").performScrollTo().assertIsDisplayed()
        capture("last-stamp-large-text")
    }

    private fun openAlbums() {
        compose.onNodeWithTag("ws16d-home-collection").performScrollTo().performClick()
        compose.onNodeWithTag("ws25-collection-tab-albums").assertIsDisplayed().performClick()
    }

    private fun assertAlbum(earned: Int, next: String) {
        compose.onNodeWithText("Woodland album").assertIsDisplayed()
        compose.onNodeWithText("$earned of 5 stamps earned").assertIsDisplayed()
        compose.onNodeWithText("Next stamp · $next").assertIsDisplayed()
    }

    private fun trace(board: WordSearchPuzzle, word: WordTarget) {
        val first = word.positions.first()
        val last = word.positions.last()
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val cellW = width.toFloat() / board.size
            val cellH = height.toFloat() / board.size
            swipe(Offset((first.col + .5f) * cellW, (first.row + .5f) * cellH),
                Offset((last.col + .5f) * cellW, (last.row + .5f) * cellH),
                durationMillis = 400L)
        }
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput("ws77-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
