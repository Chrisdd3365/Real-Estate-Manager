package com.openclassrooms.realestatemanager.model

import java.util.*
import kotlin.collections.ArrayList

class Estate {

    var type = ""   // TODO : Remove
    var typeIndex = 0
    var description = ""
    var address = ""
    var onMarketSince = Date()

    var price = 0f
    var surface = 0f
    var roomCount = 0
    var bathroomsCount = 0
    var bedroomsCount = 0

    var school = false
    var playground = false
    var shop = false
    var buses = false
    var subway = false
    var park = false

    var picturesUris = ArrayList<String>()
}