package com.openclassrooms.realestatemanager.loaning_simulator

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

class LoaningSimulatorActivityViewModel {

    val resultsVisibility = ObservableInt(View.GONE)

    val mortgageTotalCostValue = ObservableField("")
    val mortgageMonthlyPaymentsValue = ObservableField("")

    fun setResults() {
        resultsVisibility.set(View.VISIBLE)
    }

    fun hideResults() {
        resultsVisibility.set(View.GONE)
    }

    fun updateTotalCostValue(newValue : String) {
        mortgageTotalCostValue.set(newValue)
    }

    fun updateMonthlyPaymentsValue(newValue: String) {
        mortgageMonthlyPaymentsValue.set(newValue)
    }

}