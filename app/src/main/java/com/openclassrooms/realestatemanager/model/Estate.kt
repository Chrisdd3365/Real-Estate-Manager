package com.openclassrooms.realestatemanager.model

import android.content.ContentValues
import android.database.Cursor
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.DatabaseManager.Companion.getBoolean
import com.openclassrooms.realestatemanager.utils.Enums
import com.openclassrooms.realestatemanager.utils.Singleton
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.Serializable
import java.util.*

class Estate : Serializable {

    var id : Int? = null
    var typeIndex : Int? = null
    var description : String? = null
    var address : String? = null
    var onMarketSince : Calendar? = null

    private var price : Double? = null
    private var surface : Double? = null
    var roomCount : Int? = null
    var bathroomsCount : Int? = null
    var bedroomsCount : Int? = null

    var school : Boolean? = null
    var playground : Boolean? = null
    var shop : Boolean? = null
    var buses : Boolean? = null
    var subway : Boolean? = null
    var park : Boolean? = null

    var latitude : Double? = null
    var longitude : Double? = null

    var sold : Boolean? = null

    constructor()

    constructor(cursor: Cursor) {
        with (cursor) {
            id = getInt(getColumnIndex(DatabaseManager.COLUMN_ID))
            typeIndex = getInt(getColumnIndex(DatabaseManager.COLUMN_TYPE))
            description = getString(getColumnIndex(DatabaseManager.COLUMN_DESCRIPTION))
            address = getString(getColumnIndex(DatabaseManager.COLUMN_ADDRESS))
            onMarketSince = Calendar.getInstance().apply {
                timeInMillis = getLong(getColumnIndex(DatabaseManager.COLUMN_ON_MARKET_SINCE))
            }
            setDollarPrice(getDouble(getColumnIndex(DatabaseManager.COLUMN_PRICE)))
            setSquareFeetSurface(getDouble(getColumnIndex(DatabaseManager.COLUMN_SURFACE)))
            roomCount = getInt(getColumnIndex(DatabaseManager.COLUMN_ROOMS_COUNT))
            bathroomsCount = getInt(getColumnIndex(DatabaseManager.COLUMN_BATHROOMS_COUNT))
            bedroomsCount = getInt(getColumnIndex(DatabaseManager.COLUMN_BEDROOMS_COUNT))
            school = getBoolean(getColumnIndex(DatabaseManager.COLUMN_SCHOOL_NEARBY))
            playground = getBoolean(getColumnIndex(DatabaseManager.COLUMN_PLAYGROUND_NEARBY))
            shop = getBoolean(getColumnIndex(DatabaseManager.COLUMN_SHOP_NEARBY))
            buses = getBoolean(getColumnIndex(DatabaseManager.COLUMN_BUSES_NEARBY))
            subway = getBoolean(getColumnIndex(DatabaseManager.COLUMN_SUBWAY_NEARBY))
            park = getBoolean(getColumnIndex(DatabaseManager.COLUMN_PARK_NEARBY))
            latitude = getDouble(getColumnIndex(DatabaseManager.COLUMN_LATITUDE))
            longitude = getDouble(getColumnIndex(DatabaseManager.COLUMN_LONGITUDE))
            sold = getBoolean(getColumnIndex(DatabaseManager.COLUMN_SOLD))
        }
    }

    /**
     *  Saves the price in this [Estate] instance.
     *  Note that we always save the price into Dollars, so if the user is currently using Euro
     *  currency, we convert the price he entered before saving it.
     *  @param price [Double] - Price of the [Estate] to save.
     */
    fun setPrice(price : Double) {
        this.price =
            if (Singleton.currency == Enums.Currency.DOLLAR) price
            else Utils.convertEuroToDollarDouble(price)
    }

    /**
     *  As we are always saving the price in Dollars, if the user is currently using Euro currency,
     *  we need to convert the price before returning it.
     *  @return [String] - The price of this [Estate], as it in Dollars, or converted in Euros.
     */
    fun getPrice() : String {
        val price = when {
            price == null -> 0.0
            Singleton.currency == Enums.Currency.DOLLAR -> price!!
            else -> Utils.convertDollarToEuroDouble(price!!)
        }
        return Utils.getRoundedBigDecimal(price).toPlainString()
    }

    /**
     *  @return the original price (always in Dollars). This is mostly used when saving data in the
     *  database.
     */
    fun getDollarPrice() : Double {
        return if (price == null) 0.0 else price!!
    }

    /**
     *  When we want to save a price in this [Estate] and we are sure that it is in Dollars, we can
     *  use this function. This is mostly used when retrieving data from the database.
     *  @param price [Double] - The new price, in dollars, of this [Estate].
     */
    fun setDollarPrice(price: Double) {
        this.price = price
    }

    /**
     *  Saves the surface of this [Estate] instance.
     *  Note that we always save the surface in square feet, so if the user is currently using
     *  square meters, we need to convert this surface in square feet before saving it.
     *  @param surface [Double] - Surface of the [Estate] to save.
     */
    fun setSurface(surface: Double) {
        this.surface =
            if (Singleton.unit == Enums.Unit.FEET) surface
            else Utils.convertSquareMetersToSquareFeet(surface)
    }

    /**
     *  As we are always saving the surface in square feet, if the user is currently using square
     *  meters, we first need to convert the surface before returning it.
     *  @return [String] - The surface of this [Estate], as it in square feet, or converted in
     *  square meters.
     */
    fun getSurface() : String {
        val surface = when {
            surface == null -> 0.0
            Singleton.unit ==  Enums.Unit.FEET  -> surface!!
            else -> Utils.convertSquareFeetToSquareMeter(surface!!)
        }
        return Utils.getRoundedBigDecimal(surface).toPlainString()
    }

    /**
     *  When we want to save the surface in this [Estate] and we are sure that it is in square feet,
     *  we can use this function. This is mostly used when retrieving data from the database.
     *  @param surface [Double] - The new surface, in square feet, of this [Estate].
     */
    fun setSquareFeetSurface(surface: Double) {
        this.surface = surface
    }

    /**
     *  @return the original surface (always in square feet). This is mostly used when saving this
     *  [Estate] in the database.
     */
    fun getSquareFeetSurface() : Double {
        return if (surface == null) 0.0 else surface!!
    }

    fun toContentValues() : ContentValues {
        return ContentValues().apply {
            put(DatabaseManager.COLUMN_TYPE, typeIndex)
            put(DatabaseManager.COLUMN_DESCRIPTION, description)
            put(DatabaseManager.COLUMN_ADDRESS, address)
            put(DatabaseManager.COLUMN_ON_MARKET_SINCE,
                onMarketSince?.timeInMillis ?: Calendar.getInstance().timeInMillis)
            put(DatabaseManager.COLUMN_PRICE, getDollarPrice())
            put(DatabaseManager.COLUMN_SURFACE, getSquareFeetSurface())
            put(DatabaseManager.COLUMN_ROOMS_COUNT, roomCount)
            put(DatabaseManager.COLUMN_BATHROOMS_COUNT, bathroomsCount)
            put(DatabaseManager.COLUMN_BEDROOMS_COUNT, bedroomsCount)
            put(DatabaseManager.COLUMN_SCHOOL_NEARBY, school)
            put(DatabaseManager.COLUMN_PLAYGROUND_NEARBY, playground)
            put(DatabaseManager.COLUMN_SHOP_NEARBY, shop)
            put(DatabaseManager.COLUMN_BUSES_NEARBY, buses)
            put(DatabaseManager.COLUMN_SUBWAY_NEARBY, subway)
            put(DatabaseManager.COLUMN_PARK_NEARBY, park)
            put(DatabaseManager.COLUMN_LATITUDE, latitude)
            put(DatabaseManager.COLUMN_LONGITUDE, longitude)
            put(DatabaseManager.COLUMN_SOLD, sold)
        }
    }

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