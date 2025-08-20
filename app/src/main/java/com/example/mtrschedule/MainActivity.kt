package com.example.mtrschedule

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupSwipeToRefresh()
        observeData()

        // Set up back button for detail view
        binding.backButton.setOnClickListener {
            showStationList()
        }

        // Set initial timestamp
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
        viewModel.fetchStationSchedule("240") // Test with a specific station
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
        // Hide station list, show detail
        binding.stationListLayout.visibility = View.GONE
        binding.stationDetailLayout.visibility = View.VISIBLE
        binding.backButton.visibility = View.VISIBLE

        // Update UI with station details
        binding.stationIdText.text = getString(R.string.station_code_label) + " " + station.stationId
        binding.stationIdText.tag = station.stationId // Store ID for refresh
        binding.stationNameText.text = station.stationName

        // Setup train list
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
        binding.backButton.visibility = View.GONE
    }

    override fun onStationClick(station: Station) {
        viewModel.fetchStationSchedule(station.stationId)
    }
}