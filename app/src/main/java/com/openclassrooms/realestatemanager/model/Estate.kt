package com.openclassrooms.realestatemanager.model

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Estate : Serializable {

    var id : Long? = null
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
}