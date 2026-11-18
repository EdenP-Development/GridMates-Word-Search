package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.BookPageSessionRules
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * testDebug source only: prevents distributing a QA build whose seeded pages
 * fail their own immutable book hash/target/route checks.
 */
class QaPilotCatalogTest {
    @Test fun allThreeExpandedVolumesAndSixtyThreePagesAreActuallyPlayable() {
        assertTrue(BookQaPilot.enabled)
        val catalog = BookQaPilot.catalog()
        assertEquals(3, catalog.books.size)
        assertEquals(
            setOf("Woodland", "Space", "Garden"),
            catalog.books.map { it.title }.toSet()
        )
        assertEquals(63, catalog.books.sumOf { book ->
            catalog.pages(book.id).size
        })
        val puzzleIds = hashSetOf<String>()
        for (book in catalog.books) {
            val seed = when(book.theme.name) { "WOODLAND" -> 171L; "SPACE" -> 281L; else -> 391L }
            catalog.pages(book.id).take(3).forEachIndexed { index,page ->
                val original=com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2.generate(
                    book.theme,page.boardSize,page.challenge,seed+index)
                assertEquals(original.id,page.puzzleId)
                assertEquals(com.playereden.gridmates.wordsearch.core.BookBoardDigest.sha256(original),page.boardSha256)
            }
        }
        for (book in catalog.books) {
            assertEquals(8, book.chapters.size)
            assertEquals(21, catalog.pages(book.id).size)
            assertTrue(book.id.contains("qa"))
            for (chapter in book.chapters) {
                for (page in chapter.pages) {
                    assertTrue(page.id.contains("qa"))
                    val puzzle = BookPageSessionRules.puzzle(catalog, book.id, page.id)
                    assertTrue(puzzleIds.add(puzzle.id))
                    assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, puzzle))
                    assertEquals(page.boardSize.targets, puzzle.targets.size)
                    assertTrue(puzzle.targets.isNotEmpty())
                }
            }
            assertEquals(0, BookProgressRules.completedCount(
                catalog, com.playereden.gridmates.wordsearch.core.BookLibraryProgress(), book.id
            ))
        }
    }
}
