package com.jinwind.mtrschedule.repository

import android.util.Log
import com.jinwind.mtrschedule.api.RetrofitClient
import com.jinwind.mtrschedule.data.MtrStationList
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.model.Train
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TrainScheduleRepository {

    private val mtrApiService = RetrofitClient.mtrApiService
    private val TAG = "TrainScheduleRepository"

    // Get current timestamp in Hong Kong Time (UTC+8)
    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
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

                if (apiResponse != null) {
                    // Find station info in our static list
                    val stationInfo = MtrStationList.stations.find { it.id == stationId }

                    if (stationInfo == null) {
                        return@withContext Result.failure(Exception("Station info not found for ID: $stationId"))
                    }

                    // 当status=0时，表示API正常但暂无数据，返回一个没有列车的站台
                    if (apiResponse.status == 0) {
                        Log.d(TAG, "API returned status=0, possibly no trains available for station $stationId")
                        val station = Station(
                            stationId = stationId,
                            stationCode = stationId,
                            stationName = stationInfo.nameChi,
                            nextTrains = emptyList()
                        )
                        return@withContext Result.success(station)
                    }

                    // 正常处理status=1的情况
                    if (apiResponse.status == 1) {
                        // Create trains list from all platforms and their routes
                        val trains = mutableListOf<Train>()
                        val currentTimestamp = getCurrentTimestamp()

                        // 添加空值检查，确保platformList不为null
                        if (apiResponse.platformList != null) {
                            apiResponse.platformList.forEach { platform ->
                                // 添加空值检查，确保routeList不为null
                                if (platform.routeList != null) {
                                    platform.routeList.forEach { route ->
                                        // Parse minutes from time_en (e.g., "8 mins" -> 8, "Arriving" -> 0)
                                        val minutes = when {
                                            route.timeEn.equals("Arriving", ignoreCase = true) -> 0
                                            else -> route.timeEn.split(" ")[0].toIntOrNull() ?: 0
                                        }

                                        // 设置是否为双车厢：当train_length为2时表示双车厢
                                        val isDoubleCar = route.trainLength == 2

                                        // 添加日志，查看API返回的train_length值
                                        Log.d(TAG, "Train ${route.routeNo} to ${route.destinationCh} has train_length: ${route.trainLength}, isDoubleCar: $isDoubleCar")

                                        trains.add(
                                            Train(
                                                trainId = "${platform.platformId}_${route.routeNo}",
                                                routeNumber = route.routeNo,
                                                destination = route.destinationCh,
                                                platform = "站台 ${platform.platformId}",
                                                eta = route.timeCh,
                                                timeToArrival = minutes,
                                                timestamp = currentTimestamp,
                                                isDoubleCar = isDoubleCar
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        val station = Station(
                            stationId = stationId,
                            stationCode = stationId,
                            stationName = stationInfo.nameChi,
                            nextTrains = trains.sortedBy { it.timeToArrival }
                        )
                        return@withContext Result.success(station)
                    }

                    // 如果status既不是0也不是1，则视为异常
                    Log.e(TAG, "Unexpected API response status: ${apiResponse.status}")
                    return@withContext Result.failure(Exception("Unexpected API response status: ${apiResponse.status}"))
                } else {
                    Log.e(TAG, "API response body is null")
                    return@withContext Result.failure(Exception("API response body is null"))
                }
            } else {
                val errorMessage = "API Error: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMessage)
                return@withContext Result.failure(Exception(errorMessage))
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
                nextTrains = emptyList(),
                isPinned = false
            )
        }
    }

    // Get bus schedule
    suspend fun getBusSchedule(routeName: String, language: String = "zh"): Result<com.jinwind.mtrschedule.model.MtrBusResponse> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Making API request to get bus schedule for route $routeName")
            val request = com.jinwind.mtrschedule.model.MtrBusRequest(language, routeName)
            val response = mtrApiService.getBusSchedule(request)
            Log.d(TAG, "API response code: ${response.code()}")

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null) {
                    return@withContext Result.success(apiResponse)
                } else {
                    return@withContext Result.failure(Exception("API response body is null"))
                }
            } else {
                return@withContext Result.failure(Exception("API request failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting bus schedule", e)
            return@withContext Result.failure(e)
        }
    }
}