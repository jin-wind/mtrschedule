package com.jinwind.mtrschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinwind.mtrschedule.data.LightRailRouteData
import com.jinwind.mtrschedule.model.Station

/**
 * 路线模式Fragment，用于显示MTR路线信息
 */
class RouteModeFragment : Fragment() {

    private lateinit var routeListRecyclerView: RecyclerView
    private lateinit var routeStationsRecyclerView: RecyclerView
    private lateinit var routeDetailPlaceholder: TextView
    private lateinit var routeLoadingProgress: ProgressBar
    private lateinit var routeErrorText: TextView

    private lateinit var routeAdapter: RouteAdapter
    private lateinit var routeStationAdapter: RouteStationAdapter
    private lateinit var viewModel: TrainScheduleViewModel

    // 定义轻铁路线列表
    private val lightRailRoutes = listOf(
        "505", "507", "610", "614", "614P",
        "615", "615P", "705", "706", "751", "761P"
    )

    // 存储当前加载的站台列表
    private val stationMap: MutableMap<String, Station> = mutableMapOf()

    // 添加当前路线方向标志
    private var isCurrentRouteReverse = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载路线模式页面布局
        return inflater.inflate(R.layout.fragment_route_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 设置页面标题
        activity?.title = getString(R.string.route_mode)

        // 初始化控件
        routeListRecyclerView = view.findViewById(R.id.route_list_recycler_view)
        routeStationsRecyclerView = view.findViewById(R.id.route_stations_recycler_view)
        routeDetailPlaceholder = view.findViewById(R.id.route_detail_placeholder)
        routeLoadingProgress = view.findViewById(R.id.route_loading_progress)
        routeErrorText = view.findViewById(R.id.route_error_text)

        // 初始化ViewModel
        val factory = TrainScheduleViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[TrainScheduleViewModel::class.java]

        // 初始化路线模式
        initializeRouteMode()

        // 监听站点数据变化
        observeStationData()
    }

    private fun initializeRouteMode() {
        // 设置路线列表
        routeListRecyclerView.layoutManager = LinearLayoutManager(context)

        // 创建路线适配器并设置点击事件处理，添加方向参数
        routeAdapter = RouteAdapter(lightRailRoutes) { routeNumber, isReverse ->
            // 处理路线选择事件，传递方向参数
            loadRouteStations(routeNumber, isReverse)
        }

        // 设置适配器
        routeListRecyclerView.adapter = routeAdapter

        // 设置站台列表适配器
        routeStationsRecyclerView.layoutManager = LinearLayoutManager(context)
        routeStationAdapter = RouteStationAdapter()

        // 设置站台点击监听器，点击站名时跳转到卡片模式
        routeStationAdapter.setOnStationClickListener { stationId ->
            // 获取父Activity (MainActivity) 并调用切换到卡片模式的方法
            (activity as? MainActivity)?.let { mainActivity ->
                // 显示加载进度
                routeLoadingProgress.visibility = View.VISIBLE

                // 先加载该站点数据，确保在数据加载完成后再切换到卡片模式
                viewModel.fetchStationScheduleWithCallback(stationId) { success ->
                    // 在UI线程中处理回调结果
                    activity?.runOnUiThread {
                        // 隐藏加载进度
                        routeLoadingProgress.visibility = View.GONE

                        // 只有在成功加载数据后才切换模式
                        if (success) {
                            // 切换回卡片模式
                            mainActivity.switchToCardMode()
                        }
                    }
                }
            }
        }

        routeStationsRecyclerView.adapter = routeStationAdapter
    }

    private fun observeStationData() {
        // 监听站台数据变化
        viewModel.stationSchedules.observe(viewLifecycleOwner) { stations ->
            // 更新本地站台缓存
            stations.forEach { station ->
                stationMap[station.stationId] = station
            }
        }

        // 监听选中站台数据
        viewModel.selectedStation.observe(viewLifecycleOwner) { station ->
            // 更新本地站台缓存
            stationMap[station.stationId] = station
        }

        // 监听加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 仅在加载路线站台时显示加载指示器
            if (routeStationsRecyclerView.visibility == View.VISIBLE) {
                routeLoadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // 监听错误信息
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty() && routeStationsRecyclerView.visibility == View.VISIBLE) {
                routeErrorText.text = errorMessage
                routeErrorText.visibility = View.VISIBLE
            } else {
                routeErrorText.visibility = View.GONE
            }
        }
    }

    // 修改方法签名，添加isReverse参数
    private fun loadRouteStations(routeNumber: String, isReverse: Boolean) {
        // 保存当前路线方向
        isCurrentRouteReverse = isReverse

        // 显示加载指示器
        routeDetailPlaceholder.visibility = View.GONE
        routeStationsRecyclerView.visibility = View.VISIBLE
        routeLoadingProgress.visibility = View.VISIBLE
        routeErrorText.visibility = View.GONE

        // 获取路线信息
        val route = LightRailRouteData.getRoute(routeNumber) ?: return

        // 根据方向设置路线信息标题
        val titleText = if (isReverse) {
            "${route.endStation} → ${route.startStation}"
        } else {
            "${route.startStation} → ${route.endStation}"
        }
        routeErrorText.text = titleText
        routeErrorText.visibility = View.VISIBLE

        // 设置适配器方向
        routeStationAdapter.setDirection(isReverse)

        // 获取考虑了方向的站点列表
        val directionStations = LightRailRouteData.getRouteStationsForDirection(routeNumber, isReverse)

        // 首先尝试从缓存加载
        if (viewModel.isRouteCached(routeNumber)) {
            // 如果已缓存，直接使用缓存数据
            val cachedStations = viewModel.getRouteStationsFromCache(routeNumber) ?: emptyList()

            // 根据directionStations过滤缓存的站点
            val filteredStations = cachedStations.filter { station ->
                directionStations.contains(station.stationId)
            }

            // 保持directionStations的顺序
            val orderedStations = directionStations.mapNotNull { stationId ->
                filteredStations.find { it.stationId == stationId }
            }

            // 更新站点列表
            routeStationAdapter.submitList(orderedStations)

            // 隐藏加载指示器
            routeLoadingProgress.visibility = View.GONE

            // 更新本地缓存
            cachedStations.forEach { station ->
                stationMap[station.stationId] = station
            }
        } else {
            // 首先显示已有的站台数据
            updateRouteStations(directionStations)

            // 显示骨架屏效果
            routeStationAdapter.showSkeletonLoading(true)

            // 使用多线程批量加载方法加载所有站点数据
            viewModel.fetchMultipleStationSchedules(directionStations)
        }

        // 监听站点数据变化
        viewModel.stationSchedules.observe(viewLifecycleOwner) { stations ->
            // 只有当数据加载完成并且UI仍然显示时才更新
            if (routeStationsRecyclerView.visibility == View.VISIBLE) {
                updateRouteStations(directionStations)
                // 隐藏骨架屏加载效果
                routeStationAdapter.showSkeletonLoading(false)
            }
        }

        // 监听加载状态
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                // 加载完成，隐藏骨架屏
                routeStationAdapter.showSkeletonLoading(false)
            }
        }
    }

    private fun updateRouteStations(stationIds: List<String>) {
        // 获取路线经过的所有站台
        val stations = stationIds.mapNotNull { stationId -> stationMap[stationId] }

        // 更新站台列表
        routeStationAdapter.submitList(stations)

        // 如果没有站台数据，显示加载中提示
        if (stations.isEmpty()) {
            routeLoadingProgress.visibility = View.VISIBLE
        } else {
            // 即使只有部分数据也显示
            routeLoadingProgress.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(): RouteModeFragment {
            return RouteModeFragment()
        }
    }
}
