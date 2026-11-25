package com.playereden.gridmates.wordsearch

import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal object StartupBrandingSpec {
    val stages = listOf("Spudacus" to "spudacus.png", "GridMates" to "gridmates.png")
    const val FADE_MS = 300
    const val HOLD_MS = 1500
    fun initialStage(cold: Boolean, motion: Boolean): Int =
        if (cold && motion) 0 else stages.size
}

/** Adapted from the owner-approved Sudoku startup v1.0 reference.
 *  A local overlay, not a loading gate or a replacement for game state. */
@Composable
internal fun StartupBranding(
    showInitially: Boolean,
    lifecycle: Lifecycle,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val stages = StartupBrandingSpec.stages
    var stage by remember {
        mutableIntStateOf(StartupBrandingSpec.initialStage(showInitially, ValueAnimator.areAnimatorsEnabled()))
    }
    val opacity = remember { Animatable(0f) }
    var artwork by remember { mutableStateOf<ImageBitmap?>(null) }
    val visible = stage < stages.size
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) stage = stages.size
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(stage) {
        if (!visible) return@LaunchedEffect
        if (!ValueAnimator.areAnimatorsEnabled()) {
            stage = stages.size
            return@LaunchedEffect
        }
        opacity.snapTo(0f)
        artwork = null
        val file = stages[stage].second
        artwork = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open("branding/startup/v1/$file").use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            }.getOrNull()
        }
        if (artwork == null) {
            stage = stages.size
            return@LaunchedEffect
        }
        opacity.animateTo(1f, tween(StartupBrandingSpec.FADE_MS, easing = androidx.compose.animation.core.LinearEasing))
        delay(StartupBrandingSpec.HOLD_MS.toLong())
        opacity.animateTo(0f, tween(StartupBrandingSpec.FADE_MS))
        stage++
    }
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().then(if (visible) Modifier.clearAndSetSemantics {} else Modifier)) {
            content()
        }
        if (visible) {
            Box(
                Modifier.fillMaxSize().background(Color(0xFFFAE7CD))
                    .clickable(onClickLabel = "Skip startup") { stage = stages.size }
                    .testTag("startup_branding"),
                contentAlignment = Alignment.Center
            ) {
                artwork?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = stages[stage].first,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.safeDrawingPadding().padding(24.dp)
                            .widthIn(max = 420.dp).fillMaxWidth(.82f)
                            .heightIn(max = 260.dp).alpha(opacity.value)
                    )
                }
            }
        }
    }
    // Register after the game content so Back dismisses branding first.
    BackHandler(visible) { stage = stages.size }
}
