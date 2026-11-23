package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tap-step rules for the board zoom buttons. */
class Ws14FGridZoomGuidanceTest {
    @Test fun denseBoardsHaveUsefulInitialZoomTapWhileSmallBoardsStayUnchanged() {
        assertEquals(0.25f, gridZoomTapIncrement(12), 0.001f)
        assertEquals(0.25f, gridZoomTapIncrement(15), 0.001f)
        assertEquals(0.5f, gridZoomTapIncrement(20), 0.001f)
        assertEquals(0.5f, gridZoomTapIncrement(25), 0.001f)
    }
}
