package com.jinwind.mtrschedule.ui.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.Backdrop

@Composable
fun GlassButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    tint: Color = if (selected) MtrGlassTheme.colors.primary.copy(alpha = 0.78f) else MtrGlassTheme.colors.surface.copy(alpha = 0.78f),
    content: @Composable BoxScope.() -> Unit
) {
    GlassSurface(
        backdrop = backdrop,
        modifier = modifier.clickable(onClick = onClick),
        radius = 24.dp,
        tint = tint,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        content = content
    )
}
