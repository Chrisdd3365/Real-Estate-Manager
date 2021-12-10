package com.openclassrooms.realestatemanager.estate_creation.managing_details

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class ManagingFragmentViewModel : ViewModel() {

    val agentsListVisibility = ObservableInt(View.GONE)
    val loadingVisibility = ObservableInt(View.GONE)
    val noAgentsVisibility = ObservableInt(View.GONE)

    fun setAgents() {
        loadingVisibility.set(View.GONE)
        noAgentsVisibility.set(View.GONE)
        agentsListVisibility.set(View.VISIBLE)
    }

    fun setLoading() {
        noAgentsVisibility.set(View.GONE)
        agentsListVisibility.set(View.GONE)
        loadingVisibility.set(View.VISIBLE)
    }

    fun setNoAgents() {
        agentsListVisibility.set(View.GONE)
        loadingVisibility.set(View.GONE)
        noAgentsVisibility.set(View.VISIBLE)
    }
}