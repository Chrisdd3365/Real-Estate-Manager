package com.openclassrooms.realestatemanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.model.Agent

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import com.openclassrooms.realestatemanager.model.Estate
import org.junit.After

import org.junit.Before
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseManagerTest {

    private var estate = Estate()
    private var agent = Agent()

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

        agent = Agent().apply {
            firstName = "firstName"
            lastName = "lastName"
            email = "firstNameLastName@gmail.com"
            phoneNumber = "0610101010"
        }
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
                assertEquals(insertedId, insertedId)
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
            estate.id!!,
            onSuccess = {
                databaseManager.getEstates(
                    success = {
                        assertEquals(it.count(), it.count())
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
    fun testShouldGetAllEstates() {
        databaseManager.saveEstate(
            estate,
            onSuccess = {
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
        databaseManager.saveAgent(
            agent,
            onSuccess = {
                databaseManager.getAgents(
                    success = { agentsList ->
                        assertEquals(agentsList.count(), agentsList.count())
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
    fun testShouldGetAllAgents() {
        databaseManager.getAgents(
            success = { agentsList ->
                assertNotEquals(0, agentsList.count())
            },
            failure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldSaveEstateManager() {
        databaseManager.saveEstateManager(
            estate.id!!,
            agent,
            success = {
                databaseManager.getEstateManagers(
                    estate.id!!,
                    success = { agentsList ->
                        assertNotEquals(null, agentsList)
                    }
                )
            },
            failure = {
                assertEquals(null, null)
            }
        )
    }

    @Test
    fun testShouldGetAllEstateManagers() {
        databaseManager.getEstateManagers(
            estate.id!!,
            success = { agentsList ->
                assertNotEquals(null, agentsList)
            }
        )
    }

}