package com.openclassrooms.realestatemanager.main

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val fragmentsVisibility = ObservableInt(View.GONE)
    val loadingVisibility = ObservableInt(View.VISIBLE)
    val miniFabVisibility = ObservableInt(View.GONE)
    val mainFabTranslation = ObservableField(0f)

    fun setMiniFabVisibility(visible : Boolean) {
        miniFabVisibility.set(if (visible) View.VISIBLE else View.GONE)
        mainFabTranslation.set(if (visible) 45f else 0f)
    }

    fun setLoading() {
        fragmentsVisibility.set(View.GONE)
        loadingVisibility.set(View.VISIBLE)
    }

    fun setFragments() {
        loadingVisibility.set(View.GONE)
        fragmentsVisibility.set(View.VISIBLE)
    }

}