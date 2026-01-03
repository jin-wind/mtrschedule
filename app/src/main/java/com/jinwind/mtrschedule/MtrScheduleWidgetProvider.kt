package com.jinwind.mtrschedule

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.jinwind.mtrschedule.repository.TrainScheduleRepository
import com.jinwind.mtrschedule.settings.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MtrScheduleWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, MtrScheduleWidgetProvider::class.java)
            )
            appWidgetIds.forEach { appWidgetId ->
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_mtr_schedule)
        bindIntents(context, views)
        views.setTextViewText(R.id.widget_station_name, context.getString(R.string.widget_loading))
        views.setTextViewText(R.id.widget_next_train, context.getString(R.string.widget_loading))
        views.setTextViewText(R.id.widget_eta, "")
        views.setTextViewText(R.id.widget_platform_1_title, "")
        views.setTextViewText(R.id.widget_platform_2_title, "")
        clearPlatformRows(views, 1)
        clearPlatformRows(views, 2)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        refreshWidgetData(context, appWidgetManager, appWidgetId)
    }

    private fun bindIntents(context: Context, views: RemoteViews) {
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)

        val refreshIntent = Intent(context, MtrScheduleWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            refreshIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)
    }

    private fun refreshWidgetData(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val settingsManager = SettingsManager.getInstance(context)
            val stationId = resolveWidgetStationId(settingsManager)
            val repository = TrainScheduleRepository()
            val result = repository.getStationSchedule(stationId)
            val views = RemoteViews(context.packageName, R.layout.widget_mtr_schedule)
            bindIntents(context, views)

            if (result.isSuccess) {
                val station = result.getOrNull()
                if (station != null) {
                    views.setTextViewText(R.id.widget_station_name, station.stationName)
                    val nextTrain = station.nextTrains.firstOrNull()
                    if (nextTrain != null) {
                        val nextTrainText = "${nextTrain.routeNumber} - ${nextTrain.destination}"
                        views.setTextViewText(R.id.widget_next_train, nextTrainText)
                        val etaText = if (nextTrain.eta.isNotEmpty()) {
                            nextTrain.eta
                        } else if (nextTrain.timeToArrival <= 0) {
                            context.getString(R.string.arriving)
                        } else {
                            context.getString(R.string.minutes_format, nextTrain.timeToArrival)
                        }
                        views.setTextViewText(R.id.widget_eta, etaText)
                    } else {
                        views.setTextViewText(
                            R.id.widget_next_train,
                            context.getString(R.string.no_trains_available)
                        )
                        views.setTextViewText(R.id.widget_eta, "")
                    }
                    updatePlatformSections(context, views, station.nextTrains)
                } else {
                    views.setTextViewText(
                        R.id.widget_station_name,
                        context.getString(R.string.error_loading_data)
                    )
                    views.setTextViewText(R.id.widget_next_train, "")
                    views.setTextViewText(R.id.widget_eta, "")
                    clearPlatformSections(views)
                }
            } else {
                views.setTextViewText(
                    R.id.widget_station_name,
                    context.getString(R.string.error_loading_data)
                )
                views.setTextViewText(R.id.widget_next_train, "")
                views.setTextViewText(R.id.widget_eta, "")
                clearPlatformSections(views)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun resolveWidgetStationId(settingsManager: SettingsManager): String {
        return if (settingsManager.getWidgetStationSource() == "custom") {
            settingsManager.getWidgetStationId().ifEmpty {
                settingsManager.getDefaultStationId()
            }
        } else {
            settingsManager.getDefaultStationId()
        }
    }

    private fun updatePlatformSections(
        context: Context,
        views: RemoteViews,
        trains: List<com.jinwind.mtrschedule.model.Train>
    ) {
        val platform1 = trains.filter { normalizePlatform(it.platform) == "1" }
        val platform2 = trains.filter { normalizePlatform(it.platform) == "2" }

        val platform1Destination = platform1.firstOrNull()?.destination ?: ""
        val platform2Destination = platform2.firstOrNull()?.destination ?: ""

        views.setTextViewText(
            R.id.widget_platform_1_title,
            buildPlatformTitle(context, 1, platform1Destination)
        )
        views.setTextViewText(
            R.id.widget_platform_2_title,
            buildPlatformTitle(context, 2, platform2Destination)
        )

        updatePlatformRows(context, views, platform1, 1)
        updatePlatformRows(context, views, platform2, 2)
    }

    private fun updatePlatformRows(
        context: Context,
        views: RemoteViews,
        trains: List<com.jinwind.mtrschedule.model.Train>,
        platformNumber: Int
    ) {
        val topTrains = trains.sortedBy { it.timeToArrival }.take(3)
        val rows = listOf(
            PlatformRow(
                rowId = getRowId(platformNumber, 1),
                iconId = getRowIconId(platformNumber, 1),
                textId = getRowTextId(platformNumber, 1)
            ),
            PlatformRow(
                rowId = getRowId(platformNumber, 2),
                iconId = getRowIconId(platformNumber, 2),
                textId = getRowTextId(platformNumber, 2)
            ),
            PlatformRow(
                rowId = getRowId(platformNumber, 3),
                iconId = getRowIconId(platformNumber, 3),
                textId = getRowTextId(platformNumber, 3)
            )
        )

        rows.forEachIndexed { index, row ->
            val train = topTrains.getOrNull(index)
            if (train == null) {
                views.setViewVisibility(row.rowId, android.view.View.GONE)
            } else {
                val etaText = if (train.eta.isNotEmpty()) {
                    train.eta
                } else if (train.timeToArrival <= 0) {
                    context.getString(R.string.arriving)
                } else {
                    context.getString(R.string.minutes_format, train.timeToArrival)
                }
                val lineText = "${train.routeNumber} ${etaText}"
                val iconRes = if (train.isDoubleCar) {
                    R.drawable.ic_train_double
                } else {
                    R.drawable.ic_train_single
                }
                views.setImageViewResource(row.iconId, iconRes)
                views.setTextViewText(row.textId, lineText)
                views.setViewVisibility(row.rowId, android.view.View.VISIBLE)
            }
        }
    }

    private fun normalizePlatform(platform: String): String {
        return when {
            platform.contains("1") -> "1"
            platform.contains("2") -> "2"
            platform.contains("3") -> "3"
            platform.contains("4") -> "4"
            else -> platform.trim()
        }
    }

    private fun clearPlatformSections(views: RemoteViews) {
        views.setTextViewText(R.id.widget_platform_1_title, "")
        views.setTextViewText(R.id.widget_platform_2_title, "")
        clearPlatformRows(views, 1)
        clearPlatformRows(views, 2)
    }

    private fun buildPlatformTitle(context: Context, platformNumber: Int, destination: String): String {
        return if (destination.isNotEmpty()) {
            context.getString(R.string.platform_destination_format, platformNumber, destination)
        } else {
            context.getString(R.string.platform, platformNumber)
        }
    }

    private fun clearPlatformRows(views: RemoteViews, platformNumber: Int) {
        listOf(1, 2, 3).forEach { index ->
            views.setViewVisibility(getRowId(platformNumber, index), android.view.View.GONE)
        }
    }

    private fun getRowId(platformNumber: Int, index: Int): Int {
        return if (platformNumber == 1) {
            when (index) {
                1 -> R.id.widget_platform_1_row_1
                2 -> R.id.widget_platform_1_row_2
                else -> R.id.widget_platform_1_row_3
            }
        } else {
            when (index) {
                1 -> R.id.widget_platform_2_row_1
                2 -> R.id.widget_platform_2_row_2
                else -> R.id.widget_platform_2_row_3
            }
        }
    }

    private fun getRowIconId(platformNumber: Int, index: Int): Int {
        return if (platformNumber == 1) {
            when (index) {
                1 -> R.id.widget_platform_1_row_1_icon
                2 -> R.id.widget_platform_1_row_2_icon
                else -> R.id.widget_platform_1_row_3_icon
            }
        } else {
            when (index) {
                1 -> R.id.widget_platform_2_row_1_icon
                2 -> R.id.widget_platform_2_row_2_icon
                else -> R.id.widget_platform_2_row_3_icon
            }
        }
    }

    private fun getRowTextId(platformNumber: Int, index: Int): Int {
        return if (platformNumber == 1) {
            when (index) {
                1 -> R.id.widget_platform_1_row_1_text
                2 -> R.id.widget_platform_1_row_2_text
                else -> R.id.widget_platform_1_row_3_text
            }
        } else {
            when (index) {
                1 -> R.id.widget_platform_2_row_1_text
                2 -> R.id.widget_platform_2_row_2_text
                else -> R.id.widget_platform_2_row_3_text
            }
        }
    }

    private data class PlatformRow(
        val rowId: Int,
        val iconId: Int,
        val textId: Int
    )

    companion object {
        const val ACTION_REFRESH = "com.jinwind.mtrschedule.widget.ACTION_REFRESH"
    }
}
