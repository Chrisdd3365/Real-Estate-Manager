package com.openclassrooms.realestatemanager.model

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.openclassrooms.realestatemanager.DatabaseManager
import java.io.Serializable

class Agent : Serializable {

    var id : Int? = null
    var firstName : String? = null
    var lastName : String? = null
    var email : String? = null
    var phoneNumber : String? = null
    var avatar : Bitmap? = null

    constructor()

    constructor(cursor: Cursor) {
        with (cursor) {
            id = getInt(getColumnIndex(DatabaseManager.COLUMN_ID))
            firstName = getString(getColumnIndex(DatabaseManager.COLUMN_FIRST_NAME))
            lastName = getString(getColumnIndex(DatabaseManager.COLUMN_LAST_NAME))
            email = getString(getColumnIndex(DatabaseManager.COLUMN_EMAIL))
            phoneNumber = getString(getColumnIndex(DatabaseManager.COLUMN_PHONE_NUMBER))

            val byteArray = getBlob(getColumnIndex(DatabaseManager.COLUMN_AVATAR))
            avatar = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }

    fun toContentValues() : ContentValues {
        return ContentValues().apply {
            put(DatabaseManager.COLUMN_FIRST_NAME, firstName)
            put(DatabaseManager.COLUMN_LAST_NAME, lastName)
            put(DatabaseManager.COLUMN_EMAIL, email)
            put(DatabaseManager.COLUMN_PHONE_NUMBER, phoneNumber)
        }
    }

    override fun toString(): String {
        return "Agent ($id) $firstName $lastName, mail : $email, phone : $phoneNumber"
    }
}