package com.jinwind.mtrschedule.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinwind.mtrschedule.ui.glass.GlassButton
import com.jinwind.mtrschedule.ui.glass.GlassSurface
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.Backdrop

@Composable
fun NavigationDrawerContent(
    backdrop: Backdrop,
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MtrGlassTheme.colors
    GlassSurface(
        backdrop = backdrop,
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        radius = 0.dp,
        tint = colors.surfaceStrong.copy(alpha = 0.82f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("MTR Schedule", color = colors.textPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Liquid Glass", color = colors.textSecondary, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            DrawerItem(backdrop, "首頁", currentScreen == AppScreen.Home) { onScreenSelected(AppScreen.Home) }
            DrawerItem(backdrop, "路線號碼", currentScreen == AppScreen.Routes) { onScreenSelected(AppScreen.Routes) }
            DrawerItem(backdrop, "設置", currentScreen == AppScreen.Settings) { onScreenSelected(AppScreen.Settings) }
        }
    }
}

@Composable
private fun DrawerItem(backdrop: Backdrop, text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MtrGlassTheme.colors
    GlassButton(
        onClick = onClick,
        backdrop = backdrop,
        selected = selected,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, color = if (selected) colors.onPrimary else colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TopBar(
    backdrop: Backdrop,
    title: String,
    onMenuClick: () -> Unit,
    onRefreshClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colors = MtrGlassTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlassButton(onClick = onMenuClick, backdrop = backdrop) {
            Text("☰", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        GlassSurface(
            backdrop = backdrop,
            modifier = Modifier.weight(1f),
            radius = 24.dp,
            tint = colors.surface.copy(alpha = 0.72f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(title, color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        if (onRefreshClick != null) {
            GlassButton(onClick = onRefreshClick, backdrop = backdrop) {
                Text("↻", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
