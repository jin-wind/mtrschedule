package com.example.mtrschedule.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mtrschedule.TrainAdapter
import com.example.mtrschedule.databinding.FragmentPlatformTrainsBinding
import com.example.mtrschedule.model.Train

class PlatformTrainsFragment : Fragment() {

    private var _binding: FragmentPlatformTrainsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var trainAdapter: TrainAdapter
    private var platform: Int = 1
    private var trains: List<Train> = emptyList()

    companion object {
        private const val ARG_PLATFORM = "platform"
        private const val ARG_TRAINS = "trains"

        fun newInstance(platform: Int, trains: ArrayList<Train>): PlatformTrainsFragment {
            return PlatformTrainsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PLATFORM, platform)
                    putParcelableArrayList(ARG_TRAINS, trains)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            platform = it.getInt(ARG_PLATFORM, 1)
            trains = it.getParcelableArrayList<Train>(ARG_TRAINS) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlatformTrainsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateTrains(trains)
    }

    private fun setupRecyclerView() {
        trainAdapter = TrainAdapter()
        binding.trainsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trainAdapter
        }
    }

    private fun updateTrains(newTrains: List<Train>) {
        trains = newTrains
        trainAdapter.submitList(trains)
        
        if (trains.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.trainsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.trainsRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}