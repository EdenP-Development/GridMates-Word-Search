package com.playereden.gridmates.wordsearch.core

import org.junit.Assert.*
import org.junit.Test

/** WS16CB: the real Solo finish must unlock the same earned rewards shown in
 * Collection, without creating duplicate Records entries on repeat claims. */
class SoloRewardRecordsJourneyTest {
    @Test fun seededClassicPuzzleUnlocksRabbitAndFirstPageExactlyOnce() {
        val initial = SoloProgressRules.freshV2(
            WordTheme.WOODLAND, PuzzleBoardSize.BOOK_CLASSIC,
            PuzzleChallenge.CLASSIC, 815151L
        )
        val puzzle = SoloProgressRules.puzzle(initial)
        assertEquals(16, puzzle.targets.size)
        var profile = SoloProfile(initial)
        assertFalse("Rabbit must start locked",
            "woodland_rabbit" in WoodlandCollection.unlocked(profile))
        assertEquals(0, profile.completions)

        // Exactly one real word, then a hint on an unfinished target.
        val first = puzzle.targets.first()
        val (afterFirst, firstClaim) = SoloProgressRules.find(profile.active,
            first.positions.first(), first.positions.last())
        assertEquals(ClaimStatus.FOUND, firstClaim.status)
        profile = profile.copy(active = afterFirst)
        assertEquals(0, SoloProgressRules.complete(profile, 1L).completions)
        val (afterHint, hintTarget) = SoloProgressRules.hint(profile.active)
        assertNotNull(hintTarget)
        assertEquals(1, afterHint.hints.values.sum())
        assertEquals(1, afterHint.foundIds.size)
        profile = profile.copy(active = afterHint)

        puzzle.targets.drop(1).forEach { word ->
            val (next, claim) = SoloProgressRules.find(profile.active,
                word.positions.first(), word.positions.last())
            assertEquals(ClaimStatus.FOUND, claim.status)
            profile = SoloProgressRules.complete(profile.copy(active = next), 12345L)
        }

        assertEquals(1, profile.completions)
        assertEquals(puzzle.targets.size, profile.totalWords)
        assertEquals(0, profile.dailyCompletions)
        assertEquals(1, profile.assistedCompletions)
        assertEquals(1, profile.results.size)
        assertEquals(puzzle.id, profile.results.first().puzzleId)
        assertEquals(1, profile.results.first().hintSteps)
        assertTrue(profile.results.first().assisted)
        assertTrue("Rabbit should be unlocked in Collection",
            "woodland_rabbit" in WoodlandCollection.unlocked(profile))
        assertTrue(WordSearchAchievements.progress(profile).first {
            it.id == "first_puzzle"
        }.unlocked)

        val equipped = WoodlandCollection.equip(profile, "woodland_rabbit")
        assertNotNull(equipped)
        assertEquals("woodland_rabbit", equipped!!.identity.portraitId)
        assertEquals(1, SoloProgressRules.complete(profile, 99999L).completions)
        val (_, repeated) = SoloProgressRules.find(profile.active,
            first.positions.first(), first.positions.last())
        assertEquals(ClaimStatus.ALREADY_FOUND, repeated.status)
        assertEquals(1, SoloProgressRules.complete(profile, 99999L).results.size)
    }
}
