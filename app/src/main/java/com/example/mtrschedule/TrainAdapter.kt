package com.example.mtrschedule

import android.view.LayoutInflater
import android.view.View
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
            // Set route number
            binding.routeNumberText.text = train.routeNumber

            // Show destination name
            binding.destinationText.text = "${binding.root.context.getString(R.string.destination_label)} ${train.destination}"

            // Show platform if available
            if (train.platform.isNotEmpty() && train.platform != "0") {
                binding.platformText.visibility = View.VISIBLE
                binding.platformText.text = binding.root.context.getString(R.string.platform, train.platform.toIntOrNull() ?: 1)
            } else {
                binding.platformText.visibility = View.GONE
            }

            // Format arrival time
            val timeText = if (train.timeToArrival == 0) {
                binding.root.context.getString(R.string.arriving)
            } else {
                binding.root.context.getString(R.string.minutes_format, train.timeToArrival)
            }
            binding.timeText.text = timeText
            
            // Show status text for very short times
            if (train.timeToArrival <= 1) {
                binding.statusText.visibility = View.VISIBLE
                binding.statusText.text = binding.root.context.getString(R.string.arriving)
            } else {
                binding.statusText.visibility = View.GONE
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