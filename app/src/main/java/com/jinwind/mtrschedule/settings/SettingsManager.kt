package com.jinwind.mtrschedule.settings

import android.content.Context
import com.jinwind.mtrschedule.model.Station

/**
 * 设置管理器单例类
 * 提供全局访问应用设置和站点顺序的功能
 */
class SettingsManager private constructor(context: Context) {

    private val preferencesManager = PreferencesManager(context.applicationContext)

    companion object {
        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context).also { instance = it }
            }
        }
    }

    // 站点顺序相关方法
    fun saveStationOrder(stationIds: List<String>) {
        preferencesManager.saveStationOrder(stationIds)
    }

    fun getStationOrder(): List<String> {
        return preferencesManager.getStationOrder()
    }

    // 顶置站点后保存顺序
    fun topStationAndSaveOrder(stations: List<Station>, stationId: String): List<Station> {
        val stationsList = stations.toMutableList()
        val index = stationsList.indexOfFirst { it.stationId == stationId }

        if (index != -1) {
            // 将站点移到列表顶部
            val station = stationsList.removeAt(index)
            stationsList.add(0, station)

            // 保存新的顺序
            saveStationOrder(stationsList.map { it.stationId })

            // 记录该站点已被顶置过
            markStationAsTopped(stationId)
        }

        return stationsList
    }

    // 根据保存的顺序对站点列表进行排序
    fun sortStationsByOrder(stations: List<Station>): List<Station> {
        val savedOrder = getStationOrder()

        if (savedOrder.isEmpty()) {
            return stations
        }

        // 按保存顺序排序，未在保存列表的站台排在后面
        val stationMap = stations.associateBy { it.stationId }
        return savedOrder.mapNotNull { stationMap[it] } + stations.filter { it.stationId !in savedOrder }
    }

    // 新增：标记站点已被顶置
    fun markStationAsTopped(stationId: String) {
        preferencesManager.saveStationTopped(stationId)
    }

    // 新增：检查站点是否已被顶置过
    fun isStationTopped(stationId: String): Boolean {
        return preferencesManager.isStationTopped(stationId)
    }

    // 新增：获取所有已顶置的站点
    fun getToppedStations(): Set<String> {
        return preferencesManager.getToppedStations()
    }

    // 新增：清除所有已顶置站点记录
    fun clearToppedStations() {
        preferencesManager.clearToppedStations()
    }

    // 新增：清除所有顶置站点记录和站点顺序，完全恢复默认排序
    fun resetToDefaultOrder() {
        // 清除顶置站点记录
        preferencesManager.clearToppedStations()
        // 清除站点顺序
        preferencesManager.clearStationOrder()
    }

    // 默认站点相关方法
    fun setDefaultStation(station: Station) {
        preferencesManager.saveDefaultStation(station.stationId)
    }

    fun setDefaultStation(stationId: String) {
        preferencesManager.saveDefaultStation(stationId)
    }

    fun getDefaultStationId(): String {
        return preferencesManager.getDefaultStation()
    }

    // 应用主题相关方法
    fun setAppTheme(theme: String) {
        preferencesManager.saveAppTheme(theme)
    }

    fun getAppTheme(): String {
        return preferencesManager.getAppTheme()
    }

    // 应用语言相关方法
    fun setLanguage(language: String) {
        preferencesManager.saveLanguage(language)
    }

    fun getLanguage(): String {
        return preferencesManager.getLanguage()
    }

    // 小组件站点来源设置
    fun setWidgetStationSource(source: String) {
        preferencesManager.saveWidgetStationSource(source)
    }

    fun getWidgetStationSource(): String {
        return preferencesManager.getWidgetStationSource()
    }

    fun setWidgetStationId(stationId: String) {
        preferencesManager.saveWidgetStationId(stationId)
    }

    fun getWidgetStationId(): String {
        return preferencesManager.getWidgetStationId()
    }
}
