package com.jinwind.mtrschedule

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jinwind.mtrschedule.databinding.ItemStationBinding
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.settings.SettingsManager

class StationAdapter(private val listener: StationClickListener) :
    ListAdapter<Station, StationAdapter.StationViewHolder>(StationDiffCallback()) {

    interface StationClickListener {
        fun onStationClick(station: Station)
    }

    // 添加SettingsManager的引用
    private lateinit var settingsManager: SettingsManager

    // 初始化SettingsManager
    fun setSettingsManager(manager: SettingsManager) {
        settingsManager = manager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val binding = ItemStationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        val item = getItem(position)
        // 检查SettingsManager是否已初始化
        val isTopped = if (::settingsManager.isInitialized) {
            // 使用Station的isPinned属性或者检查站点是否曾经被置顶过
            item.isPinned || settingsManager.isStationTopped(item.stationId)
        } else {
            // 如果SettingsManager未初始化，只使用位置判断
            position == 0
        }
        holder.bind(item, listener, isTopped)
    }

    class StationViewHolder(private val binding: ItemStationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(station: Station, listener: StationClickListener, isTopped: Boolean) {
            binding.stationNameText.text = station.stationName
            binding.stationCodeText.text = "ID: ${station.stationId}"

            val nextTrain = station.nextTrains.firstOrNull()
            if (nextTrain != null) {
                binding.destinationText.text = nextTrain.destination

                // 输出调试信息
                Log.d("StationAdapter", "Station: ${station.stationName}, Train: ${nextTrain.destination}, isDoubleCar: ${nextTrain.isDoubleCar}")
            } else {
                binding.destinationText.text = ""
            }

            // Set click listener
            binding.root.setOnClickListener {
                listener.onStationClick(station)
            }

            // 根据站点是否被顶置过来设置样式
            val context = binding.root.context
            if (isTopped) {
                // 所有顶置过的站点都使用相同的颜色
                binding.root.setCardBackgroundColor(ContextCompat.getColor(context, R.color.station_top))
                binding.pinIcon.visibility = android.view.View.VISIBLE
                val white = ContextCompat.getColor(context, android.R.color.white)
                binding.stationNameText.setTextColor(white)
                binding.stationCodeText.setTextColor(white)
                binding.destinationText.setTextColor(white)
            } else {
                // 未顶置的站点使用普通样式
                binding.root.setCardBackgroundColor(ContextCompat.getColor(context, R.color.station_normal))
                binding.pinIcon.visibility = android.view.View.GONE
                val black = ContextCompat.getColor(context, android.R.color.black)
                binding.stationNameText.setTextColor(black)
                binding.stationCodeText.setTextColor(black)
                binding.destinationText.setTextColor(black)
            }
        }
    }

    class StationDiffCallback : DiffUtil.ItemCallback<Station>() {
        override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.stationId == newItem.stationId
        }

        override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem == newItem
        }
    }
}