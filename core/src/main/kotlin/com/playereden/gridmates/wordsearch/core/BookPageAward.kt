package com.playereden.gridmates.wordsearch.core

/** One durable reward per immutable page. Unknown historical time/assistance is never invented. */
data class BookPageAward(val bookId: String, val pageId: String, val puzzleId: String,
                         val boardSha256: String, val wordCount: Int) {
    val key: String get() = "$bookId/$pageId"
    fun validate(): BookPageAward = apply {
        val safe = Regex("[a-z][a-z0-9-]{2,63}")
        require(safe.matches(bookId) && safe.matches(pageId))
        require(puzzleId.startsWith("wordsearch:") && puzzleId.length in 15..220)
        require(Regex("[0-9a-f]{64}").matches(boardSha256) && wordCount in 1..44)
    }
}

object BookRewardRules {
    fun validate(awards: List<BookPageAward>): List<BookPageAward> {
        require(awards.size <= 12_000 && awards.map { it.key }.distinct().size == awards.size)
        awards.forEach { it.validate() }
        return awards
    }

    fun award(profile: SoloProfile, catalog: BookCatalog, progress: BookLibraryProgress,
              bookId: String, pageId: String): SoloProfile {
        require(pageId in progress.volumes[bookId]?.completedPageIds.orEmpty())
        val page = requireNotNull(catalog.page(bookId, pageId))
        val puzzle = BookPageSessionRules.puzzle(catalog, bookId, pageId)
        require(catalog.matchesPublishedPuzzle(bookId, pageId, puzzle))
        val entry = BookPageAward(bookId, pageId, page.puzzleId, page.boardSha256, puzzle.targets.size).validate()
        profile.bookAwards.firstOrNull { it.key == entry.key }?.let {
            require(it == entry) { "Rewarded page identity changed" }
            return profile
        }
        return profile.copy(bookAwards = validate(profile.bookAwards + entry))
    }
}
