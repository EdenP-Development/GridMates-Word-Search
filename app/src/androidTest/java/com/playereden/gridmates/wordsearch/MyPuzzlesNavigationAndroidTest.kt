package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

class MyPuzzlesNavigationAndroidTest {
    @get:Rule val rule = createComposeRule()

    @Test fun playerCanOpenPausedLibraryAndResumeWithoutDiscardingCurrentGame() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws09b_library_ui_" + System.nanoTime() + ".json"
        val rooms = NearbyRoomViewModel()
        try {
            val store = SoloStore(context, name)
            val pausedId = store.profile.active.puzzleId
            check(store.newPuzzle())
            rule.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(720.dp)) {
                        GridMatesShell(store, rooms)
                    }
                }
            }
            rule.onNodeWithText("My Puzzles").assertIsDisplayed().performClick()
            rule.onNodeWithText("PAUSED · 1 / 24").assertExists()
            rule.onNodeWithTag("ws31-current-solo-card").assertExists()
            rule.onNodeWithTag("ws31-current-resume").assertExists()
            rule.onNodeWithTag("ws31-paused-solo-" + pausedId).assertExists()
            rule.onNodeWithTag("ws31-paused-resume-" + pausedId).assertExists()
            rule.onNodeWithText("Resume").performScrollTo().performClick()
            rule.onNodeWithText("Trace words · pinch to zoom", substring = true)
                .assertExists()
            assertEquals(pausedId, store.profile.active.puzzleId)
            assertEquals(1, store.profile.pausedSolo.size)
        } finally {
            rooms.leaveRoom()
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, name + suffix).delete()
            }
        }
    }
}
