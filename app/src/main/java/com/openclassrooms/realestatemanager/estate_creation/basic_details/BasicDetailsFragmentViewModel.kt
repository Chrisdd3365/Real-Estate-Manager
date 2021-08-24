package com.openclassrooms.realestatemanager.estate_creation.basic_details

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class BasicDetailsFragmentViewModel : ViewModel() {

    val buttonEnabled = ObservableField(false)

    fun setButtonEnabled(newValue : Boolean) {
        buttonEnabled.set(newValue)
    }

}