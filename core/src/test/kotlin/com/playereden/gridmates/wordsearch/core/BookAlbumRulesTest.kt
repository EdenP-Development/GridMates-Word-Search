package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookAlbumRulesTest {
    private val gardenId = "garden-test-volume"
    private val woodlandId = "woodland-test-volume"

    private fun page(id: String, theme: WordTheme, seed: Long): BookPage {
        val size = PuzzleBoardSize.BOOK_SHORT
        val challenge = PuzzleChallenge.RELAXED
        val puzzle = WordSearchGeneratorV2.generate(theme, size, challenge, seed)
        return BookPage(id, puzzle.id, puzzle.contentVersion, "album-test-v2",
            size, challenge, seed, BookBoardDigest.sha256(puzzle))
    }

    private val first = page("garden-test-page-one", WordTheme.GARDEN, 1L)
    private val second = page("garden-test-page-two", WordTheme.GARDEN, 2L)
    private val extra = page("garden-test-page-extra", WordTheme.GARDEN, 3L)
    private val woodland = page("woodland-test-page-one", WordTheme.WOODLAND, 4L)
    private val required = setOf(first.id, second.id)
    private val goals = listOf(
        BookAlbumGoal("first-bloom", "First Bloom", "Finish a page", required, target = 1),
        BookAlbumGoal("full-bloom", "Full Bloom", "Finish the edition", required)
    )

    private fun catalog(gardenPages: List<BookPage>) = BookCatalog(listOf(
        PuzzleBook(gardenId, "Garden", 1, WordTheme.GARDEN,
            listOf(BookChapter("garden-test-chapter", "Garden chapter", gardenPages))),
        PuzzleBook(woodlandId, "Woodland", 1, WordTheme.WOODLAND,
            listOf(BookChapter("woodland-test-chapter", "Woodland chapter", listOf(woodland))))
    ))

    private fun award(book: String, page: BookPage) =
        BookPageAward(book, page.id, page.puzzleId, page.boardSha256, 12)

    @Test fun onlyDistinctMatchingGardenPagesEarnStamps() {
        val firstAward = award(gardenId, first)
        val awards = listOf(firstAward, firstAward, award(woodlandId, woodland),
            award(gardenId, second).copy(boardSha256 = "e".repeat(64)),
            award(woodlandId, second), award(gardenId, second).copy(puzzleId = first.puzzleId))
        val progress = requireNotNull(BookAlbumRules.progress(catalog(listOf(first, second)),
            gardenId, goals, awards))
        assertEquals(1, progress.earnedCount)
        assertEquals(1, progress.next?.completed)
        assertFalse(progress.stamps.last().earned)
    }

    @Test fun laterPagesKeepExistingEditionMilestonesEarned() {
        val completed = listOf(award(gardenId, first), award(gardenId, second))
        val progress = requireNotNull(BookAlbumRules.progress(catalog(listOf(first, second, extra)),
            gardenId, goals, completed))
        assertEquals(2, progress.earnedCount)
        assertTrue(progress.stamps.all { it.earned })
        assertNull(progress.next)
        assertNull(BookAlbumRules.progress(catalog(listOf(first)), gardenId, goals, completed))
    }

    @Test fun groupedBooksCreditVerifiedPagesFromEachOwnerOnce() {
        val appendixId = "woodland-test-appendix"
        val appendixPage = page("woodland-test-appendix-page", WordTheme.WOODLAND, 5L)
        val source = BookCatalog(catalog(listOf(first, second)).books + PuzzleBook(
            appendixId, "Woodland appendix", 2, WordTheme.WOODLAND,
            listOf(BookChapter("woodland-test-appendix-chapter", "Birdsong", listOf(appendixPage)))
        ))
        val pages = setOf(woodland.id, appendixPage.id)
        val chapter = listOf(BookAlbumGoal("birdsong", "Birdsong", "Finish both pages", pages))
        val awards = listOf(award(woodlandId, woodland), award(gardenId, first))
        val editions = setOf(woodlandId, appendixId)
        val wrongOwner = BookLibraryProgress(mapOf(woodlandId to BookVolumeProgress(
            woodlandId, setOf(woodland.id, appendixPage.id))))
        val partial = requireNotNull(BookAlbumRules.progress(
            source, woodlandId, chapter, awards, wrongOwner, editions))
        assertEquals(1, partial.next?.completed)

        val puzzle = BookPageSessionRules.puzzle(source, appendixId, appendixPage.id)
        val journal = BookProgressRules.recordVerifiedCompletion(source, BookLibraryProgress(),
            appendixId, appendixPage.id, puzzle, puzzle.targets.mapTo(mutableSetOf()) { it.id })
        val complete = requireNotNull(BookAlbumRules.progress(
            source, woodlandId, chapter, awards, journal, editions))
        assertEquals(1, complete.earnedCount)
        assertNull(complete.next)
    }
}
