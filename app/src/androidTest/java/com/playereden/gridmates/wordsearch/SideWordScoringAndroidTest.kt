package com.playereden.gridmates.wordsearch

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Actual saved Solo/Quick Play test: real board paths and atomic profile state. */
class SideWordScoringAndroidTest {
    private fun bonusPath(puzzle: WordSearchPuzzle, dictionary: Set<String>):
            Pair<GridPoint, GridPoint>? {
        for (r in 0 until puzzle.size) for (c in 0 until puzzle.size)
            for (dr in -1..1) for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                for (length in 4..minOf(puzzle.size, BonusWordRules.MAX_LENGTH)) {
                    val end = GridPoint(r + dr * (length - 1), c + dc * (length - 1))
                    val match = BonusWordRules.select(puzzle, emptySet(), dictionary,
                        GridPoint(r,c), end)
                    if (match.status == BonusClaimStatus.FOUND)
                        return GridPoint(r,c) to end
                }
            }
        return null
    }

    @Test fun realQuickPlayBonusPersistsAcrossRestartBackupAndDuplicateTrace() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "ws44_bonus_" + System.nanoTime() + ".json"
        val second = "ws44_bonus_import_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, name)
            val curated = requireNotNull(QuickPlayContent.mixed).words
                .map { it.answer }.toSet() +
                WordTheme.entries.flatMap { it.words }
            var path: Pair<GridPoint, GridPoint>? = null
            // Every tested board is real V5 QA content, not a synthetic UI fake.
            for (attempt in 0..5) {
                assertTrue(store.startQuickPlay(PuzzleBoardSize.BOOK_BIG))
                path = bonusPath(SoloProgressRules.puzzle(store.profile.active), curated)
                if (path != null) break
            }
            val (start, end) = requireNotNull(path) {
                "Expected a real non-target reviewed word among six 20x20 boards"
            }
            val before = store.profile.active.foundIds
            val matched = store.claimSideWord(start, end, daily = false)
            assertEquals(BonusClaimStatus.FOUND, matched.status)
            assertEquals(15, BonusWordRules.points(0, 1, false))
            assertEquals(before, store.profile.active.foundIds)
            assertEquals(setOf(matched.word), store.profile.active.bonusWords)
            assertEquals(BonusClaimStatus.ALREADY_FOUND,
                store.claimSideWord(start, end, daily = false).status)

            val reopened = SoloStore(context, name)
            assertEquals(store.profile, reopened.profile)
            assertEquals(1, reopened.profile.active.bonusWords.size)
            val backup = reopened.exportBackup()
            val imported = SoloStore(context, second)
            assertTrue(imported.restoreBackup(imported.previewBackup(backup)))
            assertEquals(reopened.profile, imported.profile)
            val corrupt = JSONObject(backup).apply {
                getJSONObject("active").put("sideWords",
                    JSONArray().put("FAKEWORD"))
            }.toString()
            assertTrue(runCatching { imported.previewBackup(corrupt) }.isFailure)
            assertEquals(backup, imported.exportBackup())

            val board = SoloProgressRules.puzzle(imported.profile.active)
            board.targets.forEach { t ->
                assertEquals(ClaimStatus.FOUND,
                    imported.claim(t.positions.first(), t.positions.last()).status)
            }
            val result = imported.profile.results.first()
            assertEquals(1, result.bonusWordCount)
            assertEquals(board.targets.size * 100 + 15 + 100, result.points)
            assertEquals(BonusClaimStatus.INVALID,
                imported.claimSideWord(start, end, daily = false).status)
        } finally {
            for (n in listOf(name, second)) {
                for (suffix in listOf("", ".bak", ".new"))
                    File(context.filesDir, n + suffix).delete()
                File(context.applicationInfo.dataDir, "shared_prefs").listFiles()
                    ?.filter { it.name.startsWith(n) }?.forEach { it.delete() }
            }
        }
    }
}
