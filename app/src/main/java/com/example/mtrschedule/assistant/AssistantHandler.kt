package com.example.mtrschedule.assistant

import com.example.mtrschedule.data.MtrStationList
import com.example.mtrschedule.model.Station
import com.example.mtrschedule.repository.TrainScheduleRepository
import kotlinx.coroutines.runBlocking

class AssistantHandler {

    private val repository = TrainScheduleRepository()

    fun handleVoiceCommand(command: String): String {
        // Simple command parsing
        return when {
            command.contains("next train", ignoreCase = true) -> {
                getNextTrainInfo(command)
            }
            command.contains("train to", ignoreCase = true) -> {
                getTrainToDestination(command)
            }
            else -> {
                "Sorry, I didn't understand that command. Try asking about the next train or trains to a specific destination."
            }
        }
    }

    private fun getNextTrainInfo(command: String): String {
        // Extract station name from command
        val stationName = extractStationName(command) ?: return "Please specify a station name."

        // Find matching station
        val stationBasic = MtrStationList.stations.find {
            it.nameEn.contains(stationName, ignoreCase = true)
        } ?: return "Station '$stationName' not found."

        // Get station details with schedule
        return runBlocking {
            val result = repository.getStationSchedule(stationBasic.id)
            result.fold(
                onSuccess = { station ->
                    formatStationSchedule(station)
                },
                onFailure = {
                    "Sorry, couldn't get schedule information for $stationName."
                }
            )
        }
    }

    private fun getTrainToDestination(command: String): String {
        // Implementation for finding trains to a specific destination
        return "Feature not implemented yet."
    }

    private fun extractStationName(command: String): String? {
        // Simple extraction of station name - would need more sophisticated NLP in real app
        val pattern = "(?:at|for|from)\\s+([A-Za-z\\s]+)(?:\\s+station)?".toRegex()
        return pattern.find(command)?.groupValues?.getOrNull(1)
    }

    private fun formatStationSchedule(station: Station): String {
        if (station.nextTrains.isEmpty()) {
            return "No trains scheduled at ${station.stationName} at this time."
        }

        val nextTrain = station.nextTrains.first()
        val sb = StringBuilder()

        sb.append("Next train at ${station.stationName}: ")
        sb.append("Route ${nextTrain.routeNumber} to ${nextTrain.destination} ")

        sb.append(
            if (nextTrain.timeToArrival == 0) "is arriving now"
            else "in ${nextTrain.timeToArrival} minutes"
        )

        if (station.nextTrains.size > 1) {
            val secondTrain = station.nextTrains[1]
            sb.append(". Following train: Route ${secondTrain.routeNumber} to ${secondTrain.destination} ")
            sb.append(
                if (secondTrain.timeToArrival == 0) "is arriving now"
                else "in ${secondTrain.timeToArrival} minutes"
            )
        }

        return sb.toString()
    }
}