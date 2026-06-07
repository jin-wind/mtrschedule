package com.jinwind.mtrschedule.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.jinwind.mtrschedule.ui.miuix.MtrCard
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.RadioButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager.getInstance(context) }
    val colors = MiuixTheme.colorScheme

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
                color = colors.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            SettingsSection("預設車站", "Choose the station displayed when opening the app.") {
                var query by remember { mutableStateOf("") }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(color = colors.onSurfaceContainer, fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.secondaryVariant, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text("搜尋車站", color = colors.onSurfaceContainerVariant, fontSize = 16.sp)
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
                        RadioRow(
                            text = name,
                            selected = isSelected,
                            onClick = { station?.let { defaultStationId = it.id } }
                        )
                    }
                }
            }
        }

        item {
            SettingsSection("Widget Station", "Choose which station the widget displays.") {
                Column {
                    RadioRow(
                        text = "Use default station",
                        selected = widgetSource != "custom",
                        onClick = { widgetSource = "default" }
                    )
                    RadioRow(
                        text = "Choose a station",
                        selected = widgetSource == "custom",
                        onClick = { widgetSource = "custom" }
                    )
                    if (widgetSource == "custom") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            stationNames.forEach { name ->
                                val station = MtrStationList.stations.find { "${it.nameEn} (${it.nameChi})" == name }
                                val isSelected = station?.id == widgetStationId
                                RadioRow(
                                    text = name,
                                    selected = isSelected,
                                    onClick = { station?.let { widgetStationId = it.id } }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingsSection("Language Settings", "") {
                Column {
                    RadioRow(
                        text = "System default",
                        selected = language == "system",
                        onClick = { language = "system" }
                    )
                    RadioRow(
                        text = "English",
                        selected = language == "en",
                        onClick = { language = "en" }
                    )
                    RadioRow(
                        text = "中文",
                        selected = language == "zh",
                        onClick = { language = "zh" }
                    )
                }
            }
        }

        item {
            SettingsSection("已頂置站臺", "管理頂置車站") {
                TextButton(
                    text = "重置車站排序",
                    onClick = { settingsManager.resetToDefaultOrder() }
                )
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
                colors = ButtonDefaults.buttonColorsPrimary(),
                insideMargin = PaddingValues(vertical = 14.dp)
            ) {
                Text("保存設置", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun RadioRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MiuixTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text, color = colors.onSurfaceContainer, fontSize = 16.sp)
    }
}

@Composable
private fun SettingsSection(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = MiuixTheme.colorScheme
    MtrCard(
        modifier = modifier.fillMaxWidth(),
        color = colors.surfaceContainer,
        contentColor = colors.onSurfaceContainer,
        contentPadding = PaddingValues(16.dp)
    ) {
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (description.isNotBlank()) {
            Text(
                description,
                color = colors.onSurfaceContainerVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        } else {
            Spacer(Modifier.height(12.dp))
        }
        content()
    }
}
