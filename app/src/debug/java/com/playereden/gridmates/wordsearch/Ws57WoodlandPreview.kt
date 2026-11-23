package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.FrozenWordSearch
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.ReviewedWord
import com.playereden.gridmates.wordsearch.core.ReviewedWordPack
import com.playereden.gridmates.wordsearch.core.ReviewedWordPackCodec
import com.playereden.gridmates.wordsearch.core.WordSearchGeneratorV5
import com.playereden.gridmates.wordsearch.core.WordTheme
import com.playereden.gridmates.wordsearch.core.WordTopic

/**
 * WS57 — DEBUG source set ONLY. An explicitly new, standalone six-chapter
 * playable review volume with 18 source-pinned frozen QA pages.
 *
 * Do not mutate the pre-existing 63 QA pages or any previously saved IDs.
 * The asset data contains the 312 exact, chosen manuscript entries; 122
 * candidate terms have NOT received editorial release authorization.
 *
 * The original V5 QA board digests are pinned in assets. If V5 behavior,
 * word IDs, seeds or pack hashes ever drift, fail closed instead of secretly
 * regenerating a different saved page behind an old ID.
 */
internal object Ws57WoodlandPreview {
    const val BOOK_ID = "woodland-ws57-qa-volume-two"
    private const val ORIGINAL_WOODLAND_PACK_SHA =
        "65393cb62e9da1d4a71fcaef72a2d3ff109e4e06ab727530db997f26d002f8a8"

    private data class Term(
        val chapter: Int, val chapterTitle: String, val page: Int,
        val title: String, val answer: String, val id: String,
        val display: String, val tier: String, val subtopic: String,
        val provenance: String
    )

    private fun readAsset(context: Context, path: String): List<String> =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { reader ->
            reader.readText().trimEnd().lines()
        }

    private fun manuscript(context: Context): List<Term> {
        val lines = readAsset(context, "ws57_woodland_manifest.tsv")
        require(lines.first() ==
            "chapter\tchapter_title\tpage\tpage_title\tanswer\tword_id" +
            "\tdisplay\ttier\tsubtopic\tprovenance") {
            "WS57 frozen manuscript schema mismatch"
        }
        val terms = lines.drop(1).map { line ->
            val f = line.split('\t')
            require(f.size == 10) { "WS57 manuscript field count drift" }
            Term(f[0].toInt(), f[1], f[2].toInt(), f[3], f[4],
                f[5], f[6], f[7], f[8], f[9])
        }
        require(terms.size == 312 && terms.map { it.answer }.toSet().size == 264) {
            "WS57 whole-book authored vocabulary changed"
        }
        require(terms.count { it.provenance.contains("candidate_unapproved") } == 122) {
            "WS57 unapproved word count changed; editor re-review required"
        }
        return terms
    }

    private fun expectedHashes(context: Context): Map<Pair<Int, Int>, String> {
        val lines = readAsset(context, "ws57_woodland_expected_boards.tsv")
        require(lines.first() == "chapter\tpage\texpected_board_sha256")
        return lines.drop(1).associate { line ->
            val f = line.split('\t')
            require(f.size == 3 && f[2].matches(Regex("[0-9a-f]{64}")))
            (f[0].toInt() to f[1].toInt()) to f[2]
        }.also { require(it.size == 18) }
    }

    private fun scopedPack(group: List<Term>, chapter: Int, page: Int): ReviewedWordPack {
        val title = group.first().title
        val provenance = buildString {
            when (chapter) {
                3, 4 -> {
                    append("GRIDMATES_WS52_WOODLAND_FROZEN_CANDIDATE_V1\n")
                    append(ORIGINAL_WOODLAND_PACK_SHA).append('\n')
                    append(chapter).append(':').append(page).append('\n')
                    group.forEach { term ->
                        append(term.id).append(':').append(term.answer).append(':')
                        append(term.display).append(':').append(term.subtopic)
                        append(':').append(term.tier).append('\n')
                    }
                }
                5 -> {
                    append("GRIDMATES_BIRDSONG_WS51_SOURCE_ONLY_V2\n")
                    append(ORIGINAL_WOODLAND_PACK_SHA).append('\n')
                    append(page).append(':').append(title).append('\n')
                    group.forEach { term ->
                        append(term.id).append(':').append(term.answer).append(':')
                        append(term.display).append(':').append(term.tier)
                        append(':').append(term.provenance).append('\n')
                    }
                }
                6, 7, 8 -> {
                    append(when (chapter) {
                        6 -> "GRIDMATES_WS54_STREAM_QA_ONLY\n"
                        7 -> "GRIDMATES_WS56_QUIET_CLEARINGS_QA_V1\n"
                        else -> "GRIDMATES_WS55_FOREST_WILDLIFE_QA_V1\n"
                    })
                    append(ORIGINAL_WOODLAND_PACK_SHA).append('\n')
                    append("WOODLAND:").append(chapter).append(':').append(page)
                        .append('\n')
                    group.forEach { term ->
                        append(term.id).append(':').append(term.answer).append(':')
                        append(term.display).append(':').append(term.tier).append(':')
                        append(term.subtopic).append(':').append(term.provenance)
                        append('\n')
                    }
                }
                else -> error("Unknown WS57 chapter")
            }
        }
        val packId = when (chapter) {
            3, 4 -> "en-woodland-ws52-ch${chapter}-p${page}-qa-v1"
            5 -> "en-woodland-birdsong-p${page}-qa-v2"
            6 -> "en-woodland-ws54-stream-p${page}-qa-v1"
            7 -> "en-woodland-ws56-clearings-p${page}-qa-v1"
            8 -> "en-woodland-ws55-discoveries-p${page}-qa-v1"
            else -> error("Unknown chapter")
        }
        return ReviewedWordPack(
            packId, ORIGINAL_WOODLAND_PACK_SHA,
            ReviewedWordPackCodec.sha256(provenance),
            title + " (editorial QA only)", listOf("woodland"),
            group.map { term ->
                ReviewedWord(term.id, term.answer, term.display,
                    listOf(WordTopic("woodland", term.subtopic, term.tier)))
            }
        )
    }

    /** Hash-pinned source fixtures ensure a previous saved board never changes. */
    fun catalog(context: Context): BookCatalog {
        val terms = manuscript(context)
        val pins = expectedHashes(context)
        val sizes = PuzzleBoardSize.bookPresets
        val challenges = listOf(
            PuzzleChallenge.RELAXED,
            PuzzleChallenge.CLASSIC,
            PuzzleChallenge.EXPERT
        )
        val chapters = (3..8).map { chapter ->
            val rows = terms.filter { it.chapter == chapter }
            require(rows.size == 52 && rows.map { it.answer }.toSet().size == 52)
            val title = rows.first().chapterTitle
            require(rows.all { it.chapterTitle == title })
            val pages = (1..3).map { page ->
                val words = rows.filter { it.page == page }
                val size = sizes[page - 1]
                val challenge = challenges[page - 1]
                require(words.size == size.targets && words.map { it.id }.toSet().size == words.size)
                val seed = when (chapter) {
                    3, 4 -> 520_000L + chapter * 1_000L + page * 41L
                    5 -> listOf(5171L, 5208L, 5245L)[page - 1]
                    6 -> 554_060L + page * 43L
                    7 -> 556_070L + page * 47L
                    else -> 555_080L + page * 47L
                }
                val pack = scopedPack(words, chapter, page)
                val frozen: FrozenWordSearch = WordSearchGeneratorV5.generate(
                    pack, size, challenge, seed, WordTheme.WOODLAND
                )
                val digest = BookBoardDigest.sha256(frozen.puzzle)
                require(digest == pins.getValue(chapter to page)) {
                    "WS57 content mismatch at chapter $chapter, page $page: refusing to rewrite QA board"
                }
                require(frozen.puzzle.targets.map { it.id }.toSet() ==
                    words.map { it.id }.toSet()) {
                    "WS57 board omitted a required authored word"
                }
                BookPage(
                    "ws57-woodland-qa-ch${chapter}-page${page}",
                    frozen.puzzle.id,
                    WordSearchGeneratorV5.CONTENT_VERSION,
                    pack.id,
                    size, challenge, seed, digest, frozen
                )
            }
            BookChapter("ws57-woodland-qa-chapter-${chapter}", title, pages)
        }
        return BookCatalog(listOf(
            PuzzleBook(
                BOOK_ID,
                "Woodland - Editorial QA Preview",
                2,
                WordTheme.WOODLAND,
                chapters
            )
        ))
    }

    fun appendLegacy(context: Context, legacy: BookCatalog): BookCatalog {
        val qa = catalog(context)
        return BookCatalog(legacy.books + qa.books).also {
            legacy.requireCompatibleUpgrade(it)
        }
    }
}
