package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS55 source-only chapter/editorial lab. This test may build new frozen
 * candidate pages, but MUST NEVER alter production or old QA Library pages,
 * game saves, V5, released vocab, Daily, bonus policy or physical APK.
 */
class QuietClearingsBookwideLabTest {
    private data class Term(
        val chapter: Int, val chapterTitle: String, val page: Int,
        val pageTitle: String, val answer: String, val id: String,
        val display: String, val tier: String, val subtopic: String,
        val provenance: String
    )
    private fun resource(name: String): List<String> =
        requireNotNull(javaClass.classLoader.getResource(name)).readText()
            .trimEnd().lines()

    private fun manuscript(): List<Term> {
        val lines = resource("chapter-recipes/ws56-clearings-manuscript.tsv")
        assertEquals(
            "chapter\tchapter_title\tpage\tpage_title\tanswer\tword_id" +
                "\tdisplay\ttier\tsubtopic\tprovenance", lines.first()
        )
        return lines.drop(1).map { row ->
            val f = row.split('\t')
            require(f.size == 10)
            Term(f[0].toInt(), f[1], f[2].toInt(), f[3], f[4],
                f[5], f[6], f[7], f[8], f[9])
        }
    }
    private fun source(): ReviewedWordPack = ReviewedWordPackCodec.decode(
        resource("reviewed-packs/en-woodland-v1.tsv").joinToString("\n")
    )
    private fun authoredChaptersBefore(): Map<Int, Set<String>> {
        val ws52 = resource("chapter-recipes/ws52-woodland-manuscript.tsv")
            .drop(1).map { it.split('\t') }
        val groups = ws52.groupBy({ it[0].toInt() }, { it[6] })
            .mapValues { (_, v) -> v.toSet() }.toMutableMap()
        groups[5] = resource("chapter-recipes/ws51-birdsong-qa-manuscript.tsv")
            .drop(1).map { it.split('\t')[2] }.toSet()
        groups[7] = resource("chapter-recipes/ws53-quiet-clearings-revision.tsv")
            .drop(1).map { it.split('\t')[6] }.toSet()
        groups[6] = resource("chapter-recipes/ws54-stream-manuscript.tsv")
            .drop(1).map { it.split('\t')[4] }.toSet()
        assertEquals(setOf(3,4,5,6,7,8), groups.keys)
        assertTrue(groups.values.all { it.size == 52 })
        return groups
    }
    private fun pagePack(list: List<Term>, original: ReviewedWordPack): ReviewedWordPack {
        val page = list.first().page
        val canonical = buildString {
            append("GRIDMATES_WS56_QUIET_CLEARINGS_QA_V1\n")
            append(original.sha256).append('\n')
            append("WOODLAND:7:").append(page).append('\n')
            list.forEach { term ->
                append(term.id).append(':').append(term.answer).append(':')
                append(term.display).append(':').append(term.tier).append(':')
                append(term.subtopic).append(':').append(term.provenance)
                append('\n')
            }
        }
        return ReviewedWordPack(
            "en-woodland-ws56-clearings-p${page}-qa-v1",
            original.sha256, ReviewedWordPackCodec.sha256(canonical),
            list.first().pageTitle + " (unapproved editorial QA)",
            listOf("woodland"),
            list.map { term ->
                ReviewedWord(term.id, term.answer, term.display,
                    listOf(WordTopic("woodland", term.subtopic, term.tier)))
            }
        )
    }
    @Test fun sourceWordsAndUnapprovedWildlifeSuggestionsAreExplicit() {
        val words = manuscript()
        val original = source().words.associateBy { it.id }
        assertEquals(159, original.size)
        assertEquals(52, words.size)
        assertEquals(52, words.map { it.id }.toSet().size)
        assertEquals(52, words.map { it.answer }.toSet().size)
        assertTrue(words.all { it.chapter == 7 &&
            it.chapterTitle == "Quiet Clearings" &&
            it.answer.matches(Regex("[A-Z]{4,16}")) &&
            it.display.uppercase(Locale.ROOT).filter { c -> c in 'A'..'Z' } == it.answer
        })
        val reused = words.filter { it.provenance == "existing_woodland_QA_not_release" }
        val proposals = words.filter {
            it.provenance == "ws56_clearings_candidate_unapproved"
        }
        assertEquals(16, reused.size)
        assertEquals(36, proposals.size)
        for (word in reused) {
            val from = requireNotNull(original[word.id])
            assertEquals(from.answer, word.answer)
            assertEquals(from.display, word.display)
            assertTrue(from.topics.any { it.topicId == "woodland" &&
                it.subtopic == word.subtopic && it.tier == word.tier })
        }
        assertTrue(proposals.all { it.id.startsWith("ws56q") &&
            it.id !in original && it.subtopic == "wildlife-signs" })
        assertTrue("No candidate words were silently imported into the base QA pack",
            original.keys.none { it.startsWith("ws56q") })
        val means = (1..3).map { page ->
            val list = words.filter { it.page == page }
            assertEquals(listOf(12,16,24)[page-1], list.size)
            assertTrue(list.all { it.answer.length in 4..listOf(7,10,15)[page-1] })
            if (page == 1) assertTrue(list.all { it.tier == "familiar" })
            for (a in list) for (b in list) if (a !== b) {
                assertFalse("V5 must keep every authored target",
                    a.answer.contains(b.answer) || b.answer.contains(a.answer))
            }
            list.map { it.answer.length }.average()
        }
        assertTrue(means[0] < means[1] && means[1] < means[2])
        assertEquals(listOf("Open Glade","Sunlit Spaces",
            "Woodland Stillness"), (1..3).map { page ->
            words.first { it.page == page }.pageTitle
        })
    }

    @Test fun frozenV5PagesAndSixChapterOverlapAreVerifiable() {
        val source = source()
        val terms = manuscript()
        val challenges = listOf(PuzzleChallenge.RELAXED,
            PuzzleChallenge.CLASSIC, PuzzleChallenge.EXPERT)
        val boards = PuzzleBoardSize.bookPresets
        val pages = mutableListOf<BookPage>()
        val found = mutableSetOf<String>()
        val boardReport = mutableListOf(
            "page\tpage_title\tboard\ttargets\tchallenge\tseed" +
                "\tpack_id\tpack_sha256\tboard_sha256\ttarget_words" +
                "\tgrid_rows\tapproval"
        )
        for (i in 0..2) {
            val page = i+1
            val group = terms.filter { it.page == page }
            val scoped = pagePack(group, source)
            val seed = 556_070L + page * 47L
            val original = WordSearchGeneratorV5.generate(
                scoped, boards[i], challenges[i], seed, WordTheme.WOODLAND
            )
            val repeated = WordSearchGeneratorV5.generate(
                scoped, boards[i], challenges[i], seed, WordTheme.WOODLAND
            )
            val puzzle = original.puzzle
            val hash = BookBoardDigest.sha256(puzzle)
            assertEquals(hash, BookBoardDigest.sha256(repeated.puzzle))
            assertEquals(group.map { it.id }.toSet(), puzzle.targets.map { it.id }.toSet())
            assertEquals(group.map { it.answer }.toSet(),
                puzzle.targets.map { it.word }.toSet())
            puzzle.targets.forEach { assertTrue(found.add(it.word)) }
            pages += BookPage("woodland-ws56-ch7-qa-page-${page}",
                puzzle.id, WordSearchGeneratorV5.CONTENT_VERSION, scoped.id,
                boards[i], challenges[i], seed, hash, original)
            boardReport += listOf(page.toString(), group.first().pageTitle,
                boards[i].cells.toString(), boards[i].targets.toString(),
                challenges[i].name, seed.toString(), scoped.id, scoped.sha256,
                hash, puzzle.targets.joinToString(",") { it.word },
                puzzle.rows.joinToString("/"), "UNAPPROVED_QA_RESEARCH_ONLY"
            ).joinToString("\t")
        }
        assertEquals(52, found.size)
        val book = PuzzleBook("woodland-ws56-clearings-qa-book",
            "Quiet Clearings QA manuscript", 2,
            WordTheme.WOODLAND,
            listOf(BookChapter("woodland-ws56-clearings-qa-chapter",
                "Quiet Clearings", pages)))
        val catalog = BookCatalog(listOf(book))
        for (page in pages) {
            val recovered = BookPageSessionRules.puzzle(catalog, book.id, page.id)
            assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, recovered))
        }

        val baseline = authoredChaptersBefore().toMutableMap()
        baseline[8] = resource("chapter-recipes/ws55-wildlife-manuscript.tsv")
            .drop(1).map { it.split('\t')[4] }.toSet()
        val revised = baseline.toMutableMap()
        revised[7] = found
        fun unique(groups: Map<Int, Set<String>>) =
            groups.values.flatMap { it }.toSet().size
        fun pairwise(groups: Map<Int, Set<String>>): Int {
            val ids = listOf(3,4,5,6,7,8)
            var total = 0
            for (i in ids.indices) for (j in (i+1) until ids.size) {
                total += groups.getValue(ids[i])
                    .intersect(groups.getValue(ids[j])).size
            }
            return total
        }
        assertEquals(233, unique(baseline))
        assertEquals(264, unique(revised))
        assertEquals(82, pairwise(baseline))
        assertEquals(49, pairwise(revised))
        val diff = mutableListOf(
            "metric\tcomparison\tbaseline_WS55\tproposed_WS56\tdelta"
        )
        for ((other, expectedBefore, expectedAfter) in listOf(
            Triple(3,18,14), Triple(4,26,0), Triple(5,1,0),
            Triple(6,3,1), Triple(8,0,0)
        )) {
            val before = baseline.getValue(7).intersect(baseline.getValue(other)).size
            val after = revised.getValue(7).intersect(revised.getValue(other)).size
            assertEquals(expectedBefore, before)
            assertEquals(expectedAfter, after)
            diff += listOf("pairwise_shared_targets",
                "Quiet Clearings vs Chapter $other",
                before.toString(), after.toString(), (after-before).toString()
            ).joinToString("\t")
        }
        for ((metric, location, old, now) in listOf(
            listOf("six_chapter_unique_targets","six chapters","233","264"),
            listOf("all_chapter_pairwise_intersections","six chapters","82","49")
        )) {
            diff += listOf(metric,location,old,now,
                (now.toInt()-old.toInt()).toString()).joinToString("\t")
        }

        val knownUnapproved = mutableMapOf<String,String>()
        for (line in resource("chapter-recipes/ws51-birdsong-qa-manuscript.tsv").drop(1)) {
            val columns = line.split('\t')
            if (columns[6] == "ws51_editorial_candidate_unapproved") {
                knownUnapproved[columns[2]] = "WS51_UNAPPROVED_birdlife"
            }
        }
        for (line in resource("chapter-recipes/ws54-stream-manuscript.tsv").drop(1)) {
            val columns = line.split('\t')
            if (columns[9] == "ws54_streamlife_candidate_unapproved") {
                knownUnapproved[columns[4]] = "WS54_UNAPPROVED_streamlife"
            }
        }
        for (line in resource("chapter-recipes/ws55-wildlife-manuscript.tsv").drop(1)) {
            val fields = line.split('\t')
            if (fields[9] == "ws55_wildlife_candidate_unapproved") {
                knownUnapproved[fields[4]] = "WS55_UNAPPROVED_wildlife"
            }
        }
        for (entry in terms) if (entry.provenance == "ws56_clearings_candidate_unapproved") {
            knownUnapproved[entry.answer] = "WS56_UNAPPROVED_clearings"
        }
        val occurrences = revised.values.flatMap { it }.groupingBy { it }.eachCount()
        val freq = mutableListOf("answer\tchapter_count\tchapter_ids\tprovenance")
        for (word in occurrences.keys.sortedWith(
            compareByDescending<String> { occurrences.getValue(it) }.thenBy { it }
        )) {
            val chapters = revised.keys.sorted().filter { word in revised.getValue(it) }
            freq += listOf(word,chapters.size.toString(),
                chapters.joinToString(","),
                knownUnapproved[word] ?: "existing_Woodland_QA_NOT_released"
            ).joinToString("\t")
        }
        assertEquals(265, freq.size)
        assertEquals(122, knownUnapproved.size)
        val frequencyUnapproved = freq.drop(1)
            .count { it.split('\t')[3].contains("_UNAPPROVED_") }
        assertEquals(122, frequencyUnapproved)
        fun write(filename: String, lines: List<String>) {
            val f = File("build/$filename")
            f.parentFile.mkdirs()
            f.writeText(lines.joinToString("\n") + "\n")
        }
        write("ws56-clearings-frozen-pages.tsv",boardReport)
        write("ws56-bookwide-overlap-delta.tsv",diff)
        write("ws56-bookwide-word-frequency.tsv",freq)
        assertEquals(4,boardReport.size)
        println("WS56 CLEARINGS: 3 exact V5 QA boards, 52 targets; " +
            "233->264 unique answers, 82->49 pairwise intersections; " +
            "122 candidate words still unapproved")
    }
}
