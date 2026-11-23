package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

class Ws69LibraryPolishAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun captureV111Library() {
        openLibrary()
        capture("ws69-v111-library.png")
    }

    @Test fun shelfBracketsClearTheBaseAndSavedPuzzleFitsThePhone() {
        val configuration = compose.activity.resources.configuration
        val expectedWidth = InstrumentationRegistry.getArguments().getString("expectedWidthDp")?.toInt()
        val actualWidth = configuration.screenWidthDp
        if (expectedWidth != null) {
            assertTrue("Expected ${expectedWidth}dp, received ${actualWidth}dp",
                abs(expectedWidth - actualWidth) <= 1)
        }
        val capturePrefix = "ws69-${expectedWidth ?: actualWidth}"
        Log.i("WS69_LAYOUT", "Activity width ${configuration.screenWidthDp}dp")

        compose.onNodeWithTag("ws41-home-title").assertIsDisplayed()
        capture("$capturePrefix-home.png")
        compose.onNodeWithTag("ws16-resume-solo").performScrollTo().assertIsDisplayed()
        openLibrary()
        capture("$capturePrefix-library.png")

        val base = compose.onNodeWithTag("ws16at-cabinet-base", useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val shelves = compose.onAllNodesWithTag("ws16bi-shelf-raster", useUnmergedTree = true)
            .assertCountEquals(2)
        for (index in 0..1) {
            val shelf = shelves[index].getUnclippedBoundsInRoot()
            val gap = base.top - shelf.bottom
            assertTrue("Shelf $index clears the cabinet base by only $gap", gap >= 4.dp)
            Log.i("WS69_LAYOUT", "Shelf $index clearance $gap")
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val catalog = QaBookshelfRuntime.installed(context).catalog
        val solarId = "ws66-v110-solar-system"
        val savedPage = catalog.pages(solarId).first()
        val board = BookPageSessionRules.puzzle(catalog, solarId, savedPage.id)
        compose.onNodeWithTag("ws16bh-spine-space-qa-volume-one")
            .assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
        compose.onNodeWithTag("book-continue").performScrollTo().performClick()
        compose.onNodeWithText("1 of 12 words found", substring = true).assertIsDisplayed()
        compose.onNodeWithTag("word-grid-touch-area").assertIsDisplayed()
        compose.onNodeWithTag("ws14h-hint-lightbulb").assertIsDisplayed()
        compose.onNodeWithTag("word-list-scrollable").assertIsDisplayed()
        capture("$capturePrefix-puzzle.png")
        compose.onNodeWithTag("word-list-target-${board.targets.last().id}")
            .performScrollTo().assertIsDisplayed()
    }

    private fun openLibrary() {
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithTag("ws16at-library-title").assertIsDisplayed()
        compose.onNodeWithTag("ws16bh-spine-space-qa-volume-one").assertIsDisplayed()
    }

    private fun capture(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput(name, 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
