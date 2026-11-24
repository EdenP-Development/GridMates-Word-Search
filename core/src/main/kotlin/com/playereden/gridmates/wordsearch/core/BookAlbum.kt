package com.playereden.gridmates.wordsearch.core

/** The qualifying page IDs are fixed for this edition's reward. */
data class BookAlbumGoal(
    val id: String,
    val title: String,
    val requirement: String,
    val qualifyingPages: Set<String>,
    val target: Int = qualifyingPages.size
) {
    init { require(target in 1..qualifyingPages.size) }
}

data class BookAlbumStamp(val goal: BookAlbumGoal, val completed: Int) {
    val earned: Boolean get() = completed >= goal.target
}

data class BookAlbumProgress(
    val bookId: String,
    val title: String,
    val theme: WordTheme,
    val stamps: List<BookAlbumStamp>
) {
    val earnedCount: Int get() = stamps.count { it.earned }
    val next: BookAlbumStamp? get() = stamps.firstOrNull { !it.earned }
}

object BookAlbumRules {
    /** Read the existing durable page ledger so upgrades and restores credit earlier solves. */
    fun progress(
        catalog: BookCatalog,
        bookId: String,
        goals: List<BookAlbumGoal>,
        awards: List<BookPageAward>
    ): BookAlbumProgress? {
        val book = catalog.book(bookId) ?: return null
        val pages = catalog.pages(bookId).associateBy { it.id }
        if (goals.isEmpty() || goals.any { goal -> !pages.keys.containsAll(goal.qualifyingPages) })
            return null

        val completed = awards.asSequence().filter { award ->
            val page = pages[award.pageId]
            award.bookId == bookId && page != null &&
                page.puzzleId == award.puzzleId && page.boardSha256 == award.boardSha256
        }.map { it.pageId }.toSet()

        val stamps = goals.map { goal ->
            val count = goal.qualifyingPages.count { it in completed }.coerceAtMost(goal.target)
            BookAlbumStamp(goal, count)
        }
        return BookAlbumProgress(bookId, book.title.removeSuffix(" · TEST BOOK"), book.theme, stamps)
    }
}
