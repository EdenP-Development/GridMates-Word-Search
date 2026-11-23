package com.playereden.gridmates.wordsearch.core

import java.io.File
import org.junit.Assert.*
import org.junit.Test

/** WS66-A research lab: unapproved TEST-ONLY candidates; never an Android pack. */
class Ws66aV5ScratchTrialTest {
    private data class Entry(
        val chapter: String, val theme: String, val topic: String,
        val subtopic: String, val id: String, val answer: String,
        val display: String, val tier: String, val sourcePool: String,
        val reviewStage: String
    )

    private fun fixture(): String = requireNotNull(
        javaClass.classLoader.getResource("ws66a/qa-only-v5-candidate-pools.tsv")
    ).readText()

    private fun entries(): List<Entry> {
        val lines = fixture().trimEnd().lines()
        assertEquals("chapter\ttheme\ttopic\tsubtopic\tword_id\tanswer\tdisplay\ttier\tsource_pool\treview_stage", lines.first())
        return lines.drop(1).map { line ->
            val v = line.split('\t')
            require(v.size == 10) { "Invalid WS66-A source row" }
            Entry(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9])
        }
    }

    private fun qaPack(chapter: String): ReviewedWordPack {
        val selected = entries().filter { it.chapter == chapter }
        require(selected.size >= 24) { "Need a realistic V5 test pack" }
        require(selected.map { it.id }.distinct().size == selected.size)
        require(selected.map { it.answer }.distinct().size == selected.size)
        val topic = selected.first().topic
        val theme = selected.first().theme.lowercase(java.util.Locale.ROOT)
        require(selected.all { it.topic == topic && it.theme.lowercase(java.util.Locale.ROOT) == theme })
        val id = "en-ws66a-" + theme + "-v1"
        val sourceSha = ReviewedWordPackCodec.sha256(fixture())
        val header = listOf("GRIDMATES_WORD_PACK_V1", id, sourceSha,
            "WS66A Scratch - " + chapter, topic)
        val words = selected.map { row ->
            listOf(row.id, row.answer, row.display,
                topic + ":" + row.subtopic + ":" + row.tier).joinToString("\t")
        }
        val body = (header + words).joinToString("\n")
        return ReviewedWordPackCodec.decode(
            body + "\nSHA256\t" + ReviewedWordPackCodec.sha256(body) + "\n"
        )
    }

    private data class Case(
        val chapter: String, val theme: WordTheme,
        val board: PuzzleBoardSize, val challenge: PuzzleChallenge
    )

    @Test fun realV5ScratchGridsAreDeterministicAndSavedSnapshotsCanRestore() {
        val cases = listOf(
            Case("Flower Borders", WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED),
            Case("Our Solar System", WordTheme.SPACE, PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC),
            Case("Quiet Clearings", WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT),
        )
        val seeds = listOf(1L, 42L, 20261123L)
        val report = mutableListOf("stage\tchapter\tboard\tchallenge\tseed\tstatus\tsha256\tanswers\tfailure")
        var allSuccesses = 0
        for (config in cases) {
            val pack = qaPack(config.chapter)
            val answerSets = mutableListOf<Set<String>>()
            var successful = 0
            for (seed in seeds) {
                val attempt = runCatching {
                    WordSearchGeneratorV5.generate(pack, config.board, config.challenge, seed, config.theme)
                }
                if (attempt.isFailure) {
                    report += listOf("WS66A", config.chapter, config.board.name, config.challenge.name,
                        seed.toString(), "FAIL", "", "",
                        attempt.exceptionOrNull()?.javaClass?.simpleName ?: "UNKNOWN").joinToString("\t")
                    continue
                }
                val exact = attempt.getOrThrow()
                successful++
                allSuccesses++
                val puzzle = exact.puzzle
                assertEquals(config.board.targets, puzzle.targets.size)
                assertEquals(config.board.cells, puzzle.rows.size)
                assertEquals(config.theme, puzzle.theme)
                assertEquals(exact, WordSearchGeneratorV5.generate(
                    pack, config.board, config.challenge, seed, config.theme))
                assertEquals(exact, FrozenWordSearchCodec.decode(FrozenWordSearchCodec.encode(exact)))
                val found = mutableSetOf<String>()
                for (target in puzzle.targets) {
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                        puzzle, found, target.positions.first(), target.positions.last()).status)
                    found += target.id
                }
                assertTrue(WordSearchRules.complete(puzzle, found))
                val digest = BookBoardDigest.sha256(puzzle)
                assertEquals(64, digest.length)
                answerSets += puzzle.targets.map { it.word }.toSet()
                report += listOf("WS66A", config.chapter, config.board.name, config.challenge.name,
                    seed.toString(), "PASS", digest,
                    puzzle.targets.joinToString(",") { it.word }, "").joinToString("\t")
            }
            assertTrue(config.chapter + " could not generate a real V5 board", successful > 0)
            val overlap = answerSets.zipWithNext().map { (a,b) -> (a intersect b).size }.average()
            println("WS66A_V5_TRIAL " + config.chapter + ": " + successful + "/" +
                seeds.size + " generated, distinctAnswers=" +
                answerSets.flatten().toSet().size + ", adjacentReuse=" + overlap)
        }
        assertTrue(allSuccesses > 0)
        File("build/ws66a-v5-trial-evidence.tsv").apply {
            parentFile.mkdirs()
            writeText(report.joinToString("\n") + "\n")
        }
        println("WS66A_V5_REAL_GRIDS_OK: " + allSuccesses +
            " valid QA-only boards, 0 Android activation")
    }

    @Test fun fixtureDoesNotClaimReleaseStatusAndExposesSolarShortage() {
        val all = entries()
        assertEquals(89, all.size)
        assertEquals(mapOf("Flower Borders" to 30, "Our Solar System" to 33,
            "Quiet Clearings" to 26), all.groupingBy { it.chapter }.eachCount())
        assertTrue(all.all { it.reviewStage in setOf("WS65F", "WS65G") })
        assertTrue(all.all { it.sourcePool in setOf("WS64", "WS65") })
        val solarFamiliar = all.count {
            it.chapter == "Our Solar System" && it.tier == "familiar"
        }
        assertEquals(9, solarFamiliar)
        assertTrue(solarFamiliar < PuzzleBoardSize.BOOK_SHORT.targets)
    }
}
