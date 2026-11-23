package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SideWordOriginStudyTest {
    @Test fun realPlacedTargetSegmentsCrossingsAndFreeWordsAreDistinct() {
        val board = WordSearchPuzzle(
            id = "ws47-path-provenance",
            seed = 0L,
            contentVersion = 1,
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
        val words = setOf("FLOWER", "FLOW", "WOLF", "ROSE", "DOGS", "NOTONBOARD")
        val found = SideWordOriginStudy.classify(board, words)
        assertEquals(mapOf(
            "FLOW" to SideWordOrigin.TARGET_FRAGMENT_ONLY,
            "WOLF" to SideWordOrigin.TARGET_FRAGMENT_ONLY,
            "ROSE" to SideWordOrigin.CROSSING_ONLY,
            "DOGS" to SideWordOrigin.FREE_PATH_AVAILABLE
        ), found)
        assertEquals(BonusClaimStatus.FOUND, BonusWordRules.select(
            board, emptySet(), words, GridPoint(2,3), GridPoint(2,0)).status)
        assertEquals(BonusClaimStatus.FOUND, BonusWordRules.select(
            board, emptySet(), words, GridPoint(1,2), GridPoint(4,2)).status)
    }

    @Test fun repeatedIndependentOccurrenceOutranksAWordEmbeddedInsideATarget() {
        val board = WordSearchPuzzle(
            id = "ws47-two-occurrences",
            seed = 1L, contentVersion = 1, theme = WordTheme.GARDEN,
            difficulty = WordDifficulty.EASY,
            rows = listOf("FLOWER", "XXXXXX", "WOLFXX", "XXXXXX", "XXXXXX", "XXXXXX"),
            targets = listOf(
                WordTarget("flower-target", "FLOWER",
                    (0..5).map { col -> GridPoint(0,col) })
            )
        )
        val found = SideWordOriginStudy.classify(board, setOf("FLOWER", "WOLF"))
        assertEquals(SideWordOrigin.FREE_PATH_AVAILABLE, found["WOLF"])
        assertEquals(1, found.size)
        assertTrue("FLOWER" !in found)
    }
}
