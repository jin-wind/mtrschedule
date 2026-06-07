package com.jinwind.mtrschedule.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jinwind.mtrschedule.ui.glass.GlassButton
import com.jinwind.mtrschedule.ui.glass.GlassSurface
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.Backdrop
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerContent(
    backdrop: Backdrop,
    navController: NavHostController,
    closeDrawer: () -> Unit
) {
    val colors = MtrGlassTheme.colors
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalDrawerSheet(
        drawerContainerColor = colors.surfaceStrong.copy(alpha = 0.9f),
        modifier = Modifier.width(260.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "MTR Schedule",
                color = colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                "Liquid Glass",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DrawerItem(backdrop, "首頁", Icons.Default.Home, currentRoute == "home") {
                navController.navigate("home") { launchSingleTop = true }
                closeDrawer()
            }
            DrawerItem(backdrop, "路線號碼", Icons.Default.Star, currentRoute == "routes") {
                navController.navigate("routes") { launchSingleTop = true }
                closeDrawer()
            }
            DrawerItem(backdrop, "設置", Icons.Default.Settings, currentRoute == "settings") {
                navController.navigate("settings") { launchSingleTop = true }
                closeDrawer()
            }

            Spacer(Modifier.weight(1f))
            Text(
                "v2.5",
                color = colors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    backdrop: Backdrop,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MtrGlassTheme.colors
    GlassSurface(
        backdrop = backdrop,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tint = if (selected) colors.primary.copy(alpha = 0.62f) else colors.surface.copy(alpha = 0.5f),
        radius = 16.dp,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (selected) colors.onPrimary else colors.textPrimary,
                modifier = Modifier.width(24.dp)
            )
            Text(
                text,
                color = if (selected) colors.onPrimary else colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun GlassTopBar(
    backdrop: Backdrop,
    title: String,
    onMenuClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null
) {
    val colors = MtrGlassTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassButton(
            onClick = onMenuClick,
            backdrop = backdrop,
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = colors.textPrimary
            )
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
            GlassButton(
                onClick = onRefreshClick,
                backdrop = backdrop
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = colors.textPrimary
                )
            }
        }
    }
}

@Composable
fun MtrNavigationScaffold(
    backdrop: Backdrop,
    navController: NavHostController,
    viewModel: com.jinwind.mtrschedule.TrainScheduleViewModel,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val title = when (currentRoute) {
        "home" -> "MTR Schedule"
        "routes" -> "路線號碼"
        "settings" -> "設置"
        else -> "MTR Schedule"
    }

    val onRefreshClick: (() -> Unit)? = when (currentRoute) {
        "home", "routes" -> ({ viewModel.requestRefresh() })
        else -> null
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                backdrop = backdrop,
                navController = navController,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Column {
            GlassTopBar(
                backdrop = backdrop,
                title = title,
                onMenuClick = { scope.launch { drawerState.open() } },
                onRefreshClick = onRefreshClick
            )
            content()
        }
    }
}
