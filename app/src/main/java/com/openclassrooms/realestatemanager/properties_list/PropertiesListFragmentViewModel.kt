package com.openclassrooms.realestatemanager.properties_list

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.R

class PropertiesListFragmentViewModel : ViewModel() {

    val estatePreviewVisibility = ObservableInt(View.GONE)

    val filterButtonVisibility = ObservableInt(View.VISIBLE)
    val filterButtonTextValue = ObservableField("")

    val loadingVisibility = ObservableInt(View.GONE)
    val noPropertiesVisibility = ObservableInt(View.GONE)
    val propertiesListVisibility = ObservableInt(View.GONE)

    fun setLoading() {
        noPropertiesVisibility.set(View.GONE)
        propertiesListVisibility.set(View.GONE)
        loadingVisibility.set(View.VISIBLE)
        hideFilterButton()
    }

    fun setNoProperties() {
        propertiesListVisibility.set(View.GONE)
        loadingVisibility.set(View.GONE)
        noPropertiesVisibility.set(View.VISIBLE)
    }

    fun setPropertiesList() {
        loadingVisibility.set(View.GONE)
        noPropertiesVisibility.set(View.GONE)
        propertiesListVisibility.set(View.VISIBLE)
        showFilterButton()
    }

    fun setEstatePreview() {
        estatePreviewVisibility.set(View.VISIBLE)
    }

    fun removeEstatePreview() {
        estatePreviewVisibility.set(View.GONE)
    }

    fun setSearchInProgress(context: Context) {
        filterButtonTextValue.set(context.getString(R.string.button_cancel_search))
    }

    fun setDefaultFilterButton(context: Context) {
        filterButtonTextValue.set(context.getString(R.string.button_filter))
    }

    fun showFilterButton() {
        filterButtonVisibility.set(View.VISIBLE)
    }

    fun hideFilterButton() {
        filterButtonVisibility.set(View.GONE)
    }

}