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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jinwind.mtrschedule.R
import com.jinwind.mtrschedule.TrainScheduleViewModel
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.model.Train
import com.jinwind.mtrschedule.ui.glass.GlassSurface
import com.jinwind.mtrschedule.ui.stations.arrivalText
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.jinwind.mtrschedule.ui.util.observeAsStateNotNull
import com.jinwind.mtrschedule.ui.util.observeAsStateValue
import com.kyant.backdrop.Backdrop

private val lightRailRoutes = listOf("505", "507", "610", "614", "614P", "615", "615P", "705", "706", "751", "761P")
private val busRoutes = listOf("K52", "K52A", "K52S", "K53", "K58", "506", "K51", "K51A")

@Composable
fun RoutesScreen(
    viewModel: TrainScheduleViewModel,
    backdrop: Backdrop,
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
                backdrop = backdrop,
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
                backdrop = backdrop,
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
    backdrop: Backdrop,
    routes: List<String>,
    isBusMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    selectedRoute: String?,
    onRouteSelected: (String) -> Unit
) {
    val colors = MtrGlassTheme.colors
    Column(
        modifier = Modifier
            .width(90.dp)
            .fillMaxHeight()
            .background(colors.surface.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = !isBusMode,
                onClick = { onModeChange(false) }
            )
            Text("LRT", color = colors.textPrimary, fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isBusMode,
                onClick = { onModeChange(true) }
            )
            Text("Bus", color = colors.textPrimary, fontSize = 12.sp)
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(routes, key = { it }) { route ->
                val isSelected = route == selectedRoute
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) colors.primary else colors.surface.copy(alpha = 0.6f)
                        )
                        .clickable { onRouteSelected(route) }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        route,
                        color = if (isSelected) colors.onPrimary else colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteDetail(
    backdrop: Backdrop,
    selectedRoute: String?,
    routeStations: List<Station>,
    isLoading: Boolean,
    platformFilter: String,
    onStationClick: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MtrGlassTheme.colors
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(colors.backgroundTop.copy(alpha = 0.5f))
    ) {
        if (selectedRoute == null) {
            Text(
                "請選擇左側的路線號碼",
                color = colors.textSecondary,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (isLoading && routeStations.isEmpty()) {
            CircularProgressIndicator(
                color = colors.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(routeStations, key = { it.stationId }) { station ->
                    val trains = filteredRouteTrains(station, platformFilter)
                    RouteStationCard(
                        backdrop = backdrop,
                        station = station,
                        trains = trains,
                        platformFilter = platformFilter,
                        onClick = { onStationClick(station) }
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    color = colors.primary,
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
    backdrop: Backdrop,
    station: Station,
    trains: List<Train>,
    platformFilter: String,
    onClick: () -> Unit
) {
    val colors = MtrGlassTheme.colors
    GlassSurface(
        backdrop = backdrop,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tint = colors.surface.copy(alpha = 0.72f),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                station.stationName,
                color = colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (trains.isEmpty()) {
                Text(
                    "暫無列車",
                    color = colors.textSecondary,
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
                            color = colors.textTertiary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainRow(train: Train) {
    val colors = MtrGlassTheme.colors
    val routeColor = getRouteColor(train.routeNumber)

    // 車序：取 trainId 後段數字（API 格式為 platformId_routeNo，盡量抽數字後3位）
    val carSeq = train.trainId.filter { it.isDigit() }.takeLast(3).padStart(3, '0')

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左側顏色標示線
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .background(routeColor, RoundedCornerShape(2.dp))
        )

        // 左側車序標籤 (灰框小字)
        Text(
            text = carSeq,
            color = colors.textSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 8.dp)
                .background(colors.surface.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )

        // 中間區塊（目的地 + 車卡圖標）
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = train.destination.ifBlank { "未知目的地" },
                color = colors.textPrimary,
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

        // 右側 ETA
        Text(
            text = arrivalText(train),
            color = colors.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
