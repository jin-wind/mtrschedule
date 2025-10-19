package com.jinwind.mtrschedule

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrainScheduleViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrainScheduleViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

