package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.PuzzleBook

@Composable
internal fun BookChaptersAction(onClick: () -> Unit) {
    val modifier = Modifier.testTag("book-chapters")
    val iconOnly = LocalDensity.current.fontScale >= 1.3f ||
        LocalConfiguration.current.screenWidthDp < 330
    if (iconOnly) {
        PuzzleIconAction("Chapters", onClick = onClick, modifier = modifier) {
            Canvas(Modifier.size(24.dp)) {
                val width = size.width
                for (row in listOf(.25f, .50f, .75f)) {
                    val y = size.height * row
                    drawCircle(Forest, radius = 1.3.dp.toPx(), center = Offset(width * .18f, y))
                    drawLine(
                        Forest, Offset(width * .36f, y), Offset(width * .83f, y),
                        strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round
                    )
                }
            }
        }
    } else {
        PuzzleTextAction("Chapters", onClick, modifier = modifier)
    }
}

@Composable
internal fun BookChapterPicker(
    book: PuzzleBook,
    completedPages: Set<String>,
    onDismiss: () -> Unit,
    onChapter: (Int) -> Unit
) {
    GridMatesPaperDialog("Chapters", onDismiss, Modifier.testTag("book-chapter-picker")) {
        var firstPage = 1
        book.chapters.forEachIndexed { index, chapter ->
            val lastPage = firstPage + chapter.pages.lastIndex
            val pageRange = if (firstPage == lastPage) "Page $firstPage"
                else "Pages $firstPage–$lastPage"
            val done = chapter.pages.count { it.id in completedPages }
            Row(
                Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(role = Role.Button) { onChapter(index) }
                    .testTag("book-chapter-jump-" + chapter.id)
                    .padding(horizontal = 10.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    (index + 1).toString(),
                    style = MaterialTheme.typography.titleSmall, color = Gold
                )
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        chapter.title.removeSuffix(" · TEST"),
                        style = MaterialTheme.typography.titleSmall, color = Forest
                    )
                    Text(
                        "$pageRange · $done/${chapter.pages.size} complete",
                        style = MaterialTheme.typography.labelMedium, color = Muted
                    )
                }
            }
            if (index < book.chapters.lastIndex) {
                HorizontalDivider(
                    Modifier.padding(horizontal = 10.dp),
                    color = Gold.copy(alpha = .24f)
                )
            }
            firstPage = lastPage + 1
        }
    }
}
