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

internal data class FrozenQaChapter(
    val id: String,
    val key: String,
    val title: String,
    val theme: WordTheme,
    val size: PuzzleBoardSize,
    val challenge: PuzzleChallenge,
    val volume: Int
)

internal fun appendFrozenQaChapters(
    context: Context,
    legacy: BookCatalog,
    assetRoot: String,
    chapters: List<FrozenQaChapter>
): BookCatalog {
    val index = context.assets.open("$assetRoot/boards.tsv").bufferedReader().use { it.readLines() }
    require(index.firstOrNull() == "snapshot\tboard_sha256" && index.size == 1 + chapters.size * 3)
    val hashes = index.drop(1).associate { row ->
        val fields = row.split('\t')
        require(fields.size == 2)
        fields[0] to fields[1]
    }
    require(hashes.size == chapters.size * 3)

    val books = chapters.map { chapter ->
        val pages = (1..3).map { page ->
            val snapshot = "${chapter.key}-$page.bin"
            val frozen = context.assets.open("$assetRoot/$snapshot").use {
                FrozenWordSearchCodec.decode(it.readBytes())
            }
            val board = frozen.puzzle
            val digest = BookBoardDigest.sha256(board)
            require(digest == hashes.getValue(snapshot) && board.theme == chapter.theme &&
                frozen.board == chapter.size && frozen.challenge == chapter.challenge) {
                "Preview page $assetRoot/$snapshot differs from its manifest"
            }
            BookPage("${chapter.id}-page-$page", board.id, board.contentVersion, frozen.packId,
                chapter.size, chapter.challenge, board.seed, digest, frozen)
        }
        PuzzleBook(chapter.id, chapter.title, chapter.volume, chapter.theme,
            listOf(BookChapter("${chapter.id}-chapter", chapter.title, pages)))
    }
    return BookCatalog(legacy.books + books).also(legacy::requireCompatibleUpgrade)
}
