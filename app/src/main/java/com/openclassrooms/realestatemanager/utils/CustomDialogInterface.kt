package com.openclassrooms.realestatemanager.utils

import android.app.Dialog
import java.util.*

interface CustomDialogInterface {

    fun cancelButtonClicked(dialog : Dialog)

    fun confirmSearchClicked(priceRange : IntArray, surfaceRange : IntArray, roomsRange : IntArray,
                             bathroomsRange : IntArray, bedroomsRange : IntArray,
                             schoolValue : Boolean, playgroundValue : Boolean, shopValue : Boolean,
                             busesValue : Boolean, subwayValue : Boolean, parkValue : Boolean,
                             fromDate : Date)
}