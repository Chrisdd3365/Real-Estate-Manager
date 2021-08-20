package com.openclassrooms.realestatemanager.properties_list

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class PropertiesListFragmentViewModel : ViewModel() {

    val loadingVisibility = ObservableInt(View.GONE)
    val noPropertiesVisibility = ObservableInt(View.GONE)
    val propertiesListVisibility = ObservableInt(View.GONE)

    fun setLoading() {
        noPropertiesVisibility.set(View.GONE)
        propertiesListVisibility.set(View.GONE)
        loadingVisibility.set(View.VISIBLE)
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
    }

}