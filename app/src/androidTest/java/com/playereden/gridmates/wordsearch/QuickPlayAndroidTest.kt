package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.junit.Assert.*
import org.junit.Test
import org.json.JSONObject
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
        val original = store.profile.active
        assertTrue(store.startQuickPlay())
        val run = store.profile.active
        assertNotNull(run.frozen)
        assertTrue(store.profile.pausedSolo.any { it.puzzleId == original.puzzleId })
        assertEquals(PuzzleBoardSize.BOOK_CLASSIC, run.boardSize)
        assertEquals(PuzzleChallenge.CLASSIC, run.challenge)
        val puzzle = SoloProgressRules.puzzle(run)
        assertEquals(16, puzzle.targets.size)
        assertEquals("Quick Play", puzzle.displayTitle)
        assertEquals(4, puzzle.targets.map { it.topicId }.distinct().size)
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
    @Test fun allSizesPreserveCustomPresetAndCorruptSnapshotCannotReplaceProgress() = withStore { store, name ->
        assertTrue(store.chooseSolo(WordTheme.GARDEN, PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT))
        val preset=store.soloSetupPreset()
        for(size in PuzzleBoardSize.bookPresets) {
            assertTrue(store.startQuickPlay(size))
            assertEquals(preset, store.soloSetupPreset())
            val p=SoloProgressRules.puzzle(store.profile.active)
            assertEquals(size.cells,p.size)
            assertEquals(size.targets,p.targets.size)
        }
        val before=store.exportBackup()
        val broken=JSONObject(before)
        broken.getJSONObject("active").put("frozen", "bm90LWEtdmFsaWQtYm9hcmQ=")
        assertTrue(runCatching { store.previewBackup(broken.toString()) }.isFailure)
        assertEquals(before,store.exportBackup())
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals(store.profile,SoloStore(context,name).profile)
    }

    @Test fun schemaFivePartialLegacyBoardUpgradesWithoutChangingItsLetters() = withStore { store, _ ->
        val run=store.profile.active
        val p=SoloProgressRules.puzzle(run)
        val first=p.targets.first()
        store.claim(first.positions.first(),first.positions.last())
        val legacy=JSONObject(store.exportBackup()).put("schema",5).apply { remove("bookAwards") }
        val restored=store.previewBackup(legacy.toString())
        assertEquals(p,SoloProgressRules.puzzle(restored.active))
        assertTrue(first.id in restored.active.foundIds)
        assertTrue(store.restoreBackup(restored))
        assertEquals(6,JSONObject(store.exportBackup()).getInt("schema"))
    }

}
