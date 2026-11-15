package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** A UI choice only, never a change to the puzzle generator or saved record. */
class SoloWordIndexModeTest {
    @Test fun longSoloBoardsUseOneContinuousIndex() {
        assertTrue(scrollSoloWordIndex(daily = false, targetCount = 12))
        assertTrue(scrollSoloWordIndex(daily = false, targetCount = 16))
        assertTrue(scrollSoloWordIndex(daily = false, targetCount = 44))
    }

    @Test fun shortAndDailyBoardsKeepTheirExistingPresentation() {
        assertFalse(scrollSoloWordIndex(daily = false, targetCount = 6))
        assertFalse(scrollSoloWordIndex(daily = false, targetCount = 11))
        assertFalse(scrollSoloWordIndex(daily = true, targetCount = 6))
        assertFalse(scrollSoloWordIndex(daily = true, targetCount = 16))
    }
}
