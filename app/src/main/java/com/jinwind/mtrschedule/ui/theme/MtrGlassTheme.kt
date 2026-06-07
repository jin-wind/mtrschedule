package com.jinwind.mtrschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MtrGlassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val glassColors = if (darkTheme) DarkGlassColors else LightGlassColors
    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = glassColors.primary,
            secondary = glassColors.secondary,
            background = glassColors.backgroundTop,
            surface = glassColors.surfaceStrong,
            onPrimary = glassColors.onPrimary,
            onBackground = glassColors.textPrimary,
            onSurface = glassColors.textPrimary,
            error = glassColors.error,
        )
    } else {
        lightColorScheme(
            primary = glassColors.primary,
            secondary = glassColors.secondary,
            background = glassColors.backgroundTop,
            surface = glassColors.surfaceStrong,
            onPrimary = glassColors.onPrimary,
            onBackground = glassColors.textPrimary,
            onSurface = glassColors.textPrimary,
            error = glassColors.error,
        )
    }

    CompositionLocalProvider(LocalMtrGlassColors provides glassColors) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}

object MtrGlassTheme {
    val colors: MtrGlassColors
        @Composable get() = LocalMtrGlassColors.current
}
