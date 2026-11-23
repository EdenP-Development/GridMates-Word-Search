package com.playereden.gridmates.wordsearch.core

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS50 — strictly test-only editorial prototype, not a new playable catalog.
 *
 * The existing 63 QA pages MUST NOT change behind their page IDs. This lab
 * proposes chapter-scoped vocabulary for FUTURE distinct versioned page IDs.
 * V5 selects/places words; chapter metadata constrains the source pool.
 */
class ChapterRecipeLabTest {
    private data class Recipe(
        val theme: WordTheme,
        val number: Int,
        val title: String,
        val primary: String,
        val allowed: List<String>
    )

    private fun recipes(): List<Recipe> {
        val text = requireNotNull(javaClass.classLoader.getResource(
            "chapter-recipes/ws50-chapter-map.tsv"
        )).readText().trimEnd().lines()
        assertEquals(
            "theme\tchapter_number\tchapter_title\tprimary_subtopic\tallowed_subtopics",
            text.first()
        )
        return text.drop(1).map { row ->
            val fields = row.split('\t')
            require(fields.size == 5)
            Recipe(
                WordTheme.valueOf(fields[0]), fields[1].toInt(), fields[2], fields[3],
                fields[4].split(',')
            )
        }
    }

    private fun pack(theme: WordTheme): ReviewedWordPack {
        val name = "reviewed-packs/en-${theme.name.lowercase()}-v1.tsv"
        val raw = requireNotNull(javaClass.classLoader.getResource(name)).readText()
        return ReviewedWordPackCodec.decode(raw)
    }

    private fun candidates(recipe: Recipe, original: ReviewedWordPack): List<ReviewedWord> =
        original.words.filter { word ->
            word.topics.any { assignment ->
                assignment.topicId == recipe.theme.name.lowercase() &&
                    assignment.subtopic in recipe.allowed
            }
        }

    /** Derive a NEW QA-only identity from the exact source pack and recipe. */
    private fun scopedPack(recipe: Recipe, original: ReviewedWordPack): ReviewedWordPack {
        val words = candidates(recipe, original)
        val slug = recipe.title.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
        val id = "en-${recipe.theme.name.lowercase()}-$slug-qa-v1"
        val provenance = buildString {
            append("GRIDMATES_WS50_CHAPTER_QA_V1\n")
            append(original.sha256).append('\n')
            append(recipe.theme.name).append(':').append(recipe.number).append('\n')
            append(recipe.title).append('\n')
            append(recipe.allowed.joinToString(",")).append('\n')
            words.forEach { append(it.id).append(':').append(it.answer).append('\n') }
        }
        return ReviewedWordPack(
            id, original.sha256, ReviewedWordPackCodec.sha256(provenance),
            recipe.title + " (chapter QA only)", listOf(recipe.theme.name.lowercase()), words
        )
    }

    @Test fun everyPlannedChapterHasTraceableSourceVocabulary() {
        val items = recipes()
        assertEquals(18, items.size)
        assertEquals(18, items.map { it.theme to it.number }.toSet().size)
        val lines = mutableListOf(
            "theme\tchapter\ttitle\tprimary\tallowed\tunique_candidate_words" +
                "\tfamiliar_12x12_candidates\tshortest_word\tlongest_word\tapproval"
        )
        for (theme in WordTheme.entries) {
            val group = items.filter { it.theme == theme }.sortedBy { it.number }
            assertEquals(listOf(3, 4, 5, 6, 7, 8), group.map { it.number })
            val original = pack(theme)
            for (recipe in group) {
                assertTrue(recipe.title.isNotBlank())
                assertTrue(recipe.primary in recipe.allowed)
                assertEquals(recipe.allowed.size, recipe.allowed.distinct().size)
                val allSubtopics = original.words.flatMap { word ->
                    word.topics.filter { it.topicId == theme.name.lowercase() }
                        .map { it.subtopic }
                }.toSet()
                assertTrue("Unknown subtopic: ${recipe.title}",
                    recipe.allowed.all { it in allSubtopics })
                val pool = candidates(recipe, original)
                val shortFamiliar = pool.count { word ->
                    word.answer.length <= 11 && word.topics.any {
                        it.subtopic in recipe.allowed && it.tier == "familiar"
                    }
                }
                assertTrue("Not enough familiar 12x12 candidates for ${recipe.title}",
                    shortFamiliar >= PuzzleBoardSize.BOOK_SHORT.targets)
                assertTrue("Not enough 20x20 candidates for ${recipe.title}",
                    pool.count { it.answer.length <= 19 } >= PuzzleBoardSize.BOOK_BIG.targets)
                assertEquals(pool.size, pool.map { it.id }.toSet().size)
                lines += listOf(
                    theme.name, recipe.number.toString(), recipe.title, recipe.primary,
                    recipe.allowed.joinToString(","), pool.size.toString(),
                    shortFamiliar.toString(),
                    pool.minOf { it.answer.length }.toString(),
                    pool.maxOf { it.answer.length }.toString(),
                    "research_QA_only_not_published"
                ).joinToString("\t")
            }
        }
        val report = File("build/ws50-chapter-pool-coverage.tsv")
        report.parentFile.mkdirs()
        report.writeText(lines.joinToString("\n") + "\n")
        assertEquals(19, lines.size)
        println("WS50 CHAPTER SCOPES: 18 topic-fitted QA plans; TSV=$report")
    }

    @Test fun birdsongThreePagePreviewIsFixedThemedAndVersioned() {
        val recipe = recipes().single {
            it.theme == WordTheme.WOODLAND && it.number == 5
        }
        assertEquals("Birdsong", recipe.title)
        assertEquals(listOf("birds"), recipe.allowed)
        val chapterPack = scopedPack(recipe, pack(recipe.theme))
        assertEquals(29, chapterPack.words.size)
        assertTrue(chapterPack.words.all { word ->
            word.topics.any { it.topicId == "woodland" && it.subtopic == "birds" }
        })
        val pageTitles = listOf(
            "Forest Birdwatching", "Feathers and Flight", "Birdsong Challenge"
        )
        val sizes = PuzzleBoardSize.bookPresets
        val pages = sizes.mapIndexed { index, size ->
            val challenge = if (index == 0) PuzzleChallenge.RELAXED
                else PuzzleChallenge.CLASSIC
            val seed = 171L + 5L * 1000 + index * 37L
            val frozen = WordSearchGeneratorV5.generate(
                chapterPack, size, challenge, seed, WordTheme.WOODLAND
            )
            val again = WordSearchGeneratorV5.generate(
                chapterPack, size, challenge, seed, WordTheme.WOODLAND
            )
            assertEquals(size.targets, frozen.puzzle.targets.size)
            assertEquals("Exact same source must reconstruct the same board",
                BookBoardDigest.sha256(frozen.puzzle), BookBoardDigest.sha256(again.puzzle))
            assertEquals(chapterPack.id, frozen.packId)
            assertEquals(chapterPack.sha256, frozen.packSha256)
            assertEquals(chapterPack.words.map { it.id }.toSet().intersect(
                frozen.puzzle.targets.map { it.id }.toSet()).size, size.targets)
            assertTrue(frozen.puzzle.targets.all { target ->
                chapterPack.words.any { it.id == target.id && it.answer == target.word }
            })
            BookPage(
                id = "woodland-ws50-qa-birdsong-page-${index + 1}",
                puzzleId = frozen.puzzle.id,
                generatorVersion = WordSearchGeneratorV5.CONTENT_VERSION,
                lexiconVersion = chapterPack.id,
                boardSize = size, challenge = challenge, seed = seed,
                boardSha256 = BookBoardDigest.sha256(frozen.puzzle), frozen = frozen
            ) to pageTitles[index]
        }
        val chapter = BookChapter(
            "woodland-ws50-qa-birdsong-chapter", recipe.title, pages.map { it.first }
        )
        val book = PuzzleBook(
            "woodland-ws50-qa-pilot-book", "Woodland Chapter Lab", 1,
            WordTheme.WOODLAND, listOf(chapter)
        )
        val catalog = BookCatalog(listOf(book))
        val lines = mutableListOf(
            "page\tpage_title\tsize\ttargets\tchallenge\tseed\tpack_id" +
                "\tpack_sha256\tboard_sha256\twords\tgrid_rows\tapproval"
        )
        val targetSets = mutableListOf<Set<String>>()
        pages.forEachIndexed { index, (page, title) ->
            val puzzle = BookPageSessionRules.puzzle(catalog, book.id, page.id)
            assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, puzzle))
            val words = puzzle.targets.map { it.word }.toSet()
            assertEquals(page.boardSize.targets, words.size)
            targetSets += words
            lines += listOf(
                (index + 1).toString(), title, page.boardSize.cells.toString(),
                page.boardSize.targets.toString(), page.challenge.name,
                page.seed.toString(), chapterPack.id, chapterPack.sha256,
                page.boardSha256, puzzle.targets.joinToString(",") { it.word },
                puzzle.rows.joinToString("/"), "unapproved_frozen_QA_preview"
            ).joinToString("\t")
        }
        assertEquals(3, targetSets.size)
        assertFalse(pages[0].first.boardSha256 == pages[1].first.boardSha256)
        assertFalse(pages[1].first.boardSha256 == pages[2].first.boardSha256)
        val overlaps = listOf(
            targetSets[0].intersect(targetSets[1]).size,
            targetSets[1].intersect(targetSets[2]).size
        )
        val report = File("build/ws50-birdsong-qa-preview.tsv")
        report.parentFile.mkdirs()
        report.writeText(lines.joinToString("\n") + "\n")
        println("WS50 BIRDSONG: 3 generated, unique, exact-hash QA boards; " +
            "adjacent target overlaps=${overlaps.joinToString("/")}; TSV=$report")
        assertEquals(4, lines.size)
        // Cross-page repetition is measured for editorial QA, not waved through.
        // With only 29 bird terms and 52 target slots, repeated words are inevitable.
    }
}
