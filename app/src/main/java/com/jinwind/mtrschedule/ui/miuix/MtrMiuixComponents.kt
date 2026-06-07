package com.jinwind.mtrschedule.ui.miuix

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MtrCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    color: Color = if (selected) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.surfaceContainer,
    contentColor: Color = if (selected) MiuixTheme.colorScheme.onPrimary else MiuixTheme.colorScheme.onSurfaceContainer,
    cornerRadius: Dp = 18.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = CardDefaults.defaultColors(
        color = color,
        contentColor = contentColor
    )

    if (onClick == null) {
        Card(
            modifier = modifier,
            cornerRadius = cornerRadius,
            insideMargin = contentPadding,
            colors = colors,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            cornerRadius = cornerRadius,
            insideMargin = contentPadding,
            colors = colors,
            showIndication = true,
            onClick = onClick,
            content = content
        )
    }
}

@Composable
fun MtrButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        cornerRadius = 16.dp,
        colors = if (selected) {
            ButtonDefaults.buttonColorsPrimary()
        } else {
            ButtonDefaults.buttonColors()
        },
        insideMargin = contentPadding,
        content = content
    )
}
