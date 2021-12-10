package com.openclassrooms.realestatemanager.show_estate

import android.content.Context
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums
import com.openclassrooms.realestatemanager.utils.Singleton
import com.openclassrooms.realestatemanager.utils.Utils

class ShowEstateFragmentViewModel : ViewModel() {

    val nearbyLayoutVisibility = ObservableInt(View.GONE)
    val imagesLayoutVisibility = ObservableInt(View.GONE)
    val managingAgentsVisibility = ObservableInt(View.GONE)
    val markAsSoldButtonVisibility = ObservableInt(View.GONE)
    val sellDateVisibility = ObservableInt(View.GONE)

    val description = ObservableField("")
    val surfaceSize = ObservableField("")
    val roomsCount = ObservableField("")
    val bedroomsCount = ObservableField("")
    val bathroomsCount = ObservableField("")
    val price = ObservableField("")
    val type = ObservableField("")
    val onMarketSince = ObservableField("")
    val sellDate = ObservableField("")
    val latLng = ObservableField("")

    val buttonLeftString = ObservableField("")
    val buttonRightString = ObservableField("")
    val buttonMarkAsSoldString = ObservableField("")

    fun setData(context: Context, estate: Estate) {
        description.set(estate.description)
        surfaceSize.set("${estate.getSurface()} ${Singleton.unitSymbol}")
        roomsCount.set("${estate.roomCount?.toString()} ${context.getString(R.string.rooms).lowercase()}")
        bedroomsCount.set("${estate.bedroomsCount?.toString()} ${context.getString(R.string.bedrooms).lowercase()}")
        bathroomsCount.set("${estate.bathroomsCount?.toString()} ${context.getString(R.string.bathrooms).lowercase()}")
        price.set("${estate.getPrice()} ${Singleton.currencySymbol}")
        try {
            type.set(context.resources?.getStringArray(R.array.estate_types)?.get(estate.typeIndex!!))
        } catch (exception : Exception) {
            Log.e(TAG, "ERROR : ${exception.message}")
        }
        if (estate.onMarketSince != null) {
            onMarketSince.set(
                "${context.getString(R.string.on_market_since_title)} " +
                        Utils.parseDate(estate.onMarketSince!!)
            )
        }
        latLng.set("${LatLng(estate.latitude ?: 0.0, estate.longitude ?: 0.0)}")

        setSellDate(context, estate)
    }

    fun setSellDate(context : Context, estate: Estate) {
        if (estate.sold == true) {
            sellDate.set(
                "${context.getString(R.string.sold_at)} " + Utils.parseDate(estate.soldDate!!)
            )
            sellDateVisibility.set(View.VISIBLE)
            buttonMarkAsSoldString.set(context.getString(R.string.button_mark_as_not_sold))
        } else {
            sellDateVisibility.set(View.GONE)
            buttonMarkAsSoldString.set(context.getString(R.string.button_mark_as_sold))
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

    fun showMarkAsSoldButton() {
        markAsSoldButtonVisibility.set(View.VISIBLE)
    }

    fun hideMarkAsSoldButton() {
        markAsSoldButtonVisibility.set(View.GONE)
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

    fun showManagingAgents() {
        managingAgentsVisibility.set(View.VISIBLE)
    }

    fun hideManagingAgents() {
        managingAgentsVisibility.set(View.GONE)
    }

    companion object {
        private const val TAG = "ShowEstateAVM"
    }
}