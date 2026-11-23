package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Source-compiled by CI; a physical tap/gesture test is still required. */
class Ws14K2GameChromeAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun toolsAndNoticeHistoryAreActualAccessibleActions() {
        var tools = 0
        var history = 0
        compose.setContent {
            GridMatesTheme {
                Row {
                    BookPageToolsButton(onClick = { tools++ })
                    BookPageNoticeHistoryButton(hasNotices = true,
                        onClick = { history++ })
                }
            }
        }
        compose.onNodeWithContentDescription("Puzzle tools").performClick()
        compose.onNodeWithContentDescription("Notice history").performClick()
        compose.runOnIdle {
            assertEquals(1, tools)
            assertEquals(1, history)
        }
    }
    @Test fun longNoticeHistoryScrollsWithCloseVisibleAtLargeText() {
        var closed = false
        val notices = List(30) { "Hint ${it + 1}: Look for Watering Can near row 3." }
        compose.setContent {
            GridMatesTheme {
                val density = LocalDensity.current.density
                CompositionLocalProvider(LocalDensity provides Density(density, 2f)) {
                    BookPageNoticeHistoryDialog(notices, onDismiss = { closed = true })
                }
            }
        }
        compose.onNodeWithText("Game notices").assertIsDisplayed()
        compose.onNodeWithText(notices.last()).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Close").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(true, closed) }
    }
}
