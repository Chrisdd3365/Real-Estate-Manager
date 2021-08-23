package com.openclassrooms.realestatemanager.estate_creation

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class EstateCreationActivityViewModel : ViewModel() {

    val skipTextViewVisibility = ObservableInt(View.GONE)
}