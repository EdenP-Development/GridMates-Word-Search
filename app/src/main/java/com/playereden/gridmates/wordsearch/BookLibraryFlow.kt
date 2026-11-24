package com.playereden.gridmates.wordsearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookProgressRules

/**
 * WS-11D2B: actual internal route from Library -> Book -> Page -> Book.
 *
 * The public Home screen does not instantiate this route before editorially
 * approved pages, source hashes, rights and device QA pass. The same host may
 * be exercised with synthetic pages in Android instrumentation tests.
 *
 * No duplicate book data, navigation libraries or hidden SoloStore routing.
 */
@Composable
internal fun BookLibraryFlow(
    catalog: BookCatalog,
    progressStore: BookLibraryStore,
    sessionStore: BookPageSessionStore,
    boardFontId: String,
    onExit: () -> Unit,
    showPrototypeArt: Boolean = false,
    /** Home may send an existing catalog book straight into the next safe page. */
    initialBookId: String? = null,
    safeContentPadding: PaddingValues = PaddingValues(0.dp),
    onBookProgressSaved: () -> Unit = {},
    presentation: BookShelfPresentation? = null
) {
    val shelf = presentation ?: remember(catalog) { BookShelfPresentation(catalog, BookQaPilot.editorialBookIds) }
    val initialBook = initialBookId?.let(shelf::displayBookId)
    val directFromHome = initialBook != null
    // Reuse published book and local progress validation; a fully solved book
    // enters details for replay/page choice instead of silently resetting.
    val initialPage = remember(shelf, initialBook, progressStore, sessionStore) {
        initialBook?.takeUnless { progressStore.blocked || sessionStore.blocked }
            ?.let { book ->
                runCatching {
                    val started = sessionStore.journal.sessions.mapTo(mutableSetOf()) { it.pageId }
                    BookProgressRules.continuePage(
                        shelf.catalog, shelf.progress(progressStore.progress, started), book, started
                    )
                }.getOrNull()?.id
            }
    }
    var bookId by rememberSaveable(initialBook) { mutableStateOf(initialBook) }
    var pageId by rememberSaveable(initialBook) { mutableStateOf(initialPage) }
    var update by remember(catalog) { mutableIntStateOf(0) }
    var feedback by remember(catalog) { mutableStateOf<String?>(null) }
    // QA-only in-memory style choice: does NOT affect book progress or saves.
    // The owner's current preference is the more detailed Study B, while
    // keeping Study A available as an immediate, lossless visual rollback.
    var fieldNotesArt by remember(catalog) { mutableStateOf(showPrototypeArt) }
    // WS-16F: Home is the swipe carousel; Full Library defaults to the
    // multi-book physical bookcase instead of duplicating the Home showcase.
    // Preview options still allow the older swipe view for QA comparison.
    var horizontalLibrary by remember(catalog) { mutableStateOf(false) }
    // WS16BP: keep the real bookcase page when a volume opens, then return
    // to its shelf; never persist a decorative future-book state to a save file.
    var cabinetPage by rememberSaveable { mutableIntStateOf(0) }
    var focusedBookId by remember(catalog) {
        mutableStateOf(shelf.catalog.books.firstOrNull()?.id)
    }

    val unavailable = progressStore.blocked || sessionStore.blocked
    val snapshot = remember(update, pageId, bookId) { progressStore.progress }
    val started = remember(update, pageId, bookId) {
        sessionStore.journal.sessions.mapTo(mutableSetOf()) { it.pageId }
    }
    val displayProgress = remember(shelf, snapshot, started) { shelf.progress(snapshot, started) }
    val shownBook = bookId?.let(shelf::displayBookId)

    if (unavailable) {
        BackHandler(onBack = onExit)
        Column(Modifier.fillMaxSize().padding(safeContentPadding).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(onClick = onExit) { Text("Back to Play") }
            PaperEyebrow("Your Library")
            Text(progressStore.warning ?: sessionStore.warning
                ?: "Book progress cannot be opened safely. Original files were preserved.")
        }
        return
    }

    when {
        pageId != null && shownBook != null -> {
            val activePage = requireNotNull(pageId)
            val owner = shelf.ownerOf(activePage)?.takeIf { shelf.displayBookId(it) == shownBook }
                ?: shownBook
            Box(Modifier.fillMaxSize().padding(safeContentPadding)) {
            BookPageGameplayScreen(
                catalog = catalog,
                bookId = owner,
                pageId = activePage,
                sessionStore = sessionStore,
                progressStore = progressStore,
                boardFontId = boardFontId,
                showPrototypeArt = showPrototypeArt,
                fieldNotesArt = fieldNotesArt,
                onBookProgressSaved = onBookProgressSaved,
                presentation = shelf,
                backLabel = if (directFromHome) "Back to Home" else "Back to book",
                onBack = {
                    if (directFromHome) onExit()
                    else {
                        pageId = null
                        update++
                    }
                },
                onNextPage = { nextBook, nextPage ->
                    if (shelf.displayBookId(nextBook) == shownBook &&
                        catalog.page(nextBook, nextPage) != null) {
                        pageId = nextPage
                        update++
                    }
                }
            )
            }
        }
        shownBook != null -> {
            BackHandler {
                if (directFromHome) onExit() else bookId = null
            }
            val currentBook = shownBook
            Column(Modifier.fillMaxSize().padding(safeContentPadding)) {
                if (feedback != null) {
                    Text(feedback.orEmpty(), modifier = Modifier.padding(12.dp))
                }
                PuzzleBookDetailScreen(
                    catalog = shelf.catalog,
                    progress = displayProgress,
                    bookId = currentBook,
                    onBack = {
                        if (directFromHome) onExit()
                        else { bookId = null; feedback = null }
                    },
                    onPlayPage = { selectedBook, selectedPage ->
                        if (selectedBook == currentBook &&
                            shelf.catalog.page(currentBook, selectedPage) != null) {
                            pageId = selectedPage
                            feedback = null
                        }
                    },
                    onBookmarkPage = { selectedBook, selectedPage ->
                        val owner = shelf.ownerOf(selectedPage)
                        if (selectedBook == currentBook && owner != null &&
                            shelf.displayBookId(owner) == currentBook &&
                            progressStore.bookmark(owner, selectedPage, shelf.editionIds(currentBook))) {
                            update++
                            feedback = "Bookmark saved for this book."
                        } else {
                            feedback = progressStore.warning
                                ?: "The bookmark couldn't be saved. Your last progress was kept."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    showPrototypeArt = showPrototypeArt,
                    fieldNotesArt = fieldNotesArt,
                    startedPageIds = started,
                    bookmarkedPageIds = shelf.bookmarks(snapshot, currentBook)
                )
            }
        }
        else -> {
            BackHandler(onBack = onExit)
            PuzzleBookshelfScreen(
                catalog = shelf.catalog,
                progress = displayProgress,
                showPrototypeArt = showPrototypeArt,
                fieldNotesArt = fieldNotesArt,
                onToggleArt = if (showPrototypeArt) {
                    { fieldNotesArt = !fieldNotesArt }
                } else null,
                showHorizontalBrowser = horizontalLibrary,
                initialCabinetPage = cabinetPage,
                onCabinetPageChanged = { cabinetPage = it },
                onToggleLayout = if (showPrototypeArt) {
                    { horizontalLibrary = !horizontalLibrary }
                } else null,
                initialFocusedBookId = focusedBookId,
                onFocusedBookChanged = { focusedBookId = it },
                onBack = onExit,
                safeContentPadding = safeContentPadding,
                onOpenBook = { selectedBook ->
                    if (shelf.catalog.book(selectedBook) != null) {
                        bookId = selectedBook
                        feedback = null
                    }
                }
            )
        }
    }
}
