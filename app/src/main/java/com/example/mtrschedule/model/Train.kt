package com.example.mtrschedule.model

data class Train(
    val trainId: String,
    val routeNumber: String,
    val destination: String,
    val platform: String,
    val eta: String,
    val timeToArrival: Int,
    val timestamp: String = ""
)