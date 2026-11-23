package com.playereden.gridmates.wordsearch.core

import java.io.File
import kotlin.math.max
import org.junit.Assert.*
import org.junit.Test

/**
 * WS66-B: real V5 multi-page *test-only* replay variety experiment.
 * No new publication, Android content, old board IDs or save migration.
 *
 * The first three successful seeds are the baseline. We enumerate all
 * chronological 3-page seed choices and choose the one minimizing adjacent
 * repeated answers; then maximize unique coverage as a tie-breaker.
 * All seeds, including generator failures, remain visible in the artifacts.
 */
class Ws66bMultiPageVarietyLabTest {
    private data class SourceWord(
        val chapter: String, val theme: String, val topic: String,
        val subtopic: String, val id: String, val answer: String,
        val display: String, val tier: String
    )
    private data class Scenario(
        val chapter: String, val theme: WordTheme,
        val board: PuzzleBoardSize, val challenge: PuzzleChallenge
    )
    private data class Trial(
        val seed: Long, val frozen: FrozenWordSearch, val hash: String
    ) {
        val words: Set<String> get() = frozen.puzzle.targets.map { it.word }.toSet()
    }
    private val header = "chapter\ttheme\ttopic\tsubtopic\tword_id\tanswer\tdisplay\ttier\tsource_pool\treview_stage"

    private fun input(): List<SourceWord> {
        val text = requireNotNull(javaClass.classLoader.getResource(
            "ws66a/qa-only-v5-candidate-pools.tsv")).readText().trimEnd().lines()
        assertEquals(header, text.first())
        return text.drop(1).map { line ->
            val c = line.split('\t')
            require(c.size == 10 && c[8] in setOf("WS64", "WS65") &&
                c[9] in setOf("WS65F", "WS65G"))
            SourceWord(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7])
        }
    }

    private fun pack(chapter: String, all: List<SourceWord>): ReviewedWordPack {
        val rows = all.filter { it.chapter == chapter }
        require(rows.size >= 24)
        require(rows.map { it.id }.distinct().size == rows.size)
        require(rows.map { it.answer }.distinct().size == rows.size)
        val topic = rows.first().topic
        val theme = rows.first().theme.lowercase(java.util.Locale.ROOT)
        require(rows.all { it.topic == topic && it.theme.lowercase(java.util.Locale.ROOT) == theme })
        val id = "en-ws66b-" + theme + "-v1"
        val sourceHash = ReviewedWordPackCodec.sha256(rows.joinToString("\n") {
            it.id + "\t" + it.answer + "\t" + it.display + "\t" + it.tier
        })
        val fields = listOf(
            "GRIDMATES_WORD_PACK_V1", id, sourceHash, "WS66B Scratch - " + chapter, topic
        ) + rows.map {
            it.id + "\t" + it.answer + "\t" + it.display + "\t" +
                topic + ":" + it.subtopic + ":" + it.tier
        }
        val body = fields.joinToString("\n")
        return ReviewedWordPackCodec.decode(
            body + "\nSHA256\t" + ReviewedWordPackCodec.sha256(body) + "\n"
        )
    }

    private fun overlap(a: Trial, b: Trial) = (a.words intersect b.words).size
    private fun adjacentOverlap(pages: List<Trial>) =
        pages.zipWithNext().sumOf { (a, b) -> overlap(a, b) }
    private fun coverage(pages: List<Trial>) = pages.flatMap { it.words }.toSet().size

    /** Only choose chronological seed lists; never re-label pages behind existing IDs. */
    private fun bestThree(trials: List<Trial>): List<Trial> {
        require(trials.size >= 3)
        var best = trials.take(3)
        var minOverlap = adjacentOverlap(best)
        var maxCoverage = coverage(best)
        for (a in 0 until trials.size - 2)
            for (b in a + 1 until trials.size - 1)
                for (c in b + 1 until trials.size) {
                    val proposed = listOf(trials[a], trials[b], trials[c])
                    val score = adjacentOverlap(proposed)
                    val unique = coverage(proposed)
                    if (score < minOverlap || (score == minOverlap && unique > maxCoverage)) {
                        best = proposed
                        minOverlap = score
                        maxCoverage = unique
                    }
                }
        return best
    }

    private fun checkPlayableAndFrozen(exact: FrozenWordSearch) {
        assertEquals(exact, FrozenWordSearchCodec.decode(FrozenWordSearchCodec.encode(exact)))
        val puzzle = exact.puzzle
        assertEquals(exact.board.targets, puzzle.targets.size)
        val used = mutableSetOf<String>()
        for (target in puzzle.targets) {
            assertEquals(1, WordSearchGeneratorV5.occurrences(puzzle, target.word))
            assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(
                puzzle, used, target.positions.first(), target.positions.last()).status)
            used += target.id
        }
        assertTrue(WordSearchRules.complete(puzzle, used))
    }

    @Test fun multiPageRealV5TrialsReportBothRepetitionAndInsufficientPools() {
        val words = input()
        assertEquals(89, words.size)
        val scenarios = listOf(
            Scenario("Flower Borders", WordTheme.GARDEN, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED),
            Scenario("Flower Borders", WordTheme.GARDEN, PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC),
            Scenario("Flower Borders", WordTheme.GARDEN, PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT),
            Scenario("Our Solar System", WordTheme.SPACE, PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC),
            Scenario("Our Solar System", WordTheme.SPACE, PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT),
            Scenario("Quiet Clearings", WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED),
            Scenario("Quiet Clearings", WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC),
            Scenario("Quiet Clearings", WordTheme.WOODLAND, PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT),
        )
        val trialRows = mutableListOf(
            "chapter\tboard\tchallenge\tseed\tstatus\tsha256\tanswers\tfailure"
        )
        val selectionRows = mutableListOf(
            "chapter\tboard\tchallenge\tpage\tseed\tsha256\tanswers\tadjacent_repeat_from_previous"
        )
        val summaryRows = mutableListOf(
            "chapter\tboard\tchallenge\teligible_pool\tper_board_targets\tunavoidable_pair_overlap_floor" +
                "\tattempts\tsuccesses\tfailures\tbaseline_adjacent_repeats\tselected_adjacent_repeats" +
                "\tbaseline_distinct_answers\tselected_distinct_answers\tselection_status"
        )
        val chosenByChapter = mutableMapOf<String, MutableSet<String>>()
        val packByChapter = scenarios.map { it.chapter }.distinct().associateWith { pack(it, words) }
        var knownWorkingCases = 0
        var trialCount = 0
        var successesTotal = 0
        for ((caseIndex, scenario) in scenarios.withIndex()) {
            val pack = packByChapter.getValue(scenario.chapter)
            val possible = pack.words.count { entry ->
                entry.answer.length < scenario.board.cells &&
                    entry.answer != entry.answer.reversed() &&
                    entry.topics.any {
                        it.topicId == pack.topicIds.first() &&
                            (scenario.challenge == PuzzleChallenge.EXPERT ||
                                it.tier != "advanced" && (scenario.challenge != PuzzleChallenge.RELAXED ||
                                    it.tier == "familiar"))
                    }
            }
            val floor = max(0, 2 * scenario.board.targets - possible)
            val successful = mutableListOf<Trial>()
            for (n in 0 until 12) {
                val seed = 660000L + caseIndex * 1000L + n
                trialCount++
                val attempt = runCatching {
                    WordSearchGeneratorV5.generate(pack, scenario.board, scenario.challenge,
                        seed, scenario.theme)
                }
                if (attempt.isFailure) {
                    trialRows += listOf(
                        scenario.chapter, scenario.board.name, scenario.challenge.name,
                        seed.toString(), "FAIL", "", "",
                        attempt.exceptionOrNull()?.javaClass?.simpleName ?: "UNKNOWN"
                    ).joinToString("\t")
                    continue
                }
                val frozen = attempt.getOrThrow()
                val hash = BookBoardDigest.sha256(frozen.puzzle)
                successful += Trial(seed, frozen, hash)
                successesTotal++
                trialRows += listOf(
                    scenario.chapter, scenario.board.name, scenario.challenge.name,
                    seed.toString(), "PASS", hash,
                    frozen.puzzle.targets.joinToString(",") { it.word }, ""
                ).joinToString("\t")
            }
            var baseline = -1
            var optimized = -1
            var baselineDistinct = -1
            var selectedDistinct = -1
            var state = "NOT_ENOUGH_GENERATED_PAGES"
            if (successful.size >= 3) {
                val first = successful.take(3)
                val chosen = bestThree(successful)
                baseline = adjacentOverlap(first)
                optimized = adjacentOverlap(chosen)
                baselineDistinct = coverage(first)
                selectedDistinct = coverage(chosen)
                assertTrue("Scheduling should never worsen adjacent repetition", optimized <= baseline)
                assertTrue("Selected page coverage should not exceed source inventory",
                    selectedDistinct <= possible)
                assertTrue("Impossible pairwise overlap below mathematical minimum",
                    overlap(chosen[0], chosen[1]) >= floor &&
                        overlap(chosen[1], chosen[2]) >= floor)
                assertEquals(3, chosen.map { it.hash }.distinct().size)
                for ((index, selection) in chosen.withIndex()) {
                    checkPlayableAndFrozen(selection.frozen)
                    val again = WordSearchGeneratorV5.generate(
                        pack, scenario.board, scenario.challenge, selection.seed, scenario.theme)
                    assertEquals(selection.frozen, again)
                    chosenByChapter.getOrPut(scenario.chapter) { mutableSetOf() }
                        .addAll(selection.words)
                    selectionRows += listOf(
                        scenario.chapter, scenario.board.name, scenario.challenge.name,
                        (index + 1).toString(), selection.seed.toString(), selection.hash,
                        selection.frozen.puzzle.targets.joinToString(",") { it.word },
                        (if (index == 0) 0 else overlap(chosen[index - 1], selection)).toString()
                    ).joinToString("\t")
                }
                state = "THREE_VALID_TEST_ONLY_PAGES"
            }
            summaryRows += listOf(
                scenario.chapter, scenario.board.name, scenario.challenge.name,
                possible.toString(), scenario.board.targets.toString(), floor.toString(),
                "12", successful.size.toString(), (12 - successful.size).toString(),
                baseline.toString(), optimized.toString(), baselineDistinct.toString(),
                selectedDistinct.toString(), state
            ).joinToString("\t")
            println("WS66B_CASE " + scenario.chapter + " " + scenario.board.name +
                " " + successful.size + "/12; 3-page adjacent repeats " +
                baseline + " -> " + optimized + "; theoretical floor per pair=" + floor +
                "; status=" + state)
            if ((scenario.chapter == "Flower Borders" &&
                    scenario.board == PuzzleBoardSize.BOOK_SHORT) ||
                (scenario.chapter == "Our Solar System" &&
                    scenario.board == PuzzleBoardSize.BOOK_CLASSIC) ||
                (scenario.chapter == "Quiet Clearings" &&
                    scenario.board == PuzzleBoardSize.BOOK_BIG)) {
                if (successful.size >= 3) knownWorkingCases++
            }
        }
        val path = File("build/ws66b-variety")
        path.mkdirs()
        File(path, "all-seed-trials.tsv").writeText(trialRows.joinToString("\n") + "\n")
        File(path, "three-page-selections.tsv").writeText(selectionRows.joinToString("\n") + "\n")
        File(path, "per-scenario-metrics.tsv").writeText(summaryRows.joinToString("\n") + "\n")
        assertEquals(96, trialCount)
        assertTrue(successesTotal > 0)
        assertEquals("All three previously validated configurations should build 3 pages",
            3, knownWorkingCases)
        println("WS66B_MULTI_PAGE_TRIAL_OK: " + successesTotal +
            "/96 real V5 seed trials; no production pack and no Android content")
    }
}
