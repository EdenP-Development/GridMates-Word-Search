package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.BookAlbumProgress

@Composable
internal fun BookAlbumScreen(
    album: BookAlbumProgress,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().padding(horizontal = 18.dp)) {
        Ws15BackHeader("Your Library", onBack, "Back to book")
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(top = 4.dp, bottom = 16.dp)
        ) {
            BookAlbumContent(album)
        }
    }
}

@Composable
internal fun BookAlbumContent(album: BookAlbumProgress) {
    Column(
        Modifier.fillMaxWidth().testTag("book-album-content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GridMatesInfoPanel(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BookArt(album.theme, BookArtKind.EMBLEM, Modifier.size(52.dp), fieldNotes = true,
                    pageAccentCandidate = true)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${album.title} album", color = Forest,
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleLarge)
                    Text("${album.earnedCount} of ${album.stamps.size} stamps earned",
                        color = Muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
            PaperProgress(album.earnedCount, album.stamps.size)
            Text(album.next?.let { "Next stamp · ${it.goal.title}" } ?: "Album complete",
                color = Forest, style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.testTag("book-album-next"))
        }

        val artwork = listOf(BookArtKind.EMBLEM, BookArtKind.CHAPTER,
            BookArtKind.BOOKMARK, BookArtKind.COMPLETION)
        album.stamps.forEachIndexed { index, stamp ->
            GridMatesInfoPanel(Modifier.fillMaxWidth().testTag("book-stamp-${stamp.goal.id}")) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BookArt(album.theme, artwork.getOrElse(index) { BookArtKind.COMPLETION },
                        Modifier.size(46.dp).alpha(if (stamp.earned) 1f else .28f),
                        fieldNotes = true)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(stamp.goal.title, color = Forest,
                            style = MaterialTheme.typography.titleMedium)
                        Text(stamp.goal.requirement, color = Muted,
                            style = MaterialTheme.typography.bodySmall)
                        Text(if (stamp.earned) "Earned"
                            else "${stamp.completed} of ${stamp.goal.target} pages",
                            color = Forest, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
internal fun BookAlbumLink(
    album: BookAlbumProgress,
    onClick: () -> Unit,
    newlyEarned: String? = null
) {
    GridMatesIllustratedFrame(
        Modifier.fillMaxWidth().heightIn(min = 52.dp).testTag("book-album-link"),
        variant = GridMatesFrameVariant.MEDIUM, onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BookArt(album.theme, BookArtKind.EMBLEM, Modifier.size(32.dp), fieldNotes = true,
                pageAccentCandidate = true)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("${album.title} album", color = Forest,
                    style = MaterialTheme.typography.titleSmall)
                Text(newlyEarned?.let { "Stamp earned · $it" }
                    ?: "${album.earnedCount} of ${album.stamps.size} stamps earned",
                    modifier = if (newlyEarned != null) Modifier.semantics {
                        liveRegion = LiveRegionMode.Polite
                    } else Modifier,
                    color = Muted, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
