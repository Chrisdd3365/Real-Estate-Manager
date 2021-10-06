package com.openclassrooms.realestatemanager.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.utils.Enums
import com.openclassrooms.realestatemanager.utils.Singleton
import com.openclassrooms.realestatemanager.utils.Utils
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

    private var price : Double? = null
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

    var latitude : Double? = null
    var longitude : Double? = null

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
     *  @return [Double] - The price of this [Estate], as it in Dollars, or converted in Euros.
     */
    fun getPrice() : Double {
        return when {
            price == null -> 0.0
            Singleton.currency == Enums.Currency.DOLLAR -> price!!
            else -> Utils.convertDollarToEuroDouble(price!!)
        }
    }

    /**
     *  Returns the original price (always in Dollars). This is mostly used when saving data in the
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