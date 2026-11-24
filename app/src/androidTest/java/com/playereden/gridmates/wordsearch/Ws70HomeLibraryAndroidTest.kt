package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

class Ws70HomeLibraryAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun checkWidth(): Int {
        val arguments = InstrumentationRegistry.getArguments()
        val expectedWidth = requireNotNull(arguments.getString("expectedWidthDp")).toInt()
        val actualWidth = compose.activity.resources.configuration.screenWidthDp
        assertTrue("Expected ${expectedWidth}dp, received ${actualWidth}dp",
            abs(expectedWidth - actualWidth) <= 1)
        return expectedWidth
    }

    @Test fun homeGesturesFollowSeparateAxes() {
        val expectedWidth = checkWidth()
        val title = compose.onNodeWithTag("ws41-home-title").assertIsDisplayed()
        val heading = compose.onNodeWithText("Your puzzle books").assertIsDisplayed()
        val menu = compose.onNodeWithTag("ws16-menu").assertIsDisplayed()
        val profile = compose.onNodeWithTag("ws16-profile").assertIsDisplayed()
        val titleTop = title.getUnclippedBoundsInRoot().top.value
        val headingTop = heading.getUnclippedBoundsInRoot().top.value
        val menuBounds = menu.getUnclippedBoundsInRoot()
        val profileBounds = profile.getUnclippedBoundsInRoot()
        capture("ws70-$expectedWidth-home.png")

        compose.onNodeWithTag("ws14d-cover-pager").performTouchInput {
            swipeLeft(startX = width * .76f, endX = width * .23f)
        }
        compose.onNodeWithTag("ws16-selected-book").assertTextContains("Space", substring = true)
        compose.onNodeWithText("2 of 3").assertIsDisplayed()
        assertEquals(titleTop, title.getUnclippedBoundsInRoot().top.value, 1f)

        compose.onNodeWithTag("home-mode-scroll").performTouchInput {
            swipeUp(startY = height * .77f, endY = height * .28f)
        }
        val titleTravel = titleTop - title.getUnclippedBoundsInRoot().top.value
        val headingTravel = headingTop - heading.getUnclippedBoundsInRoot().top.value
        assertTrue("Home title should move by more than a pixel", titleTravel > 1f)
        assertEquals(titleTravel, headingTravel, 1f)
        assertEquals(menuBounds, menu.getUnclippedBoundsInRoot())
        assertEquals(profileBounds, profile.getUnclippedBoundsInRoot())
        compose.onNodeWithText("My Puzzles").performScrollTo().assertIsDisplayed()
        capture("ws70-$expectedWidth-home-scrolled.png")
        menu.performClick()
        compose.onNodeWithText("Settings & backup").assertIsDisplayed()
        compose.onNodeWithText("Close").performClick()
    }

    @Test fun libraryShelvesFillInOrderAndReopenSavedPuzzle() {
        val expectedWidth = checkWidth()
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        val spines = listOf("woodland", "space", "garden").map { theme ->
            compose.onNode(
                hasTestTag("ws16bh-spine-$theme-qa-volume-one") and
                    hasAnyAncestor(hasTestTag("ws16at-movable-shelf-0"))
            ).assertIsDisplayed()
        }
        val leftEdges = spines.map { it.getUnclippedBoundsInRoot().left }
        assertTrue(leftEdges.zipWithNext().all { (a, b) -> a < b })
        val slots = if (expectedWidth == 393) 4 else 3
        for (position in 0 until slots) {
            compose.onNodeWithTag("ws35-binding-blank-1-$position")
                .assertIsDisplayed().assertHasNoClickAction()
        }
        if (slots == 4) compose.onNodeWithTag("ws35-binding-blank-0-3")
            .assertIsDisplayed().assertHasNoClickAction()
        capture("ws70-$expectedWidth-library.png")

        spines[1].performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
        compose.onNodeWithTag("book-continue").performScrollTo().performClick()
        compose.onNodeWithText("1 of 12 words found", substring = true).assertIsDisplayed()
        compose.onNodeWithTag("word-grid-touch-area").assertIsDisplayed()
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
