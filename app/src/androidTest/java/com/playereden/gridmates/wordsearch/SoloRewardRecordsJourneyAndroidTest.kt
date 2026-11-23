package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Compiled by normal QA; execution requires an emulator or real Android phone. */
class SoloRewardRecordsJourneyAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun interruptedClassicGameReopensFinishesAndEarnsPersistentRewards() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws16cb_roundtrip_" + System.nanoTime() + ".json"
        try {
            val first = SoloStore(ctx, name)
            assertTrue(first.chooseV2(WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC))
            val puzzle = SoloProgressRules.puzzle(first.profile.active)
            val target = puzzle.targets.first()
            assertEquals(ClaimStatus.FOUND,
                first.claim(target.positions.first(), target.positions.last()).status)
            first.flush()

            val resumed = SoloStore(ctx, name)
            assertEquals(puzzle.id, resumed.profile.active.puzzleId)
            assertTrue(target.id in resumed.profile.active.foundIds)
            assertEquals(0, resumed.profile.completions)
            assertFalse("woodland_rabbit" in WoodlandCollection.unlocked(resumed.profile))

            val hint = resumed.hint()
            assertNotNull(hint.second)
            puzzle.targets.drop(1).forEach { word ->
                assertEquals(ClaimStatus.FOUND,
                    resumed.claim(word.positions.first(), word.positions.last()).status)
            }
            assertEquals(1, resumed.profile.completions)
            assertTrue(resumed.profile.results.single().assisted)
            assertEquals(puzzle.targets.size, resumed.profile.totalWords)
            assertEquals(0, resumed.profile.dailyCompletions)
            assertTrue("woodland_rabbit" in WoodlandCollection.unlocked(resumed.profile))
            assertTrue(WordSearchAchievements.progress(resumed.profile)
                .first { it.id == "first_puzzle" }.unlocked)
            assertTrue(resumed.equipPortrait("woodland_rabbit"))
            resumed.flush()

            val reopened = SoloStore(ctx, name)
            assertEquals(puzzle.id, reopened.profile.results.single().puzzleId)
            assertEquals(1, reopened.profile.completions)
            assertEquals("woodland_rabbit", reopened.profile.identity.portraitId)
            // Rendering the same saved journal is read-only.
            compose.setContent {
                GridMatesTheme {
                    Column(Modifier.width(360.dp).height(720.dp)
                        .verticalScroll(rememberScrollState())) {
                        SoloRecordsScreen(reopened.profile)
                    }
                }
            }
            compose.onNodeWithText("Recent puzzles").assertExists()
            compose.onNodeWithText("Your story starts with a puzzle.")
                .assertDoesNotExist()
            compose.runOnIdle {
                assertEquals(1, reopened.profile.completions)
                assertEquals(1, reopened.profile.results.size)
            }
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(ctx.filesDir, name + suffix).delete()
            }
        }
    }
}
