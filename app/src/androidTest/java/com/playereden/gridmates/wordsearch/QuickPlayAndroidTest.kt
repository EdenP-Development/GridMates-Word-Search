package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class QuickPlayAndroidTest {
    private fun withStore(test: (SoloStore, String) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws39_quick_${System.nanoTime()}.json"
        try { test(SoloStore(context, name), name) }
        finally {
            listOf("", ".bak", ".new").forEach { File(context.filesDir, name + it).delete() }
            File(context.applicationInfo.dataDir, "shared_prefs").listFiles()
                ?.filter { it.name.startsWith(name) }?.forEach { it.delete() }
        }
    }

    @Test fun quickPlayReopensExactBoardAndAwardsNormalCompletionOnce() = withStore { store, name ->
        val oldTheme = store.profile.active.theme
        assertTrue(store.startQuickPlay())
        val run = store.profile.active
        assertNotEquals(oldTheme, run.theme)
        assertEquals(PuzzleBoardSize.BOOK_SHORT, run.boardSize)
        assertEquals(PuzzleChallenge.CLASSIC, run.challenge)
        val puzzle = SoloProgressRules.puzzle(run)
        assertEquals(12, puzzle.targets.size)
        val first = puzzle.targets.first()
        assertEquals(ClaimStatus.FOUND,
            store.claim(first.positions.last(), first.positions.first()).status)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val reopened = SoloStore(context, name)
        assertEquals(puzzle, SoloProgressRules.puzzle(reopened.profile.active))
        assertTrue(first.id in reopened.profile.active.foundIds)
        puzzle.targets.drop(1).forEach { t -> assertEquals(ClaimStatus.FOUND,
            reopened.claim(t.positions.first(), t.positions.last()).status) }
        assertEquals(1, reopened.profile.completions)
        assertEquals(1, reopened.profile.results.size)
        assertEquals(ClaimStatus.ALREADY_FOUND,
            reopened.claim(first.positions.first(), first.positions.last()).status)
        assertEquals(1, reopened.profile.completions)
        assertTrue(reopened.startQuickPlay())
        assertNotEquals(run.puzzleId, reopened.profile.active.puzzleId)
    }

    @Test fun newQuickPlayPreservesPartialGamesAndNeverDiscardsThemWhenFull() = withStore { store, _ ->
        val original = store.profile.active
        val board = SoloProgressRules.puzzle(original)
        val first = board.targets.first()
        assertEquals(ClaimStatus.FOUND,
            store.claim(first.positions.first(), first.positions.last()).status)
        assertTrue(store.startQuickPlay())
        assertTrue(store.profile.pausedSolo.any {
            it.puzzleId == original.puzzleId && first.id in it.foundIds
        })
        // Fill the bounded pause library by starting and finding one word.
        repeat(23) {
            val t = SoloProgressRules.puzzle(store.profile.active).targets.first()
            store.claim(t.positions.first(), t.positions.last())
            assertTrue(store.startQuickPlay())
        }
        assertEquals(24, store.profile.pausedSolo.size)
        val target = SoloProgressRules.puzzle(store.profile.active).targets.first()
        store.claim(target.positions.first(), target.positions.last())
        val before = store.profile
        assertFalse(store.startQuickPlay())
        assertEquals(before, store.profile)
        assertTrue(requireNotNull(store.warning).contains("full"))
    }
}
