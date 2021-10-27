package com.openclassrooms.realestatemanager.loaning_simulator

import android.view.View
import androidx.databinding.ObservableInt

class LoaningSimulatorActivityViewModel {

    val resultsVisibility = ObservableInt(View.GONE)

    fun setResults() {
        resultsVisibility.set(View.VISIBLE)
    }

    fun hideResults() {
        resultsVisibility.set(View.GONE)
    }

}