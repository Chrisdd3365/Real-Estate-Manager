package com.openclassrooms.realestatemanager.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity
import com.openclassrooms.realestatemanager.mapview.MapViewFragment
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.properties_list.PropertiesListFragment
import com.openclassrooms.realestatemanager.utils.StaticData

// TODO : Add a splash screen before this activity

/**
 *  This [AppCompatActivity] handles the main view, with 2 tabs : a list of properties, and a map.
 *  It links a [ViewPager] with a [TabLayout] : Every time we swipe the [ViewPager], it also changes
 *  the tab name on [TabLayout], and reciprocally.
 */
class MainActivity : AppCompatActivity() {

    // Helper classes
    private var mainViewPagerAdapter : MainViewPagerAdapter? = null
    private var viewModel = MainActivityViewModel()

    // Child fragments
    private var propertiesListFragment : PropertiesListFragment? = null
    private var mapViewFragment : MapViewFragment? = null

    // Layout variables
    private var tabLayout : TabLayout? = null
    private var viewPager : ViewPager2? = null
    private var mainFab : FloatingActionButton? = null
    private var addPropertyFab : FloatingActionButton? = null
    private var addAgentFab : FloatingActionButton? = null

    private var areMiniFabEnabled = false

    var estateList = ArrayList<Estate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )
        binding.viewModel = viewModel

        // Init layout variables
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        mainFab = binding.mainFab
        addPropertyFab = binding.addPropertyFab
        addAgentFab = binding.addAgentFab

        // Setup estate list
        estateList = getStaticEstateList()

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

        // Setup buttons
        mainFab?.setOnClickListener {
            if (areMiniFabEnabled) {
                viewModel.setMiniFabVisibility(false)
                areMiniFabEnabled = false
            } else {
                viewModel.setMiniFabVisibility(true)
                areMiniFabEnabled = true
            }
        }

        addPropertyFab?.setOnClickListener {
            startActivity(EstateCreationActivity.newInstance(this, null))
        }
    }

    private fun getStaticEstateList() : ArrayList<Estate> {
        return StaticData.staticEstatesList
    }
}