package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS54 research: complete six-chapter Woodland QA vocabulary comparison plus
 * a new, explicitly UNAPPROVED By the Stream chapter. Nothing here changes
 * published/QA Library IDs, prior frozen research books, bonus recognition,
 * existing source packs, saved progress or V5 placement logic.
 */
class ForestDiscoveriesWildlifeLabTest {
    private data class Entry(
        val chapter: Int, val chapterTitle: String, val page: Int,
        val title: String, val answer: String, val id: String,
        val display: String, val tier: String, val subtopic: String,
        val provenance: String
    )

    private fun read(name: String): List<String> =
        requireNotNull(javaClass.classLoader.getResource(name)).readText()
            .trimEnd().lines()

    private fun terms(): List<Entry> {
        val lines = read("chapter-recipes/ws55-discoveries-manuscript.tsv")
        assertEquals(
            "chapter\tchapter_title\tpage\tpage_title\tanswer\tword_id" +
                "\tdisplay\ttier\tsubtopic\tprovenance",
            lines.first()
        )
        return lines.drop(1).map { line ->
            val f = line.split('\t')
            require(f.size == 10)
            Entry(f[0].toInt(), f[1], f[2].toInt(), f[3], f[4],
                f[5], f[6], f[7], f[8], f[9])
        }
    }

    private fun originalPack(): ReviewedWordPack = ReviewedWordPackCodec.decode(
        read("reviewed-packs/en-woodland-v1.tsv").joinToString("\n")
    )

    private fun existingSixChapters(): Map<Int, Set<String>> {
        val old = read("chapter-recipes/ws52-woodland-manuscript.tsv")
            .drop(1).map { it.split('\t') }
        val chapters = old.groupBy({ it[0].toInt() }, { it[6] })
            .mapValues { (_, v) -> v.toSet() }.toMutableMap()
        chapters[7] = read("chapter-recipes/ws53-quiet-clearings-revision.tsv")
            .drop(1).map { it.split('\t')[6] }.toSet()
        chapters[5] = read("chapter-recipes/ws51-birdsong-qa-manuscript.tsv")
            .drop(1).map { it.split('\t')[2] }.toSet()
        assertEquals(setOf(3,4,5,6,7,8), chapters.keys)
        assertTrue(chapters.values.all { it.size == 52 })
        return chapters
    }

    private fun pack(terms: List<Entry>, source: ReviewedWordPack): ReviewedWordPack {
        val page = terms.first().page
        val stamp = buildString {
            append("GRIDMATES_WS55_DISCOVERIES_QA_ONLY\n")
            append(source.sha256).append('\n')
            append("WOODLAND:8:").append(page).append('\n')
            for (term in terms) {
                append(term.id).append(':').append(term.answer).append(':')
                append(term.display).append(':').append(term.tier).append(':')
                append(term.subtopic).append(':').append(term.provenance)
                append('\n')
            }
        }
        return ReviewedWordPack(
            "en-woodland-ws55-discoveries-p${page}-qa-v1",
            source.sha256, ReviewedWordPackCodec.sha256(stamp),
            terms.first().title + " (wildlife manuscript QA only)",
            listOf("woodland"),
            terms.map { term ->
                ReviewedWord(
                    term.id, term.answer, term.display,
                    listOf(WordTopic("woodland", term.subtopic, term.tier))
                )
            }
        )
    }

    @Test fun originalWordIDsAndUnapprovedCandidatesAreExplicit() {
        val terms = terms()
        val source = originalPack()
        val original = source.words.associateBy { it.id }
        val existing = terms.filter { it.provenance == "existing_woodland_QA_not_release" }
        val proposed = terms.filter {
            it.provenance == "ws55_wildlife_signs_candidate_unapproved"
        }
        assertEquals(52, terms.size)
        assertEquals(52, terms.map { it.answer }.toSet().size)
        assertEquals(52, terms.map { it.id }.toSet().size)
        assertEquals(26, existing.size)
        assertEquals(26, proposed.size)
        assertTrue(terms.all {
            it.chapter == 8 && it.chapterTitle == "Forest Discoveries" &&
                it.answer.matches(Regex("[A-Z]{3,16}"))
        })
        for (term in existing) {
            val originalTerm = requireNotNull(original[term.id])
            assertEquals(originalTerm.answer, term.answer)
            assertEquals(originalTerm.display, term.display)
            assertTrue(originalTerm.topics.any { t ->
                t.topicId == "woodland" && t.subtopic == term.subtopic &&
                    t.tier == term.tier
            })
        }
        assertTrue(proposed.all {
            it.id.startsWith("ws55w") && it.subtopic == "streamlife" &&
                it.id !in original
        })
        assertTrue("Original QA source pack must never be expanded by this test",
            source.words.none { it.id.startsWith("ws55w") })
        val means = (1..3).map { page ->
            val selected = terms.filter { it.page == page }
            assertEquals(listOf(12,16,24)[page-1], selected.size)
            assertTrue(selected.all {
                it.answer.length <= listOf(7,10,15)[page-1]
            })
            if (page == 1) assertTrue(selected.all { it.tier == "familiar" })
            for (a in selected) for (b in selected) if (a !== b) {
                assertFalse("Would remove an author-selected nested target: " +
                    "${a.answer} in ${b.answer}", b.answer.contains(a.answer))
            }
            selected.map { it.answer.length }.average()
        }
        assertTrue("Page mean lengths should rise with difficulty",
            means[0] < means[1] && means[1] < means[2])
        assertEquals(
            listOf("Tracks and Trails", "Signs of Wildlife", "Hidden in the Woods"),
            (1..3).map { page -> terms.first { it.page == page }.title }
        )
    }

    @Test fun generatedPagesAndSixChapterOverlapImprovementsAreReproducible() {
        val source = originalPack()
        val manuscript = terms()
        val sizes = PuzzleBoardSize.bookPresets
        val challenges = listOf(
            PuzzleChallenge.RELAXED, PuzzleChallenge.CLASSIC,
            PuzzleChallenge.EXPERT
        )
        val pages = mutableListOf<BookPage>()
        val targetSet = mutableSetOf<String>()
        val boardReport = mutableListOf(
            "page\tpage_title\tboard\ttargets\tchallenge\tseed" +
                "\tpack_id\tpack_sha256\tboard_sha256\ttarget_words" +
                "\tgrid_rows\tapproval"
        )

        for (index in 0..2) {
            val pageNum = index + 1
            val group = manuscript.filter { it.page == pageNum }
            val scoped = pack(group, source)
            val seed = 555_080L + pageNum * 43L
            val generated = WordSearchGeneratorV5.generate(
                scoped, sizes[index], challenges[index], seed, WordTheme.WOODLAND
            )
            val repeated = WordSearchGeneratorV5.generate(
                scoped, sizes[index], challenges[index], seed, WordTheme.WOODLAND
            )
            val p = generated.puzzle
            val hash = BookBoardDigest.sha256(p)
            assertEquals(hash, BookBoardDigest.sha256(repeated.puzzle))
            assertEquals(group.map { it.id }.toSet(), p.targets.map { it.id }.toSet())
            assertEquals(group.map { it.answer }.toSet(),
                p.targets.map { it.word }.toSet())
            for (target in p.targets) {
                assertTrue("No target repeated in 3-page wildlife manuscript",
                    targetSet.add(target.word))
            }
            val page = BookPage(
                "woodland-ws55-ch8-discoveries-qa-page-${pageNum}", p.id,
                WordSearchGeneratorV5.CONTENT_VERSION, scoped.id,
                sizes[index], challenges[index], seed, hash, generated
            )
            pages.add(page)
            boardReport += listOf(
                pageNum.toString(), group.first().title, sizes[index].cells.toString(),
                sizes[index].targets.toString(), challenges[index].name,
                seed.toString(), scoped.id, scoped.sha256, hash,
                p.targets.joinToString(",") { it.word },
                p.rows.joinToString("/"), "UNAPPROVED_QA_RESEARCH_ONLY"
            ).joinToString("\t")
        }
        assertEquals(52, targetSet.size)
        val book = PuzzleBook(
            "woodland-ws55-discoveries-qa-volume",
            "Forest Discoveries Editorial Manuscript", 2,
            WordTheme.WOODLAND,
            listOf(BookChapter("woodland-ws55-discoveries-qa", "Forest Discoveries", pages))
        )
        val catalog = BookCatalog(listOf(book))
        for (page in pages) {
            val regenerated = BookPageSessionRules.puzzle(catalog, book.id, page.id)
            assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, regenerated))
        }


        val before = existingSixChapters().toMutableMap()
        before[6] = read("chapter-recipes/ws54-stream-manuscript.tsv")
            .drop(1).map { it.split('\t')[4] }.toSet()
        val after = before.toMutableMap()
        after[8] = targetSet.toSet()
        fun unique(g: Map<Int, Set<String>>) =
            g.values.flatMap { it }.toSet().size
        fun shared(g: Map<Int, Set<String>>, a: Int, b: Int) =
            g.getValue(a).intersect(g.getValue(b)).size
        fun pairwise(g: Map<Int, Set<String>>): Int {
            val ids = listOf(3,4,5,6,7,8)
            return ids.indices.sumOf { i ->
                (i+1 until ids.size).sumOf { j -> shared(g, ids[i], ids[j]) }
            }
        }
        assertEquals(202, unique(before))
        assertEquals(231, unique(after))
        assertEquals(139, pairwise(before))
        assertEquals(86, pairwise(after))
        val pairs = mutableListOf("chapter\tbaseline_shared\tws55_shared")
        for ((ch, old, revised) in listOf(
            Triple(3,12,4), Triple(4,25,7), Triple(5,0,1),
            Triple(6,10,1), Triple(7,20,1)
        )) {
            assertEquals(old, shared(before, ch, 8))
            assertEquals(revised, shared(after, ch, 8))
            pairs += listOf(ch, old, revised).joinToString("\t")
        }
        val freq = mutableListOf("answer\tchapter_count\tchapter_ids\tprovenance")
        for (word in after.values.flatMap { it }.toSet().sorted()) {
            val chapters = after.keys.sorted().filter { word in after.getValue(it) }
            val provenance = when {
                manuscript.any { it.answer == word &&
                    it.provenance == "ws55_wildlife_signs_candidate_unapproved" } ->
                    "WS55_CANDIDATE_UNAPPROVED"
                read("chapter-recipes/ws54-stream-manuscript.tsv").drop(1)
                    .any { val f = it.split('\t')
                        f[4] == word && f[9] == "ws54_streamlife_candidate_unapproved" } ->
                    "WS54_CANDIDATE_UNAPPROVED"
                read("chapter-recipes/ws51-birdsong-qa-manuscript.tsv").drop(1)
                    .any { val f = it.split('\t')
                        f[2] == word && f[6] == "ws51_editorial_candidate_unapproved" } ->
                    "WS51_CANDIDATE_UNAPPROVED"
                else -> "EXISTING_QA_NOT_RELEASE"
            }
            freq += listOf(word, chapters.size, chapters.joinToString(","), provenance)
                .joinToString("\t")
        }
        output("ws55-discoveries-frozen-pages.tsv", boardReport)
        output("ws55-discoveries-chapter-overlaps.tsv", pairs)
        output("ws55-bookwide-word-frequency.tsv", freq)
        assertEquals(232, freq.size)
    }

    private fun output(filename: String, lines: List<String>) {
        val file = File("build/$filename")
        file.parentFile.mkdirs()
        file.writeText(lines.joinToString("\n") + "\n")
    }
}
