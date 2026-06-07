package com.jinwind.mtrschedule.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jinwind.mtrschedule.TrainScheduleViewModel
import com.jinwind.mtrschedule.ui.main.MtrNavigationScaffold
import com.jinwind.mtrschedule.ui.routes.RoutesScreen
import com.jinwind.mtrschedule.ui.settings.SettingsScreen
import com.jinwind.mtrschedule.ui.stations.StationListScreen

@Composable
fun MtrApp(viewModel: TrainScheduleViewModel) {
    MtrMiuixTheme {
        val navController = rememberNavController()

        MtrNavigationScaffold(
            navController = navController,
            viewModel = viewModel
        ) { contentPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                composable("home") {
                    StationListScreen(
                        viewModel = viewModel
                    )
                }
                composable("routes") {
                    RoutesScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                composable("settings") {
                    SettingsScreen(navController = navController)
                }
            }
        }
    }
}
