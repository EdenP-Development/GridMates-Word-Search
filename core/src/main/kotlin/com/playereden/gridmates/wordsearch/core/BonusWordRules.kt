package com.playereden.gridmates.wordsearch.core

/**
 * WS44 — optional, local-only side-word rewards. A word must be on the traced
 * letters AND in an explicitly provided curated lexicon. Random filler is never
 * treated as a dictionary by itself. No cloud lookup, ads or currency.
 *
 * This remains separate from WordSearchRules.claim: bonus discoveries never
 * fulfill targets, count as achievements, or modify Race/Duel claim authority.
 */
enum class BonusClaimStatus { FOUND, ALREADY_FOUND, FULL, INVALID }

data class BonusWordClaim(
    val status: BonusClaimStatus,
    val word: String? = null
)

object BonusWordRules {
    const val TARGET_POINTS = 100
    const val SIDE_WORD_POINTS = 15
    const val COMPLETION_POINTS = 100
    const val MAX_SIDE_WORDS = 12
    const val MIN_LENGTH = 4
    const val MAX_LENGTH = 16

    /** Pure, earned-only score. Time and hint use do not penalize accessibility. */
    fun points(targetsFound: Int, sideWordsFound: Int, completed: Boolean): Int =
        targetsFound.coerceAtLeast(0) * TARGET_POINTS +
            sideWordsFound.coerceIn(0, MAX_SIDE_WORDS) * SIDE_WORD_POINTS +
            if (completed) COMPLETION_POINTS else 0

    fun select(
        puzzle: WordSearchPuzzle,
        already: Set<String>,
        dictionary: Set<String>,
        start: GridPoint,
        end: GridPoint
    ): BonusWordClaim {
        val traced = wordOnLine(puzzle, start, end)
            ?: return BonusWordClaim(BonusClaimStatus.INVALID)
        // The displayed target word never earns extra side-word points, even
        // if a duplicate physical occurrence exists in an older V1/V2 board.
        if (traced !in dictionary || puzzle.targets.any { it.word == traced }) {
            return BonusWordClaim(BonusClaimStatus.INVALID)
        }
        if (traced in already) return BonusWordClaim(BonusClaimStatus.ALREADY_FOUND, traced)
        if (already.size >= MAX_SIDE_WORDS) return BonusWordClaim(BonusClaimStatus.FULL, traced)
        return BonusWordClaim(BonusClaimStatus.FOUND, traced)
    }

    /** Defensive saved-state verification independent of which QA pack is installed. */
    fun validateSaved(puzzle: WordSearchPuzzle, stored: Set<String>): Set<String> {
        require(stored.size <= MAX_SIDE_WORDS) { "Too many side words in saved puzzle" }
        require(stored.all { word ->
            word.length in MIN_LENGTH..MAX_LENGTH &&
                word.all { it in 'A'..'Z' } &&
                puzzle.targets.none { it.word == word } &&
                existsInBoard(puzzle, word)
        }) { "Invalid side word in saved puzzle" }
        return stored
    }

    private fun wordOnLine(
        puzzle: WordSearchPuzzle, start: GridPoint, end: GridPoint
    ): String? {
        val path = WordSearchRules.line(start, end, puzzle.size)
        if (path.size !in MIN_LENGTH..MAX_LENGTH) return null
        val result = StringBuilder(path.size)
        for (point in path) result.append(puzzle.letter(point) ?: return null)
        return result.toString()
    }

    private fun existsInBoard(puzzle: WordSearchPuzzle, word: String): Boolean {
        for (r in 0 until puzzle.size) for (c in 0 until puzzle.size)
            for (dr in -1..1) for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val endR = r + dr * (word.length - 1)
                val endC = c + dc * (word.length - 1)
                if (endR !in 0 until puzzle.size || endC !in 0 until puzzle.size) continue
                if (word.indices.all { i ->
                    puzzle.rows[r + dr * i][c + dc * i] == word[i]
                }) return true
            }
        return false
    }
}
