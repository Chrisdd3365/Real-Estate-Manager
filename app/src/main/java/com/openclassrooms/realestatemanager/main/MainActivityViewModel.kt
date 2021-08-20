package com.openclassrooms.realestatemanager.main

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val miniFabVisibility = ObservableInt(View.GONE)

    fun setMiniFabVisibility(visibility : Int) {
        miniFabVisibility.set(visibility)
    }

}