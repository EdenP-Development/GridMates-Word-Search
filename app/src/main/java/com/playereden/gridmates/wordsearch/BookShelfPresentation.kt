package com.playereden.gridmates.wordsearch

import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookVolumeProgress

/** A display catalog for the shelf and chapter list. Page ownership stays with the saved catalog. */
internal class BookShelfPresentation(source: BookCatalog, previewBookIds: Set<String>) {
    private val primaryByTheme = source.books.filterNot { it.id in previewBookIds }
        .distinctBy { it.theme }.associateBy { it.theme }

    private val displayIds = source.books.associate { book ->
        val displayBook = if (book.id in previewBookIds) primaryByTheme[book.theme] ?: book else book
        book.id to displayBook.id
    }
    private val editions = source.books.groupBy { displayIds.getValue(it.id) }
    private val pageOwners = buildMap {
        for (book in source.books) for (page in source.pages(book.id)) put(page.id, book.id)
    }

    val catalog = BookCatalog(editions.map { (id, books) ->
        val primary = requireNotNull(source.book(id))
        if (books.size == 1) primary else {
            val ordered = listOf(primary) + books.filterNot { it.id == id }
            val chapters = ordered.flatMap { it.chapters }.groupBy { it.title }.values.map { matching ->
                matching.first().copy(pages = matching.flatMap { it.pages })
            }
            primary.copy(chapters = chapters)
        }
    })

    fun displayBookId(bookId: String): String? = displayIds[bookId]

    fun ownerOf(pageId: String): String? = pageOwners[pageId]

    fun editionIds(bookId: String): Set<String> =
        editions[bookId].orEmpty().mapTo(linkedSetOf()) { it.id }

    fun bookmarks(raw: BookLibraryProgress, bookId: String): Set<String> =
        editionIds(bookId).mapNotNullTo(linkedSetOf()) { raw.volumes[it]?.bookmarkedPageId }

    fun progress(
        raw: BookLibraryProgress,
        startedPages: Set<String> = emptySet(),
        preferredEdition: String? = null
    ): BookLibraryProgress = BookLibraryProgress(buildMap {
        for ((id, books) in editions) {
            val entries = books.mapNotNull { raw.volumes[it.id] }
            if (entries.isEmpty()) continue
            val done = entries.flatMap { it.completedPageIds }.toSet()
            val bookmarks = entries.mapNotNull { it.bookmarkedPageId }
            val preferred = entries.firstOrNull { it.bookId == preferredEdition }?.bookmarkedPageId
            put(id, BookVolumeProgress(id, done,
                preferred ?: bookmarks.firstOrNull { it in startedPages } ?: bookmarks.firstOrNull()))
        }
    })
}
