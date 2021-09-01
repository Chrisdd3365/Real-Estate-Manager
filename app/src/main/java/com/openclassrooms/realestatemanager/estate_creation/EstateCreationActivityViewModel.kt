package com.openclassrooms.realestatemanager.estate_creation

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class EstateCreationActivityViewModel : ViewModel() {

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
}