package com.openclassrooms.realestatemanager.main

import android.view.View
import androidx.viewpager.widget.PagerAdapter

class MainViewPagerAdapter : PagerAdapter() {

    override fun getCount(): Int = 2

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        TODO("Not yet implemented")
    }
}