package com.jinwind.mtrschedule.ui.stations

import com.jinwind.mtrschedule.model.Train

sealed class TrainListItem {
    data class Header(val title: String) : TrainListItem()
    data class TrainItem(val train: Train) : TrainListItem()
}

fun buildTrainListItems(trains: List<Train>): List<TrainListItem> {
    if (trains.isEmpty()) return emptyList()

    val platform1Trains = trains.filter {
        normalizePlatform(it.platform) == "1"
    }.sortedBy { it.timeToArrival }

    val otherTrains = trains.filter {
        normalizePlatform(it.platform) != "1"
    }.sortedWith(compareBy<Train> {
        when (normalizePlatform(it.platform)) {
            "2" -> 2
            "3" -> 3
            else -> 99
        }
    }.thenBy { it.timeToArrival })

    val items = mutableListOf<TrainListItem>()
    if (platform1Trains.isNotEmpty()) {
        items.add(TrainListItem.Header("站台1"))
        platform1Trains.forEach { items.add(TrainListItem.TrainItem(it)) }
    }

    var currentPlatform: String? = null
    otherTrains.forEach { train ->
        val platform = normalizePlatform(train.platform)
        if (platform != currentPlatform) {
            currentPlatform = platform
            items.add(TrainListItem.Header(getPlatformDisplayName(platform)))
        }
        items.add(TrainListItem.TrainItem(train))
    }

    return items
}

fun normalizePlatform(platform: String): String {
    val trimmed = platform.trim()
    val number = Regex("\\d+").find(trimmed)?.value
    return number ?: trimmed
}

fun getPlatformDisplayName(platform: String): String {
    val normalized = normalizePlatform(platform)
    return if (normalized.isNotBlank()) "站台$normalized" else "站台"
}
