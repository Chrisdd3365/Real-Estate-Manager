package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.model.Estate
import java.util.*
import kotlin.collections.ArrayList

object StaticData {

    var staticEstatesList : ArrayList<Estate> = ArrayList<Estate>().apply {

        add(Estate().apply {
            typeIndex = 0 ; type = "House" ; description = "First test house" ; address = "123 rue Test"
            onMarketSince = Date() ; setPrice(115000.0) ; surface = 500f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 1 ; type = "Duplex" ;
            description = "Second test house with a very long description Second test house with " +
                    "a very long description Second test house with a very long description " +
                    "Second test house with a very long description Second test house with a " +
                    "very long description " ;
            address = "456 rue Cerise"
            onMarketSince = Date() ; setPrice(110000.0) ; surface = 400f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = true
            shop = true ; buses = true ; subway = true ; park = true
        })

        add(Estate().apply {
            typeIndex = 2 ; type = "Flat" ; description = "Third test house" ; address = "789 rue du Panier"
            onMarketSince = Date() ; setPrice(120000.0) ; surface = 600f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 3 ; type = "Townhouse" ; description = "Fourth test house" ; address = "4 rue General F"
            onMarketSince = Date() ; setPrice(120000.0) ; surface = 610f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 4 ; type = "Penthouse" ; description = "Fifth test house" ; address = "2 rue Siamois"
            onMarketSince = Date() ; setPrice(95000.0) ; surface = 300f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 0 ; type = "House" ; description = "Sixth test house" ; address = "7 impasse Chaton"
            onMarketSince = Date() ; setPrice(150000.0) ; surface = 1000f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 1 ; type = "Duplex" ; description = "Seventh test house" ; address = "8 rue du Pêcheur"
            onMarketSince = Date() ; setPrice(115000.0) ; surface = 550f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 2 ; type = "Flat" ; description = "Eighth test house" ; address = "123 rue Jesus"
            onMarketSince = Date() ; setPrice(112000.0) ; surface = 590f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 3 ; type = "Townhouse" ; description = "Ninth test house" ; address = "394 rue Snap"
            onMarketSince = Date() ; setPrice(114000.0) ; surface = 500f ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })
    }

}