package com.jinwind.mtrschedule.ui.stations

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jinwind.mtrschedule.R
import com.jinwind.mtrschedule.TrainScheduleViewModel
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.ui.glass.GlassSurface
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.jinwind.mtrschedule.ui.util.observeAsStateNotNull
import com.jinwind.mtrschedule.ui.util.observeAsStateValue
import com.kyant.backdrop.Backdrop

@Composable
fun StationListScreen(
    viewModel: TrainScheduleViewModel,
    backdrop: Backdrop,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val stations: List<Station> by viewModel.stationSchedules.observeAsStateNotNull(emptyList())
    val isLoading: Boolean by viewModel.isLoading.observeAsStateNotNull(false)
    val error: String by viewModel.error.observeAsStateNotNull("")
    val selectedStation: Station? by viewModel.selectedStation.observeAsStateValue(null)
    val refreshRequest: Int by viewModel.refreshRequests.observeAsStateNotNull(0)

    var query by remember { mutableStateOf("") }

    LaunchedEffect(refreshRequest) {
        if (refreshRequest == 0) return@LaunchedEffect

        val station = selectedStation
        if (station != null) {
            viewModel.fetchStationSchedule(station.stationId)
        } else {
            viewModel.loadAllStations()
        }
    }

    BackHandler(enabled = selectedStation != null) {
        viewModel.clearSelectedStation()
    }

    val filtered = if (query.isBlank()) stations else stations.filter {
        it.stationName.contains(query, ignoreCase = true) ||
                it.stationId.contains(query, ignoreCase = true)
    }
    val sorted = filtered.sortedByDescending { it.isPinned }

    Box(modifier.fillMaxSize()) {
        if (isLoading && stations.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MtrGlassTheme.colors.primary
            )
        } else if (error.isNotEmpty() && stations.isEmpty()) {
            Text(
                text = error,
                color = MtrGlassTheme.colors.error,
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            )
        } else if (selectedStation != null) {
            StationDetailScreen(
                backdrop = backdrop,
                station = selectedStation,
                onBackClick = { viewModel.clearSelectedStation() }
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .size(28.dp),
                    color = MtrGlassTheme.colors.primary
                )
            }
        } else {
            Column(modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                SearchField(
                    backdrop = backdrop,
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(
                    modifier = Modifier.padding(top = 12.dp).weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sorted, key = { it.stationId }) { station ->
                        StationCard(
                            backdrop = backdrop,
                            station = station,
                            isTopped = station.isPinned,
                            onClick = { viewModel.fetchStationSchedule(station.stationId) },
                            onPin = { viewModel.togglePinStation(station.stationId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(
    backdrop: Backdrop,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MtrGlassTheme.colors
    GlassSurface(
        backdrop = backdrop,
        modifier = modifier,
        radius = 24.dp,
        tint = colors.surface.copy(alpha = 0.72f),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(color = colors.textPrimary, fontSize = 16.sp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text("搜尋車站", color = colors.textTertiary, fontSize = 16.sp)
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun StationCard(
    backdrop: Backdrop,
    station: Station,
    isTopped: Boolean,
    onClick: () -> Unit,
    onPin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MtrGlassTheme.colors
    GlassSurface(
        backdrop = backdrop,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tint = if (isTopped) colors.primary.copy(alpha = 0.62f) else colors.surface.copy(alpha = 0.78f)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(station.stationName, color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("ID: ${station.stationId}", color = colors.textSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                station.nextTrains.firstOrNull()?.destination?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = colors.textSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp))
                }
            }
            Box(
                Modifier
                    .size(36.dp)
                    .clickable(onClick = onPin),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(if (isTopped) R.drawable.ic_star_filled else R.drawable.ic_star_outline),
                    contentDescription = if (isTopped) "Pinned" else "Pin",
                    colorFilter = ColorFilter.tint(if (isTopped) colors.onPrimary else colors.primary),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
