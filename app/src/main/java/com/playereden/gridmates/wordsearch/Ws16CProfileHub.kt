package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.WordSearchAchievements

/** A local-first profile hub, reached from the Home avatar.
 * Existing Collection/Records remain separate working destinations.
 */
@Composable
internal fun Ws16CProfileHub(
    store: SoloStore,
    onHome: () -> Unit,
    onCollection: () -> Unit,
    onRecords: () -> Unit,
    onMyPuzzles: () -> Unit
) {
    val profile = store.profile
    val unlockedCompanions = remember(profile) {
        WoodlandCollection.progress(profile).count { it.unlocked }
    }
    val earnedAchievements = remember(profile) {
        WordSearchAchievements.progress(profile).count { it.unlocked }
    }
    val displayName = profile.identity.displayName.trim().ifBlank { "Player" }
    val portraitId = profile.identity.portraitId
    val context = LocalContext.current
    val drawable = remember(context, portraitId) {
        if (WoodlandCollection.portraits.none { it.id == portraitId }) 0
        else context.resources.getIdentifier(portraitId, "drawable", context.packageName)
    }

    Ws15BackHeader(
        title = "Player profile",
        subtitle = "Your puzzles and collections",
        onBack = onHome,
        backLabel = "Back to Home"
    )
    GridMatesIllustratedFrame(
        modifier = Modifier.fillMaxWidth().testTag("ws16c-profile-summary"),
        variant = GridMatesFrameVariant.FEATURE
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(76.dp)
                .clip(CircleShape)
                .background(StorybookTokens.warmPaper),
                contentAlignment = Alignment.Center) {
                if (drawable != 0) {
                    Image(painter = painterResource(drawable),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(76.dp).clip(CircleShape))
                } else {
                    Canvas(Modifier.size(40.dp)) {
                        drawCircle(Forest,
                            radius = size.width * 0.16f,
                            center = Offset(size.width * .5f, size.height * .27f),
                            style = Stroke(2.dp.toPx()))
                        drawArc(Forest, 200f, 140f, false,
                            topLeft = Offset(size.width*.14f,size.height*.44f),
                            size = Size(size.width*.72f,size.height*.45f),
                            style = Stroke(2.dp.toPx()))
                    }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(displayName, color = Forest,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text("Your local GridMates profile", color = Muted,
                    style = MaterialTheme.typography.bodyMedium)
                // Two stable metric lines avoid orphaning "found" on small phones.
                Text("${profile.completions} puzzles completed", color = Ink,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("ws16bu-profile-completed"))
                Text("${profile.totalWords} words found", color = Ink,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("ws16bu-profile-words"))
            }
        }
        // The existing local portrait and achievement progression becomes
        // visible in the dossier without copying or changing saved data.
        Column(Modifier.fillMaxWidth()
            .padding(start = 13.dp, end = 13.dp, bottom = 13.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Companions collected", color = Forest,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge)
                Text("$unlockedCompanions / ${WoodlandCollection.portraits.size}",
                    color = Muted, style = MaterialTheme.typography.labelMedium)
            }
            PaperProgress(unlockedCompanions, WoodlandCollection.portraits.size,
                Modifier.testTag("ws26-profile-collection-progress"))
            Text("$earnedAchievements of ${WordSearchAchievements.definitions.size} achievements earned",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }

    Text("Explore", color = Forest,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                .testTag("ws16c-profile-collection"),
            variant = GridMatesFrameVariant.MEDIUM,
            onClick = onCollection
        ) {
            Box(Modifier.fillMaxWidth().heightIn(min = 65.dp)
                .padding(horizontal = 13.dp, vertical = 9.dp),
                contentAlignment = Alignment.CenterStart) {
                Ws15SectionHeading(
                    glyph = Ws15SectionGlyph.PORTRAIT,
                    title = "Portraits, collections & achievements",
                    subtitle = "Browse portraits, board fonts and rewards"
                )
            }
        }
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                .testTag("ws16c-profile-records"),
            variant = GridMatesFrameVariant.MEDIUM,
            onClick = onRecords
        ) {
            Box(Modifier.fillMaxWidth().heightIn(min = 65.dp)
                .padding(horizontal = 13.dp, vertical = 9.dp),
                contentAlignment = Alignment.CenterStart) {
                Ws15SectionHeading(
                    glyph = Ws15SectionGlyph.AWARD,
                    title = "Records & statistics",
                    subtitle = "Review your completed puzzles and results"
                )
            }
        }
        GridMatesIllustratedFrame(
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                .testTag("ws16c-profile-my-puzzles"),
            variant = GridMatesFrameVariant.MEDIUM,
            onClick = onMyPuzzles
        ) {
            Box(Modifier.fillMaxWidth().heightIn(min = 65.dp)
                .padding(horizontal = 13.dp, vertical = 9.dp),
                contentAlignment = Alignment.CenterStart) {
                Ws15SectionHeading(
                    glyph = Ws15SectionGlyph.JOURNAL,
                    title = "My Puzzles",
                    subtitle = "Continue or manage saved word searches"
                )
            }
        }
    }
    Text("Your profile and progress stay on this device unless you choose to export a backup.",
        color = Muted, style = MaterialTheme.typography.bodySmall)
}
