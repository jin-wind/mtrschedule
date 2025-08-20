package com.example.mtrschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtrschedule.databinding.ItemTrainBinding
import com.example.mtrschedule.model.Train

class TrainAdapter : ListAdapter<Train, TrainAdapter.TrainViewHolder>(TrainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainViewHolder {
        val binding = ItemTrainBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrainViewHolder(private val binding: ItemTrainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(train: Train) {
            // Set route number as the main item
            binding.routeNumberText.text = "Route ${train.routeNumber}"

            // Show destination name
            binding.destinationText.text = train.destination

            // Display platform information
            val platformText = when (train.platform) {
                "1" -> "站台1"
                "2" -> "站台2"
                else -> "站台${train.platform}"
            }
            binding.platformText.text = platformText

            // Apply different styling based on platform
            when (train.platform) {
                "1" -> binding.platformText.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                "2" -> binding.platformText.setTextColor(binding.root.context.getColor(android.R.color.holo_blue_dark))
                else -> binding.platformText.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
            }

            // Format arrival time
            binding.timeText.text = if (train.timeToArrival == 0) {
                binding.root.context.getString(R.string.arriving)
            } else {
                binding.root.context.getString(R.string.minutes_format, train.timeToArrival)
            }
        }
    }

    class TrainDiffCallback : DiffUtil.ItemCallback<Train>() {
        override fun areItemsTheSame(oldItem: Train, newItem: Train): Boolean {
            return oldItem.trainId == newItem.trainId
        }

        override fun areContentsTheSame(oldItem: Train, newItem: Train): Boolean {
            return oldItem == newItem
        }
    }
}