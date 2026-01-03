package com.example.mtrschedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtrschedule.databinding.ActivityMainBinding
import com.example.mtrschedule.model.Station
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), StationAdapter.StationClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TrainScheduleViewModel
    private lateinit var adapter: StationAdapter
    private var currentStation: Station? = null
    private var isUpdatingNavigation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        setupViewModel()
        setupRecyclerView()
        setupSwipeToRefresh()
        setupBottomNavigation()
        observeData()

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // 設置初始時間戳
        updateTimestamp()
    }

    private fun updateTimestamp() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = dateFormat.format(Date())
        binding.timestampText.text = getString(R.string.last_updated_format, timestamp)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TrainScheduleViewModel::class.java]

        // 加載默認站點
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val defaultStationId = sharedPreferences.getString("default_station", "240") // 默認站點
        viewModel.fetchStationSchedule(defaultStationId ?: "240")
        updateNavigationSelection(R.id.action_route_mode)
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
            currentStation = station
            displayStationDetail(station)
            updateNavigationSelection(R.id.action_route_mode)
            updateTimestamp()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading && !binding.swipeRefreshLayout.isRefreshing)
                View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.errorText.text = errorMessage
                binding.errorText.visibility = View.VISIBLE
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            } else {
                binding.errorText.visibility = View.GONE
            }
        }
    }

    private fun displayStationDetail(station: Station) {
        // 隱藏站點列表，顯示詳細信息
        binding.stationListLayout.visibility = View.GONE
        binding.stationDetailLayout.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        updateNavigationSelection(R.id.action_route_mode)

        // 更新 UI
        binding.stationIdText.text = getString(R.string.station_code_label) + " " + station.stationId
        binding.stationIdText.tag = station.stationId // 存儲 ID 以便刷新
        binding.stationNameText.text = station.stationName

        // 設置列車列表
        val trainAdapter = TrainAdapter()
        binding.trainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainRecyclerView.adapter = trainAdapter
        trainAdapter.submitList(station.nextTrains)

        if (station.nextTrains.isEmpty()) {
            binding.noTrainsText.visibility = View.VISIBLE
        } else {
            binding.noTrainsText.visibility = View.GONE
        }
    }

    private fun showStationList() {
        binding.stationListLayout.visibility = View.VISIBLE
        binding.stationDetailLayout.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        updateNavigationSelection(R.id.action_card_mode)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (isUpdatingNavigation) {
                return@setOnItemSelectedListener true
            }
            when (item.itemId) {
                R.id.action_route_mode -> {
                    val station = currentStation
                    if (station != null) {
                        displayStationDetail(station)
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.search_hint),
                            Toast.LENGTH_SHORT
                        ).show()
                        updateNavigationSelection(R.id.action_card_mode)
                    }
                    true
                }
                R.id.action_card_mode -> {
                    showStationList()
                    viewModel.fetchStationSchedules()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateNavigationSelection(itemId: Int) {
        if (binding.bottomNavigation.selectedItemId == itemId) {
            return
        }
        isUpdatingNavigation = true
        binding.bottomNavigation.selectedItemId = itemId
        isUpdatingNavigation = false
    }

    override fun onStationClick(station: Station) {
        viewModel.fetchStationSchedule(station.stationId)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (binding.stationDetailLayout.visibility == View.VISIBLE) {
            showStationList()
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.stationDetailLayout.visibility == View.VISIBLE) {
            showStationList()
        } else {
            super.onBackPressed()
        }
    }
}
