package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.BonusWordRules
import com.playereden.gridmates.wordsearch.core.SoloRun
import com.playereden.gridmates.wordsearch.core.WordSearchPuzzle

/**
 * Human-friendly completion summary for Android's native Share sheet.
 * Deliberately never includes puzzle answers, RNG seed, room information,
 * player name, device identity, or any private local-record identifiers.
 */
internal object PuzzleResultShare {
    fun text(puzzle: WordSearchPuzzle, run: SoloRun,
             seconds: Long, daily: Boolean): String {
        val size = puzzle.size
        val title = if (daily) "Daily Puzzle" else "Solo Puzzle"
        val minutes = seconds.coerceAtLeast(0L) / 60L
        val sec = (seconds.coerceAtLeast(0L) % 60L).toString().padStart(2, '0')
        val hints = run.hints.values.sum().coerceAtLeast(0)
        val hintLine = if (hints == 0) "No hints used"
            else "$hints hint " + (if (hints == 1) "step" else "steps") + " used"
        val challenge = run.challenge?.label ?: run.difficulty.label
        return listOf(
            "GridMates: Word Search",
            title + " complete!",
            puzzle.displayTitle + " · " + size + "×" + size + " · " + challenge,
            puzzle.targets.size.toString() + " words found · " + minutes + ":" + sec,
            hintLine,
            "Score " + BonusWordRules.points(puzzle.targets.size,
                run.bonusWords.size, true) + " · " +
                run.bonusWords.size + " bonus " +
                (if (run.bonusWords.size == 1) "word" else "words"),
            "Good puzzles. Good company."
        ).joinToString("\n")
    }
}
