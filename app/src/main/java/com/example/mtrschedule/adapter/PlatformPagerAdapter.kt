package com.example.mtrschedule.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mtrschedule.fragment.PlatformTrainsFragment
import com.example.mtrschedule.model.Train

class PlatformPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val platformTrains: Map<Int, List<Train>>
) : FragmentStateAdapter(fragmentActivity) {

    private val platforms = platformTrains.keys.sorted()

    override fun getItemCount(): Int = platforms.size

    override fun createFragment(position: Int): Fragment {
        val platform = platforms[position]
        val trains = platformTrains[platform] ?: emptyList()
        return PlatformTrainsFragment.newInstance(platform, ArrayList(trains))
    }

    fun getPlatformNumber(position: Int): Int = platforms[position]
}