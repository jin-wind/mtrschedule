package com.jinwind.mtrschedule.ui.stations

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.jinwind.mtrschedule.ui.miuix.MtrButton
import com.jinwind.mtrschedule.ui.miuix.MtrCard
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun StationDetailScreen(
    station: Station?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MiuixTheme.colorScheme
    if (station == null) {
        Column(
            modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select a station", color = colors.onBackgroundVariant, fontSize = 18.sp)
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MtrButton(onClick = onBackClick) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                MtrCard(
                    modifier = Modifier.weight(1f),
                    color = colors.surfaceContainer,
                    contentColor = colors.onSurfaceContainer
                ) {
                    Text(station.stationName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Station ID: ${station.stationId}",
                        color = colors.onSurfaceContainerVariant,
                        fontSize = 14.sp
                    )
                }
            }
        }

        val items = buildTrainListItems(station.nextTrains)
        if (items.isEmpty()) {
            item {
                Text(
                    "No trains available at this time",
                    color = colors.onBackgroundVariant,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            items(items) { item ->
                when (item) {
                    is TrainListItem.Header -> PlatformHeader(item.title)
                    is TrainListItem.TrainItem -> TrainCard(item.train)
                }
            }
        }
    }
}

@Composable
private fun PlatformHeader(title: String) {
    val colors = MiuixTheme.colorScheme
    MtrCard(
        color = colors.primaryContainer,
        contentColor = colors.onPrimaryContainer,
        cornerRadius = 16.dp,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TrainCard(train: Train, modifier: Modifier = Modifier) {
    val colors = MiuixTheme.colorScheme
    MtrCard(
        modifier = modifier.fillMaxWidth(),
        color = colors.surfaceContainer,
        contentColor = colors.onSurfaceContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(train.routeNumber, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    train.destination,
                    color = colors.onSurfaceContainerVariant,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    platformDisplay(train.platform),
                    color = colors.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Image(
                painter = painterResource(if (train.isDoubleCar) R.drawable.ic_train_double else R.drawable.ic_train_single),
                contentDescription = if (train.isDoubleCar) "Double car" else "Single car",
                modifier = Modifier.height(24.dp)
            )
            Text(arrivalText(train), fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
