package com.openclassrooms.realestatemanager.model

import android.util.Log
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Estate : Serializable {

    var id : Int? = null
    var type : String? = null   // TODO : Remove
    var typeIndex : Int? = null
    var description : String? = null
    var address : String? = null
    var onMarketSince : Date? = null

    var price : Float? = null
    var surface : Float? = null
    var roomCount : Int? = null
    var bathroomsCount : Int? = null
    var bedroomsCount : Int? = null

    var school : Boolean? = null
    var playground : Boolean? = null
    var shop : Boolean? = null
    var buses : Boolean? = null
    var subway : Boolean? = null
    var park : Boolean? = null

    var picturesUris = ArrayList<String>()

    override fun toString(): String {
        return "[ESTATE] $id ; $roomCount rooms ; $bathroomsCount bathrooms ; " +
                "$bedroomsCount bedrooms"
    }

    override fun equals(other: Any?): Boolean {
        if ((other as Estate).id == this.id)
            return true
        return super.equals(other)
    }

    companion object {

        @Suppress("unused")
        private const val TAG = "Estate"
    }
}