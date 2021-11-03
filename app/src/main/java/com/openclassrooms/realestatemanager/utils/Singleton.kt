package com.openclassrooms.realestatemanager.utils

import java.util.*

object Singleton {

    var currency = Enums.Currency.DOLLAR
    var currencySymbol = "$"

    var unit = Enums.Unit.METER
    var unitSymbol = "ft²"

    var oldestEstate = Calendar.getInstance()
    var mostRecentEstate = Calendar.getInstance()
}