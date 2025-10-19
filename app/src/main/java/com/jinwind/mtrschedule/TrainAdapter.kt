package com.jinwind.mtrschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jinwind.mtrschedule.databinding.ItemTrainBinding
import com.jinwind.mtrschedule.model.Train

class TrainAdapter : ListAdapter<TrainAdapterItem, RecyclerView.ViewHolder>(TrainDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_TRAIN = 1
    }

    // 添加排序方式变量
    private var sortByPlatform = true // 默认启用站台排序

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TrainAdapterItem.Header -> VIEW_TYPE_HEADER
            is TrainAdapterItem.TrainItem -> VIEW_TYPE_TRAIN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_platform_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val binding = ItemTrainBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                TrainViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TrainAdapterItem.Header -> (holder as HeaderViewHolder).bind(item)
            is TrainAdapterItem.TrainItem -> (holder as TrainViewHolder).bind(item.train)
        }
    }

    // 添加设置排序方式的方法
    fun setSortByPlatform(sort: Boolean) {
        if (this.sortByPlatform != sort) {
            this.sortByPlatform = sort
            // 重新排序当前列表
            processAndSubmitList(getCurrentList().filterIsInstance<TrainAdapterItem.TrainItem>().map { it.train })
        }
    }

    // 重写submitList方法，添加排序和分组逻辑
    override fun submitList(list: List<TrainAdapterItem>?) {
        super.submitList(list)
    }

    // 新方法：处理列车列表并添加分组标题
    fun processAndSubmitList(trains: List<Train>) {
        if (trains.isEmpty()) {
            super.submitList(emptyList())
            return
        }

        // 首先收集所有站台1的车辆
        val platform1Trains = trains.filter {
            val platform = normalizePlatform(it.platform)
            platform == "1"
        }.sortedBy { it.timeToArrival }

        // 其他站台的车辆
        val otherTrains = trains.filter {
            val platform = normalizePlatform(it.platform)
            platform != "1"
        }.sortedWith(compareBy<Train> {
            // 按站台号排序
            when (normalizePlatform(it.platform)) {
                "2" -> 2
                "3" -> 3
                else -> 99
            }
        }.thenBy { it.timeToArrival })

        // 准备显示项目
        val items = mutableListOf<TrainAdapterItem>()

        // 先添加站台1的所有车辆（如果有）
        if (platform1Trains.isNotEmpty()) {
            items.add(TrainAdapterItem.Header("站台1"))
            platform1Trains.forEach {
                items.add(TrainAdapterItem.TrainItem(it))
            }
        }

        // 然后添加其他站台的车辆，并按站台分组
        if (otherTrains.isNotEmpty()) {
            var currentPlatform: String? = null

            for (train in otherTrains) {
                val platform = normalizePlatform(train.platform)

                // 如果站台发生变化，添加一个标题
                if (platform != currentPlatform) {
                    currentPlatform = platform
                    items.add(TrainAdapterItem.Header(getPlatformDisplayName(platform)))
                }

                // 添加列车项
                items.add(TrainAdapterItem.TrainItem(train))
            }
        }

        super.submitList(items)
    }

    // 标准化站台名称，便于比较
    private fun normalizePlatform(platform: String): String {
        return when {
            platform.contains("1") -> "1"
            platform.contains("2") -> "2"
            platform.contains("3") -> "3"
            else -> platform
        }
    }

    // 获取站台的显示名称
    private fun getPlatformDisplayName(platform: String): String {
        return when (platform) {
            "1" -> "站台1"
            "2" -> "站台2"
            "3" -> "站台3"
            else -> "站台$platform"
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.platformHeaderText)

        fun bind(header: TrainAdapterItem.Header) {
            headerText.text = header.title
        }
    }

    class TrainViewHolder(private val binding: ItemTrainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(train: Train) {
            // Set route number as the main item
            binding.routeNumberText.text = "${train.routeNumber}"

            // Show destination name
            binding.destinationText.text = train.destination

            val iconRes = if (train.isDoubleCar) {
                R.drawable.ic_train_double
            } else {
                R.drawable.ic_train_single
            }
            binding.trainCarIcon.setImageResource(iconRes)
            binding.trainCarIcon.contentDescription = binding.root.context.getString(
                if (train.isDoubleCar) R.string.train_car_double_description else R.string.train_car_single_description
            )

            // Display platform information
            val platformText = when (train.platform) {
                "1" -> "站台1"
                "2" -> "站台2"
                else -> "${train.platform}"
            }
            binding.platformText.text = platformText

            // Apply different styling based on platform
            when (train.platform) {
                "1", "站台1", "Platform 1" -> binding.platformText.setTextColor(binding.root.context.getColor(R.color.platform_color))
                "2", "站台2", "Platform 2" -> binding.platformText.setTextColor(binding.root.context.getColor(android.R.color.holo_blue_dark))
                else -> binding.platformText.setTextColor(binding.root.context.getColor(R.color.platform_color))
            }

            // Format arrival time
            binding.timeText.text = if (train.timeToArrival == 0) {
                binding.root.context.getString(R.string.arriving)
            } else {
                binding.root.context.getString(R.string.minutes_format, train.timeToArrival)
            }
        }
    }

    // 差异比较类
    class TrainDiffCallback : DiffUtil.ItemCallback<TrainAdapterItem>() {
        override fun areItemsTheSame(oldItem: TrainAdapterItem, newItem: TrainAdapterItem): Boolean {
            return when {
                oldItem is TrainAdapterItem.Header && newItem is TrainAdapterItem.Header ->
                    oldItem.title == newItem.title
                oldItem is TrainAdapterItem.TrainItem && newItem is TrainAdapterItem.TrainItem ->
                    oldItem.train.trainId == newItem.train.trainId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: TrainAdapterItem, newItem: TrainAdapterItem): Boolean {
            return when {
                oldItem is TrainAdapterItem.Header && newItem is TrainAdapterItem.Header ->
                    oldItem.title == newItem.title
                oldItem is TrainAdapterItem.TrainItem && newItem is TrainAdapterItem.TrainItem ->
                    oldItem.train == newItem.train
                else -> false
            }
        }
    }
}

// 密封类，用于表示适配器中的不同项类型
sealed class TrainAdapterItem {
    data class Header(val title: String) : TrainAdapterItem()
    data class TrainItem(val train: Train) : TrainAdapterItem()
}
