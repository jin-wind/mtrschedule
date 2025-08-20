package com.example.mtrschedule

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtrschedule.databinding.ActivityMainBinding
import com.example.mtrschedule.model.Station
import com.example.mtrschedule.util.LanguageHelper
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), StationAdapter.StationClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TrainScheduleViewModel
    private lateinit var adapter: StationAdapter
    private var autoRefreshHandler: Handler? = null
    private var autoRefreshRunnable: Runnable? = null
    private var isAutoRefreshEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupSwipeToRefresh()
        setupSearchView()
        setupDisplayModeChips()
        setupLanguageToggle()
        observeData()
        startAutoRefresh()

        // Set initial timestamp
        updateTimestamp()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        
        // Handle navigation icon click (back button)
        binding.toolbar.setNavigationOnClickListener {
            showStationList()
        }
        
        // Initially hide navigation icon
        binding.toolbar.navigationIcon = null
    }

    private fun setupLanguageToggle() {
        binding.languageToggleButton.setOnClickListener {
            val isChineseNow = LanguageHelper.toggleLanguage(this)
            
            // Show feedback message
            val message = if (isChineseNow) {
                getString(R.string.language_changed).replace("English", "中文")
            } else {
                getString(R.string.language_changed)
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            
            // Recreate activity to apply new language
            recreate()
        }
    }

    private fun setupSearchView() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                viewModel.searchStations(query)
            }
        })
    }

    private fun setupDisplayModeChips() {
        binding.displayModeChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChip = findViewById<Chip>(checkedIds[0])
                when (selectedChip?.id) {
                    R.id.chipByStation -> {
                        // Currently showing by station (default mode)
                        viewModel.fetchStationSchedules()
                    }
                    R.id.chipByRoute -> {
                        // Switch to route-based view (to be implemented)
                        Toast.makeText(this, "Route view coming soon!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startAutoRefresh() {
        if (!isAutoRefreshEnabled) return
        
        autoRefreshHandler = Handler(Looper.getMainLooper())
        autoRefreshRunnable = Runnable {
            if (binding.stationDetailLayout.visibility == View.VISIBLE) {
                // Refresh current station detail
                val stationId = binding.stationIdText.tag as? String
                stationId?.let {
                    viewModel.fetchStationSchedule(it)
                }
            } else {
                // Refresh station list
                viewModel.fetchStationSchedules()
            }
            updateTimestamp()
            
            // Schedule next refresh
            startAutoRefresh()
        }
        
        autoRefreshHandler?.postDelayed(autoRefreshRunnable!!, 30000) // 30 seconds
    }

    private fun stopAutoRefresh() {
        autoRefreshHandler?.removeCallbacks(autoRefreshRunnable!!)
        autoRefreshHandler = null
        autoRefreshRunnable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoRefresh()
    }

    override fun onPause() {
        super.onPause()
        stopAutoRefresh()
    }

    override fun onResume() {
        super.onResume()
        if (isAutoRefreshEnabled) {
            startAutoRefresh()
        }
    }

    private fun updateTimestamp() {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        binding.timestampText.text = getString(R.string.last_updated_format, timestamp).replace("UTC", "")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TrainScheduleViewModel::class.java]
        // Load all stations initially instead of just one
        viewModel.fetchStationSchedules()
    }

    private fun setupRecyclerView() {
        adapter = StationAdapter(this)
        binding.stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stationsRecyclerView.adapter = adapter
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (binding.stationDetailLayout.visibility == View.VISIBLE) {
                val stationId = binding.stationIdText.tag as? String
                stationId?.let {
                    viewModel.fetchStationSchedule(it)
                }
            } else {
                viewModel.fetchStationSchedules()
            }
            updateTimestamp()
        }
    }

    private fun observeData() {
        viewModel.stationSchedules.observe(this) { stations ->
            adapter.submitList(stations)
            updateTimestamp()
        }

        viewModel.selectedStation.observe(this) { station ->
            displayStationDetail(station)
            updateTimestamp()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
            
            // Show/hide progress indicator
            binding.progressIndicator.visibility = if (isLoading && !binding.swipeRefreshLayout.isRefreshing)
                View.VISIBLE else View.GONE
                
            // Show/hide auto-refresh indicator
            binding.autoRefreshIndicator.visibility = if (isLoading && isAutoRefreshEnabled)
                View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showErrorState(errorMessage)
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            } else {
                hideErrorState()
            }
        }
    }

    private fun showErrorState(errorMessage: String) {
        binding.errorLayout.visibility = View.VISIBLE
        binding.errorText.text = errorMessage
        binding.stationListLayout.visibility = View.GONE
        binding.stationDetailLayout.visibility = View.GONE
        
        binding.retryButton.setOnClickListener {
            hideErrorState()
            viewModel.fetchStationSchedules()
        }
    }

    private fun hideErrorState() {
        binding.errorLayout.visibility = View.GONE
        binding.stationListLayout.visibility = View.VISIBLE
    }

    private fun displayStationDetail(station: Station) {
        // Show navigation icon and update toolbar
        binding.toolbar.navigationIcon = getDrawable(R.drawable.ic_arrow_back)
        binding.toolbar.title = station.stationName
        
        // Hide station list, show detail
        binding.stationListLayout.visibility = View.GONE
        binding.stationDetailLayout.visibility = View.VISIBLE

        // Update UI with station details
        binding.stationIdText.text = getString(R.string.station_code_label) + " " + station.stationId
        binding.stationIdText.tag = station.stationId // Store ID for refresh
        binding.stationNameText.text = station.stationName

        // For now, show trains in a simple list (platform separation to be implemented later)
        if (station.nextTrains.isEmpty()) {
            binding.noTrainsLayout.visibility = View.VISIBLE
            binding.platformTabLayout.visibility = View.GONE
            binding.platformViewPager.visibility = View.GONE
        } else {
            binding.noTrainsLayout.visibility = View.GONE
            binding.platformTabLayout.visibility = View.VISIBLE
            binding.platformViewPager.visibility = View.VISIBLE
            
            // For now, just show all trains in a single view
            // TODO: Implement platform separation with ViewPager2
            setupPlatformTabs(station)
        }
    }

    private fun setupPlatformTabs(station: Station) {
        // Simple implementation for now - group trains by platform
        val platformGroups = station.nextTrains.groupBy { it.platform ?: 1 }
        
        // Clear existing tabs
        binding.platformTabLayout.removeAllTabs()
        
        // Add tabs for each platform
        platformGroups.keys.sorted().forEach { platform ->
            val tab = binding.platformTabLayout.newTab()
            tab.text = getString(R.string.platform, platform)
            binding.platformTabLayout.addTab(tab)
        }
        
        // For now, just show the first platform's trains
        // TODO: Implement proper ViewPager2 adapter
        if (platformGroups.isNotEmpty()) {
            val firstPlatformTrains = platformGroups.values.first()
            // This would normally go to a RecyclerView in the ViewPager
        }
    }

    private fun showStationList() {
        // Hide navigation icon and reset toolbar
        binding.toolbar.navigationIcon = null
        binding.toolbar.title = getString(R.string.app_title)
        
        binding.stationListLayout.visibility = View.VISIBLE
        binding.stationDetailLayout.visibility = View.GONE
    }

    override fun onStationClick(station: Station) {
        viewModel.fetchStationSchedule(station.stationId)
    }
}