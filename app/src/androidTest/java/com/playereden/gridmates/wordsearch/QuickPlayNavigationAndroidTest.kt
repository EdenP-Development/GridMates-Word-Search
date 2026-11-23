package com.playereden.gridmates.wordsearch

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import java.io.File

class QuickPlayNavigationAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun homeQuickPlayChoosesMediumAndReturnsHomeWithResume() {
        compose.onNodeWithTag("ws39-quick-play").performScrollTo()
        snapshot("ws41-quick-play-home.png")
        compose.onNodeWithTag("ws39-quick-play").performClick()
        compose.onNodeWithText("16 words · Recommended").assertExists()
        snapshot("ws41-quick-play-chooser.png")
        compose.onNodeWithTag("quick-size-15").performClick()
        compose.onNodeWithText("15×15 · Classic").assertExists()
        compose.onNodeWithText("0 / 16 found").assertExists()
        snapshot("ws41-quick-play-puzzle.png")
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithContentDescription("Continue saved solo word search")
            .performScrollTo().assertExists()
    }

    private fun snapshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.takeScreenshot().let { bitmap ->
            File(instrumentation.targetContext.filesDir, name).outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            bitmap.recycle()
        }
    }
}
