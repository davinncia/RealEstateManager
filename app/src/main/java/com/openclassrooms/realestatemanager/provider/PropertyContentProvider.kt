package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.openclassrooms.realestatemanager.db.PropertyRoomDatabase
import com.openclassrooms.realestatemanager.model.Property

//TODO NINO: My content provider
class PropertyContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        val TABLE_NAME: String = Property::class.java.simpleName
        val URI_PROPERTY: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw IllegalStateException("RealEstateManager is read only.")
    }

    //TODO NINO: smart cast for context impossible ?
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {

            context?.let {  val propertyId = ContentUris.parseId(uri)
                val cursor: Cursor? = PropertyRoomDatabase.getDatabase(it).propertyDao().getPropertyWithCursor(propertyId)
                cursor?.setNotificationUri((it).contentResolver, uri)
                return cursor
            }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalStateException("RealEstateManager is read only.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalStateException("RealEstateManager is read only.")
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
    }

}