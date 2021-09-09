package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.net.Uri


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

    fun decodeUri(context: Context, uri: Uri, requiredSize: Int): Bitmap? {

        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri),
            null,
            bitmapOptions
        )
        var widthTmp = bitmapOptions.outWidth
        var heightTmp = bitmapOptions.outHeight
        var scale = 1
        while (true) {
            if (widthTmp / 2 < requiredSize || heightTmp / 2 < requiredSize) break
            widthTmp /= 2
            heightTmp /= 2
            scale *= 2
        }
        val resultBitmapOptions = BitmapFactory.Options()
        resultBitmapOptions.inSampleSize = scale
        return BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri),
            null,
            resultBitmapOptions
        )
    }
}