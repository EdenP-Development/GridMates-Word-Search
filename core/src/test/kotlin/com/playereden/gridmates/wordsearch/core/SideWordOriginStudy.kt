package com.playereden.gridmates.wordsearch.core

/**
 * WS47 offline analysis helper. Classifies the BEST available route for each
 * eligible *distinct* side word in a real board, not which route a human used.
 *
 * FREE_PATH_AVAILABLE > CROSSING_ONLY > TARGET_FRAGMENT_ONLY.
 *
 * A word may be a literal substring of a listed target AND have a completely
 * separate occurrence elsewhere; in that case we classify it as FREE, not as
 * solely a fragment. No gameplay code or reward policy depends on this study.
 */
internal enum class SideWordOrigin {
    TARGET_FRAGMENT_ONLY,
    CROSSING_ONLY,
    FREE_PATH_AVAILABLE
}

internal object SideWordOriginStudy {
    fun classify(puzzle: WordSearchPuzzle, dictionary: Set<String>): Map<String, SideWordOrigin> {
        val targetTexts = puzzle.targets.map { it.word }.toSet()
        val placedPaths = puzzle.targets.map { it.positions }
        val occupied = placedPaths.flatMap { it }.toHashSet()
        val result = mutableMapOf<String, SideWordOrigin>()
        val n = puzzle.size

        for (r in 0 until n) for (c in 0 until n)
            for (dr in -1..1) for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val word = StringBuilder()
                val path = ArrayList<GridPoint>(BonusWordRules.MAX_LENGTH)
                for (length in 1..minOf(n, BonusWordRules.MAX_LENGTH)) {
                    val y = r + dr * (length - 1)
                    val x = c + dc * (length - 1)
                    if (y !in 0 until n || x !in 0 until n) break
                    val point = GridPoint(y, x)
                    path.add(point)
                    word.append(puzzle.rows[y][x])
                    if (length < BonusWordRules.MIN_LENGTH) continue
                    val text = word.toString()
                    if (text !in dictionary || text in targetTexts) continue

                    val embedded = placedPaths.any { placed ->
                        isContiguousSegment(placed, path) ||
                            isContiguousSegment(placed.asReversed(), path)
                    }
                    val kind = when {
                        embedded -> SideWordOrigin.TARGET_FRAGMENT_ONLY
                        path.any { it in occupied } -> SideWordOrigin.CROSSING_ONLY
                        else -> SideWordOrigin.FREE_PATH_AVAILABLE
                    }
                    // Keep the least target-dependent route for that word.
                    val prior = result[text]
                    if (prior == null || kind.ordinal > prior.ordinal) result[text] = kind
                }
            }
        return result.toSortedMap()
    }

    private fun isContiguousSegment(whole: List<GridPoint>, piece: List<GridPoint>): Boolean {
        if (piece.size >= whole.size) return false
        // Targets are immutable, and only contiguous cells in their actual
        // placement qualify. Nearby accidental strings are NOT fragments.
        return whole.windowed(piece.size).any { it == piece }
    }
}
