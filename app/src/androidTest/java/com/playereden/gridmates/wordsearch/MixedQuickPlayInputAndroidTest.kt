package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class MixedQuickPlayInputAndroidTest {
    @get:Rule val compose=createComposeRule()
    @Test fun mixedWordFingerTracePersistsAndCompletionPlaysAnotherAtSameSize() {
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val name="ws41_input_${System.nanoTime()}.json"
        try {
            val store=SoloStore(context,name)
            assertTrue(store.startQuickPlay(PuzzleBoardSize.BOOK_CLASSIC))
            val before=store.profile.active.puzzleId
            val puzzle=SoloProgressRules.puzzle(store.profile.active)
            compose.setContent { GridMatesTheme { Box(Modifier.width(360.dp).height(820.dp)) {
                WordSearchScreen(store,onBack={},onRecords={},onNewPuzzle={},showBookArt=true,
                    onPlayAnother={ store.startQuickPlay(requireNotNull(store.profile.active.boardSize)) })
            } } }
            val t=puzzle.targets.first();val start=t.positions.first();val end=t.positions.last()
            compose.onNodeWithTag("word-grid-touch-area").performTouchInput {
                val x=width.toFloat()/puzzle.size;val y=height.toFloat()/puzzle.size
                swipe(Offset((start.col+0.5f)*x,(start.row+0.5f)*y),
                    Offset((end.col+0.5f)*x,(end.row+0.5f)*y),400L)
            }
            compose.runOnIdle {
                assertTrue(t.id in SoloStore(context,name).profile.active.foundIds)
                puzzle.targets.drop(1).forEach { word -> store.claim(word.positions.first(),word.positions.last()) }
            }
            compose.onNodeWithText("Puzzle complete!").assertExists()
            compose.onNodeWithText("Play another puzzle").performScrollTo().performClick()
            compose.runOnIdle {
                assertEquals(1,store.profile.completions)
                assertEquals(PuzzleBoardSize.BOOK_CLASSIC,store.profile.active.boardSize)
                assertNotEquals(before,store.profile.active.puzzleId)
            }
            compose.onNodeWithText("0 / 16 found").assertExists()
        } finally { listOf("", ".bak", ".new").forEach { File(context.filesDir,name+it).delete() } }
    }
    @Test fun chooserKeepsLargeChoiceReachableAtTwoHundredPercentText() {
        var picked:PuzzleBoardSize?=null
        compose.setContent { GridMatesTheme {
            val current=LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(current.density,2f)) {
                QuickPlayChooser("",{picked=it},{})
            }
        } }
        compose.onNodeWithTag("quick-size-20").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(PuzzleBoardSize.BOOK_BIG,picked) }
    }
}
