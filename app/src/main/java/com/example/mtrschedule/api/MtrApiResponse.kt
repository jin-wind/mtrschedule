package com.example.mtrschedule.api

import com.google.gson.annotations.SerializedName

data class MtrApiResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("system_time") val systemTime: String,
    @SerializedName("platform_list") val platformList: List<Platform> = emptyList()
)

data class Platform(
    @SerializedName("platform_id") val platformId: Int,
    @SerializedName("route_list") val routeList: List<Route> = emptyList()
)

data class Route(
    @SerializedName("train_length") val trainLength: Int,
    @SerializedName("arrival_departure") val arrivalDeparture: String,
    @SerializedName("dest_en") val destinationEn: String,
    @SerializedName("dest_ch") val destinationCh: String,
    @SerializedName("time_en") val timeEn: String,
    @SerializedName("time_ch") val timeCh: String,
    @SerializedName("route_no") val routeNo: String,
    @SerializedName("stop") val stop: Int
)