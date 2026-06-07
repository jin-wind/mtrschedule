package com.jinwind.mtrschedule.ui.routes

import androidx.compose.ui.graphics.Color

fun getRouteColor(routeNo: String): Color = when (routeNo) {
    "505" -> Color(0xFFD32F2F)
    "507" -> Color(0xFF388E3C)
    "610" -> Color(0xFFF57C00)
    "614" -> Color(0xFF0288D1)
    "614P" -> Color(0xFFEC407A)
    "615" -> Color(0xFFFBC02D)
    "615P" -> Color(0xFF689F38)
    "705" -> Color(0xFF7B1FA2)
    "706" -> Color(0xFFE64A19)
    "751" -> Color(0xFF1976D2)
    "761P" -> Color(0xFF43A047)
    else -> Color(0xFF9E9E9E)
}
