package com.openclassrooms.realestatemanager.show_estate

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class ShowEstateActivityViewModel : ViewModel() {

    val surfaceSize = ObservableField("")
    val roomsCount = ObservableField("")
    val bedroomsCount = ObservableField("")
    val bathroomsCount = ObservableField("")
    val price = ObservableField("")
}