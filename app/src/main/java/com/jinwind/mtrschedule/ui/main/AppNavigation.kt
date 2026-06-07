package com.jinwind.mtrschedule.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jinwind.mtrschedule.TrainScheduleViewModel
import com.jinwind.mtrschedule.ui.miuix.MtrCard
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MtrNavigationScaffold(
    navController: NavHostController,
    viewModel: TrainScheduleViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    var drawerOpen by rememberSaveable { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val colors = MiuixTheme.colorScheme

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

    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colors.surface,
            topBar = {
                MtrTopBar(
                    title = title,
                    onMenuClick = { drawerOpen = true },
                    onRefreshClick = onRefreshClick
                )
            },
            content = content
        )

        if (drawerOpen) {
            androidx.compose.foundation.layout.Box(
                Modifier
                    .fillMaxSize()
                    .background(colors.windowDimming)
                    .clickable { drawerOpen = false }
            )
            NavigationDrawerContent(
                navController = navController,
                closeDrawer = { drawerOpen = false },
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}

@Composable
private fun MtrTopBar(
    title: String,
    onMenuClick: () -> Unit,
    onRefreshClick: (() -> Unit)? = null
) {
    val colors = MiuixTheme.colorScheme

    SmallTopAppBar(
        title = title,
        color = colors.surface,
        titleColor = colors.onSurface,
        navigationIcon = {
            ToolbarButton(onClick = onMenuClick) {
                Text("☰", color = colors.onSecondaryVariant, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            if (onRefreshClick != null) {
                ToolbarButton(onClick = onRefreshClick) {
                    Text("↻", color = colors.onSecondaryVariant, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
private fun ToolbarButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val colors = MiuixTheme.colorScheme
    IconButton(
        onClick = onClick,
        backgroundColor = colors.secondaryVariant,
        cornerRadius = 16.dp
    ) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
private fun NavigationDrawerContent(
    navController: NavHostController,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MiuixTheme.colorScheme
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp),
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        color = colors.surfaceContainer,
        contentColor = colors.onSurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "MTR Schedule",
                color = colors.onSurfaceContainer,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Miuix",
                color = colors.onSurfaceContainerVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DrawerItem("首頁", currentRoute == "home") {
                navController.navigate("home") { launchSingleTop = true }
                closeDrawer()
            }
            DrawerItem("路線號碼", currentRoute == "routes") {
                navController.navigate("routes") { launchSingleTop = true }
                closeDrawer()
            }
            DrawerItem("設置", currentRoute == "settings") {
                navController.navigate("settings") { launchSingleTop = true }
                closeDrawer()
            }

            Spacer(Modifier.weight(1f))
            Text(
                "v2.5",
                color = colors.onSurfaceContainerVariant,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MiuixTheme.colorScheme

    MtrCard(
        modifier = Modifier.fillMaxWidth(),
        selected = selected,
        color = if (selected) colors.primary else colors.secondaryVariant,
        contentColor = if (selected) colors.onPrimary else colors.onSecondaryVariant,
        cornerRadius = 16.dp,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
        onClick = onClick
    ) {
        Text(
            text,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
