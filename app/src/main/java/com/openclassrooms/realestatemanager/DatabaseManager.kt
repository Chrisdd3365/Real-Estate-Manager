package com.openclassrooms.realestatemanager

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.openclassrooms.realestatemanager.model.Agent
import com.openclassrooms.realestatemanager.model.Estate
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class DatabaseManager(context : Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ESTATE_TABLE)
        db?.execSQL(SQL_CREATE_IMAGES_TABLE)
        db?.execSQL(SQL_CREATE_AGENTS_TABLE)
        db?.execSQL(SQL_CREATE_MANAGING_TABLE)
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
    fun saveEstate(estate : Estate, onSuccess: ((newId : Int) -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_TYPE, estate.typeIndex)
            put(COLUMN_DESCRIPTION, estate.description)
            put(COLUMN_ADDRESS, estate.address)
            put(COLUMN_ON_MARKET_SINCE, Date().toString()) // TODO
            put(COLUMN_PRICE, estate.getDollarPrice())
            put(COLUMN_SURFACE, estate.getSquareFeetSurface())
            put(COLUMN_ROOMS_COUNT, estate.roomCount)
            put(COLUMN_BATHROOMS_COUNT, estate.bathroomsCount)
            put(COLUMN_BEDROOMS_COUNT, estate.bedroomsCount)
            put(COLUMN_SCHOOL_NEARBY, estate.school)
            put(COLUMN_PLAYGROUND_NEARBY, estate.playground)
            put(COLUMN_SHOP_NEARBY, estate.shop)
            put(COLUMN_BUSES_NEARBY, estate.buses)
            put(COLUMN_SUBWAY_NEARBY, estate.subway)
            put(COLUMN_PARK_NEARBY, estate.park)
            put(COLUMN_LATITUDE, estate.latitude)
            put(COLUMN_LONGITURE, estate.longitude)
        }

        val insertedId = database.insert(ESTATE_TABLE, null, contentValues)
        database.close()

        if (insertedId == -1L)
            onFailure?.invoke()
        else
            onSuccess?.invoke(insertedId.toInt())
    }

    fun updateEstate(estate: Estate, onSuccess: (() -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_TYPE, estate.typeIndex)
            put(COLUMN_DESCRIPTION, estate.description)
            put(COLUMN_ADDRESS, estate.address)
            put(COLUMN_ON_MARKET_SINCE, Date().toString()) // TODO
            put(COLUMN_PRICE, estate.getDollarPrice())
            put(COLUMN_SURFACE, estate.getSquareFeetSurface())
            put(COLUMN_ROOMS_COUNT, estate.roomCount)
            put(COLUMN_BATHROOMS_COUNT, estate.bathroomsCount)
            put(COLUMN_BEDROOMS_COUNT, estate.bedroomsCount)
            put(COLUMN_SCHOOL_NEARBY, estate.school)
            put(COLUMN_PLAYGROUND_NEARBY, estate.playground)
            put(COLUMN_SHOP_NEARBY, estate.shop)
            put(COLUMN_BUSES_NEARBY, estate.buses)
            put(COLUMN_SUBWAY_NEARBY, estate.subway)
            put(COLUMN_PARK_NEARBY, estate.park)
            put(COLUMN_LATITUDE, estate.latitude)
            put(COLUMN_LONGITURE, estate.longitude)
        }

        val affectedRows = database.update(
            ESTATE_TABLE,
            contentValues,
            "$COLUMN_ID IS ?",
            arrayOf("${estate.id}")
        )
        database.close()

        if (affectedRows == -1)
            onFailure?.invoke()
        else
            onSuccess?.invoke()
    }

    fun deleteEstate(idToDelete : Int, onSuccess: (() -> Unit)?, onFailure: (() -> Unit)?) {
        val database = this.writableDatabase

        val affectedRows = database.delete(
            ESTATE_TABLE,
            "$COLUMN_ID is ?",
            arrayOf("$idToDelete")
        )
        database.close()

        if (affectedRows == 0)
            onFailure?.invoke()
        else
            onSuccess?.invoke()
    }

    fun getEstates(success : (ArrayList<Estate>) -> Unit, failure : () -> Unit) {
        val database = this.readableDatabase
        val estates = ArrayList<Estate>()

        try {
            val cursor = database.query(
                ESTATE_TABLE,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_ON_MARKET_SINCE DESC"
            )

            with(cursor) {
                while (moveToNext()) {
                    estates.add(
                        Estate().apply {
                            id = getInt(getColumnIndex(COLUMN_ID))
                            typeIndex = getInt(getColumnIndex(COLUMN_TYPE))
                            description = getString(getColumnIndex(COLUMN_DESCRIPTION))
                            address = getString(getColumnIndex(COLUMN_ADDRESS))
                            onMarketSince = Date() // TODO
                            setDollarPrice(getDouble(getColumnIndex(COLUMN_PRICE)))
                            setSquareFeetSurface(getDouble(getColumnIndex(COLUMN_SURFACE)))
                            roomCount = getInt(getColumnIndex(COLUMN_ROOMS_COUNT))
                            bathroomsCount = getInt(getColumnIndex(COLUMN_BATHROOMS_COUNT))
                            bedroomsCount = getInt(getColumnIndex(COLUMN_BEDROOMS_COUNT))
                            school = getBoolean(getColumnIndex(COLUMN_SCHOOL_NEARBY))
                            playground = getBoolean(getColumnIndex(COLUMN_PLAYGROUND_NEARBY))
                            shop = getBoolean(getColumnIndex(COLUMN_SHOP_NEARBY))
                            buses = getBoolean(getColumnIndex(COLUMN_BUSES_NEARBY))
                            subway = getBoolean(getColumnIndex(COLUMN_SUBWAY_NEARBY))
                            park = getBoolean(getColumnIndex(COLUMN_PARK_NEARBY))
                            latitude = getDouble(getColumnIndex(COLUMN_LATITUDE))
                            longitude = getDouble(getColumnIndex(COLUMN_LONGITURE))
                        }
                    )
                }
            }
            cursor.close()

            success.invoke(estates)

        } catch (exception : Exception) {
            Log.e(TAG, "Error while querying database : $exception")
            failure.invoke()
        }
    }

    fun saveEstateImage(estateId : Int, image : Bitmap, onFailure: (() -> Any)?,
                        onSuccess: (() -> Any)?) {
        val database = this.writableDatabase

        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val contentValues = ContentValues().apply {
            put(COLUMN_IMAGE, byteArrayOutputStream.toByteArray())
            put(COLUMN_ESTATE_ID, estateId)
        }

        val insertedId = database.insert(IMAGE_TABLE, null, contentValues)
        database.close()

        if (insertedId == -1L)
            onFailure?.invoke()
        else
            onSuccess?.invoke()
    }

    fun getImagesForEstate(estateId: Int, success: (ArrayList<Bitmap>) -> Unit, failure: () -> Unit) {
        Log.d(TAG, "getImageForEstate() id $estateId")
        val database = this.readableDatabase
        val images = ArrayList<Bitmap>()

        try {

            val cursor = database.query(
                IMAGE_TABLE,
                null,
                "$COLUMN_ESTATE_ID IS ?",
                arrayOf("$estateId"),
                null,
                null,
                null,
            )

            with(cursor) {
                while (cursor.moveToNext()) {
                    val byteArray = getBlob(getColumnIndex(COLUMN_IMAGE))
                    images.add(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
                }
            }
            cursor.close()

            Log.d(TAG, "getImageForEstate() ; found ${images.size} images")

            success.invoke(images)

        } catch (exception : Exception) {
            Log.e(TAG, "Error while querying database : $exception")
            failure.invoke()
        }
    }

    fun deleteImagesForEstate(estateId: Int, onSuccess: () -> Unit) {
        val database = this.writableDatabase

        database.delete(
            IMAGE_TABLE,
            "$COLUMN_ESTATE_ID is ?",
            arrayOf("$estateId")
        )
        database.close()

        onSuccess.invoke()
    }

    fun saveAgent(agent : Agent, onSuccess: (() -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val byteArrayOutputStream = ByteArrayOutputStream()
        agent.avatar?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val contentValues = ContentValues().apply {
            put(COLUMN_FIRST_NAME, agent.firstName)
            put(COLUMN_LAST_NAME, agent.lastName)
            put(COLUMN_EMAIL, agent.email)
            put(COLUMN_PHONE_NUMBER, agent.phoneNumber)
            put(COLUMN_AVATAR, byteArrayOutputStream.toByteArray())
        }

        val insertedId = database.insert(AGENTS_TABLE, null, contentValues)
        database.close()

        if (insertedId == -1L)
            onFailure?.invoke()
        else
            onSuccess?.invoke()
    }

    fun getAgents(success: ((ArrayList<Agent>) -> Unit), failure: (() -> Unit)) {
        val database = this.readableDatabase
        val result = ArrayList<Agent>()

        try {
            val cursor = database.query(
                AGENTS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
            )

            with(cursor) {
                while (moveToNext()) {
                    result.add(
                        Agent().apply {
                            id = getInt(getColumnIndex(COLUMN_ID))
                            firstName = getString(getColumnIndex(COLUMN_FIRST_NAME))
                            lastName = getString(getColumnIndex(COLUMN_LAST_NAME))
                            email = getString(getColumnIndex(COLUMN_EMAIL))
                            phoneNumber = getString(getColumnIndex(COLUMN_PHONE_NUMBER))

                            val byteArray = getBlob(getColumnIndex(COLUMN_AVATAR))
                            avatar = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        }
                    )
                }
            }
            cursor.close()

            success.invoke(result)

        } catch (exception : Exception) {
            Log.e(TAG, "Error while querying database : $exception")
            failure.invoke()
        }
    }

    fun deleteEstateManagers(estateId: Int, success: () -> Unit) {
        val database = this.writableDatabase

        database.delete(
            MANAGING_TABLE,
            "$COLUMN_ESTATE_ID is ?",
            arrayOf("$estateId")
        )
        database.close()
        success.invoke()
    }

    fun saveEstateManager(estateId: Int, managingAgent : Agent,
                           success : () -> Unit, failure: () -> Unit) {
        val database = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(COLUMN_ESTATE_ID, estateId)
            put(COLUMN_AGENT_ID, managingAgent.id)
        }

        val insertedId = database.insert(MANAGING_TABLE, null, contentValues)

        database.close()

        if (insertedId == -1L)
            failure.invoke()
        else
            success.invoke()
    }

    fun getEstateManagers(estateId: Int, success: (ArrayList<Agent>) -> Unit, failure: () -> Unit) {
        val database = this.readableDatabase

        try {
            val result = ArrayList<Agent>()
            val cursor = database.rawQuery(SQL_MANAGING_AGENTS_JOIN, arrayOf("$estateId"))
            with(cursor) {
                while (cursor.moveToNext()) {
                    result.add(
                        Agent().apply {
                            id = getInt(getColumnIndex(COLUMN_ID))
                            firstName = getString(getColumnIndex(COLUMN_FIRST_NAME))
                            lastName = getString(getColumnIndex(COLUMN_LAST_NAME))
                            email = getString(getColumnIndex(COLUMN_EMAIL))
                            phoneNumber = getString(getColumnIndex(COLUMN_PHONE_NUMBER))

                            val byteArray = getBlob(getColumnIndex(COLUMN_AVATAR))
                            avatar = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                        }
                    )
                }
            }
            cursor.close()
            success.invoke(result)

        } catch (exception : Exception) {
            Log.e(TAG, "ERROR : $exception")
            failure.invoke()
        }
    }

    /**
     *  Extension to handle nullable [Boolean] retrieval from [Cursor].
     *  @param columnIndex ([Int]) - The index of the column to parse in the Database.
     *  TODO : Move to somewhere else
     */
    private fun Cursor.getBoolean(columnIndex: Int): Boolean? {
        return if (isNull(columnIndex))
            null
        else
            getInt(columnIndex) != 0
    }

    companion object {

        private const val TAG = "DatabaseManager"

        private const val DATABASE_NAME = "${BuildConfig.APPLICATION_ID}-database"
        private const val DATABASE_VERSION = 1

        private const val ESTATE_TABLE = "estates"
        private const val IMAGE_TABLE = "images"
        private const val AGENTS_TABLE = "agents"
        private const val MANAGING_TABLE = "managing"

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
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITURE = "longitude"

        // Image table columns
        private const val COLUMN_IMAGE = "uri"
        private const val COLUMN_ESTATE_ID = "estate_id"

        // Agent table columns
        private const val COLUMN_FIRST_NAME = "first_name"
        private const val COLUMN_LAST_NAME = "last_name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE_NUMBER = "phone_number"
        private const val COLUMN_AVATAR = "avatar"

        // Managing table columns
        private const val COLUMN_AGENT_ID = "agent_id"
//        private const val COLUMN_ESTATE_ID = "estate_id"

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
                $COLUMN_PARK_NEARBY BOOLEAN,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITURE REAL
            );
        """

        private const val SQL_CREATE_IMAGES_TABLE = """
            CREATE TABLE IF NOT EXISTS $IMAGE_TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_IMAGE BLOB NOT NULL,
                $COLUMN_ESTATE_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_ESTATE_ID)
                    REFERENCES $ESTATE_TABLE ($COLUMN_ID)
            );
        """

        // TODO : REPLACE TEXT WITH VARCHAR
        private const val SQL_CREATE_AGENTS_TABLE = """
            CREATE TABLE IF NOT EXISTS $AGENTS_TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_FIRST_NAME TEXT,
                $COLUMN_LAST_NAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PHONE_NUMBER TEXT,
                $COLUMN_AVATAR BLOB
            );
        """

        private const val SQL_CREATE_MANAGING_TABLE = """
            CREATE TABLE IF NOT EXISTS $MANAGING_TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_ESTATE_ID INTEGER NOT NULL,
                $COLUMN_AGENT_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_ESTATE_ID)
                    REFERENCES $ESTATE_TABLE ($COLUMN_ID),
                FOREIGN KEY ($COLUMN_AGENT_ID)
                    REFERENCES $AGENTS_TABLE ($COLUMN_ID)
            );
        """

        private const val SQL_MANAGING_AGENTS_JOIN = """
            SELECT  $AGENTS_TABLE.$COLUMN_ID as $COLUMN_ID,
                    $AGENTS_TABLE.$COLUMN_FIRST_NAME as $COLUMN_FIRST_NAME,
                    $AGENTS_TABLE.$COLUMN_LAST_NAME as $COLUMN_LAST_NAME,
                    $AGENTS_TABLE.$COLUMN_EMAIL as $COLUMN_EMAIL,
                    $AGENTS_TABLE.$COLUMN_PHONE_NUMBER as $COLUMN_PHONE_NUMBER,
                    $AGENTS_TABLE.$COLUMN_AVATAR as $COLUMN_AVATAR,
                    $MANAGING_TABLE.$COLUMN_ESTATE_ID as $COLUMN_ESTATE_ID,
                    $MANAGING_TABLE.$COLUMN_AGENT_ID as $COLUMN_AGENT_ID
                FROM $MANAGING_TABLE
                    INNER JOIN $AGENTS_TABLE
                        ON $MANAGING_TABLE.$COLUMN_AGENT_ID = $AGENTS_TABLE.$COLUMN_ID
            WHERE $MANAGING_TABLE.$COLUMN_ESTATE_ID IS ?
        """
    }
}