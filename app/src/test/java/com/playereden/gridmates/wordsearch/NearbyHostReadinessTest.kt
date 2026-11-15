package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** WS16CA: Never show a live Host action when interface readiness is absent. */
class NearbyHostReadinessTest {
    @Test fun hostRequiresLocalInterfaceAndIdleState() {
        assertFalse(canOfferNearbyHost(ready = false, busy = false))
        assertFalse(canOfferNearbyHost(ready = false, busy = true))
        assertFalse(canOfferNearbyHost(ready = true, busy = true))
        assertTrue(canOfferNearbyHost(ready = true, busy = false))
    }
}
