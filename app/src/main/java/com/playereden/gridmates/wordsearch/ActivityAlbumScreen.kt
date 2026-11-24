package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.ActivityAlbumProgress
import com.playereden.gridmates.wordsearch.core.ActivityAlbumRules
import com.playereden.gridmates.wordsearch.core.PuzzleBoardSize

@Composable
internal fun ActivityAlbumLink(
    album: ActivityAlbumProgress,
    onClick: () -> Unit,
    newlyEarned: String? = null
) {
    AlbumLink("Activity album", album.earnedCount, album.stamps.size, onClick,
        Modifier.testTag("activity-album-link"), newlyEarned) {
        Ws16BActionArt(Ws16BArt.DAILY, iconSize = 34.dp)
    }
}

@Composable
internal fun ActivityAlbumContent(album: ActivityAlbumProgress) {
    Column(
        Modifier.fillMaxWidth().testTag("activity-album-content"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GridMatesInfoPanel(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Ws16BActionArt(Ws16BArt.DAILY, iconSize = 52.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Activity album", color = Forest,
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleLarge)
                    Text("${album.earnedCount} of ${album.stamps.size} stamps earned",
                        color = Muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
            PaperProgress(album.earnedCount, album.stamps.size)
            Text(album.next?.let { "Next stamp · ${it.title}" } ?: "Album complete",
                color = Forest, style = MaterialTheme.typography.labelLarge)
        }
        album.stamps.forEach { stamp ->
            GridMatesInfoPanel(Modifier.fillMaxWidth().testTag("activity-stamp-${stamp.id}")) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Ws29AchievementMedal(stamp.id, stamp.unlocked, size = 48.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stamp.title, color = Forest, style = MaterialTheme.typography.titleMedium)
                        Text(stamp.description, color = Muted,
                            style = MaterialTheme.typography.bodyMedium)
                        val status = when {
                            stamp.unlocked -> "Earned"
                            stamp.id == ActivityAlbumRules.ALL_SIZES -> {
                                val remaining = PuzzleBoardSize.bookPresets
                                    .filterNot { it in album.solvedQuickPlaySizes }
                                    .joinToString(", ") { "${it.cells}×${it.cells}" }
                                "Still to solve · $remaining"
                            }
                            else -> "${stamp.current} of ${stamp.target}"
                        }
                        Text(status, color = Forest, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
