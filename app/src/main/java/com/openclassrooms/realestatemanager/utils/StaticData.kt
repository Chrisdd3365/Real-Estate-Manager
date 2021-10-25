package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.model.Estate
import java.util.*
import kotlin.collections.ArrayList

object StaticData {

    var staticEstatesList : ArrayList<Estate> = ArrayList<Estate>().apply {

        add(Estate().apply {
            typeIndex = 0 ; description = "First test house" ; address = "123 rue Test"
            onMarketSince = Date() ; setPrice(115000.0) ; setSurface(500.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 1 ;
            description = "Second test house with a very long description Second test house with " +
                    "a very long description Second test house with a very long description " +
                    "Second test house with a very long description Second test house with a " +
                    "very long description " ;
            address = "456 rue Cerise"
            onMarketSince = Date() ; setPrice(110000.0) ; setSurface(400.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = true
            shop = true ; buses = true ; subway = true ; park = true
        })

        add(Estate().apply {
            typeIndex = 2 ; description = "Third test house" ; address = "789 rue du Panier"
            onMarketSince = Date() ; setPrice(120000.0) ; setSurface(600.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 3 ; description = "Fourth test house" ; address = "4 rue General F"
            onMarketSince = Date() ; setPrice(120000.0) ; setSurface(610.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 4 ; description = "Fifth test house" ; address = "2 rue Siamois"
            onMarketSince = Date() ; setPrice(95000.0) ; setSurface(300.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 0 ; description = "Sixth test house" ; address = "7 impasse Chaton"
            onMarketSince = Date() ; setPrice(150000.0) ; setSurface(1000.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 1 ; description = "Seventh test house" ; address = "8 rue du Pêcheur"
            onMarketSince = Date() ; setPrice(115000.0) ; setSurface(550.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 2 ; description = "Eighth test house" ; address = "123 rue Jesus"
            onMarketSince = Date() ; setPrice(112000.0) ; setSurface(590.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })

        add(Estate().apply {
            typeIndex = 3 ; description = "Ninth test house" ; address = "394 rue Snap"
            onMarketSince = Date() ; setPrice(114000.0) ; setSurface(500.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false
        })
    }

}