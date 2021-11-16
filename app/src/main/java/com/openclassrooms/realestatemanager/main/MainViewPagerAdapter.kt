package com.openclassrooms.realestatemanager.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.openclassrooms.realestatemanager.mapview.MapViewFragment
import com.openclassrooms.realestatemanager.properties_list.PropertiesListFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity,
                           private val propertiesListFragment: PropertiesListFragment,
                           private val mapViewFragment: MapViewFragment)
    : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            propertiesListFragment
        else
            mapViewFragment
    }

}