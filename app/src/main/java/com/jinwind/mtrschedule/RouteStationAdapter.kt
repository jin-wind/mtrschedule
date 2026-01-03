package com.jinwind.mtrschedule

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinwind.mtrschedule.model.Station
import com.jinwind.mtrschedule.model.Train

/**
 * 路线站台适配器，用于显示路线经过的站台及每个站台的列车信息
 */
class RouteStationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var stations: List<Station> = emptyList()
    private var isReverse: Boolean = false // 添加方向标志，默认为正向
    private var isLoading: Boolean = false // 添加加载状态标志
    // 添加站点点击监听器
    private var onStationClickListener: ((stationId: String) -> Unit)? = null

    // 骨架屏数量
    private val SKELETON_ITEM_COUNT = 6

    // 视图类型常量
    private val VIEW_TYPE_STATION = 0
    private val VIEW_TYPE_SKELETON = 1

    // 添加过滤平台标志，默认只显示站台1
    private var platformFilter: String = "1"

    // 添加特殊站点的名称列表，便于后续维护
    private val allPlatformsStations = listOf("屯门码头", "屯門碼頭", "Tuen Mun Ferry Pier", "三聖", "三圣", "Sam Shing")
    private val mergePlatformsStations = listOf("田景", "Tin King")
    private val cityStations = listOf("市中心", "Town Centre", "市中心站")

    inner class RouteStationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stationNameTextView: TextView = itemView.findViewById(R.id.station_name)
        private val stationIdTextView: TextView = itemView.findViewById(R.id.station_id)
        private val trainRecyclerView: RecyclerView = itemView.findViewById(R.id.station_trains_recycler_view)
        private val noTrainsTextView: TextView = itemView.findViewById(R.id.no_trains_text)
        private val trainAdapter = RouteTrainAdapter()

        init {
            // 设置列车列表 - 使用RouteTrainAdapter，并且设置为水平滚动
            trainRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            trainRecyclerView.adapter = trainAdapter
            
            // 优化RecyclerView性能
            trainRecyclerView.itemAnimator = null
        }

        fun bind(station: Station) {
            // 设置站台名称和ID
            stationNameTextView.text = station.stationName
            stationIdTextView.text = "(${station.stationCode})"

            // 设置站名点击事件，跳转到卡片模式
            stationNameTextView.setOnClickListener {
                onStationClickListener?.invoke(station.stationId)
            }

            // 设置站点ID点击事件，跳转到卡片模式
            stationIdTextView.setOnClickListener {
                onStationClickListener?.invoke(station.stationId)
            }

            // 根据站台名称应用不同的过滤逻辑
            val filteredTrains = when {
                // 屯门码头站：显示所有站台的车辆，无论方向如何
                isSpecialStation(station.stationName, allPlatformsStations) -> {
                    station.nextTrains.sortedBy { it.timeToArrival }
                }
                // 田景站：合并2号站台和3号站台的车辆
                isSpecialStation(station.stationName, mergePlatformsStations) -> {
                    val trainsToShow = mutableListOf<Train>()

                    // 如果当前过滤的是站台1，则只显示站台1的车辆
                    if (platformFilter == "1") {
                        trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "1"))
                    }
                    // 如果当前过滤的是站台2，则合并站台2和站台3的车辆
                    else if (platformFilter == "2") {
                        trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "2"))
                        trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "3"))
                    }

                    trainsToShow.sortedBy { it.timeToArrival }
                }
                // 市中心站：特殊处理，因为只有1号和4号月台，没有2号月台
                isSpecialStation(station.stationName, cityStations) -> {
                    val trainsToShow = mutableListOf<Train>()

                    // 如果当前过滤的是站台1，则显示站台1的车辆
                    if (platformFilter == "1") {
                        trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "1"))
                    }
                    // 如果当前过滤的是站台2（反向模式），则显示站台4的车辆
                    else if (platformFilter == "2") {
                        trainsToShow.addAll(filterTrainsByPlatform(station.nextTrains, "4"))
                    }

                    trainsToShow.sortedBy { it.timeToArrival }
                }
                // 其他站台：按照原有逻辑过滤
                else -> {
                    filterTrainsByPlatform(station.nextTrains, platformFilter).sortedBy { it.timeToArrival }
                }
            }

            trainAdapter.submitList(filteredTrains)

            // 处理无列车的情况
            if (filteredTrains.isEmpty()) {
                noTrainsTextView.visibility = View.VISIBLE
                trainRecyclerView.visibility = View.GONE
            } else {
                noTrainsTextView.visibility = View.GONE
                trainRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    // 骨架屏ViewHolder
    inner class SkeletonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stationNameSkeleton: View = itemView.findViewById(R.id.skeleton_station_name)
        private val stationIdSkeleton: View = itemView.findViewById(R.id.skeleton_station_id)
        private val trainsSkeleton: View = itemView.findViewById(R.id.skeleton_trains)

        private val handler = Handler(Looper.getMainLooper())
        private var animator: ValueAnimator? = null

        fun bind() {
            // 启动骨架屏动画效果
            startShimmerAnimation()
        }

        private fun startShimmerAnimation() {
            // 创建动画
            animator?.cancel()
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                interpolator = LinearInterpolator()

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    val color = if (value < 0.5f) {
                        0xFFEEEEEE.toInt() + ((0xFFF5F5F5.toInt() - 0xFFEEEEEE.toInt()) * value * 2).toInt()
                    } else {
                        0xFFF5F5F5.toInt() - ((0xFFF5F5F5.toInt() - 0xFFEEEEEE.toInt()) * (value - 0.5f) * 2).toInt()
                    }

                    val drawable = GradientDrawable().apply {
                        setColor(color)
                        cornerRadius = 8f
                    }

                    stationNameSkeleton.background = drawable
                    stationIdSkeleton.background = drawable
                    trainsSkeleton.background = drawable
                }

                start()
            }
        }

        fun stopAnimation() {
            animator?.cancel()
            animator = null
        }
    }

    // 辅助方法：检查站点是否为特殊站点
    private fun isSpecialStation(stationName: String, specialStations: List<String>): Boolean {
        return specialStations.any { stationName.contains(it, ignoreCase = true) }
    }

    // 辅助方法：根据站台号过滤列车
    private fun filterTrainsByPlatform(trains: List<Train>, platformNumber: String): List<Train> {
        // 如果是巴士模式（platform为空），则不进行过滤
        if (trains.isNotEmpty() && trains.all { it.platform.isEmpty() }) {
            return trains
        }
        
        return trains.filter { train ->
            train.platform == platformNumber ||
            train.platform == "站台$platformNumber" ||
            train.platform == "站台 $platformNumber" ||
            train.platform == "Platform $platformNumber" ||
            train.platform == "Platform $platformNumber"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SKELETON else VIEW_TYPE_STATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SKELETON) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_skeleton_station, parent, false)
            SkeletonViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_route_station, parent, false)
            RouteStationViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SkeletonViewHolder) {
            holder.bind()
        } else if (holder is RouteStationViewHolder && stations.isNotEmpty()) {
            // 不需要在这里反转顺序，因为站台列表已经根据方向排序好了
            // 直接使用position作为索引
            if (position < stations.size) {
                holder.bind(stations[position])
            }
        }
    }

    override fun getItemCount() = if (isLoading) SKELETON_ITEM_COUNT else stations.size

    fun submitList(stations: List<Station>) {
        this.stations = stations
        notifyDataSetChanged()
    }
    
    // Update data and loading state in one go to prevent double refresh
    fun updateData(stations: List<Station>, isLoading: Boolean) {
        this.stations = stations
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    // 设置方向
    fun setDirection(isReverse: Boolean) {
        if (this.isReverse != isReverse) {
            this.isReverse = isReverse
            // 切换平台显示：正向显示站台1，反向显示站台2
            this.platformFilter = if (isReverse) "2" else "1"
            // 强制更新整个列表，以确保站台显示顺序正确反转
            notifyDataSetChanged()
        }
    }

    // 设置骨架屏加载效果
    fun showSkeletonLoading(show: Boolean) {
        if (isLoading != show) {
            isLoading = show
            notifyDataSetChanged()
        }
    }

    // 清理资源
    fun onDestroy() {
        val count = itemCount
        for (i in 0 until count) {
            val holder = getViewHolder(i)
            if (holder is SkeletonViewHolder) {
                holder.stopAnimation()
            }
        }
    }

    // 设置站台点击监听器
    fun setOnStationClickListener(listener: (stationId: String) -> Unit) {
        this.onStationClickListener = listener
    }

    private fun getViewHolder(position: Int): RecyclerView.ViewHolder? {
        return null // 实际使用时需要从RecyclerView获取ViewHolder
    }
}
