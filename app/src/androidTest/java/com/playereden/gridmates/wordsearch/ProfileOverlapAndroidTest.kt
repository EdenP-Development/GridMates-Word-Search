package com.playereden.gridmates.wordsearch

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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Regression for v89 physical screenshot: progress text painted over portrait. */
class ProfileOverlapAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun identityAndProgressHaveDisjointBoundsInsideTheSummaryFrame() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val filename = "ws28_overlap_" + System.nanoTime() + ".json"
        try {
            val store = SoloStore(context, filename)
            compose.setContent {
                GridMatesTheme {
                    Column(Modifier.width(360.dp).height(720.dp)
                        .verticalScroll(rememberScrollState())) {
                        Ws16CProfileHub(store, {}, {}, {}, {})
                    }
                }
            }
            val identity = compose.onNodeWithTag("ws28-profile-identity-row")
                .fetchSemanticsNode().boundsInRoot
            val progression = compose.onNodeWithTag("ws28-profile-progress-section")
                .fetchSemanticsNode().boundsInRoot
            val frame = compose.onNodeWithTag("ws16c-profile-summary")
                .fetchSemanticsNode().boundsInRoot

            assertTrue("Player identity must precede earned rewards, not overlap them: " +
                "$identity versus $progression",
                identity.bottom <= progression.top + 1f)
            assertTrue("Profile progress must stay inside its illustrated frame",
                progression.bottom <= frame.bottom + 1f)
            compose.onNodeWithTag("ws16bu-profile-completed").assertExists()
            compose.onNodeWithTag("ws16bu-profile-words").assertExists()
            compose.onNodeWithTag("ws26-profile-collection-progress").assertExists()
        } finally {
            for (suffix in listOf("", ".bak", ".new")) {
                File(context.filesDir, filename + suffix).delete()
            }
        }
    }
}
