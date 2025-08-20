package com.example.mtrschedule.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MtrApiService {
    @GET("lrt/getSchedule")
    suspend fun getNextTrains(@Query("station_id") stationId: String): Response<MtrApiResponse>
}