package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.openclassrooms.realestatemanager.BuildConfig
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return Math.round(dollars * 0.812).toInt()
    }

    fun convertDollarToEuroDouble(dollars : Double) : Double {
        return dollars * 0.812
    }

    fun convertEuroToDollarDouble(euros : Double) : Double {
        return euros / 0.812
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    val todayDate: String
        get() {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd")
            return dateFormat.format(Date())
        }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    fun isInternetAvailable(context: Context): Boolean {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifi.isWifiEnabled
    }

    fun getBitmapFromUri(context : Context, uri : String) : Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                context.contentResolver,
                Uri.parse(uri))
            )
        else
            MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(uri))
    }

    fun getTmpFileUri(context: Context): Uri {
        val tmpFile = File.createTempFile(
            "tmp_image_file",
            ".png",
            context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider", tmpFile
        )
    }

    fun switchCurrency(context: Context) {
        val newCurrency =
            if (Singleton.currency == Enums.Currency.DOLLAR) Enums.Currency.EURO
            else Enums.Currency.DOLLAR
        changeCurrency(context, newCurrency)
    }

    /**
     *  Saves the chosen currency in the [Singleton] and in the SharedPreferences.
     *  @param context [Context] - Current context
     *  @param newCurrency [Enums.Currency] - New currency to save.
     */
    fun changeCurrency(context: Context, newCurrency : Enums.Currency) {
        Singleton.currency = newCurrency
        Singleton.currencySymbol = if (newCurrency == Enums.Currency.DOLLAR) "$" else "€"
        SharedPreferencesManager.saveCurrency(context, newCurrency)
    }

    fun getPriceWithCurrency(price : Double) : Double {
        return if (Singleton.currency == Enums.Currency.DOLLAR)
            price
        else
            convertDollarToEuroDouble(price)
    }
}