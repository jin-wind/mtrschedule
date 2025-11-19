package com.jinwind.mtrschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RouteAdapter(
    private var routes: List<String>,
    private val onRouteSelected: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private var isReverse = false // 添加方向标志，默认为正向

    fun updateRoutes(newRoutes: List<String>) {
        routes = newRoutes
        selectedPosition = RecyclerView.NO_POSITION
        isReverse = false
        notifyDataSetChanged()
    }

    // 获取当前选中的路线
    fun getSelectedRoute(): String? {
        return if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < routes.size) {
            routes[selectedPosition]
        } else {
            null
        }
    }

    // 获取当前方向
    fun isReverse(): Boolean = isReverse

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeNumber: TextView = itemView.findViewById(R.id.route_number)

        fun bind(route: String, position: Int) {
            // 显示路线号，移除箭头指示
            routeNumber.text = route

            // 设置选中项的背景色和文字颜色
            if (position == selectedPosition) {
                // 使用自定义的选中背景
                itemView.setBackgroundResource(R.drawable.route_item_selected_background)
                // 文字颜色设为白色
                routeNumber.setTextColor(itemView.resources.getColor(android.R.color.white, null))
            } else {
                // 使用自定义的普通背景
                itemView.setBackgroundResource(R.drawable.route_item_background)
                // 文字颜色根据当前主题自动设置
                // 在布局XML中已设置默认颜色
            }

            itemView.setOnClickListener {
                if (position == selectedPosition) {
                    // 如果点击的是已选中的路线，则切换方向
                    isReverse = !isReverse
                    notifyItemChanged(selectedPosition)
                } else {
                    // 否则选择新路线，并重置方向为正向
                    val previousSelected = selectedPosition
                    selectedPosition = position
                    isReverse = false

                    // 更新选中和取消选中的项
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(selectedPosition)

                // 触发选中回调，传递路线和方向
                onRouteSelected(route, isReverse)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position], position)
    }

    override fun getItemCount() = routes.size
}
