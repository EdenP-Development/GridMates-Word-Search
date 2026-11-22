package com.playereden.gridmates.wordsearch

import android.content.Context
import com.playereden.gridmates.wordsearch.core.BookCatalog

/**
 * RELEASE BUILD: no unpublished/temporary/test puzzle-book content ships.
 * An approved production manifest gets a separate, explicitly reviewed route.
 */
internal object BookQaPilot {
    const val enabled = false
    fun catalog(): BookCatalog = BookCatalog()
    @Suppress("UNUSED_PARAMETER")
    fun catalog(context: Context): BookCatalog = BookCatalog()
}
