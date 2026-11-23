package com.playereden.gridmates.wordsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HomeTitleLayoutAndroidTest {
    @get:Rule val compose=createComposeRule()
    @Test fun wholeGameTitleAndBothActionsFitNarrowAndLargeTextLayouts() {
        val layout=mutableStateOf(360 to 1f)
        var profiles=0;var menus=0
        compose.setContent { GridMatesTheme {
            val density=LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density,layout.value.second)) {
                Box(Modifier.width(layout.value.first.dp)) {
                    Ws16HomeHeader(onProfile={profiles++},onMenu={menus++})
                }
            }
        } }
        val failures=mutableListOf<String>()
        for ((width,scale) in listOf(360 to 1f,324 to 1f,284 to 1f,360 to 2f,324 to 2f,284 to 2f)) {
            compose.runOnIdle { layout.value=width to scale }
            val results=mutableListOf<TextLayoutResult>()
            compose.onNodeWithTag("ws41-home-title").performSemanticsAction(
                SemanticsActions.GetTextLayoutResult) { it(results) }
            assertEquals("Word Search",results.single().layoutInput.text.text)
            val result=results.single()
            println("WS41 HEADER width=$width scale=$scale size=${result.size} constraints=${result.layoutInput.constraints} lines=${result.lineCount} overflowWidth=${result.didOverflowWidth} overflowHeight=${result.didOverflowHeight}")
            if(result.hasVisualOverflow) failures+="Title clipped at $width dp, $scale text; size=${result.size}, lines=${result.lineCount}"
            compose.onNodeWithContentDescription("Open player profile").assertIsDisplayed().performClick()
            compose.onNodeWithContentDescription("Open menu").assertIsDisplayed().performClick()
        }
        assertTrue(failures.joinToString("; "),failures.isEmpty())
        compose.runOnIdle { assertEquals(6,profiles);assertEquals(6,menus) }
    }
}
