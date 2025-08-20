package com.example.mtrschedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mtrschedule.databinding.ItemStationBinding
import com.example.mtrschedule.model.Station

class StationAdapter(private val listener: StationClickListener) :
    ListAdapter<Station, StationAdapter.StationViewHolder>(StationDiffCallback()) {

    interface StationClickListener {
        fun onStationClick(station: Station)
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
        holder.bind(getItem(position), listener)
    }

    class StationViewHolder(private val binding: ItemStationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(station: Station, listener: StationClickListener) {
            binding.stationNameText.text = station.stationName
            binding.stationCodeText.text = station.stationId

            val nextTrain = station.nextTrains.firstOrNull()
            if (nextTrain != null) {
                val timeText = if (nextTrain.timeToArrival == 0) {
                    binding.root.context.getString(R.string.arriving)
                } else {
                    binding.root.context.getString(R.string.minutes_format, nextTrain.timeToArrival)
                }
                binding.nextTrainTimeText.text = timeText
                binding.destinationText.text = "${binding.root.context.getString(R.string.next_train_label)} ${nextTrain.destination}"
            } else {
                binding.nextTrainTimeText.text = "-"
                binding.destinationText.text = binding.root.context.getString(R.string.no_trains_available)
            }

            // Set click listener
            binding.root.setOnClickListener {
                listener.onStationClick(station)
            }
        }
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