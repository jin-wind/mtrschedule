package com.jinwind.mtrschedule

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jinwind.mtrschedule.data.MtrStationList
import com.jinwind.mtrschedule.databinding.ActivitySettingsBinding
import com.jinwind.mtrschedule.settings.SettingsManager
import com.jinwind.mtrschedule.util.LanguageHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingsManager: SettingsManager
    private var currentLanguage = "system"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化工具栏
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // 初始化设置管理器
        settingsManager = SettingsManager.getInstance(this)
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        setupDefaultStationSettings()
        setupWidgetStationSettings()
        setupToppedStationsSettings()
        setupLanguageSettings()

        // 保存按钮点击事件
        binding.saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun setupDefaultStationSettings() {
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
        val defaultStationId = settingsManager.getDefaultStationId()
        val defaultStationIndex = MtrStationList.stations.indexOfFirst { it.id == defaultStationId }
        if (defaultStationIndex >= 0) {
            binding.stationSpinner.setSelection(defaultStationIndex)
        }
    }

    private fun setupWidgetStationSettings() {
        val stationNames = MtrStationList.stations.map { "${it.nameEn} (${it.nameChi})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stationNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.widgetStationSpinner.adapter = adapter

        val source = settingsManager.getWidgetStationSource()
        if (source == "custom") {
            binding.widgetSourceCustomRadio.isChecked = true
        } else {
            binding.widgetSourceDefaultRadio.isChecked = true
        }

        val savedStationId = settingsManager.getWidgetStationId()
        val savedIndex = MtrStationList.stations.indexOfFirst { it.id == savedStationId }
        if (savedIndex >= 0) {
            binding.widgetStationSpinner.setSelection(savedIndex)
        }

        val isCustom = binding.widgetSourceCustomRadio.isChecked
        binding.widgetStationSpinner.isEnabled = isCustom

        binding.widgetStationSourceGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.widgetStationSpinner.isEnabled = checkedId == R.id.widgetSourceCustomRadio
        }
    }

    private fun setupToppedStationsSettings() {
        // 恢复默认排序按钮点击事件
        binding.clearToppedButton.setOnClickListener {
            settingsManager.resetToDefaultOrder()

            // 通知MainActivity需要重置置顶站点的状态
            val intent = Intent()
            intent.putExtra("RESET_PINNED_STATIONS", true)
            setResult(RESULT_OK, intent)

            Toast.makeText(this, getString(R.string.reset_order_success), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLanguageSettings() {
        // 加载当前语言设置
        currentLanguage = settingsManager.getLanguage()

        // 根据当前语言设置选中相应的单选按钮
        when (currentLanguage) {
            "en" -> binding.englishRadio.isChecked = true
            "zh" -> binding.chineseRadio.isChecked = true
            else -> binding.systemLanguageRadio.isChecked = true
        }
    }

    private fun saveSettings() {
        // 保存默认站点
        val selectedStationIndex = binding.stationSpinner.selectedItemPosition
        if (selectedStationIndex >= 0 && selectedStationIndex < MtrStationList.stations.size) {
            val selectedStationId = MtrStationList.stations[selectedStationIndex].id
            settingsManager.setDefaultStation(selectedStationId)
        }

        val widgetSource = if (binding.widgetSourceCustomRadio.isChecked) {
            "custom"
        } else {
            "default"
        }
        settingsManager.setWidgetStationSource(widgetSource)
        if (widgetSource == "custom") {
            val widgetStationIndex = binding.widgetStationSpinner.selectedItemPosition
            if (widgetStationIndex >= 0 && widgetStationIndex < MtrStationList.stations.size) {
                val widgetStationId = MtrStationList.stations[widgetStationIndex].id
                settingsManager.setWidgetStationId(widgetStationId)
            }
        }

        // 保存语言设置
        val newLanguage = when {
            binding.englishRadio.isChecked -> "en"
            binding.chineseRadio.isChecked -> "zh"
            else -> "system"
        }

        // 只有当语言设置发生变化时才应用新语言
        if (newLanguage != currentLanguage) {
            settingsManager.setLanguage(newLanguage)
            // 应用语言设置
            LanguageHelper.setLocale(this, newLanguage)

            // 重启应用以应用语言更改
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finishAffinity()
        } else {
            // 如果语言没有变化，只关闭设置页面
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
