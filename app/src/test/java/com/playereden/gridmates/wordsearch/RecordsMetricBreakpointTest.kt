package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pure breakpoint validation; UI node checks live in Android instrumentation. */
class RecordsMetricBreakpointTest {
    @Test fun normalPhoneContentWidthsStayTwoColumn() {
        assertFalse(stackMatchSummaryMetrics(288f, 1f))  // 320dp screen with 16dp margins
        assertFalse(stackMatchSummaryMetrics(328f, 1f))  // 360dp screen with 16dp margins
        assertFalse(stackMatchSummaryMetrics(288f, 1.3f))
    }
    @Test fun extremeNarrowOrLargeTextStacks() {
        assertTrue(stackMatchSummaryMetrics(260f, 1f))
        assertTrue(stackMatchSummaryMetrics(288f, 1.8f))
        assertTrue(stackMatchSummaryMetrics(328f, 2f))
    }
}
