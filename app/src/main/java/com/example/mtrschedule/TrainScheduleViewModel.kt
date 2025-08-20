package com.example.mtrschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtrschedule.model.Station
import com.example.mtrschedule.repository.TrainScheduleRepository
import kotlinx.coroutines.launch

class TrainScheduleViewModel : ViewModel() {

    private val repository = TrainScheduleRepository()

    private val _stationSchedules = MutableLiveData<List<Station>>()
    val stationSchedules: LiveData<List<Station>> = _stationSchedules

    private val _selectedStation = MutableLiveData<Station>()
    val selectedStation: LiveData<Station> = _selectedStation

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        // Load all stations basic info when ViewModel is created
        loadAllStations()
    }

    // Load all stations without detailed schedules
    private fun loadAllStations() {
        val allStations = repository.getAllStationsBasicInfo()
        _stationSchedules.value = allStations
    }

    // Fetch multiple station schedules (limited for performance)
    fun fetchStationSchedules(limit: Int = 10) {
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            val result = repository.getStationSchedules(limit)
            result.fold(
                onSuccess = { stations ->
                    _stationSchedules.value = stations
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load station schedules"
                    _isLoading.value = false
                }
            )
        }
    }

    // Fetch a single station schedule
    fun fetchStationSchedule(stationId: String) {
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            val result = repository.getStationSchedule(stationId)
            result.fold(
                onSuccess = { station ->
                    _selectedStation.value = station
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load station schedule"
                    _isLoading.value = false
                }
            )
        }
    }

    // Filter stations by search query
    fun searchStations(query: String) {
        if (query.isEmpty()) {
            loadAllStations()
            return
        }

        val allStations = repository.getAllStationsBasicInfo()
        val filteredStations = allStations.filter { station ->
            station.stationName.contains(query, ignoreCase = true) ||
                    station.stationId.contains(query, ignoreCase = true)
        }

        _stationSchedules.value = filteredStations
    }
}