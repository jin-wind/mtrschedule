package com.jinwind.mtrschedule.ui.stations

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jinwind.mtrschedule.R
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.model.Train
import com.jinwind.mtrschedule.ui.glass.GlassButton
import com.jinwind.mtrschedule.ui.glass.GlassSurface
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.Backdrop

@Composable
fun StationDetailScreen(
    backdrop: Backdrop,
    station: Station?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MtrGlassTheme.colors
    if (station == null) {
        Column(
            modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select a station", color = colors.textSecondary, fontSize = 18.sp)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                GlassButton(
                    onClick = onBackClick,
                    backdrop = backdrop
                ) {
                    Text("←", color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                GlassSurface(
                    backdrop = backdrop,
                    modifier = Modifier.weight(1f),
                    tint = colors.surfaceStrong.copy(alpha = 0.72f)
                ) {
                    Column {
                        Text(station.stationName, color = colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Station ID: ${station.stationId}", color = colors.textSecondary, fontSize = 14.sp)
                    }
                }
            }
        }

        val items = buildTrainListItems(station.nextTrains)
        if (items.isEmpty()) {
            item {
                Text(
                    "No trains available at this time",
                    color = colors.textSecondary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            items(items) { item ->
                when (item) {
                    is TrainListItem.Header -> PlatformHeader(backdrop, item.title)
                    is TrainListItem.TrainItem -> TrainCard(backdrop, item.train)
                }
            }
        }
    }
}

@Composable
private fun PlatformHeader(backdrop: Backdrop, title: String) {
    val colors = MtrGlassTheme.colors
    GlassSurface(
        backdrop = backdrop,
        radius = 18.dp,
        tint = colors.primary.copy(alpha = 0.44f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(title, color = colors.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TrainCard(backdrop: Backdrop, train: Train, modifier: Modifier = Modifier) {
    val colors = MtrGlassTheme.colors
    GlassSurface(backdrop = backdrop, modifier = modifier.fillMaxWidth(), tint = colors.surface.copy(alpha = 0.78f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(train.routeNumber, color = colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(train.destination, color = colors.textSecondary, fontSize = 18.sp, modifier = Modifier.padding(top = 4.dp))
                Text(platformDisplay(train.platform), color = colors.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
            }
            Image(
                painter = painterResource(if (train.isDoubleCar) R.drawable.ic_train_double else R.drawable.ic_train_single),
                contentDescription = if (train.isDoubleCar) "Double car" else "Single car",
                modifier = Modifier.height(24.dp)
            )
            Text(arrivalText(train), color = colors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun arrivalText(train: Train): String = when {
    train.eta.isNotBlank() -> train.eta
    train.timeToArrival == 0 -> "Arriving"
    train.timeToArrival == 1 -> "1 min"
    else -> "${train.timeToArrival} min"
}

private fun platformDisplay(platform: String): String = getPlatformDisplayName(platform)
