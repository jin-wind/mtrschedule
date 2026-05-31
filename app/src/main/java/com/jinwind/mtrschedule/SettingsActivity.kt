package com.jinwind.mtrschedule

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jinwind.mtrschedule.data.MtrStationList
import com.jinwind.mtrschedule.databinding.ActivitySettingsBinding
import com.jinwind.mtrschedule.settings.SettingsManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        settingsManager = SettingsManager.getInstance(this)
        
        setupToolbar()
        setupDefaultStation()
        setupLanguage()
        setupResetOrder()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupDefaultStation() {
        // 使用 MtrStationList.stations 列表
        val stations = MtrStationList.stations
        val stationNames = stations.map { it.nameChi }
        val currentDefaultId = settingsManager.getDefaultStationId()
        val currentDefaultName = stations.find { it.id == currentDefaultId }?.nameChi ?: "None"
        
        binding.selectedStationText.text = currentDefaultName

        binding.defaultStationLayout.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.default_station))
            
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stationNames)
            builder.setAdapter(adapter) { _, which ->
                val selectedStation = stations[which]
                settingsManager.setDefaultStation(selectedStation.id)
                binding.selectedStationText.text = selectedStation.nameChi
            }
            builder.show()
        }
    }

    private fun setupLanguage() {
        val currentLang = settingsManager.getLanguage()
        when (currentLang) {
            "en" -> binding.radioEnglish.isChecked = true
            "zh" -> binding.radioChinese.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }

        binding.languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newLang = when (checkedId) {
                R.id.radioEnglish -> "en"
                R.id.radioChinese -> "zh"
                else -> "system"
            }
            
            if (newLang != currentLang) {
                settingsManager.setLanguage(newLang)
                // 重启 Activity 以应用语言
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupResetOrder() {
        binding.resetOrderButton.setOnClickListener {
            settingsManager.resetToDefaultOrder()
            Toast.makeText(this, getString(R.string.reset_order_success), Toast.LENGTH_LONG).show()
        }
    }
}
