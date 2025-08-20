package com.example.mtrschedule.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Train(
    val trainId: String,
    val routeNumber: String,
    val destination: String,
    val platform: String,
    val eta: String,
    val timeToArrival: Int,
    val timestamp: String = ""
) : Parcelable