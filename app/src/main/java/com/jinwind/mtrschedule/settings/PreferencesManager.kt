package com.jinwind.mtrschedule.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

/**
 * 设置和站点顺序管理类
 * 负责保存和读取用户设置和站点顺序
 */
class PreferencesManager(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "AppPreferences"
        private const val PREFS_STATION_ORDER_KEY = "station_order"
        private const val PREFS_DEFAULT_STATION_KEY = "default_station"
        private const val PREFS_APP_THEME_KEY = "app_theme"
        private const val PREFS_LANGUAGE_KEY = "app_language"
        private const val PREFS_TOPPED_STATIONS_KEY = "topped_stations" // 新增：已顶置站点的键
        private const val PREFS_WIDGET_STATION_SOURCE_KEY = "widget_station_source"
        private const val PREFS_WIDGET_STATION_ID_KEY = "widget_station_id"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    
    // 保存站点顺序
    fun saveStationOrder(order: List<String>) {
        sharedPreferences.edit().putString(PREFS_STATION_ORDER_KEY, order.joinToString(",")).apply()
    }
    
    // 获取站点顺序
    fun getStationOrder(): List<String> {
        val orderString = sharedPreferences.getString(PREFS_STATION_ORDER_KEY, "")
        return if (orderString.isNullOrEmpty()) {
            emptyList()
        } else {
            orderString.split(",")
        }
    }
    
    // 保存默认站点
    fun saveDefaultStation(stationId: String) {
        sharedPreferences.edit().putString(PREFS_DEFAULT_STATION_KEY, stationId).apply()
    }
    
    // 获取默认站点
    fun getDefaultStation(): String {
        return sharedPreferences.getString(PREFS_DEFAULT_STATION_KEY, "240") ?: "240"
    }

    // 保存应用主题设置
    fun saveAppTheme(theme: String) {
        sharedPreferences.edit().putString(PREFS_APP_THEME_KEY, theme).apply()
    }

    // 获取应用主题设置
    fun getAppTheme(): String {
        return sharedPreferences.getString(PREFS_APP_THEME_KEY, "system") ?: "system"
    }

    // 保存应用语言设置
    fun saveLanguage(language: String) {
        sharedPreferences.edit().putString(PREFS_LANGUAGE_KEY, language).apply()
    }

    // 获取应用语言设置
    fun getLanguage(): String {
        return sharedPreferences.getString(PREFS_LANGUAGE_KEY, "system") ?: "system"
    }

    // 小组件站点来源设置
    fun saveWidgetStationSource(source: String) {
        sharedPreferences.edit().putString(PREFS_WIDGET_STATION_SOURCE_KEY, source).apply()
    }

    fun getWidgetStationSource(): String {
        return sharedPreferences.getString(PREFS_WIDGET_STATION_SOURCE_KEY, "default") ?: "default"
    }

    fun saveWidgetStationId(stationId: String) {
        sharedPreferences.edit().putString(PREFS_WIDGET_STATION_ID_KEY, stationId).apply()
    }

    fun getWidgetStationId(): String {
        return sharedPreferences.getString(PREFS_WIDGET_STATION_ID_KEY, "") ?: ""
    }

    // 新增：保存已顶置的站点列表
    fun saveStationTopped(stationId: String) {
        val toppedStations = getToppedStations().toMutableSet()
        toppedStations.add(stationId)
        sharedPreferences.edit().putStringSet(PREFS_TOPPED_STATIONS_KEY, toppedStations).apply()
    }

    // 新增：获取所有已顶置的站点
    fun getToppedStations(): Set<String> {
        return sharedPreferences.getStringSet(PREFS_TOPPED_STATIONS_KEY, emptySet()) ?: emptySet()
    }

    // 新增：检查站点是否已被顶置过
    fun isStationTopped(stationId: String): Boolean {
        return getToppedStations().contains(stationId)
    }

    // 新增：清除所有已顶置站点记录
    fun clearToppedStations() {
        sharedPreferences.edit().remove(PREFS_TOPPED_STATIONS_KEY).apply()
    }

    // 新增：清除站点顺序
    fun clearStationOrder() {
        sharedPreferences.edit().remove(PREFS_STATION_ORDER_KEY).apply()
    }
}
