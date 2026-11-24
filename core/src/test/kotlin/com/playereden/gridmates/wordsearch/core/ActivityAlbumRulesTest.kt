package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActivityAlbumRulesTest {
    private val blank = SoloProfile(SoloProgressRules.fresh(seed = 42))

    @Test fun dailyStampsUseTheSameEarnedStateAsAchievements() {
        // Spread the completions across the month. An eighth unfinished day
        // contributes nothing.
        val days = (1..13 step 2).map { day ->
            val entry = DailyChallenge.start("2026-11-${day.toString().padStart(2, '0')}")
            val board = SoloProgressRules.puzzle(entry.run)
            DailyChallenge.complete(entry.copy(run = entry.run.copy(
                foundIds = board.targets.map { it.id }.toSet())), day.toLong())
        }
        val before = blank.copy(dailyEntries = days.dropLast(1) + DailyChallenge.start("2026-11-14"))
        assertEquals(1, ActivityAlbumRules.progress(before).earnedCount)
        val profile = before.copy(dailyEntries = before.dailyEntries + days.last())
        val album = ActivityAlbumRules.progress(profile)
        val dailyStamps = album.stamps.filter { it.id == "one_daily" || it.id == "seven_dailies" }
        assertEquals(WordSearchAchievements.progress(profile).filter { it.id in dailyStamps.map { s -> s.id } },
            dailyStamps)
        assertTrue(dailyStamps.all { it.unlocked })
        assertEquals(2, album.earnedCount)
    }

    @Test fun allSizesRequiresThreeDistinctMixedQuickPlaySizes() {
        val results = listOf(
            result(PuzzleBoardSize.BOOK_SHORT, 1),
            result(PuzzleBoardSize.BOOK_SHORT, 2),
            result(PuzzleBoardSize.BOOK_BIG, 3),
            result(PuzzleBoardSize.BOOK_CLASSIC, 4, mixed = false),
            result(PuzzleBoardSize.BOOK_GRAND, 5)
        )
        val profile = blank.copy(results = results)
        val partial = ActivityAlbumRules.progress(profile)
        val goal = partial.stamps.single { it.id == ActivityAlbumRules.ALL_SIZES }
        assertFalse(goal.unlocked)
        assertEquals(2, goal.current)
        assertEquals(1, partial.earnedCount)
        assertEquals(setOf(PuzzleBoardSize.BOOK_SHORT, PuzzleBoardSize.BOOK_BIG),
            partial.solvedQuickPlaySizes)

        val completed = ActivityAlbumRules.progress(profile.copy(
            results = results + result(PuzzleBoardSize.BOOK_CLASSIC, 6)))
        assertTrue(completed.stamps.single { it.id == ActivityAlbumRules.ALL_SIZES }.unlocked)
        assertEquals(2, completed.earnedCount)
    }

    private fun result(size: PuzzleBoardSize, seed: Long, mixed: Boolean = true): SoloResult =
        SoloResult("fixture-$seed", WordTheme.WOODLAND, WordDifficulty.MEDIUM, size.targets,
            elapsedSeconds = 40, hintSteps = 0, completedAtMs = seed, boardSize = size,
            challenge = PuzzleChallenge.CLASSIC, quickPackId = if (mixed) "en-cozy-mixed-v1" else null)
}
