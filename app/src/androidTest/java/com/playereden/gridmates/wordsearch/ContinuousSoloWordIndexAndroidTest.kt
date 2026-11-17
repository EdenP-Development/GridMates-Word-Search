package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Real layout checks; compiled by CI, executed on device/emulator separately. */
class ContinuousSoloWordIndexAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun longGardenTargetsKeepTheirWholeWordAtPhoneTextScale() {
        val words = listOf("PHOTOSYNTHESIS", "GERMINATION")
        val sample = WordSearchPuzzle(
            id = "word-list-layout-only:long-garden",
            seed = 0L, contentVersion = 3,
            theme = WordTheme.GARDEN, difficulty = WordDifficulty.HARD,
            rows = List(30) { "A".repeat(30) },
            targets = words.map { WordTarget(it, it, emptyList()) }
        )
        compose.setContent {
            val base = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(base.density, 1.25f)) {
                GridMatesTheme {
                    Box(Modifier.width(320.dp).height(300.dp)) {
                        WordList(sample, emptySet(), daily = false,
                            modifier = Modifier.height(300.dp), scrollAll = true)
                    }
                }
            }
        }
        for (word in words) {
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText("○ $word").assertIsDisplayed()
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertTrue("The complete target should fit on one line",
                layouts.isNotEmpty() && layouts.all {
                    !it.hasVisualOverflow && it.lineCount == 1
                })
        }
    }

    @Test fun classicFifteenByFifteenCanScrollToAllWordsWithoutEightPages() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws16bz_scroll_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(ctx, name)
            store.chooseV2(WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_CLASSIC, PuzzleChallenge.CLASSIC)
            val puzzle = SoloProgressRules.puzzle(store.profile.active)
            assertEquals(16, puzzle.targets.size)
            compose.setContent {
                GridMatesTheme {
                    Box(Modifier.width(360.dp).height(780.dp)) {
                        WordSearchScreen(store, onBack = {}, onRecords = {})
                    }
                }
            }
            compose.onNodeWithTag("word-list-scrollable").assertExists()
            compose.onNodeWithText("Page 1 of 8").assertDoesNotExist()
            compose.onNodeWithTag("word-list-target-" + puzzle.targets.last().id)
                .performScrollTo().assertIsDisplayed()
            compose.runOnIdle {
                assertEquals(0, store.profile.active.foundIds.size)
                assertEquals(0, store.profile.completions)
            }
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(ctx.filesDir, name + suffix).delete()
            }
        }
    }

    @Test fun shortDailyWordListStillUsesExistingPaging() {
        val puzzle = WordSearchGeneratorV2.generate(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_SHORT,
            PuzzleChallenge.RELAXED, 27L
        )
        compose.setContent {
            GridMatesTheme {
                Box(Modifier.width(360.dp).height(220.dp)) {
                    WordList(puzzle, emptySet(), daily = true,
                        modifier = Modifier.height(220.dp))
                }
            }
        }
        compose.onNodeWithTag("word-list-scrollable").assertDoesNotExist()
        compose.onNodeWithText("Words to find", substring = true).assertExists()
    }
}
