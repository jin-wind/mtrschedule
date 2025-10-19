package com.jinwind.mtrschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jinwind.mtrschedule.model.Train

/**
 * 路线模式下使用的列车适配器，提供更紧凑的列车信息显示
 */
class RouteTrainAdapter : ListAdapter<Train, RouteTrainAdapter.RouteTrainViewHolder>(TrainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteTrainViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route_train, parent, false)
        return RouteTrainViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteTrainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RouteTrainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val routeNumberText: TextView = itemView.findViewById(R.id.routeNumberText)
    private val timeText: TextView = itemView.findViewById(R.id.timeText)
    private val destinationText: TextView = itemView.findViewById(R.id.destinationText)
        private val carIcon: ImageView = itemView.findViewById(R.id.carIcon)

        fun bind(train: Train) {
            // 显示路线号
            routeNumberText.text = train.routeNumber

            val iconRes = if (train.isDoubleCar) {
                R.drawable.ic_train_double
            } else {
                R.drawable.ic_train_single
            }
            carIcon.setImageResource(iconRes)
            carIcon.contentDescription = itemView.context.getString(
                if (train.isDoubleCar) R.string.train_car_double_description else R.string.train_car_single_description
            )

            // 显示到达时间
            destinationText.text = train.destination

            // 顯示到達時間
            timeText.text = when {
                train.timeToArrival == 0 -> itemView.context.getString(R.string.arriving)
                train.timeToArrival == 1 -> "1分钟"
                else -> itemView.context.getString(R.string.minutes_format, train.timeToArrival)
            }
        }
    }

    private class TrainDiffCallback : DiffUtil.ItemCallback<Train>() {
        override fun areItemsTheSame(oldItem: Train, newItem: Train): Boolean {
            return oldItem.trainId == newItem.trainId
        }

        override fun areContentsTheSame(oldItem: Train, newItem: Train): Boolean {
            return oldItem == newItem
        }
    }
}
