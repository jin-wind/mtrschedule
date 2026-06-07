package com.jinwind.mtrschedule.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class MtrGlassColors(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val tintBlue: Color,
    val tintPink: Color,
    val surface: Color,
    val surfaceStrong: Color,
    val surfaceSubtle: Color,
    val highlight: Color,
    val stroke: Color,
    val shadow: Color,
    val primary: Color,
    val secondary: Color,
    val onPrimary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val error: Color,
)

val DarkGlassColors = MtrGlassColors(
    backgroundTop = Color(0xFF101214),
    backgroundBottom = Color(0xFF1A1D20),
    tintBlue = Color.Transparent,
    tintPink = Color.Transparent,
    surface = Color(0xB3262A2E),
    surfaceStrong = Color(0xE61D2024),
    surfaceSubtle = Color(0x33FFFFFF),
    highlight = Color(0x55FFFFFF),
    stroke = Color(0x2EFFFFFF),
    shadow = Color(0x73000000),
    primary = Color(0xFFE0444F),
    secondary = Color(0xFF42A5A7),
    onPrimary = Color.White,
    textPrimary = Color(0xFFF4F5F6),
    textSecondary = Color(0xFFC9CDD1),
    textTertiary = Color(0xFF9299A1),
    error = Color(0xFFFF6B73),
)

val LightGlassColors = MtrGlassColors(
    backgroundTop = Color(0xFFF6F7F8),
    backgroundBottom = Color(0xFFECEFF1),
    tintBlue = Color.Transparent,
    tintPink = Color.Transparent,
    surface = Color(0xCCFFFFFF),
    surfaceStrong = Color(0xF8FFFFFF),
    surfaceSubtle = Color(0x66FFFFFF),
    highlight = Color(0xE6FFFFFF),
    stroke = Color(0x8AFFFFFF),
    shadow = Color(0x2410181F),
    primary = Color(0xFFB21F2D),
    secondary = Color(0xFF177E89),
    onPrimary = Color.White,
    textPrimary = Color(0xFF20242A),
    textSecondary = Color(0xFF5F6872),
    textTertiary = Color(0xFF87909A),
    error = Color(0xFFB3261E),
)

val LocalMtrGlassColors = staticCompositionLocalOf { DarkGlassColors }
