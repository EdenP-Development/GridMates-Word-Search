package com.playereden.gridmates.wordsearch.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.MessageDigest

/** Exact board carried by new saves. A removed/updated pack cannot mutate a saved page. */
data class FrozenWordSearch(
    val packId: String, val packSha256: String,
    val board: PuzzleBoardSize, val challenge: PuzzleChallenge,
    val puzzle: WordSearchPuzzle
) {
    init {
        require(Regex("en-[a-z][a-z0-9-]{2,38}-v[1-9][0-9]*").matches(packId))
        require(Regex("[0-9a-f]{64}").matches(packSha256))
        require(board in PuzzleBoardSize.bookPresets && puzzle.seed >= 0)
        require(puzzle.contentVersion == 5 && puzzle.difficulty == challenge.legacyDifficulty)
        require(puzzle.id == WordSearchGeneratorV5.puzzleId(
            packId, packSha256, puzzle.theme, board, challenge, puzzle.seed))
        require(puzzle.displayTitle.length in 1..60 && puzzle.displayTitle.none(Char::isISOControl))
        require(puzzle.rows.size == board.cells && puzzle.rows.all {
            it.length == board.cells && it.all { c -> c in 'A'..'Z' }
        })
        require(puzzle.targets.size == board.targets)
        require(puzzle.targets.map { it.id }.distinct().size == board.targets)
        require(puzzle.targets.map { it.word }.distinct().size == board.targets)
        puzzle.targets.forEach { target ->
            require(Regex("[a-z][a-z0-9_-]{4,39}").matches(target.id))
            require(target.word.length in 3 until board.cells && target.word.all { it in 'A'..'Z' })
            require(target.display.length in 1..60 && target.display.none(Char::isISOControl))
            require(target.display.uppercase(java.util.Locale.ROOT).filter { it in 'A'..'Z' } == target.word)
            require(target.topicId != null && Regex("[a-z][a-z0-9-]{2,40}").matches(target.topicId))
            require(target.positions.size == target.word.length)
            require(WordSearchRules.line(target.positions.first(), target.positions.last(), board.cells) == target.positions)
            require(target.positions.mapNotNull(puzzle::letter).joinToString("") == target.word)
            require(WordSearchGeneratorV5.occurrences(puzzle, target.word) == 1)
        }
    }
}

/** Strict, bounded binary snapshot with a checksum, embedded in schema-6 JSON backups. */
object FrozenWordSearchCodec {
    private const val MAGIC = 0x474D5735
    private const val MAX_BYTES = 64_000
    fun encode(value: FrozenWordSearch): ByteArray {
        val output = ByteArrayOutputStream()
        DataOutputStream(output).use { out ->
            out.writeInt(MAGIC); out.writeInt(1)
            out.writeUTF(value.packId); out.writeUTF(value.packSha256)
            out.writeUTF(value.board.name); out.writeUTF(value.challenge.name)
            val p = value.puzzle
            out.writeUTF(p.id); out.writeLong(p.seed); out.writeUTF(p.theme.name)
            out.writeUTF(p.displayTitle); out.writeInt(p.size)
            p.rows.forEach(out::writeUTF)
            out.writeInt(p.targets.size)
            p.targets.forEach { t ->
                out.writeUTF(t.id); out.writeUTF(t.word); out.writeUTF(t.display)
                out.writeUTF(requireNotNull(t.topicId)); out.writeInt(t.positions.size)
                t.positions.forEach { out.writeInt(it.row); out.writeInt(it.col) }
            }
        }
        val payload = output.toByteArray()
        require(payload.size + 32 <= MAX_BYTES)
        return payload + MessageDigest.getInstance("SHA-256").digest(payload)
    }

    fun decode(bytes: ByteArray): FrozenWordSearch {
        require(bytes.size in 64..MAX_BYTES)
        val payload = bytes.copyOf(bytes.size - 32)
        require(MessageDigest.isEqual(bytes.copyOfRange(payload.size, bytes.size),
            MessageDigest.getInstance("SHA-256").digest(payload)))
        DataInputStream(ByteArrayInputStream(payload)).use { input ->
            require(input.readInt() == MAGIC && input.readInt() == 1)
            val id = input.readUTF(); val hash = input.readUTF()
            val board = PuzzleBoardSize.valueOf(input.readUTF())
            val challenge = PuzzleChallenge.valueOf(input.readUTF())
            require(board in PuzzleBoardSize.bookPresets)
            val puzzleId = input.readUTF(); val seed = input.readLong()
            val theme = WordTheme.valueOf(input.readUTF()); val title = input.readUTF()
            require(input.readInt() == board.cells)
            val rows = List(board.cells) { input.readUTF() }
            require(input.readInt() == board.targets)
            val targets = List(board.targets) {
                val wordId = input.readUTF(); val word = input.readUTF()
                val display = input.readUTF(); val topic = input.readUTF()
                val count = input.readInt()
                require(count in 3 until board.cells)
                WordTarget(wordId, word, List(count) { GridPoint(input.readInt(), input.readInt()) }, display, topic)
            }
            require(input.available() == 0)
            return FrozenWordSearch(id, hash, board, challenge, WordSearchPuzzle(
                puzzleId, seed, 5, theme, challenge.legacyDifficulty, rows, targets, title))
        }
    }
}
