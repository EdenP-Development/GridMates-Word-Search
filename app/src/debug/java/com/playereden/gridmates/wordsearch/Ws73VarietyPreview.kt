package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize
import com.playereden.gridmates.wordsearch.core.PuzzleChallenge
import com.playereden.gridmates.wordsearch.core.WordTheme

internal object Ws73VarietyPreview {
    const val SOLAR = "ws73-v116-solar-system"
    const val CLEARINGS = "ws73-v116-quiet-clearings"

    fun append(context: Context, legacy: BookCatalog): BookCatalog = appendFrozenQaChapters(
        context, legacy, "ws73-v116",
        listOf(
            FrozenQaChapter(SOLAR, "solar-system", "Our Solar System · Relaxed", WordTheme.SPACE,
                PuzzleBoardSize.BOOK_SHORT, PuzzleChallenge.RELAXED, volume = 3),
            FrozenQaChapter(CLEARINGS, "quiet-clearings", "Quiet Clearings · Expert", WordTheme.WOODLAND,
                PuzzleBoardSize.BOOK_BIG, PuzzleChallenge.EXPERT, volume = 4)
        )
    )
}
