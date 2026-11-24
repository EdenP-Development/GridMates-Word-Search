package com.playereden.gridmates.wordsearch

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Run after installing v116 over the pinned signed v115 with seeded journals. */
class Ws73VarietyAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val space = "space-qa-volume-one"
    private val woodland = "woodland-qa-volume-one"

    @Test fun oldPageOrderAndInProgressPuzzleSurviveUpgrade() {
        val runtime = QaBookshelfRuntime.installed(context)
        val previous = BookQaPilot.appendChapterPreview(context, BookQaPilot.catalog(context))
        previous.requireCompatibleUpgrade(runtime.catalog)
        val oldShelf = BookShelfPresentation(previous, BookQaPilot.editorialBookIds).catalog
        val newShelf = runtime.shelf.catalog
        assertEquals(oldShelf.books.map { it.id }, newShelf.books.map { it.id })
        assertEquals(listOf(45, 27, 21), newShelf.books.map { newShelf.pages(it.id).size })
        for (book in oldShelf.books) {
            val oldPages = oldShelf.pages(book.id)
            assertEquals(oldPages, newShelf.pages(book.id).take(oldPages.size))
        }

        val oldSolar = Ws66ChapterPreview.SOLAR
        val oldPage = previous.pages(oldSolar).first()
        val progress = requireNotNull(runtime.progressStore).progress
        assertEquals(previous.pages(space).first().id, progress.volumes[space]?.bookmarkedPageId)
        assertEquals(oldPage.id, progress.volumes[oldSolar]?.bookmarkedPageId)
        val oldTarget = BookPageSessionRules.puzzle(previous, oldSolar, oldPage.id).targets.first()
        assertEquals(setOf(oldTarget.id), runtime.sessionStore?.session(oldSolar, oldPage.id)?.foundIds)

        openBook(space)
        val oldOrdinal = newShelf.pages(space).indexOf(oldPage) + 1
        compose.onNodeWithText("Resume page $oldOrdinal").assertIsDisplayed()
        compose.onNodeWithTag("book-continue").performClick()
        compose.onNodeWithText("1 of 12 words found", substring = true).assertIsDisplayed()
        capture("resumed-v115-solar")
    }

    @Test fun solarRelaxedPlaysFromTheSpaceBook() =
        playNewChapter(space, Ws73VarietyPreview.SOLAR, "Our Solar System · Relaxed", 25, 12, 12,
            "solar-relaxed")

    @Test fun quietClearingsExpertPlaysFromTheWoodlandBook() =
        playNewChapter(woodland, Ws73VarietyPreview.CLEARINGS, "Quiet Clearings · Expert", 43, 20, 24,
            "quiet-clearings-expert")

    private fun openBook(bookId: String) {
        compose.onNodeWithTag("ws16-full-shelf").performScrollTo().performClick()
        compose.onNodeWithTag("ws16bh-spine-$bookId").assertIsDisplayed().performClick()
        compose.onNodeWithTag("library-view-pages").assertIsDisplayed().performClick()
    }

    private fun playNewChapter(
        displayedBook: String, owner: String, chapter: String, ordinal: Int,
        size: Int, words: Int, screenshot: String
    ) {
        val runtime = QaBookshelfRuntime.installed(context)
        val page = runtime.catalog.pages(owner).first()
        val board = BookPageSessionRules.puzzle(runtime.catalog, owner, page.id)
        assertEquals(size, board.size)
        assertEquals(words, board.targets.size)
        assertEquals(owner, runtime.shelf.ownerOf(page.id))
        assertEquals(page.id, runtime.shelf.catalog.pages(displayedBook)[ordinal - 1].id)

        openBook(displayedBook)
        compose.onNodeWithTag("book-chapters").assertIsDisplayed().performClick()
        compose.onNodeWithTag("book-chapter-jump-$owner-chapter")
            .performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText(chapter).assertIsDisplayed()
        val play = hasContentDescription("Play page $ordinal")
        compose.onNodeWithTag("book-detail-list").performScrollToNode(play)
        compose.onNode(play).assertIsDisplayed().performClick()
        compose.onNodeWithText("Page $ordinal").assertIsDisplayed()
        compose.onNodeWithText("0 of $words words found", substring = true).assertIsDisplayed()

        val target = board.targets.first()
        val first = target.positions.first()
        val last = target.positions.last()
        compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
            val cellX = width.toFloat() / board.size
            val cellY = height.toFloat() / board.size
            swipe(
                Offset((first.col + .5f) * cellX, (first.row + .5f) * cellY),
                Offset((last.col + .5f) * cellX, (last.row + .5f) * cellY),
                durationMillis = 400L
            )
        }
        compose.onNodeWithText("1 of $words words found", substring = true).assertIsDisplayed()
        val saved = QaBookshelfRuntime.installed(context).sessionStore?.session(owner, page.id)
        assertEquals(setOf(target.id), saved?.foundIds)
        capture(screenshot)
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        try {
            context.openFileOutput("ws73-$name.png", 0).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
