package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/** Runs on Android, not merely Kotlin compilation.
 * An injected interface predicate avoids depending on an external Wi-Fi router;
 * runtime.host() still authoritatively checks the real wlan/ap interface.
 */
class NearbyAutoWifiRefreshAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun hostAutomaticallyDisablesAndReenablesWithoutLeavingNearby() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws16ce_wifi_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        val connected = AtomicBoolean(false)
        try {
            val store = SoloStore(ctx, name)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(740.dp)) {
                        NearbyCoopScreen(store, rooms, onBack = {},
                            hostReadinessProbe = { connected.get() })
                    }
                }
            }
            compose.onNodeWithTag("ws16ca-host-button")
                .assertExists().assertIsNotEnabled()
            compose.onNodeWithText("Join nearby game").assertIsEnabled()

            // The Android screen is not recreated and the user does not tap
            // the manual refresh button; its existing poll sees new Wi-Fi.
            connected.set(true)
            compose.waitUntil(timeoutMillis = 8_000L) {
                runCatching {
                    compose.onNodeWithTag("ws16ca-host-button").assertIsEnabled()
                }.isSuccess
            }
            compose.onNodeWithText("Join nearby game").assertIsEnabled()

            // Losing the local interface must never leave an enabled Host.
            connected.set(false)
            compose.waitUntil(timeoutMillis = 8_000L) {
                runCatching {
                    compose.onNodeWithTag("ws16ca-host-button").assertIsNotEnabled()
                }.isSuccess
            }
            compose.onNodeWithText("Join nearby game").assertIsEnabled()
            compose.onNodeWithTag("ws16ca-host-wifi-required")
                .assertExists()
        } finally {
            rooms.leaveRoom()
            listOf("", ".bak", ".new").forEach {
                File(ctx.filesDir, name + it).delete()
            }
        }
    }
}
