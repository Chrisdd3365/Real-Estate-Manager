package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.DatabaseManager
import com.openclassrooms.realestatemanager.model.Estate
import java.lang.IllegalArgumentException


class EstateContentProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        val URI_ESTATE: Uri = Uri.parse("content://$AUTHORITY/${DatabaseManager.ESTATE_TABLE}")
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        if (context != null) {
            return DatabaseManager(context!!).getCursor()
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String? {
        throw IllegalArgumentException("Unknown URI: $uri")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (context != null && values != null) {
            val estate = Estate.fromContentValues(values)
            DatabaseManager(context!!).saveEstate(
                estate,
                onSuccess = { insertedId ->
                    "$uri/${insertedId}".toUri()
                },
                onFailure = {
                    throw Exception("Save estate failed")
                }
            )
            context?.contentResolver?.notifyChange(uri, null)
        }
        throw Exception("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalArgumentException("Impossible to delete $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw IllegalArgumentException("Impossible to update $uri")
    }

}