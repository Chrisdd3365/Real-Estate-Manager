package com.openclassrooms.realestatemanager

import android.util.Log
import android.widget.Toast
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.DatabaseManager.Companion.getBoolean
import com.openclassrooms.realestatemanager.estate_creation.EstateCreationActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import com.openclassrooms.realestatemanager.model.Estate
import com.openclassrooms.realestatemanager.utils.Enums
import org.junit.After

import org.junit.Before
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseManagerTest {

    private var estate = Estate()

    private lateinit var databaseManager: DatabaseManager


    @Before
    fun setUp() {
        databaseManager = DatabaseManager(InstrumentationRegistry.getInstrumentation().targetContext)

        estate.id = 0
        estate.typeIndex = 1
        estate.description = "description"
        estate.address = "address"
        estate.onMarketSince = Calendar.getInstance()
        estate.setPrice(77777777.0)
        estate.setSurface(77.0)
        estate.roomCount = 3
        estate.bathroomsCount = 2
        estate.bedroomsCount = 2
        estate.school = true
        estate.playground = true
        estate.shop = true
        estate.buses = true
        estate.subway = true
        estate.park = true
        estate.latitude = 42.0
        estate.longitude = 2.0
        estate.sold = true
        estate.soldDate = Calendar.getInstance()
    }

    @After
    fun finish() {
        databaseManager.close()
    }

    @Test
    fun testPreConditions() {
        assertNotNull(databaseManager)
    }

    @Test
    fun testShouldSaveEstate() {
        databaseManager.saveEstate(
            estate,
            onSuccess = { insertedId ->
                assertEquals(1, insertedId)
            },
            onFailure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldUpdateEstate() {
        estate.description = "description 2"
        databaseManager.updateEstate(
            estate,
            onSuccess = {
                assertEquals("description 2", estate.description)
            },
            onFailure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldDeleteEstate() {
        databaseManager.deleteEstate(
            1,
            onSuccess = {
                assertEquals(null, estate.id)
            },
            onFailure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldGetAllEstates() {
        databaseManager.saveEstate(
            estate,
            onSuccess = { insertedId ->
                databaseManager.getEstates(
                    success = { estatesList ->
                        assertEquals(estatesList.count(), estatesList.count())
                    },
                    failure = {
                        assertEquals(null, null)
                    }
                )
            },
            onFailure = {
                assertEquals(null, null)
            }
        )

    }

    @Test
    fun testShouldSaveEstateImage() {
        val pictureUri = "pictureUri"
        databaseManager.saveEstateImage(
            estate.id!!,
            pictureUri,
            onSuccess = {
                assertEquals("pictureUri", pictureUri)
            },
            onFailure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldGetImagesForEstate() {
        val pictureUri = "pictureUri"
        databaseManager.saveEstateImage(
            estate.id!!,
            pictureUri,
            onSuccess = {
                databaseManager.getImagesForEstate(
                    estate.id!!,
                    success = { imagesList ->
                        assertEquals(imagesList.count(), imagesList.count())
                    },
                    failure = {
                        assertEquals(null, null)
                    }
                )
            },
            onFailure = {
                assertEquals(null, null)
            }
        )

    }

    @Test
    fun testShouldDeleteImagesForEstate() {
        databaseManager.deleteImagesForEstate(
            estate.id!!,
            onSuccess = {
                databaseManager.getImagesForEstate(
                    estate.id!!,
                    success = { imagesList ->
                        assertEquals(imagesList.count(), imagesList.count())
                    },
                    failure = {
                        assertEquals(null, null)
                    }
                )
            }
        )
    }

    @Test
    fun testShouldSaveAgent() {

    }

    @Test
    fun testShouldGetAllAgents() {

    }

    @Test
    fun testShouldSaveEstateManager() {

    }

    @Test
    fun testShouldGetAllEstateManagers() {

    }

}