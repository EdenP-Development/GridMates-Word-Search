package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookAlbumProgress
import com.playereden.gridmates.wordsearch.core.WordTheme

@Composable
internal fun BookPageCompletionScreen(
    bookTitle: String,
    theme: WordTheme,
    pageNumber: Int,
    wordCount: Int,
    completedPages: Int,
    pageCount: Int,
    nextPageNumber: Int?,
    status: String,
    backLabel: String,
    showPrototypeArt: Boolean,
    fieldNotesArt: Boolean,
    onBack: () -> Unit,
    onNext: (() -> Unit)?,
    onReview: () -> Unit,
    onPractice: () -> Unit,
    bookAlbum: BookAlbumProgress? = null,
    onOpenAlbum: (() -> Unit)? = null,
    newlyEarned: String? = null
) {
    Box(
        Modifier.fillMaxSize().testTag("book-page-result"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier.widthIn(max = 480.dp).fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PuzzleTitleBar(
                title = bookTitle,
                subtitle = "Page $pageNumber",
                compact = true,
                leading = { BookPageBackButton(onBack = onBack, label = backLabel) },
                actions = {}
            )
            GridMatesIllustratedFrame(
                Modifier.fillMaxWidth(), variant = GridMatesFrameVariant.PANEL
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (showPrototypeArt) {
                        BookArt(theme, BookArtKind.COMPLETION, Modifier.size(64.dp),
                            fieldNotes = fieldNotesArt)
                    }
                    Text(
                        if (completedPages == pageCount) "Book complete" else "Page complete",
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.headlineMedium, color = Forest,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "$wordCount of $wordCount words found",
                        style = MaterialTheme.typography.bodyLarge, color = Ink,
                        textAlign = TextAlign.Center
                    )
                    PaperProgress(completedPages, pageCount, Modifier.widthIn(max = 290.dp))
                    Text(
                        "$completedPages of $pageCount pages completed",
                        style = MaterialTheme.typography.bodyMedium, color = Muted,
                        textAlign = TextAlign.Center
                    )
                    if (status.isNotBlank()) {
                        Text(status, style = MaterialTheme.typography.bodyMedium, color = Muted,
                            textAlign = TextAlign.Center)
                    }
                }
            }
            GridMatesIllustratedPill(
                label = when {
                    onNext == null -> backLabel
                    nextPageNumber == pageNumber + 1 -> "Next page"
                    else -> "Continue to page $nextPageNumber"
                },
                onClick = onNext ?: onBack,
                modifier = Modifier.fillMaxWidth().testTag("book-result-primary")
            )
            if (bookAlbum != null && onOpenAlbum != null) {
                BookAlbumLink(bookAlbum, onOpenAlbum, newlyEarned)
            }
            OutlinedButton(
                onClick = onReview,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("book-result-review")
            ) {
                Text("Review completed board", textAlign = TextAlign.Center)
            }
            TextButton(
                onClick = onPractice,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("book-result-practice")
            ) {
                Text("Practice again", color = Forest, textAlign = TextAlign.Center)
            }
        }
    }
}
