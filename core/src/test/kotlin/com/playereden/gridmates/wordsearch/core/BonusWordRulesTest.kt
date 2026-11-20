package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

class BonusWordRulesTest {
    private val puzzle = WordSearchPuzzle(
        id = "bonus-example", seed = 1L, contentVersion = 1,
        theme = WordTheme.WOODLAND, difficulty = WordDifficulty.EASY,
        rows = listOf("CATSXY", "DOGSAB", "TREECD", "SNOWEF", "BIRDGQ", "FROGHI"),
        targets = listOf(WordTarget("target-cats", "CATS", listOf(
            GridPoint(0, 0), GridPoint(0, 1), GridPoint(0, 2), GridPoint(0, 3)
        )))
    )
    private val dictionary = setOf("CATS", "DOGS", "TREE", "BIRD", "FROG", "NOTONBOARD")

    @Test fun wordMustBeOnExactStraightLineAndInCuratedList() {
        assertEquals(BonusWordClaim(BonusClaimStatus.FOUND, "DOGS"),
            BonusWordRules.select(puzzle, emptySet(), dictionary,
                GridPoint(1, 0), GridPoint(1, 3)))
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), dictionary,
                GridPoint(1, 0), GridPoint(2, 3)).status)
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), dictionary,
                GridPoint(0, 0), GridPoint(0, 3)).status) // listed target
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), dictionary,
                GridPoint(1, 3), GridPoint(1, 0)).status) // "SGOD" not in lexicon
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), setOf("NOTONBOARD"),
                GridPoint(1, 0), GridPoint(1, 3)).status)
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), dictionary,
                GridPoint(-1, 0), GridPoint(-1, 3)).status)
        assertEquals(BonusClaimStatus.INVALID,
            BonusWordRules.select(puzzle, emptySet(), dictionary,
                GridPoint(1, 0), GridPoint(1, 2)).status) // 3 letters is too short
    }

    @Test fun oncePerWordAndCappedWithoutAffectingTargetProgress() {
        assertEquals(BonusClaimStatus.ALREADY_FOUND,
            BonusWordRules.select(puzzle, setOf("DOGS"), dictionary,
                GridPoint(1, 0), GridPoint(1, 3)).status)
        val full = setOf("DOGS", "TREE", "BIRD", "FROG") +
            (1..8).map { "WORD" + it }.toSet()
        assertEquals(BonusClaimStatus.FULL,
            BonusWordRules.select(puzzle, full, dictionary,
                GridPoint(2, 0), GridPoint(2, 3)).status)
        assertEquals(0, BonusWordRules.points(0, 0, false))
        assertEquals(115, BonusWordRules.points(1, 1, false))
        assertEquals(415, BonusWordRules.points(3, 1, true))
        assertEquals(1380, BonusWordRules.points(11, 12, true))
        assertEquals(100, BonusWordRules.points(0, 0, true))
    }

    @Test fun corruptSideWordsAreRejectedAndLegacyEmptyStateIsValid() {
        assertTrue(BonusWordRules.validateSaved(puzzle, emptySet()).isEmpty())
        assertEquals(setOf("DOGS", "TREE"),
            BonusWordRules.validateSaved(puzzle, setOf("DOGS", "TREE")))
        for (invalid in listOf(
            setOf("CATS"), setOf("MISSING"), setOf("DOG"), setOf("dogS"),
            (1..13).map { "WORD$it" }.toSet()
        )) {
            assertTrue("Should reject $invalid",
                runCatching { BonusWordRules.validateSaved(puzzle, invalid) }.isFailure)
        }
    }
}
