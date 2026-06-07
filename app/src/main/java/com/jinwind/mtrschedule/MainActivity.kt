package com.jinwind.mtrschedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.jinwind.mtrschedule.settings.SettingsManager
import com.jinwind.mtrschedule.ui.theme.MtrApp

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TrainScheduleViewModel
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init ViewModel + SettingsManager (used by Compose screens)
        val factory = TrainScheduleViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[TrainScheduleViewModel::class.java]
        settingsManager = SettingsManager.getInstance(this)

        // Load default station schedule
        val defaultStationId = settingsManager.getDefaultStationId()
        viewModel.fetchStationSchedule(defaultStationId)

        // Pre-load all stations
        viewModel.loadAllStations()

        // Compose UI
        setContentView(
            ComposeView(this).apply {
                setContent {
                    MtrApp(viewModel = viewModel)
                }
            }
        )
    }

    /**
     * Called by RouteModeFragment when a station is selected.
     * The Compose UI (StationListScreen) already observes selectedStation LiveData.
     */
    fun switchToCardMode() {
        // No-op: Compose UI already displays the selected station.
    }
}
