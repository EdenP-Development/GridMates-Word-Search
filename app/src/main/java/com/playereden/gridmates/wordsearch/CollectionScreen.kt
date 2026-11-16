package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.playereden.gridmates.wordsearch.core.AchievementProgress
import com.playereden.gridmates.wordsearch.core.BoardFontCollection
import com.playereden.gridmates.wordsearch.core.BoardFontProgress
import com.playereden.gridmates.wordsearch.core.PortraitProgress
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.WordSearchAchievements

@Composable
internal fun CollectionScreen(store: SoloStore) {
    val profile = store.profile
    val portraits = remember(profile) { WoodlandCollection.progress(profile) }
    val achievements = remember(profile) { WordSearchAchievements.progress(profile) }
    val equipped = portraits.firstOrNull { it.equipped }
    val equippedName = equipped?.portrait?.label ?: "Owl"
    val equippedId = equipped?.portrait?.id ?: "woodland_owl"
    val context = LocalContext.current
    val originalImagesAvailable = remember(context) {
        WoodlandCollection.portraits.all { portrait ->
            context.resources.getIdentifier(
                portrait.id, "drawable", context.packageName
            ) != 0
        }
    }
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var draftName by remember(profile.identity.displayName) {
        mutableStateOf(profile.identity.displayName)
    }
    var collectionTab by rememberSaveable { mutableStateOf("Portraits") }
    var portraitPage by rememberSaveable { mutableIntStateOf(0) }
    var achievementPage by rememberSaveable { mutableIntStateOf(0) }
    // Page capacity must be based on actual *content* width, not screen width.
    val fontScale = LocalDensity.current.fontScale
    val achievementPageSize = if (fontScale >= 1.3f) 2 else 4
    var licenseFontId by rememberSaveable { mutableStateOf<String?>(null) }
    val completed = WoodlandCollection.completedCount(profile)
    val nextPortrait = portraits.firstOrNull { !it.unlocked }?.portrait
    val fonts = remember(profile, store.selectedBoardFontId) {
        BoardFontCollection.progress(profile, store.selectedBoardFontId)
    }

    if (licenseFontId != null) {
        val fontId = requireNotNull(licenseFontId)
        val label = BoardFontCollection.definitions.firstOrNull { it.id == fontId }?.label
            ?: "Typeface"
        val notice = remember(fontId) {
            runCatching {
                context.assets.open("font_licenses/${fontId}_ofl.txt")
                    .bufferedReader(Charsets.UTF_8).use { it.readText() }
            }.getOrElse { "License notice could not be opened." }
        }
        AlertDialog(
            onDismissRequest = { licenseFontId = null },
            title = { Text("$label · Open Font License") },
            text = {
                Column(Modifier.heightIn(max = 440.dp)
                    .verticalScroll(rememberScrollState())) {
                    Text(notice, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = { licenseFontId = null }) { Text("Close") }
            }
        )
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Your local profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your name is saved on this device and shown to a partner " +
                        "during Nearby Co-op. No account is needed.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = draftName,
                        onValueChange = { draftName = it.take(24) },
                        label = { Text("Display name") },
                        singleLine = true,
                        supportingText = { Text("Up to 24 characters") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    store.updateDisplayName(draftName)
                    showNameDialog = false
                }) { Text("Save name") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("Cancel") }
            }
        )
    }

    // The shell already titles this screen "Your Collection". A small
    // introduction saves space for actual portraits, fonts, and achievements.
    Text("Earn portraits and fonts by finishing puzzles. Everything is free.",
        modifier = Modifier.fillMaxWidth().testTag("ws16bb-collection-heading"),
        color = Muted, style = MaterialTheme.typography.bodyMedium)

    // WS25: real native illustrated tabs; live labels, selection and 48dp
    // targets stay in Compose rather than being baked into a sprite.
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        for (label in listOf("Portraits", "Fonts", "Achievements")) {
            val isSelected = collectionTab == label
            GridMatesIllustratedFrame(
                modifier = Modifier.heightIn(min = 48.dp)
                    .testTag("ws25-collection-tab-" + label.lowercase())
                    .semantics { selected = isSelected },
                variant = if (isSelected) GridMatesFrameVariant.GREEN_ICON
                    else GridMatesFrameVariant.MEDIUM,
                onClick = { collectionTab = label }
            ) {
                Box(Modifier.padding(horizontal = 13.dp, vertical = 13.dp),
                    contentAlignment = Alignment.Center) {
                    Text(label, color = if (isSelected) Paper else Forest,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    // The selected collection tab must open to its content immediately,
    // not below a full-page profile header. Keep profile/dossier in Portraits.
    if (collectionTab == "Portraits") {
        // WS27: the profile has its own full dossier. On regular phones,
        // keep only a short identity/edit strip so the first collectible row
        // actually appears when opening the Collection screen.
        GridMatesInfoPanel(Modifier.fillMaxWidth()
            .testTag("ws16bt-collection-profile")) {
            val roomy = fontScale < 1.3f
            if (roomy) {
                Row(Modifier.fillMaxWidth()
                    .testTag("ws27-compact-collection-identity"),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    PortraitBadge(equippedId, equippedName, 36)
                    Column(Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(profile.identity.displayName, color = Forest,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(equippedName + " · Equipped", color = Muted,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    TextButton(onClick = {
                        draftName = profile.identity.displayName
                        showNameDialog = true
                    }, modifier = Modifier.heightIn(min = 48.dp)
                        .testTag("ws16bu-edit-profile-name")) {
                        Text("Edit display name", color = Forest,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1)
                    }
                }
            } else {
                // Large type remains stacked; preserve an untruncated edit CTA.
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    PortraitBadge(equippedId, equippedName, 40)
                    Column(Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(profile.identity.displayName, color = Forest,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(equippedName + " · Equipped portrait", color = Muted,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
                TextButton(onClick = {
                    draftName = profile.identity.displayName
                    showNameDialog = true
                }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("ws16bu-edit-profile-name")) {
                    Text("Edit display name", color = Forest)
                }
            }
        }
    }

    if (collectionTab == "Fonts") {
        Ws15SectionHeading(
            glyph = Ws15SectionGlyph.TYPEFACE, title = "Board fonts",
            subtitle = fonts.count { it.unlocked }.toString() + " of " +
                fonts.size + " unlocked"
        )
        Text("Board fonts change puzzle letters only, not GridMates menus. " +
            "The readable starter is always free; earn other styles through achievements.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        BoxWithConstraints(Modifier.fillMaxWidth().testTag("ws16bv-font-gallery")) {
            // Five-letter font samples need real width to remain legible.
            // A tablet may show two cards; ordinary phones keep one column.
            val columns = if (maxWidth < 600.dp || fontScale >= 1.3f) 1 else 2
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                fonts.chunked(columns).forEach { pair ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { item ->
                            BoardFontCard(item, Modifier.weight(1f),
                                onEquip = { store.equipBoardFont(item.font.id) },
                                onLicense = { licenseFontId = item.font.id })
                        }
                        if (pair.size < columns) Box(Modifier.weight(1f))
                    }
                }
            }
        }
        Text("Every font is bundled for offline use under its own Open Font License. " +
            "Tap License to read the full copyright and redistribution terms.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    } else if (collectionTab == "Achievements") {
        Ws15SectionHeading(
            glyph = Ws15SectionGlyph.AWARD, title = "Achievements",
            subtitle = achievements.count { it.unlocked }.toString() +
                " of " + achievements.size + " earned"
        )
        Text("Achievements reflect your saved Solo and Daily progress.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
        val orderedAchievements = achievements.sortedWith(
            compareBy<AchievementProgress> { it.unlocked }.thenBy { it.target - it.current })
        val pages = ((orderedAchievements.size + achievementPageSize - 1) /
            achievementPageSize).coerceAtLeast(1)
        val current = achievementPage.coerceIn(0, pages - 1)
        // Put paging ahead of the long card stack: on narrow phones the
        // controls must remain discoverable immediately after changing tabs.
        CollectionPager(current, pages, id = "achievements", onChange = { achievementPage = it })
        orderedAchievements.drop(current * achievementPageSize).take(achievementPageSize)
            .forEach { item -> AchievementCard(item) }
    } else {
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                Ws15SectionHeading(
                    glyph = Ws15SectionGlyph.PORTRAIT,
                    title = "Woodland",
                    subtitle = portraits.count { it.unlocked }.toString() +
                        " of " + portraits.size + " companions unlocked"
                )
            }
            // Real source-hashed WS22 art, decorative and debug-only.
            if (fontScale < 1.3f) WoodlandCollectionAccent()
        }
        // Repeating explanatory copy above the gallery hid the artwork in v88.
        // Only show the warning when the original shipped portraits are missing.
        if (!originalImagesAvailable) {
            Text("Some portrait artwork is unavailable in this build. " +
                "Letter badges will appear instead.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        // A single BoxWithConstraints owns BOTH the page size and the card
        // layout. Avoid 4 jumbo rows on a narrow content-width viewport.
        BoxWithConstraints(Modifier.fillMaxWidth().testTag("ws16bu-gallery")) {
            val columns = if (maxWidth < 320.dp || fontScale >= 1.3f) 1 else 2
            val portraitPageSize = if (columns == 1) 3 else 4
            val portraitPages = ((portraits.size + portraitPageSize - 1) /
                portraitPageSize).coerceAtLeast(1)
            val current = portraitPage.coerceIn(0, portraitPages - 1)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CollectionPager(current, portraitPages, id = "portraits",
                    onChange = { portraitPage = it })
                portraits.drop(current * portraitPageSize).take(portraitPageSize)
                    .chunked(columns).forEach { group ->
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            group.forEach { item ->
                                val remaining = WoodlandCollection.remaining(
                                    profile, item.portrait.id) ?: 0
                                if (columns == 2) {
                                    PortraitTile(item, remaining, Modifier.weight(1f),
                                        onEquip = { store.equipPortrait(item.portrait.id) })
                                } else {
                                    PortraitCard(item, remaining, Modifier.weight(1f),
                                        onEquip = { store.equipPortrait(item.portrait.id) })
                                }
                            }
                            if (group.size < columns) Box(Modifier.weight(1f))
                        }
                    }
            }
        }
        Text("New companions unlock through completed Solo and Daily puzzles.",
            color = Muted, style = MaterialTheme.typography.bodySmall)

        // History/progress is still available, but after the actual gallery.
        // The portrait pager and equip choices come first on a phone.
        GridMatesInfoPanel(Modifier.fillMaxWidth().testTag("ws16bt-collection-progress")) {
            PaperEyebrow("Collection progress")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(portraits.count { it.unlocked }.toString() + " of " +
                        portraits.size + " portraits", color = Forest,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Text(achievements.count { it.unlocked }.toString() + " of " +
                        achievements.size + " achievements earned",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            PaperProgress(portraits.count { it.unlocked }, portraits.size)
            Text(
                if (nextPortrait == null) "You have earned every Woodland portrait."
                else "Next portrait: " + nextPortrait.label + " · " +
                    (nextPortrait.requiredCompletions - completed).coerceAtLeast(0) +
                    " more completed " + (if (nextPortrait.requiredCompletions - completed == 1)
                        "puzzle" else "puzzles") + " to unlock.",
                color = Muted, style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun BoardFontCard(
    item: BoardFontProgress,
    modifier: Modifier,
    onEquip: () -> Unit,
    onLicense: () -> Unit
) {
    // WS16BV: the glyph sample stays large, while status and legal access
    // no longer take two full-height rows of disabled fake buttons.
    Card(
        modifier.testTag("ws16bv-font-" + item.font.id),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = StorybookTokens.warmPaper),
        border = BorderStroke(if (item.equipped) 2.dp else 1.dp,
            if (item.equipped) Forest else Color(0xFFDFE0D6))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BoardFontPreview(item.font.id)
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(item.font.label, color = Forest,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (item.equipped) {
                    Text("Equipped", color = Forest,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.testTag("ws16bv-font-status-" + item.font.id))
                }
            }
            Text(
                if (item.unlocked) item.font.description else
                    "Unlock: " + (item.achievementName ?: "Achievement") +
                        " · " + item.font.description,
                color = Ink, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("ws16bv-font-detail-" + item.font.id)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.unlocked && !item.equipped) {
                    OutlinedButton(onClick = onEquip,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                            .testTag("ws16bv-font-equip-" + item.font.id)) {
                        Text("Equip")
                    }
                } else if (!item.unlocked) {
                    // A readable state label, not an inert gray button.
                    Text("Locked", color = Ink,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                            .testTag("ws16bv-font-status-" + item.font.id))
                }
                TextButton(onClick = onLicense,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        .testTag("ws16bv-font-license-" + item.font.id)) {
                    Text("License", color = Forest)
                }
            }
        }
    }
}

/** Source-pinned WS22 motif: debug art is optional; release has a native fallback. */
@Composable
private fun WoodlandCollectionAccent() {
    val context = LocalContext.current
    val resource = remember(context) {
        context.resources.getIdentifier(
            "gm_ws25_collection_robin", "drawable", context.packageName)
    }
    if (resource != 0) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(58.dp).testTag("ws25-collection-robin")
        )
    }
}

@Composable
private fun PortraitBadge(
    id: String, label: String, sizeDp: Int, unlocked: Boolean = true
) {
    val context = LocalContext.current
    val resource = remember(context, id) {
        context.resources.getIdentifier(id, "drawable", context.packageName)
    }
    if (resource == 0) {
        PortraitMonogram(label, sizeDp)
    } else {
        Box(
            Modifier.size(sizeDp.dp).clip(CircleShape)
                .background(ColorForestPale),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(resource),
                contentDescription = "$label Woodland portrait",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().alpha(if (unlocked) 1f else 0.45f)
            )
        }
    }
}

@Composable
private fun PortraitMonogram(label: String, sizeDp: Int) {
    Box(
        Modifier.size(sizeDp.dp).background(
            if (label == "Owl") ColorForestPale else ColorGoldPale,
            CircleShape
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(label.take(1).uppercase(), color = Forest,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
    }
}

private val ColorForestPale = androidx.compose.ui.graphics.Color(0xFFD9E7D9)
private val ColorGoldPale = androidx.compose.ui.graphics.Color(0xFFF3E3C3)

/**
 * WS26 gallery view for ordinary phone text size: the artwork is the hero.
 * Locked progress, equip actions and original stable portrait IDs are live.
 * Narrow phones / enlarged font scale continue to use PortraitCard below.
 */
@Composable
private fun PortraitTile(
    item: PortraitProgress,
    remaining: Int,
    modifier: Modifier,
    onEquip: () -> Unit
) {
    Card(
        modifier.testTag("ws16bu-portrait-" + item.portrait.id),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = StorybookTokens.warmPaper),
        border = BorderStroke(if (item.equipped) 2.dp else 1.dp,
            if (item.equipped) Forest else StorybookTokens.antiqueGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp)
                .testTag("ws26-portrait-tile-content"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            PortraitBadge(item.portrait.id, item.portrait.label, 68, item.unlocked)
            Text(item.portrait.label,
                color = Forest, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center)
            Text(when {
                item.equipped -> "Your portrait"
                item.unlocked -> "Unlocked"
                else -> "$remaining puzzles to unlock"
            }, color = Muted, style = MaterialTheme.typography.labelSmall,
                maxLines = 2, textAlign = TextAlign.Center)
            if (!item.unlocked && item.portrait.requiredCompletions > 0) {
                val earned = (item.portrait.requiredCompletions - remaining).coerceAtLeast(0)
                PaperProgress(earned, item.portrait.requiredCompletions)
            }
            // A tappable 48dp CTA only exists when this reward is earned.
            if (item.unlocked && !item.equipped) {
                OutlinedButton(onClick = onEquip,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .testTag("ws16bu-equip-" + item.portrait.id)) {
                    Text("Equip")
                }
            } else {
                Box(Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    contentAlignment = Alignment.Center) {
                    Text(if (item.equipped) "Equipped" else "Locked",
                        color = if (item.equipped) Forest else Muted,
                        style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun PortraitCard(
    item: PortraitProgress,
    remaining: Int,
    modifier: Modifier,
    onEquip: () -> Unit
) {
    // Compact illustrated collection row: portrait left, real metadata right.
    // Preserve the equipped border, unlock progress, and true equip action.
    Card(modifier.testTag("ws16bu-portrait-" + item.portrait.id),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = StorybookTokens.warmPaper),
        border = BorderStroke(if (item.equipped) 2.dp else 1.dp,
            if (item.equipped) Forest else Color(0xFFDFE0D6))
    ) {
        Column(Modifier.fillMaxWidth().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                PortraitBadge(item.portrait.id, item.portrait.label, 54, item.unlocked)
                Column(Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(item.portrait.label, color = Forest,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(when {
                        item.equipped -> "Equipped"
                        item.unlocked -> "Unlocked · Ready to equip"
                        else -> remaining.toString() + " more " +
                            (if (remaining == 1) "puzzle" else "puzzles") + " to unlock"
                    }, color = Ink, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (!item.unlocked && item.portrait.requiredCompletions > 0) {
                val completed = (item.portrait.requiredCompletions - remaining).coerceAtLeast(0)
                PaperProgress(completed, item.portrait.requiredCompletions)
            }
            if (item.unlocked && !item.equipped) {
                OutlinedButton(onClick = onEquip,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        .testTag("ws16bu-equip-" + item.portrait.id)) {
                    Text("Equip")
                }
            }
            // Equipped and Locked are informational, not faded disabled buttons.
        }
    }
}

@Composable
private fun AchievementCard(item: AchievementProgress) {
    GridMatesInfoPanel(Modifier.fillMaxWidth().testTag("ws16bt-achievement-" + item.id)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(item.title, fontWeight = FontWeight.SemiBold, color = Forest,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f))
            Text(if (item.unlocked) "Earned" else
                item.current.toString() + " / " + item.target,
                color = if (item.unlocked) Forest else Muted,
                style = MaterialTheme.typography.labelMedium)
        }
        Text(item.description, style = MaterialTheme.typography.bodyMedium, color = Muted)
        PaperProgress(item.current, item.target)
        if (!item.unlocked) {
            Text((item.target - item.current).coerceAtLeast(0).toString() +
                " more to earn this achievement",
                color = Muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Compact but complete gallery navigation: no rewards are hidden or discarded. */
@Composable
private fun CollectionPager(page: Int, pageCount: Int, id: String, onChange: (Int) -> Unit) {
    if (pageCount <= 1) return
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = { onChange(page - 1) }, enabled = page > 0,
            modifier = Modifier.heightIn(min = 48.dp).testTag("$id-previous")) {
            Text("Previous")
        }
        Text("Page ${page + 1} of $pageCount",
            color = Muted, style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = { onChange(page + 1) }, enabled = page < pageCount - 1,
            modifier = Modifier.heightIn(min = 48.dp).testTag("$id-next")) {
            Text("Next")
        }
    }
}
