package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme

internal object Ws66ChapterPreview {
    const val SOLAR = "ws66-v110-solar-system"
    const val CLEARINGS = "ws66-v110-quiet-clearings"

    fun append(context: Context, legacy: BookCatalog): BookCatalog = appendFrozenQaChapters(
        context, legacy, "ws66-v110",
        listOf(
            FrozenQaChapter(SOLAR, "solar-system", "Our Solar System", WordTheme.SPACE,
                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, volume = 2),
            FrozenQaChapter(CLEARINGS, "quiet-clearings", "Quiet Clearings", WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT, volume = 3)
        )
    )
}
