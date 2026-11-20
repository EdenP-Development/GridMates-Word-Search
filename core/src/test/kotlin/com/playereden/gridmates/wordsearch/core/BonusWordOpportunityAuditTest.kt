package com.playereden.gridmates.wordsearch.core

import java.io.File
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WS46: count physical opportunities for genuine curated side words.
 *
 * Research/QA only: this test DOES NOT alter any board, word pack, runtime
 * threshold, saved puzzle ID or player score. It does not measure whether a
 * person will notice a given word and is NOT a real human-play result.
 */
class BonusWordOpportunityAuditTest {
    private val inAppFallback = WordTheme.entries.flatMap { it.words }
        .filter { it.length in BonusWordRules.MIN_LENGTH..BonusWordRules.MAX_LENGTH }
        .toSet()

    private val reviewedPack by lazy {
        val tsv = requireNotNull(javaClass.classLoader
            .getResource("reviewed-packs/en-cozy-mixed-v1.tsv")).readText()
        ReviewedWordPackCodec.decode(tsv)
    }

    private val qaDictionary by lazy {
        reviewedPack.words.map { it.answer }.toSet() + inAppFallback
    }

    /**
     * Scan precisely the eight straight-line directions accepted by the
     * existing side-word rule. Unique words, not duplicate occurrences or
     * reverse paths, earn points.
     */
    private fun eligibleSideWords(
        puzzle: WordSearchPuzzle,
        dictionary: Set<String>
    ): Set<String> {
        val accepted = linkedSetOf<String>()
        val targets = puzzle.targets.map { it.word }.toHashSet()
        for (row in 0 until puzzle.size) for (col in 0 until puzzle.size) {
            for (dr in -1..1) for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val path = StringBuilder()
                for (length in 1..minOf(BonusWordRules.MAX_LENGTH, puzzle.size)) {
                    val y = row + dr * (length - 1)
                    val x = col + dc * (length - 1)
                    if (y !in 0 until puzzle.size || x !in 0 until puzzle.size) break
                    path.append(puzzle.rows[y][x])
                    if (length < BonusWordRules.MIN_LENGTH) continue
                    val word = path.toString()
                    if (word !in dictionary || word in targets) continue
                    val start = GridPoint(row, col)
                    val end = GridPoint(y, x)
                    assertEquals("Side word must match actual WS44 validator: $word",
                        BonusClaimStatus.FOUND,
                        BonusWordRules.select(puzzle, emptySet(), dictionary,
                            start, end).status)
                    accepted += word
                }
            }
        }
        return accepted
    }

    private data class Trial(
        val group: String,
        val qa: Set<String>,
        val fallback: Set<String>,
        val listedTargetCount: Int
    )

    private fun trial(group: String, puzzle: WordSearchPuzzle): Trial {
        val qa = eligibleSideWords(puzzle, qaDictionary)
        val fallback = qa.intersect(inAppFallback)
        // Also independently scan the release-safe fallback lexicon.
        assertEquals(fallback, eligibleSideWords(puzzle, inAppFallback))
        assertTrue("An off-list word cannot also be a listed target",
            qa.none { candidate -> puzzle.targets.any { it.word == candidate } })
        assertTrue("Unlisted word must be 4–16 A–Z letters",
            qa.all { it.length in 4..16 && it.all { c -> c in 'A'..'Z' } })
        return Trial(group, qa, fallback, puzzle.targets.size)
    }

    private fun report(
        rows: List<Trial>,
        origin: String,
        include: (Trial) -> Set<String>
    ): String {
        val counts = rows.map { include(it).size }.sorted()
        val total = counts.size
        val all = rows.flatMap { include(it) }
        val frequency = rows.flatMap { include(it) }.groupingBy { it }.eachCount()
        val top = frequency.entries.sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }
            .thenBy { it.key }).take(7).joinToString(",") { "${it.key}:${it.value}" }
        val zero = counts.count { it == 0 }
        val oneToThree = counts.count { it in 1..3 }
        val fourToEleven = counts.count { it in 4..11 }
        val twelveOrMore = counts.count { it >= BonusWordRules.MAX_SIDE_WORDS }
        val mean = counts.average()
        val p90 = counts[((total - 1) * 90) / 100]
        val median = counts[(total - 1) / 2]
        val fourLetterShare = if (all.isEmpty()) 0.0 else
            100.0 * all.count { it.length == 4 } / all.size
        val offeredPoints = counts.map { BonusWordRules.points(0,it,false) }.average()
        fun percent(n: Int): String = "%.1f".format(Locale.ROOT, 100.0 * n / total)
        fun fmt(n: Double): String = "%.2f".format(Locale.ROOT, n)
        return listOf(
            rows.first().group, origin, total, rows.first().listedTargetCount,
            percent(zero), percent(oneToThree), percent(fourToEleven),
            percent(twelveOrMore), fmt(mean), counts.first(), median, p90,
            counts.last(), fmt(fourLetterShare), fmt(offeredPoints),
            frequency.size, top
        ).joinToString("\t")
    }

    @Test fun exactScannerAgreesWithOriginalSideWordContract() {
        val puzzle = WordSearchPuzzle(
            id = "ws46-scanner-check", seed = 0L, contentVersion = 1,
            theme = WordTheme.WOODLAND, difficulty = WordDifficulty.EASY,
            rows = listOf("CATSXY", "DOGSAB", "TREECD", "SNOWEF", "BIRDGQ", "FROGHI"),
            targets = listOf(WordTarget("listed-cats","CATS",
                (0..3).map { GridPoint(0,it) }))
        )
        val found = eligibleSideWords(puzzle,
            setOf("CATS", "DOGS", "TREE", "BIRD", "FROG", "NOTONBOARD"))
        assertEquals(setOf("DOGS", "TREE", "BIRD", "FROG"), found)
    }

    @Test fun actualFrozenAndLegacyBoardsProduceReproducibleOpportunityReport() {
        val allTrials = mutableListOf<Trial>()
        val pack = reviewedPack
        assertEquals(670, pack.words.size)
        for (size in PuzzleBoardSize.bookPresets)
            for (challenge in PuzzleChallenge.entries)
                repeat(24) { seed ->
                    val board = WordSearchGeneratorV5.generate(
                        pack, size, challenge, seed.toLong()).puzzle
                    allTrials += trial("v5_${size.name}_${challenge.name}", board)
                }
        for (theme in WordTheme.entries) {
            repeat(24) { seed ->
                val board = WordSearchGenerator.generate(
                    theme, WordDifficulty.EASY, seed.toLong())
                allTrials += trial("daily_v1_8x8_${theme.name}", board)
            }
            repeat(12) { seed ->
                val board = WordSearchGeneratorV2.generate(
                    theme, PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC,
                    seed.toLong())
                allTrials += trial("solo_v2_15x15_${theme.name}", board)
            }
            repeat(5) { seed ->
                val board = WordSearchGeneratorV3.generate(
                    theme, PuzzleBoardSize.BOOK_GRAND, PuzzleChallenge.CLASSIC,
                    seed.toLong())
                allTrials += trial("solo_v3_25x25_${theme.name}", board)
            }
        }
        assertEquals(339, allTrials.size)
        val header = listOf("group","lexicon","boards","targets",
            "pct_zero","pct_1to3","pct_4to11","pct_12plus","mean",
            "min","median","p90","max","pct_4letter",
            "potential_bonus_points_mean_capped","distinct_words_seen",
            "most_frequent_words").joinToString("\t")
        val lines = mutableListOf(header)
        for ((_, items) in allTrials.groupBy { it.group }.toSortedMap()) {
            lines += report(items, "qa_670_plus_builtins") { it.qa }
            lines += report(items, "builtins_only_release_fallback") { it.fallback }
        }
        val location = File("build/ws46-bonus-opportunities.tsv")
        location.parentFile.mkdirs()
        location.writeText(lines.joinToString("\n") + "\n")
        println("WS46 BONUS OPPORTUNITY AUDIT: ${allTrials.size} deterministic, real source-backed boards; TSV=$location")
        lines.drop(1).forEach { println("WS46 ${it}") }
        assertEquals(1 + allTrials.groupBy { it.group }.size * 2, lines.size)
    }
}
