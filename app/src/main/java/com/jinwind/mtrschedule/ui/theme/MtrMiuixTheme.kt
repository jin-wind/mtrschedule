package com.jinwind.mtrschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun MtrMiuixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val controller = remember(darkTheme) {
        ThemeController(
            colorSchemeMode = if (darkTheme) ColorSchemeMode.Dark else ColorSchemeMode.Light,
            lightColors = lightMtrColors(),
            darkColors = darkMtrColors()
        )
    }

    MiuixTheme(controller = controller, content = content)
}

private fun lightMtrColors() = lightColorScheme(
    primary = Color(0xFFB21F2D),
    onPrimary = Color.White,
    primaryVariant = Color(0xFFD83B49),
    onPrimaryVariant = Color.White,
    primaryContainer = Color(0xFFFFD9DE),
    onPrimaryContainer = Color(0xFF3F0008),
    secondary = Color(0xFFE9EEF0),
    onSecondary = Color(0xFF20242A),
    secondaryVariant = Color(0xFFF1F4F5),
    onSecondaryVariant = Color(0xFF20242A),
    background = Color(0xFFF6F7F8),
    onBackground = Color(0xFF20242A),
    onBackgroundVariant = Color(0xFF5F6872),
    surface = Color(0xFFF0F2F3),
    onSurface = Color(0xFF20242A),
    surfaceVariant = Color.White,
    onSurfaceSecondary = Color(0xFF5F6872),
    onSurfaceVariantSummary = Color(0xFF6F7882),
    surfaceContainer = Color.White,
    onSurfaceContainer = Color(0xFF20242A),
    onSurfaceContainerVariant = Color(0xFF66707A),
    surfaceContainerHigh = Color(0xFFE9ECEE),
    onSurfaceContainerHigh = Color(0xFF30363C),
    surfaceContainerHighest = Color(0xFFDDE2E5),
    onSurfaceContainerHighest = Color(0xFF20242A),
    outline = Color(0xFFD3DADF),
    dividerLine = Color(0xFFE0E5E8),
    error = Color(0xFFB3261E)
)

private fun darkMtrColors() = darkColorScheme(
    primary = Color(0xFFE0444F),
    onPrimary = Color.White,
    primaryVariant = Color(0xFFC93442),
    onPrimaryVariant = Color.White,
    primaryContainer = Color(0xFF5A1119),
    onPrimaryContainer = Color(0xFFFFD9DE),
    secondary = Color(0xFF3D454B),
    onSecondary = Color(0xFFF4F5F6),
    secondaryVariant = Color(0xFF2E363B),
    onSecondaryVariant = Color(0xFFE4E8EA),
    background = Color(0xFF101214),
    onBackground = Color(0xFFF4F5F6),
    onBackgroundVariant = Color(0xFFC9CDD1),
    surface = Color(0xFF16191C),
    onSurface = Color(0xFFF4F5F6),
    surfaceVariant = Color(0xFF1D2024),
    onSurfaceSecondary = Color(0xFFC9CDD1),
    onSurfaceVariantSummary = Color(0xFFADB4BA),
    surfaceContainer = Color(0xFF202429),
    onSurfaceContainer = Color(0xFFF4F5F6),
    onSurfaceContainerVariant = Color(0xFFC9CDD1),
    surfaceContainerHigh = Color(0xFF2A2F35),
    onSurfaceContainerHigh = Color(0xFFE7EAEC),
    surfaceContainerHighest = Color(0xFF343A41),
    onSurfaceContainerHighest = Color(0xFFF4F5F6),
    outline = Color(0xFF424950),
    dividerLine = Color(0xFF343A41),
    error = Color(0xFFFF6B73)
)
