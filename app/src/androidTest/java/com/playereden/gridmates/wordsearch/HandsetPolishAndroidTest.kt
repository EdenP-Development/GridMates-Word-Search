package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.WordTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class HandsetPolishAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun homeKeepsTitleAndActionsInPlaceWhenModesScroll() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = "ws42-home-${System.nanoTime()}.json"
        val store = SoloStore(context, file)
        val rooms = NearbyRoomViewModel()
        try {
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(580.dp)) {
                        GridMatesShell(store, rooms, QaBookshelfRuntime(context))
                    }
                }
            }
            val menu = compose.onNodeWithContentDescription("Open menu")
            val initial = menu.getUnclippedBoundsInRoot()
            compose.onNodeWithText("My Puzzles").performScrollTo().assertIsDisplayed()
            val range = compose.onNodeWithTag("home-mode-scroll").fetchSemanticsNode()
                .config[SemanticsProperties.VerticalScrollAxisRange]
            assertTrue("Expected the Home modes to scroll", range.value() > 0f)
            assertEquals(initial, menu.getUnclippedBoundsInRoot())
            compose.onNodeWithText("Word Search").assertIsDisplayed()
            compose.onNodeWithContentDescription("Open player profile").assertIsDisplayed()
            menu.performClick()
            compose.onNodeWithText("Settings & backup").assertIsDisplayed()
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new")) File(context.filesDir, file + suffix).delete()
        }
    }

    @Test fun chaptersJumpToLaterPagesAndBackAt200PercentText() {
        val catalog = BookQaPilot.catalog()
        val book = catalog.books.first { it.theme == WordTheme.GARDEN }
        val first = book.chapters.first()
        val last = book.chapters.last()
        var played: String? = null
        compose.setContent {
            GridMatesTheme {
                val density = LocalDensity.current.density
                CompositionLocalProvider(LocalDensity provides Density(density, 2f)) {
                    Box(Modifier.width(320.dp).height(640.dp)) {
                        PuzzleBookDetailScreen(
                            catalog, BookLibraryProgress(), book.id, onBack = {},
                            onPlayPage = { _, page -> played = page }, showPrototypeArt = true
                        )
                    }
                }
            }
        }
        val chapters = compose.onNodeWithTag("book-chapters")
        chapters.performClick()
        compose.onNodeWithTag("book-chapter-jump-" + last.id).performScrollTo().performClick()
        compose.onNodeWithText(last.title).assertIsDisplayed()
        compose.onNodeWithText("Play page 19").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(last.pages.first().id, played) }

        chapters.assertIsDisplayed().performClick()
        compose.onNodeWithTag("book-chapter-jump-" + first.id).performClick()
        compose.onNodeWithText(first.title).assertIsDisplayed()
        compose.onNodeWithContentDescription("Back to Your Library").assertIsDisplayed()
    }
}
