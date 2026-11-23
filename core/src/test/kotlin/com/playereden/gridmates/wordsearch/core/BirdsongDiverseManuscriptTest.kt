package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS51: scoped editorial QA candidate only, never changes a playable book.
 * Book pages are generated once from deterministic, page-specific source word
 * sets, then frozen. No generator change or placed bonus words.
 */
class BirdsongDiverseManuscriptTest {
    private data class Term(
        val page: Int, val pageTitle: String, val word: String, val display: String,
        val id: String, val tier: String, val provenance: String
    )

    private fun terms(): List<Term> {
        val rows = requireNotNull(javaClass.classLoader.getResource(
            "chapter-recipes/ws51-birdsong-qa-manuscript.tsv"
        )).readText().trimEnd().lines()
        assertEquals(
            "page\tpage_title\tanswer\tdisplay\tword_id\ttier\tprovenance",
            rows.first()
        )
        return rows.drop(1).map { row ->
            val f = row.split('\t')
            require(f.size == 7)
            Term(f[0].toInt(), f[1], f[2], f[3], f[4], f[5], f[6])
        }
    }

    private fun woodlandSource(): ReviewedWordPack {
        val text = requireNotNull(javaClass.classLoader.getResource(
            "reviewed-packs/en-woodland-v1.tsv"
        )).readText()
        return ReviewedWordPackCodec.decode(text)
    }

    private fun editionPack(
        pageNumber: Int, chapterTerms: List<Term>, woodland: ReviewedWordPack
    ): ReviewedWordPack {
        val title = chapterTerms.first().pageTitle
        val provenance = buildString {
            append("GRIDMATES_BIRDSONG_WS51_SOURCE_ONLY_V2\n")
            append(woodland.sha256).append('\n')
            append(pageNumber).append(':').append(title).append('\n')
            for (term in chapterTerms) {
                append(term.id).append(':').append(term.word)
                append(':').append(term.display).append(':').append(term.tier)
                append(':').append(term.provenance).append('\n')
            }
        }
        val scoped = chapterTerms.map { term ->
            val subtopic = if (term.provenance == "existing_woodland_birds_QA")
                "birds" else "birdlife"
            ReviewedWord(term.id, term.word, term.display, listOf(
                WordTopic("woodland", subtopic, term.tier)
            ))
        }
        return ReviewedWordPack(
            "en-woodland-birdsong-p${pageNumber}-qa-v2",
            woodland.sha256, ReviewedWordPackCodec.sha256(provenance),
            title + " (unapproved QA manuscript)",
            listOf("woodland"), scoped
        )
    }

    @Test fun originalBirdsAndCandidateOriginAreUnchangedAndHonest() {
        val all = terms()
        val woodland = woodlandSource()
        val existingBirds = woodland.words.filter { word -> word.topics.any {
            it.topicId == "woodland" && it.subtopic == "birds"
        } }
        assertEquals(29, existingBirds.size)
        assertEquals(52, all.size)
        assertEquals(52, all.map { it.word }.toSet().size)
        assertEquals(listOf(12,16,24), (1..3).map { n ->
            all.count { it.page == n }
        })
        val existing = all.filter { it.provenance == "existing_woodland_birds_QA" }
        val proposals = all.filter {
            it.provenance == "ws51_editorial_candidate_unapproved"
        }
        assertEquals(29, existing.size)
        assertEquals(23, proposals.size)
        assertEquals(existingBirds.map { it.id }.toSet(), existing.map { it.id }.toSet())
        for (word in existing) {
            assertTrue("Existing bird source must match by stable canonical ID",
                existingBirds.any { it.id == word.id &&
                    it.answer == word.word && it.display == word.display })
        }
        assertTrue(proposals.all { it.id.startsWith("ws51b") &&
            it.word.all { letter -> letter in 'A'..'Z' } &&
            it.word.length in 4..16 })
        assertTrue("Do not pretend new word proposals are release reviewed",
            proposals.none { it.provenance == "existing_woodland_birds_QA" })
        for (page in 1..3) {
            val list = all.filter { it.page == page }.map { it.word }
            assertEquals(list.size, list.toSet().size)
            for (a in list) for (b in list) if (a != b) {
                assertTrue("V5 must not silently drop authored subword: $a/$b",
                    !a.contains(b) && !b.contains(a))
            }
        }
        val mean = (1..3).map { page ->
            all.filter { it.page == page }.map { it.word.length }.average()
        }
        assertTrue("Page lengths should progress from familiar to challenging",
            mean[0] < mean[1] && mean[1] < mean[2])
        assertTrue(all.filter { it.page == 1 }.all { it.tier == "familiar" &&
            it.word.length <= 7 })
    }

    @Test fun threeAuthoredPageListsProduceReproducibleFrozenBoards() {
        val catalogWords = terms()
        val woodland = woodlandSource()
        val sizes = PuzzleBoardSize.bookPresets
        val challenges = listOf(PuzzleChallenge.RELAXED, PuzzleChallenge.CLASSIC,
            PuzzleChallenge.EXPERT)
        val seeds = listOf(5171L, 5208L, 5245L)
        val records = mutableListOf<Pair<BookPage,String>>()
        val boardRows = mutableListOf(
            "page\tpage_title\tboard\ttargets\tchallenge\tseed\tpack_id" +
                "\tpack_sha256\tboard_sha256\twords\tgrid_rows\tapproval"
        )
        val selectedWords = mutableSetOf<String>()
        for (index in 0..2) {
            val page = index + 1
            val assigned = catalogWords.filter { it.page == page }
            val pack = editionPack(page, assigned, woodland)
            val board = sizes[index]
            val challenge = challenges[index]
            val frozen = WordSearchGeneratorV5.generate(
                pack, board, challenge, seeds[index], WordTheme.WOODLAND
            )
            val again = WordSearchGeneratorV5.generate(
                pack, board, challenge, seeds[index], WordTheme.WOODLAND
            )
            val p = frozen.puzzle
            val digest = BookBoardDigest.sha256(p)
            assertEquals(digest, BookBoardDigest.sha256(again.puzzle))
            assertEquals(assigned.map { it.word }.toSet(),
                p.targets.map { it.word }.toSet())
            assertEquals(assigned.map { it.id }.toSet(),
                p.targets.map { it.id }.toSet())
            assertEquals(board.targets, p.targets.size)
            for (target in p.targets) {
                assertTrue("Every target must be unique across all three pages",
                    selectedWords.add(target.word))
            }
            val bookPage = BookPage(
                "woodland-ws51-qa-birdsong-v2-page-$page", p.id,
                WordSearchGeneratorV5.CONTENT_VERSION, pack.id, board, challenge,
                seeds[index], digest, frozen
            )
            records += bookPage to assigned.first().pageTitle
            boardRows += listOf(
                page.toString(), assigned.first().pageTitle, board.cells.toString(),
                board.targets.toString(), challenge.name, seeds[index].toString(),
                pack.id, pack.sha256, digest,
                p.targets.joinToString(",") { it.word },
                p.rows.joinToString("/"), "QA_editorial_candidate_NOT_RELEASED"
            ).joinToString("\t")
        }
        assertEquals(52, selectedWords.size)
        val chapter = BookChapter(
            "woodland-ws51-qa-birdsong-v2-chapter", "Birdsong",
            records.map { it.first }
        )
        val book = PuzzleBook(
            "woodland-ws51-qa-manuscript-book", "Woodland QA chapter lab", 1,
            WordTheme.WOODLAND, listOf(chapter)
        )
        val catalog = BookCatalog(listOf(book))
        for ((page, _) in records) {
            val reconstructed = BookPageSessionRules.puzzle(catalog, book.id, page.id)
            assertTrue(catalog.matchesPublishedPuzzle(book.id, page.id, reconstructed))
        }
        val output = File("build/ws51-birdsong-diverse-frozen-pages.tsv")
        output.parentFile.mkdirs()
        output.writeText(boardRows.joinToString("\n") + "\n")
        println("WS51 BIRDSONG: 3 source-authored QA boards, 52 distinct targets, " +
            "ZERO cross-page word repeats; TSV=$output")
        assertEquals(4, boardRows.size)
    }
}
