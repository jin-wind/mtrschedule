package com.jinwind.mtrschedule.ui.glass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.jinwind.mtrschedule.ui.theme.MtrGlassColors
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun GlassScaffold(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(LayerBackdrop) -> Unit
) {
    val backdrop = rememberLayerBackdrop()
    val colors = MtrGlassTheme.colors
    Box(modifier.fillMaxSize()) {
        Canvas(
            Modifier
                .layerBackdrop(backdrop)
                .fillMaxSize()
        ) {
            drawGlassBackground(colors)
        }
        content(backdrop)
    }
}

private fun DrawScope.drawGlassBackground(colors: MtrGlassColors) {
    drawRect(
        Brush.verticalGradient(
            colors = listOf(colors.backgroundTop, colors.backgroundBottom),
            startY = 0f,
            endY = size.height
        )
    )
    drawRect(
        Brush.verticalGradient(
            colors = listOf(colors.highlight.copy(alpha = 0.10f), colors.highlight.copy(alpha = 0f)),
            startY = 0f,
            endY = size.height * 0.45f
        )
    )
}
