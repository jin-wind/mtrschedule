package com.jinwind.mtrschedule.ui.routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jinwind.mtrschedule.R
import com.jinwind.mtrschedule.TrainScheduleViewModel
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.model.Train
import com.jinwind.mtrschedule.ui.miuix.MtrCard
import com.jinwind.mtrschedule.ui.stations.arrivalText
import com.jinwind.mtrschedule.ui.util.observeAsStateNotNull
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.RadioButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

private val lightRailRoutes = listOf("505", "507", "610", "614", "614P", "615", "615P", "705", "706", "751", "761P")
private val busRoutes = listOf("K52", "K52A", "K52S", "K53", "K58", "506", "K51", "K51A")

@Composable
fun RoutesScreen(
    viewModel: TrainScheduleViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var isBusMode by remember { mutableStateOf(false) }
    var selectedRoute by remember { mutableStateOf<String?>(null) }
    var isReverse by remember { mutableStateOf(false) }

    val busSchedule: List<Station> by viewModel.busSchedule.observeAsStateNotNull(emptyList())
    val stationSchedules: List<Station> by viewModel.stationSchedules.observeAsStateNotNull(emptyList())
    val isLoading: Boolean by viewModel.isLoading.observeAsStateNotNull(false)
    val refreshRequest: Int by viewModel.refreshRequests.observeAsStateNotNull(0)
    val routes = if (isBusMode) busRoutes else lightRailRoutes

    LaunchedEffect(refreshRequest) {
        if (refreshRequest == 0) return@LaunchedEffect

        selectedRoute?.let { route ->
            if (isBusMode) {
                viewModel.getBusSchedule(route)
            } else {
                val stationIds = if (isReverse) {
                    com.jinwind.mtrschedule.data.LightRailRouteData.getRouteStationsForDirection(route, true)
                } else {
                    com.jinwind.mtrschedule.data.LightRailRouteData.getRoute(route)?.stations ?: emptyList()
                }
                viewModel.fetchMultipleStationSchedules(stationIds)
            }
        }
    }

    val routeStations = remember(selectedRoute, isBusMode, stationSchedules, busSchedule, isReverse) {
        val route = selectedRoute
        if (route == null) {
            emptyList()
        } else if (isBusMode) {
            busSchedule
        } else {
            val stationIds = if (isReverse) {
                com.jinwind.mtrschedule.data.LightRailRouteData.getRouteStationsForDirection(route, true)
            } else {
                com.jinwind.mtrschedule.data.LightRailRouteData.getRoute(route)?.stations ?: emptyList()
            }
            stationIds.mapNotNull { stationId ->
                stationSchedules.find { it.stationId == stationId }
            }
        }
    }

    val platformFilter = platformFilterForDirection(isReverse)

    Box(modifier.fillMaxSize()) {
        Row(modifier.fillMaxSize()) {
            RouteList(
                routes = routes,
                isBusMode = isBusMode,
                onModeChange = { isBusMode = it; selectedRoute = null; isReverse = false },
                selectedRoute = selectedRoute,
                onRouteSelected = { route ->
                    if (selectedRoute == route) {
                        isReverse = !isReverse
                    } else {
                        selectedRoute = route
                        isReverse = false
                        if (isBusMode) {
                            viewModel.getBusSchedule(route)
                        } else {
                            com.jinwind.mtrschedule.data.LightRailRouteData.getRoute(route)?.stations?.let {
                                viewModel.fetchMultipleStationSchedules(it)
                            }
                        }
                    }
                }
            )
            RouteDetail(
                selectedRoute = selectedRoute,
                routeStations = routeStations,
                isLoading = isLoading,
                platformFilter = platformFilter,
                modifier = Modifier.weight(1f),
                onStationClick = { station ->
                    viewModel.fetchStationSchedule(station.stationId)
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun RouteList(
    routes: List<String>,
    isBusMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    selectedRoute: String?,
    onRouteSelected: (String) -> Unit
) {
    val colors = MiuixTheme.colorScheme
    Column(
        modifier = Modifier
            .width(104.dp)
            .fillMaxHeight()
            .background(colors.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        ModeRow("LRT", selected = !isBusMode) { onModeChange(false) }
        ModeRow("Bus", selected = isBusMode) { onModeChange(true) }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(routes, key = { it }) { route ->
                val isSelected = route == selectedRoute
                MtrCard(
                    modifier = Modifier.fillMaxWidth(),
                    selected = isSelected,
                    color = if (isSelected) colors.primary else colors.surfaceContainer,
                    contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceContainer,
                    cornerRadius = 12.dp,
                    contentPadding = PaddingValues(vertical = 11.dp, horizontal = 8.dp),
                    onClick = { onRouteSelected(route) }
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            route,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MiuixTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, color = colors.onSurface, fontSize = 12.sp)
    }
}

@Composable
private fun RouteDetail(
    selectedRoute: String?,
    routeStations: List<Station>,
    isLoading: Boolean,
    platformFilter: String,
    onStationClick: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MiuixTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(colors.surface)
    ) {
        if (selectedRoute == null) {
            Text(
                "請選擇左側的路線號碼",
                color = colors.onBackgroundVariant,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (isLoading && routeStations.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(routeStations, key = { it.stationId }) { station ->
                    val trains = filteredRouteTrains(station, platformFilter)
                    RouteStationCard(
                        station = station,
                        trains = trains,
                        onClick = { onStationClick(station) }
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RouteStationCard(
    station: Station,
    trains: List<Train>,
    onClick: () -> Unit
) {
    val colors = MiuixTheme.colorScheme
    MtrCard(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surfaceContainer,
        contentColor = colors.onSurfaceContainer,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        onClick = onClick
    ) {
        Text(
            station.stationName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        if (trains.isEmpty()) {
            Text(
                "暫無列車",
                color = colors.onSurfaceContainerVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Column(
                modifier = Modifier.padding(top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                trains.take(4).forEach { train ->
                    TrainRow(train = train)
                }
                if (trains.size > 4) {
                    Text(
                        "+${trains.size - 4} 更多列車",
                        color = colors.onSurfaceContainerVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainRow(train: Train) {
    val colors = MiuixTheme.colorScheme
    val routeColor = getRouteColor(train.routeNumber)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .background(routeColor, RoundedCornerShape(2.dp))
        )

        Text(
            text = train.routeNumber,
            color = routeColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 8.dp)
                .background(colors.secondaryVariant, RoundedCornerShape(5.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = train.destination.ifBlank { "未知目的地" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(if (train.isDoubleCar) R.drawable.ic_train_double else R.drawable.ic_train_single),
                contentDescription = if (train.isDoubleCar) "Double car" else "Single car",
                modifier = Modifier.height(16.dp)
            )
        }

        Text(
            text = arrivalText(train),
            color = colors.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
