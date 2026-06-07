package com.jinwind.mtrschedule.ui.glass

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun GlassSurface(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    radius: Dp = 28.dp,
    tint: Color = MtrGlassTheme.colors.surface,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val colors = MtrGlassTheme.colors
    val shape = RoundedCornerShape(radius)
    val glassModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(radius) },
            effects = {
                vibrancy()
                blur(2.dp.toPx())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    lens(12.dp.toPx(), 24.dp.toPx())
                }
            },
            onDrawSurface = {
                drawRect(tint)
                drawRect(colors.highlight.copy(alpha = 0.12f))
            }
        )
    } else {
        Modifier
            .shadow(12.dp, shape, clip = false)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(colors.highlight.copy(alpha = 0.36f), tint)
                )
            )
    }

    Box(
        modifier
            .then(glassModifier)
            .clip(shape)
            .border(1.dp, colors.stroke, shape)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GlassChip(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    content: @Composable BoxScope.() -> Unit
) {
    GlassSurface(
        backdrop = backdrop,
        modifier = modifier,
        radius = 22.dp,
        tint = if (selected) MtrGlassTheme.colors.primary.copy(alpha = 0.72f) else MtrGlassTheme.colors.surface.copy(alpha = 0.72f),
        contentPadding = contentPadding,
        content = content
    )
}
