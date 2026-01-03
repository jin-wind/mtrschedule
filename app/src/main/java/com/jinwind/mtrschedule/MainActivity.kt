package com.jinwind.mtrschedule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jinwind.mtrschedule.databinding.ActivityMainBinding
import com.jinwind.mtrschedule.model.Station
import android.text.Editable
import android.text.TextWatcher
import com.jinwind.mtrschedule.settings.SettingsManager
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity(), StationAdapter.StationClickListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TrainScheduleViewModel
    private lateinit var adapter: StationAdapter
    private var allStations: List<Station> = emptyList()
    private lateinit var settingsManager: SettingsManager
    private lateinit var toggle: ActionBarDrawerToggle

    private var needScrollToTop = false

    // 添加标记，记录卡片模式是从路线模式打开的
    private var isCardOpenedFromRouteMode = false

    // 设置页面请求码
    private val SETTINGS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置Toolbar
        setSupportActionBar(binding.toolbar)

        settingsManager = SettingsManager.getInstance(this)

        // 設置導航抽屜
        setupDrawer()

        setupViewModel()
        setupRecyclerView()
        setupSwipeToRefresh()
        observeData()
        setupSearch()

        // 設置初始時間戳
        updateTimestamp()
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun updateTimestamp() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = dateFormat.format(Date())
        binding.timestampText.text = getString(R.string.last_updated_format, timestamp)
    }

    private fun setupViewModel() {
        val factory = TrainScheduleViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[TrainScheduleViewModel::class.java]

        // 加載默認站點（使用SettingsManager）
        val defaultStationId = settingsManager.getDefaultStationId()
        viewModel.fetchStationSchedule(defaultStationId)
    }

    private fun setupRecyclerView() {
        adapter = StationAdapter(this)
        // 初始化适配器的SettingsManager
        adapter.setSettingsManager(settingsManager)
        binding.stationsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stationsRecyclerView.adapter = adapter

        // 监听数据变化，顶置后自动滚动到顶部
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                scrollIfNeeded()
            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                scrollIfNeeded()
            }
            private fun scrollIfNeeded() {
                if (needScrollToTop) {
                    val layoutManager = binding.stationsRecyclerView.layoutManager as? LinearLayoutManager
                    layoutManager?.scrollToPositionWithOffset(0, 0)
                    // 滚动后强制刷新第0项，彻底解决空白
                    binding.stationsRecyclerView.post {
                        adapter.notifyItemChanged(0)
                    }
                    needScrollToTop = false
                }
            }
        })

        // 添加右滑頂置功能
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 获取被滑动的站台
                    val swipedStation = adapter.currentList[position]

                    // 使用SettingsManager顶置站点并保存顺序
                    allStations = settingsManager.topStationAndSaveOrder(allStations, swipedStation.stationId)

                    // 刷新列表（保持当前搜索过滤）
                    filterAndShowStations(binding.searchEditText.text?.toString() ?: "")
                    // 标记需要滚动到顶部
                    needScrollToTop = true
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.stationsRecyclerView)
    }

    private fun setupSwipeToRefresh() {
        // 只允许在详情页下拉刷新
        binding.swipeRefreshLayout.setOnRefreshListener {
            val stationId = binding.stationIdText.tag as? String
            stationId?.let {
                viewModel.fetchStationSchedule(it)
            }
            updateTimestamp()
        }
        // 初始状态禁用下拉刷新（只在详情页启用）
        binding.swipeRefreshLayout.isEnabled = false
    }

    private fun observeData() {
        viewModel.stationSchedules.observe(this) { stations ->
            // 添加日誌以檢查 stations 的內容
            Log.d("MainActivity", "Updated station list: ${stations.map { it.stationId }}")

            // 使用SettingsManager对站点进行排序
            allStations = settingsManager.sortStationsByOrder(stations)

            filterAndShowStations(binding.searchEditText.text?.toString() ?: "")
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
        // 隱藏站點列表，顯示詳細信息
        binding.stationListLayout.visibility = View.GONE
        binding.stationDetailLayout.visibility = View.VISIBLE
        // 進入詳情頁，啟用下拉刷新
        binding.swipeRefreshLayout.isEnabled = true

        // 更新 UI
        binding.stationIdText.text = "Station ID: " + station.stationId
        binding.stationIdText.tag = station.stationId // 存儲 ID 以便刷新
        binding.stationNameText.text = station.stationName

        // 設置列車列表
        val trainAdapter = TrainAdapter()
        binding.trainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.trainRecyclerView.adapter = trainAdapter

        // 使用新的处理方法，自动根据站台进行分组显示
        trainAdapter.processAndSubmitList(station.nextTrains)

        if (station.nextTrains.isEmpty()) {
            binding.noTrainsText.visibility = View.VISIBLE
        } else {
            binding.noTrainsText.visibility = View.GONE
        }
    }


    private fun showStationList() {
        binding.stationListLayout.visibility = View.VISIBLE
        binding.stationDetailLayout.visibility = View.GONE
        // 返回列表頁，禁用下拉刷新
        binding.swipeRefreshLayout.isEnabled = false
    }

    override fun onStationClick(station: Station) {
        viewModel.fetchStationSchedule(station.stationId)
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (handleBackNavigation()) {
            true
        } else {
            super.onSupportNavigateUp()
        }
    }

    @Deprecated("Use the new back navigation system")
    override fun onBackPressed() {
        if (!handleBackNavigation()) {
            super.onBackPressed()
        }
    }

    private fun handleBackNavigation(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }

        if (binding.fragmentContainer.visibility == View.VISIBLE) {
            // Return to main content from route mode.
            supportFragmentManager.findFragmentByTag("route_mode_fragment")?.let {
                supportFragmentManager.beginTransaction().hide(it).commit()
            }
            binding.fragmentContainer.visibility = View.GONE
            binding.mainContent.visibility = View.VISIBLE
            title = getString(R.string.app_name)
            return true
        }

        if (binding.stationDetailLayout.visibility == View.VISIBLE) {
            if (isCardOpenedFromRouteMode) {
                switchBackToRouteMode()
            } else {
                showStationList()
            }
            return true
        }

        return false
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndShowStations(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterAndShowStations(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allStations)
            return
        }
        val filtered = allStations.filter { station ->
            station.stationName.contains(query, ignoreCase = true) ||
            station.stationId.contains(query, ignoreCase = true)
        }
        adapter.submitList(filtered)
    }

    // 切换到卡片模式（从路线模式返回站点卡片模式）
    fun switchToCardMode() {
        // 关闭侧边抽屉栏（如果打开的话）
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        // 隐藏路线模式Fragment
        supportFragmentManager.findFragmentByTag("route_mode_fragment")?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }

        // 显示主内容区，隐藏Fragment容器
        binding.mainContent.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE

        // 显示站点卡片页面，隐藏站点列表
        binding.stationListLayout.visibility = View.GONE
        binding.stationDetailLayout.visibility = View.VISIBLE

        // 记录是从路线模式打开的卡片模式
        isCardOpenedFromRouteMode = true

        // 进入详情页，启用下拉刷新
        binding.swipeRefreshLayout.isEnabled = true

        // 设置标题为站点卡片模式
        title = getString(R.string.app_name)
    }

    // 从卡片模式返回到路线模式
    private fun switchBackToRouteMode() {
        // 隐藏卡片模式
        binding.mainContent.visibility = View.GONE

        // 显示Fragment容器
        binding.fragmentContainer.visibility = View.VISIBLE

        // 显示路线模式Fragment
        supportFragmentManager.findFragmentByTag("route_mode_fragment")?.let {
            supportFragmentManager.beginTransaction().show(it).commit()
        }

        // 设置标题为路线模式
        title = getString(R.string.route_mode)

        // 重置标记
        isCardOpenedFromRouteMode = false
    }

    // 保存当前站台顺序到 SettingsManager
    private fun saveStationOrderToPrefs(order: List<String>) {
        settingsManager.saveStationOrder(order)
    }

    // 从 SettingsManager 读取站台顺序 - 已集成到 observeData 方法
    private fun loadStationOrderFromPrefs(): List<String> {
        return settingsManager.getStationOrder()
    }

    // 处理设置页面返回的结果
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // 检查是否需要重置置顶站点
            val resetPinnedStations = data?.getBooleanExtra("RESET_PINNED_STATIONS", false) ?: false
            if (resetPinnedStations) {
                // 调用ViewModel的resetPinnedStations()方法重置所有置顶站点
                viewModel.resetPinnedStations()
                // 重新加载所有站点以更新UI
                viewModel.loadAllStations()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 处理侧边栏菜单项点击
        when (item.itemId) {
            R.id.nav_home -> {
                // 回到首页
                if (binding.stationDetailLayout.visibility == View.VISIBLE) {
                    showStationList()
                }
                // 确保主内容区显示，隐藏路线模式Fragment
                supportFragmentManager.findFragmentByTag("route_mode_fragment")?.let {
                    supportFragmentManager.beginTransaction().hide(it).commit()
                }
                binding.mainContent.visibility = View.VISIBLE
                binding.fragmentContainer.visibility = View.GONE
            }
            R.id.nav_routes -> {
                // 打开路线模式页面
                // 隐藏主内容区
                binding.mainContent.visibility = View.GONE
                // 显示Fragment容器
                binding.fragmentContainer.visibility = View.VISIBLE

                // 查找是否已经存在路线模式Fragment
                var fragment = supportFragmentManager.findFragmentByTag("route_mode_fragment")

                if (fragment == null) {
                    // 如果不存在，创建新的Fragment
                    fragment = RouteModeFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment, "route_mode_fragment")
                        .commit()
                } else {
                    // 如果已存在但被隐藏，显示它
                    supportFragmentManager.beginTransaction().show(fragment).commit()
                }
            }
            R.id.nav_settings -> {
                // 打开设置页面
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_REQUEST_CODE)
            }
            // 其他菜单项可以在这里添加
        }

        // 关闭侧边栏
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle home button click (for drawer toggle)
                toggle.onOptionsItemSelected(item)
                true
            }
            R.id.action_settings -> {
                // Handle settings action
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_REQUEST_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
