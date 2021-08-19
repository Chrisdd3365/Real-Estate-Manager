package com.openclassrooms.realestatemanager.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.mapview.MapViewFragment
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.properties_list.PropertiesListFragment

// TODO : Add a splash screen before this activity

/**
 *  This [AppCompatActivity] handles the main view, with 2 tabs : a list of properties, and a map.
 *  It links a [ViewPager] with a [TabLayout] : Every time we swipe the [ViewPager], it also changes
 *  the tab name on [TabLayout], and reciprocally.
 */
class MainActivity : AppCompatActivity() {

    // Helper classes
    private var mainViewPagerAdapter : MainViewPagerAdapter? = null

    // Child fragments
    private var propertiesListFragment : PropertiesListFragment? = null
    private var mapViewFragment : MapViewFragment? = null

    // Layout variables
    private var tabLayout : TabLayout? = null
    private var viewPager : ViewPager2? = null

    var estateList = ArrayList<Estate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        // Setup estate list
        estateList = createStaticEstateList()

        // Init child fragments
        propertiesListFragment = PropertiesListFragment.newInstance(estateList)
        mapViewFragment = MapViewFragment.newInstance()

        // Create adapter
        mainViewPagerAdapter = MainViewPagerAdapter(this, propertiesListFragment!!,
            mapViewFragment!!)

        // Configure [ViewPager]
        viewPager?.adapter = mainViewPagerAdapter

        // Configure [TabLayout]
        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            if (position == 0)
                tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_list, theme)
            else
                tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_building_map, theme)
        }.attach()
    }

    private fun createStaticEstateList() : ArrayList<Estate> {
        val result = ArrayList<Estate>()

        for (i in 0..15) {
            result.add(
                Estate().apply {
                    this.address = "Test address $i"
                    this.type = "Duplex"
                }
            )
        }

        return result
    }
}