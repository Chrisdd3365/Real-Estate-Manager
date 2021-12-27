package com.openclassrooms.realestatemanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import com.openclassrooms.realestatemanager.model.Estate
import org.junit.After

import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DatabaseManagerTest {

    private var estate = Estate()

    private lateinit var databaseManager: DatabaseManager


    @Before
    fun setUp() {
        databaseManager = DatabaseManager(InstrumentationRegistry.getInstrumentation().targetContext)
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
        estate.id = 0
        databaseManager.saveEstate(
            estate,
            onSuccess = { insertedId ->
                assertEquals(0, insertedId)
            },
            onFailure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldUpdateEstate() {

    }

    @Test
    fun testShouldDeleteEstate() {

    }

    @Test
    fun testShouldGetAllEstates() {

    }

    @Test
    fun testShouldGetImagesForEstate() {

    }

    @Test
    fun testShouldSaveEstateImage() {

    }

    @Test
    fun testShouldDeleteImagesForEstate() {

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