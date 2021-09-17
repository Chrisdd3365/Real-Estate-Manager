package com.openclassrooms.realestatemanager.estate_creation.add_images

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

class AddPicturesFragmentViewModel : ViewModel() {

    val noPicturesVisibility = ObservableInt(View.GONE)
    val picturesVisibility = ObservableInt(View.GONE)

    fun setNoPictures() {
        picturesVisibility.set(View.GONE)
        noPicturesVisibility.set(View.VISIBLE)
    }

    fun setPictures() {
        noPicturesVisibility.set(View.GONE)
        picturesVisibility.set(View.VISIBLE)
    }

}