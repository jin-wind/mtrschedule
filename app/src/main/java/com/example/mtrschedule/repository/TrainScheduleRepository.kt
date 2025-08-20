package com.example.mtrschedule.repository

import android.util.Log
import com.example.mtrschedule.api.RetrofitClient
import com.example.mtrschedule.data.MtrStationList
import com.example.mtrschedule.model.Station
import com.example.mtrschedule.model.Train
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TrainScheduleRepository {

    private val mtrApiService = RetrofitClient.mtrApiService
    private val TAG = "TrainScheduleRepository"

    // Get current timestamp in UTC
    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date())
    }

    // Get schedule for a single station
    suspend fun getStationSchedule(stationId: String): Result<Station> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Making API request to get train schedule for station $stationId")
            val response = mtrApiService.getNextTrains(stationId)
            Log.d(TAG, "API response code: ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d(TAG, "API response status: ${apiResponse?.status}")

                if (apiResponse != null && apiResponse.status == 1) {
                    // Find station info in our static list
                    val stationInfo = MtrStationList.stations.find { it.id == stationId }

                    if (stationInfo == null) {
                        return@withContext Result.failure(Exception("Station info not found for ID: $stationId"))
                    }

                    // Create trains list from all platforms and their routes
                    val trains = mutableListOf<Train>()
                    val currentTimestamp = getCurrentTimestamp()

                    apiResponse.platformList.forEach { platform ->
                        platform.routeList.forEach { route ->
                            // Parse minutes from time_en (e.g., "8 mins" -> 8, "Arriving" -> 0)
                            val minutes = when {
                                route.timeEn.equals("Arriving", ignoreCase = true) -> 0
                                else -> route.timeEn.split(" ")[0].toIntOrNull() ?: 0
                            }

                            trains.add(
                                Train(
                                    trainId = "${platform.platformId}_${route.routeNo}",
                                    routeNumber = route.routeNo,
                                    destination = route.destinationCh,
                                    platform = "Platform ${platform.platformId}",
                                    eta = route.timeCh,
                                    timeToArrival = minutes,
                                    timestamp = currentTimestamp
                                )
                            )
                        }
                    }

                    val station = Station(
                        stationId = stationId,
                        stationCode = stationId,
                        stationName = stationInfo.nameChi,
                        nextTrains = trains.sortedBy { it.timeToArrival }
                    )
                    Result.success(station)
                } else {
                    Log.e(TAG, "Invalid API response: status=${apiResponse?.status}")
                    Result.failure(Exception("Invalid API response: status=${apiResponse?.status}"))
                }
            } else {
                val errorMessage = "API Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in API call", e)
            Result.failure(e)
        }
    }

    // Get schedules for multiple stations
    suspend fun getStationSchedules(limit: Int = 10): Result<List<Station>> = withContext(Dispatchers.IO) {
        try {
            // Get first few stations for better performance
            val stationIds = MtrStationList.stations.take(limit).map { it.id }

            val stations = mutableListOf<Station>()

            for (stationId in stationIds) {
                val result = getStationSchedule(stationId)

                if (result.isSuccess) {
                    result.getOrNull()?.let { stations.add(it) }
                } else {
                    Log.e(TAG, "Failed to get schedule for station $stationId: ${result.exceptionOrNull()?.message}")
                }
            }

            if (stations.isNotEmpty()) {
                Result.success(stations)
            } else {
                Result.failure(Exception("Failed to load any station data"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching multiple stations", e)
            Result.failure(e)
        }
    }

    // Get all station basic info (without schedules)
    fun getAllStationsBasicInfo(): List<Station> {
        return MtrStationList.stations.map { stationInfo ->
            Station(
                stationId = stationInfo.id,
                stationCode = stationInfo.id,
                stationName = stationInfo.nameChi,
                nextTrains = emptyList()
            )
        }
    }
}