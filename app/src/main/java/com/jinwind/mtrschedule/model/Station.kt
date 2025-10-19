package com.jinwind.mtrschedule.model

data class Station(
    val stationId: String,
    val stationCode: String,
    val stationName: String,
    val nextTrains: List<Train>,
    val isPinned: Boolean = false
)