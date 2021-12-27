package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import java.io.File
import java.io.FileDescriptor
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {

    @Suppress("unused")
    private const val TAG = "Utils"

    private const val SQUARE_SYMBOL = "m²"
    private const val FEET_SYMBOL = "ft²"

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    @Suppress("unused")
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.812).roundToInt()
    }

    fun convertDollarToEuroDouble(dollars : Double) : Double {
        return getRoundedBigDecimal(dollars.times(0.812)).toDouble()
    }

    fun convertEuroToDollarDouble(euros : Double) : Double {
        return getRoundedBigDecimal(euros / 0.812).toDouble()
    }

    fun convertSquareMetersToSquareFeet(squareMeters: Double) : Double {
        return getRoundedBigDecimal(squareMeters * 10.76391).toDouble()
    }

    fun convertSquareFeetToSquareMeter(squareFeet: Double) : Double {
        return getRoundedBigDecimal(squareFeet / 10.76391).toDouble()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    @Suppress("unused")
    fun getTodayDate(date: Date): String? {
        val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(date)
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    fun isInternetAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun getBitmapFromUri(context : Context, uri : String) : Bitmap {
        val parcelFileDescriptor : ParcelFileDescriptor? = context.contentResolver
            .openFileDescriptor(Uri.parse(uri), "r")
        val fileDescriptor : FileDescriptor? = parcelFileDescriptor?.fileDescriptor
        return BitmapFactory.decodeFileDescriptor(fileDescriptor)
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

    @Suppress("unused")
    fun getPriceWithCurrency(price : Double) : Double {
        return if (Singleton.currency == Enums.Currency.DOLLAR)
            price
        else
            convertDollarToEuroDouble(price)
    }

    fun switchUnits(context: Context) {
        val newUnit =
            if (Singleton.unit == Enums.Unit.FEET) Enums.Unit.METER
            else Enums.Unit.FEET
        changeUnit(context, newUnit)
    }

    fun changeUnit(context: Context, newUnit : Enums.Unit) {
        Singleton.unit = newUnit
        Singleton.unitSymbol = if (newUnit == Enums.Unit.FEET) FEET_SYMBOL else SQUARE_SYMBOL
        SharedPreferencesManager.saveUnit(context, newUnit)
    }

    fun getRoundedBigDecimal(value : Double) : BigDecimal {
        return BigDecimal(value)
            .setScale(2, RoundingMode.HALF_UP)
            .stripTrailingZeros()
    }

    fun parseDate(calendar: Calendar) : String {
        val dateFormat = SimpleDateFormat(
            if (isFrench()) "dd/MM/yyyy" else "MM/dd/yyyy",
            Locale.getDefault()
        )
        return dateFormat.format(calendar.time)
    }

    private fun isFrench(): Boolean {
        return (Locale.getDefault().displayLanguage == Locale.FRENCH.displayLanguage)
    }

    fun checkEstatesTimeOnMarket(currentEstateDate : Calendar) {
        if (currentEstateDate.before(Singleton.oldestEstate))
            Singleton.oldestEstate = currentEstateDate
        if (currentEstateDate.after(Singleton.mostRecentEstate))
            Singleton.mostRecentEstate = currentEstateDate
    }

    /**
     *  Shows an [AlertDialog] to explain to the user that the app needs the permissions he just
     *  denied, and how to enable them.
     *  If [Build.VERSION.SDK_INT] is >= [Build.VERSION_CODES.R], we cannot ask for permissions more
     *  than twice, so we give to the user a way to go to the app settings.
     *  In the previous versions however, we only re-ask for permissions.
     *  @param context [Context]
     *  @param content [String] - The message to display inside the [AlertDialog].
     *  @param goToSettings [Unit] - The function to execute if the user clicks on the
     *  "Go to settings" option.
     *  @param reAskPermissions [Unit] - The function to execute if the user clicks on the "Re-ask
     *  permissions" option.
     */
    fun showPermissionsDeniedDialog(context: Context,
                                    content: String,
                                    goToSettings: () -> Unit,
                                    reAskPermissions: () -> Unit) {
        val dialogBuilder = AlertDialog.Builder(context)
        val message: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            message = "$content\n${context.getString(R.string.permissions_not_granted_solution_new)}"
            dialogBuilder.setPositiveButton(R.string.go_to_settings) { _, _ ->
                goToSettings.invoke()
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
            }
        } else {
            message = "$content\n${context.getString(R.string.permissions_not_granted_solution_old)}"
            dialogBuilder.setPositiveButton(R.string.allow_permissions) { dialog, _ ->
                dialog.dismiss()
                reAskPermissions.invoke()
            }
        }

        dialogBuilder.setNegativeButton(R.string.button_confirm) { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.setTitle(R.string.permissions_not_granted_title)
        dialogBuilder.setMessage(message)
        dialogBuilder.create().show()
    }
}