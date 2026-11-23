package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookCatalog
import com.playereden.gridmates.wordsearch.core.BookLibraryProgress
import com.playereden.gridmates.wordsearch.core.BookProgressRules
import com.playereden.gridmates.wordsearch.core.PuzzleBook

/**
 * WS-11D: reusable, native Compose Bookshelf screens.
 *
 * The host must provide an editorially approved catalog and actual book-game
 * callback. No book, content, artwork or Solo puzzle is invented here.
 */
@Composable
internal fun PuzzleBookshelfScreen(
    catalog: BookCatalog,
    progress: BookLibraryProgress,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    modifier: Modifier = Modifier,
    safeContentPadding: PaddingValues = PaddingValues(0.dp),
    showPrototypeArt: Boolean = false,
    fieldNotesArt: Boolean = false,
    onToggleArt: (() -> Unit)? = null,
    showHorizontalBrowser: Boolean = false,
    initialCabinetPage: Int = 0,
    onCabinetPageChanged: (Int) -> Unit = {},
    onToggleLayout: (() -> Unit)? = null,
    initialFocusedBookId: String? = null,
    onFocusedBookChanged: (String) -> Unit = {}
) {
    val healthy = isBookStateReadable(catalog, progress)
    // The approved catalog, save rules and error screens stay identical.
    // Only the QA visual browse surface becomes an actual physical bookcase.
    if (showPrototypeArt && healthy && catalog.books.isNotEmpty()) {
        if (showHorizontalBrowser) {
            HorizontalBookBrowserScreen(
                catalog = catalog,
                progress = progress,
                onBack = onBack,
                onOpenBook = onOpenBook,
                fieldNotesArt = fieldNotesArt,
                onToggleArt = onToggleArt,
                onToggleLayout = onToggleLayout,
                initialFocusedBookId = initialFocusedBookId,
                onFocusedBookChanged = onFocusedBookChanged,
                modifier = modifier.padding(safeContentPadding)
            )
        } else {
            BookcaseBookshelfScreen(
                catalog = catalog,
                progress = progress,
                onBack = onBack,
                onOpenBook = onOpenBook,
                fieldNotesArt = fieldNotesArt,
                onToggleArt = onToggleArt,
                onToggleLayout = onToggleLayout,
                initialCabinetPage = initialCabinetPage,
                onCabinetPageChanged = onCabinetPageChanged,
                safeContentPadding = safeContentPadding,
                modifier = modifier
            )
        }
        return
    }
    LazyColumn(
        modifier = modifier.padding(safeContentPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "library-heading") {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Back to Play")
                }
                PaperEyebrow("GridMates · Word Search")
                Text("Your Library", color = Forest,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold)
                if (showPrototypeArt) {
                    Text("Prototype artwork · final illustrations can change",
                        color = Muted, style = MaterialTheme.typography.labelMedium)
                    Text(
                        if (fieldNotesArt) "Illustrated study B · Field Notes"
                        else "Original study A · Quiet Paperbacks",
                        color = Forest, style = MaterialTheme.typography.bodyMedium
                    )
                    if (onToggleArt != null) {
                        OutlinedButton(
                            onClick = onToggleArt,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) {
                            Text(if (fieldNotesArt) "Show original art (A)"
                                else "Try more illustrated art (B)")
                        }
                    }
                }
                Text("Find a book, choose a page, and pick up where you left off.",
                    color = Muted, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (!healthy) {
            item(key = "library-unreadable") {
                PaperPanel(Modifier.fillMaxWidth()) {
                    Text("Your saved book progress could not be opened.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Forest, fontWeight = FontWeight.Bold)
                    Text("Your previous data has not been reset. Book navigation is unavailable until it can be recovered.",
                        color = Ink, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else if (catalog.books.isEmpty()) {
            item(key = "library-empty") {
                PaperPanel(Modifier.fillMaxWidth()) {
                    Text("Our first puzzle books are being prepared",
                        color = Forest, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium)
                    Text("There are no finished books available yet. Free Play and Daily are still available.",
                        color = Muted, style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            items(catalog.books, key = { "book:" + it.id }) { book ->
                BookListCard(
                    book = book,
                    completed = BookProgressRules.completedCount(catalog, progress, book.id),
                    total = catalog.pages(book.id).size,
                    onOpen = { onOpenBook(book.id) },
                    showPrototypeArt = showPrototypeArt,
                    fieldNotesArt = fieldNotesArt
                )
            }
        }
    }
}

@Composable
private fun BookListCard(
    book: PuzzleBook, completed: Int, total: Int, onOpen: () -> Unit,
    showPrototypeArt: Boolean = false,
    fieldNotesArt: Boolean = false
) {
    PaperPanel(Modifier.fillMaxWidth()) {
        PaperEyebrow("Puzzle Book · Volume " + book.volume)
        if (showPrototypeArt) {
            // The actual source-generated cover AND spine now sit beside the
            // existing accessible title/status; the Open action stays native.
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                BookArt(book.theme, BookArtKind.COVER,
                    Modifier.width(102.dp).height(136.dp).testTag("book-cover-" + book.id), fieldNotes = fieldNotesArt)
                BookArt(book.theme, BookArtKind.SPINE,
                    Modifier.width(16.dp).height(104.dp).testTag("book-spine-" + book.id), fieldNotes = fieldNotesArt)
                Column(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(book.title.removeSuffix(" · TEST BOOK"),
                        color = Forest, fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge)
                    Text(if (book.title.endsWith(" · TEST BOOK"))
                            "TEST BOOK · " + book.chapters.size +
                                (if (book.chapters.size == 1) " chapter" else " chapters")
                        else book.theme.label + " · " + book.chapters.size +
                            (if (book.chapters.size == 1) " chapter" else " chapters"),
                        color = Muted, style = MaterialTheme.typography.bodyMedium)
                    Text(completed.toString() + " of " + total + " pages completed",
                        color = Ink, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Text(book.title, color = Forest, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge)
            Text(book.theme.label + " · " + book.chapters.size +
                (if (book.chapters.size == 1) " chapter" else " chapters"),
                color = Muted, style = MaterialTheme.typography.bodyMedium)
            Text(completed.toString() + " of " + total + " pages completed",
                color = Ink, style = MaterialTheme.typography.bodyMedium)
        }
        PaperProgress(completed, total)
        // The book thumbnail is the focal point; the native action remains
        // prominent but need not be a full-width block in every shelf card.
        if (showPrototypeArt) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onOpen,
                    modifier = Modifier.widthIn(min = 160.dp).heightIn(min = 48.dp)) {
                    Text(if (completed == total) "Read again" else if (completed > 0)
                        "Continue book" else "Open book")
                }
            }
        } else {
            Button(onClick = onOpen,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                Text(if (completed == total) "Read again" else if (completed > 0)
                    "Continue book" else "Open book")
            }
        }
    }
}

/** WS16BS: same progress/page content and callbacks, with a shared art skin. */
@Composable
private fun BookDetailCohesionPanel(
    illustrated: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    if (illustrated) {
        GridMatesIllustratedFrame(modifier = modifier,
            variant = GridMatesFrameVariant.PANEL) {
            Column(Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
        }
    } else {
        PaperPanel(modifier = modifier, content = content)
    }
}

/**
 * Real native book/chapter/page UI. Do not display a Play action until the
 * host supplies an actual book-puzzle implementation; a missing callback
 * yields accurate non-playable copy instead of a dead or misleading control.
 */
@Composable
internal fun PuzzleBookDetailScreen(
    catalog: BookCatalog,
    progress: BookLibraryProgress,
    bookId: String,
    onBack: () -> Unit,
    onPlayPage: ((bookId: String, pageId: String) -> Unit)? = null,
    onBookmarkPage: ((bookId: String, pageId: String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    showPrototypeArt: Boolean = false,
    fieldNotesArt: Boolean = false,
    startedPageIds: Set<String> = emptySet(),
    bookmarkedPageIds: Set<String> = emptySet()
) {
    val healthy = isBookStateReadable(catalog, progress)
    val book = catalog.book(bookId)
    val pages = if (healthy && book != null) catalog.pages(bookId) else emptyList()
    val completed = if (healthy && book != null)
        progress.volumes[bookId]?.completedPageIds.orEmpty() else emptySet()
    val next = if (healthy && book != null)
        BookProgressRules.continuePage(catalog, progress, bookId, startedPageIds) else null
    val bookmark = if (healthy && book != null)
        progress.volumes[bookId]?.bookmarkedPageId else null

    val listState = rememberLazyListState()
    val chapters = if (healthy) book?.chapters.orEmpty() else emptyList()
    var chaptersOpen by rememberSaveable(bookId) { mutableStateOf(false) }

    if (chaptersOpen && chapters.size > 1 && book != null) {
        BookChapterPicker(book, completed, onDismiss = { chaptersOpen = false }) { chapterIndex ->
            chaptersOpen = false
            // Two list entries precede the first chapter: the cover and progress panel.
            val previous = chapters.take(chapterIndex).sumOf { 1 + it.pages.size }
            listState.requestScrollToItem(2 + previous)
        }
    }

    Column(modifier.fillMaxSize()) {
        Ws15BackHeader(
            title = "Your Library",
            onBack = onBack,
            backLabel = "Back to Your Library",
            titleMaxLines = 2,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
            trailing = if (chapters.size > 1) {
                {
                    BookChaptersAction { chaptersOpen = true }
                }
            } else null
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().testTag("book-detail-list"),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "book-heading") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (showPrototypeArt && book != null) {
                        // After picking a volume from a shelf it appears as a
                        // large, readable physical object, not a giant text card.
                        Row(
                            Modifier.fillMaxWidth().testTag("pulled-book-hero"),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // One coherent book silhouette: cover already has a
                            // left binding; never draw a separate tiny spine.
                            BookArt(book.theme, BookArtKind.COVER,
                                Modifier.width(112.dp).height(150.dp)
                                    .testTag("book-detail-cover-" + book.id),
                                fieldNotes = fieldNotesArt)
                            Column(Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(book.title.removeSuffix(" · TEST BOOK"),
                                    color = Forest,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold)
                                Text(
                                    "Volume " + book.volume + " · " +
                                        book.chapters.size +
                                        (if (book.chapters.size == 1) " chapter" else " chapters"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Muted
                                )
                                // QA status stays visible at the library entry, not
                                // stamped repeatedly on the illustrated detail hero.
                                BookArt(book.theme, BookArtKind.EMBLEM,
                                    Modifier.size(40.dp).testTag("book-emblem-" + book.id),
                                    fieldNotes = fieldNotesArt, pageAccentCandidate = true)
                            }
                        }
                    } else {
                        Text(
                            book?.title ?: "Book unavailable",
                            color = Forest, style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (!healthy || book == null) {
                item(key = "book-unavailable") {
                    BookDetailCohesionPanel(showPrototypeArt,
                        Modifier.fillMaxWidth().testTag("book-detail-unavailable-panel")) {
                        Text("This book cannot be opened right now.",
                            color = Forest, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Text("Saved progress has not been changed. Return to the Library.",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                item(key = "book-progress") {
                    BookDetailCohesionPanel(showPrototypeArt,
                        Modifier.fillMaxWidth().testTag("book-detail-progress-panel")) {
                        Text(
                            if (showPrototypeArt) "Book progress"
                            else book.theme.label + " · Volume " + book.volume,
                            color = Muted, style = MaterialTheme.typography.bodyMedium
                        )
                        Row(Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (showPrototypeArt && pages.isNotEmpty() &&
                                completed.size == pages.size) {
                                BookArt(book.theme, BookArtKind.COMPLETION,
                                    Modifier.size(48.dp).testTag("book-volume-complete-" + book.id), fieldNotes = fieldNotesArt)
                            }
                            Text(completed.size.toString() + " of " + pages.size +
                                " pages completed",
                                color = Ink, style = MaterialTheme.typography.bodyMedium)
                        }
                        PaperProgress(completed.size, pages.size)
                        if (next == null) {
                            Text("Book complete · You can revisit any page.",
                                color = Forest, style = MaterialTheme.typography.titleMedium)
                        } else if (onPlayPage != null) {
                            Button(
                                onClick = { onPlayPage(bookId, next.id) },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                        .testTag("book-continue")
                            ) {
                                Text(when {
                                    next.id in startedPageIds -> "Resume page " + (pages.indexOf(next) + 1)
                                    completed.isEmpty() -> "Start solving"
                                    else -> "Continue solving"
                                })
                            }
                        } else {
                            Text("Book pages will be playable after content approval.",
                                color = Muted, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                book.chapters.forEachIndexed { index, chapter ->
                    item(key = "chapter:" + chapter.id) {
                        Column(
                            Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                if (showPrototypeArt) {
                                    BookArt(book.theme, BookArtKind.EMBLEM,
                                        Modifier.size(36.dp).testTag("book-chapter-" + chapter.id),
                                        fieldNotes = fieldNotesArt, pageAccentCandidate = true)
                                }
                                Column(Modifier.weight(1f)) {
                                    val chapterDone = chapter.pages.count { it.id in completed }
                                    PaperEyebrow(
                                        if (showPrototypeArt)
                                            "Chapter " + (index + 1) + " · " +
                                                chapterDone + "/" + chapter.pages.size + " pages"
                                        else "Chapter " + (index + 1)
                                    )
                                    Text(
                                        if (showPrototypeArt) chapter.title.removeSuffix(" · TEST")
                                        else chapter.title,
                                        style = if (showPrototypeArt)
                                            MaterialTheme.typography.titleMedium
                                        else MaterialTheme.typography.titleLarge,
                                        color = Forest, fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (!showPrototypeArt) {
                                Text(chapter.pages.count { it.id in completed }.toString() +
                                    " of " + chapter.pages.size + " pages completed",
                                    color = Muted, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    items(chapter.pages, key = { "page:" + it.id }) { page ->
                        // Use book-wide page numbers. Chapter 2 must not restart
                        // at "Page 1" while Chapter 1's Page 1 also exists.
                        val ordinal = pages.indexOfFirst { it.id == page.id } + 1
                        val isComplete = page.id in completed
                        val isCurrent = page.id == next?.id
                        val inProgress = !isComplete && page.id in startedPageIds
                        val action = if (inProgress) "Resume" else "Play"
                        BookDetailCohesionPanel(showPrototypeArt,
                            Modifier.fillMaxWidth().testTag("book-detail-page-panel-" + page.id)) {
                            Row(Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (showPrototypeArt && !isComplete &&
                                    (bookmark == page.id || page.id in bookmarkedPageIds)) {
                                    BookArt(book.theme, BookArtKind.BOOKMARK,
                                        Modifier.width(17.dp).height(48.dp)
                                            .testTag("book-bookmark-" + page.id), fieldNotes = fieldNotesArt)
                                }
                                if (showPrototypeArt && isComplete) {
                                    BookArt(book.theme, BookArtKind.COMPLETION,
                                        Modifier.size(43.dp).testTag("book-page-complete-" + page.id), fieldNotes = fieldNotesArt)
                                }
                                Column(Modifier.weight(1f)) {
                                    Text("Page " + ordinal + when {
                                        isComplete -> " · Completed"
                                        inProgress -> " · In progress"
                                        isCurrent -> " · Next page"
                                        else -> " · Not started"
                                    },
                                        color = Forest,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(page.boardSize.cells.toString() + "×" +
                                        page.boardSize.cells + " · " + page.challenge.label,
                                        color = Muted,
                                        style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            // On a normal phone the two page actions share a row,
                            // avoiding a long pair of oversized buttons on every page.
                            // Stack again for 320dp and/or enlarged system text.
                            if (showPrototypeArt && !isComplete &&
                                onPlayPage != null && onBookmarkPage != null) {
                                BoxWithConstraints(Modifier.fillMaxWidth()) {
                                    val compactRow = maxWidth >= 270.dp &&
                                        LocalDensity.current.fontScale <= 1.25f
                                    if (compactRow) {
                                        Row(Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { onPlayPage(bookId, page.id) },
                                                modifier = Modifier.weight(0.44f).heightIn(min = 48.dp)
                                                    .semantics { contentDescription = "$action page $ordinal" }
                                            ) { Text(action, maxLines = 1) }
                                            OutlinedButton(
                                                onClick = { onBookmarkPage(bookId, page.id) },
                                                enabled = bookmark != page.id,
                                                modifier = Modifier.weight(0.56f).heightIn(min = 48.dp)
                                                    .semantics {
                                                        contentDescription = if (bookmark == page.id)
                                                            "Bookmark saved for page $ordinal"
                                                        else "Bookmark page $ordinal"
                                                    }
                                            ) {
                                                Text(if (bookmark == page.id) "Saved" else "Bookmark",
                                                    maxLines = 1)
                                            }
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { onPlayPage(bookId, page.id) },
                                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                                    .semantics { contentDescription = "$action page $ordinal" }
                                            ) { Text(action, maxLines = 1) }
                                            OutlinedButton(
                                                onClick = { onBookmarkPage(bookId, page.id) },
                                                enabled = bookmark != page.id,
                                                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                                    .semantics {
                                                        contentDescription = if (bookmark == page.id)
                                                            "Bookmark saved for page $ordinal"
                                                        else "Bookmark page $ordinal"
                                                    }
                                            ) {
                                                Text(if (bookmark == page.id) "Saved" else "Bookmark",
                                                    maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (onPlayPage != null) {
                                    Button(onClick = { onPlayPage(bookId, page.id) },
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                        Text(if (isComplete) "Replay page $ordinal"
                                            else "$action page $ordinal")
                                    }
                                }
                                if (!isComplete && onBookmarkPage != null) {
                                    OutlinedButton(
                                        onClick = { onBookmarkPage(bookId, page.id) },
                                        enabled = bookmark != page.id,
                                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                    ) {
                                        Text(if (bookmark == page.id) "Bookmark saved"
                                            else "Bookmark page $ordinal")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isBookStateReadable(
    catalog: BookCatalog, progress: BookLibraryProgress
): Boolean = try {
    BookProgressRules.validate(catalog, progress)
    true
} catch (_: IllegalArgumentException) {
    false
}
