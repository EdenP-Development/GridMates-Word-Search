package com.playereden.gridmates.wordsearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** WS16CC: ordinary type stays compact while accessibility type gets full rows. */
class LobbyLargeTextLayoutTest {
    @Test fun dailyAndNearbyShareTheSameLargeTextBreakpoint() {
        assertFalse(shouldStackLobbyActions(1.0f))
        assertFalse(shouldStackLobbyActions(1.49f))
        assertTrue(shouldStackLobbyActions(1.5f))
        assertTrue(shouldStackLobbyActions(1.8f))
        assertTrue(shouldStackLobbyActions(2.0f))
    }
}
