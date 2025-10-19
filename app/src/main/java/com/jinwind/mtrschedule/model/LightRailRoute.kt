package com.jinwind.mtrschedule.model

/**
 * 轻铁路线数据模型，包含路线号码和经过的站台ID列表
 */
data class LightRailRoute(
    val routeNumber: String,            // 路线号码，如 "505"
    val stations: List<String>,         // 路线经过的站台ID列表
    val startStation: String,           // 起始站名称
    val endStation: String,             // 终点站名称
    val isCircular: Boolean = false     // 是否为环形路线
)
