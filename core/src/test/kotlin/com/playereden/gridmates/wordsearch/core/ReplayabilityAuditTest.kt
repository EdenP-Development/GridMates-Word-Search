package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

/** Independent checks of target occurrences and selection over varied seeds. */
class ReplayabilityAuditTest {
    @Test fun everySoloSizeAndChallengeHasUniqueFindableTargetsAcrossSeedSweep() {
        val seeds = listOf(0L, 7L, 99L, 471L, 20261117L, 4294967296L, Long.MAX_VALUE - 1)
        var boards = 0
        for (theme in WordTheme.entries) for (size in PuzzleBoardSize.soloPresets)
            for (challenge in PuzzleChallenge.entries) for (seed in seeds) {
                val run = SoloProgressRules.freshForBoard(theme, size, challenge, seed)
                val puzzle = SoloProgressRules.puzzle(run)
                assertEquals("${puzzle.id}", size.targets, puzzle.targets.size)
                assertEquals(puzzle, SoloProgressRules.puzzle(run))
                val found = mutableSetOf<String>()
                puzzle.targets.forEach { target ->
                    assertEquals("Duplicate/missing ${target.word} in ${puzzle.id}",
                        1, countOccurrences(puzzle.rows, target.word))
                    assertEquals(ClaimStatus.FOUND, WordSearchRules.claim(puzzle, found,
                        target.positions.last(), target.positions.first()).status)
                    if (challenge == PuzzleChallenge.RELAXED) {
                        val a = target.positions.first()
                        val b = target.positions.last()
                        assertTrue((a.row == b.row && b.col > a.col) ||
                            (a.col == b.col && b.row > a.row))
                    }
                    found += target.id
                    assertEquals(ClaimStatus.ALREADY_FOUND, WordSearchRules.claim(puzzle, found,
                        target.positions.first(), target.positions.last()).status)
                }
                assertTrue(WordSearchRules.complete(puzzle, found))
                assertEquals(ClaimStatus.INVALID, WordSearchRules.claim(puzzle, emptySet(),
                    GridPoint(-1, 0), GridPoint(0, 0)).status)
                assertEquals(ClaimStatus.INVALID, WordSearchRules.claim(puzzle, emptySet(),
                    GridPoint(0, 0), GridPoint(1, 2)).status)
                boards++
            }
        assertEquals(315, boards)
        println("WS39 replay audit passed: $boards seeded boards, all themes/sizes/challenges")
    }

    @Test fun wordPacksHaveNoUnreachableEntriesAndFreshSeedsProduceDifferentBoards() {
        WordTheme.entries.forEach { theme ->
            val v2Words = mutableSetOf<String>()
            val v3Words = mutableSetOf<String>()
            val layouts = mutableSetOf<List<String>>()
            repeat(256) { seed ->
                for (challenge in PuzzleChallenge.entries) {
                    v2Words += WordLibraryV2.words(theme, PuzzleBoardSize.BOOK_BIG,
                        challenge, seed.toLong())
                    v3Words += WordLibraryV3.words(theme, PuzzleBoardSize.BOOK_GIANT,
                        challenge, seed.toLong(), 0)
                }
                if (seed < 16) layouts += WordSearchGeneratorV2.generate(theme,
                    PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.CLASSIC, seed.toLong()).rows
            }
            assertEquals("Unreachable V2 vocabulary for $theme", WordLibraryV2.count(theme), v2Words.size)
            assertEquals("Unreachable V3 vocabulary for $theme", WordLibraryV3.count(theme), v3Words.size)
            assertTrue((v2Words + v3Words).all { it.all { c -> c in 'A'..'Z' } })
            assertEquals(16, layouts.size)
            println("WS39 $theme: V2=${v2Words.size}, V3=${v3Words.size}, 16/16 distinct layouts")
        }
    }

    private fun countOccurrences(rows: List<String>, word: String): Int {
        var matches = 0
        for (r in rows.indices) for (c in rows.indices)
            for (dr in -1..1) for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                if (word.indices.all { i ->
                    val y = r + dr * i
                    val x = c + dc * i
                    y in rows.indices && x in rows.indices && rows[y][x] == word[i]
                }) matches++
            }
        return matches
    }
}
