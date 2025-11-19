package com.jinwind.mtrschedule

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jinwind.mtrschedule.data.BusStopMap
import com.jinwind.mtrschedule.data.LightRailRouteData

import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.repository.TrainScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrainScheduleViewModel(private val app: Application) : ViewModel() {

    private val repository = TrainScheduleRepository()

    private val _stationSchedules = MutableLiveData<List<Station>>()
    val stationSchedules: LiveData<List<Station>> = _stationSchedules

    private val _selectedStation = MutableLiveData<Station>()
    val selectedStation: LiveData<Station> = _selectedStation

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Bus schedule LiveData
    private val _busSchedule = MutableLiveData<List<Station>>()
    val busSchedule: LiveData<List<Station>> = _busSchedule

    private val PREFS_NAME = "AppPreferences"
    private val PREFS_PINNED_KEY = "pinned_station_ids"
    private val PREFS_CACHE_TIMESTAMP_KEY = "station_cache_timestamp"

    // 常用路线列表（可自行调整）
    private val commonRoutes = listOf("615", "615P", "610", "614")

    // 站点数据缓存
    private val stationCache = mutableMapOf<String, Station>()
    // 路线数据缓存映射 <路线号，站点ID列表>
    private val routeStationsCache = mutableMapOf<String, List<Station>>()

    init {
        // 加载所有站点基本信息
        loadAllStations()

        // 自动预加载常用路线数据
        preloadCommonRoutes()
    }

    private fun savePinnedStations(ids: List<String>) {
        val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(PREFS_PINNED_KEY, ids.joinToString(",")).apply()
    }

    private fun loadPinnedStations(): Set<String> {
        val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val str = prefs.getString(PREFS_PINNED_KEY, "") ?: ""
        return str.split(",").filter { it.isNotBlank() }.toSet()
    }

    /**
     * 预加载常用路线数据
     */
    private fun preloadCommonRoutes() {
        viewModelScope.launch {
            // 检查缓存时间戳
            val prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastCacheTime = prefs.getLong(PREFS_CACHE_TIMESTAMP_KEY, 0)
            val currentTime = System.currentTimeMillis()

            // 如果缓存时间超过30分钟，则更新缓存
            if (currentTime - lastCacheTime > 30 * 60 * 1000) {
                commonRoutes.forEach { routeNumber ->
                    val route = LightRailRouteData.getRoute(routeNumber) ?: return@forEach
                    // 后台加载路线站点数据
                    loadRouteStationsInBackground(route.stations)
                }

                // 更新缓存时间戳
                prefs.edit().putLong(PREFS_CACHE_TIMESTAMP_KEY, currentTime).apply()
            }
        }
    }

    /**
     * 在后台加载路线站点数据
     */
    private fun loadRouteStationsInBackground(stationIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferredResults = stationIds.map { stationId ->
                async(Dispatchers.IO) {
                    if (!stationCache.containsKey(stationId)) {
                        val result = repository.getStationSchedule(stationId)
                        result.fold(
                            onSuccess = { station ->
                                stationCache[stationId] = station
                            },
                            onFailure = { /* 忽略加载失败的站点 */ }
                        )
                    }
                }
            }

            // 等待所有请求完成
            deferredResults.forEach { it.await() }
        }
    }

    /**
     * 缓存站点数据
     */
    private fun cacheStationData(stations: List<Station>) {
        stations.forEach { station ->
            stationCache[station.stationId] = station
        }
    }

    /**
     * 从缓存中获取站点数据
     */
    fun getStationFromCache(stationId: String): Station? {
        return stationCache[stationId]
    }

    /**
     * 检查路线数据是否已缓存
     */
    fun isRouteCached(routeNumber: String): Boolean {
        val route = LightRailRouteData.getRoute(routeNumber) ?: return false
        return route.stations.all { stationId -> stationCache.containsKey(stationId) }
    }

    /**
     * 从缓存中获取路线站点数据
     */
    fun getRouteStationsFromCache(routeNumber: String): List<Station>? {
        val route = LightRailRouteData.getRoute(routeNumber) ?: return null
        val stations = route.stations.mapNotNull { stationId -> stationCache[stationId] }
        return if (stations.size == route.stations.size) stations else null
    }

    // Load all stations without detailed schedules
    fun loadAllStations() {
        val allStations = repository.getAllStationsBasicInfo()
        val pinnedIds = loadPinnedStations()
        val withPinned = allStations.map { s ->
            if (s.stationId in pinnedIds) s.copy(isPinned = true) else s.copy(isPinned = false)
        }
        _stationSchedules.value = withPinned
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

    // 带回调的站点数据加载方法
    fun fetchStationScheduleWithCallback(stationId: String, callback: (Boolean) -> Unit) {
        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            val result = repository.getStationSchedule(stationId)
            result.fold(
                onSuccess = { station ->
                    _selectedStation.value = station
                    _isLoading.value = false
                    callback(true)  // 成功加载数据，回调返回true
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Failed to load station schedule"
                    _isLoading.value = false
                    callback(false)  // 加载失败，回调返回false
                }
            )
        }
    }

    // 批量获取多个站点的数据
    fun fetchMultipleStationSchedules(stationIds: List<String>) {
        if (stationIds.isEmpty()) return

        _isLoading.value = true
        _error.value = ""

        viewModelScope.launch {
            val stations = mutableListOf<Station>()
            var hasError = false

            // 使用协程并行加载所有站点数据
            val deferredResults = stationIds.map { stationId ->
                async(Dispatchers.IO) {
                    repository.getStationSchedule(stationId)
                }
            }

            // 等待所有请求完成并处理结果
            deferredResults.forEach { deferred ->
                try {
                    val result = deferred.await()
                    result.fold(
                        onSuccess = { station ->
                            synchronized(stations) {
                                stations.add(station)
                            }
                        },
                        onFailure = { exception ->
                            hasError = true
                        }
                    )
                } catch (e: Exception) {
                    hasError = true
                }
            }

            if (stations.isNotEmpty()) {
                _stationSchedules.value = _stationSchedules.value?.let { currentList ->
                    val updatedList = currentList.toMutableList()
                    // 更新已有的站点数据
                    stations.forEach { newStation ->
                        val index = updatedList.indexOfFirst { it.stationId == newStation.stationId }
                        if (index != -1) {
                            updatedList[index] = newStation
                        } else {
                            updatedList.add(newStation)
                        }
                    }
                    updatedList
                } ?: stations

                // 将加载的站点数据保存到缓存
                cacheStationData(stations)
            }

            if (hasError) {
                _error.value = "部分站点数据加载失败"
            }

            _isLoading.value = false
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

    fun togglePinStation(stationId: String) {
        val current = _stationSchedules.value ?: return
        val updated = current.map {
            if (it.stationId == stationId) it.copy(isPinned = !it.isPinned)
            else it
        }
        _stationSchedules.value = updated
        // 保存所有 isPinned=true 的站点ID
        savePinnedStations(updated.filter { it.isPinned }.map { it.stationId })
    }

    // 重置所有置顶站点的状态
    fun resetPinnedStations() {
        // 清除SharedPreferences中保存的置顶站点IDs
        savePinnedStations(emptyList())

        // 更新内存中的站点列表，将所有站点的isPinned设置为false
        val current = _stationSchedules.value ?: return
        val updated = current.map { it.copy(isPinned = false) }
        _stationSchedules.value = updated
    }

    fun getBusSchedule(routeName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getBusSchedule(routeName)
            if (result.isSuccess) {
                val response = result.getOrNull()
                val stations = response?.busStop?.map { busStop ->
                    Station(
                        stationId = busStop.busStopId,
                        stationCode = busStop.busStopId,
                        stationName = BusStopMap.getName(busStop.busStopId),
                        nextTrains = busStop.bus?.map { busTime ->
                            // Use departure time if arrival time is empty (e.g. at terminus)
                            val timeText = if (!busTime.arrivalTimeText.isNullOrEmpty()) {
                                busTime.arrivalTimeText
                            } else {
                                busTime.departureTimeText ?: ""
                            }
                            
                            com.jinwind.mtrschedule.model.Train(
                                trainId = busTime.busId ?: "",
                                routeNumber = routeName,
                                destination = "",
                                platform = "",
                                eta = timeText,
                                timeToArrival = 0, // TODO: Parse time
                                timestamp = "",
                                isDoubleCar = false
                            )
                        } ?: emptyList()
                    )
                } ?: emptyList()
                _busSchedule.value = stations
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to get bus schedule"
            }
            _isLoading.value = false
        }
    }
}