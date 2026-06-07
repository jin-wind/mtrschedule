package com.jinwind.mtrschedule.ui.settings

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jinwind.mtrschedule.data.MtrStationList
import com.jinwind.mtrschedule.settings.SettingsManager
import com.jinwind.mtrschedule.ui.glass.GlassSurface
import com.jinwind.mtrschedule.ui.theme.MtrGlassTheme
import com.kyant.backdrop.Backdrop

@Composable
fun SettingsScreen(
    backdrop: Backdrop,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager.getInstance(context) }

    var defaultStationId by remember { mutableStateOf(settingsManager.getDefaultStationId()) }
    var widgetSource by remember { mutableStateOf(settingsManager.getWidgetStationSource()) }
    var widgetStationId by remember { mutableStateOf(settingsManager.getWidgetStationId()) }
    var language by remember { mutableStateOf(settingsManager.getLanguage()) }

    val stationNames = remember { MtrStationList.stations.map { "${it.nameEn} (${it.nameChi})" } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "設置",
                color = MtrGlassTheme.colors.textPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            // Default Station Card
            GlassSection(backdrop, "預設車站", "Choose the station displayed when opening the app.") {
                // Simple search field
                var query by remember { mutableStateOf("") }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("搜尋車站", color = MtrGlassTheme.colors.textTertiary, fontSize = 16.sp)
                        }
                        inner()
                    }
                )
                Spacer(Modifier.height(12.dp))

                val filtered = if (query.isEmpty()) stationNames else stationNames.filter {
                    it.contains(query, ignoreCase = true)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    filtered.take(5).forEach { name ->
                        val station = MtrStationList.stations.find { "${it.nameEn} (${it.nameChi})" == name }
                        val isSelected = station?.id == defaultStationId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { station?.let { defaultStationId = it.id } },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { station?.let { defaultStationId = it.id } }
                            )
                            Text(name, color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        item {
            // Widget Station Card
            GlassSection(backdrop, "Widget Station", "Choose which station the widget displays.") {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = widgetSource != "custom",
                            onClick = { widgetSource = "default" }
                        )
                        Text("Use default station", color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = widgetSource == "custom",
                            onClick = { widgetSource = "custom" }
                        )
                        Text("Choose a station", color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                    }
                    if (widgetSource == "custom") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            stationNames.forEach { name ->
                                val station = MtrStationList.stations.find { "${it.nameEn} (${it.nameChi})" == name }
                                val isSelected = station?.id == widgetStationId
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { station?.let { widgetStationId = it.id } },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { station?.let { widgetStationId = it.id } }
                                    )
                                    Text(name, color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Language Card
            GlassSection(backdrop, "Language Settings", "") {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = language == "system",
                            onClick = { language = "system" }
                        )
                        Text("System default", color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = language == "en",
                            onClick = { language = "en" }
                        )
                        Text("English", color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = language == "zh",
                            onClick = { language = "zh" }
                        )
                        Text("中文", color = MtrGlassTheme.colors.textPrimary, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            // Reset button
            GlassSection(backdrop, "已頂置站臺", "管理頂置車站") {
                Row {
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            settingsManager.resetToDefaultOrder()
                        }
                    ) {
                        Text("重置車站排序", color = MtrGlassTheme.colors.primary)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    settingsManager.setDefaultStation(defaultStationId)
                    settingsManager.setWidgetStationSource(widgetSource)
                    if (widgetSource == "custom") {
                        settingsManager.setWidgetStationId(widgetStationId)
                    }
                    settingsManager.setLanguage(language)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text("保存設置", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun GlassSection(
    backdrop: Backdrop,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassSurface(
        backdrop = backdrop,
        modifier = modifier.fillMaxWidth(),
        tint = MtrGlassTheme.colors.surfaceStrong.copy(alpha = 0.5f),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Text(
                title,
                color = MtrGlassTheme.colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (description.isNotBlank()) {
                Text(
                    description,
                    color = MtrGlassTheme.colors.textSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            } else {
                Spacer(Modifier.height(12.dp))
            }
            content()
        }
    }
}