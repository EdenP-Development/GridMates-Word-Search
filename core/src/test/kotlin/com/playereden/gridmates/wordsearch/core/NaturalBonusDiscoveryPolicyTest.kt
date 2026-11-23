package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WS48: the owner wants naturally occurring, real off-list words recognized.
 * This is a policy regression test, not a generator or scoring modification.
 * Placed-target fragments, crossings and free filler paths are all eligible
 * when the PLAYER traces a straight 4–16 letter path in the allowed lexicon.
 */
class NaturalBonusDiscoveryPolicyTest {
    private fun board() = WordSearchPuzzle(
        id = "ws48-natural-only",
        seed = 0L, contentVersion = 1,
        theme = WordTheme.WOODLAND,
        difficulty = WordDifficulty.EASY,
        rows = listOf(
            "XXXXXX",
            "XXRXXX",
            "FLOWER",
            "XXSXXX",
            "XXEXXX",
            "DOGSXX"
        ),
        targets = listOf(
            WordTarget("flower-target", "FLOWER",
                (0..5).map { col -> GridPoint(2, col) })
        )
    )

    @Test fun naturallyPresentWordsAreRecognizedWithoutBonusPlacement() {
        val puzzle = board() // Only the listed FLOWER target was placed.
        val lexicon = setOf("FLOWER", "FLOW", "WOLF", "ROSE", "DOGS")
        val paths = listOf(
            Triple("FLOW", GridPoint(2, 0), GridPoint(2, 3)), // target fragment
            Triple("WOLF", GridPoint(2, 3), GridPoint(2, 0)), // reversed fragment
            Triple("ROSE", GridPoint(1, 2), GridPoint(4, 2)), // crosses a target
            Triple("DOGS", GridPoint(5, 0), GridPoint(5, 3))  // unrelated path
        )
        for ((word, start, end) in paths) {
            val result = BonusWordRules.select(puzzle, emptySet(), lexicon, start, end)
            assertEquals("Recognize natural $word", BonusClaimStatus.FOUND, result.status)
            assertEquals(word, result.word)
        }

        val origins = SideWordOriginStudy.classify(puzzle, lexicon)
        assertEquals(SideWordOrigin.TARGET_FRAGMENT_ONLY, origins["WOLF"])
        assertEquals(SideWordOrigin.CROSSING_ONLY, origins["ROSE"])
        assertEquals(SideWordOrigin.FREE_PATH_AVAILABLE, origins["DOGS"])
        assertEquals(4, origins.size)
    }

    @Test fun naturalDiscoveryDoesNotRemoveDictionaryDuplicateOrTargetGuards() {
        val puzzle = board()
        val lexicon = setOf("FLOWER", "FLOW", "WOLF", "ROSE", "DOGS")
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), lexicon,
                GridPoint(2, 0), GridPoint(2, 5)).status) // listed target
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), setOf("FLOWER"),
                GridPoint(5, 0), GridPoint(5, 3)).status) // not approved
        assertEquals(BonusClaimStatus.ALREADY_FOUND,
            BonusWordRules.select(puzzle, setOf("DOGS"), lexicon,
                GridPoint(5, 0), GridPoint(5, 3)).status)
        assertEquals(BonusClaimStatus.FULL,
            BonusWordRules.select(puzzle, (1..12).map { "OTHER$it" }.toSet(), lexicon,
                GridPoint(5, 0), GridPoint(5, 3)).status)

        assertEquals(345, BonusWordRules.points(3, 3, false))
        assertEquals(445, BonusWordRules.points(3, 3, true))
        assertEquals(580, BonusWordRules.points(3, 13, true)) // 12-extra cap
    }
}
