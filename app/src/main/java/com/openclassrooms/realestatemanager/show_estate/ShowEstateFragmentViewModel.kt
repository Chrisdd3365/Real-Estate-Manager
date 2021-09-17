package com.openclassrooms.realestatemanager.show_estate

import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums

class ShowEstateFragmentViewModel : ViewModel() {

    val nearbyLayoutVisibility = ObservableInt(View.GONE)
    val imagesLayoutVisibility = ObservableInt(View.GONE)

    val description = ObservableField("")
    val surfaceSize = ObservableField("")
    val roomsCount = ObservableField("")
    val bedroomsCount = ObservableField("")
    val bathroomsCount = ObservableField("")
    val price = ObservableField("")
    val type = ObservableField("")

    val buttonLeftString = ObservableField("")
    val buttonRightString = ObservableField("")

    fun setData(context: Context?, estate: Estate) {
        description.set(estate.description)
        // TODO : Get square meters or square feet given language
        surfaceSize.set("${estate.surface.toString()} ${context?.getString(R.string.square_meters)}")
        roomsCount.set("${estate.roomCount?.toString()} ${context?.getString(R.string.rooms)}")
        bedroomsCount.set("${estate.bedroomsCount?.toString()} ${context?.getString(R.string.bedrooms)}")
        bathroomsCount.set("${estate.bathroomsCount?.toString()} ${context?.getString(R.string.bathrooms)}")
        price.set(estate.price?.toString() + "$") // TODO : Get price from utils
        try {
            type.set(context?.resources?.getStringArray(R.array.estate_types)?.get(estate.typeIndex!!))
        } catch (exception : Exception) {
            Log.e(TAG, "ERROR : ${exception.message}")
        }
    }

    fun setButtonsText(context: Context?, showEstateType: Enums.ShowEstateType) {
        when (showEstateType) {
            Enums.ShowEstateType.ASK_FOR_CONFIRMATION -> {
                buttonLeftString.set(context?.getString(R.string.button_cancel))
                buttonRightString.set(context?.getString(R.string.button_confirm))
            }
            Enums.ShowEstateType.SHOW_ESTATE -> {
                buttonLeftString.set(context?.getString(R.string.button_delete))
                buttonRightString.set(context?.getString(R.string.button_edit))
            }
        }
    }

    fun hideNearbyLayout() {
        nearbyLayoutVisibility.set(View.GONE)
    }

    fun showNearbyLayout() {
        nearbyLayoutVisibility.set(View.VISIBLE)
    }

    fun showImagesLayout() {
        imagesLayoutVisibility.set(View.VISIBLE)
    }

    fun hideImagesLayout() {
        imagesLayoutVisibility.set(View.GONE)
    }

    companion object {
        private const val TAG = "ShowEstateAVM"
    }
}