package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV2
import com.playereden.gridmates.wordsearch.core.WordTheme
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV5

/**
 * DEBUG/QA BUILD ONLY. This source does NOT exist in release builds.
 *
 * Three real, seeded V2 word-search themes for hands-on phone testing of WS-11.
 * Every volume/page is explicitly a TEST fixture, not licensed/editor-approved
 * book content. These exact IDs are reserved for QA forever and MUST NEVER be
 * reused as published book IDs. Hashes are derived from existing V2 generator
 * solely here; production publisher manifests must instead pin reviewed hashes.
 */
internal object BookQaPilot {
    const val enabled = true

    private val cachedCatalog by lazy { buildCatalog() }
    fun catalog(): BookCatalog = cachedCatalog

    /** WS57 debug-only book, appended without modifying the 63 old QA pages. */
    fun catalog(context: Context): BookCatalog =
        Ws57WoodlandPreview.appendLegacy(context, cachedCatalog)

    private fun buildCatalog(): BookCatalog {
        fun page(
            name: String, theme: WordTheme, size: PuzzleBoardSize,
            challenge: PuzzleChallenge, seed: Long
        ): BookPage {
            val puzzle = WordSearchGeneratorV2.generate(theme, size, challenge, seed)
            return BookPage(
                id = name, puzzleId = puzzle.id,
                generatorVersion = puzzle.contentVersion,
                lexiconVersion = "qa-fixture-v2",
                boardSize = size, challenge = challenge,
                seed = seed, boardSha256 = BookBoardDigest.sha256(puzzle)
            )
        }
        fun volume(
            prefix: String, display: String, theme: WordTheme, baseSeed: Long
        ): PuzzleBook {
            return PuzzleBook(
                id = "$prefix-qa-volume-one",
                title = display,
                volume = 1, theme = theme,
                chapters = listOf(
                    BookChapter(
                        "$prefix-qa-chapter-one", "Getting Started",
                        listOf(
                            page("$prefix-qa-page-one", theme,
                                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED,
                                baseSeed),
                            page("$prefix-qa-page-two", theme,
                                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC,
                                baseSeed + 1L)
                        )
                    ),
                    BookChapter(
                        "$prefix-qa-chapter-two", "A Bigger Challenge",
                        listOf(
                            page("$prefix-qa-page-three", theme,
                                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.CLASSIC,
                                baseSeed + 2L)
                        )
                    )
                ) + (3..8).map { chapter ->
                    val names = when (theme) {
                        WordTheme.WOODLAND -> listOf("Along the Trail", "Under the Canopy", "Birdsong",
                            "By the Stream", "Quiet Clearings", "Forest Discoveries")
                        WordTheme.SPACE -> listOf("Night Sky", "Our Solar System", "Ready for Launch",
                            "Among the Stars", "Mission Control", "Beyond the Horizon")
                        WordTheme.GARDEN -> listOf("First Shoots", "Flower Borders", "Garden Visitors",
                            "Tools and Trellises", "Growing Season", "A Flourishing Garden")
                    }
                    val pack = requireNotNull(QuickPlayContent.themed(theme))
                    BookChapter("$prefix-qa-v5-chapter-$chapter", names[chapter - 3],
                        PuzzleBoardSize.bookPresets.mapIndexed { index, size ->
                            val challenge = if (index == 0) PuzzleChallenge.RELAXED
                                else if (index == 2 && chapter % 2 == 0) PuzzleChallenge.EXPERT
                                else PuzzleChallenge.CLASSIC
                            val frozen = WordSearchGeneratorV5.generate(pack, size, challenge,
                                baseSeed + chapter * 1000L + index * 37L, theme)
                            BookPage("$prefix-qa-v5-ch$chapter-page${index + 1}", frozen.puzzle.id,
                                5, pack.id, size, challenge, frozen.puzzle.seed,
                                BookBoardDigest.sha256(frozen.puzzle), frozen)
                        })
                }
            )
        }
        return BookCatalog(listOf(
            volume("woodland", "Woodland", WordTheme.WOODLAND, 171L),
            volume("space", "Space", WordTheme.SPACE, 281L),
            volume("garden", "Garden", WordTheme.GARDEN, 391L)
        ))
    }
}
