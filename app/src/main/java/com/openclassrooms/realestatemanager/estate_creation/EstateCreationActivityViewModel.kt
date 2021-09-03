package com.openclassrooms.realestatemanager.estate_creation

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class EstateCreationActivityViewModel : ViewModel() {

    val loadingVisibility = ObservableInt(View.VISIBLE)
    val fragmentVisibility = ObservableInt(View.GONE)
    val navigationButtonsVisibility = ObservableInt(View.GONE)
    val skipTextViewVisibility = ObservableInt(View.GONE)
    val previousButtonVisibility = ObservableInt(View.GONE)
    val nextButtonVisibility = ObservableInt(View.GONE)
    val buttonPreviousEnabled = ObservableField(false)
    val buttonNextEnabled = ObservableField(false)

    fun setSkipTextVisibility(visibility : Int) {
        skipTextViewVisibility.set(visibility)
    }

    fun setNavigationButtonVisibility(previousVisibility: Int, nextVisibility: Int) {
        previousButtonVisibility.set(previousVisibility)
        nextButtonVisibility.set(nextVisibility)
    }

    fun setButtonPreviousEnabled(enabled : Boolean) {
        buttonPreviousEnabled.set(enabled)
    }

    fun setButtonNextEnabled(enabled: Boolean) {
        buttonNextEnabled.set(enabled)
    }

    fun setLoading() {
        fragmentVisibility.set(View.GONE)
        navigationButtonsVisibility.set(View.GONE)
        loadingVisibility.set(View.VISIBLE)
    }

    fun setFragments() {
        loadingVisibility.set(View.GONE)
        navigationButtonsVisibility.set(View.VISIBLE)
        fragmentVisibility.set(View.VISIBLE)
    }
}