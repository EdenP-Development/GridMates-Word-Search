package com.playereden.gridmates.wordsearch.core

import kotlin.random.Random

/** New pack-aware engine. Never changes the frozen V1/V2/V3 generators or V4 lab. */
object WordSearchGeneratorV5 {
    const val CONTENT_VERSION = 5
    private val allDirections = (-1..1).flatMap { r ->
        (-1..1).mapNotNull { c -> if (r == 0 && c == 0) null else r to c }
    }
    private const val FILLER = "EEEEAAAOOOIINNRRTTSSLLDDCCUUMMPPGGBBFFHHVVWWYYJKQXZ"
    fun puzzleId(packId: String, packHash: String, theme: WordTheme,
                 board: PuzzleBoardSize, challenge: PuzzleChallenge, seed: Long) =
        "wordsearch:v5:$packId:$packHash:${theme.name}:${board.name}:${challenge.name}:$seed"

    fun generate(pack: ReviewedWordPack, board: PuzzleBoardSize,
                 challenge: PuzzleChallenge, seed: Long,
                 theme: WordTheme = WordTheme.WOODLAND): FrozenWordSearch {
        require(board in PuzzleBoardSize.bookPresets && seed >= 0)
        val size = board.cells
        val directions = when (challenge) {
            PuzzleChallenge.RELAXED -> listOf(0 to 1, 1 to 0)
            PuzzleChallenge.CLASSIC -> listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1, 0 to -1, -1 to 0)
            PuzzleChallenge.EXPERT -> allDirections
        }
        val packSeed = pack.sha256.take(15).toLong(16)
        repeat(80) { attempt ->
            val rng = Random(seed xor packSeed xor (board.ordinal.toLong() shl 17) xor
                (challenge.ordinal.toLong() shl 9) xor (attempt.toLong() * 0x125A7L))
            val pools = pack.topicIds.associateWith { topic ->
                pack.words.filter { word ->
                    word.answer.length <= size - 1 && word.answer != word.answer.reversed() &&
                        word.topics.any { it.topicId == topic && when (challenge) {
                            PuzzleChallenge.RELAXED -> it.tier == "familiar"
                            PuzzleChallenge.CLASSIC -> it.tier != "advanced"
                            PuzzleChallenge.EXPERT -> true
                        } }
                }.shuffled(rng)
            }
            val selected = mutableListOf<Pair<ReviewedWord, String>>()
            val order = pack.topicIds.shuffled(rng)
            for (slot in 0 until board.targets) {
                val topic = order[slot % order.size]
                val chosen = pools.getValue(topic).firstOrNull { word ->
                    selected.none { (other, _) ->
                        word.answer.contains(other.answer) || other.answer.contains(word.answer)
                    }
                } ?: break
                selected += chosen to topic
            }
            if (selected.size != board.targets) return@repeat
            val grid = Array(size) { CharArray(size) { ' ' } }
            val targets = mutableListOf<WordTarget>()
            val quadrants = IntArray(4)
            var failed = false
            for ((word, selectedTopic) in selected.sortedByDescending { it.first.answer.length }) {
                var best: List<GridPoint>? = null
                var bestScore = Int.MAX_VALUE
                repeat(400) candidate@{
                    val (dr, dc) = directions[rng.nextInt(directions.size)]
                    val r = rng.nextInt(size)
                    val c = rng.nextInt(size)
                    if (r + dr * (word.answer.length - 1) !in 0 until size ||
                        c + dc * (word.answer.length - 1) !in 0 until size) return@candidate
                    val path = List(word.answer.length) { i -> GridPoint(r + i * dr, c + i * dc) }
                    if (path.withIndex().any { (i, p) -> grid[p.row][p.col] != ' ' &&
                            grid[p.row][p.col] != word.answer[i] }) return@candidate
                    val middle = path[path.size / 2]
                    val q = (if (middle.row >= size / 2) 2 else 0) +
                        if (middle.col >= size / 2) 1 else 0
                    val overlap = path.count { grid[it.row][it.col] != ' ' }
                    val score = quadrants[q] * 8 +
                        (if (challenge == PuzzleChallenge.EXPERT) -overlap else overlap) + rng.nextInt(4)
                    if (score < bestScore) { bestScore = score; best = path }
                }
                val path = best
                if (path == null) { failed = true; break }
                path.forEachIndexed { i, p -> grid[p.row][p.col] = word.answer[i] }
                val middle = path[path.size / 2]
                quadrants[(if (middle.row >= size / 2) 2 else 0) +
                    (if (middle.col >= size / 2) 1 else 0)]++
                targets += WordTarget(word.id, word.answer, path, word.display, selectedTopic)
            }
            if (failed) return@repeat
            for (r in grid.indices) for (c in grid[r].indices) if (grid[r][c] == ' ')
                grid[r][c] = FILLER[rng.nextInt(FILLER.length)]
            val puzzle = WordSearchPuzzle(
                puzzleId(pack.id, pack.sha256, theme, board, challenge, seed), seed,
                CONTENT_VERSION, theme, challenge.legacyDifficulty,
                grid.map { it.concatToString() }, targets, pack.title
            )
            if (targets.any { occurrences(puzzle, it.word) != 1 }) return@repeat
            return FrozenWordSearch(pack.id, pack.sha256, board, challenge, puzzle)
        }
        error("Could not generate a reviewed-pack puzzle within the bounded attempts")
    }

    internal fun occurrences(puzzle: WordSearchPuzzle, word: String): Int {
        var matches = 0
        for (r in 0 until puzzle.size) for (c in 0 until puzzle.size)
            for ((dr, dc) in allDirections) {
                if (word.indices.all { i ->
                    val y = r + dr * i; val x = c + dc * i
                    y in puzzle.rows.indices && x in puzzle.rows[y].indices && puzzle.rows[y][x] == word[i]
                })
                    matches++
            }
        return matches
    }
}
