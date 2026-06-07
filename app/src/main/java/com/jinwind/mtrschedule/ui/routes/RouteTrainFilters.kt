package com.jinwind.mtrschedule.ui.routes

import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.model.Train

private val allPlatformsStations = listOf("屯门码头", "屯門碼頭", "Tuen Mun Ferry Pier", "三聖", "三圣", "Sam Shing")
private val mergePlatformsStations = listOf("田景", "Tin King")
private val cityStations = listOf("市中心", "Town Centre", "市中心站")

fun platformFilterForDirection(isReverse: Boolean): String = if (isReverse) "2" else "1"

fun filteredRouteTrains(station: Station, platformFilter: String): List<Train> {
    return when {
        isSpecialStation(station.stationName, allPlatformsStations) -> station.nextTrains.sortedBy { it.timeToArrival }
        isSpecialStation(station.stationName, mergePlatformsStations) -> {
            val trainsToShow = mutableListOf<Train>()
            if (platformFilter == "1") {
                trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "1"))
            } else if (platformFilter == "2") {
                trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "2"))
                trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "3"))
            }
            trainsToShow.sortedBy { it.timeToArrival }
        }
        isSpecialStation(station.stationName, cityStations) -> {
            val trainsToShow = mutableListOf<Train>()
            if (platformFilter == "1") {
                trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "1"))
            } else if (platformFilter == "2") {
                trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "4"))
            }
            trainsToShow.sortedBy { it.timeToArrival }
        }
        else -> filterTrainsByPlatform(station.nextTrains, platformFilter).sortedBy { it.timeToArrival }
    }
}

private fun isSpecialStation(stationName: String, specialStations: List<String>): Boolean {
    return specialStations.any { stationName.contains(it, ignoreCase = true) }
}

private fun filterTrainsByPlatform(trains: List<Train>, platformNumber: String): List<Train> {
    if (trains.isNotEmpty() && trains.all { it.platform.isEmpty() }) {
        return trains
    }

    return trains.filter { train ->
        train.platform == platformNumber ||
            train.platform == "站台$platformNumber" ||
            train.platform == "站台 $platformNumber" ||
            train.platform == "Platform $platformNumber"
    }
}
