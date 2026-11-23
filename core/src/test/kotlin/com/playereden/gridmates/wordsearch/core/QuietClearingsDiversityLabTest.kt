package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS53: one proposed versioned Quiet Clearings chapter manuscript.
 * Deliberately does not modify old WS52 pages, original WS50 chapter map,
 * the 63 existing QA Library pages, saves, V5 or production vocabulary.
 */
class QuietClearingsDiversityLabTest {
    private data class Entry(
        val chapter: Int, val chapterTitle: String, val page: Int,
        val title: String, val primary: String, val allowed: List<String>,
        val answer: String, val id: String, val display: String,
        val tier: String, val subtopic: String, val provenance: String
    )

    private fun resource(name: String): List<String> =
        requireNotNull(javaClass.classLoader.getResource(name)).readText()
            .trimEnd().lines()

    private fun entries(): List<Entry> {
        val rows = resource("chapter-recipes/ws53-quiet-clearings-revision.tsv")
        assertEquals(
            "chapter\tchapter_title\tpage\tpage_title\tprimary_subtopic" +
                "\tallowed_subtopics\tanswer\tword_id\tdisplay\ttier" +
                "\tword_subtopic\tprovenance", rows.first()
        )
        return rows.drop(1).map { row ->
            val f = row.split('\t')
            require(f.size == 12)
            Entry(f[0].toInt(), f[1], f[2].toInt(), f[3], f[4],
                f[5].split(','), f[6], f[7], f[8], f[9], f[10], f[11])
        }
    }

    private fun sourcePack(): ReviewedWordPack = ReviewedWordPackCodec.decode(
        resource("reviewed-packs/en-woodland-v1.tsv").joinToString("\n")
    )

    private fun originalWS52(): Map<Int, Set<String>> =
        resource("chapter-recipes/ws52-woodland-manuscript.tsv")
            .drop(1).groupBy({ it.substringBefore('\t').toInt() }, { row ->
                row.split('\t')[6]
            }).mapValues { (_, words) -> words.toSet() }

    private fun pagePack(
        terms: List<Entry>, original: ReviewedWordPack
    ): ReviewedWordPack {
        val number = terms.first().page
        val source = buildString {
            append("GRIDMATES_WS53_CLEARINGS_EDITORIAL_QA_V1\n")
            append(original.sha256).append('\n')
            append("WOODLAND:7:").append(number).append("\n")
            append("habitats/trees\n")
            terms.forEach { t ->
                append(t.id).append(':').append(t.answer).append(':')
                append(t.display).append(':').append(t.subtopic).append(':')
                append(t.tier).append('\n')
            }
        }
        val words = terms.map { t ->
            ReviewedWord(t.id, t.answer, t.display,
                listOf(WordTopic("woodland", t.subtopic, t.tier)))
        }
        return ReviewedWordPack(
            "en-woodland-ws53-clearings-p${number}-qa-v1",
            original.sha256, ReviewedWordPackCodec.sha256(source),
            terms.first().title + " (editorial QA only)",
            listOf("woodland"), words
        )
    }

    @Test fun revisedManuscriptIsSourceFaithfulAndNotRetroactive() {
        val original = sourcePack()
        val canonical = original.words.associateBy { it.id }
        val items = entries()
        assertEquals(52, items.size)
        assertEquals(52, items.map { it.answer }.toSet().size)
        assertEquals(listOf(12,16,24), (1..3).map { p ->
            items.count { it.page == p }
        })
        assertTrue(items.all {
            it.chapter == 7 && it.chapterTitle == "Quiet Clearings" &&
                it.primary == "habitats" && it.allowed == listOf("trees","habitats") &&
                it.provenance == "existing_woodland_QA_not_release"
        })
        for (term in items) {
            val word = requireNotNull(canonical[term.id])
            assertEquals(word.answer, term.answer)
            assertEquals(word.display, term.display)
            assertTrue(word.topics.any { t ->
                t.topicId == "woodland" && t.subtopic == term.subtopic &&
                    t.tier == term.tier
            })
            assertTrue(term.subtopic in term.allowed)
        }
        val means = (1..3).map { page ->
            val group = items.filter { it.page == page }
            assertTrue(group.all { it.answer.length in 4..listOf(7,10,15)[page-1] })
            if (page == 1) assertTrue(group.all { it.tier == "familiar" })
            assertTrue(group.count { it.subtopic == "habitats" } >=
                listOf(5,6,9)[page-1])
            for (a in group) for (b in group) if (a !== b) {
                assertTrue("V5 authored target substring conflict: ${a.answer}/${b.answer}",
                    !a.answer.contains(b.answer) && !b.answer.contains(a.answer))
            }
            group.map { it.answer.length }.average()
        }
        assertTrue(means[0] < means[1] && means[1] < means[2])
        assertEquals(listOf("Moss and Fern", "Grove and Canopy",
            "Woodland Whispers"), (1..3).map { p ->
            items.first { it.page == p }.title
        })
    }

    @Test fun revisedChapterBuildsThreeFrozenBoardsAndReducesBookDuplication() {
        val source = sourcePack()
        val items = entries()
        val manuscript = mutableListOf(
            "page\tpage_title\tboard\ttargets\tchallenge\tseed" +
                "\tpack_id\tpack_sha256\tboard_sha256\ttarget_words" +
                "\tgrid_rows\tapproval"
        )
        val pages = mutableListOf<BookPage>()
        val selected = mutableSetOf<String>()
        val challenges = listOf(
            PuzzleChallenge.RELAXED, PuzzleChallenge.CLASSIC,
            PuzzleChallenge.EXPERT
        )
        for (page in 1..3) {
            val terms = items.filter { it.page == page }
            val size = PuzzleBoardSize.bookPresets[page-1]
            val challenge = challenges[page-1]
            val seed = 533_070L + page * 41L
            val pack = pagePack(terms, source)
            val frozen = WordSearchGeneratorV5.generate(
                pack, size, challenge, seed, WordTheme.WOODLAND
            )
            val again = WordSearchGeneratorV5.generate(
                pack, size, challenge, seed, WordTheme.WOODLAND
            )
            val p = frozen.puzzle
            val hash = BookBoardDigest.sha256(p)
            assertEquals(hash, BookBoardDigest.sha256(again.puzzle))
            assertEquals(terms.map { it.answer }.toSet(),
                p.targets.map { it.word }.toSet())
            assertEquals(terms.map { it.id }.toSet(),
                p.targets.map { it.id }.toSet())
            p.targets.forEach { target ->
                assertTrue("A chapter target must never repeat", selected.add(target.word))
            }
            pages += BookPage(
                "woodland-ws53-ch7-qa-page-${page}", p.id,
                WordSearchGeneratorV5.CONTENT_VERSION,
                pack.id, size, challenge, seed, hash, frozen
            )
            manuscript += listOf(
                page.toString(), terms.first().title, size.cells.toString(),
                size.targets.toString(), challenge.name, seed.toString(),
                pack.id, pack.sha256, hash,
                p.targets.joinToString(",") { it.word },
                p.rows.joinToString("/"), "QA_research_only_NOT_published"
            ).joinToString("\t")
        }
        assertEquals(52, selected.size)
        val chapter = BookChapter("woodland-ws53-quiet-clearings-qa",
            "Quiet Clearings", pages)
        val book = PuzzleBook("woodland-ws53-candidate-book",
            "Quiet Clearings Editorial Lab", 2,
            WordTheme.WOODLAND, listOf(chapter))
        val catalog = BookCatalog(listOf(book))
        for (page in pages) {
            val p = BookPageSessionRules.puzzle(catalog, book.id, page.id)
            assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, p))
        }

        val old = originalWS52()
        assertEquals(setOf(3,4,6,7,8), old.keys)
        assertEquals(48, old.getValue(7).intersect(old.getValue(3)).size)
        assertEquals(33, old.getValue(7).intersect(old.getValue(6)).size)
        assertEquals(0, old.getValue(7).intersect(old.getValue(4)).size)
        assertEquals(12, old.getValue(7).intersect(old.getValue(8)).size)
        val new = old.toMutableMap()
        new[7] = selected
        val oldUnique = old.values.flatten().toSet().size
        val newUnique = new.values.flatten().toSet().size
        assertEquals(124, oldUnique)
        assertEquals(129, newUnique)
        val comparison = mutableListOf(
            "metric\tcomparison\tbaseline_WS52\tproposed_WS53\tdelta"
        )
        for (other in listOf(3,4,6,8)) {
            val before = old.getValue(7).intersect(old.getValue(other)).size
            val after = selected.intersect(old.getValue(other)).size
            if (other == 3) assertEquals(18, after)
            assertTrue("Do not shift overlap to another 30+-word chapter",
                after < 30)
            comparison += listOf(
                "pairwise_shared_targets", "Quiet Clearings vs Chapter $other",
                before.toString(), after.toString(), (after-before).toString()
            ).joinToString("\t")
        }
        comparison += listOf(
            "book_unique_targets", "Five Woodland chapters",
            oldUnique.toString(), newUnique.toString(),
            (newUnique-oldUnique).toString()
        ).joinToString("\t")
        fun write(filename: String, lines: List<String>) {
            val file = File("build/$filename")
            file.parentFile.mkdirs()
            file.writeText(lines.joinToString("\n") + "\n")
        }
        write("ws53-quiet-clearings-frozen-boards.tsv", manuscript)
        write("ws53-woodland-overlap-delta.tsv", comparison)
        println("WS53 CLEARINGS: 3 deterministic frozen QA boards, " +
            "52 distinct words, shared with Trail 48->18, " +
            "whole-book distinct 124->129; no existing page changed")
    }
}
