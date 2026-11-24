package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val garden = "garden-qa-volume-one"

/** The emulator runs this first in the signed v117 app to create a real pre-upgrade save. */
class Ws75V117AlbumSeedAndroidTest {
    @Test fun saveOneGardenPageAndOneWoodlandPage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val runtime = QaBookshelfRuntime.installed(context)
        val progress = requireNotNull(runtime.progressStore)
        for (book in listOf(garden, "woodland-qa-volume-one")) {
            val page = runtime.catalog.pages(book).first()
            val board = BookPageSessionRules.puzzle(runtime.catalog, book, page.id)
            assertTrue(progress.recordCompletion(book, page.id, board,
                board.targets.mapTo(mutableSetOf()) { it.id }))
        }
        assertTrue(runtime.syncBookRewards(SoloStore(context)))
    }
}

class Ws75GardenAlbumAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun earlierCreditLiveUnlockAndBothRoutesSurviveUpgrade() {
        openCollectionAlbum()
        assertAlbum(1, "New Shoots")
        capture("upgraded-collection")
        compose.onNodeWithContentDescription("Back to Home").performClick()

        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithTag("ws16bh-spine-$garden").assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
        val albumLink = hasTestTag("book-album-link")
        val details = compose.onNodeWithTag("book-detail-list")
        details.performScrollToNode(albumLink)
        compose.onNode(albumLink).assertIsDisplayed().performClick()
        assertAlbum(1, "New Shoots")
        compose.onNodeWithContentDescription("Back to book").performClick()

        val play = hasTestTag("book-continue")
        details.performScrollToNode(play)
        compose.onNode(play).assertIsDisplayed().performClick()
        compose.onNodeWithText("Page 2").assertIsDisplayed()
        val runtime = QaBookshelfRuntime.installed(context)
        val page = runtime.catalog.pages(garden)[1]
        val board = BookPageSessionRules.puzzle(runtime.catalog, garden, page.id)
        for (target in board.targets) {
            val first = target.positions.first()
            val last = target.positions.last()
            compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
                val cellX = width.toFloat() / board.size
                val cellY = height.toFloat() / board.size
                swipe(
                    Offset((first.col + .5f) * cellX, (first.row + .5f) * cellY),
                    Offset((last.col + .5f) * cellX, (last.row + .5f) * cellY),
                    durationMillis = 360L
                )
            }
        }
        compose.onNodeWithTag("book-page-result").assertIsDisplayed()
        compose.onNodeWithText("Stamp earned · New Shoots").performScrollTo().assertIsDisplayed()
        capture("stamp-earned")
        compose.onNode(albumLink).performScrollTo().performClick()
        assertAlbum(2, "Garden Visitors")
        capture("library-album")
        compose.activityRule.scenario.recreate()
        assertAlbum(2, "Garden Visitors")
        compose.onNodeWithContentDescription("Back to book").performClick()
        compose.onNodeWithTag("book-page-result").assertIsDisplayed()
    }

    @Test fun albumsFitAtLargerText() {
        openCollectionAlbum()
        assertAlbum(2, "Garden Visitors")
        compose.onNodeWithTag("book-stamp-garden-full-bloom").performScrollTo().assertIsDisplayed()
        capture("large-text-album")
    }

    private fun openCollectionAlbum() {
        compose.onNodeWithTag("ws16d-home-collection").performScrollTo().performClick()
        compose.onNodeWithTag("ws25-collection-tab-albums").performScrollTo().performClick()
    }

    private fun assertAlbum(earned: Int, next: String) {
        compose.onNodeWithText("Garden album").assertIsDisplayed()
        compose.onNodeWithText("$earned of 4 stamps earned").assertIsDisplayed()
        compose.onNodeWithText("Next stamp · $next").assertIsDisplayed()
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput("ws75-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
