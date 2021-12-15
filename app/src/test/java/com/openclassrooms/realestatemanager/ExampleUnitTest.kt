package com.openclassrooms.realestatemanager

import android.content.Context
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.show_estate.ShowEstateFragmentViewModel
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    private val context: Context = mock(Context::class.java)
    private val mEstate = mock(Estate::class.java)

    private val showEstateFragmentViewModel = ShowEstateFragmentViewModel()

    @Before
    fun setUp() {
        Mockito.doReturn("address").`when`(mEstate).address
//        Mockito.doReturn(1).`when`(mEstate).bathroomsCount
//        Mockito.doReturn(2).`when`(mEstate).bedroomsCount
//        Mockito.doReturn(true).`when`(mEstate).buses
//        Mockito.doReturn("description").`when`(mEstate).description
//        Mockito.doReturn(0).`when`(mEstate).id
//        Mockito.doReturn(30.0).`when`(mEstate).latitude
//        Mockito.doReturn(1.0).`when`(mEstate).longitude
//        Mockito.doReturn(Calendar.getInstance()).`when`(mEstate).onMarketSince
//        Mockito.doReturn(true).`when`(mEstate).park
//        Mockito.doReturn(true).`when`(mEstate).playground
//        Mockito.doReturn(2).`when`(mEstate).roomCount
//        Mockito.doReturn(true).`when`(mEstate).school
//        Mockito.doReturn(true).`when`(mEstate).shop
//        Mockito.doReturn(true).`when`(mEstate).sold
//        Mockito.doReturn(Calendar.getInstance()).`when`(mEstate).soldDate
//        Mockito.doReturn(true).`when`(mEstate).subway
//        Mockito.doReturn(1).`when`(mEstate).typeIndex
    }

    @Test
    fun setData_nominalCase() {
        val address = "address"

        showEstateFragmentViewModel.setData(context, mEstate)

        assertEquals(address, mEstate.address)
    }

}