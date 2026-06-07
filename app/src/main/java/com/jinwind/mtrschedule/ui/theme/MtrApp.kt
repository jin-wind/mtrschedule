package com.jinwind.mtrschedule.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jinwind.mtrschedule.TrainScheduleViewModel
import com.jinwind.mtrschedule.ui.glass.GlassScaffold
import com.jinwind.mtrschedule.ui.main.MtrNavigationScaffold
import com.jinwind.mtrschedule.ui.routes.RoutesScreen
import com.jinwind.mtrschedule.ui.settings.SettingsScreen
import com.jinwind.mtrschedule.ui.stations.StationListScreen

@Composable
fun MtrApp(viewModel: TrainScheduleViewModel) {
    val darkTheme = isSystemInDarkTheme()
    val glassColors = if (darkTheme) DarkGlassColors else LightGlassColors

    MtrGlassTheme(darkTheme = darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme(
                primary = glassColors.primary,
                secondary = glassColors.secondary,
                background = glassColors.backgroundTop,
                surface = glassColors.surfaceStrong,
                onPrimary = glassColors.onPrimary,
                onBackground = glassColors.textPrimary,
                onSurface = glassColors.textPrimary,
                error = glassColors.error,
            ) else lightColorScheme(
                primary = glassColors.primary,
                secondary = glassColors.secondary,
                background = glassColors.backgroundTop,
                surface = glassColors.surfaceStrong,
                onPrimary = glassColors.onPrimary,
                onBackground = glassColors.textPrimary,
                onSurface = glassColors.textPrimary,
                error = glassColors.error,
            )
        ) {
            val navController = rememberNavController()
            GlassScaffold(modifier = Modifier.fillMaxSize()) { backdrop ->
                MtrNavigationScaffold(
                    backdrop = backdrop,
                    navController = navController,
                    viewModel = viewModel
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") {
                            StationListScreen(
                                viewModel = viewModel,
                                backdrop = backdrop,
                                navController = navController
                            )
                        }
                        composable("routes") {
                            RoutesScreen(
                                viewModel = viewModel,
                                backdrop = backdrop,
                                navController = navController
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                backdrop = backdrop,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}