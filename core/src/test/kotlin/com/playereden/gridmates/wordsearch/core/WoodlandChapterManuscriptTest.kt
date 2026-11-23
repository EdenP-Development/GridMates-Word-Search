package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS52 research only. Five additional Woodland chapters, 15 author-chosen
 * target lists; unmodified V5 generates layouts. Never change the old
 * production/QA books, their page IDs, progress or lexicon authorization.
 */
class WoodlandChapterManuscriptTest {
    private data class Entry(
        val chapter: Int, val chapterTitle: String, val primary: String,
        val allowed: List<String>, val page: Int, val pageTitle: String,
        val answer: String, val id: String, val display: String,
        val tier: String, val subtopic: String, val provenance: String
    )

    private fun manuscript(): List<Entry> {
        val source = requireNotNull(javaClass.classLoader.getResource(
            "chapter-recipes/ws52-woodland-manuscript.tsv"
        )).readText().trimEnd().lines()
        assertEquals(
            "chapter\tchapter_title\tprimary_subtopic\tallowed_subtopics" +
                "\tpage\tpage_title\tanswer\tword_id\tdisplay\ttier" +
                "\tword_subtopic\tprovenance",
            source.first()
        )
        return source.drop(1).map { row ->
            val f = row.split('\t')
            require(f.size == 12)
            Entry(
                f[0].toInt(), f[1], f[2], f[3].split(','),
                f[4].toInt(), f[5], f[6], f[7], f[8], f[9], f[10], f[11]
            )
        }
    }

    private fun woodland(): ReviewedWordPack {
        val source = requireNotNull(javaClass.classLoader.getResource(
            "reviewed-packs/en-woodland-v1.tsv"
        )).readText()
        return ReviewedWordPackCodec.decode(source)
    }

    private fun qaPack(
        group: List<Entry>, chapter: Int, page: Int, original: ReviewedWordPack
    ): ReviewedWordPack {
        val source = buildString {
            append("GRIDMATES_WS52_WOODLAND_FROZEN_CANDIDATE_V1\n")
            append(original.sha256).append('\n')
            append(chapter).append(':').append(page).append('\n')
            group.forEach { term ->
                append(term.id).append(':').append(term.answer).append(':')
                append(term.display).append(':').append(term.subtopic)
                append(':').append(term.tier).append('\n')
            }
        }
        val selected = group.map { term ->
            ReviewedWord(
                term.id, term.answer, term.display,
                listOf(WordTopic("woodland", term.subtopic, term.tier))
            )
        }
        return ReviewedWordPack(
            "en-woodland-ws52-ch${chapter}-p${page}-qa-v1",
            original.sha256, ReviewedWordPackCodec.sha256(source),
            group.first().pageTitle + " QA preview",
            listOf("woodland"), selected
        )
    }

    @Test fun originalVocabularyAndChapterRecipesRemainSourceFaithful() {
        val entries = manuscript()
        val source = woodland()
        val mapped = source.words.associateBy { it.id }
        val sourceMap = requireNotNull(javaClass.classLoader.getResource(
            "chapter-recipes/ws50-chapter-map.tsv"
        )).readText().trimEnd().lines().drop(1).map { it.split('\t') }
        assertEquals(260, entries.size)
        assertEquals(setOf(3,4,6,7,8), entries.map { it.chapter }.toSet())
        assertTrue(entries.all {
            it.provenance == "existing_woodland_QA_not_release"
        })
        for (term in entries) {
            val original = requireNotNull(mapped[term.id]) {
                "No canonical Woodland ID ${term.id}"
            }
            assertEquals(original.answer, term.answer)
            assertEquals(original.display, term.display)
            assertTrue(original.topics.any {
                it.topicId == "woodland" &&
                    it.subtopic == term.subtopic && it.tier == term.tier
            })
            assertTrue(term.subtopic in term.allowed)
            val map = sourceMap.single {
                it[0] == "WOODLAND" && it[1].toInt() == term.chapter
            }
            assertEquals(map[2], term.chapterTitle)
            assertEquals(map[3], term.primary)
            assertEquals(map[4].split(','), term.allowed)
        }
        for (chapter in entries.groupBy { it.chapter }.toSortedMap()) {
            val group = chapter.value
            assertEquals("No target repetition within entire chapter",
                52, group.map { it.answer }.toSet().size)
            assertEquals(listOf(12,16,24), (1..3).map { page ->
                group.count { it.page == page }
            })
            val lengths = (1..3).map { page ->
                val terms = group.filter { it.page == page }
                val allowedMax = listOf(7,10,15)[page - 1]
                assertTrue(terms.all { it.answer.length in 4..allowedMax })
                if (page == 1) assertTrue(terms.all { it.tier == "familiar" })
                assertTrue(terms.count { it.subtopic == terms.first().primary } >=
                    listOf(5,6,9)[page - 1])
                for (a in terms) for (b in terms) if (a !== b) {
                    assertTrue("Substring conflict in chapter ${chapter.key}, page $page",
                        !a.answer.contains(b.answer) && !b.answer.contains(a.answer))
                }
                terms.map { it.answer.length }.average()
            }
            assertTrue("Page target lengths must increase in chapter ${chapter.key}",
                lengths[0] < lengths[1] && lengths[1] < lengths[2])
        }
    }

    @Test fun fifteenRealBoardsUseExactPageTargetsAndFrozenBookRules() {
        val entries = manuscript()
        val original = woodland()
        val challenges = listOf(
            PuzzleChallenge.RELAXED, PuzzleChallenge.CLASSIC,
            PuzzleChallenge.EXPERT
        )
        val bookChapters = mutableListOf<BookChapter>()
        val report = mutableListOf(
            "chapter\tchapter_title\tpage\tpage_title\tboard\ttargets" +
                "\tchallenge\tseed\tmean_target_length\tprimary_terms" +
                "\tpack_id\tpack_sha256\tboard_sha256\ttargets_list" +
                "\tgrid_rows\tpublication_status"
        )
        val metrics = mutableListOf(
            "chapter\tchapter_title\tpage\tpage_title\ttargets" +
                "\tmean_target_length\tprimary_target_count" +
                "\tchapter_distinct_targets\tchapter_cross_page_repeats" +
                "\tqa_scope"
        )
        val pageWords = linkedMapOf<Int, Set<String>>()
        for ((chapter, group) in entries.groupBy { it.chapter }.toSortedMap()) {
            val bookPages = mutableListOf<BookPage>()
            val used = mutableSetOf<String>()
            for (page in 1..3) {
                val words = group.filter { it.page == page }
                val boardSize = PuzzleBoardSize.bookPresets[page - 1]
                val challenge = challenges[page - 1]
                val seed = 520_000L + chapter * 1_000L + page * 41L
                val pack = qaPack(words, chapter, page, original)
                val frozen = WordSearchGeneratorV5.generate(
                    pack, boardSize, challenge, seed, WordTheme.WOODLAND
                )
                val again = WordSearchGeneratorV5.generate(
                    pack, boardSize, challenge, seed, WordTheme.WOODLAND
                )
                val p = frozen.puzzle
                val hash = BookBoardDigest.sha256(p)
                assertEquals("Same recipe and seed must reproduce exact board",
                    hash, BookBoardDigest.sha256(again.puzzle))
                assertEquals(words.map { it.id }.toSet(), p.targets.map { it.id }.toSet())
                assertEquals(words.map { it.answer }.toSet(),
                    p.targets.map { it.word }.toSet())
                for (target in p.targets) {
                    assertTrue("Repeated target in chapter ${chapter}: ${target.word}",
                        used.add(target.word))
                }
                val id = "woodland-ws52-ch${chapter}-p${page}-qa"
                val manuscriptPage = BookPage(
                    id, p.id, WordSearchGeneratorV5.CONTENT_VERSION,
                    pack.id, boardSize, challenge, seed, hash, frozen
                )
                bookPages += manuscriptPage
                val mean = String.format(
                    Locale.ROOT, "%.2f", words.map { it.answer.length }.average()
                )
                val primaryCount = words.count { it.subtopic == words.first().primary }
                report += listOf(
                    chapter.toString(), words.first().chapterTitle, page.toString(),
                    words.first().pageTitle, boardSize.cells.toString(),
                    boardSize.targets.toString(), challenge.name, seed.toString(),
                    mean, primaryCount.toString(), pack.id, pack.sha256, hash,
                    p.targets.joinToString(",") { it.word },
                    p.rows.joinToString("/"), "QA_only_not_release_approved"
                ).joinToString("\t")
                metrics += listOf(
                    chapter.toString(), words.first().chapterTitle, page.toString(),
                    words.first().pageTitle, boardSize.targets.toString(),
                    mean, primaryCount.toString(), "52", "0",
                    "source_verified_QA_editorial_candidate"
                ).joinToString("\t")
            }
            assertEquals(52, used.size)
            pageWords[chapter] = used.toSet()
            bookChapters += BookChapter(
                "woodland-ws52-ch${chapter}-qa",
                group.first().chapterTitle, bookPages
            )
        }
        assertEquals(5, bookChapters.size)
        assertEquals(15, bookChapters.sumOf { it.pages.size })
        val book = PuzzleBook(
            "woodland-ws52-qa-manuscript-volume", "Woodland WS52 Manuscript QA",
            2, WordTheme.WOODLAND, bookChapters
        )
        val catalog = BookCatalog(listOf(book))
        for (chapter in bookChapters) for (page in chapter.pages) {
            val p = BookPageSessionRules.puzzle(catalog, book.id, page.id)
            assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, p))
        }
        fun write(name: String, lines: List<String>) {
            val file = File("build/$name")
            file.parentFile.mkdirs()
            file.writeText(lines.joinToString("\n") + "\n")
        }
        write("ws52-woodland-five-chapter-frozen-boards.tsv", report)
        write("ws52-woodland-chapter-quality.tsv", metrics)
        assertEquals(16, report.size)
        assertEquals(16, metrics.size)
        val all = pageWords.values.flatten()
        println("WS52 WOODLAND: 15 frozen QA candidate boards, 260 word slots, " +
            "${all.toSet().size} unique across chapters, zero repeats within " +
            "each chapter; report contains page/pack/board SHA and exact grids")
    }
}
