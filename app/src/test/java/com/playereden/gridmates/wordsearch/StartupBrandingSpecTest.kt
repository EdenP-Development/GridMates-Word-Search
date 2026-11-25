package com.playereden.gridmates.wordsearch

import org.junit.Assert.*
import org.junit.Test

class StartupBrandingSpecTest {
    @Test fun approvedSequenceAndTiming() {
        assertEquals(listOf("Spudacus", "GridMates"), StartupBrandingSpec.stages.map { it.first })
        assertEquals(300, StartupBrandingSpec.FADE_MS)
        assertEquals(1500, StartupBrandingSpec.HOLD_MS)
        assertEquals(4200, StartupBrandingSpec.stages.size *
            (2 * StartupBrandingSpec.FADE_MS + StartupBrandingSpec.HOLD_MS))
    }
    @Test fun onlyColdLaunchWithMotionShowsBranding() {
        for (cold in listOf(false,true)) for (motion in listOf(false,true)) {
            assertEquals(if (cold && motion) 0 else StartupBrandingSpec.stages.size,
                StartupBrandingSpec.initialStage(cold,motion))
        }
    }
}
