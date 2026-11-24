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
    /** A displayed book can combine pages that retain different owners in the journal. */
    fun progress(
        catalog: BookCatalog,
        bookId: String,
        goals: List<BookAlbumGoal>,
        awards: List<BookPageAward>,
        journal: BookLibraryProgress? = null,
        editionIds: Set<String> = setOf(bookId)
    ): BookAlbumProgress? {
        val book = catalog.book(bookId) ?: return null
        val ownedPages = buildMap {
            for (owner in editionIds + bookId) {
                for (page in catalog.pages(owner)) put(page.id, owner to page)
            }
        }
        if (goals.isEmpty() || goals.any { goal -> !ownedPages.keys.containsAll(goal.qualifyingPages) })
            return null

        val completed = buildSet {
            for ((owner, saved) in journal?.volumes.orEmpty()) {
                if (saved.bookId == owner) {
                    saved.completedPageIds.filterTo(this) { ownedPages[it]?.first == owner }
                }
            }
            for (award in awards) {
                val (owner, page) = ownedPages[award.pageId] ?: continue
                if (award.bookId == owner && award.puzzleId == page.puzzleId &&
                    award.boardSha256 == page.boardSha256) {
                    add(page.id)
                }
            }
        }

        val stamps = goals.map { goal ->
            val count = goal.qualifyingPages.count { it in completed }.coerceAtMost(goal.target)
            BookAlbumStamp(goal, count)
        }
        return BookAlbumProgress(bookId, book.title.removeSuffix(" · TEST BOOK"), book.theme, stamps)
    }
}
