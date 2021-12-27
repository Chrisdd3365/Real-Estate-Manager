package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.utils.Utils
import junit.framework.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    private val context = Mockito.mock(Context::class.java)

    @Test
    fun testConvertDollarToEuro() {
        val priceInDollar = 777777
        assertEquals(631555, Utils.convertDollarToEuro(priceInDollar))
    }

    @Test
    fun testConvertEuroToDollarDouble() {
        val priceInEuro = 777777.0
        assertEquals(957853.45, Utils.convertEuroToDollarDouble(priceInEuro))
    }

    @Test
    fun whenInternetIsNotAvailableForOldSdk() {
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val networkInfo = Mockito.mock(NetworkInfo::class.java)

        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(false)

        assertFalse(Utils.isInternetAvailable(context))
    }

    @Test
    fun whenInternetIsNotAvailableForNewSdk() {
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val capabilities = Mockito.mock(NetworkCapabilities::class.java)

        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)).thenReturn(capabilities)

        assertFalse(Utils.isInternetAvailable(context))
    }

    @Test
    fun whenInternetIsAvailableForOldSdk() {
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val networkInfo = Mockito.mock(NetworkInfo::class.java)

        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(true)

        assertTrue(Utils.isInternetAvailable(context))
    }

    @Test
    fun whenInternetIsAvailableTransportCellularForNewSdk() {
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val capabilities = Mockito.mock(NetworkCapabilities::class.java)

        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)).thenReturn(capabilities)
        Mockito.`when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true)

        assertTrue(Utils.isInternetAvailable(context))
    }

    @Test
    fun whenInternetIsAvailableTransportWifiForNewSdk() {
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val capabilities = Mockito.mock(NetworkCapabilities::class.java)

        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)).thenReturn(capabilities)
        Mockito.`when`(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)

        assertTrue(Utils.isInternetAvailable(context))
    }

    @Test
    fun convertTodayDateToRightFormat() {
        val date = Date()
        assertEquals("27/12/2021", Utils.getTodayDate(date))
    }

}