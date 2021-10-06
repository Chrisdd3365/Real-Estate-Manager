package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.content.SharedPreferences
import com.openclassrooms.realestatemanager.BuildConfig

object SharedPreferencesManager {

    private const val SHARED_PREFERENCES_FILE = "${BuildConfig.APPLICATION_ID}-prefs"

    private const val TAG_CURRENCY = "currency"

    private fun getSharedPreferences(context: Context) : SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    fun saveCurrency(context : Context, currency: Enums.Currency) {
        val sharedPreferences = getSharedPreferences(context)

        with (sharedPreferences.edit()) {
            putInt(TAG_CURRENCY, currency.ordinal)
            apply()
        }
    }

    fun getCurrency(context: Context) : Enums.Currency {
        val sharedPreferences = getSharedPreferences(context)

        val ordinal = sharedPreferences.getInt(TAG_CURRENCY, Enums.Currency.DOLLAR.ordinal)
        return Enums.Currency.values()[ordinal]
    }
}