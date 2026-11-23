package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.ClaimStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val SPACE = "space-qa-volume-one"
private const val SOLAR = "ws66-v110-solar-system"

/** Run on the disposable emulator while the pinned v110 APK is installed. */
class Ws67V110SeedAndroidTest {
    @Test fun saveBookmarksAndAFoundWordInTheSeparateSpaceVolumes() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val runtime = QaBookshelfRuntime.installed(context)
        val pages = runtime.catalog.pages(SOLAR)
        val page = pages.first()
        val progress = requireNotNull(runtime.progressStore)
        val sessions = requireNotNull(runtime.sessionStore)
        assertTrue(progress.bookmark(SPACE, runtime.catalog.pages(SPACE).first().id))
        assertTrue(progress.bookmark(SOLAR, page.id))
        assertTrue(sessions.open(SOLAR, page.id))
        val word = BookPageSessionRules.puzzle(runtime.catalog, SOLAR, page.id).targets.first()
        assertEquals(ClaimStatus.FOUND, sessions.claim(SOLAR, page.id,
            word.positions.first(), word.positions.last()).status)
    }
}

class Ws67HomeLibraryAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun onlyTheGreenPillOpensTheSoloPuzzle() {
        val card = compose.onNodeWithTag("ws16-continue-solo")
            .performScrollTo().assertHasNoClickAction()
        card.performTouchInput { click(Offset(width * .22f, height * .5f)) }
        compose.onNodeWithTag("ws16-home-header").assertExists()
        screenshot("ws67-home.png")
        compose.onNodeWithTag("ws16-resume-solo").assertHasClickAction()
            .performTouchInput { click(center) }
        compose.onNodeWithTag("word-grid-touch-area").assertExists()
    }

    @Test fun consolidatedSpaceBookResumesTheOriginalSolarSave() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val runtime = QaBookshelfRuntime.installed(context)
        val shelf = runtime.shelf
        val page = runtime.catalog.pages(SOLAR).first()
        assertEquals(listOf(42, 24, 21), shelf.catalog.books.map { shelf.catalog.pages(it.id).size })
        assertEquals(87, shelf.catalog.books.sumOf { shelf.catalog.pages(it.id).size })
        runtime.catalog.books.forEach { stored ->
            val display = requireNotNull(shelf.displayBookId(stored.id))
            runtime.catalog.pages(stored.id).forEach { original ->
                assertEquals(stored.id, shelf.ownerOf(original.id))
                assertEquals(original, shelf.catalog.page(display, original.id))
            }
        }
        val raw = requireNotNull(runtime.progressStore).progress
        assertEquals(2, shelf.bookmarks(raw, SPACE).size)
        assertEquals(6, shelf.catalog.book(SPACE)?.chapters?.single { it.title == "Our Solar System" }?.pages?.size)
        val visibleNumber = shelf.catalog.pages(SPACE).indexOf(page) + 1

        compose.onNodeWithText("1 of 3").assertExists()
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        screenshot("ws67-library.png")
        compose.onNodeWithTag("ws16bh-spine-$SOLAR").assertDoesNotExist()
        compose.onNodeWithTag("ws16bh-spine-$SPACE").assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
        compose.onNodeWithText("Resume page $visibleNumber").assertExists()
        compose.onNodeWithTag("book-continue").performScrollTo().performClick()
        compose.onNodeWithText("Space").assertIsDisplayed()
        compose.onNodeWithText("1 of 12 words found", substring = true).assertExists()
        screenshot("ws67-resumed-solar.png")
    }

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val bitmap = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
        try {
            instrumentation.targetContext.openFileOutput(name, 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
