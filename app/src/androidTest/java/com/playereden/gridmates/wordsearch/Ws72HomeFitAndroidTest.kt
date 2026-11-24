package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs

class Ws72HomeFitAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val scroll get() = compose.onNodeWithTag("home-mode-scroll")
    private val daily get() = compose.onNodeWithTag("ws16-action-daily")
    private val together get() = compose.onNodeWithTag("ws16-action-together")

    @Test fun captureV114Home() {
        checkDisplay(360, 1f)
        compose.onNodeWithTag("ws41-home-title").assertIsDisplayed()
        recordLayout("v114-home")
        assertTrue(daily.getUnclippedBoundsInRoot().height >= 67.dp)
    }

    @Test fun tallPortraitHomeFitsWithoutVerticalScrolling() {
        checkDisplay(360, 1f)
        recordLayout("v115-home")
        assertEquals("Home still scrolls on the matching portrait layout", 0f, scrollRange(), 0f)
        val viewport = scroll.getUnclippedBoundsInRoot()
        for (tag in listOf("ws41-home-title", "ws16-full-shelf", "ws16d-home-collection",
            "ws16-resume-solo", "ws16-action-daily", "ws16-action-together", "ws39-quick-play",
            "ws16-new-solo", "ws16-my-puzzles")) {
            val bounds = compose.onNodeWithTag(tag).assertIsDisplayed().getUnclippedBoundsInRoot()
            assertTrue("$tag extends outside Home", bounds.top >= viewport.top &&
                bounds.bottom <= viewport.bottom)
        }
        val dailyBounds = daily.assertHasClickAction().getUnclippedBoundsInRoot()
        val togetherBounds = together.assertHasClickAction().getUnclippedBoundsInRoot()
        assertTrue(dailyBounds.height in 48.dp..55.dp)
        assertTrue(togetherBounds.height in 48.dp..55.dp)
        assertEquals(dailyBounds.top.value, togetherBounds.top.value, .5f)
        compose.onNodeWithTag("ws14d-cover-pager").performTouchInput {
            swipeLeft(startX = width * .76f, endX = width * .23f)
        }
        compose.onNodeWithText("2 of 3").assertIsDisplayed()
        assertEquals(0f, scrollRange(), 0f)
    }

    @Test fun shortPhoneAtLargeTextKeepsAllActionsReachable() {
        checkDisplay(320, 2f)
        assertTrue("The short, large-text layout needs a scrollable Home", scrollRange() > 0f)
        daily.performScrollTo().assertHasClickAction().assertIsDisplayed()
        together.performScrollTo().assertHasClickAction().assertIsDisplayed()
        val dailyBounds = daily.getUnclippedBoundsInRoot()
        val togetherBounds = together.getUnclippedBoundsInRoot()
        assertTrue(togetherBounds.top >= dailyBounds.bottom)
        assertTrue(dailyBounds.height >= 48.dp && togetherBounds.height >= 48.dp)
        compose.onNodeWithTag("ws16-new-solo").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("ws16-my-puzzles").performScrollTo().assertIsDisplayed()
        recordLayout("v115-home-large-text")
    }

    private fun scrollRange() = scroll.fetchSemanticsNode()
        .config[SemanticsProperties.VerticalScrollAxisRange].maxValue()

    private fun checkDisplay(widthDp: Int, scale: Float) {
        val config = compose.activity.resources.configuration
        assertTrue("Expected ${widthDp}dp, received ${config.screenWidthDp}dp",
            abs(widthDp - config.screenWidthDp) <= 1)
        assertTrue("Expected font scale $scale, received ${config.fontScale}",
            abs(scale - config.fontScale) < .01f)
    }

    private fun recordLayout(name: String) {
        val bounds = scroll.getUnclippedBoundsInRoot()
        context.openFileOutput("ws72-$name.txt", 0).bufferedWriter().use {
            it.write("verticalScrollMaxPx=${scrollRange()}\n")
            it.write("viewportHeightDp=${bounds.height.value}\n")
            it.write("dailyHeightDp=${daily.getUnclippedBoundsInRoot().height.value}\n")
            it.write("togetherHeightDp=${together.getUnclippedBoundsInRoot().height.value}\n")
        }
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput("ws72-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
