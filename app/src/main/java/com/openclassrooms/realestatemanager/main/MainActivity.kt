package com.openclassrooms.realestatemanager.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding

// TODO : Add a splash screen before this activity

/**
 *  This [AppCompatActivity] handles the main view, with 2 tabs : a list of properties, and a map.
 *  It links a [ViewPager] with a [TabLayout].
 *  TODO : Add a fragment for each tab
 */
class MainActivity : AppCompatActivity() {

    // Helper classes
    private var mainViewPagerAdapter = MainViewPagerAdapter()

    // Layout variables
    private var tabLayout : TabLayout? = null
    private var viewPager : ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main
        )

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        // Configure [TabLayout]
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
            }

        })

        // Configure [ViewPager]
        viewPager?.adapter = mainViewPagerAdapter
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                TODO("Not yet implemented")
            }

            override fun onPageSelected(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onPageScrollStateChanged(state: Int) {
                TODO("Not yet implemented")
            }
        })
    }
}