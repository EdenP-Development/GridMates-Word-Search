package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookAlbumGoal
import com.playereden.gridmates.wordsearch.core.BookAlbumProgress
import com.playereden.gridmates.wordsearch.core.BookAlbumRules
import com.playereden.gridmates.wordsearch.core.BookPageAward
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
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
    private const val gardenId = "garden-qa-volume-one"
    private const val woodlandId = "woodland-qa-volume-one"
    val editorialBookIds = setOf(Ws57WoodlandPreview.BOOK_ID,
        Ws66ChapterPreview.SOLAR, Ws66ChapterPreview.CLEARINGS,
        Ws73VarietyPreview.SOLAR, Ws73VarietyPreview.CLEARINGS)

    private val cachedCatalog by lazy { buildCatalog() }
    fun catalog(): BookCatalog = cachedCatalog

    /** WS57 debug-only book, appended without modifying the 63 old QA pages. */
    fun catalog(context: Context): BookCatalog =
        Ws57WoodlandPreview.appendLegacy(context, cachedCatalog)

    fun appendChapterPreview(context: Context, legacy: BookCatalog): BookCatalog =
        Ws66ChapterPreview.append(context, legacy)

    fun appendVarietyPreview(context: Context, legacy: BookCatalog): BookCatalog =
        Ws73VarietyPreview.append(context, legacy)

    private fun starterPages(prefix: String) =
        setOf("$prefix-qa-page-one", "$prefix-qa-page-two")

    private fun originalEditionPages(prefix: String) =
        starterPages(prefix) + "$prefix-qa-page-three" +
            (3..8).flatMap { chapter -> (1..3).map { "$prefix-qa-v5-ch$chapter-page$it" } }

    private val gardenGoals by lazy {
        val starterPages = starterPages("garden")
        val editionPages = originalEditionPages("garden")
        val visitors = (1..3).mapTo(linkedSetOf()) { "garden-qa-v5-ch5-page$it" }
        listOf(
            BookAlbumGoal("garden-first-bloom", "First Bloom", "Finish any Garden page", editionPages, 1),
            BookAlbumGoal("garden-new-shoots", "New Shoots", "Finish the Getting Started chapter", starterPages),
            BookAlbumGoal("garden-visitors", "Garden Visitors", "Finish the Garden Visitors chapter", visitors),
            BookAlbumGoal("garden-full-bloom", "In Full Bloom", "Finish all 21 Garden pages", editionPages)
        )
    }

    private val woodlandGoals by lazy {
        val birdsong = (1..3).flatMap { page ->
            listOf("woodland-qa-v5-ch5-page$page", "ws57-woodland-qa-ch5-page$page")
        }.toSet()
        // The final stamp belongs to this 45-page edition even if more pages are added.
        val editionPages = originalEditionPages("woodland") +
            (3..8).flatMap { chapter -> (1..3).map { "ws57-woodland-qa-ch$chapter-page$it" } } +
            (1..3).flatMap { page ->
                listOf("${Ws66ChapterPreview.CLEARINGS}-page-$page",
                    "${Ws73VarietyPreview.CLEARINGS}-page-$page")
            }
        listOf(
            BookAlbumGoal("woodland-first-footsteps", "First Footsteps",
                "Finish any Woodland page", editionPages, 1),
            BookAlbumGoal("woodland-trailhead", "Trailhead",
                "Finish the Getting Started chapter", starterPages("woodland")),
            BookAlbumGoal("woodland-dawn-chorus", "Dawn Chorus",
                "Finish the six Birdsong pages", birdsong),
            BookAlbumGoal("woodland-deep-woods", "Deep in the Woods",
                "Finish 30 Woodland pages", editionPages, 30),
            BookAlbumGoal("woodland-wanderer", "Woodland Wanderer",
                "Finish all 45 Woodland pages", editionPages)
        )
    }

    fun bookAlbums(
        catalog: BookCatalog,
        shelf: BookShelfPresentation,
        journal: BookLibraryProgress?,
        awards: List<BookPageAward>
    ): Map<String, BookAlbumProgress> = listOfNotNull(
        BookAlbumRules.progress(catalog, woodlandId, woodlandGoals, awards,
            journal, shelf.editionIds(woodlandId)),
        BookAlbumRules.progress(catalog, gardenId, gardenGoals, awards)
    ).associateBy { it.bookId }

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
