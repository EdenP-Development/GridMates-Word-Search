package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridmates.wordsearch.core.WoodlandCollection
import com.playereden.gridmates.wordsearch.core.WordSearchAchievements
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

/** A paged gallery must expose later rewards without losing stored unlocks. */
class CollectionGalleryPagingAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun portraitAndAchievementNextPagesStayReachableAtPhoneSize() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws07g_gallery_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, filename)
            val oldPortraits = WoodlandCollection.progress(store.profile)
            val oldAchievements = WordSearchAchievements.progress(store.profile)
            compose.setContent {
                GridMatesTheme {
                    Column(Modifier.width(360.dp).height(720.dp)
                        .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CollectionScreen(store)
                    }
                }
            }
            compose.onNodeWithText("Earn portraits and fonts by finishing puzzles.", substring = true)
                .assertIsDisplayed()
            compose.onNodeWithTag("ws16bu-gallery").assertExists()
            compose.onNodeWithText("Page 1 of 3").assertExists()
            compose.onNodeWithTag("ws16bu-portrait-woodland_owl").assertExists()
            compose.onNodeWithTag("ws16bu-portrait-woodland_fox").assertExists()
            compose.onNodeWithTag("ws16bu-portrait-woodland_rabbit").assertExists()
            compose.onNodeWithTag("ws16bu-portrait-woodland_bear").assertExists()
            compose.onNodeWithTag("ws16bu-equip-woodland_fox").assertExists()
            compose.onNodeWithText("Edit display name").assertExists()
            compose.onNodeWithTag("ws16bt-collection-profile").assertExists()
            compose.onNodeWithTag("ws27-compact-collection-identity").assertExists()
            compose.onNodeWithTag("ws16bt-collection-progress").assertExists()
            compose.onNodeWithText("COLLECTION PROGRESS").assertExists()
            // Gallery navigation must be visible on first entry, without scrolling
            // past profile and lifetime progress cards.
            compose.onNodeWithTag("portraits-next")
                .assertIsDisplayed().assertIsEnabled().performClick()
            compose.onNodeWithText("Page 2 of 3")
                .performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("ws16bu-portrait-woodland_badger").assertExists()

            compose.onNodeWithText("Achievements").performScrollTo().performClick()
            compose.onNodeWithTag("ws16bt-achievement-first_puzzle").assertExists()
            // The pager should be directly visible below the achievements
            // introduction, not buried beneath four long cards.
            compose.onNodeWithTag("achievements-next")
                .assertIsDisplayed().assertIsEnabled().performClick()
            compose.onNodeWithText("Page 2 of", substring = true)
                .performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Fonts").performScrollTo().performClick()
            compose.onNodeWithText("board fonts unlocked", substring = true).assertExists()
            compose.onNodeWithTag("ws16bv-font-gallery").assertExists()
            compose.onNodeWithTag("ws16bv-font-atkinson").assertExists()
            compose.onNodeWithTag("ws16bv-font-status-atkinson").assertExists()
            compose.onNodeWithTag("ws16bv-font-license-atkinson")
                .performScrollTo().performClick()
            compose.onNodeWithText("Readable Print · Open Font License").assertExists()
            compose.onNodeWithText("Close").performClick()
            compose.onNodeWithTag("ws16bv-font-plex").assertExists()
            compose.onNodeWithTag("ws16bv-font-equip-plex").assertDoesNotExist()
            compose.onNodeWithTag("ws16bv-font-license-plex").assertExists()

            compose.runOnIdle {
                assertEquals(12, WoodlandCollection.progress(store.profile).size)
                assertEquals(8, WordSearchAchievements.progress(store.profile).size)
                assertEquals(oldPortraits, WoodlandCollection.progress(store.profile))
                assertEquals(oldAchievements, WordSearchAchievements.progress(store.profile))
            }
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }
}
