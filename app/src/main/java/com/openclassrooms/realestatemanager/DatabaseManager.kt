package com.openclassrooms.realestatemanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.openclassrooms.realestatemanager.model.Estate
import java.util.*

class DatabaseManager(context : Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ESTATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    /**
     *  Saves an [Estate] instance in the Database.
     *  Note : We could optimize the 6 boolean columns by replacing them with a single one, and make
     *          some binary masks to get the data.
     *  When the saving is complete, we call [onSuccess] callback.
     *  @param estate ([Estate]) The [Estate] we want to save.
     *  @param onSuccess ([Unit]) Function to call when the save is completed, with the id inserted.
     *  @param onFailure ([Unit]) Function to call when an error occurs.
     */
    fun saveEstate(estate : Estate, onSuccess: ((newId : Long) -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_TYPE, estate.typeIndex)
            put(COLUMN_DESCRIPTION, estate.description)
            put(COLUMN_ADDRESS, estate.address)
            put(COLUMN_ON_MARKET_SINCE, Date().toString()) // TODO
            put(COLUMN_PRICE, estate.price)
            put(COLUMN_SURFACE, estate.surface)
            put(COLUMN_ROOMS_COUNT, estate.roomCount)
            put(COLUMN_BATHROOMS_COUNT, estate.bathroomsCount)
            put(COLUMN_BEDROOMS_COUNT, estate.bedroomsCount)
            put(COLUMN_SCHOOL_NEARBY, estate.school)
            put(COLUMN_PLAYGROUND_NEARBY, estate.playground)
            put(COLUMN_SHOP_NEARBY, estate.shop)
            put(COLUMN_BUSES_NEARBY, estate.buses)
            put(COLUMN_SUBWAY_NEARBY, estate.subway)
            put(COLUMN_PARK_NEARBY, estate.park)
        }

        val insertedId = database.insert(ESTATE_TABLE, null, contentValues)
        database.close()

        if (insertedId == -1L)
            onFailure?.invoke()
        else
            onSuccess?.invoke(insertedId)
    }

    fun updateEstate(estate: Estate, onSuccess: (() -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_TYPE, estate.typeIndex)
            put(COLUMN_DESCRIPTION, estate.description)
            put(COLUMN_ADDRESS, estate.address)
            put(COLUMN_ON_MARKET_SINCE, Date().toString()) // TODO
            put(COLUMN_PRICE, estate.price)
            put(COLUMN_SURFACE, estate.surface)
            put(COLUMN_ROOMS_COUNT, estate.roomCount)
            put(COLUMN_BATHROOMS_COUNT, estate.bathroomsCount)
            put(COLUMN_BEDROOMS_COUNT, estate.bedroomsCount)
            put(COLUMN_SCHOOL_NEARBY, estate.school)
            put(COLUMN_PLAYGROUND_NEARBY, estate.playground)
            put(COLUMN_SHOP_NEARBY, estate.shop)
            put(COLUMN_BUSES_NEARBY, estate.buses)
            put(COLUMN_SUBWAY_NEARBY, estate.subway)
            put(COLUMN_PARK_NEARBY, estate.park)
        }

        val affectedRows = database.update(
            ESTATE_TABLE,
            contentValues,
            COLUMN_ID,
            arrayOf("${estate.id}")
        )
        database.close()

        if (affectedRows == -1)
            onFailure?.invoke()
        else
            onSuccess?.invoke()
    }

    fun deleteEstate(idToDelete : Long, onSuccess: (() -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val affectedRows = database.delete(ESTATE_TABLE, COLUMN_ID, arrayOf("$idToDelete"))
        database.close()

        if (affectedRows == 0)
            onFailure?.invoke()
        else
            onSuccess?.invoke()
    }

    companion object {
        private const val DATABASE_NAME = "${BuildConfig.APPLICATION_ID}-database"
        private const val DATABASE_VERSION = 1

        private const val ESTATE_TABLE = "estates"

        private const val COLUMN_ID = "id"

        // Estate table columns
        private const val COLUMN_TYPE = "typeIndex"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_ADDRESS = "address"
        private const val COLUMN_ON_MARKET_SINCE = "onMarketSince"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_SURFACE = "surface"
        private const val COLUMN_ROOMS_COUNT = "rooms_count"
        private const val COLUMN_BATHROOMS_COUNT = "bathrooms_count"
        private const val COLUMN_BEDROOMS_COUNT = "bedrooms_count"
        private const val COLUMN_SCHOOL_NEARBY = "school_nearby"
        private const val COLUMN_PLAYGROUND_NEARBY = "playground_nearby"
        private const val COLUMN_SHOP_NEARBY = "shop_nearby"
        private const val COLUMN_BUSES_NEARBY = "buses_nearby"
        private const val COLUMN_SUBWAY_NEARBY = "subway_nearby"
        private const val COLUMN_PARK_NEARBY = "park_nearby"

        private const val SQL_CREATE_ESTATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $ESTATE_TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_TYPE INTEGER NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_ADDRESS TEXT NOT NULL,
                $COLUMN_ON_MARKET_SINCE DATE NOT NULL,
                $COLUMN_PRICE REAL NOT NULL,
                $COLUMN_SURFACE REAL NOT NULL,
                $COLUMN_ROOMS_COUNT INT,
                $COLUMN_BATHROOMS_COUNT INT,
                $COLUMN_BEDROOMS_COUNT INT,
                $COLUMN_SCHOOL_NEARBY BOOLEAN,
                $COLUMN_PLAYGROUND_NEARBY BOOLEAN,
                $COLUMN_SHOP_NEARBY BOOLEAN,
                $COLUMN_BUSES_NEARBY BOOLEAN,
                $COLUMN_SUBWAY_NEARBY BOOLEAN,
                $COLUMN_PARK_NEARBY BOOLEAN
            );
        """
    }
}