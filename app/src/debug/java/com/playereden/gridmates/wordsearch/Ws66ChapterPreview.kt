package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookBoardDigest
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookChapter
import com.playereden.gridmates.wordsearch.core.BookPage
import com.playereden.gridmates.wordsearch.core.FrozenWordSearchCodec
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleBook
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme

internal object Ws66ChapterPreview {
    const val SOLAR = "ws66-v110-solar-system"
    const val CLEARINGS = "ws66-v110-quiet-clearings"
    private const val root = "ws66-v110"

    fun append(context: Context, legacy: BookCatalog): BookCatalog {
        val index = context.assets.open("$root/boards.tsv").bufferedReader().use {
            it.readLines()
        }
        require(index.first() == "snapshot\tboard_sha256" && index.size == 7)
        val hashes = index.drop(1).associate { row ->
            val fields = row.split('\t')
            require(fields.size == 2)
            fields[0] to fields[1]
        }
        require(hashes.size == 6)

        fun book(
            id: String, key: String, title: String, theme: WordTheme,
            size: PuzzleBoardSize, challenge: PuzzleChallenge, volume: Int
        ): PuzzleBook {
            val pages = (1..3).map { page ->
                val snapshot = "$key-$page.bin"
                val frozen = context.assets.open("$root/$snapshot").use {
                    FrozenWordSearchCodec.decode(it.readBytes())
                }
                val board = frozen.puzzle
                val digest = BookBoardDigest.sha256(board)
                require(digest == hashes.getValue(snapshot) && board.theme == theme &&
                    frozen.board == size && frozen.challenge == challenge) {
                    "Preview page $snapshot differs from its manifest"
                }
                BookPage("$id-page-$page", board.id, board.contentVersion, frozen.packId,
                    size, challenge, board.seed, digest, frozen)
            }
            return PuzzleBook(id, title, volume, theme,
                listOf(BookChapter("$id-chapter", title, pages)))
        }

        return BookCatalog(legacy.books + listOf(
            book(SOLAR, "solar-system", "Our Solar System", WordTheme.SPACE,
                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, volume = 2),
            book(CLEARINGS, "quiet-clearings", "Quiet Clearings", WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT, volume = 3)
        )).also(legacy::requireCompatibleUpgrade)
    }
}
