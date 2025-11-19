package com.jinwind.mtrschedule.api

import com.jinwind.mtrschedule.model.MtrBusRequest
import com.jinwind.mtrschedule.model.MtrBusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MtrApiService {
    @GET("lrt/getSchedule")
    suspend fun getNextTrains(@Query("station_id") stationId: String): Response<MtrApiResponse>

    @POST("bus/getSchedule")
    suspend fun getBusSchedule(@Body request: MtrBusRequest): Response<MtrBusResponse>
}