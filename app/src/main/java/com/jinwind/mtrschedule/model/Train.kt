package com.jinwind.mtrschedule.model

data class Train(
    val trainId: String,
    val routeNumber: String,
    val destination: String,
    val platform: String,
    val eta: String,
    val timeToArrival: Int,
    val timestamp: String = "",
    val isDoubleCar: Boolean = true // 添加是否为双车厢的属性
)