package com.openclassrooms.realestatemanager.main

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val miniFabVisibility = ObservableInt(View.GONE)
    val mainFabTranslation = ObservableField(0f)

    fun setMiniFabVisibility(visible : Boolean) {
        miniFabVisibility.set(if (visible) View.VISIBLE else View.GONE)
        mainFabTranslation.set(if (visible) 45f else 0f)
    }

}