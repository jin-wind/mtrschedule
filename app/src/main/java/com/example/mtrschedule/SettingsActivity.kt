package com.example.mtrschedule

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.mtrschedule.data.MtrStationList
import com.example.mtrschedule.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        // 提取車站名稱列表
        val stationNames = MtrStationList.stations.map { "${it.nameEn} (${it.nameChi})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stationNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stationSpinner.adapter = adapter

        var filteredStations = MtrStationList.stations // 用於保存過濾後的車站列表

        // 搜尋功能
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredStations = MtrStationList.stations.filter {
                    it.nameEn.contains(newText ?: "", ignoreCase = true) ||
                            it.nameChi.contains(newText ?: "")
                }
                val filteredNames = filteredStations.map { "${it.nameEn} (${it.nameChi})" }
                adapter.clear()
                adapter.addAll(filteredNames)
                adapter.notifyDataSetChanged()
                return true
            }
        })

        // 加載已保存的默認車站
        val defaultStationId = sharedPreferences.getString("default_station", MtrStationList.stations[0].id)
        val defaultStationIndex = MtrStationList.stations.indexOfFirst { it.id == defaultStationId }
        if (defaultStationIndex >= 0) {
            binding.stationSpinner.setSelection(defaultStationIndex)
        }

        // 保存選擇的車站
        binding.saveButton.setOnClickListener {
            val selectedStationIndex = binding.stationSpinner.selectedItemPosition
            val selectedStationId = filteredStations[selectedStationIndex].id // 使用過濾後的列表
            sharedPreferences.edit().putString("default_station", selectedStationId).apply()
            finish()
        }
    }
}