package com.example.mtrschedule.model

data class Station(
    val stationId: String,
    val stationCode: String,
    val stationName: String,
    val nextTrains: List<Train>
)