package com.jinwind.mtrschedule.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object TrainScheduleDebugger {
    private const val TAG = "TrainDebugger"

    suspend fun directApiCall(stationId: String) = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://rt.data.gov.hk/v1/transport/mtr/lrt/getSchedule?station_id=$stationId")
                .build()

            val response = client.newCall(request).execute()
            Log.d(TAG, "Direct API call response code: ${response.code}")

            val responseBody = response.body?.string() ?: "Empty response"
            Log.d(TAG, "Direct API call response: $responseBody")

            responseBody
        } catch (e: Exception) {
            Log.e(TAG, "Error in direct API call", e)
            "Error: ${e.message}"
        }
    }
}