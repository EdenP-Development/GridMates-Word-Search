package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Compiles in standard CI; needs Android emulator/phone execution for visual QA. */
class NearbyDailyLargeTextAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun dailyPrimaryActionRemainsReachableAt320dpAnd200PercentFont() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws16cc_daily_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            compose.setContent {
                val original = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(original.density, 2.0f)
                ) {
                    GridMatesTheme {
                        Column(Modifier.width(320.dp).height(640.dp)
                            .verticalScroll(rememberScrollState())) {
                            DailyLandingScreen(store, onPlay = {}, onBack = {})
                        }
                    }
                }
            }
            compose.onNodeWithTag("ws16cc-daily-today-stacked").assertExists()
            compose.onNodeWithText("Play today").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Daily Journal").performScrollTo().assertIsDisplayed()
        } finally {
            listOf("", ".bak", ".new").forEach {
                File(ctx.filesDir, name + it).delete()
            }
        }
    }

    @Test fun allNearbyModesAndJoinStayReachableAt320dpAnd200PercentFont() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws16cc_nearby_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(ctx, name)
            compose.setContent {
                val original = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(original.density, 2.0f)
                ) {
                    GridMatesTheme {
                        Column(Modifier.width(320.dp).height(640.dp)) {
                            NearbyCoopScreen(store, rooms, onBack = {})
                        }
                    }
                }
            }
            compose.onNodeWithTag("ws16cc-nearby-modes-stacked").assertExists()
            compose.onNodeWithText("Co-op").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Race").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Duel").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Join nearby game")
                .performScrollTo().assertIsDisplayed()
        } finally {
            rooms.leaveRoom()
            listOf("", ".bak", ".new").forEach {
                File(ctx.filesDir, name + it).delete()
            }
        }
    }
}
