package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.model.Estate
import java.util.*
import kotlin.collections.ArrayList

object StaticData {

    var staticEstatesList : ArrayList<Estate> = ArrayList<Estate>().apply {

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.MARCH, 12) }
            typeIndex = 0 ; description = "First test house" ; address = "123 rue Test"
            onMarketSince = calendarDate ; setPrice(115000.0) ; setSurface(500.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = false
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.APRIL, 8) }
            typeIndex = 1 ;
            description = "Second test house with a very long description Second test house with " +
                    "a very long description Second test house with a very long description " +
                    "Second test house with a very long description Second test house with a " +
                    "very long description " ;
            address = "456 rue Cerise"
            onMarketSince = calendarDate ; setPrice(110000.0) ; setSurface(400.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = true
            shop = true ; buses = true ; subway = true ; park = true ; sold = true
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.MAY, 3) }
            typeIndex = 2 ; description = "Third test house" ; address = "789 rue du Panier"
            onMarketSince = calendarDate ; setPrice(120000.0) ; setSurface(600.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = false
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.JUNE, 23) }
            typeIndex = 3 ; description = "Fourth test house" ; address = "4 rue General F"
            onMarketSince = calendarDate ; setPrice(120000.0) ; setSurface(610.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = false
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.JULY, 13) }
            typeIndex = 4 ; description = "Fifth test house" ; address = "2 rue Siamois"
            onMarketSince = calendarDate ; setPrice(95000.0) ; setSurface(300.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = true
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.AUGUST, 30) }
            typeIndex = 0 ; description = "Sixth test house" ; address = "7 impasse Chaton"
            onMarketSince = calendarDate ; setPrice(150000.0) ; setSurface(1000.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = false
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.SEPTEMBER, 11) }
            typeIndex = 1 ; description = "Seventh test house" ; address = "8 rue du Pêcheur"
            onMarketSince = calendarDate ; setPrice(115000.0) ; setSurface(550.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = true
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.OCTOBER, 3) }
            typeIndex = 2 ; description = "Eighth test house" ; address = "123 rue Jesus"
            onMarketSince = calendarDate ; setPrice(112000.0) ; setSurface(590.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = false
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.NOVEMBER, 11) }
            typeIndex = 3 ; description = "Ninth test house" ; address = "394 rue Snap"
            onMarketSince = calendarDate ; setPrice(114000.0) ; setSurface(500.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = false
        })

        add(Estate().apply {
            val calendarDate = Calendar.getInstance().apply { set(2021, Calendar.DECEMBER, 24) }
            typeIndex = 3 ; description = "Last test house" ; address = "North Pole"
            onMarketSince = calendarDate ; setPrice(114000.0) ; setSurface(500.0) ; roomCount = 3
            bathroomsCount = 2 ; bedroomsCount = 4 ; school = true ; playground = false
            shop = true ; buses = true ; subway = true ; park = false ; sold = true
        })
    }

}