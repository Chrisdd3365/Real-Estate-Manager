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
import com.openclassrooms.realestatemanager.utils.Utils
import java.io.ByteArrayOutputStream

class DatabaseManager(context : Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ESTATE_TABLE)
        db?.execSQL(SQL_CREATE_IMAGES_TABLE)
        db?.execSQL(SQL_CREATE_AGENTS_TABLE)
        db?.execSQL(SQL_CREATE_MANAGING_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

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

        val insertedId = database.insert(ESTATE_TABLE, null, estate.toContentValues())
        database.close()

        if (insertedId == -1L)
            onFailure?.invoke()
        else
            onSuccess?.invoke(insertedId.toInt())
    }

    fun updateEstate(estate: Estate, onSuccess: (() -> Any)?, onFailure: (() -> Any)?) {
        val database = this.writableDatabase

        val affectedRows = database.update(
            ESTATE_TABLE,
            estate.toContentValues(),
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

            while (cursor.moveToNext()) {
                val newEstate = Estate(cursor)
                if (newEstate.onMarketSince != null)
                    Utils.checkEstatesTimeOnMarket(newEstate.onMarketSince!!)
                estates.add(newEstate)
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

        val contentValues = agent.toContentValues()
        contentValues.put(COLUMN_AVATAR, byteArrayOutputStream.toByteArray())

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

            while (cursor.moveToNext()) {
                result.add(Agent(cursor))
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
            while (cursor.moveToNext()) {
                result.add(Agent(cursor))
            }
            cursor.close()
            success.invoke(result)

        } catch (exception : Exception) {
            Log.e(TAG, "ERROR : $exception")
            failure.invoke()
        }
    }

    fun filterEstates(priceRange: IntArray, surfaceRange: IntArray, roomsRange: IntArray,
                      bathroomsRange: IntArray, bedroomsRange: IntArray, schoolValue: Boolean,
                      playgroundValue: Boolean, shopValue: Boolean, busesValue: Boolean,
                      subwayValue: Boolean, parkValue: Boolean, fromDate: Long, sold: Boolean,
                      onSuccess: ((ArrayList<Estate>) -> Unit), onFailure: (() -> Unit)) {

        val database = this.readableDatabase

        val result = ArrayList<Estate>()

        var selection = "$COLUMN_PRICE BETWEEN ? AND ? " +
                "AND $COLUMN_SURFACE BETWEEN ? AND ? " +
                "AND $COLUMN_ROOMS_COUNT BETWEEN ? AND ? " +
                "AND $COLUMN_BATHROOMS_COUNT BETWEEN ? AND ? " +
                "AND $COLUMN_BEDROOMS_COUNT BETWEEN ? AND ? " +
                "AND $COLUMN_ON_MARKET_SINCE >= ? " +
                "AND $COLUMN_SOLD == ?"

        if (schoolValue) selection += " AND $COLUMN_SCHOOL_NEARBY"
        if (playgroundValue) selection += " AND $COLUMN_PLAYGROUND_NEARBY"
        if (shopValue) selection += " AND $COLUMN_SHOP_NEARBY"
        if (busesValue) selection += " AND $COLUMN_BUSES_NEARBY"
        if (subwayValue) selection += " AND $COLUMN_SUBWAY_NEARBY"
        if (parkValue) selection += " AND $COLUMN_PARK_NEARBY"

        val selectionArgs = arrayOf(
            "${priceRange[0]}", "${priceRange[1]}",
            "${surfaceRange[0]}", "${surfaceRange[1]}",
            "${roomsRange[0]}", "${roomsRange[1]}",
            "${bathroomsRange[0]}", "${bathroomsRange[1]}",
            "${bedroomsRange[0]}", "${bedroomsRange[1]}",
            "$fromDate", if (sold) "1" else "0"
        )

        try {
            val cursor = database.query(
                ESTATE_TABLE,
                null,
                selection,
                selectionArgs,
                null,
                null,
                "$COLUMN_ON_MARKET_SINCE DESC"
            )

            while (cursor.moveToNext()) {
                result.add(Estate(cursor))
            }

            cursor.close()

            onSuccess.invoke(result)

        } catch (exception : Exception) {
            Log.e(TAG, "Error : ${exception.message}")
            onFailure.invoke()
        }

    }

    companion object {

        private const val TAG = "DatabaseManager"

        private const val DATABASE_NAME = "${BuildConfig.APPLICATION_ID}-database"
        private const val DATABASE_VERSION = 1

        private const val ESTATE_TABLE = "estates"
        private const val IMAGE_TABLE = "images"
        private const val AGENTS_TABLE = "agents"
        private const val MANAGING_TABLE = "managing"

        const val COLUMN_ID = "id"

        // Estate table columns
        const val COLUMN_TYPE = "typeIndex"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_ON_MARKET_SINCE = "onMarketSince"
        const val COLUMN_PRICE = "price"
        const val COLUMN_SURFACE = "surface"
        const val COLUMN_ROOMS_COUNT = "rooms_count"
        const val COLUMN_BATHROOMS_COUNT = "bathrooms_count"
        const val COLUMN_BEDROOMS_COUNT = "bedrooms_count"
        const val COLUMN_SCHOOL_NEARBY = "school_nearby"
        const val COLUMN_PLAYGROUND_NEARBY = "playground_nearby"
        const val COLUMN_SHOP_NEARBY = "shop_nearby"
        const val COLUMN_BUSES_NEARBY = "buses_nearby"
        const val COLUMN_SUBWAY_NEARBY = "subway_nearby"
        const val COLUMN_PARK_NEARBY = "park_nearby"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_SOLD = "sold"

        // Image table columns
        private const val COLUMN_IMAGE = "uri"
        private const val COLUMN_ESTATE_ID = "estate_id"

        // Agent table columns
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE_NUMBER = "phone_number"
        const val COLUMN_AVATAR = "avatar"

        // Managing table columns
        private const val COLUMN_AGENT_ID = "agent_id"
//        private const val COLUMN_ESTATE_ID = "estate_id"

        private const val SQL_CREATE_ESTATE_TABLE = """
            CREATE TABLE IF NOT EXISTS $ESTATE_TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_TYPE INTEGER NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_ADDRESS TEXT NOT NULL,
                $COLUMN_ON_MARKET_SINCE LONG NOT NULL,
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
                $COLUMN_LONGITUDE REAL,
                $COLUMN_SOLD BOOLEAN
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


        /**
         *  Extension to handle nullable [Boolean] retrieval from [Cursor].
         *  @param columnIndex ([Int]) - The index of the column to parse in the Database.
         *  TODO : Move to somewhere else
         */
        fun Cursor.getBoolean(columnIndex: Int): Boolean? {
            return if (isNull(columnIndex))
                null
            else
                getInt(columnIndex) != 0
        }
    }
}