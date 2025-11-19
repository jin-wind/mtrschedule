package com.jinwind.mtrschedule.model

import com.google.gson.annotations.SerializedName

data class MtrBusRequest(
    val language: String,
    val routeName: String
)

data class MtrBusResponse(
    val busStop: List<MtrBusStop>?,
    val routeName: String?,
    val footerRemarks: String?
)

data class MtrBusStop(
    val busStopId: String,
    val bus: List<MtrBusTime>?,
    val busIcon: String?,
    val isSuspended: String?
)

data class MtrBusTime(
    val arrivalTimeText: String?,
    val departureTimeText: String?,
    val isScheduled: String?,
    val busId: String?,
    val isDelayed: String?
)
