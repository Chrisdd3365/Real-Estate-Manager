package com.openclassrooms.realestatemanager

import android.content.ContentValues
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.provider.EstateContentProvider
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EstateContentProviderTest {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var estateContentProvider: EstateContentProvider

    @Before
    fun setUp() {
        databaseManager = DatabaseManager(InstrumentationRegistry.getInstrumentation().targetContext)
        estateContentProvider = EstateContentProvider()
    }

    @After
    fun finish() {
        databaseManager.close()
    }

    @Test
    fun testShouldGetQuery() {
        val cursor = databaseManager.getCursor()
        assertThat(cursor, notNullValue())
        cursor.close()
    }

    @Test
    fun testShouldInsert() {
        estateContentProvider.insert(EstateContentProvider.URI_ESTATE, generateEstate())
        val cursor = databaseManager.getCursor()
        assertThat(cursor, notNullValue())
        assertThat(cursor.count, `is`(1))
        assertThat(cursor.moveToFirst(), `is`(true))
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), `is`("description"))
    }

    private fun generateEstate(): ContentValues {
        val values = ContentValues()

        values.put("id", 77)
        values.put("typeIndex", 1)
        values.put("description", "description")
        values.put("address", "rue de la paix")
        values.put("price", 7777777.0)
        values.put("surface", 77.0)
        values.put("roomCount", 3)
        values.put("bathroomsCount", 2)
        values.put("bedroomsCount", 3)
        values.put("school", true)
        values.put("playground", true)
        values.put("shop", true)
        values.put("buses", true)
        values.put("subway", true)
        values.put("park", true)
        values.put("latitude", 42.0)
        values.put("longitude", 3.0)
        values.put("sold", true)

        return values
    }

}